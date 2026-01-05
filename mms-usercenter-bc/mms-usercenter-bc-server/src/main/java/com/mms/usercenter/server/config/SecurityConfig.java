package com.mms.usercenter.server.config;

import com.mms.usercenter.server.security.filter.JwtAuthenticationFilter;
import com.mms.common.security.service.ServiceWhitelistService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 实现功能【Spring Security 配置类】
 * <p>
 *  统一配置 usercenter 服务的安全规则（无状态、白名单、JWT 过滤器等）
 * <p>
 *
 * @author li.hongyu
 * @date 2025-12-09 11:42:20
 */
@Configuration
@EnableWebSecurity  // 启用 Web 安全
@EnableMethodSecurity(prePostEnabled = true) // 启用方法级安全
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * 自定义 JWT 认证过滤器
     */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 白名单服务
     */
    private final ServiceWhitelistService serviceWhitelistService;

    /**
     * 配置安全过滤器链
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
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
                // 添加自定义 JWT 认证过滤器
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}


