package com.lcy.yozoraticket.service.impl;

import com.lcy.yozoraticket.constant.RedisConstant;
import com.lcy.yozoraticket.context.BaseContext;
import com.lcy.yozoraticket.dto.RegisterDTO;
import com.lcy.yozoraticket.dto.UserLoginDTO;
import com.lcy.yozoraticket.mapper.LoginMapper;
import com.lcy.yozoraticket.service.LoginService;
import com.lcy.yozoraticket.utils.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final LoginMapper loginMapper;
    private final JWTUtil jwtUtil;
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 用户登录
     * TODO 未处理账号密码错误或者违规的情况
     * @param userLoginDTO
     * @return
     */
    @Override
    public String login(UserLoginDTO userLoginDTO) {
        //查询当前用户id
        Long userId = loginMapper.userLogin(userLoginDTO);
        //创建登录session
        String sessionId = UUID.randomUUID().toString();
        //生成jwt的token
        String token = jwtUtil.createToken(userId,sessionId);
        //redis创建当前登录用户session
        stringRedisTemplate.opsForValue().set(RedisConstant.LOGIN_SESSION_KEY + userId,sessionId,7, TimeUnit.DAYS);

        return token;
    }

    /**
     * 用户注册
     * @param registerDTO
     */
    @Override
    public void register(RegisterDTO registerDTO) {
        //将注册用户的信息插入数据库
        loginMapper.register(registerDTO);
    }
}
