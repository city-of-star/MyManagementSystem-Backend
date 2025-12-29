package com.mms.common.security.utils;

import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.security.constants.JwtConstants;
import com.mms.common.core.enums.jwt.TokenType;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * 实现功能【Token验证工具类】
 * <p>
 * 负责Token的解析、验证（签名、过期、类型）
 * 仅适用于 WebMvc 场景，使用 同步Redis 会阻塞线程，不可用于网关
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-08 10:18:37
 */
@AllArgsConstructor
public class TokenValidatorUtils {

    private final JwtUtils jwtUtils;
    private final TokenBlacklistUtils tokenBlacklistUtils;

    /**
     * 解析并验证Token
     *
     * @param token        Token字符串
     * @param expectedType 期望的Token类型（可为null，表示不验证类型）
     * @return Claims
     */
    public Claims parseAndValidate(String token, TokenType expectedType) {
        if (!StringUtils.hasText(token)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        try {
            // 解析Token
            Claims claims = jwtUtils.parseToken(token);

            // 验证Token是否过期
            Date expiration = claims.getExpiration();
            if (expiration == null || expiration.before(new Date())) {
                throw new BusinessException(ErrorCode.LOGIN_EXPIRED);
            }

            // 验证Token类型
            if (expectedType != null) {
                TokenType realType = jwtUtils.extractTokenType(claims);
                if (realType != expectedType) {
                    throw new BusinessException(ErrorCode.INVALID_TOKEN);
                }
            }

            // 检查Token是否在黑名单中
            String jti = claims.getId();
            if (StringUtils.hasText(jti) &&  tokenBlacklistUtils.isBlacklisted(jti)) {
                throw new BusinessException(ErrorCode.LOGIN_EXPIRED);
            }

            return claims;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }
    }

    /**
     * 从Authorization请求头中提取Bearer Token
     *
     * @param authHeader Authorization请求头的值
     * @return 提取的Token字符串
     * @throws BusinessException 如果认证头为空、格式不正确或Token为空
     */
    public static String extractTokenFromHeader(String authHeader) {
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
