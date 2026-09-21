package com.lcy.yozoraticket.service;

import com.lcy.yozoraticket.entity.Order;
import com.lcy.yozoraticket.mapper.EventSessionsMapper;
import com.lcy.yozoraticket.mapper.OrderMapper;
import com.lcy.yozoraticket.mapper.PaymentsMapper;
import com.lcy.yozoraticket.mapper.TicketTiersMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderTimeOutService {

    private final OrderMapper orderMapper;
    private final TicketTiersMapper ticketTiersMapper;
    private final PaymentsMapper paymentsMapper;

    /**
     *处理订单超时和超时订单锁定的票回到票池
     */
    @Transactional
    public void closeOrder(){
        //初始化状态值为超时
        Byte orderStatus = 2;
        Byte paymentStatus = 3;
        //查询出所有订单超时的订单
        List<Order> orderList = orderMapper.selectAllOrderTimeOut();


        //遍历订单集合
        for (Order order : orderList) {
            //返回影响行数
            Integer orderRow = orderMapper.updateOrderStatus(order.getOrderId(),orderStatus);
            Integer paymentRow = paymentsMapper.updateStatus(order.getOrderId(),paymentStatus);
            //如果未受到影响则用户已经付款或其他可能
            if (orderRow == 0 && paymentRow == 0){
                //跳过循环
                continue;
            }

            //获取当前订单买票数量
            Byte ticketTotal = order.getTicketTotal();
            //释放库存
            ticketTiersMapper.updateUpdateLock(
                    order.getEventSessionId(),
                    order.getTicketLevel(),
                    ticketTotal
            );

        }

    }

}
