package com.lcy.yozoraticket.mapper;

import com.lcy.yozoraticket.dto.TicketBuyDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TicketTiersMapper {

    /**
     * 查询用户购票的等级所对应的价格
     * @param ticketLevel 票等级
     * @param eventSessionId  场次id
     * @return
     */
    Integer selectTicketPrice(Byte ticketLevel, Long eventSessionId);

    /**
     * 用户购票
     * @param ticketBuyDTO
     */
    Integer buyTicketSuccess(@Param("ticketBuyDTO") TicketBuyDTO ticketBuyDTO);

    /**
     * 解锁被锁定的票
     * @param ticketTotal
     */
    void updateUpdateLock(Long eventSessionId,Byte ticketLevel,Byte ticketTotal);
}
