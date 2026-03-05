package com.mms.common.cache.config;

import com.mms.common.cache.constants.CacheKeyPrefix;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.util.StringUtils;

/**
 * 实现功能【缓存Key生成器配置】
 * <p>
 * 提供统一的缓存Key生成策略，避免硬编码key
 * </p>
 *
 * @author li.hongyu
 * @date 2026-03-05 09:38:16
 */
public class KeyGeneratorConfig {

    /**
     * 用户中心服务的缓存Key生成器
     * Key格式：mms:cache:usercenter:methodName:...
     */
    public KeyGenerator userKeyGenerator() {
        return (target, method, params) -> {
            StringBuilder keyBuilder = new StringBuilder(CacheKeyPrefix.USERCENTER).append(method.getName());
            if (params.length > 0) {
                keyBuilder.append(":");
                keyBuilder.append(StringUtils.arrayToDelimitedString(params, ":"));
            }
            return keyBuilder.toString();
        };
    }

    /**
     * 基础数据服务的缓存Key生成器
     * Key格式：mms:cache:base:methodName:...
     */
    public KeyGenerator baseKeyGenerator() {
        return (target, method, params) -> {
            StringBuilder keyBuilder = new StringBuilder(CacheKeyPrefix.BASE).append(method.getName());
            if (params.length > 0) {
                keyBuilder.append(":");
                keyBuilder.append(StringUtils.arrayToDelimitedString(params, ":"));
            }
            return keyBuilder.toString();
        };
    }

    /**
     * 网关服务的缓存Key生成器
     * Key格式：mms:cache:gateway:methodName:...
     */
    public KeyGenerator gatewayKeyGenerator() {
        return (target, method, params) -> {
            StringBuilder keyBuilder = new StringBuilder(CacheKeyPrefix.GATEWAY).append(method.getName());
            if (params.length > 0) {
                keyBuilder.append(":");
                keyBuilder.append(StringUtils.arrayToDelimitedString(params, ":"));
            }
            return keyBuilder.toString();
        };
    }
}

