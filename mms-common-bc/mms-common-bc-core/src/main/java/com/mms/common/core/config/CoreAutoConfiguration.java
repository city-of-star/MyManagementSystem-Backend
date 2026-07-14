package com.mms.common.core.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mms.common.core.listeners.ApplicationStartupLogger;
import com.mms.common.core.utils.JacksonObjectMapperUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 实现功能【核心组件自动装配配置】
 * <p>
 * 提供应用启动成功日志记录工具
 * <p>
 *
 * @author li.hongyu
 * @date 2026-03-03 14:45:04
 */
@Configuration
public class CoreAutoConfiguration {

    /**
     * 通用 ObjectMapper
     */
    @Bean(name = JacksonObjectMapperUtils.COMMON_OBJECT_MAPPER_BEAN_NAME)
    @ConditionalOnMissingBean(name = JacksonObjectMapperUtils.COMMON_OBJECT_MAPPER_BEAN_NAME)
    public ObjectMapper commonObjectMapper() {
        return JacksonObjectMapperUtils.createCommonObjectMapper();
    }

    /**
     * 创建 ApplicationStartupLogger Bean
     */
    @Bean
    @ConditionalOnMissingBean
    public ApplicationStartupLogger applicationStartupLogger() {
        return new ApplicationStartupLogger();
    }
}

