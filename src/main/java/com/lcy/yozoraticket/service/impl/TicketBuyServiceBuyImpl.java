package com.lcy.yozoraticket.service.impl;

import com.lcy.yozoraticket.context.BaseContext;
import com.lcy.yozoraticket.dto.TicketBuyDTO;
import com.lcy.yozoraticket.dto.query.EventSessionSellTimeQueryDTO;
import com.lcy.yozoraticket.entity.Order;
import com.lcy.yozoraticket.enums.ErrorCode;
import com.lcy.yozoraticket.exception.BusinessException;
import com.lcy.yozoraticket.mapper.*;
import com.lcy.yozoraticket.service.OrderTimeOutService;
import com.lcy.yozoraticket.service.TicketBuyService;
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
    private final OrderTimeOutService orderTimeOutService;

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

        System.err.println(sessionSellTimeQueryDTO);

        //判断当前演出是否可以购买
        if (now.isBefore(sessionSellTimeQueryDTO.getSellTime())) {
            throw new BusinessException(ErrorCode.TICKET_TIME_NOT_SELL);
        }

        //判断当前演出的状态是否允许购买
        if (sessionSellTimeQueryDTO.getStatus() == (byte) 1){
            throw new BusinessException(ErrorCode.TICKET_STATUS_NOT_SELL);
        }

        //判断当前票仓是否有票
        if (sessionSellTimeQueryDTO.getTicketPermit() < ticketBuyDTO.getCount()){
            throw new BusinessException(ErrorCode.NOT_TICKET);
        }

        //查询用户购票的等级所对应的价格
        Integer ticketPrice = ticketTiersMapper.selectTicketPrice(ticketLevel,eventSessionId);

        //购票，数据库票数减少,返回数据库影响的行数
        Integer rows = ticketTiersMapper.buyTicketSuccess(ticketBuyDTO);
        if (rows == 0){
            throw new BusinessException(ErrorCode.TICKET_SELL_ERROR);
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

    /**
     * 用户取消订单
     * @param orderNo
     */
    @Transactional
    @Override
    public void backTicket(String orderNo) {
        //初始化状态值为超时
        Byte orderStatus = 2;
        //初始化状态值为已取消
        Byte paymentStatus = 2;

        //获取当前用户id
        Long userId = BaseContext.getCurrentId();

        //根据订单号查询订单
        Order order = orderMapper.selectOrderByOrderNo(orderNo,userId);
        //取消订单
        Integer rows = orderMapper.updateOrderStatus(order.getOrderId(), orderStatus);
        if (rows == 1){
            //获取当前订单买票数量
            Byte ticketTotal = order.getTicketTotal();
            //释放库存
            ticketTiersMapper.updateUpdateLock(
                    order.getEventSessionId(),
                    order.getTicketLevel(),
                    ticketTotal
            );
            //数据库修改支付记录状态 设置状态已取消
            paymentsMapper.updateStatus(order.getOrderId(),paymentStatus);
        } else {
            //抛出 当前订单已支付或者已取消 异常
           throw new BusinessException(ErrorCode.ORDER_STATUS_IS_PAY_OR_TIMEOUT);
        }
    }

    /**
     * 用户提交购票订单
     * @param orderNo
     */
    @Override
    @Transactional
    public void buyCommitTicket(String orderNo) {
        //初始化状态值为已支付
        Byte orderStatus = 1;
        //初始化状态值为支付成功
        Byte paymentStatus = 1;

        //获取当前用户id
        Long userId = BaseContext.getCurrentId();

        //根据订单号查询订单
        Order order = orderMapper.selectOrderByOrderNo(orderNo,userId);
        //支付订单 (返回影响的行数)
        Integer rows = orderMapper.updateOrderStatus(order.getOrderId(),orderStatus);
        //如果影响行数为1行，则代表状态未修改
        if (rows == 1){
            //获取当前订单买票数量
            Byte ticketTotal = order.getTicketTotal();
            //释放库存
            ticketTiersMapper.updateUpdateCommitLock(
                    order.getEventSessionId(),
                    order.getTicketLevel(),
                    ticketTotal
            );

            //数据库修改支付记录状态 设置状态已支付成功
            paymentsMapper.updateStatusCommit(order.getOrderId(),paymentStatus,(byte) 0);
        } else {
            //状态已被修改，抛出异常
            throw new BusinessException(ErrorCode.ORDER_STATUS_IS_PAY_OR_TIMEOUT);
        }

    }
}
