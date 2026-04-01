package com.mms.common.security.servlet.config;

import com.mms.common.security.core.properties.GatewaySignatureProperties;
import com.mms.common.security.core.properties.WhitelistProperties;
import com.mms.common.security.servlet.aop.PermissionCheckAspect;
import com.mms.common.security.servlet.feign.FeignHeaderRelayInterceptor;
import com.mms.common.security.servlet.feign.FeignLogger;
import com.mms.common.security.servlet.filter.JwtAuthenticationFilter;
import com.mms.common.security.servlet.filter.UserAuthorityProvider;
import com.mms.common.security.servlet.properties.CookieProperties;
import com.mms.common.security.servlet.service.GatewaySignatureVerificationService;
import com.mms.common.security.servlet.service.ServiceWhitelistService;
import feign.Logger;
import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 实现功能【Servlet 环境安全组件自动装配】
 * <p>
 * 仅在 Servlet Web 应用中生效（业务服务），网关（WebFlux）不会加载。
 * 负责装配：
 * - GatewaySignatureVerificationService
 * - ServiceWhitelistService
 * - JwtAuthenticationFilter（通用 JWT 过滤器）
 * - 默认 SecurityFilterChain（可被业务服务自定义覆盖）
 * - Feign Header Relay（仅在存在 Feign 依赖时）
 * </p>
 *
 * @author li.hongyu
 * @date 2026-03-13 17:00:34
 */
@Slf4j
@Configuration
@ConditionalOnClass(HttpServletRequest.class)
@EnableConfigurationProperties({CookieProperties .class})  // 在此类当中注入配置属性Bean
public class ServletSecurityAutoConfiguration {

    /**
     * 创建 GatewaySignatureVerificationService Bean
     * 需要配置 gateway.signature.public-key 属性
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "gateway.signature", name = "public-key")
    public GatewaySignatureVerificationService gatewaySignatureVerificationService(GatewaySignatureProperties gatewaySignatureProperties) {
        return new GatewaySignatureVerificationService(gatewaySignatureProperties);
    }

    /**
     * 创建 ServiceWhitelistService Bean
     */
    @Bean
    @ConditionalOnMissingBean
    public ServiceWhitelistService whitelistUtils(WhitelistProperties whitelistProperties) {
        return new ServiceWhitelistService(whitelistProperties);
    }

    /**
     * 创建通用 JwtAuthenticationFilter Bean
     * 仅在存在 UserAuthorityProvider、GatewaySignatureVerificationService、ServiceWhitelistService 时创建
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({UserAuthorityProvider.class, GatewaySignatureVerificationService.class, ServiceWhitelistService.class})
    public JwtAuthenticationFilter jwtAuthenticationFilter(UserAuthorityProvider userAuthorityProvider, GatewaySignatureVerificationService gatewaySignatureVerificationService, ServiceWhitelistService serviceWhitelistService) {
        log.info("【JWT过滤器】加载成功");
        return new JwtAuthenticationFilter(userAuthorityProvider, gatewaySignatureVerificationService, serviceWhitelistService);
    }

    /**
     * 创建默认的 SecurityFilterChain
     * 仅在不存在自定义 SecurityFilterChain Bean 时创建
     */
    @Bean
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    @ConditionalOnBean({JwtAuthenticationFilter.class, ServiceWhitelistService.class})
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter, ServiceWhitelistService serviceWhitelistService) throws Exception {
        log.info("【Spring Security配置类】加载成功");
        return SecurityConfig.buildDefaultSecurityFilterChain(http, jwtAuthenticationFilter, serviceWhitelistService);
    }

    /**
     * 注册权限校验切面
     */
    @Bean
    @ConditionalOnMissingBean
    public PermissionCheckAspect permissionCheckAspect() {
        return new PermissionCheckAspect();
    }

    /**
     * Feign 相关 Bean：仅在存在 Feign 依赖时装配
     * 避免未引入 Feign 的服务启动时报缺类。
     */
    @Configuration
    @ConditionalOnClass(RequestInterceptor.class)
    static class FeignBeans {

        @Bean
        @ConditionalOnMissingBean
        public RequestInterceptor feignHeaderRelayInterceptor() {
            return new FeignHeaderRelayInterceptor();
        }

        @Bean
        @ConditionalOnMissingBean
        public Logger feignLogger() {
            return new FeignLogger().feignLogger();
        }

        @Bean
        @ConditionalOnMissingBean
        public Logger.Level feignLoggerLevel() {
            return Logger.Level.FULL;
        }
    }
}

