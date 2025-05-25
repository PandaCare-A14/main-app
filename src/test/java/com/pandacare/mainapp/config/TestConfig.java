package com.pandacare.mainapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.UUID;

@Configuration
@Profile("test")
public class TestConfig {

    @Bean
    public TestIdGenerator testIdGenerator() {
        return new TestIdGenerator();
    }

    public static class TestIdGenerator {
        public UUID generateId() {
            return UUID.randomUUID();
        }
    }
}