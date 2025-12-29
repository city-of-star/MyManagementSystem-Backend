package com.mms.common.security.utils;

import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.security.constants.JwtConstants;
import com.mms.common.core.enums.jwt.TokenType;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.Date;

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
        
        // 构建黑名单Redis key，检查Token是否在黑名单中
        String key = JwtConstants.CacheKeys.TOKEN_BLACKLIST_PREFIX + jti;
        return reactiveStringRedisTemplate.hasKey(key)
                .defaultIfEmpty(false)
                .flatMap(exists -> {
                    // 如果Token在黑名单中，返回登录过期错误
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new BusinessException(ErrorCode.LOGIN_EXPIRED));
                    }
                    // Token不在黑名单中，验证通过
                    return Mono.just(claims);
                });
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

        if (!authHeader.startsWith(JwtConstants.Headers.BEARER_PREFIX)) {
            throw new BusinessException(ErrorCode.INVALID_AUTH_HEADER);
        }

        String token = authHeader.substring(JwtConstants.Headers.BEARER_PREFIX.length()).trim();
        if (!StringUtils.hasText(token)) {
            throw new BusinessException(ErrorCode.INVALID_AUTH_HEADER);
        }
        
        return token;
    }
}

