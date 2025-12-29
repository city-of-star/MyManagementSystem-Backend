package com.mms.usercenter.server.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.PathContainer;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.ArrayList;
import java.util.List;

/**
 * 实现功能【用户中心服务白名单配置类】
 * <p>
 * 统一管理用户中心服务的白名单路径，支持通过配置文件动态配置
 * 白名单路径不需要JWT token验证，直接放行
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-29
 */
@Getter
@Configuration
@ConfigurationProperties(prefix = "usercenter.security.whitelist")
public class SecurityWhitelistConfig {

    /**
     * 路径模式解析器（线程安全）
     */
    private static final PathPatternParser PATH_PATTERN_PARSER = new PathPatternParser();

    /**
     * 白名单路径列表
     */
    private List<String> paths = new ArrayList<>();

    /**
     * 默认白名单路径（如果配置文件中没有配置，则使用默认值）
     */
    public SecurityWhitelistConfig() {
        // 认证相关（不需要token）
        this.paths.add("/usercenter/auth/login");           // 登录
        this.paths.add("/usercenter/auth/refresh");         // 刷新token
        
        // 内部权限查询（网关已做认证，服务层不需要再验证）
        this.paths.add("/usercenter/authority/**");         // 内部权限查询
        
        // Spring Boot Actuator 端点
        this.paths.add("/usercenter/actuator/**");          // Spring Boot Actuator 端点
        
        // Swagger/Knife4j 相关路径
        this.paths.add("/usercenter/doc.html");             // Knife4j 主页面
        this.paths.add("/usercenter/v3/api-docs/**");       // OpenAPI 文档
        this.paths.add("/usercenter/webjars/**");           // Knife4j 静态资源
        this.paths.add("/usercenter/swagger-resources/**"); // Swagger 资源
        this.paths.add("/usercenter/favicon.ico");          // favicon 图标
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
        if (path == null || paths == null || paths.isEmpty()) {
            return false;
        }

        PathContainer pathContainer = PathContainer.parsePath(path);
        for (String pattern : paths) {
            PathPattern compiledPattern = PATH_PATTERN_PARSER.parse(pattern);
            if (compiledPattern.matches(pathContainer)) {
                return true;
            }
        }
        return false;
    }
}

