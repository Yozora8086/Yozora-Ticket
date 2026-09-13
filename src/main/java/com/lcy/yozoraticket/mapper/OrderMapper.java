package com.lcy.yozoraticket.mapper;

import com.lcy.yozoraticket.entity.Order;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OrderMapper {

    /**
     * 插入用户购票订单数据
     * @param order
     */
    void insert(Order order);

    /**
     * 查询用户订单
     * @param orderId
     * @return
     */
    @Select("select * from orders where order_id = #{orderId}")
    Order select(Long orderId);


}
