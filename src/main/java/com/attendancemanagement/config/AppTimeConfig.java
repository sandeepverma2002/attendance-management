package com.attendancemanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.ZoneId;

@Configuration
public class AppTimeConfig {
    @Bean
    public ZoneId appZoneId() {
        // Change if needed, or make configurable via properties
        return ZoneId.of("Asia/Kolkata");
    }
}