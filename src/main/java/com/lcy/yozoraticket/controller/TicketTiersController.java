package com.lcy.yozoraticket.controller;

import com.lcy.yozoraticket.dto.TicketTiersDTO;
import com.lcy.yozoraticket.service.TicketTiersService;
import com.lcy.yozoraticket.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/yozora/ticketTiers")
@RequiredArgsConstructor
public class TicketTiersController {
    private final TicketTiersService ticketTiersService;

    /**
     * 修改票等级的数据
     * @param ticketTiersDTOList
     * @return
     */
    @PatchMapping("/updateTicketTier")
    public Result updateTicketTier(@RequestBody List<TicketTiersDTO> ticketTiersDTOList){
        ticketTiersService.updateTicketTier(ticketTiersDTOList);
        return Result.success("修改成功");
    }
}
