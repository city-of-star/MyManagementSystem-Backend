package com.mms.usercenter.service.auth.utils;

import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.webmvc.utils.UserContextUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 实现功能【用户工具类】
 * <p>
 * 统一获取当前用户信息，提供便捷方法获取权限、角色等
 * <p>
 *
 * @author li.hongyu
 * @date 2025-12-09 11:40:24
 */
public final class UserUtils {

    /**
     * 获取当前用户ID
     */
    public static Long getUserId() {
        Long userId = UserContextUtils.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.LOGIN_EXPIRED, "登录信息已过期，请重新登录");
        }
        return userId;
    }

    /**
     * 获取当前用户名
     */
    public static String getUsername() {
        String username = UserContextUtils.getUsername();
        if (username == null) {
            throw new BusinessException(ErrorCode.LOGIN_EXPIRED, "登录信息已过期，请重新登录");
        }
        return username;
    }

    /**
     * 获取当前用户拥有的角色
     */
    public static Set<String> getRoles() {
        Authentication authentication = getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getAuthorities() == null) {
            return Collections.emptySet();
        }
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority != null && authority.startsWith("ROLE_"))
                .map(authority -> authority.substring("ROLE_".length()))
                .collect(Collectors.toSet());
    }

    /**
     * 获取当前用户拥有的权限
     */
    public static Set<String> getPermissions() {
        Authentication authentication = getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getAuthorities() == null) {
            return Collections.emptySet();
        }
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority != null && !authority.startsWith("ROLE_"))
                .collect(Collectors.toSet());
    }

    /**
     * 判断当前用户是否拥有某角色
     */
    public static boolean hasRole(String roleCode) {
        return StringUtils.hasText(roleCode) && getRoles().contains(roleCode);
    }


    /**
     * 判断当前用户是否拥有某权限
     */
    public static boolean hasPermission(String permissionCode) {
        return StringUtils.hasText(permissionCode) && getPermissions().contains(permissionCode);
    }

    /**
     * 获取当前用户认证信息
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 私有构造函数，防止实例化
     */
    private UserUtils() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }
}