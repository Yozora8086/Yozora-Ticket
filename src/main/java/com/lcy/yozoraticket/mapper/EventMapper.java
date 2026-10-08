package com.lcy.yozoraticket.mapper;

import com.lcy.yozoraticket.dto.AddEventDTO;
import com.lcy.yozoraticket.vo.EventListVO;
import com.lcy.yozoraticket.vo.EventVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EventMapper {
    /**
     * 分页查询演出
     * @param offset
     * @param pageSize
     * @return
     */
    List<EventListVO> selectEventList(Integer offset, Integer pageSize);

    /**
     * 新增演出
     * @param addEventDTO
     */
    void addEvent(AddEventDTO addEventDTO);

    /**
     * 删除演出
     * @param eventId
     */
    void deleteEventById(Long eventId);

    /**
     * 根据演出id查询演出详情
     * @param eventId
     * @return
     */
    EventVO selectEventDetail(Long eventId);
}
