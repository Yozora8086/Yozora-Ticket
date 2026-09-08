package com.lcy.yozoraticket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginDTO {
    //用户名
    private String userName;
    //用户密码
    private String userPassword;
}
