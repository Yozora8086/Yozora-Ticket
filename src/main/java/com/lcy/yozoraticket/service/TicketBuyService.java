package com.lcy.yozoraticket.service;

import com.lcy.yozoraticket.dto.TicketBuyDTO;
import com.lcy.yozoraticket.entity.Order;
import com.lcy.yozoraticket.vo.OrderVO;

public interface TicketBuyService {
    /**
     * 用户购票
     * @param ticketBuyDTO
     * @return
     */
    Order buyTicket(TicketBuyDTO ticketBuyDTO);


}
