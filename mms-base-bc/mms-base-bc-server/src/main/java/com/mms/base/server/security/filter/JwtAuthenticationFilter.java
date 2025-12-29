package com.mms.base.server.security.filter;

import com.mms.base.feign.usercenter.UserAuthorityFeign;
import com.mms.common.core.constants.gateway.GatewayConstants;
import com.mms.common.core.constants.usercenter.UserAuthorityConstants;
import com.mms.base.feign.usercenter.dto.UserAuthorityDto;
import com.mms.common.core.response.Response;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
 * 实现功能【下游服务认证填充过滤器】
 * <p>
 * - 网关已完成 JWT 校验，并透传 userId/username
 * - 这里根据 userId 从 Redis 读取角色/权限，组装 Authentication 填充到 SecurityContext
 * - 便于 PermissionCheckAspect 正常获取权限
 * <p>
 *
 * @author li.hongyu
 * @date 2025-12-23 20:05:32
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserAuthorityFeign userAuthorityFeign;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String userId = request.getHeader(GatewayConstants.Headers.USER_ID);
        String username = request.getHeader(GatewayConstants.Headers.USER_NAME);
        if (!StringUtils.hasText(userId)) {
            filterChain.doFilter(request, response);
            return;
        }

        Set<String> roles = loadStringSet(UserAuthorityConstants.USER_ROLE_PREFIX + userId);
        Set<String> permissions = loadStringSet(UserAuthorityConstants.USER_PERMISSION_PREFIX + userId);

        // 缓存缺失时回源用户中心
        if (CollectionUtils.isEmpty(roles) && CollectionUtils.isEmpty(permissions)) {
            Long userIdLong = parseUserId(userId);
            if (userIdLong != null) {
                Response<UserAuthorityDto> resp = userAuthorityFeign.getUserAuthorities(userIdLong);
                if (resp != null && Response.SUCCESS_CODE == resp.getCode() && resp.getData() != null) {
                    roles = defaultSet(resp.getData().getRoles());
                    permissions = defaultSet(resp.getData().getPermissions());
                    cacheAuthorities(userId, roles, permissions);
                }
            }
        }

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

        String principal = StringUtils.hasText(username) ? username : userId;
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, authorities);
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

    private Long parseUserId(String userId) {
        try {
            return Long.parseLong(userId);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private Set<String> defaultSet(Set<String> set) {
        return set == null ? Collections.emptySet() : set;
    }

    private void cacheAuthorities(String userId, Set<String> roles, Set<String> permissions) {
        redisTemplate.opsForValue().set(
                UserAuthorityConstants.USER_ROLE_PREFIX + userId,
                defaultSet(roles),
                UserAuthorityConstants.ROLE_PERMISSION_CACHE_TTL_MINUTES,
                java.util.concurrent.TimeUnit.MINUTES
        );
        redisTemplate.opsForValue().set(
                UserAuthorityConstants.USER_PERMISSION_PREFIX + userId,
                defaultSet(permissions),
                UserAuthorityConstants.ROLE_PERMISSION_CACHE_TTL_MINUTES,
                java.util.concurrent.TimeUnit.MINUTES
        );
    }
}

