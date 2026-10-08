package com.lcy.yozoraticket.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketTiersVO {
    //票等级Id
    private Long ticketTiersId;
    //演出场次id
    private Long eventSessionId;
    //票等级
    private Byte ticketLevel;
    //票价格
    private Integer ticketPrice;
    //库存可售票数
    private Integer ticketPermit;

}
