package com.mms.common.datasource.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 实现功能【数据源组件自动装配配置】
 * <p>
 * 提供以下功能
 * - MyBatis-Plus拦截器配置配置
 * - SQL日志拦截器
 * - MyBatis-Plus自动填充处理器
 * <p>
 *
 * @author li.hongyu
 * @date 2026-03-03 14:53:11
 */
@Configuration
public class DatasourceAutoConfiguration {

    private final static MyBatisPlusConfig mybatisPlusConfig = new MyBatisPlusConfig();

    /**
     * 创建 MyBatis-Plus拦截器配置 Bean
     */
    @Bean
    @ConditionalOnMissingBean(MybatisPlusInterceptor.class)
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        return mybatisPlusConfig.mybatisPlusInterceptor();
    }

    /**
     * 创建 SQL日志拦截器 Bean
     */
    @Bean
    @ConditionalOnMissingBean(SqlOneLineLogInterceptor.class)
    public Interceptor sqlLogInterceptor() {
        return mybatisPlusConfig.sqlLogInterceptor();
    }

    /**
     * 创建 MyBatis-Plus自动填充处理器 Bean
     */
    @Bean
    @ConditionalOnMissingBean
    public MyBatisPlusMetaObjectHandler myBatisPlusMetaObjectHandler() {
        return new MyBatisPlusMetaObjectHandler();
    }
}