package com.lcy.yozoraticket.service;

import com.lcy.yozoraticket.dto.TicketBuyDTO;
import com.lcy.yozoraticket.entity.Order;
import com.lcy.yozoraticket.entity.OrderItem;

public interface OrderItemService {
    /**
     * 生成订单明细
     * @param ticketBuyDTO
     * @param order
     * @return
     */
    OrderItem orderItem(TicketBuyDTO ticketBuyDTO, Order order);
}
