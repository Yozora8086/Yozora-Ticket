package com.lcy.yozoraticket.mapper;

import com.lcy.yozoraticket.entity.Ticket;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TicketsMapper {
    /**
     * 将电子票数据插入数据库
     * @param ticket
     */
    void insertTicket(Ticket ticket);

    /**
     * 修改电子票核销状态 修改为已核销
     * @param ticketCode
     */
    void updateStatus(String ticketCode);

    /**
     * 查询出电子票已到期但未核销的电子票
     * @return
     */
    List<Ticket> selectTimeOutTicket();

    /**
     * 修改
     * @param tickets
     * @return
     */
    Integer updateTimeOutStatus(List<Ticket> tickets);
}
