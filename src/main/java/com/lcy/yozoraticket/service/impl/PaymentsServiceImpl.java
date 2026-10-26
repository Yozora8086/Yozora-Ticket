package com.lcy.yozoraticket.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lcy.yozoraticket.constant.RedisConstant;
import com.lcy.yozoraticket.dto.EventOrderDTO;
import com.lcy.yozoraticket.dto.TicketBuyDTO;
import com.lcy.yozoraticket.entity.Order;
import com.lcy.yozoraticket.entity.Payments;
import com.lcy.yozoraticket.enums.ErrorCode;
import com.lcy.yozoraticket.exception.BusinessException;
import com.lcy.yozoraticket.mapper.PaymentsMapper;
import com.lcy.yozoraticket.service.PaymentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PaymentsServiceImpl implements PaymentsService {

    private final PaymentsMapper paymentsMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 生成支付记录
     * @param ticketBuyDTO
     * @param eventOrderDTO
     */
    @Override
    public void Payments(TicketBuyDTO ticketBuyDTO, EventOrderDTO eventOrderDTO) {

        //创建支付记录对象
        Payments payments = Payments.builder()
                .orderId(eventOrderDTO.getOrderId())
                .paymentNo("P" + System.currentTimeMillis() + String.format("%08d", ThreadLocalRandom.current().nextInt(100_000_000)))
                .amount(ticketBuyDTO.getCount() * eventOrderDTO.getEventSessionNoListDTO().getTicketTiersVO().getTicketPrice())
                .status((byte) 0)
                .createPayTime(LocalDateTime.now())
                .build();

        //将支付记录对象序列化成json
        String paymentJson;
        try {
            paymentJson = objectMapper.writeValueAsString(payments);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.JSON_SERIALIZATION_ERROR);
        }

        //将支付记录信息缓存到redis
        stringRedisTemplate.opsForValue().set(
                RedisConstant.TICKET_PAYMENT_KEY + eventOrderDTO.getOrderId(),
                paymentJson,
                7,
                TimeUnit.DAYS
        );

    }
}
