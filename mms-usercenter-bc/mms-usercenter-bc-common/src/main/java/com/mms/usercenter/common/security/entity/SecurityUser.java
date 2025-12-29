package com.mms.usercenter.common.security.entity;

import com.mms.common.core.constants.usercenter.UserAuthorityConstants;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 实现功能【自定义 UserDetails】
 * <p>
 * 实现 Spring Security 的 UserDetails 接口
 * 存储用户信息（用户ID、用户名、角色、权限等）
 * 供 Spring Security 权限验证使用
 * <p>
 *
 * @author li.hongyu
 * @date 2025-12-09 10:49:30
 */
@Data
public class SecurityUser implements UserDetails, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long userId;
    private String username;
    private String password;
    private String realName;
    private Integer status;
    private Integer locked;
    private LocalDateTime lastLoginTime;
    private String lastLoginIp;
    private Set<String> roles = new HashSet<>();
    private Set<String> permissions = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Spring Security 约定角色加前缀，权限原样放行
        Set<GrantedAuthority> authorities = new HashSet<>();
        if (roles != null) {
            authorities.addAll(roles.stream()
                    .map(role -> new SimpleGrantedAuthority(UserAuthorityConstants.ROLE_PREFIX + role))
                    .toList());
        }
        if (permissions != null) {
            authorities.addAll(permissions.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet()));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return locked == null || locked == 0;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status == null || status == 1;
    }
}