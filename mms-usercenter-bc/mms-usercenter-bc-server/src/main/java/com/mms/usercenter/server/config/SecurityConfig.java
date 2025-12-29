package com.mms.usercenter.server.config;

import com.mms.usercenter.server.security.filter.JwtAuthenticationFilter;
import lombok.AllArgsConstructor;
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
 * 作用说明：
 * 1. 配置 Spring Security 的安全策略
 * 2. 与网关层配合：网关负责 JWT 验证，服务层负责加载用户权限信息
 * 3. 为方法级权限控制（@PreAuthorize）和 SecurityUtils 提供支持
 * <p>
 * 工作流程：
 * 1. 网关验证 JWT token，提取用户名，通过 Header 透传到服务层
 * 2. JwtAuthenticationFilter 从 Header 读取用户名
 * 3. 调用 UserDetailsService 加载用户详情和权限
 * 4. 设置到 SecurityContext，供后续权限验证使用
 * <p>
 *
 * @author li.hongyu
 * @date 2025-12-09 11:42:20
 */
@Configuration
@EnableWebSecurity  // 启用 Web 安全
@EnableMethodSecurity(prePostEnabled = true) // 启用方法级安全
@AllArgsConstructor
public class SecurityConfig {

    /**
     * 自定义 JWT 认证过滤器
     * 作用：从网关透传的 Header 中读取用户名，加载用户权限，设置到 SecurityContext
     */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 配置安全过滤器链
     * <p>
     * 说明：
     * - 这是 Spring Security 的核心配置方法
     * - 定义了哪些路径需要认证，哪些路径可以放行
     * - 配置了自定义的 JWT 认证过滤器
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
                    .requestMatchers(
                        "/auth/login",
                        "/auth/refresh",
                        "/authority/**",
                        "/actuator/**",
                        "/doc.html",
                        "/v3/api-docs/**",
                        "/webjars/**",
                        "/swagger-resources/**",
                        "/favicon.ico"
                    ).permitAll()
                    .anyRequest().authenticated()
                )
                // 添加自定义 JWT 认证过滤器
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}