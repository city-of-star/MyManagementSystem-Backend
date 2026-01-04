package com.mms.base.server.security.filter;

import com.mms.base.feign.usercenter.UserAuthorityFeign;
import com.mms.common.core.constants.gateway.GatewayConstants;
import com.mms.common.core.constants.usercenter.UserAuthorityConstants;
import com.mms.base.feign.usercenter.dto.UserAuthorityDto;
import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.core.response.Response;
import com.mms.common.security.service.GatewaySignatureVerificationService;
import com.mms.common.web.utils.WhitelistUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 实现功能【网关签名验证过滤器】
 * <p>
 * 作用说明：
 * 1. 检查请求路径是否在白名单中，如果是则直接放行
 * 2. 验证网关签名（使用RSA公钥），确保请求来自网关且未被篡改
 * 3. 从请求头获取用户信息（网关已验证并透传）
 * 4. 根据 username 从 Redis 读取角色/权限，组装 Authentication 填充到 SecurityContext
 * 5. 便于 PermissionCheckAspect 正常获取权限
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
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserAuthorityFeign userAuthorityFeign;
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
     * 5. 加载用户角色和权限
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

        // 获取用户名
        String username = request.getHeader(GatewayConstants.Headers.USER_NAME);
        
        // 网关签名验证通过后，必须要有用户名
        if (!StringUtils.hasText(username)) {
            log.warn("网关签名验证通过，但请求头中缺少用户名: {}", request.getRequestURI());
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        // 从 Redis 中加载用户角色和权限
        Set<String> roles = loadStringSet(UserAuthorityConstants.USER_ROLE_PREFIX + username);
        Set<String> permissions = loadStringSet(UserAuthorityConstants.USER_PERMISSION_PREFIX + username);

        // 缓存缺失时，从用户中心获取
        if (CollectionUtils.isEmpty(roles) && CollectionUtils.isEmpty(permissions)) {
            Response<UserAuthorityDto> resp = userAuthorityFeign.getUserAuthorities(username);
            if (resp != null && Response.SUCCESS_CODE == resp.getCode() && resp.getData() != null) {
                roles = defaultSet(resp.getData().getRoles());
                permissions = defaultSet(resp.getData().getPermissions());
                cacheAuthorities(username, roles, permissions);
            }
        }

        // 将权限信息注入到 SpringSecurity 框架上下文，以启用方法级别的权限
        Set<GrantedAuthority> authorities = new HashSet<>();
        if (!CollectionUtils.isEmpty(roles)) {
            authorities.addAll(roles.stream()
                    .map(role -> new SimpleGrantedAuthority(UserAuthorityConstants.ROLE_PREFIX + role))
                    .toList());
        }
        if (!CollectionUtils.isEmpty(permissions)) {
            authorities.addAll(permissions.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet()));
        }

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(username, null, authorities);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    /**
     * 从 Redis 读取对象并转换为字符串集合，兼容 Set/List/单值
     */
    private Set<String> loadStringSet(String key) {
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached == null) {
            return Collections.emptySet();
        }
        if (cached instanceof Set<?> set) {
            return set.stream()
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .collect(Collectors.toSet());
        }
        if (cached instanceof Iterable<?> iterable) {
            Set<String> result = new HashSet<>();
            for (Object item : iterable) {
                if (item != null) {
                    result.add(item.toString());
                }
            }
            return result;
        }
        return Collections.singleton(cached.toString());
    }

    private Set<String> defaultSet(Set<String> set) {
        return set == null ? Collections.emptySet() : set;
    }

    private void cacheAuthorities(String username, Set<String> roles, Set<String> permissions) {
        redisTemplate.opsForValue().set(
                UserAuthorityConstants.USER_ROLE_PREFIX + username,
                defaultSet(roles),
                UserAuthorityConstants.ROLE_PERMISSION_CACHE_TTL_MINUTES,
                java.util.concurrent.TimeUnit.MINUTES
        );
        redisTemplate.opsForValue().set(
                UserAuthorityConstants.USER_PERMISSION_PREFIX + username,
                defaultSet(permissions),
                UserAuthorityConstants.ROLE_PERMISSION_CACHE_TTL_MINUTES,
                java.util.concurrent.TimeUnit.MINUTES
        );
    }
}

