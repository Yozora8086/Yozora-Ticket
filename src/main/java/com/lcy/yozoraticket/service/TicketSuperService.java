package com.lcy.yozoraticket.service;

import com.lcy.yozoraticket.dto.EventOrderDTO;
import com.lcy.yozoraticket.dto.TicketBuyDTO;
import com.lcy.yozoraticket.entity.Order;
import com.lcy.yozoraticket.vo.EventVO;
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
    public EventOrderDTO buyTicket(TicketBuyDTO ticketBuyDTO){
        //生成订单
        EventOrderDTO eventOrderDTO = ticketBuyService.buyTicket(ticketBuyDTO);

        //生成订单明细
        orderItemService.orderItem(ticketBuyDTO, eventOrderDTO);

        //生成支付记录
        paymentsService.Payments(ticketBuyDTO, eventOrderDTO);

        return eventOrderDTO;
    }

}
