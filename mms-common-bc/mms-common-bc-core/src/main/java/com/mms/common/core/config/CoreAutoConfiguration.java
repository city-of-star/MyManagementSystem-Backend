package com.mms.common.core.config;

import com.mms.common.core.listeners.ApplicationStartupLogger;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Configuration
public class CoreAutoConfiguration {

    /**
     * 创建 ApplicationStartupLogger Bean
     */
    @Bean
    @ConditionalOnMissingBean
    public ApplicationStartupLogger applicationStartupLogger() {
        log.info("【应用启动日志记录器】加载成功");
        return new ApplicationStartupLogger();
    }
}