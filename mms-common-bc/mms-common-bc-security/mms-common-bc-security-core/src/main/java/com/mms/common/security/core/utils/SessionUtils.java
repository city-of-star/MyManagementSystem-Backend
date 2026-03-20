package com.mms.common.security.core.utils;

import com.mms.common.cache.utils.RedisUtils;
import com.mms.common.security.core.constants.JwtCacheKeyConstants;
import com.mms.common.security.core.properties.JwtProperties;
import lombok.AllArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * 实现功能【单会话 Session 工具类】
 * <p>
 * 负责将“当前有效会话 sid”按用户名写入 Redis，用于严格单会话校验。
 * </p>
 *
 * @author li.hongyu
 * @date 2026-03-20 17:18:55
 */
@AllArgsConstructor
public class SessionUtils {

    private final JwtProperties jwtProperties;

    /**
     * 写入当前会话 sid
     *
     * @param username 用户名
     * @param sid      会话ID
     */
    public void storeSessionId(String username, String sid) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(sid)) {
            return;
        }
        String key = JwtCacheKeyConstants.SESSION_PREFIX + username;
        RedisUtils.set(key, sid, jwtProperties.getSessionExpiration(), TimeUnit.MILLISECONDS);
    }

    /**
     * 获取当前会话 sid
     */
    public String getSessionId(String username) {
        if (!StringUtils.hasText(username)) {
            return null;
        }
        String key = JwtCacheKeyConstants.SESSION_PREFIX + username;
        return RedisUtils.get(key, String.class);
    }

    /**
     * 删除当前会话（登出时使用）
     */
    public void removeSessionId(String username) {
        if (!StringUtils.hasText(username)) {
            return;
        }
        String key = JwtCacheKeyConstants.SESSION_PREFIX + username;
        RedisUtils.delete(key);
    }
}

