package com.lcy.yozoraticket.config;

import com.lcy.yozoraticket.vo.EventVO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;

@Configuration
public class RedisLuaConfig {
    /**
     * 注册购票lua脚本
     * @return
     */
    @Bean
    public DefaultRedisScript<String> ticketBuyScript(){

        DefaultRedisScript<String> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("/lua/TicketBuy.lua"));
        script.setResultType(String.class);

        return script;
    }


}
