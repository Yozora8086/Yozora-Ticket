package com.lcy.yozoraticket.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lcy.yozoraticket.vo.TicketTiersVO;
import com.lcy.yozoraticket.vo.VenueVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventSessionNoListDTO {
    //演出场次id
    private Long eventSessionId;
    //演出id
    private Long eventId;
    //场地id
    private Long venueId;
    //演出场次开始时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    //每场演出对应的状态
    private Byte status;
    //演出场地对象
    private VenueVO venueVO;
    //每场演出所属的票级别
    private TicketTiersVO ticketTiersVO;
}
