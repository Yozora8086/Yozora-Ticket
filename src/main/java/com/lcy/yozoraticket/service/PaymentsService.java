package com.lcy.yozoraticket.service;

import com.lcy.yozoraticket.dto.TicketBuyDTO;
import com.lcy.yozoraticket.entity.Order;

public interface PaymentsService {
    /**
     * 生成支付记录
     * @param ticketBuyDTO
     * @param order
     */
    void Payments(TicketBuyDTO ticketBuyDTO, Order order);
}
