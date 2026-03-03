package com.mms.common.threadpool.config;

import com.mms.common.threadpool.builder.ThreadPoolBuilder;
import com.mms.common.threadpool.properties.ThreadPoolProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 实现功能【线程池自动装配配置】
 * <p>
 * 提供两个线程池：
 * - schedulerTaskExecutor：定时任务线程池
 * - fileTaskExecutor：文件上传/下载线程池
 * <p>
 *
 * @author li.hongyu
 * @date 2026-02-11 16:36:39
 */
@Configuration
@EnableConfigurationProperties(ThreadPoolProperties.class)
public class ThreadPoolAutoConfiguration {

    private final ThreadPoolBuilder builder = new ThreadPoolBuilder();

    /**
     * 定时任务线程池
     */
    @Bean(name = "schedulerTaskExecutor")
    @ConditionalOnMissingBean(name = "schedulerTaskExecutor")
    public ThreadPoolTaskExecutor schedulerTaskExecutor(ThreadPoolProperties properties) {
        return builder.schedulerTaskExecutor(properties);
    }

    /**
     * 文件上传/下载线程池
     */
    @Bean(name = "fileTaskExecutor")
    @ConditionalOnMissingBean(name = "fileTaskExecutor")
    public ThreadPoolTaskExecutor fileTaskExecutor(ThreadPoolProperties properties) {
        return builder.fileTaskExecutor(properties);
    }
}

