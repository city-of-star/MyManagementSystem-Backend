package com.mms.common.security.core.utils;

import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.core.enums.jwt.TokenType;
import com.mms.common.security.core.constants.JwtClaimsConstants;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Optional;

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
    private final SessionUtils sessionUtils;

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

            // 从 JWT Claims 中获取 username、sid
            String username = Optional.ofNullable(claims.get(JwtClaimsConstants.USERNAME))
                    .map(Object::toString)
                    .orElse(null);
            String sid = Optional.ofNullable(claims.get(JwtClaimsConstants.SESSION_ID))
                    .map(Object::toString)
                    .orElse(null);
            if (!StringUtils.hasText(username) || !StringUtils.hasText(sid)) {
                throw new BusinessException(ErrorCode.LOGIN_EXPIRED);
            }
            // 校验 session
            String currentSid = sessionUtils.getSessionId(username);
            if (!StringUtils.hasText(currentSid) || !sid.equals(currentSid)) {
                throw new BusinessException(ErrorCode.LOGIN_EXPIRED);
            }

            return claims;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }
    }
}
