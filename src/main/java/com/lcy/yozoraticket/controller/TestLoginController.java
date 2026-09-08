package com.lcy.yozoraticket.controller;

import com.lcy.yozoraticket.utils.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/yozora/test")
public class TestLoginController {

    /**
     * 测试
     * @return
     */
    @GetMapping("/login")
    public Result test(){
        System.out.println("嗯哼");
        return Result.success("测试");
    }

}
