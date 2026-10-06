package com.lcy.yozoraticket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketTiersDTO {
    //演出场次id
    private Long eventSessionId;
    //票等级
    private Byte ticketLevel;
    //票价格
    private Integer ticketPrice;
    //总票数
    private Integer ticketAll;
    //库存可售票数
    private Integer ticketPermit;
    //库存冻结票数
    private Integer ticketLock;
}
