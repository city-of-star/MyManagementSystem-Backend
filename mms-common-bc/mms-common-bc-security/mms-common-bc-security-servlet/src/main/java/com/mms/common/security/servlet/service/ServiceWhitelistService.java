package com.mms.common.security.servlet.service;

import com.mms.common.security.core.properties.WhitelistProperties;
import com.mms.common.security.core.service.AbstractWhitelistService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 实现功能【业务服务白名单工具】
 * <p>
 * 支持白名单缓存，当 Nacos 配置更新时自动刷新缓存
 * 仅在 Servlet 环境（业务服务）中加载
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-30 16:44:56
 */
@RequiredArgsConstructor
public class ServiceWhitelistService extends AbstractWhitelistService {

    private final WhitelistProperties whitelistProperties;

    /**
     * 当前服务名
     */
    @Value("${spring.application.name}")
    private String applicationName;

    /**
     * 构建当前服务的白名单模式列表
     * <p>
     * 业务服务不需要加服务前缀，直接使用配置中的模式
     * 根据服务名选择对应的白名单配置
     * </p>
     */
    @Override
    protected List<String> buildRawPatterns() {
        List<String> patterns = new ArrayList<>();
        // 公共白名单
        if (whitelistProperties.getCommon() != null && !whitelistProperties.getCommon().isEmpty()) {
            patterns.addAll(whitelistProperties.getCommon());
        }
        // 判空
        Map<String, List<String>> services = whitelistProperties.getServices();
        if (services == null || services.isEmpty() || applicationName == null) {
            return patterns;
        }
        // 服务专属白名单
        List<String> servicePatterns = services.get(applicationName);
        if (servicePatterns != null && !servicePatterns.isEmpty()) {
            patterns.addAll(servicePatterns);
        }
        return patterns;
    }
}