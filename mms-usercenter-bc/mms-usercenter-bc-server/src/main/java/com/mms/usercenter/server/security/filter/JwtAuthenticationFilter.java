package com.mms.usercenter.server.security.filter;

import com.mms.common.core.constants.gateway.GatewayConstants;
import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.security.service.GatewaySignatureVerificationService;
import com.mms.common.web.utils.WhitelistUtils;
import com.mms.usercenter.common.security.entity.SecurityUser;
import com.mms.usercenter.service.security.service.impl.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 实现功能【网关签名验证过滤器】
 * <p>
 * 作用说明：
 * 1. 检查请求路径是否在白名单中，如果是则直接放行
 * 2. 验证网关签名（使用RSA公钥），确保请求来自网关且未被篡改
 * 3. 从请求头获取用户信息（网关已验证并透传）
 * 4. 调用 UserDetailsService 加载用户详情和权限信息
 * 5. 创建 Authentication 对象并设置到 SecurityContext
 * 6. 为后续的方法级权限控制（@PreAuthorize）和 SecurityUtils 提供支持
 * <p>
 * 安全架构：
 * - 网关层（JwtAuthFilter）：完整验证JWT token（签名、过期、黑名单），使用RSA私钥生成签名
 * - 服务层（本过滤器）：验证网关签名（RSA公钥），信任网关透传的用户信息，加载权限
 * - 数字签名架构：网关做完整验证并签名，服务层做签名验证，防止请求头篡改和绕过网关
 *
 * @author li.hongyu
 * @date 2025-01-XX
 */
@Slf4j
@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserDetailsServiceImpl userDetailsService;
    private final GatewaySignatureVerificationService gatewaySignatureVerificationService;
    private final WhitelistUtils whitelistUtils;

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
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 检查是否已有认证信息
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 白名单请求：不需要签名验证，直接放行
        if (whitelistUtils.isWhitelisted(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 验证网关签名
        gatewaySignatureVerificationService.validate(request);

        // 从请求头获取用户名（网关已验证并透传）
        String username = request.getHeader(GatewayConstants.Headers.USER_NAME);
        if (!StringUtils.hasText(username)) {
            log.warn("网关签名验证通过，但请求头中缺少用户名: {}", request.getRequestURI());
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        // 加载用户详情和权限
        SecurityUser userDetails = (SecurityUser) userDetailsService.loadUserByUsername(username);

        // 创建 Authentication 对象
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        // 设置认证详情（IP 地址、Session ID 等）
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // 设置到 SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 继续过滤器链
        filterChain.doFilter(request, response);
    }
}