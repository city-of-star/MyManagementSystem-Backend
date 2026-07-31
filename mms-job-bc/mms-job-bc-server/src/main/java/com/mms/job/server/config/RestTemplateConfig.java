package com.mms.job.server.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * 实现功能【LoadBalanced RestTemplate 配置】
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
     * 供 JobExecuteService 等通过服务名调用其他服务使用。
     * 读超时放宽，避免 mysqldump + git push 等长任务被提前掐断。
     */
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofMinutes(15))
                .build();
    }
}