package com.lcy.yozoraticket.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lcy.yozoraticket.vo.TicketTiersVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventSessionsDTO {
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
    //演出场次状态
    private Byte status;
    //当前演出所属的票等级
    private List<TicketTiersVO> ticketTiersVOList;

}
