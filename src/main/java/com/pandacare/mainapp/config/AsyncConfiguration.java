package com.pandacare.mainapp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Configuration for asynchronous processing
 */
@Configuration
@EnableAsync
public class AsyncConfiguration {

    private static final Logger log = LoggerFactory.getLogger(AsyncConfiguration.class);

    @Bean(name = "ratingTaskExecutor")
    public Executor ratingTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // Core pool size - number of threads to keep alive
        executor.setCorePoolSize(5);

        // Maximum pool size - maximum number of threads
        executor.setMaxPoolSize(20);

        // Queue capacity - number of tasks to queue before creating new threads
        executor.setQueueCapacity(100);

        // Thread name prefix for easy identification in logs
        executor.setThreadNamePrefix("RatingAsync-");

        // Keep alive time for idle threads above core pool size
        executor.setKeepAliveSeconds(60);

        // Allow core threads to timeout
        executor.setAllowCoreThreadTimeOut(true);

        // Wait for tasks to complete on shutdown
        executor.setWaitForTasksToCompleteOnShutdown(true);

        // Maximum time to wait for shutdown
        executor.setAwaitTerminationSeconds(30);

        // Rejection policy when thread pool and queue are full
        executor.setRejectedExecutionHandler(new CustomRejectedExecutionHandler());

        executor.initialize();

        log.info("Rating task executor initialized with core pool size: {}, max pool size: {}, queue capacity: {}",
                executor.getCorePoolSize(), executor.getMaxPoolSize(), executor.getQueueCapacity());

        return executor;
    }

    @Bean(name = "generalTaskExecutor")
    public Executor generalTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("GeneralAsync-");
        executor.setKeepAliveSeconds(60);
        executor.setAllowCoreThreadTimeOut(true);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.setRejectedExecutionHandler(new CustomRejectedExecutionHandler());

        executor.initialize();

        log.info("General task executor initialized with core pool size: {}, max pool size: {}, queue capacity: {}",
                executor.getCorePoolSize(), executor.getMaxPoolSize(), executor.getQueueCapacity());

        return executor;
    }

    /**
     * Custom rejection handler that logs rejected tasks
     */
    private static class CustomRejectedExecutionHandler implements RejectedExecutionHandler {
        private static final Logger log = LoggerFactory.getLogger(CustomRejectedExecutionHandler.class);

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            log.warn("Task rejected from executor: {}. Active threads: {}, Pool size: {}, Queue size: {}",
                    executor.toString(),
                    executor.getActiveCount(),
                    executor.getPoolSize(),
                    executor.getQueue().size());

            // Try to run in the caller's thread as fallback
            try {
                if (!executor.isShutdown()) {
                    r.run();
                    log.info("Rejected task executed in caller's thread");
                }
            } catch (Exception e) {
                log.error("Failed to execute rejected task in caller's thread: {}", e.getMessage(), e);
            }
        }
    }
}