package com.lcy.yozoraticket.dto.RedisDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderRedisDTO {
    //演出场次id
    private Long eventSessionId;
    //票等级
    private Byte ticketLevel;
    //订单总价
    private Integer total;
    //订单关闭时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payCloseTime;

}
