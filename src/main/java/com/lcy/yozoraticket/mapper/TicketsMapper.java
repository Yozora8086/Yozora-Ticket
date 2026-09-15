package com.lcy.yozoraticket.mapper;

import com.lcy.yozoraticket.entity.Ticket;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TicketsMapper {
    /**
     * 将电子票数据插入数据库
     * @param ticket
     */
    void insertTicket(Ticket ticket);
}
