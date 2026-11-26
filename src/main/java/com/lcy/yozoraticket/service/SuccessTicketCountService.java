package com.lcy.yozoraticket.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lcy.yozoraticket.config.RabbitMqConfig;
import com.lcy.yozoraticket.constant.RedisConstant;
import com.lcy.yozoraticket.dto.message.TicketSuccessMessage;
import com.lcy.yozoraticket.entity.Order;
import com.lcy.yozoraticket.entity.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SuccessTicketCountService {

    private final StringRedisTemplate stringRedisTemplate;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 订单支付成功消息组装
     * @param orderNo
     * @throws JsonProcessingException
     */
    public void TicketCountPersistence(String orderNo) throws JsonProcessingException {
        //通过订单号获取redis里的订单json,并且将其反序列化
        String orderJson = stringRedisTemplate.opsForValue().get(
                RedisConstant.TICKET_ORDER_KEY + orderNo
        );
        Order order = objectMapper.readValue(orderJson, Order.class);

        //通过订单id获取redis里的订单明细json,并且将其反序列化
        String orderItemJson = stringRedisTemplate.opsForValue().get(
                RedisConstant.TICKET_ORDER_ITEM_KEY + order.getOrderId().toString()
        );
        OrderItem orderItem = objectMapper.readValue(orderItemJson, OrderItem.class);

        //构建消息实体并序列化
        TicketSuccessMessage ts = TicketSuccessMessage.builder()
                .ticketLevel(order.getTicketLevel())
                .eventSessionId(order.getEventSessionId())
                .orderId(orderItem.getOrderId())
                .count(orderItem.getCount())
                .status((byte) 1)
                .build();

        String message = objectMapper.writeValueAsString(ts);

        //消息推送
        rabbitTemplate.convertAndSend(
                "",
                RabbitMqConfig.YOZORA_TICKET_SUCCESS_QUEUE,
                message
        );

    }
}
