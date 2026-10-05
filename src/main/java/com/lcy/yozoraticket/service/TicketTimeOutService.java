package com.lcy.yozoraticket.service;

import com.lcy.yozoraticket.entity.Ticket;
import com.lcy.yozoraticket.mapper.TicketsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketTimeOutService {

    private final TicketsMapper ticketsMapper;

    /**
     * 来判断电子票是否超越核销时间，然后修改状态
     */
    @Transactional
    public void closeTicket() {
        //获取所有电子票未核销但已超出核销时间的票
        List<Ticket> tickets = ticketsMapper.selectTimeOutTicket();
        //将所有满足条件的电子票作废
        Integer ticketRows = ticketsMapper.updateTimeOutStatus(tickets);
        System.err.println(ticketRows);

    }
}
