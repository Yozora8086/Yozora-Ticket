package com.lcy.yozoraticket.service.impl;

import com.lcy.yozoraticket.dto.TicketBuyDTO;
import com.lcy.yozoraticket.dto.query.OrderItemQueryDTO;
import com.lcy.yozoraticket.entity.Order;
import com.lcy.yozoraticket.entity.OrderItem;
import com.lcy.yozoraticket.mapper.EventSessionsMapper;
import com.lcy.yozoraticket.mapper.OrderItemMapper;
import com.lcy.yozoraticket.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {

    private final EventSessionsMapper eventSessionsMapper;
    private final OrderItemMapper orderItemMapper;

    /**
     * 生成订单明细
     * @param ticketBuyDTO 购票参数对象
     * @param order 订单
     * @return
     */
    @Override
    public OrderItem orderItem(TicketBuyDTO ticketBuyDTO, Order order) {
        //查询  下单时的演出名称, 下单时的场次时间, 下单时的场馆名称
        OrderItemQueryDTO orderItemQueryDTO = eventSessionsMapper.selectEventMsg(ticketBuyDTO.getEventSessionId());

        //创建订单明细
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(order.getOrderId());
        orderItem.setTicketLevel(ticketBuyDTO.getTicketLevel());
        orderItem.setCount(ticketBuyDTO.getCount());
        orderItem.setPrice(order.getTotal() / ticketBuyDTO.getCount());
        orderItem.setEventName(orderItemQueryDTO.getEventName());
        orderItem.setSessionTime(orderItemQueryDTO.getStartTime());
        orderItem.setVenueName(orderItemQueryDTO.getVenueName());

        //插入订单明细
        orderItemMapper.insertOrderItem(orderItem);
        return orderItem;
    }
}
