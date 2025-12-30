package com.mms.gateway.config;

import com.mms.common.security.properties.WhitelistProperties;
import com.mms.gateway.utils.GatewayPathMatcherUtils;
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
@Configuration
public class GatewayWhitelistConfig {

    /**
     * 白名单路径列表
     */
    private final List<String> paths = new ArrayList<>();

    /**
     * 默认白名单路径（如果配置文件中没有配置，则使用默认值）
     */
    public GatewayWhitelistConfig(WhitelistProperties whitelistProperties) {
        // 公共白名单：为每个下游服务加前缀
        for (String p : whitelistProperties.getCommon()) {
            this.paths.add("/usercenter" + p);
            this.paths.add("/base" + p);
        }

        // usercenter 专属
        for (String p : whitelistProperties.getUsercenter()) {
            this.paths.add("/usercenter" + p);
        }

        // base 专属
        for (String p : whitelistProperties.getBase()) {
            this.paths.add("/base" + p);
        }
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

