package com.lcy.yozoraticket.service.impl;

import cn.hutool.core.util.IdUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lcy.yozoraticket.constant.RedisConstant;
import com.lcy.yozoraticket.context.BaseContext;
import com.lcy.yozoraticket.dto.EventOrderDTO;
import com.lcy.yozoraticket.dto.EventSessionNoListDTO;
import com.lcy.yozoraticket.dto.RedisDTO.OrderRedisDTO;
import com.lcy.yozoraticket.dto.TicketBuyDTO;
import com.lcy.yozoraticket.dto.query.EventSessionSellTimeQueryDTO;
import com.lcy.yozoraticket.entity.Order;
import com.lcy.yozoraticket.enums.ErrorCode;
import com.lcy.yozoraticket.exception.BusinessException;
import com.lcy.yozoraticket.mapper.*;
import com.lcy.yozoraticket.service.OrderTimeOutService;
import com.lcy.yozoraticket.service.TicketBuyService;
import com.lcy.yozoraticket.vo.EventSessionsVO;
import com.lcy.yozoraticket.vo.EventVO;
import com.lcy.yozoraticket.vo.TicketTiersVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TicketBuyServiceBuyImpl implements TicketBuyService {

    private final TicketTiersMapper ticketTiersMapper;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final EventSessionsMapper eventSessionsMapper;
    private final PaymentsMapper paymentsMapper;
    private final OrderTimeOutService orderTimeOutService;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 用户购票
     * @param ticketBuyDTO
     * @return
     */
    @Transactional
    @Override
    public EventOrderDTO buyTicket(TicketBuyDTO ticketBuyDTO) {
        //用户购票等级
        Byte ticketLevel = ticketBuyDTO.getTicketLevel();
        //用户购买的哪一场的票
        Long eventSessionId = ticketBuyDTO.getEventSessionId();
        //查询当前演出开售时间,并返回演出状态对象
        EventSessionSellTimeQueryDTO sessionSellTimeQueryDTO = eventSessionsMapper.selectStartSellTime(eventSessionId,ticketLevel);
        //获取当前时间
        LocalDateTime now = LocalDateTime.now();

        //判断当前演出是否可以购买
        if (now.isBefore(sessionSellTimeQueryDTO.getSellTime())) {
            throw new BusinessException(ErrorCode.TICKET_TIME_NOT_SELL);
        }

        //判断当前演出的状态是否允许购买
        if (sessionSellTimeQueryDTO.getStatus() == (byte) 1){
            throw new BusinessException(ErrorCode.TICKET_STATUS_NOT_SELL);
        }

        //判断当前票仓是否有票
        if (sessionSellTimeQueryDTO.getTicketPermit() < ticketBuyDTO.getCount()){
            throw new BusinessException(ErrorCode.NOT_TICKET);
        }

        //查询用户购票的等级所对应的价格
        Object object = stringRedisTemplate.opsForHash().get(
                RedisConstant.WARMUP_TICKET_PERMIT_KEY + eventSessionId,
                "ticketPrice" + ":" + ticketLevel.toString()
        );

        //单张票价
        Integer ticketPrice = Integer.valueOf((String) object);

        //订单总价
        Integer total = ticketPrice * ticketBuyDTO.getCount();


        //获取当前userId
        Long userId = BaseContext.getCurrentId();
        OrderRedisDTO orderRedisDTO = OrderRedisDTO.builder()
                .eventSessionId(eventSessionId)
                .ticketLevel(ticketLevel)
                .total(total)
                .payCloseTime(LocalDateTime.now().plusMinutes(15))
                .build();

        //获取演出详情JSON
        String eventDetail = stringRedisTemplate.opsForValue().get(
                RedisConstant.WARMUP_EVENT_KEY + ticketBuyDTO.getEventId().toString()
        );

        //将演出信息JSON反序列化的对象
        EventVO event;
        try {
            event = objectMapper.readValue(eventDetail, EventVO.class);
        } catch (JsonProcessingException e){
            throw new BusinessException(ErrorCode.JSON_DESERIALIZATION_ERROR);
        }

        //创建订单
        Order order = Order.builder()
                .orderId(IdUtil.getSnowflakeNextId())
                .orderNo(System.currentTimeMillis() + String.format("%08d", ThreadLocalRandom.current().nextInt(100_000_000)))
                .eventSessionId(eventSessionId)
                .userId(userId)
                .total(total)
                .ticketTotal(ticketBuyDTO.getCount())
                .status((byte) 0)
                .ticketLevel(ticketLevel)
                .payCloseTime(LocalDateTime.now().plusMinutes(15))
                .build();

        String orderJson;

        //序列化订单对象
        try{
            orderJson = objectMapper.writeValueAsString(order);
        } catch (JsonProcessingException e)
        {
            throw new BusinessException(ErrorCode.JSON_SERIALIZATION_ERROR);
        }

        //将当前订单信息缓存到redis
        stringRedisTemplate.opsForValue().set(
                RedisConstant.TICKET_ORDER_KEY + order.getOrderNo(),
                orderJson,
                7,
                TimeUnit.DAYS
        );

        //将订单所依赖的信息缓存到redis
        stringRedisTemplate.opsForValue().set(
                RedisConstant.TICKET_BUY_KEY + userId.toString(),
                orderJson,
                15,
                TimeUnit.MINUTES
        );

        //redis预扣库存票数
        stringRedisTemplate.opsForHash().increment(
                RedisConstant.WARMUP_TICKET_PERMIT_KEY + eventSessionId.toString(),
                "ticketPermit" + eventSessionId.toString(),
                - ticketBuyDTO.getCount()
        );

        //冻结已下单的票数
        stringRedisTemplate.opsForValue().increment(
                RedisConstant.TICKET_LOCK_KEY + eventSessionId.toString() + ticketLevel.toString(),
                ticketBuyDTO.getCount()
        );


        EventSessionsVO eventSessionsVO = new EventSessionsVO();
        TicketTiersVO ticketTiersVO = new TicketTiersVO();
        //遍历演出信息下的演出场次，拿到购票的演出场次对象和票等级对象
        for (EventSessionsVO esv : event.getEventSessionsVOList()) {
            if (esv.getEventSessionId() == eventSessionId){
                eventSessionsVO = esv;
            }
            for (TicketTiersVO ttv : esv.getTicketTiersVOList()) {
                if (ttv.getTicketLevel() == ticketLevel){
                    ticketTiersVO = ttv;
                }
            }
        }

        //对象转换
        EventSessionNoListDTO eventSessionNoListDTO = EventSessionNoListDTO.builder()
                .eventId(event.getEventId())
                .venueId(eventSessionsVO.getVenueId())
                .startTime(eventSessionsVO.getStartTime())
                .status(eventSessionsVO.getStatus())
                .venueVO(eventSessionsVO.getVenueVO())
                .ticketTiersVO(ticketTiersVO)
                .build();

        //对象转换
        EventOrderDTO eventOrderDTO = EventOrderDTO.builder()
                .orderId(order.getOrderId())
                .orderNo(order.getOrderNo())
                .status(order.getStatus())
                .payCloseTime(order.getPayCloseTime())
                .eventName(event.getEventName())
                .eventPoster(event.getEventPoster())
                .eventSessionNoListDTO(eventSessionNoListDTO)
                .build();

        return eventOrderDTO;

    }

    /**
     * 用户取消订单
     * @param orderNo
     */
    @Transactional
    @Override
    public void backTicket(String orderNo) {
        //初始化状态值为超时
        Byte orderStatus = 2;
        //初始化状态值为已取消
        Byte paymentStatus = 2;

        //获取当前用户id
        Long userId = BaseContext.getCurrentId();

        //根据订单号查询订单
        Order order = orderMapper.selectOrderByOrderNo(orderNo,userId);
        //取消订单
        Integer rows = orderMapper.updateOrderStatus(order.getOrderId(), orderStatus);
        if (rows == 1){
            //获取当前订单买票数量
            Byte ticketTotal = order.getTicketTotal();
            //释放库存
            ticketTiersMapper.updateUpdateLock(
                    order.getEventSessionId(),
                    order.getTicketLevel(),
                    ticketTotal
            );
            //数据库修改支付记录状态 设置状态已取消
            paymentsMapper.updateStatus(order.getOrderId(),paymentStatus);
        } else {
            //抛出 当前订单已支付或者已取消 异常
           throw new BusinessException(ErrorCode.ORDER_STATUS_IS_PAY_OR_TIMEOUT);
        }
    }

    /**
     * 用户提交购票订单
     * @param orderNo
     */
    @Override
    @Transactional
    public void buyCommitTicket(String orderNo) {
        //初始化状态值为已支付
        Byte orderStatus = 1;
        //初始化状态值为支付成功
        Byte paymentStatus = 1;

        //获取当前用户id
        Long userId = BaseContext.getCurrentId();

        //根据订单号查询订单
        Order order = orderMapper.selectOrderByOrderNo(orderNo,userId);
        //支付订单 (返回影响的行数)
        Integer rows = orderMapper.updateOrderStatus(order.getOrderId(),orderStatus);
        //如果影响行数为1行，则代表状态未修改
        if (rows == 1){
            //获取当前订单买票数量
            Byte ticketTotal = order.getTicketTotal();
            //释放库存
            ticketTiersMapper.updateUpdateCommitLock(
                    order.getEventSessionId(),
                    order.getTicketLevel(),
                    ticketTotal
            );

            //数据库修改支付记录状态 设置状态已支付成功
            paymentsMapper.updateStatusCommit(order.getOrderId(),paymentStatus,(byte) 0);
        } else {
            //状态已被修改，抛出异常
            throw new BusinessException(ErrorCode.ORDER_STATUS_IS_PAY_OR_TIMEOUT);
        }

    }
}
