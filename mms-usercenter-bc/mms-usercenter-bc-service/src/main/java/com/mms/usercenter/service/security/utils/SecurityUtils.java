package com.mms.usercenter.service.security.utils;

import com.mms.usercenter.common.security.entity.SecurityUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 实现功能【安全工具类】
 * <p>
 * 统一获取当前用户信息
 * 提供便捷方法获取权限、角色等
 * <p>
 *
 * @author li.hongyu
 * @date 2025-12-09 11:40:24
 */
public final class SecurityUtils {

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static SecurityUser getCurrentUser() {
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof SecurityUser securityUser) {
            return securityUser;
        }
        return null;
    }

    public static Long getUserId() {
        SecurityUser user = getCurrentUser();
        return user == null ? null : user.getUserId();
    }

    public static String getUsername() {
        SecurityUser user = getCurrentUser();
        return user == null ? null : user.getUsername();
    }

    public static Set<String> getRoles() {
        SecurityUser user = getCurrentUser();
        return user == null || user.getRoles() == null ? Collections.emptySet() : user.getRoles();
    }

    public static Set<String> getPermissions() {
        SecurityUser user = getCurrentUser();
        return user == null || user.getPermissions() == null ? Collections.emptySet() : user.getPermissions();
    }

    public static boolean hasRole(String roleCode) {
        return getRoles().stream().anyMatch(roleCode::equals);
    }

    public static boolean hasPermission(String permissionCode) {
        return getPermissions().stream().anyMatch(permissionCode::equals);
    }

    public static Set<String> getAuthorities() {
        Authentication authentication = getAuthentication();
        if (authentication == null || authentication.getAuthorities() == null) {
            return Collections.emptySet();
        }
        return authentication.getAuthorities().stream()
                .map(Object::toString)
                .collect(Collectors.toSet());
    }
}