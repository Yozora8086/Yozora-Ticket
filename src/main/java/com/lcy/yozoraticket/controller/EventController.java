package com.lcy.yozoraticket.controller;

import com.lcy.yozoraticket.dto.AddEventDTO;
import com.lcy.yozoraticket.service.EventService;
import com.lcy.yozoraticket.utils.Result;
import com.lcy.yozoraticket.vo.EventListVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/yozora/ticket")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    /**
     * 分页查询演出
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/showEventList")
    public Result<List<EventListVO>> showEventList(@RequestParam Integer page, Integer pageSize){
        List<EventListVO> eventList = eventService.selectEventList(page,pageSize);
        return Result.success(eventList,"分页查询成功");
    }

    /**
     * 新增演出
     * @param addEventDTO
     * @return
     */
    @PostMapping("/addEvent")
    public Result addEvent(@RequestBody AddEventDTO addEventDTO){
        eventService.addEvent(addEventDTO);
        return Result.success("新增演出成功");
    }
}
