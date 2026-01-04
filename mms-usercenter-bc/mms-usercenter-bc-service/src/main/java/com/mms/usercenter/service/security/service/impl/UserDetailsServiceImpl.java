package com.mms.usercenter.service.security.service.impl;

import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.usercenter.common.auth.entity.UserEntity;
import com.mms.usercenter.common.security.entity.SecurityUser;
import com.mms.usercenter.service.auth.mapper.UserMapper;
import com.mms.usercenter.common.security.vo.UserAuthorityVo;
import com.mms.usercenter.service.security.service.UserAuthorityService;
import jakarta.annotation.Resource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 实现功能【用户详情服务实现类】
 * <p>
 * 实现 Spring Security 的 UserDetailsService 接口
 * 根据用户名从数据库加载用户信息
 * 加载用户角色和权限
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
        UserEntity user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }

        SecurityUser securityUser = new SecurityUser();
        securityUser.setUserId(user.getId());
        securityUser.setUsername(user.getUsername());
        securityUser.setPassword(user.getPassword());
        securityUser.setRealName(user.getRealName());
        securityUser.setStatus(user.getStatus());
        securityUser.setLocked(user.getLocked());
        securityUser.setLastLoginIp(user.getLastLoginIp());
        securityUser.setLastLoginTime(user.getLastLoginTime());

        // 角色、权限查询（缓存）
        UserAuthorityVo authorities = userAuthorityService.getUserAuthorities(user.getUsername());
        securityUser.setRoles(authorities.getRoles());
        securityUser.setPermissions(authorities.getPermissions());

        return securityUser;
    }
}