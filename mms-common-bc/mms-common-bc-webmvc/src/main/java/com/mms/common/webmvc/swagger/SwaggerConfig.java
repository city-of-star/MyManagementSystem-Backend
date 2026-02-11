package com.mms.common.webmvc.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger / OpenAPI 配置
 * 说明：
 * - 仅用于 Spring MVC 业务服务，不用于 Gateway（WebFlux）
 * - 默认通过网关访问当前服务的接口：{gateway-url}/{spring.application.name}
 */
@Configuration
public class SwaggerConfig {

    /**
     * 网关地址（可通过配置文件覆盖，默认 http://localhost:5092）
     */
    @Value("${swagger.gateway-url:http://localhost:5092}")
    private String gatewayUrl;

    /**
     * 当前服务名，用于拼接网关前缀
     */
    @Value("${spring.application.name:}")
    private String applicationName;

    /**
     * 服务版本（优先使用 BuildProperties，其次 info.app.version）
     */
    @Value("${info.app.version:unknown}")
    private String appVersion;

    @Autowired(required = false)
    private BuildProperties buildProperties;

    /**
     * 配置 OpenAPI 信息与服务器地址
     */
    @Bean
    public OpenAPI customOpenAPI() {
        String displayName = (applicationName == null || applicationName.isEmpty())
                ? "API"
                : applicationName + " API";

        OpenAPI openAPI = new OpenAPI()
                .info(new Info()
                        .title(displayName + " 文档")
                        .description(displayName + "（通过网关访问）")
                        .version(resolveVersion())
                        .contact(new Contact()
                                .name("MMS开发团队")
                                .email("2825646787@qq.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));

        // 去掉网关地址末尾的 /
        String baseUrl = gatewayUrl.endsWith("/")
                ? gatewayUrl.substring(0, gatewayUrl.length() - 1)
                : gatewayUrl;

        // 拼接服务前缀（允许 applicationName 为空）
        String servicePrefix = (applicationName == null || applicationName.isEmpty())
                ? ""
                : "/" + applicationName;

        Server gatewayServer = new Server()
                .url(baseUrl + servicePrefix)
                .description("网关地址（推荐：所有请求经过网关 JWT 验证）");

        openAPI.setServers(List.of(gatewayServer));
        return openAPI;
    }

    /**
     * 确定文档展示的版本号：
     * - 优先使用 BuildProperties 中的版本（即构建产物版本）
     * - 否则回退到 info.app.version 配置
     */
    private String resolveVersion() {
        if (buildProperties != null
                && buildProperties.getVersion() != null
                && !buildProperties.getVersion().isBlank()) {
            return buildProperties.getVersion();
        }
        return appVersion;
    }
}