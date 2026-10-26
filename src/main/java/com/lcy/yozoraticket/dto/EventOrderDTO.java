package com.lcy.yozoraticket.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lcy.yozoraticket.vo.EventSessionsVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventOrderDTO {
    //订单id
    private Long orderId;
    //订单号
    private String orderNo;
    //订单状态
    private Byte status;
    //订单关闭时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payCloseTime;
    //演出id
    private Long eventId;
    //演出名
    private String eventName;
    //演出海报
    private String eventPoster;
    //演出状态
    private Byte eventStatus;
    //演出场次集合
    private EventSessionNoListDTO eventSessionNoListDTO;
}
