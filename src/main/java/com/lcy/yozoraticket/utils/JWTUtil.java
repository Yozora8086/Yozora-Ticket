package com.lcy.yozoraticket.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class JWTUtil {

    @Value("${jwt.secretKey}")
    private String jwtKey;

    public String createToken(Long userId,String sessionId){
        SecretKey secretKey = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(jwtKey)
        );
        Map<String, Object> claims = new HashMap<>();

        claims.put("userId",userId);
        claims.put("sessionId",sessionId);

        JwtBuilder jwtBuilder = Jwts.builder()
                .signWith(secretKey,Jwts.SIG.HS256)
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60 * 1000 * 60 * 24 * 24));

        String token = jwtBuilder.compact();
        return token;
    }

    public Claims checkToken(String token) {
        SecretKey secretKey = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(jwtKey)
        );

        Jws<Claims> jws = Jwts.parser()
                .verifyWith(secretKey)     // 使用同一个密钥验签
                .build()                   // 创建解析器
                .parseSignedClaims(token); // 校验并解析 token

        return jws.getPayload();           // 返回里面的数据

    }
}
