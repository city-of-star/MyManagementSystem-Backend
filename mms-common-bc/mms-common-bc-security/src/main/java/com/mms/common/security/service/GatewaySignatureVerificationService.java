package com.mms.common.security.service;

import com.mms.common.core.constants.gateway.GatewayConstants;
import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.security.properties.GatewaySignatureProperties;
import com.mms.common.security.utils.GatewaySignatureUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * 实现功能【网关签名验证器】
 * <p>
 * 下游服务使用RSA公钥验证网关签名，确保请求来自网关且未被篡改
 * </p>
 * <p>
 * 注意：此服务仅在 Servlet 环境（业务服务）中生效，网关（WebFlux）环境不会创建此 Bean
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-30 11:42:40
 */
@Slf4j
@AllArgsConstructor
public class GatewaySignatureVerificationService {

    private final GatewaySignatureProperties signatureProperties;

    /**
     * 验证网关签名
     *
     * @param request HTTP请求对象
     * @throws BusinessException 如果签名验证失败
     */
    public void validate(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        String traceId = request.getHeader(GatewayConstants.Headers.TRACE_ID);
        
        // 从请求头获取用户信息和签名
        String userId = request.getHeader(GatewayConstants.Headers.USER_ID);
        String username = request.getHeader(GatewayConstants.Headers.USER_NAME);
        String tokenJti = request.getHeader(GatewayConstants.Headers.TOKEN_JTI);
        String signature = request.getHeader(GatewayConstants.Headers.GATEWAY_SIGNATURE);
        String timestampStr = request.getHeader(GatewayConstants.Headers.GATEWAY_TIMESTAMP);

        // 检查必要字段是否存在
        if (!StringUtils.hasText(userId) || !StringUtils.hasText(username) ||
                !StringUtils.hasText(tokenJti) || !StringUtils.hasText(signature) ||
                !StringUtils.hasText(timestampStr)) {
            log.warn("网关签名验证失败: traceId={}, path={}, method={}, reason=确实请求头, userId={}, username={}, tokenJti={}, signature={}, timestamp={}",
                    traceId, path, method, userId, username, tokenJti, 
                    signature != null ? "存在" : "缺失", timestampStr);
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        // 解析时间戳
        long timestamp;
        try {
            timestamp = Long.parseLong(timestampStr);
        } catch (NumberFormatException e) {
            log.warn("网关签名验证失败: traceId={}, path={}, method={}, reason=无效的过期时间格式, timestamp={}",
                    traceId, path, method, timestampStr);
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        // 验证时间戳有效性（防重放攻击）
        long currentTime = System.currentTimeMillis();
        long timeDiff = Math.abs(currentTime - timestamp);
        if (timeDiff > signatureProperties.getTimestampValidity()) {
            log.warn("网关签名验证失败: traceId={}, path={}, method={}, reason=签名已过期, timestamp={}, currentTime={}, diff={}ms, userId={}, username={}",
                    traceId, path, method, timestamp, currentTime, timeDiff, userId, username);
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        // 验证签名
        boolean isValid = GatewaySignatureUtils.verify(
                userId, username, tokenJti, timestamp, signature, signatureProperties.getPublicKey());

        if (!isValid) {
            log.warn("网关签名验证失败: traceId={}, path={}, method={}, reason=签名不匹配, userId={}, username={}, tokenJti={}",
                    traceId, path, method, userId, username, tokenJti);
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        // 记录验证成功日志
        log.info("网关签名验证成功: traceId={}, path={}, method={}, username={}", traceId, path, method, username);
    }
}


