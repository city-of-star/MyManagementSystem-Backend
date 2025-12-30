package com.mms.common.security.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 实现功能【统一白名单配置属性】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-30 16:39:56
 */
@Data
@ConfigurationProperties(prefix = "whitelist")
public class WhitelistProperties {

    /**
     * 通用白名单（Swagger、actuator 等）
     */
    private List<String> common = new ArrayList<>();

    /**
     * 用户中心专属白名单
     */
    private List<String> usercenter = new ArrayList<>();

    /**
     * Base 服务专属白名单
     */
    private List<String> base = new ArrayList<>();
}