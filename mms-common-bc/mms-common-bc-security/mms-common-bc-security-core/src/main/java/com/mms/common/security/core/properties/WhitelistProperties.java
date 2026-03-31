package com.mms.common.security.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * 通用白名单
     */
    private List<String> common = new ArrayList<>();

    /**
     * 各服务白名单
     */
    private Map<String, List<String>> services = new HashMap<>();
}