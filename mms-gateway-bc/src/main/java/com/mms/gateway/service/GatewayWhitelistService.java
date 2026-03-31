package com.mms.gateway.service;

import com.mms.common.security.core.properties.WhitelistProperties;
import com.mms.common.security.core.service.AbstractWhitelistService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 实现功能【网关白名单配置类】
 * <p>
 * 统一管理网关白名单路径，支持通过配置文件动态配置
 * 支持白名单缓存，当 Nacos 配置更新时自动刷新缓存
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-01 15:36:17
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(WhitelistProperties.class)
public class GatewayWhitelistService extends AbstractWhitelistService {

    /**
     * 接口路由统一前缀
     */
    private final static String API_PREFIX = "/api/";

    private final WhitelistProperties whitelistProperties;

    /**
     * 构建网关白名单路径列表
     * <p>
     * 网关层面的路径需要包含服务前缀
     * </p>
     */
    @Override
    protected List<String> buildRawPatterns() {
        List<String> paths = new ArrayList<>();
        Map<String, List<String>> services = whitelistProperties.getServices();
        if (services == null || services.isEmpty()) {
            return paths;
        }
        // 网关白名单
        for (Map.Entry<String, List<String>> entry : services.entrySet()) {
            // 服务名
            String serviceName = entry.getKey();
            if (serviceName == null || serviceName.isBlank()) {
                continue;
            }
            // 公共白名单（拼接 /api/{serviceName} 前缀）
            for (String commonPattern : whitelistProperties.getCommon()) {
                String normalizedPattern = normalizePath(commonPattern);
                paths.add(API_PREFIX + serviceName + normalizedPattern);
            }
            // 服务专属白名单（拼接 /api/{serviceName} 前缀）
            List<String> servicePatterns = entry.getValue();
            if (servicePatterns == null || servicePatterns.isEmpty()) {
                continue;
            }
            for (String pattern : servicePatterns) {
                String normalizedPattern = normalizePath(pattern);
                paths.add(API_PREFIX + serviceName + normalizedPattern);
            }
        }
        return paths;
    }

}

