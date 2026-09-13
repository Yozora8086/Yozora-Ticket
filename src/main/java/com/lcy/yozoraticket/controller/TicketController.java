package com.lcy.yozoraticket.controller;

import com.lcy.yozoraticket.service.TicketService;
import com.lcy.yozoraticket.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/yozora/ticket")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping("/getTicket")
    public Result ticket(String orderNo){
        ticketService.getTicket(orderNo);
        return Result.success("成功");
    }
}
