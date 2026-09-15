package com.lcy.yozoraticket.controller;

import com.lcy.yozoraticket.service.TicketService;
import com.lcy.yozoraticket.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/yozora/ticket")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    /**
     * 生成演出电子票
     * @param orderNo
     * @return
     */
    @PostMapping("/createTicket")
    public Result ticket(@RequestParam String orderNo){
        ticketService.getTicket(orderNo);
        return Result.success("成功");
    }
}
