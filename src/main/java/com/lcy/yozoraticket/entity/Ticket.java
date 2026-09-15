package com.lcy.yozoraticket.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ticket {
    //电子票id
    private Long ticketId;
    //订单明细id
    private Long orderItemId;
    //唯一票码
    private String ticketCode;
    //电子票状态：0未使用，1已核销，2已作废
    private Byte status;
    //核销时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime verifiedTime;
    //出票时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
}
