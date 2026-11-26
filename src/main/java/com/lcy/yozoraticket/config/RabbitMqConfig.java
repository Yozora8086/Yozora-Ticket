package com.lcy.yozoraticket.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String YOZORA_TICKET_QUEUE = "yozora.ticket.queue";
    public static final String YOZORA_TICKET_SUCCESS_QUEUE = "yozora.ticket.success.queue";

    /**
     * 购票队列
     * @return
     */
    @Bean
    public Queue ticketQueue(){
        return new Queue(YOZORA_TICKET_QUEUE,true);
    }

    /**
     * 提交订单队列
     * @return
     */
    @Bean
    public Queue ticketSuccessQueue() {
        return new Queue(YOZORA_TICKET_SUCCESS_QUEUE,true);
    }
}
