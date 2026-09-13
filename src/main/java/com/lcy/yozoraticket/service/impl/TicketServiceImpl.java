package com.lcy.yozoraticket.service.impl;

import com.lcy.yozoraticket.mapper.OrderItemMapper;
import com.lcy.yozoraticket.mapper.OrderMapper;
import com.lcy.yozoraticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final OrderItemMapper orderItemMapper;
    @Override
    public void getTicket(String orderNo) {
        Long orderId = orderItemMapper.selectOrderId(orderNo);


    }
}
