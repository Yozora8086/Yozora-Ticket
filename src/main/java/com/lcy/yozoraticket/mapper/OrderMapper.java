package com.lcy.yozoraticket.mapper;

import com.lcy.yozoraticket.entity.Order;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OrderMapper {

    /**
     * 插入用户购票订单数据
     * @param order
     */
    @Insert("insert into orders (order_no,user_id,total,status,pay_close_time)" +
            "values " +
            "(#{order.orderNo},#{order.userId},#{order.total},#{order.status},#{order.payCloseTime})")
    void insert(@Param("order") Order order);
}
