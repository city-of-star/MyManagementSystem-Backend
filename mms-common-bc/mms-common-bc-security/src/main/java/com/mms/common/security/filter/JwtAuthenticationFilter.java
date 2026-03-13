package com.mms.common.security.filter;

import com.mms.common.core.constants.gateway.GatewayConstants;
import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.security.service.GatewaySignatureVerificationService;
import com.mms.common.security.service.ServiceWhitelistService;
import com.mms.common.security.vo.UserAuthorityVo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 实现功能【通用Jwt过滤器】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-03-13 15:51:56
 */
@Slf4j
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserAuthorityProvider userAuthorityProvider;
    private final GatewaySignatureVerificationService gatewaySignatureVerificationService;
    private final ServiceWhitelistService serviceWhitelistUtils;

    /**
     * 过滤器核心逻辑
     * <p>
     * 执行流程：
     * 1. 检查 SecurityContext 中是否已有认证信息（避免重复处理）
     * 2. 检查请求路径是否在白名单中，如果是则直接放行（不需要签名验证）
     * 3. 验证网关签名（使用RSA公钥），确保请求来自网关且未被篡改
     * 4. 从请求头获取用户信息（网关已验证并透传）
     * 5. 加载用户详情和权限
     * 6. 创建 Authentication 对象并设置到 SecurityContext
     * 7. 继续过滤器链
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        String method = request.getMethod();
        String traceId = request.getHeader(GatewayConstants.Headers.TRACE_ID);
        // 检查是否已有认证信息
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }
        // 白名单请求：不需要签名验证，直接放行
        if (serviceWhitelistUtils.isWhitelisted(path)) {
            filterChain.doFilter(request, response);
            return;
        }
        // 验证网关签名
        gatewaySignatureVerificationService.validate(request);
        // 获取用户名和用户ID
        String username = request.getHeader(GatewayConstants.Headers.USER_NAME);
        String userId = request.getHeader(GatewayConstants.Headers.USER_ID);
        // 网关签名验证通过后，必须要有用户名
        if (!StringUtils.hasText(username)) {
            log.warn("网关签名验证通过但缺少用户名: traceId={}, path={}, method={}, userId={}", traceId, path, method, userId);
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }
        // 获取用户角色和权限
        UserAuthorityVo authoritiesVo = userAuthorityProvider.getUserAuthoritiesFromSource(username);
        // 组装用户权限
        Set<GrantedAuthority> authorities = new HashSet<>();
        Set<String> roles = authoritiesVo.getRoles();
        Set<String> permissions = authoritiesVo.getPermissions();
        if (!CollectionUtils.isEmpty(roles)) {
            authorities.addAll(roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet()));
        }
        if (!CollectionUtils.isEmpty(permissions)) {
            authorities.addAll(permissions.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet()));
        }
        // 创建 Authentication 对象，并添加用户名和权限
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
        // 设置认证详情（IP 地址、Session ID 等）
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        // 设置到 SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 继续过滤器链
        filterChain.doFilter(request, response);
    }
}