package com.lcy.yozoraticket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemsDTO {
    //订单id
    private Long orderId;
    //票级别
    private Long ticketTierId;
    //购买数量
    private Integer count;
    //下单单价
    private Integer price;
    //演出名
    private String eventName;
    //演出场次时间
    private LocalDateTime sessionTime;
    //演出场地名
    private String venueName;
}
