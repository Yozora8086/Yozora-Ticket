package com.lcy.yozoraticket.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    //订单明细id
    private Long orderItemId;
    //订单id
    private Long orderId;
    //票档
    private Byte ticketLevel;
    //购买数量
    private Byte count;
    //下单单价
    private Integer price;
    //下单时演出的名称
    private String eventName;
    //下单时的场次时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sessionTime;
    //下单时的场馆名称
    private String venueName;
}
