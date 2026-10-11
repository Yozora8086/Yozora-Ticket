package com.lcy.yozoraticket.mapper;

import com.lcy.yozoraticket.dto.TicketBuyDTO;
import com.lcy.yozoraticket.dto.TicketTiersDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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

    /**
     * 解锁被锁定的票(支付成功)
     * @param eventSessionId
     * @param ticketLevel
     * @param ticketTotal
     */
    void updateUpdateCommitLock(Long eventSessionId, Byte ticketLevel, Byte ticketTotal);

    /**
     * 增某一演出下的票档
     * @param ticketTiersDTOList
     */
    void addTicketLevel(List<TicketTiersDTO> ticketTiersDTOList);

    /**
     *  删除演出下所属的演出场次下属的所有票档
     * @param eventSessionList
     */
    void deleteByEventSessionId(List<Long> eventSessionList);

    /**
     * 修改票等级的数据
     * @param ticketTiersDTOList
     */
    void updateTicketTier(List<TicketTiersDTO> ticketTiersDTOList);
}
