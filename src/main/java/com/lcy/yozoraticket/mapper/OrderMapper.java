package com.lcy.yozoraticket.mapper;

import com.lcy.yozoraticket.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

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


    /**
     * 查询订单表中所有超时的订单
     * @return
     */
    List<Order> selectAllOrderTimeOut();

    /**
     * 修改当前订单的支付状态为超时
     * @return 影响行数
     */
    Integer updateOrderStatus(Long orderId,Byte status);

    /**
     * 根据订单号查询订单
     * @param orderNo
     * @param userId
     * @return
     */
    @Select("SELECT * FROM orders WHERE order_no = #{orderNo} and user_id = #{userId}")
    Order selectOrderByOrderNo(String orderNo,Long userId);

    /**
     * 核销演出电子票
     * @param ticketCode
     * @return
     */
    Long selectOrderByTicketCode(String ticketCode);
}
