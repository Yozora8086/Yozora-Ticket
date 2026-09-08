package com.lcy.yozoraticket.service;

import com.lcy.yozoraticket.dto.UserLoginDTO;

public interface LoginService {

    /**
     * 用户登录
     * @param userLoginDTO
     * @return
     */
    String login(UserLoginDTO userLoginDTO);
}
