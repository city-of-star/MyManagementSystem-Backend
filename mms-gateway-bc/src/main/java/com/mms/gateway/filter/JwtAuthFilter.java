package com.mms.gateway.filter;

import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.core.enums.jwt.TokenType;
import com.mms.common.security.core.constants.JwtClaimsConstants;
import com.mms.common.security.core.constants.JwtHeaderConstants;
import com.mms.common.security.core.utils.ReactiveTokenValidatorUtils;
import com.mms.gateway.service.GatewayWhitelistService;
import com.mms.common.core.constants.gateway.GatewayConstants;
import com.mms.gateway.service.GatewaySignatureService;
import com.mms.gateway.utils.GatewayResponseUtils;
import com.mms.gateway.utils.GatewayMdcUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.Optional;

/**
 * 实现功能【JWT 鉴权过滤器】
 * <p>
 * - 支持白名单放行
 * - 校验 Authorization: Bearer <token>
 * - 解析用户信息并透传到下游
 * - 未认证/无效时返回标准响应体（带 traceId）
 * </p>
 *
 * @author li.hongyu
 * @date 2025-11-10 15:36:17
 */
@Slf4j
@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    // 白名单配置
    @Resource
    private GatewayWhitelistService gatewayWhitelistService;

    // Reactive Token验证器
    @Resource
    private ReactiveTokenValidatorUtils reactiveTokenValidatorUtils;

    // 网关签名服务
    @Resource
    private GatewaySignatureService gatewaySignatureService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = request.getMethod().name();
        String traceId = GatewayMdcUtils.getTraceIdFromMdc();
        // 白名单直接放行
        if (gatewayWhitelistService.isWhitelisted(path)) {
            return chain.filter(exchange);
        }
        // 解析 Authorization 头部
        String authHeader = resolveAuthHeader(request);
        // 提取 JWT Token
        String token;
        try {
            token = reactiveTokenValidatorUtils.extractTokenFromHeader(authHeader);
        } catch (BusinessException e) {
            // 认证头格式错误，直接返回错误响应
            log.warn("JWT认证失败: traceId={}, path={}, method={}, reason={}", traceId, path, method, e.getMessage());
            return GatewayResponseUtils.writeError(exchange, HttpStatus.UNAUTHORIZED, e.getMessage());
        }
        // 解析并验证Token（验证类型必须是ACCESS，并检查黑名单）
        return reactiveTokenValidatorUtils.parseAndValidate(token, TokenType.ACCESS)
            .flatMap(claims -> {
                // 从 Token 中获取 userId
                String userId = Optional.ofNullable(claims.get(JwtClaimsConstants.USER_ID))
                        .map(Object::toString)
                        .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN));
                // 从 Token 中获取 username
                String username = Optional.ofNullable(claims.get(JwtClaimsConstants.USERNAME))
                        .map(Object::toString)
                        .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN));
                // 从 Token 中获取 jti（Token 标识）
                String jti = claims.getId();
                // 从 Token 中获取 expiration（Token 过期时间）
                Date expiration = claims.getExpiration();
                // 生成网关签名（使用RSA私钥）
                String[] signatureResult = gatewaySignatureService.generateSignature(userId, username, jti);
                String signature = signatureResult[0];
                String timestamp = signatureResult[1];
                // 将用户信息和签名透传到下游服务
                ServerHttpRequest mutatedRequest = request.mutate()
                    .headers(httpHeaders -> {
                        if (StringUtils.hasText(userId)) {
                            // 将 userId 添加到请求头，供下游服务使用
                            httpHeaders.set(GatewayConstants.Headers.USER_ID, userId);
                            // 兼容 websocket 模块默认从 X-User-Id 读取握手用户
                            httpHeaders.set("X-User-Id", userId);
                        }
                        if (StringUtils.hasText(username)) {
                            // 将 username 添加到请求头，供下游服务使用
                            httpHeaders.set(GatewayConstants.Headers.USER_NAME, username);
                        }
                        if (StringUtils.hasText(jti)) {
                            // 将 jti 添加到请求头，供下游服务使用（用于黑名单）
                            httpHeaders.set(GatewayConstants.Headers.TOKEN_JTI, jti);
                        }
                        if (expiration != null) {
                            // 将 expiration 添加到请求头，供下游服务使用（用于黑名单TTL计算）
                            httpHeaders.set(GatewayConstants.Headers.TOKEN_EXP, String.valueOf(expiration.getTime()));
                        }
                        // 添加网关签名和时间戳
                        httpHeaders.set(GatewayConstants.Headers.GATEWAY_SIGNATURE, signature);
                        httpHeaders.set(GatewayConstants.Headers.GATEWAY_TIMESTAMP, timestamp);
                    })
                    .build();
                // 记录认证成功日志
                log.info("JWT认证成功: traceId={}, path={}, method={}, username={}", traceId, path, method, username);
                // 继续过滤器链
                return chain.filter(exchange.mutate().request(mutatedRequest).build());
            })
            .onErrorResume(BusinessException.class, e -> {
                // Token验证失败（业务异常：过期、无效、黑名单等）
                log.warn("JWT认证失败: traceId={}, path={}, method={}, reason={}", traceId, path, method, e.getMessage());
                return GatewayResponseUtils.writeError(exchange, HttpStatus.UNAUTHORIZED, e.getMessage());
            })
            .onErrorResume(e -> {
                log.error("网关链路异常: traceId={}, path={}, method={}, error={}", traceId, path, method, e.getMessage(), e);
                return Mono.error(e);
            });
    }

    @Override
    public int getOrder() {
        // 在 TraceFilter 之后执行，保证 traceId 已经生成并透传到请求头
        return GatewayConstants.FilterOrder.JWT_AUTH_FILTER;
    }

    /**
     * 解析请求中的认证头信息。
     */
    private String resolveAuthHeader(ServerHttpRequest request) {
        // 读取标准 Authorization 头
        String authHeader = request.getHeaders().getFirst(JwtHeaderConstants.AUTHORIZATION);
        if (StringUtils.hasText(authHeader)) {
            // 一般请求直接返回
            return authHeader;
        }
        // 非 WS 握手请求保持原有 HTTP 认证语义
        if (!isWebSocketHandshake(request)) {
            return authHeader;
        }
        // WebSocket 握手
        String protocolHeader = request.getHeaders().getFirst(GatewayConstants.WebSocket.PROTOCOL_HEADER);
        if (!StringUtils.hasText(protocolHeader)) {
            return authHeader;
        }
        if (protocolHeader.regionMatches(true, 0, GatewayConstants.WebSocket.BEARER_PROTOCOL_PREFIX, 0, GatewayConstants.WebSocket.BEARER_PROTOCOL_PREFIX.length())) {
            String token = protocolHeader.substring(GatewayConstants.WebSocket.BEARER_PROTOCOL_PREFIX.length());
            if (StringUtils.hasText(token)) {
                return JwtHeaderConstants.BEARER_PREFIX + token;
            }
        }
        return authHeader;
    }

    /**
     * 判断当前请求是否为 WebSocket 握手请求
     */
    private boolean isWebSocketHandshake(ServerHttpRequest request) {
        String upgrade = request.getHeaders().getFirst(HttpHeaders.UPGRADE);
        return GatewayConstants.WebSocket.UPGRADE_HEADER_VALUE.equalsIgnoreCase(upgrade);
    }
}

