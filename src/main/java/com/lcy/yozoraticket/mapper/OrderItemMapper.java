package com.lcy.yozoraticket.mapper;

import com.lcy.yozoraticket.dto.query.OrderItemQueryDTO;
import com.lcy.yozoraticket.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderItemMapper {

    /**
     * 插入订单明细
     * @param orderItem
     */
    void insertOrderItem(OrderItem orderItem);

    /**
     * 查询订单详细id
     * @param orderId
     * @return
     */
    Long selectOrderId(Long OrderId);
}
