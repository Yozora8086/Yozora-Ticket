package com.lcy.yozoraticket.service;

import com.lcy.yozoraticket.dto.TicketBuyDTO;
import com.lcy.yozoraticket.entity.Order;

public interface TicketBuyService {
    /**
     * 用户购票
     * @param ticketBuyDTO
     * @return
     */
    Order buyTicket(TicketBuyDTO ticketBuyDTO);
}
