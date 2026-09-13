package com.lcy.yozoraticket.mapper;

import com.lcy.yozoraticket.dto.query.OrderItemQueryDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EventSessionsMapper {

    OrderItemQueryDTO selectEventMsg(Long eventSessionId);
}
