package com.mms.gateway.utils;

import com.mms.common.security.properties.WhitelistProperties;
import com.mms.common.security.utils.AbstractWhitelistUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

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
public class GatewayWhitelistUtils extends AbstractWhitelistUtils {

    private final WhitelistProperties whitelistProperties;

    /**
     * 构建网关白名单路径列表
     * <p>
     * 网关层面的路径需要包含服务前缀（/usercenter 或 /base）
     * </p>
     */
    @Override
    protected List<String> buildRawPatterns() {
        List<String> paths = new ArrayList<>();
        
        // 公共白名单：为每个下游服务加前缀
        for (String pattern : whitelistProperties.getCommon()) {
            String normalizedPattern = normalizePath(pattern);
            paths.add("/usercenter" + normalizedPattern);
            paths.add("/base" + normalizedPattern);
        }

        // usercenter 专属白名单
        for (String pattern : whitelistProperties.getUsercenter()) {
            String normalizedPattern = normalizePath(pattern);
            paths.add("/usercenter" + normalizedPattern);
        }

        // base 专属白名单
        for (String pattern : whitelistProperties.getBase()) {
            String normalizedPattern = normalizePath(pattern);
            paths.add("/base" + normalizedPattern);
        }
        
        return paths;
    }

}

