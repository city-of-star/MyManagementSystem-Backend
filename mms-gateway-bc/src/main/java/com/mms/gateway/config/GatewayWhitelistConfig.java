package com.mms.gateway.config;

import com.mms.gateway.utils.GatewayPathMatcherUtils;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 实现功能【网关白名单配置类】
 * <p>
 * 统一管理网关白名单路径，支持通过配置文件动态配置
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-01 15:36:17
 */
@Getter
@Configuration
@ConfigurationProperties(prefix = "gateway.whitelist")
public class GatewayWhitelistConfig {

    /**
     * 白名单路径列表
     */
    private List<String> paths = new ArrayList<>();

    /**
     * 默认白名单路径（如果配置文件中没有配置，则使用默认值）
     */
    public GatewayWhitelistConfig() {
        // 基础健康与登录
        this.paths.add("/actuator/**");                // Spring Boot Actuator 端点
        this.paths.add("/health");                     // Spring Boot health 端点
        this.paths.add("/usercenter/auth/login");      // 用户中心登录
        this.paths.add("/usercenter/auth/refresh");    // 用户中心刷新
        
        // Swagger/Knife4j 相关路径
        this.paths.add("/usercenter/doc.html");             // 用户中心 Knife4j 主页面
        this.paths.add("/usercenter/v3/api-docs/**");       // 用户中心 OpenAPI 文档
        this.paths.add("/usercenter/webjars/**");           // 用户中心 Knife4j 静态资源
        this.paths.add("/usercenter/swagger-resources/**"); // 用户中心 Swagger 资源
        this.paths.add("/usercenter/favicon.ico");          // 用户中心 favicon 图标
        
        this.paths.add("/base/doc.html");             // Base 服务 Knife4j 主页面
        this.paths.add("/base/v3/api-docs/**");       // Base 服务 OpenAPI 文档
        this.paths.add("/base/webjars/**");           // Base 服务 Knife4j 静态资源
        this.paths.add("/base/swagger-resources/**"); // Base 服务 Swagger 资源
        this.paths.add("/base/favicon.ico");          // Base 服务 favicon 图标
    }

    /**
     * 设置白名单路径列表
     *
     * @param paths 白名单路径列表
     */
    public void setPaths(List<String> paths) {
        this.paths = paths != null ? paths : new ArrayList<>();
    }

    /**
     * 检查路径是否在白名单中
     *
     * @param path 待检查的路径
     * @return 如果路径在白名单中则返回 true
     */
    public boolean isWhitelisted(String path) {
        return GatewayPathMatcherUtils.isWhitelisted(path, this.paths);
    }
}

