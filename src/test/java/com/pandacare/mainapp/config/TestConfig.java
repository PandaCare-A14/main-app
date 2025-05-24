package com.pandacare.mainapp.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * Test configuration to provide mock beans and override configurations for tests
 */
@TestConfiguration
@Profile("test")
public class TestConfig {
    
    /**
     * Provides a mock authentication base URL for tests
     */
    @Bean
    @Primary
    public String testAuthBaseUrl() {
        return "http://localhost:8081";
    }
}
