package com.mms.usercenter.service.auth.utils;

import com.mms.common.webmvc.utils.UserContextUtils;
import com.mms.usercenter.common.auth.entity.UserEntity;
import com.mms.usercenter.common.auth.vo.UserDetailVo;
import com.mms.usercenter.service.auth.mapper.UserMapper;
import com.mms.usercenter.service.auth.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
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
@Component
public class UserUtils {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserService userService;

    /**
     * 获取当前用户实体信息
     */
    public UserEntity getCurrentUserEntity() {
        Long userId = UserContextUtils.getUserId();
        if (userId == null) {
            return null;
        }
        return userMapper.selectById(userId);
    }

    /**
     * 获取当前用户详细信息（包含部门、岗位）
     */
    public UserDetailVo getCurrentUserDetail() {
        Long userId = UserContextUtils.getUserId();
        if (userId == null) {
            return null;
        }
        return userService.getUserById(userId);
    }

    /**
     * 获取当前用户ID
     */
    public Long getUserId() {
        return UserContextUtils.getUserId();
    }

    /**
     * 获取当前用户名
     */
    public String getUsername() {
        return UserContextUtils.getUsername();
    }

    /**
     * 获取当前用户拥有的角色
     */
    public Set<String> getRoles() {
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
    public Set<String> getPermissions() {
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
    public boolean hasRole(String roleCode) {
        return StringUtils.hasText(roleCode) && getRoles().contains(roleCode);
    }


    /**
     * 判断当前用户是否拥有某权限
     */
    public boolean hasPermission(String permissionCode) {
        return StringUtils.hasText(permissionCode) && getPermissions().contains(permissionCode);
    }

    /**
     * 获取当前用户认证信息
     */
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}