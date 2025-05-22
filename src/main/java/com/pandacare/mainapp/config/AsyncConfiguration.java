package com.pandacare.mainapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Configuration class for enabling asynchronous execution
 * This is used by our Observer pattern to update doctor statistics asynchronously
 */
@Configuration
@EnableAsync
public class AsyncConfiguration {
    // No additional configuration needed, just enabling the @Async annotation
}