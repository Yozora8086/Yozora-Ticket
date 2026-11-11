package com.lcy.yozoraticket.service.impl;

import cn.hutool.core.util.IdUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lcy.yozoraticket.constant.RedisConstant;
import com.lcy.yozoraticket.context.BaseContext;
import com.lcy.yozoraticket.dto.EventOrderDTO;
import com.lcy.yozoraticket.dto.EventSessionNoListDTO;
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
import java.time.format.DateTimeFormatter;
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
    private final DefaultRedisScript<Long> commitOrBackTicketScript;

    //时间戳格式
    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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

        //执行取消订单lua脚本，并返回当前订单是否属于当前用户
        Long result = stringRedisTemplate.execute(
                commitOrBackTicketScript,
                List.of(
                        RedisConstant.TICKET_ORDER_KEY,
                        RedisConstant.TICKET_PAYMENT_KEY,
                        RedisConstant.TICKET_LOCK_KEY,
                        RedisConstant.WARMUP_TICKET_PERMIT_KEY
                ),
                orderNo,
                userId.toString(),
                orderStatus.toString(),
                paymentStatus.toString()
        );

        //不属于则抛出异常
        if (result == 0) {
            throw new BusinessException(ErrorCode.ORDER_NOT_YOU);
        }
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

        //获取当前时间，并按照规定格式返回
        String time = LocalDateTime.now().format(FORMATTER);

        //获取当前用户id
        Long userId = BaseContext.getCurrentId();

        //执行提交成功订单lua脚本，并返回当前订单是否属于当前用户
        Long result = stringRedisTemplate.execute(
                commitOrBackTicketScript,
                List.of(
                        RedisConstant.TICKET_ORDER_KEY,
                        RedisConstant.TICKET_PAYMENT_KEY,
                        RedisConstant.TICKET_LOCK_KEY,
                        RedisConstant.WARMUP_TICKET_PERMIT_KEY
                ),
                orderNo,
                userId.toString(),
                orderStatus.toString(),
                paymentStatus.toString(),
                time
        );

        //不属于则抛出异常
        if (result == 0) {
            throw new BusinessException(ErrorCode.ORDER_NOT_YOU);
        }

        return true;

    }
}
