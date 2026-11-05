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
import com.lcy.yozoraticket.entity.Payments;
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
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
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
    private final DefaultRedisScript<String> ticketBuyScript;

    /**
     * 用户购票
     * @param ticketBuyDTO
     * @return
     */
    @Transactional
    @Override
    public EventOrderDTO buyTicket(TicketBuyDTO ticketBuyDTO) {
        //将购票所需参数进行序列化
        String ticketBuyDTOJson;
        try {
            ticketBuyDTOJson = objectMapper.writeValueAsString(ticketBuyDTO);
        } catch (JsonProcessingException e){
            throw new BusinessException(ErrorCode.JSON_SERIALIZATION_ERROR);
        }

        //获取当前userId
        Long userId = BaseContext.getCurrentId();

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

        //创建订单
        Order order = Order.builder()
                .orderId(IdUtil.getSnowflakeNextId())
                .orderNo(System.currentTimeMillis() + String.format("%08d", ThreadLocalRandom.current().nextInt(100_000_000)))
                .eventSessionId(eventSessionId)
                .userId(userId)
                .ticketTotal(ticketBuyDTO.getCount())
                .status((byte) 0)
                .ticketLevel(ticketLevel)
                .payCloseTime(LocalDateTime.now().plusMinutes(15))
                .build();

        //将订单空对象序列化为JSON
        String orderJson;
        try{
            orderJson = objectMapper.writeValueAsString(order);
        } catch (JsonProcessingException e){
            throw new BusinessException(ErrorCode.JSON_SERIALIZATION_ERROR);
        }

        //执行购票lua脚本,并接收演出信息参数
        String eventJson = stringRedisTemplate.execute(
                ticketBuyScript,
                Collections.emptyList(),
                ticketBuyDTO.getEventId().toString(),
                orderJson,
                ticketBuyDTOJson
        );

        //将演出信息Json反序列化为对象
        EventVO event;
        try{
            event = objectMapper.readValue(eventJson, EventVO.class);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.JSON_DESERIALIZATION_ERROR);
        }

        //redis预扣库存票数
        stringRedisTemplate.opsForHash().increment(
                RedisConstant.WARMUP_TICKET_PERMIT_KEY + eventSessionId.toString(),
                "ticketPermit" + ticketBuyDTO.getTicketLevel().toString(),
                - ticketBuyDTO.getCount()
        );

        //冻结已下单的票数
        stringRedisTemplate.opsForValue().increment(
                RedisConstant.TICKET_LOCK_KEY + eventSessionId.toString() + ":" + ticketLevel.toString(),
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

        //获取redis中缓存的订单信息
        String orderJson = stringRedisTemplate.opsForValue().get(
                RedisConstant.TICKET_ORDER_KEY + orderNo
        );

        Order order;
        //将订单信息反序列化成order对象
        try {
            order = objectMapper.readValue(orderJson, Order.class);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.JSON_DESERIALIZATION_ERROR);
        }

        //判断当前订单是否属于当前用户
        if (userId != order.getUserId()) {
            throw new BusinessException(ErrorCode.ORDER_NOT_YOU);
        }


        //修改当前订单状态
        order.setStatus(orderStatus);

        //将当前order对象序列化为json
        try {
            orderJson = objectMapper.writeValueAsString(order);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.JSON_SERIALIZATION_ERROR);
        }

        //redis缓存覆盖新的的订单状态
        stringRedisTemplate.opsForValue().set(
                RedisConstant.TICKET_ORDER_KEY + orderNo,
                orderJson,
                7,
                TimeUnit.DAYS
        );

        //获取redis缓存的支付记录
        String paymentJson = stringRedisTemplate.opsForValue().get(
                RedisConstant.TICKET_PAYMENT_KEY + order.getOrderId().toString()
        );

        //将json反序列化为Payments对象
        Payments payments;
        try {
            payments = objectMapper.readValue(paymentJson, Payments.class);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.JSON_DESERIALIZATION_ERROR);
        }

        //设置支付记录状态
        payments.setStatus(paymentStatus);

        //将支付记录对象序列化为json
        try {
            paymentJson = objectMapper.writeValueAsString(payments);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.JSON_SERIALIZATION_ERROR);
        }

        //redis缓存覆盖新的的支付记录状态
        stringRedisTemplate.opsForValue().set(
                RedisConstant.TICKET_PAYMENT_KEY + order.getOrderId().toString(),
                paymentJson,
                7,
                TimeUnit.DAYS
        );

        //redis释放预扣库存
        stringRedisTemplate.opsForValue().decrement(
                RedisConstant.TICKET_LOCK_KEY + order.getEventSessionId().toString() + order.getTicketLevel(),
                order.getTicketTotal()
        );

        //取消的订单的票数回到库存
        stringRedisTemplate.opsForHash().increment(
                RedisConstant.WARMUP_TICKET_PERMIT_KEY + order.getEventSessionId().toString(),
                "ticketPermit" + order.getTicketLevel().toString(),
                order.getTicketTotal()
        );
    }

    /**
     * 用户提交购票订单
     * @param orderNo
     */
    @Override
    @Transactional
    public boolean buyCommitTicket(String orderNo) {
        //初始化状态值为已支付
        Byte orderStatus = 1;
        //初始化状态值为支付成功
        Byte paymentStatus = 1;

        //获取当前用户id
        Long userId = BaseContext.getCurrentId();


        //获取redis中缓存的订单信息
        String orderJson = stringRedisTemplate.opsForValue().get(
                RedisConstant.TICKET_ORDER_KEY + orderNo
        );

        Order order;
        //将订单信息反序列化成order对象
        try {
            order = objectMapper.readValue(orderJson, Order.class);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.JSON_DESERIALIZATION_ERROR);
        }

        //判断当前订单是否属于当前用户
        if (userId != order.getUserId()) {
            throw new BusinessException(ErrorCode.ORDER_NOT_YOU);
        }


        //修改当前订单状态
        order.setStatus(orderStatus);

        //将当前order对象序列化为json
        try {
            orderJson = objectMapper.writeValueAsString(order);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.JSON_SERIALIZATION_ERROR);
        }

        //redis缓存覆盖新的的订单状态
        stringRedisTemplate.opsForValue().set(
                RedisConstant.TICKET_ORDER_KEY + orderNo,
                orderJson,
                7,
                TimeUnit.DAYS
        );

        //获取redis缓存的支付记录
        String paymentJson = stringRedisTemplate.opsForValue().get(
                RedisConstant.TICKET_PAYMENT_KEY + order.getOrderId().toString()
        );

        //将json反序列化为Payments对象
        Payments payments;
        try {
            payments = objectMapper.readValue(paymentJson, Payments.class);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.JSON_DESERIALIZATION_ERROR);
        }

        //设置支付记录状态和支付时间
        payments.setStatus(paymentStatus);
        payments.setPaySuccessTime(LocalDateTime.now());

        //将支付记录对象序列化为json
        try {
            paymentJson = objectMapper.writeValueAsString(payments);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.JSON_SERIALIZATION_ERROR);
        }

        //redis缓存覆盖新的的支付记录状态
        stringRedisTemplate.opsForValue().set(
                RedisConstant.TICKET_PAYMENT_KEY + order.getOrderId().toString(),
                paymentJson,
                7,
                TimeUnit.DAYS
        );

        //redis释放预扣库存
        stringRedisTemplate.opsForValue().decrement(
                RedisConstant.TICKET_LOCK_KEY + order.getEventSessionId().toString() + ":" + order.getTicketLevel(),
                order.getTicketTotal()
        );

        return true;

    }
}
