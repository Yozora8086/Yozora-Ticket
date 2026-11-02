package com.lcy.yozoraticket.service.impl;

import cn.hutool.core.util.IdUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lcy.yozoraticket.constant.RedisConstant;
import com.lcy.yozoraticket.context.BaseContext;
import com.lcy.yozoraticket.entity.Order;
import com.lcy.yozoraticket.entity.OrderItem;
import com.lcy.yozoraticket.entity.Ticket;
import com.lcy.yozoraticket.enums.ErrorCode;
import com.lcy.yozoraticket.exception.BusinessException;
import com.lcy.yozoraticket.mapper.OrderItemMapper;
import com.lcy.yozoraticket.mapper.OrderMapper;
import com.lcy.yozoraticket.mapper.TicketsMapper;
import com.lcy.yozoraticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.ErrorResponse;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final OrderItemMapper orderItemMapper;
    private final TicketsMapper ticketsMapper;
    private final OrderMapper orderMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 生成演出电子票
     * @param orderNo
     */
    @Override
    public void getTicket(String orderNo) {

        //获取当前订单缓存信息
        String orderJson = stringRedisTemplate.opsForValue().get(
                RedisConstant.TICKET_ORDER_KEY + orderNo
        );

        //将订单信息反序列化为订单对象
        Order order;
        try {
            order = objectMapper.readValue(orderJson,Order.class);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.JSON_DESERIALIZATION_ERROR);
        }


        //获取订单明细缓存信息
        String orderItemJson = stringRedisTemplate.opsForValue().get(
                RedisConstant.TICKET_ORDER_ITEM_KEY + order.getOrderId().toString()
        );

        //订单明细反序列化为订单对象
        OrderItem orderItem;
        try {
            orderItem = objectMapper.readValue(orderItemJson, OrderItem.class);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.JSON_DESERIALIZATION_ERROR);
        }

        //创建电子票对象
        Ticket ticket = new Ticket();
        ticket.setTicketId(IdUtil.getSnowflakeNextId());
        ticket.setOrderItemId(orderItem.getOrderItemId());
        ticket.setTicketCode("T" + System.currentTimeMillis() + String.format("%08d", ThreadLocalRandom.current().nextInt(100_000_000)));
        ticket.setStatus((byte) 0);
        ticket.setCreatedTime(LocalDateTime.now());

        //将电子票对象序列化为JSON
        String ticketJson;
        try {
            ticketJson = objectMapper.writeValueAsString(ticket);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.JSON_SERIALIZATION_ERROR);
        }

        //将电子票缓存到redis中
        stringRedisTemplate.opsForValue().set(
                RedisConstant.TICKET_ELECTRONIC_KEY + ticket.getTicketId().toString(),
                ticketJson,
                7,
                TimeUnit.DAYS
        );
    }

    /**
     * 核销演出电子票
     * @param ticketCode
     */
    @Override
    public void useTicket(String ticketCode) {
       Long getUserId = orderMapper.selectOrderByTicketCode(ticketCode);
        System.err.println("1--" + getUserId);

       //判断当前电子票是否属于该用户
        Long userId = BaseContext.getCurrentId();
        System.err.println("2--" + userId);
        if (!userId.equals(getUserId)){
            throw new BusinessException(ErrorCode.TICKET_NOT_YOU);
        }
        //修改电子票核销状态 修改为已核销
        ticketsMapper.updateStatus(ticketCode);

    }
}
