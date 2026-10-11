package com.lcy.yozoraticket.service;

import com.lcy.yozoraticket.dto.TicketTiersDTO;

import java.util.List;

public interface TicketTiersService {
    /**
     * 修改票等级的数据
     * @param ticketTiersDTOList
     */
    void updateTicketTier(List<TicketTiersDTO> ticketTiersDTOList);
}
