package com.lcy.yozoraticket.controller;

import com.lcy.yozoraticket.dto.UserLoginDTO;
import com.lcy.yozoraticket.service.LoginService;
import com.lcy.yozoraticket.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("yozora/user")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    /**
     * 用户登录
     * @param userLoginDTO
     * @return
     */
    @GetMapping("login")
    public Result<String> login(@RequestBody UserLoginDTO userLoginDTO){
         String token = loginService.login(userLoginDTO);

         return Result.success(token,"登录成功");
    }
}
