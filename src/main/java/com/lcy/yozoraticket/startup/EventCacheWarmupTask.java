package com.lcy.yozoraticket.startup;

import com.lcy.yozoraticket.service.EventCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class EventCacheWarmupTask implements ApplicationRunner {

    private final EventCacheService eventCacheService;

    /**
     * 项目启动演出时演出预热
     * @param args
     */
    @Override
    public void run(ApplicationArguments args) {
        eventCacheService.warmupSellingEvents();
    }
}
