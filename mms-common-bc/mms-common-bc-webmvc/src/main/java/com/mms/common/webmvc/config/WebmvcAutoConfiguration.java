package com.mms.common.webmvc.config;

import com.mms.common.webmvc.advice.GlobalExceptionAdvice;
import com.mms.common.webmvc.filter.TraceIdMvcFilter;
import com.mms.common.webmvc.file.FileDownloadService;
import com.mms.common.webmvc.file.impl.FileDownloadServiceImpl;
import com.mms.common.webmvc.swagger.SwaggerConfig;
import io.swagger.v3.oas.models.OpenAPI;
import com.fasterxml.jackson.databind.Module;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 实现功能【Web MVC组件自动装配配置】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-03-03 15:34:12
 */
@Slf4j
@Configuration
public class WebmvcAutoConfiguration {

    /**
     * 创建 全局异常捕获处理器 Bean
     */
    @Bean
    @ConditionalOnMissingBean(GlobalExceptionAdvice.class)
    public GlobalExceptionAdvice globalExceptionAdvice() {
        GlobalExceptionAdvice advice = new GlobalExceptionAdvice();
        log.info("【全局异常捕获处理器】加载成功");
        return advice;
    }

    /**
     * 创建 Jackson HttpMessageConverter Bean
     */
    @Bean
    public WebMvcConfigurer jacksonWebMvcConfigurer(List<Module> jacksonModules) {
        WebMvcConfigurer webMvcConfigurer = new JacksonConfig().jacksonWebMvcConfigurer(jacksonModules);
        log.info("【Jackson序列化配置】加载成功");
        return webMvcConfigurer;
    }

    /**
     * 创建 TraceId过滤器 Bean
     */
    @Bean
    @ConditionalOnMissingBean
    public TraceIdMvcFilter traceIdMvcFilter() {
        TraceIdMvcFilter traceIdMvcFilter = new TraceIdMvcFilter();
        log.info("【TraceId过滤器】加载成功");
        return traceIdMvcFilter;
    }

    /**
     * 创建 Swagger配置 Bean
     */
    @Bean
    @ConditionalOnMissingBean(SwaggerConfig.class)
    public SwaggerConfig swaggerConfig() {
        SwaggerConfig swaggerConfig = new SwaggerConfig();
        log.info("【Swagger基础配置】加载成功");
        return swaggerConfig;
    }

    /**
     * 创建 OpenAPI配置 Bean
     */
    @Bean
    @ConditionalOnMissingBean(OpenAPI.class)
    public OpenAPI customOpenAPI(SwaggerConfig swaggerConfig) {
        OpenAPI openAPI = swaggerConfig.customOpenAPI();
        log.info("【Swagger OpenAPI配置】加载成功");
        return openAPI;
    }

    /**
     * 创建 文件下载服务 Bean
     */
    @Bean
    @ConditionalOnMissingBean(FileDownloadService.class)
    public FileDownloadService fileDownloadService() {
        return new FileDownloadServiceImpl();
    }
}