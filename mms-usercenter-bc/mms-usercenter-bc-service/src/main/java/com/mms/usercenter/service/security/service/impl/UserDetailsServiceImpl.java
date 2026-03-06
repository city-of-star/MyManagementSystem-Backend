package com.mms.usercenter.service.security.service.impl;

import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.usercenter.common.security.dto.SecurityUserDto;
import com.mms.usercenter.common.security.entity.SecurityUser;
import com.mms.usercenter.common.security.vo.UserAuthorityVo;
import com.mms.usercenter.service.auth.mapper.UserMapper;
import com.mms.usercenter.service.security.service.UserAuthorityService;
import jakarta.annotation.Resource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 实现功能【用户详情服务实现类】
 * <p>
 * 实现 Spring Security 的 UserDetailsService 接口
 * 根据用户名从数据库加载用户信息，并缓存用户基本信息和权限数据
 * 返回 SecurityUser 对象
 * <p>
 *
 * @author li.hongyu
 * @date 2025-12-09 11:47:37
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserAuthorityService userAuthorityService;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!StringUtils.hasText(username)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "用户名不能为空");
        }
        SecurityUser securityUser = new SecurityUser();
        // 用户认证信息查询（带缓存）
        SecurityUserDto securityUserDto = userAuthorityService.getSecurityUserDtoByUsername(username);
        securityUser.setUserId(securityUserDto.getUserId());
        securityUser.setUsername(securityUserDto.getUsername());
        securityUser.setPassword(securityUserDto.getPassword());
        securityUser.setRealName(securityUserDto.getRealName());
        securityUser.setStatus(securityUserDto.getStatus());
        securityUser.setLocked(securityUserDto.getLocked());
        securityUser.setLastLoginIp(securityUserDto.getLastLoginIp());
        securityUser.setLastLoginTime(securityUserDto.getLastLoginTime());
        // 用户角色、权限查询（带缓存）
        UserAuthorityVo authorities = userAuthorityService.getUserAuthorities(username);
        securityUser.setRoles(authorities.getRoles());
        securityUser.setPermissions(authorities.getPermissions());
        return securityUser;
    }
}