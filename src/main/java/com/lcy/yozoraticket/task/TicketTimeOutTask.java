package com.lcy.yozoraticket.task;

import com.lcy.yozoraticket.service.TicketTimeOutService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TicketTimeOutTask {

    private final TicketTimeOutService ticketTimeOutService;

    /**
     * 定时方法，来判断电子票是否超越核销时间
     */
    @Scheduled(fixedDelay = 60000)
    void closeTicket(){
       ticketTimeOutService.closeTicket();
    }

}
