package com.mms.common.security.service;

import com.mms.common.security.properties.WhitelistProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;

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
        patterns.addAll(whitelistProperties.getCommon());
        
        // 服务专属白名单
        if ("usercenter".equals(applicationName)) {
            patterns.addAll(whitelistProperties.getUsercenter());
        } else if ("base".equals(applicationName)) {
            patterns.addAll(whitelistProperties.getBase());
        }
        
        return patterns;
    }
}