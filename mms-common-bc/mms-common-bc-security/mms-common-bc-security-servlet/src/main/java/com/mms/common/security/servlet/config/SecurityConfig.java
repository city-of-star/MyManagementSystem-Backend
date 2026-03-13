package com.mms.common.security.servlet.config;

import com.mms.common.security.servlet.filter.JwtAuthenticationFilter;
import com.mms.common.security.servlet.service.ServiceWhitelistService;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 实现功能【通用 Spring Security 配置辅助类】
 * <p>
 *  提供基于 JWT 的无状态安全配置构建方法，统一：
 *  - 关闭 CSRF
 *  - Session 政策设为 STATELESS
 *  - 白名单放行，其他请求必须认证
 *  - 在过滤器链中注册通用 JwtAuthenticationFilter
 * <p>
 *
 * @author li.hongyu
 * @date 2026-03-13 16:05:58
 */
public class SecurityConfig {

    /**
     * 构建通用安全过滤器链
     */
    public static SecurityFilterChain buildDefaultSecurityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter, ServiceWhitelistService serviceWhitelistService) throws Exception {
        http
            // 禁用 CSRF 保护
            .csrf(AbstractHttpConfigurer::disable)
            // 配置 Session 策略为无状态
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 配置请求授权规则
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(serviceWhitelistService.getWhitelistPatternStrings()).permitAll()
                    .anyRequest().authenticated()
            )
            // 添加通用 JWT 认证过滤器
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}