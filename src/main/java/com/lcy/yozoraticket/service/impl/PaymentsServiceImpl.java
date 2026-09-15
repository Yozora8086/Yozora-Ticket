package com.lcy.yozoraticket.service.impl;

import com.lcy.yozoraticket.dto.TicketBuyDTO;
import com.lcy.yozoraticket.entity.Order;
import com.lcy.yozoraticket.entity.Payments;
import com.lcy.yozoraticket.mapper.PaymentsMapper;
import com.lcy.yozoraticket.service.PaymentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class PaymentsServiceImpl implements PaymentsService {
    private final PaymentsMapper paymentsMapper;

    /**
     * 生成支付记录
     * @param ticketBuyDTO
     * @param order
     */
    @Override
    public void Payments(TicketBuyDTO ticketBuyDTO, Order order) {
        //创建支付记录对象
        Payments payments = new Payments();

        payments.setOrderId(order.getOrderId());
        payments.setPaymentNo("P" + System.currentTimeMillis() + String.format("%08d", ThreadLocalRandom.current().nextInt(100_000_000)));
        payments.setAmount(order.getTotal());
        payments.setStatus((byte) 0);
        payments.setCreatePayTime(LocalDateTime.now());

        //数据库插入支付记录
        paymentsMapper.insertPayment(payments);
    }
}
