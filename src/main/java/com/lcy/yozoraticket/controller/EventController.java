package com.lcy.yozoraticket.controller;

import com.lcy.yozoraticket.dto.AddEventDTO;
import com.lcy.yozoraticket.dto.EventDTO;
import com.lcy.yozoraticket.dto.TicketTiersDTO;
import com.lcy.yozoraticket.service.EventService;
import com.lcy.yozoraticket.utils.Result;
import com.lcy.yozoraticket.vo.EventListVO;
import com.lcy.yozoraticket.vo.EventVO;
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

    /**
     * 新增某一演出下的票档
     * @param ticketTiersDTOList
     * @return
     */
    @PostMapping("/addTicketLevel")
    public Result addTicketLevel(@RequestBody List<TicketTiersDTO> ticketTiersDTOList){
        eventService.addTicketLevel(ticketTiersDTOList);
        return Result.success("新增票档成功");
    }

    /**
     * 删除演出
     * @param eventId
     * @return
     */
    @DeleteMapping("/deleteEvent")
    public Result deleteEvent(@RequestParam Long eventId){
        eventService.deleteEvent(eventId);
        return Result.success("删除成功");
    }

    /**
     * 查看演出详情
     * @param eventId
     * @return
     */
    @GetMapping("/eventDetail")
    public Result<EventVO> eventDetail(@RequestParam Long eventId){
       EventVO eventVO = eventService.getEventDetail(eventId);
       return Result.success(eventVO,"查询成功");
    }

    /**
     * 修改演出信息
     * @param eventDTO
     * @return
     */
    @PatchMapping("/updateEvent")
    public Result updateEvent(@RequestBody EventDTO eventDTO){
        eventService.updateEvent(eventDTO);
        return Result.success("修改成功");
    }
}
