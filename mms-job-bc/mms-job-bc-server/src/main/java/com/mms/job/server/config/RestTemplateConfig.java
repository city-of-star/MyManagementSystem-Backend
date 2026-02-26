package com.mms.job.server.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 实现功能【】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-02-26 21:10:00
 */
@Configuration
public class RestTemplateConfig {

    /**
     * 供 JobExecuteService 等通过服务名调用其他服务使用：
     * 例如 http://base/internal/job/execute
     */
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}