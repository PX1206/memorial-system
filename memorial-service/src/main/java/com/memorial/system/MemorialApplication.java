package com.memorial.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.memorial.common", "com.memorial.system"})
@EnableScheduling
public class MemorialApplication {

    public static void main(String[] args) {
        SpringApplication.run(com.memorial.system.MemorialApplication.class, args);
    }

}
