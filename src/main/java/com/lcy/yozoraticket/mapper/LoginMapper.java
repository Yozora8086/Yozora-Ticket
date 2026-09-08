package com.lcy.yozoraticket.mapper;

import com.lcy.yozoraticket.dto.UserLoginDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LoginMapper {

    /**
     * 查询当前用户的用户id
     * @param userLoginDTO
     * @return
     */
    Long userLogin(@Param("userLoginDTO") UserLoginDTO userLoginDTO);
}
