package com.lcy.yozoraticket.service.impl;

import com.lcy.yozoraticket.context.BaseContext;
import com.lcy.yozoraticket.dto.TicketBuyDTO;
import com.lcy.yozoraticket.dto.query.OrderItemQueryDTO;
import com.lcy.yozoraticket.entity.Order;
import com.lcy.yozoraticket.entity.OrderItem;
import com.lcy.yozoraticket.entity.Payments;
import com.lcy.yozoraticket.mapper.*;
import com.lcy.yozoraticket.service.TicketBuyService;
import com.lcy.yozoraticket.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class TicketBuyServiceBuyImpl implements TicketBuyService {

    private final TicketTiersMapper ticketTiersMapper;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final EventSessionsMapper eventSessionsMapper;
    private final PaymentsMapper paymentsMapper;

    /**
     * 用户购票
     * @param ticketBuyDTO
     * @return
     */
    @Transactional
    @Override
    public Order buyTicket(TicketBuyDTO ticketBuyDTO) {
        //用户购票等级
        Byte ticketLevel = ticketBuyDTO.getTicketLevel();
        //用户购买的哪一场的票
        Long eventSessionId = ticketBuyDTO.getEventSessionId();

        //查询用户购票的等级所对应的价格
        Integer ticketPrice = ticketTiersMapper.selectTicketPrice(ticketLevel,eventSessionId);

        //购票，数据库票数减少
        ticketTiersMapper.buyTicketSuccess(ticketBuyDTO);

        //创建订单
        Order order = new Order();
        //生成订单号(时间戳加8位随机数)
        order.setOrderNo(System.currentTimeMillis() + String.format("%08d", ThreadLocalRandom.current().nextInt(100_000_000)));
        order.setEventSessionId(ticketBuyDTO.getEventSessionId());
        order.setUserId(BaseContext.getCurrentId());
        order.setTotal(ticketPrice * ticketBuyDTO.getCount());
        order.setTicketTotal(ticketBuyDTO.getCount());
        order.setStatus((byte) 0);
        order.setTicketLevel(ticketLevel);

        //支付倒计时
        order.setPayCloseTime(LocalDateTime.now().plusMinutes(15));

        orderMapper.insert(order);

        //查询  下单时的演出名称, 下单时的场次时间, 下单时的场馆名称
        OrderItemQueryDTO orderItemQueryDTO = eventSessionsMapper.selectEventMsg(ticketBuyDTO.getEventSessionId());

        System.err.println(orderItemQueryDTO);

        //创建订单明细
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(order.getOrderId());
        orderItem.setTicketLevel(ticketLevel);
        orderItem.setCount(ticketBuyDTO.getCount());
        orderItem.setPrice(ticketPrice);
        orderItem.setEventName(orderItemQueryDTO.getEventName());
        orderItem.setSessionTime(orderItemQueryDTO.getStartTime());
        orderItem.setVenueName(orderItemQueryDTO.getVenueName());

        //插入订单明细
        orderItemMapper.insertOrderItem(orderItem);

        //创建支付记录对象
        Payments payments = new Payments();

        payments.setOrderId(order.getOrderId());
        payments.setPaymentNo("P" + System.currentTimeMillis() + String.format("%08d", ThreadLocalRandom.current().nextInt(100_000_000)));
        payments.setAmount(ticketPrice * ticketBuyDTO.getCount());
        payments.setStatus((byte) 0);
        payments.setCreatePayTime(LocalDateTime.now());

        //数据库插入支付记录
        paymentsMapper.insertPayment(payments);


        return order;
    }


}
