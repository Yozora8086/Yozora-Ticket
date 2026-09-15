package com.lcy.yozoraticket.controller;

import com.lcy.yozoraticket.dto.TicketBuyDTO;
import com.lcy.yozoraticket.entity.Order;
import com.lcy.yozoraticket.entity.OrderItem;
import com.lcy.yozoraticket.service.OrderItemService;
import com.lcy.yozoraticket.service.PaymentsService;
import com.lcy.yozoraticket.service.TicketBuyService;
import com.lcy.yozoraticket.utils.Result;
import com.lcy.yozoraticket.vo.OrderItemVO;
import com.lcy.yozoraticket.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/yozora/buy")
@RequiredArgsConstructor
public class TicketBuyController {

    private final TicketBuyService ticketBuyService;
    private final OrderItemService orderItemService;
    private final PaymentsService paymentsService;

    /**
     * 用户购票
     * @param ticketBuyDTO
     * @return
     */
    @PutMapping("/ticketBuy")
    public Result<OrderVO> tickBuy(@RequestBody TicketBuyDTO ticketBuyDTO){
        //生成订单
        Order order = ticketBuyService.buyTicket(ticketBuyDTO);

        //生成订单明细
        orderItemService.orderItem(ticketBuyDTO, order);

        //生成支付记录
        paymentsService.Payments(ticketBuyDTO, order);


        //将实体类转VO
        OrderVO orderVO = OrderVO.builder()
                .orderNo(order.getOrderNo())
                .total(order.getTotal())
                .status(order.getStatus())
                .payCloseTime(order.getPayCloseTime())
                .build();

        return Result.success(orderVO,"购买成功");
    }



}
