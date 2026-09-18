package com.lcy.yozoraticket.controller;

import com.lcy.yozoraticket.dto.TicketBuyDTO;
import com.lcy.yozoraticket.entity.Order;
import com.lcy.yozoraticket.entity.OrderItem;
import com.lcy.yozoraticket.service.OrderItemService;
import com.lcy.yozoraticket.service.PaymentsService;
import com.lcy.yozoraticket.service.TicketBuyService;
import com.lcy.yozoraticket.service.TicketSuperService;
import com.lcy.yozoraticket.utils.Result;
import com.lcy.yozoraticket.vo.OrderItemVO;
import com.lcy.yozoraticket.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/yozora/buy")
@RequiredArgsConstructor
public class TicketBuyController {

    private final TicketSuperService ticketSuperService;

    /**
     * 用户购票
     * @param ticketBuyDTO
     * @return
     */
    @PutMapping("/ticketBuy")
    public Result<OrderVO> tickBuy(@RequestBody TicketBuyDTO ticketBuyDTO){

        //调用购票业务流程Service
        Order order = ticketSuperService.buyTicket(ticketBuyDTO);

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
