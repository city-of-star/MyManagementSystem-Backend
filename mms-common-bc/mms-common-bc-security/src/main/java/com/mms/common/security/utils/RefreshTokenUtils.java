package com.mms.common.security.utils;

import com.mms.common.security.constants.JwtConstants;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 实现功能【Refresh Token工具类】
 * <p>
 * 负责Refresh Token管理
 * 主要是与 redis交互
 * <p>
 *
 * @author li.hongyu
 * @date 2025-12-11 15:25:09
 */
@AllArgsConstructor
public class RefreshTokenUtils {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 将Refresh Token存储到Redis
     *
     * @param username      用户名
     * @param refreshClaims Refresh Token的Claims（用于获取过期时间和jti）
     */
    public void storeRefreshToken(String username, Claims refreshClaims) {
        if (!StringUtils.hasText(username) || refreshClaims == null) {
            return;
        }

        // 获取过期时间
        Date expiration = refreshClaims.getExpiration();
        if (expiration == null) {
            return;
        }

        // 计算剩余有效时间
        long ttl = expiration.getTime() - System.currentTimeMillis();
        if (ttl <= 0) {
            // Token已过期，无需存储
            return;
        }

        // 存储Refresh Token，key格式：mms:auth:refresh:{username}
        // value存储refresh token的jti，用于后续验证
        String key = JwtConstants.CacheKeys.REFRESH_TOKEN_PREFIX + username;
        String jti = refreshClaims.getId();
        redisTemplate.opsForValue().set(key, jti, ttl, TimeUnit.MILLISECONDS);
    }

    /**
     * 从Redis获取Refresh Token的jti
     *
     * @param username 用户名
     * @return Refresh Token的jti，如果不存在则返回null
     */
    public String getRefreshTokenJti(String username) {
        if (!StringUtils.hasText(username)) {
            return null;
        }

        String key = JwtConstants.CacheKeys.REFRESH_TOKEN_PREFIX + username;
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? value.toString() : null;
    }

    /**
     * 验证Refresh Token是否有效（检查是否在Redis中存在且匹配）
     *
     * @param username      用户名
     * @param refreshClaims Refresh Token的Claims
     * @return true表示有效，false表示无效或Redis未配置
     */
    public boolean isRefreshTokenValid(String username, Claims refreshClaims) {
        if (!StringUtils.hasText(username) || refreshClaims == null) {
            return false;
        }

        String storedJti = getRefreshTokenJti(username);
        String currentJti = refreshClaims.getId();

        // 如果Redis中没有存储，或者jti不匹配，则认为无效
        return storedJti != null && storedJti.equals(currentJti);
    }

    /**
     * 从Redis删除Refresh Token
     *
     * @param username 用户名
     */
    public void removeRefreshToken(String username) {
        if (!StringUtils.hasText(username)) {
            return;
        }

        String key = JwtConstants.CacheKeys.REFRESH_TOKEN_PREFIX + username;
        redisTemplate.delete(key);
    }
}