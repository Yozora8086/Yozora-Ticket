package com.lcy.yozoraticket.mq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lcy.yozoraticket.config.RabbitMqConfig;
import com.lcy.yozoraticket.dto.message.TicketSuccessMessage;
import com.lcy.yozoraticket.enums.ErrorCode;
import com.lcy.yozoraticket.exception.BusinessException;
import com.lcy.yozoraticket.mapper.OrderMapper;
import com.lcy.yozoraticket.mapper.PaymentsMapper;
import com.lcy.yozoraticket.mapper.TicketTiersMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TicketSuccessConsumer {

    private final ObjectMapper objectMapper;
    private final OrderMapper orderMapper;
    private final PaymentsMapper paymentsMapper;
    private final TicketTiersMapper ticketTiersMapper;

    /**
     * 成功购票队列消费者
     * @param json
     */
    @RabbitListener(queues = RabbitMqConfig.YOZORA_TICKET_SUCCESS_QUEUE)
    @Transactional (rollbackFor = Exception.class)
    public void receive(String json) {
        TicketSuccessMessage ticketSuccessMessage;

        //将传过来的json反序列化TicketSuccessMessage对象
        try {
            ticketSuccessMessage = objectMapper.readValue(json, TicketSuccessMessage.class);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.JSON_DESERIALIZATION_ERROR);
        }

        //订单、订单明细状态修改
        orderMapper.updateOrderStatus(
                ticketSuccessMessage.getOrderId(),
                ticketSuccessMessage.getStatus()
        );
       paymentsMapper.updateStatus(
               ticketSuccessMessage.getOrderId(),
               ticketSuccessMessage.getStatus()
       );

       //票库存，冻结库存释放
       ticketTiersMapper.updateUpdateCommitLock(
               ticketSuccessMessage.getEventSessionId(),
               ticketSuccessMessage.getTicketLevel(),
               ticketSuccessMessage.getCount()
       );


    }
}
