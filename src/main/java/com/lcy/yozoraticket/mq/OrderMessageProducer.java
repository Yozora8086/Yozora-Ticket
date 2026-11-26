package com.lcy.yozoraticket.mq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lcy.yozoraticket.config.RabbitMqConfig;
import com.lcy.yozoraticket.constant.RedisConstant;
import com.lcy.yozoraticket.dto.message.OrderCreatedMessage;
import com.lcy.yozoraticket.entity.Order;
import com.lcy.yozoraticket.entity.OrderItem;
import com.lcy.yozoraticket.entity.Payments;
import com.lcy.yozoraticket.enums.ErrorCode;
import com.lcy.yozoraticket.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderMessageProducer {

    private final RabbitTemplate rabbitTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;


    /**
     * 组装购票所依赖的实体类
     * @param orderNo
     */
    public void sendCreated(String orderNo){
        try {
            //获取订单实体
            Order order = read(
                    RedisConstant.TICKET_ORDER_KEY + orderNo,
                    Order.class
            );

            Long orderId = order.getOrderId();

            //通过订单明细所依赖的orderId来获取订单明细实体
            OrderItem orderItem = read(
                    RedisConstant.TICKET_ORDER_ITEM_KEY + orderId.toString(),
                    OrderItem.class
            );

            //通过支付记录所依赖的orderId来获取支付记录实体
            Payments payments = read(
                    RedisConstant.TICKET_PAYMENT_KEY + orderId.toString(),
                    Payments.class
            );

            //组装实体
            OrderCreatedMessage orderCreatedMessage = new OrderCreatedMessage(order,orderItem,payments);

            String messageJson = objectMapper.writeValueAsString(orderCreatedMessage);

            //将json发送到交换机
            rabbitTemplate.convertAndSend(
                    "",
                    RabbitMqConfig.YOZORA_TICKET_QUEUE,
                    messageJson
            );


        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.JSON_DESERIALIZATION_ERROR);
        }

    }


    /**
     * 从redis获取数据并反序列化然后返回
     * @param key
     * @param type
     * @return
     * @param <T>
     * @throws JsonProcessingException
     */
    private <T> T read(String key,Class<T> type) throws JsonProcessingException {
        String json = stringRedisTemplate.opsForValue().get(key);
        return objectMapper.readValue(json,type);

    }

}
