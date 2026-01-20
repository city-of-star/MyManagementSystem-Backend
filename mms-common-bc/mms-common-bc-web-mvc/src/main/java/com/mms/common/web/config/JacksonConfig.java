package com.mms.common.web.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * 实现功能【Jackson 配置类】
 * <p>
 * 统一配置 Jackson 的日期时间序列化/反序列化格式
 * 用于处理 LocalDateTime、LocalDate、LocalTime、旧版java.util.Date 类型的日期格式转换
 * </p>
 *
 * @author li.hongyu
 * @date 2026-01-20 14:11:28
 */
@Configuration
public class JacksonConfig {

    /**
     * 统一的日期时间格式（年月日 时分秒）
     */
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 统一的日期格式（年月日）
     */
    private static final String DATE_PATTERN = "yyyy-MM-dd";

    /**
     * 统一的时间格式（时分秒）
     */
    private static final String TIME_PATTERN = "HH:mm:ss";

    /**
     * 日期时间格式化器（LocalDateTime）
     */
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

    /**
     * 日期格式化器（LocalDate）
     */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);

    /**
     * 时间格式化器（LocalTime）
     */
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_PATTERN);

    /**
     * 自定义 Jackson ObjectMapper 构建器
     * <p>
     * - LocalDateTime：yyyy-MM-dd HH:mm:ss（年月日 时分秒）
     * - LocalDate：yyyy-MM-dd（年月日）
     * - LocalTime：HH:mm:ss（时分秒）
     * - java.util.Date：yyyy-MM-dd HH:mm:ss（旧类型统一）
     * </p>
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> {
            // 创建 JavaTimeModule
            JavaTimeModule javaTimeModule = new JavaTimeModule();

            // LocalDateTime：序列化器（对象 -> JSON） & 反序列化器（JSON -> 对象）
            javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DATE_TIME_FORMATTER));
            javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DATE_TIME_FORMATTER));

            // LocalDate：序列化器 & 反序列化器
            javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DATE_FORMATTER));
            javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DATE_FORMATTER));

            // LocalTime：序列化器 & 反序列化器
            javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(TIME_FORMATTER));
            javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(TIME_FORMATTER));

            // 注册模块
            builder.modules(javaTimeModule);

            // 禁用将日期序列化为时间戳
            builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            // 旧的 java.util.Date 统一格式（主要用于非 java.time 的 Date 字段）
            builder.simpleDateFormat(DATE_TIME_PATTERN);

            // 忽略未知属性（提高兼容性）
            builder.featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        };
    }
}
