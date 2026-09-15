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
     * 根据订单编号查询订单id
     * @param orderNo
     * @return
     */
    @Select("select order_id from orders where order_no = #{orderNo}")
    Long selectOrderId(String orderNo);


}
