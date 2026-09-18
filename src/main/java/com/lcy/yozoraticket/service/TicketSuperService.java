package com.lcy.yozoraticket.service;

import com.lcy.yozoraticket.dto.TicketBuyDTO;
import com.lcy.yozoraticket.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TicketSuperService {

    private final TicketBuyService ticketBuyService;
    private final OrderItemService orderItemService;
    private final PaymentsService paymentsService;

    /**
     * 用户购票
     * @param ticketBuyDTO
     * @return
     */
    @Transactional
    public Order buyTicket(TicketBuyDTO ticketBuyDTO){
        //生成订单
        Order order = ticketBuyService.buyTicket(ticketBuyDTO);

        //生成订单明细
        orderItemService.orderItem(ticketBuyDTO, order);

        //生成支付记录
        paymentsService.Payments(ticketBuyDTO, order);

        return order;
    }

}
