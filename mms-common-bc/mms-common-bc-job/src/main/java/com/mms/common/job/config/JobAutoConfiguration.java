package com.mms.common.job.config;

import com.mms.common.job.JobHandler;
import com.mms.common.job.JobHandlerRegistry;
import com.mms.common.job.execute.JobExecuteExecutor;
import com.mms.common.job.mq.JobExecuteMqListener;
import com.mms.common.job.web.JobValidateController;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;

/**
 * 实现功能【定时任务组件自动装配配置】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-03-03 15:25:43
 */
@Configuration
@Import(JobExecuteMqListener.class)
public class JobAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public JobExecuteExecutor jobExecuteExecutor(JobHandlerRegistry jobHandlerRegistry) {
        return new JobExecuteExecutor(jobHandlerRegistry);
    }

    @Bean
    @ConditionalOnMissingBean(JobValidateController.class)
    public JobValidateController jobValidateController(JobExecuteExecutor jobExecuteExecutor) {
        return new JobValidateController(jobExecuteExecutor);
    }

    @Bean
    @ConditionalOnMissingBean
    public JobHandlerRegistry jobHandlerRegistry(List<JobHandler> handlers) {
        return new JobHandlerRegistry(handlers);
    }
}
