package com.mms.common.cache.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mms.common.core.utils.JacksonObjectMapperUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * 实现功能【Redis操作工具类】
 * <p>
 * 封装常用的Redis操作方法，简化业务代码
 * </p>
 *
 * @author li.hongyu
 * @date 2026-03-02 09:48:24
 */
public class RedisUtils {

    private static RedisTemplate<String, Object> redisTemplate;

    /**
     * 专用于 Redis 序列化的 ObjectMapper，支持 Java 8 时间类型
     */
    private static final ObjectMapper OBJECT_MAPPER = JacksonObjectMapperUtils.createRedisObjectMapper();

    public static void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        RedisUtils.redisTemplate = redisTemplate;
    }

    /**
     * 设置缓存
     *
     * @param key   键
     * @param value 值
     */
    public static void set(String key, Object value) {
        if (!StringUtils.hasText(key) || value == null) {
            return;
        }
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置缓存（带过期时间）
     *
     * @param key      键
     * @param value    值
     * @param timeout  过期时间
     * @param timeUnit 时间单位
     */
    public static void set(String key, Object value, long timeout, TimeUnit timeUnit) {
        if (!StringUtils.hasText(key) || value == null) {
            return;
        }
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * 设置缓存（带过期时间，单位：秒）
     *
     * @param key     键
     * @param value   值
     * @param timeout 过期时间（秒）
     */
    public static void set(String key, Object value, long timeout) {
        set(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * 获取缓存（简单类型或对象）
     *
     * @param key   键
     * @param clazz 类型
     * @param <T>   泛型
     * @return 值
     */
    public static <T> T get(String key, Class<T> clazz) {
        if (!StringUtils.hasText(key)) {
            return null;
        }
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }
        if (clazz.isInstance(value)) {
            return clazz.cast(value);
        }
        // 兜底：Redis 反序列化可能得到 LinkedHashMap（未携带类型信息时）
        return OBJECT_MAPPER.convertValue(value, clazz);
    }

    /**
     * 获取缓存（支持泛型的复杂类型）
     *
     * @param key     键
     * @param typeRef 类型引用（带泛型）
     * @param <T>     目标类型
     * @return 值
     */
    public static <T> T get(String key, TypeReference<T> typeRef) {
        if (!StringUtils.hasText(key)) {
            return null;
        }
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }
        return OBJECT_MAPPER.convertValue(value, typeRef);
    }

    /**
     * 删除缓存
     *
     * @param key 键
     * @return 是否删除成功
     */
    public static Boolean delete(String key) {
        if (!StringUtils.hasText(key)) {
            return false;
        }
        return redisTemplate.delete(key);
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return 是否存在
     */
    public static Boolean exists(String key) {
        if (!StringUtils.hasText(key)) {
            return false;
        }
        return redisTemplate.hasKey(key);
    }

    /**
     * 设置过期时间
     *
     * @param key      键
     * @param timeout  过期时间
     * @param timeUnit 时间单位
     * @return 是否设置成功
     */
    public static Boolean expire(String key, long timeout, TimeUnit timeUnit) {
        if (!StringUtils.hasText(key)) {
            return false;
        }
        return redisTemplate.expire(key, timeout, timeUnit);
    }

    /**
     * 设置过期时间（单位：秒）
     *
     * @param key     键
     * @param timeout 过期时间（秒）
     * @return 是否设置成功
     */
    public static Boolean expire(String key, long timeout) {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 获取过期时间
     *
     * @param key 键
     * @return 过期时间（秒），-1表示永久，-2表示key不存在
     */
    public static Long getExpire(String key) {
        if (!StringUtils.hasText(key)) {
            return -2L;
        }
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    // ==================== 自增自减操作 ====================

    /**
     * 自增（+1）
     *
     * @param key 键
     * @return 自增后的值
     */
    public static Long increment(String key) {
        if (!StringUtils.hasText(key)) {
            return 0L;
        }
        return redisTemplate.opsForValue().increment(key);
    }

    /**
     * 自增（指定步长）
     *
     * @param key   键
     * @param delta 步长（增量）
     * @return 自增后的值
     */
    public static Long increment(String key, long delta) {
        if (!StringUtils.hasText(key)) {
            return 0L;
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 自减（-1）
     *
     * @param key 键
     * @return 自减后的值
     */
    public static Long decrement(String key) {
        if (!StringUtils.hasText(key)) {
            return 0L;
        }
        return redisTemplate.opsForValue().decrement(key);
    }

    /**
     * 自减（指定步长）
     *
     * @param key   键
     * @param delta 步长（减量）
     * @return 自减后的值
     */
    public static Long decrement(String key, long delta) {
        if (!StringUtils.hasText(key)) {
            return 0L;
        }
        return redisTemplate.opsForValue().decrement(key, delta);
    }

    // ==================== Hash操作（不常用） ====================

    /**
     * Hash设置
     *
     * @param key   键
     * @param field 字段
     * @param value 值
     */
    public static void hSet(String key, String field, Object value) {
        if (!StringUtils.hasText(key) || !StringUtils.hasText(field) || value == null) {
            return;
        }
        redisTemplate.opsForHash().put(key, field, value);
    }

    /**
     * Hash获取
     *
     * @param key   键
     * @param field 字段
     * @return 值
     */
    public static Object hGet(String key, String field) {
        if (!StringUtils.hasText(key) || !StringUtils.hasText(field)) {
            return null;
        }
        return redisTemplate.opsForHash().get(key, field);
    }

    /**
     * Hash删除字段
     *
     * @param key   键
     * @param field 字段
     * @return 删除的字段数量
     */
    public static Long hDelete(String key, String field) {
        if (!StringUtils.hasText(key) || !StringUtils.hasText(field)) {
            return 0L;
        }
        return redisTemplate.opsForHash().delete(key, field);
    }

    /**
     * 判断Hash中是否存在字段
     *
     * @param key   键
     * @param field 字段
     * @return 是否存在
     */
    public static Boolean hExists(String key, String field) {
        if (!StringUtils.hasText(key) || !StringUtils.hasText(field)) {
            return false;
        }
        return redisTemplate.opsForHash().hasKey(key, field);
    }

    /**
     * Hash自增
     *
     * @param key   键
     * @param field 字段
     * @param delta 增量
     * @return 自增后的值
     */
    public static Long hIncrement(String key, String field, long delta) {
        if (!StringUtils.hasText(key) || !StringUtils.hasText(field)) {
            return 0L;
        }
        return redisTemplate.opsForHash().increment(key, field, delta);
    }

    /**
     * Hash自减
     *
     * @param key   键
     * @param field 字段
     * @param delta 减量
     * @return 自减后的值
     */
    public static Long hDecrement(String key, String field, long delta) {
        return hIncrement(key, field, -delta);
    }
}
