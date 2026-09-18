package com.lcy.yozoraticket.service.impl;

import com.lcy.yozoraticket.context.BaseContext;
import com.lcy.yozoraticket.dto.TicketBuyDTO;
import com.lcy.yozoraticket.dto.query.EventSessionSellTimeQueryDTO;
import com.lcy.yozoraticket.dto.query.OrderItemQueryDTO;
import com.lcy.yozoraticket.entity.Order;
import com.lcy.yozoraticket.exception.NotTicketException;
import com.lcy.yozoraticket.exception.TicketIsNotSellException;
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
        //查询当前演出开售时间,并返回演出状态对象
        EventSessionSellTimeQueryDTO sessionSellTimeQueryDTO = eventSessionsMapper.selectStartSellTime(eventSessionId,ticketLevel);
        //获取当前时间
        LocalDateTime now = LocalDateTime.now();

        //判断当前演出是否可以购买
        if (now.isBefore(sessionSellTimeQueryDTO.getSellTime())) {
            throw new TicketIsNotSellException("当前演出未达发售时间");
        }

        //判断当前演出的状态是否允许购买
        if (sessionSellTimeQueryDTO.getStatus() == (byte) 1){
            throw new TicketIsNotSellException("当前演出已取消，无法购买");
        }

        //判断当前票仓是否有票
        if (sessionSellTimeQueryDTO.getTicketPermit() < ticketBuyDTO.getCount()){
            throw new NotTicketException();
        }

        //查询用户购票的等级所对应的价格
        Integer ticketPrice = ticketTiersMapper.selectTicketPrice(ticketLevel,eventSessionId);

        //购票，数据库票数减少,返回数据库影响的行数
        Integer rows = ticketTiersMapper.buyTicketSuccess(ticketBuyDTO);
        if (rows == 0){
            throw new TicketIsNotSellException("购票失败");
        }


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

        //数据库插入订单
        orderMapper.insert(order);
        return order;

    }


}
