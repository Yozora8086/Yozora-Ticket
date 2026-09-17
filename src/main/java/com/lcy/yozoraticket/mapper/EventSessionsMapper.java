package com.lcy.yozoraticket.mapper;

import com.lcy.yozoraticket.dto.query.EventSessionSellTimeQueryDTO;
import com.lcy.yozoraticket.dto.query.OrderItemQueryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

@Mapper
public interface EventSessionsMapper {

    OrderItemQueryDTO selectEventMsg(Long eventSessionId);

    /**
     * 查询当前演出开售时间
     * @param eventSessionId
     * @return
     */
    @Select("SELECT sell_time,status FROM event_sessions WHERE event_session_id = #{eventSessionId}")
    EventSessionSellTimeQueryDTO selectStartSellTime(Long eventSessionId);
}
