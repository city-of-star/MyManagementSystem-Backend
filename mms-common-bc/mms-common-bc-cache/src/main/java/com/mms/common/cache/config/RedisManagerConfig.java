package com.mms.common.cache.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mms.common.cache.constants.CacheKeyPrefix;
import com.mms.common.cache.constants.CacheTtl;
import com.mms.common.core.utils.JacksonObjectMapperUtils;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 实现功能【Redis缓存管理器配置】
 * <p>
 * 提供RedisCacheManager，支持Spring Cache注解（@Cacheable、@CacheEvict、@CachePut）
 * 支持按cacheName配置不同的TTL
 * </p>
 *
 * @author li.hongyu
 * @date 2026-03-05 09:37:59
 */
public class RedisManagerConfig {

    /**
     * 创建RedisCacheManager Bean
     * 配置默认TTL和按cacheName的TTL映射
     */
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        ObjectMapper objectMapper = JacksonObjectMapperUtils.createRedisObjectMapper();
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);
        // 默认缓存配置：1小时TTL，使用JSON序列化
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(CacheTtl.LONG_SECONDS))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer))
                .disableCachingNullValues(); // 不缓存null值
        // 按cacheName配置不同的TTL
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        // 基础数据服务相关缓存
        cacheConfigurations.put(CacheKeyPrefix.BASE + "dict", defaultConfig);
        return RedisCacheManager.builder(Objects.requireNonNull(connectionFactory))
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware() // 支持事务
                .build();
    }
}

