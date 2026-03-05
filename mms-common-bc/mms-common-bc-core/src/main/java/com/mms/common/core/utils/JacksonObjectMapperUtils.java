package com.mms.common.core.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 实现功能【统一的 Jackson ObjectMapper 工具类】
 * <p>
 * 1. 提供统一的 Java 8 时间类型配置（LocalDateTime / LocalDate / LocalTime）
 * 2. 统一日期时间格式：参考 {@link DateUtils}
 * 3. 统一常用特性（是否写时间戳、是否忽略未知字段等）
 * <p>
 *
 * @author li.hongyu
 * @date 2026-03-05 10:57:43
 */
public final class JacksonObjectMapperUtils {

    /**
     * 创建统一配置的 JavaTimeModule
     * <p>
     * - LocalDateTime：yyyy-MM-dd HH:mm:ss
     * - LocalDate：yyyy-MM-dd
     * - LocalTime：HH:mm:ss
     */
    public static JavaTimeModule createJavaTimeModule() {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        // LocalDateTime：序列化 & 反序列化
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateUtils.DATETIME_FORMATTER));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateUtils.DATETIME_FORMATTER));
        // LocalDate：序列化 & 反序列化
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateUtils.DATE_FORMATTER));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateUtils.DATE_FORMATTER));
        // LocalTime：序列化 & 反序列化
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateUtils.TIME_FORMATTER));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateUtils.TIME_FORMATTER));
        return javaTimeModule;
    }

    /**
     * 创建一个“通用”的 ObjectMapper 配置：
     * <p>
     * - 支持 Java 8 时间类型（使用 {@link #createJavaTimeModule()}）
     * - 关闭写时间戳（使用可读字符串）
     * - 统一旧版 java.util.Date 格式
     * - 反序列化时忽略未知字段
     *
     * @return 按项目约定配置好的 ObjectMapper
     */
    public static ObjectMapper createCommonObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // Java 8 时间模块（LocalDateTime/LocalDate/LocalTime）
        objectMapper.registerModule(createJavaTimeModule());
        // 不将日期写为时间戳，而是使用字符串格式
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 旧版 java.util.Date 的统一格式
        objectMapper.setDateFormat(new SimpleDateFormat(DateUtils.PATTERN_DATETIME));
        // 反序列化时忽略未知字段，提高兼容性
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return objectMapper;
    }

    /**
     * 为 Redis 场景创建 ObjectMapper
     *
     * @return 用于 Redis 序列化的 ObjectMapper
     */
    public static ObjectMapper createRedisObjectMapper() {
        ObjectMapper objectMapper = createCommonObjectMapper();
        // Redis 场景需要保留类型信息，否则反序列化时可能只得到 LinkedHashMap，导致强转异常
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );
        return objectMapper;
    }

    private JacksonObjectMapperUtils() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }
}