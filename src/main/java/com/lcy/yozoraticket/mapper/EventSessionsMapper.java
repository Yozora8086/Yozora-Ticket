package com.lcy.yozoraticket.mapper;

import com.lcy.yozoraticket.dto.EventSessionsDTO;
import com.lcy.yozoraticket.dto.query.EventSessionSellTimeQueryDTO;
import com.lcy.yozoraticket.dto.query.OrderItemQueryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface EventSessionsMapper {

    OrderItemQueryDTO selectEventMsg(Long eventSessionId);

    /**
     * 查询当前演出开售时间
     * @param eventSessionId
     * @return
     */
    EventSessionSellTimeQueryDTO selectStartSellTime(Long eventSessionId,Byte ticketLevel);


    /**
     * 新增场次
     * @param eventSessionsDTOList
     */
    void addEventSessionList(List<EventSessionsDTO> eventSessionsDTOList);
}
