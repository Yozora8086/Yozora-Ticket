package com.lcy.yozoraticket.controller;

import com.lcy.yozoraticket.dto.EventSessionsDTO;
import com.lcy.yozoraticket.service.EventSessionService;
import com.lcy.yozoraticket.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/yozora/eventSession")
@RequiredArgsConstructor
public class EventSessionController {

    private final EventSessionService eventSessionService;

    /**
     * 修改演出场次信息
     * @param eventSessionsDTO
     * @return
     */
    @PatchMapping("updateEventSession")
    public Result updateEventSession(@RequestBody EventSessionsDTO eventSessionsDTO){
        eventSessionService.updateEventSession(eventSessionsDTO);
        return Result.success("修改成功");
    }
}
