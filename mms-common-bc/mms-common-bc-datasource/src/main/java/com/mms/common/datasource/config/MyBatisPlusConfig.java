package com.mms.common.datasource.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 实现功能【MyBatis Plus配置类】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-01-07 10:37:27
 */
@Configuration
public class MyBatisPlusConfig {

    /**
     * MyBatis Plus拦截器配置
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    /**
     * SQL日志拦截器
     * MyBatis Plus会自动扫描所有实现了Interceptor接口的Bean并注册
     */
    @Bean
    public Interceptor sqlLogInterceptor() {
        return new SqlLogInterceptor();
    }
}

