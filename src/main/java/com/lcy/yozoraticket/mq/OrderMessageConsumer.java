package com.lcy.yozoraticket.mq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lcy.yozoraticket.config.RabbitMqConfig;
import com.lcy.yozoraticket.dto.message.OrderCreatedMessage;
import com.lcy.yozoraticket.enums.ErrorCode;
import com.lcy.yozoraticket.exception.BusinessException;
import com.lcy.yozoraticket.service.OrderPersistenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderMessageConsumer {

    private final ObjectMapper objectMapper;
    private final OrderPersistenceService orderPersistenceService;

    /**
     * 订单消息消费者
     * @param json
     */
    @RabbitListener(queues = RabbitMqConfig.YOZORA_TICKET_QUEUE) //监听指定队列
    public void receive(String json){
        OrderCreatedMessage message;
        //将接收到的消息反序列化成对象
        try{
            message = objectMapper.readValue(json,OrderCreatedMessage.class);
        } catch (JsonProcessingException e){
            throw new BusinessException(ErrorCode.JSON_DESERIALIZATION_ERROR);
        }

        //购票所依赖的所有数据落库
        orderPersistenceService.saveCreated(message);

    }
}
