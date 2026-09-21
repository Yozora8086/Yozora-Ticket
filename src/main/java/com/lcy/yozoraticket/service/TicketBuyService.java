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


    /**
     * 用户取消订单
     * @param orderNo
     */
    void backTicket(String orderNo);

    /**
     * 用户提交购票订单
     * @param orderNo
     */
    void buyCommitTicket(String orderNo);
}
