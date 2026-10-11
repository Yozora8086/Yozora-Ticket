package com.lcy.yozoraticket.service.impl;

import com.lcy.yozoraticket.dto.TicketTiersDTO;
import com.lcy.yozoraticket.mapper.TicketTiersMapper;
import com.lcy.yozoraticket.service.TicketTiersService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketTiersServiceImpl implements TicketTiersService {

    private final TicketTiersMapper ticketTiersMapper;

    /**
     * 修改票等级的数据
     * @param ticketTiersDTOList
     */
    @Override
    public void updateTicketTier(List<TicketTiersDTO> ticketTiersDTOList) {
        ticketTiersMapper.updateTicketTier(ticketTiersDTOList);
    }
}
