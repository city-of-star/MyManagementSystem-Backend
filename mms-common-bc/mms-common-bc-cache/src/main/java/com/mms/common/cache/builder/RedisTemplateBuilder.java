package com.mms.common.cache.builder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mms.common.cache.utils.RedisUtils;
import com.mms.common.core.utils.JacksonObjectMapperUtils;
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
     * 专用于 Redis 序列化的 ObjectMapper，支持 Java 8 时间类型
     */
    private static final ObjectMapper REDIS_OBJECT_MAPPER = JacksonObjectMapperUtils.createRedisObjectMapper();

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
        // 设置value序列化方式为JSON（支持 Java 8 时间类型）
        GenericJackson2JsonRedisSerializer jsonSerializer =
                new GenericJackson2JsonRedisSerializer(REDIS_OBJECT_MAPPER);
        redisTemplate.setValueSerializer(jsonSerializer);
        redisTemplate.setHashValueSerializer(jsonSerializer);
        redisTemplate.afterPropertiesSet();
        // 注入到RedisUtils工具类
        RedisUtils.setRedisTemplate(redisTemplate);
        return redisTemplate;
    }
}