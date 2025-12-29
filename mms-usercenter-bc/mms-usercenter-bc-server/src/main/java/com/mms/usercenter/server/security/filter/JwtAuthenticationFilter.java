package com.mms.usercenter.server.security.filter;

import com.mms.common.core.constants.gateway.GatewayConstants;
import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.enums.jwt.TokenType;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.security.constants.JwtConstants;
import com.mms.common.security.utils.TokenValidatorUtils;
import com.mms.usercenter.common.security.entity.SecurityUser;
import com.mms.usercenter.server.config.SecurityWhitelistConfig;
import com.mms.usercenter.service.security.service.impl.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
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
import java.util.Optional;

/**
 * 实现功能【JWT 认证过滤器】
 * <p>
 * 作用说明：
 * 1. 优先从Authorization头获取token并进行轻量级验证（签名、过期、黑名单）
 * 2. 验证网关透传的用户信息与token一致（防止请求头被篡改）
 * 3. 如果没有token，向后兼容从网关透传的Header读取用户名（信任网关）
 * 4. 调用 UserDetailsService 加载用户详情和权限信息
 * 5. 创建 Authentication 对象并设置到 SecurityContext
 * 6. 为后续的方法级权限控制（@PreAuthorize）和 SecurityUtils 提供支持
 * <p>
 * 安全架构：
 * - 网关层（JwtAuthFilter）：完整验证JWT token（签名、过期、黑名单），透传token和用户信息
 * - 服务层（本过滤器）：轻量级验证token，验证用户信息一致性，加载权限
 * - 这是混合验证架构：网关做完整验证，服务层做轻量级验证和一致性校验
 *
 * @author li.hongyu
 * @date 2025-12-09 11:42:40
 */
@Slf4j
@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserDetailsServiceImpl userDetailsService;
    private final TokenValidatorUtils tokenValidatorUtils;
    private final SecurityWhitelistConfig whitelistConfig;

    /**
     * 过滤器核心逻辑
     * <p>
     * 执行流程：
     * 1. 检查 SecurityContext 中是否已有认证信息（避免重复处理）
     * 2. 检查请求路径是否在白名单中，如果是则直接放行（不需要token）
     * 3. 从Authorization头获取token并进行验证
     * 4. 验证网关透传的用户名与token一致（防止请求头被篡改）
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

        // 获取请求路径
        String requestPath = request.getRequestURI();

        // 检查是否在白名单中，如果是则直接放行（不需要token验证）
        if (whitelistConfig.isWhitelisted(requestPath)) {
            log.debug("请求路径在白名单中，直接放行: {}", requestPath);
            filterChain.doFilter(request, response);
            return;
        }

        // 从Authorization头获取token
        String authHeader = request.getHeader(JwtConstants.Headers.AUTHORIZATION);

        // 没有token，则抛出【无效的认证头】异常
        if (!StringUtils.hasText(authHeader)) {
            throw new BusinessException(ErrorCode.INVALID_AUTH_HEADER);
        }

        // 提取并验证token
        String token = TokenValidatorUtils.extractTokenFromHeader(authHeader);
        Claims claims = tokenValidatorUtils.parseAndValidate(token, TokenType.ACCESS);

        // 从token中提取用户名
        String username = Optional.ofNullable(claims.get(JwtConstants.Claims.USERNAME))
                .map(Object::toString)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN));

        // 获取网关透传的用户名，如果为null，说明是白名单或者被人伪造，白名单不用验证，被人伪造的话导致为null的话不用管，直接用token里面的用户名就可以了
        String headerUsername = request.getHeader(GatewayConstants.Headers.USER_NAME);

        // 验证网关透传的用户名是否与token一致（防止请求头被篡改）
        if (StringUtils.hasText(headerUsername) && !username.equals(headerUsername)) {
            log.warn("下游服务验证token时，用户名不一致，token中的username: {}, 请求头中的username: {}", username, headerUsername);
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