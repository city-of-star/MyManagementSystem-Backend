package com.mms.common.cache.config;

import com.mms.common.cache.builder.RedisTemplateBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 实现功能【缓存模块自动装配配置】
 * <p>
 * 提供以下功能：
 * 1. RedisTemplate：Redis操作模板
 * 2. KeyGenerator：统一的缓存Key生成器
 * 3. CacheManager：Spring Cache缓存管理器（支持注解缓存）
 * </p>
 *
 * @author li.hongyu
 * @date 2026-03-03 10:25:14
 */
@EnableCaching
@Configuration
public class CacheAutoConfiguration {

    private final KeyGeneratorConfig keyGeneratorConfig = new KeyGeneratorConfig();

    /**
     * 创建 RedisTemplate Bean
     */
    @Bean
    @ConditionalOnMissingBean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        return new RedisTemplateBuilder().buildRedisTemplate(connectionFactory);
    }

    /**
     * 创建 用户中心服务的缓存Key生成器 Bean
     */
    @Bean(name = "userKeyGenerator")
    @ConditionalOnMissingBean(name = "userKeyGenerator")
    public KeyGenerator userKeyGenerator() {
        return keyGeneratorConfig.userKeyGenerator();
    }

    /**
     * 创建 基础数据服务的缓存Key生成器 Bean
     */
    @Bean(name = "baseKeyGenerator")
    @ConditionalOnMissingBean(name = "baseKeyGenerator")
    public KeyGenerator baseKeyGenerator() {
        return keyGeneratorConfig.baseKeyGenerator();
    }

    /**
     * 创建 网关服务的缓存Key生成器 Bean
     */
    @Bean(name = "gatewayKeyGenerator")
    @ConditionalOnMissingBean(name = "gatewayKeyGenerator")
    public KeyGenerator gatewayKeyGenerator() {
        return keyGeneratorConfig.gatewayKeyGenerator();
    }

    /**
     * 创建 Redis缓存管理器配置 Bean
     */
    @Bean
    @ConditionalOnMissingBean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        return new RedisManagerConfig().cacheManager(connectionFactory);
    }
}
