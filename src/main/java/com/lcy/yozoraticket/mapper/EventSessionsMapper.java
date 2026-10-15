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

    /**
     * 获取当前演出下的所有演出场次的id
     * @param eventId
     * @return
     */
    List<Long> getIdByEventIds(Long eventId);

    /**
     * 删除演出下所属的演出场次
     * @param eventId
     */
    void deleteByEventId(Long eventId);

    /**
     * 修改演出场次信息
     * @param eventSessionsDTO
     */
    void updateEventSession(EventSessionsDTO eventSessionsDTO);

    /**
     * 查询演出场次id(按照状态查询 status = 0)
     * @return
     */
    List<Long> selectEventSessionIdByStatus();
}
