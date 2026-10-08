package com.lcy.yozoraticket.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventSessionsVO {
    //演出场次id
    private Long eventSessionId;
    //演出id
    private Long eventId;
    //场地id
    private Long venueId;
    //演出场次开始时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    //演出场次票开售时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sellTime;
    //演出场地对象
    private VenueVO venueVO;
    //每场演出所属的票级别
    private List<TicketTiersVO> ticketTiersVOList;
}
