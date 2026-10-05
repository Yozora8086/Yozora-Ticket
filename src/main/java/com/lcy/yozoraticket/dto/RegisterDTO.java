package com.lcy.yozoraticket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDTO {
    //用户名
    private String userName;
    //用户密码
    private String userPassword;
    //用户邮箱
    private String userEmail;
}
