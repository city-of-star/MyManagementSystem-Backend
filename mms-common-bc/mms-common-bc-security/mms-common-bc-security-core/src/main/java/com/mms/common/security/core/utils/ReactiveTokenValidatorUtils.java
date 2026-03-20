package com.mms.common.security.core.utils;

import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.core.enums.jwt.TokenType;
import com.mms.common.security.core.constants.JwtCacheKeyConstants;
import com.mms.common.security.core.constants.JwtClaimsConstants;
import com.mms.common.security.core.constants.JwtHeaderConstants;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.Optional;

/**
 * 实现功能【Token验证工具类（Reactive 版）】
 * <p>
 * 适用于 WebFlux 场景，使用 Reactive Redis 避免阻塞线程。
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-11 16:16:16
 */
@AllArgsConstructor
public class ReactiveTokenValidatorUtils {

    private final JwtUtils jwtUtils;
    private final ReactiveStringRedisTemplate reactiveStringRedisTemplate;

    /**
     * 解析并验证Token（与同步版保持一致）
     *
     * @param token        Token字符串
     * @param expectedType 期望的Token类型（可为null，表示不验证类型）
     * @return Mono<Claims>
     */
    public Mono<Claims> parseAndValidate(String token, TokenType expectedType) {
        if (!StringUtils.hasText(token)) {
            return Mono.error(new BusinessException(ErrorCode.INVALID_TOKEN));
        }

        // 解析 Token
        final Claims claims;
        try {
            claims = jwtUtils.parseToken(token);
        } catch (BusinessException e) {
            return Mono.error(e);
        } catch (Exception e) {
            return Mono.error(new BusinessException(ErrorCode.INVALID_TOKEN));
        }

        // 校验过期
        Date expiration = claims.getExpiration();
        if (expiration == null || expiration.before(new Date())) {
            return Mono.error(new BusinessException(ErrorCode.LOGIN_EXPIRED));
        }

        // 校验类型
        if (expectedType != null) {
            TokenType realType = jwtUtils.extractTokenType(claims);
            if (realType != expectedType) {
                return Mono.error(new BusinessException(ErrorCode.INVALID_TOKEN));
            }
        }

        // 黑名单检查（Reactive Redis）
        String jti = claims.getId();
        if (!StringUtils.hasText(jti)) {
            return Mono.error(new BusinessException(ErrorCode.INVALID_TOKEN));
        }
        
        // 严格单会话：token 中的 sid 必须与 Redis 当前 sid 一致
        String username = Optional.ofNullable(claims.get(JwtClaimsConstants.USERNAME))
                .map(Object::toString)
                .orElse(null);
        String sid = Optional.ofNullable(claims.get(JwtClaimsConstants.SESSION_ID))
                .map(Object::toString)
                .orElse(null);
        if (!StringUtils.hasText(username) || !StringUtils.hasText(sid)) {
            return Mono.error(new BusinessException(ErrorCode.LOGIN_EXPIRED));
        }

        // 先查黑名单，再查 session（都通过才放行）
        String blacklistKey = JwtCacheKeyConstants.TOKEN_BLACKLIST_PREFIX + jti;
        String sessionKey = JwtCacheKeyConstants.SESSION_PREFIX + username;

        return reactiveStringRedisTemplate.hasKey(blacklistKey)
            .defaultIfEmpty(false)
            .flatMap(blacklisted -> {
                if (Boolean.TRUE.equals(blacklisted)) {
                    return Mono.error(new BusinessException(ErrorCode.LOGIN_EXPIRED));
                }
                return reactiveStringRedisTemplate.opsForValue().get(sessionKey)
                    .defaultIfEmpty("")
                    .flatMap(currentSid -> {
                        String normalizedCurrentSid = normalizeRedisSid(currentSid);
                        if (!StringUtils.hasText(normalizedCurrentSid) || !sid.equals(normalizedCurrentSid)) {
                            return Mono.error(new BusinessException(ErrorCode.LOGIN_EXPIRED));
                        }
                        return Mono.just(claims);
                    });
            });
    }

    /**
     * Redis 里 sid 可能会因为序列化方式不同而带引号（例如 JSON 序列化的字符串值："abc"）。
     * 这里做简单归一化，避免网关侧校验误判。
     */
    private String normalizeRedisSid(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        String v = value.trim();
        if (v.length() >= 2) {
            char first = v.charAt(0);
            char last = v.charAt(v.length() - 1);
            if ((first == '"' && last == '"') || (first == '\'' && last == '\'')) {
                return v.substring(1, v.length() - 1);
            }
        }
        return v;
    }

    /**
     * 从Authorization请求头中提取Bearer Token（与同步版保持一致）
     * <p>
     * 注意：此方法是纯字符串处理，无IO操作，执行时间极短，在响应式环境中调用是安全的。
     * </p>
     *
     * @param authHeader Authorization请求头的值
     * @return 提取的Token字符串
     * @throws BusinessException 如果认证头为空、格式不正确或Token为空
     */
    public String extractTokenFromHeader(String authHeader) {
        if (!StringUtils.hasText(authHeader)) {
            throw new BusinessException(ErrorCode.INVALID_AUTH_HEADER);
        }

        if (!authHeader.startsWith(JwtHeaderConstants.BEARER_PREFIX)) {
            throw new BusinessException(ErrorCode.INVALID_AUTH_HEADER);
        }

        String token = authHeader.substring(JwtHeaderConstants.BEARER_PREFIX.length()).trim();
        if (!StringUtils.hasText(token)) {
            throw new BusinessException(ErrorCode.INVALID_AUTH_HEADER);
        }
        
        return token;
    }
}

