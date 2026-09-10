package com.lcy.yozoraticket.interceptor;

import com.lcy.yozoraticket.constant.RedisConstant;
import com.lcy.yozoraticket.context.BaseContext;
import com.lcy.yozoraticket.exception.LoginSessionErrorException;
import com.lcy.yozoraticket.exception.NoLoginException;
import com.lcy.yozoraticket.utils.JWTUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {

    private final JWTUtil jwtUtil;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //如果 handler 不是一个 HandlerMethod（即不是一个 Controller 方法），就返回 true（或者不拦截）。
        if (!(handler instanceof HandlerMethod)){
            return true;
        }

        //获取请求头
        String token = request.getHeader("Authorization");

        //判断token是否为空
        if (token == null || token.isBlank()){
            throw new NoLoginException();
        }

        //token校验
        Claims claims = jwtUtil.checkToken(token);

        Object userObj = claims.get("userId");
        //获取session
        Object sessionObj = claims.get("sessionId");

        Long userId = ((Number) userObj).longValue();
        String sessionId = String.valueOf(sessionObj);

        //将当前用户id存入线程
        BaseContext.setCurrentId(userId);

        //获取当前用户最新的session
        String sessionIdNow = stringRedisTemplate.opsForValue().get(RedisConstant.LOGIN_SESSION_KEY + userId);

        //判断是否相等，不等则代表当前账号已在其他地方登录
        if (!sessionIdNow.equals(sessionId)){
            throw new LoginSessionErrorException();
        }


        return true;

    }
}
