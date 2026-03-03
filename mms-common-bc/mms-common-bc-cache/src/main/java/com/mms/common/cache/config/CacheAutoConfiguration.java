package com.mms.common.cache.config;

import com.mms.common.cache.builder.RedisTemplateBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 实现功能【缓存模块自动装配配置】
 * <p>
 * 提供 RedisTemplate
 * </p>
 *
 * @author li.hongyu
 * @date 2026-03-03 10:25:14
 */
@Configuration
public class CacheAutoConfiguration {

    /**
     * 创建 RedisTemplate Bean
     */
    @Bean
    @ConditionalOnMissingBean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        return new RedisTemplateBuilder().buildRedisTemplate(connectionFactory);
    }
}

