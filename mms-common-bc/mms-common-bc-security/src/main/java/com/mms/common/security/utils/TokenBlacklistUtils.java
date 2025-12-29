package com.mms.common.security.utils;

import com.mms.common.security.constants.JwtConstants;
import com.mms.common.core.enums.jwt.TokenType;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 实现功能【Token黑名单工具类】
 * <p>
 * 负责Token黑名单管理
 * 主要是与 redis交互
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-11 14:22:16
 */
@AllArgsConstructor
public class TokenBlacklistUtils {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 检查Token是否在黑名单中
     *
     * @param jti Token的唯一标识
     * @return true表示在黑名单中，false表示不在
     */
    public boolean isBlacklisted(String jti) {
        if (!StringUtils.hasText(jti)) {
            return false;
        }
        String key = JwtConstants.CacheKeys.TOKEN_BLACKLIST_PREFIX + jti;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 将Token加入黑名单
     *
     * @param claims Token的Claims
     */
    public void addToBlacklist(Claims claims) {

        if (claims == null) {
            return;
        }

        // 获取 jti、过期时间、token类型
        String jti = claims.getId();
        Date expiration = claims.getExpiration();
        if (!StringUtils.hasText(jti) || expiration == null) {
            return;
        }

        // 调用重载方法
        addToBlacklist(jti, expiration.getTime(), extractTokenType(claims));
    }

    /**
     * 将Token加入黑名单（通过jti和过期时间）
     * <p>
     * 适用于网关已验证Token并透传jti和过期时间的场景
     * </p>
     *
     * @param jti        Token的唯一标识
     * @param expiration Token的过期时间戳（毫秒）
     * @param tokenType  Token类型（ACCESS或REFRESH）
     * @throws IllegalStateException 如果Redis未配置
     */
    public void addToBlacklist(String jti, long expiration, TokenType tokenType) {
        if (!StringUtils.hasText(jti)) {
            return;
        }

        // 计算剩余有效时间
        long ttl = expiration - System.currentTimeMillis();
        if (ttl <= 0) {
            // Token已过期，无需加入黑名单
            return;
        }

        // 将Token加入黑名单，TTL设置为Token的剩余有效时间
        String key = JwtConstants.CacheKeys.TOKEN_BLACKLIST_PREFIX + jti;
        redisTemplate.opsForValue().set(key, tokenType != null ? tokenType.name() : "ACCESS", ttl, TimeUnit.MILLISECONDS);
    }

    /**
     * 从Claims中提取Token类型
     *
     * @param claims JWT Claims
     * @return Token类型，如果不存在则返回null
     */
    private TokenType extractTokenType(Claims claims) {
        Object tokenTypeObj = claims.get(JwtConstants.Claims.TOKEN_TYPE);
        if (tokenTypeObj == null) {
            return null;
        }
        try {
            return TokenType.valueOf(tokenTypeObj.toString());
        } catch (Exception e) {
            return null;
        }
    }
}

