package com.mms.common.cache.builder;

import com.mms.common.cache.utils.RedisUtils;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 实现功能【RedisTemplate构建器】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu 15:37:28
 * @date 2026-01-07 10:37:27
 */
public class RedisTemplateBuilder {

    /**
     * 构建RedisTemplate
     * 使用String序列化key，JSON序列化value
     */
    public RedisTemplate<String, Object> buildRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        // 设置key序列化方式为String
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        // 设置value序列化方式为JSON
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.afterPropertiesSet();
        // 注入到RedisUtils工具类
        RedisUtils.setRedisTemplate(redisTemplate);
        return redisTemplate;
    }
}