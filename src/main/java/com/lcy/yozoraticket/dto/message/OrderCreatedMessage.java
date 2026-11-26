package com.lcy.yozoraticket.dto.message;

import com.lcy.yozoraticket.entity.Order;
import com.lcy.yozoraticket.entity.OrderItem;
import com.lcy.yozoraticket.entity.Payments;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedMessage {
    //订单
    private Order order;
    //订单详情
    private OrderItem orderItem;
    //支付详情
    private Payments payments;
}
