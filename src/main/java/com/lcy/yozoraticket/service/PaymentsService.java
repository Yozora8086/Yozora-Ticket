package com.lcy.yozoraticket.service;

import com.lcy.yozoraticket.dto.EventOrderDTO;
import com.lcy.yozoraticket.dto.TicketBuyDTO;
import com.lcy.yozoraticket.entity.Order;

public interface PaymentsService {
    /**
     * 生成支付记录
     * @param ticketBuyDTO
     * @param eventOrderDTO
     */
    void Payments(TicketBuyDTO ticketBuyDTO, EventOrderDTO eventOrderDTO);
}
