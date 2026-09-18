package com.lcy.yozoraticket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class YozoraTicketApplication {

    public static void main(String[] args) {
        SpringApplication.run(YozoraTicketApplication.class, args);
    }

}
