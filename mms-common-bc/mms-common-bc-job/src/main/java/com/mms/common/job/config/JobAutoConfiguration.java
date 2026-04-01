package com.mms.common.job.config;

import com.mms.common.job.JobHandler;
import com.mms.common.job.JobHandlerRegistry;
import com.mms.common.job.web.JobExecuteController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 实现功能【定时任务组件自动装配配置】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-03-03 15:25:43
 */
@Slf4j
@Configuration
public class JobAutoConfiguration {

    /**
     * 创建 定时任务执行入口 Bean
     */
    @Bean
    @ConditionalOnMissingBean(JobExecuteController.class)
    public JobExecuteController jobExecuteController(JobHandlerRegistry jobHandlerRegistry) {
        log.info("【定时任务执行入口】加载成功");
        return new JobExecuteController(jobHandlerRegistry);
    }

    /**
     * 创建 定时任务处理器注册中心 Bean
     */
    @Bean
    @ConditionalOnMissingBean
    public JobHandlerRegistry jobHandlerRegistry(List<JobHandler> handlers) {
        log.info("【定时任务处理器注册中心】加载成功");
        return new JobHandlerRegistry(handlers);
    }
}