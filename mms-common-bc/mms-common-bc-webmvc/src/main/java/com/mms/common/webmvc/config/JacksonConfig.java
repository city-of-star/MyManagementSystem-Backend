package com.mms.common.webmvc.config;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.mms.common.core.utils.DateUtils;
import com.mms.common.core.utils.JacksonObjectMapperUtils;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
     * 自定义 Jackson ObjectMapper 构建器
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> {
            // 使用统一核心工具类创建的 JavaTimeModule
            builder.modules(JacksonObjectMapperUtils.createJavaTimeModule());
            // 禁用将日期序列化为时间戳
            builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            // 旧的 java.util.Date 统一格式（主要用于非 java.time 的 Date 字段）
            builder.simpleDateFormat(DateUtils.PATTERN_DATETIME);
            // Long/long 统一序列化为字符串，避免前端 JS 精度丢失（雪花 ID 等场景）
            builder.serializerByType(Long.class, ToStringSerializer.instance);
            builder.serializerByType(Long.TYPE, ToStringSerializer.instance);
            // MyBatis-Plus Page：分页参数输出为 int number，避免受全局 Long->String 影响
            builder.serializerByType(Page.class, new MybatisPlusPageSerializer());
            // 忽略未知属性（提高兼容性）
            builder.featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        };
    }
}
