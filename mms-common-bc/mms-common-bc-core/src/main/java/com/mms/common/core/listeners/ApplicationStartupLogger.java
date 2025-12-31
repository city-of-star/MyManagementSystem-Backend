package com.mms.common.core.listeners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 实现功能【应用启动成功日志记录】
 * <p>
 * 应用启动完成后打印应用基本信息
 * 统一支持网关服务和业务服务，根据应用名称自动识别服务类型
 * <p>
 *
 * @author li.hongyu
 * @date 2025-12-10 16:12:56
 */
@Slf4j
@Component
public class ApplicationStartupLogger implements ApplicationListener<ApplicationReadyEvent> {

    @Value("${spring.application.name:application}")
    private String applicationName;

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    @Value("${server.port:8080}")
    private int port;

    @Value("${info.app.version:unknown}")
    private String appVersion;

    @Autowired(required = false)
    private BuildProperties buildProperties;

    @Value("${spring.cloud.nacos.server-addr:unknown}")
    private String nacosServerAddr;

    @Value("${spring.cloud.nacos.config.namespace:${spring.cloud.nacos.discovery.namespace:unknown}}")
    private String nacosNamespace;

    @Value("${spring.cloud.nacos.config.group:${spring.cloud.nacos.discovery.group:DEFAULT_GROUP}}")
    private String nacosGroup;

    @Value("${logback.path:logs}")
    private String logPath;

    /**
     * 网关地址（用于构建通过网关访问的 Swagger 地址）
     */
    @Value("${swagger.gateway-url:http://localhost:5092}")
    private String gatewayUrl;

    /**
     * Knife4j UI 入口路径，默认 /doc.html。
     */
    @Value("${swagger.ui-path:/doc.html}")
    private String swaggerUiPath;

    /**
     * 判断是否为网关服务
     */
    private boolean isGateway() {
        return "gateway".equalsIgnoreCase(applicationName);
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // 打印启动完成与基础环境信息
        log.info("==================== {} 服务启动信息 ====================", applicationName);
        log.info("环境: {}", activeProfile);
        log.info("端口: {}", port);
        log.info("版本: {}", resolveVersion());
        log.info("构建时间: {}", resolveBuildTime());
        log.info("日志目录: {}", logPath);
        log.info("Nacos: {} | namespace={} | group={}", nacosServerAddr, nacosNamespace, nacosGroup);
        
        // 网关服务不打印 Swagger 地址
        if (!isGateway()) {
            // 构建 Swagger 地址
            String gatewayBase = stripTrailingSlash(gatewayUrl);
            String servicePrefix = "/" + applicationName;
            String gatewaySwaggerUrl = gatewayBase + servicePrefix + ensureLeadingSlash(swaggerUiPath);
            log.info("Swagger 文档地址: {}", gatewaySwaggerUrl);
        }
        
        log.info("==================== {} 服务启动成功 ====================\n", applicationName);

        // 在应用完全就绪时输出自定义 banner（位于各服务资源目录的 banner.txt）
        printBannerIfPresent();
    }

    /**
     * 在就绪后输出 classpath 下的 banner.txt，确保最后呈现。
     */
    private void printBannerIfPresent() {
        ClassPathResource resource = new ClassPathResource("banner.txt");
        if (!resource.exists()) {
            return;
        }
        try (InputStream is = resource.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            // 出现 IO 问题时仅记录，不影响启动
            System.out.println("读取 banner.txt 失败：" + e.getMessage());
        }
    }

    private String stripTrailingSlash(String url) {
        return url != null && url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    private String ensureLeadingSlash(String path) {
        return path != null && path.startsWith("/") ? path : "/" + path;
    }

    /**
     * 确定应用版本：优先使用 buildProperties 中的版本号，缺省回退到 info.app.version。
     */
    private String resolveVersion() {
        if (buildProperties != null && buildProperties.getVersion() != null && !buildProperties.getVersion().isBlank()) {
            return buildProperties.getVersion();
        }
        return appVersion;
    }

    /**
     * 确定构建时间：优先使用 buildProperties 中的时间，无法获取时返回 unknown。
     */
    private String resolveBuildTime() {
        String timeStr = null;

        if (buildProperties != null && buildProperties.getTime() != null) {
            timeStr = buildProperties.getTime().toString();
        }

        if (timeStr != null) {
            try {
                // 解析 ISO 8601 格式的时间（如 2025-12-10T11:28:13.377Z）
                Instant instant = Instant.parse(timeStr);

                // 转换为本地时区（默认使用系统时区，这里是北京时间 CST = UTC+8）
                ZoneId zoneId = ZoneId.systemDefault();
                ZonedDateTime localTime = instant.atZone(zoneId);

                // 格式化为可读性更强的时间格式
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                return formatter.format(localTime);

            } catch (Exception e) {
                // 如果解析失败，返回原始字符串
                return timeStr;
            }
        }

        return "unknown";
    }
}

