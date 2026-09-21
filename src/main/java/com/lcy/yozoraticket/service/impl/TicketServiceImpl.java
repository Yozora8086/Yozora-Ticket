package com.lcy.yozoraticket.service.impl;

import com.lcy.yozoraticket.context.BaseContext;
import com.lcy.yozoraticket.entity.Order;
import com.lcy.yozoraticket.entity.Ticket;
import com.lcy.yozoraticket.enums.ErrorCode;
import com.lcy.yozoraticket.exception.BusinessException;
import com.lcy.yozoraticket.mapper.OrderItemMapper;
import com.lcy.yozoraticket.mapper.OrderMapper;
import com.lcy.yozoraticket.mapper.TicketsMapper;
import com.lcy.yozoraticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.ErrorResponse;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final OrderItemMapper orderItemMapper;
    private final TicketsMapper ticketsMapper;
    private final OrderMapper orderMapper;

    /**
     * 生成演出电子票
     * @param orderNo
     */
    @Override
    public void getTicket(String orderNo) {
        //根据订单编号查询订单id
        Long orderId = orderMapper.selectOrderId(orderNo);
        //根据订单id查询订单明细id
        Long orderItemId = orderItemMapper.selectOrderId(orderId);

        //创建电子票对象
        Ticket ticket = new Ticket();
        ticket.setOrderItemId(orderItemId);
        ticket.setTicketCode("T" + System.currentTimeMillis() + String.format("%08d", ThreadLocalRandom.current().nextInt(100_000_000)));
        ticket.setStatus((byte) 0);
        ticket.setCreatedTime(LocalDateTime.now());

        //数据库插入电子票数据
        ticketsMapper.insertTicket(ticket);
    }

    /**
     * 核销演出电子票
     * @param ticketCode
     */
    @Override
    public void useTicket(String ticketCode) {
       Long getUserId = orderMapper.selectOrderByTicketCode(ticketCode);
        System.err.println("1--" + getUserId);

       //判断当前电子票是否属于该用户
        Long userId = BaseContext.getCurrentId();
        System.err.println("2--" + userId);
        if (!userId.equals(getUserId)){
            throw new BusinessException(ErrorCode.TICKET_NOT_YOU);
        }
        //修改电子票核销状态 修改为已核销
        ticketsMapper.updateStatus(ticketCode);

    }
}
