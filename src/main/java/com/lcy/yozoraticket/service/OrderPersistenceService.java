package com.lcy.yozoraticket.service;

import com.lcy.yozoraticket.dto.TicketBuyDTO;
import com.lcy.yozoraticket.dto.message.OrderCreatedMessage;
import com.lcy.yozoraticket.entity.Order;
import com.lcy.yozoraticket.enums.ErrorCode;
import com.lcy.yozoraticket.exception.BusinessException;
import com.lcy.yozoraticket.mapper.OrderItemMapper;
import com.lcy.yozoraticket.mapper.OrderMapper;
import com.lcy.yozoraticket.mapper.PaymentsMapper;
import com.lcy.yozoraticket.mapper.TicketTiersMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderPersistenceService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final PaymentsMapper paymentsMapper;
    private final TicketTiersMapper ticketTiersMapper;

    /**
     * 购票数据持久化
     * @param message
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveCreated(OrderCreatedMessage message){
        //取出订单
        Order order = message.getOrder();
        Long existId = orderMapper.selectOrderId(order.getOrderNo());

        //判断当前订单是否已经落库
        if (existId != null) {
           throw new BusinessException(ErrorCode.QUEUE_ORDER_EXIST_ERROR);
        }

        //未落库插入数据
        orderMapper.insert(order);
        orderItemMapper.insertOrderItem(message.getOrderItem());
        paymentsMapper.insertPayment(message.getPayments());

        //组装购票参数
        TicketBuyDTO ticketBuyDTO = TicketBuyDTO.builder()
                .ticketLevel(message.getOrderItem().getTicketLevel())
                .count(message.getOrderItem().getCount())
                .eventSessionId(message.getOrder().getEventSessionId())
                .build();

        //数据库修改锁定票和库存票并判断是否修改成功
        Integer row = ticketTiersMapper.buyTicketSuccess(ticketBuyDTO);
        if (row == null || row != 1) {
                throw new BusinessException(ErrorCode.QUEUE_UPDATE_DATABASE_ERROR);
        }

    }

}
