package com.lcy.yozoraticket.controller;

import com.lcy.yozoraticket.dto.RegisterDTO;
import com.lcy.yozoraticket.dto.UserLoginDTO;
import com.lcy.yozoraticket.service.LoginService;
import com.lcy.yozoraticket.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("register")
    public Result register(@RequestBody RegisterDTO registerDTO){
        loginService.register(registerDTO);
        return Result.success("注册成功");
    }
}
