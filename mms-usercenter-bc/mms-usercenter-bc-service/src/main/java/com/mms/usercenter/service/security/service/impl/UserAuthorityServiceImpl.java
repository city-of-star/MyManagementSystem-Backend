package com.mms.usercenter.service.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.mms.common.cache.constants.CacheTtl;
import com.mms.common.cache.utils.RedisUtils;
import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.usercenter.common.security.constants.UserAuthorityCacheKeyConstants;
import com.mms.common.core.exceptions.ServerException;
import com.mms.usercenter.common.auth.entity.UserEntity;
import com.mms.usercenter.common.auth.entity.UserRoleEntity;
import com.mms.usercenter.common.auth.entity.RolePermissionEntity;
import com.mms.usercenter.common.security.dto.SecurityUserDto;
import com.mms.usercenter.common.security.entity.SecurityUser;
import com.mms.usercenter.common.security.vo.UserAuthorityVo;
import com.mms.usercenter.service.auth.mapper.PermissionMapper;
import com.mms.usercenter.service.auth.mapper.RoleMapper;
import com.mms.usercenter.service.auth.mapper.RolePermissionMapper;
import com.mms.usercenter.service.auth.mapper.UserMapper;
import com.mms.usercenter.service.auth.mapper.UserRoleMapper;
import com.mms.usercenter.service.security.service.UserAuthorityService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 实现功能【用户权限服务实现类】
 * <p>
 * - 负责用户角色和权限的查询、缓存管理
 * </p>
 * @author li.hongyu
 * @date 2025-12-23 20:21:55
 */
@Slf4j
@Service
public class UserAuthorityServiceImpl implements UserAuthorityService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private PermissionMapper permissionMapper;

    @Resource
    private RolePermissionMapper rolePermissionMapper;

    /**
     * 根据用户名查询用户认证信息（带缓存）
     * @param username 用户名
     * @return 用户认证信息
     */
    @Override
    public SecurityUser getSecurityUserByUsername(String username) {
        try {
            // 从缓存中尝试获取 SecurityUser
            String cacheKey = UserAuthorityCacheKeyConstants.USER_AUTH_INFO_CACHE_PREFIX + username;
            SecurityUserDto cachedUser = RedisUtils.get(cacheKey, SecurityUserDto.class);
            if (cachedUser != null) {
                SecurityUser securityUser = new SecurityUser();
                securityUser.setUserId(cachedUser.getUserId());
                securityUser.setUsername(cachedUser.getUsername());
                securityUser.setPassword(cachedUser.getPassword());
                securityUser.setRealName(cachedUser.getRealName());
                securityUser.setStatus(cachedUser.getStatus());
                securityUser.setLocked(cachedUser.getLocked());
                securityUser.setLastLoginIp(cachedUser.getLastLoginIp());
                securityUser.setLastLoginTime(cachedUser.getLastLoginTime());
                return securityUser;
            }
            // 缓存未命中，查询数据库
            UserEntity user = userMapper.selectByUsername(username);
            if (user == null) {
                throw new BusinessException(ErrorCode.USER_NOT_FOUND);
            }
            // 构建 SecurityUser
            SecurityUser securityUser = new SecurityUser();
            securityUser.setUserId(user.getId());
            securityUser.setUsername(user.getUsername());
            securityUser.setPassword(user.getPassword());
            securityUser.setRealName(user.getRealName());
            securityUser.setStatus(user.getStatus());
            securityUser.setLocked(user.getLocked());
            securityUser.setLastLoginIp(user.getLastLoginIp());
            securityUser.setLastLoginTime(user.getLastLoginTime());
            // 构建 SecurityUserDto
            SecurityUserDto securityUserDto = new SecurityUserDto();
            securityUserDto.setUserId(user.getId());
            securityUserDto.setUsername(user.getUsername());
            securityUserDto.setPassword(user.getPassword());
            securityUserDto.setRealName(user.getRealName());
            securityUserDto.setStatus(user.getStatus());
            securityUserDto.setLocked(user.getLocked());
            securityUserDto.setLastLoginIp(user.getLastLoginIp());
            securityUserDto.setLastLoginTime(user.getLastLoginTime());
            // 写入缓存
            RedisUtils.set(cacheKey, securityUserDto, CacheTtl.LONG_SECONDS);
            return securityUser;
        } catch (Exception e) {
            throw new ServerException("获取用户认证信息失败", e);
        }
    }

    /**
     * 获取用户权限信息（包含角色和权限）
     *
     * @param username 用户名
     * @return UserAuthorityVo 用户权限信息对象
     */
    @Override
    @Transactional(readOnly = true)
    public UserAuthorityVo getUserAuthorities(String username) {
        try {
            UserAuthorityVo vo = new UserAuthorityVo();
            vo.setRoles(loadUserRoles(username));
            vo.setPermissions(loadUserPermissions(username));
            return vo;
        } catch (Exception e) {
            throw new ServerException("获取用户权限信息失败", e);
        }
    }

    /**
     * 清除指定用户的认证信息缓存
     * @param username 用户名
     */
    @Override
    public void clearSecurityUserByUsername(String username) {
        String cacheKey = UserAuthorityCacheKeyConstants.USER_AUTH_INFO_CACHE_PREFIX + username;
        RedisUtils.delete(cacheKey);
    }

    /**
     * 加载用户角色集合
     * 采用缓存优先策略：先查缓存，缓存不存在再查数据库
     *
     * @param username 用户名
     * @return 用户角色编码集合
     */
    private Set<String> loadUserRoles(String username) {
        if (!StringUtils.hasText(username)) {
            return Collections.emptySet();
        }
        // 构建用户角色缓存键
        String cacheKey = UserAuthorityCacheKeyConstants.USER_ROLE_PREFIX + username;
        // 尝试从缓存获取
        Set<String> cachedRoles = RedisUtils.get(cacheKey, new TypeReference<Set<String>>(){});
        if (!CollectionUtils.isEmpty(cachedRoles)) {
            return cachedRoles;
        }
        // 缓存未命中，查询数据库
        List<String> roleCodeList = roleMapper.selectRoleCodesByUsername(username);
        // 过滤并转换成Set集合
        Set<String> roleCodes = roleCodeList.stream()
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());
        // 写入缓存
        RedisUtils.set(cacheKey, roleCodes, CacheTtl.LONG_SECONDS);
        return roleCodes;
    }

    /**
     * 加载用户权限集合
     * 采用缓存优先策略：先查缓存，缓存不存在再查数据库
     *
     * @param username 用户名
     * @return 用户权限编码集合
     */
    private Set<String> loadUserPermissions(String username) {
        if (!StringUtils.hasText(username)) {
            return Collections.emptySet();
        }
        // 构建用户权限缓存键
        String cacheKey = UserAuthorityCacheKeyConstants.USER_PERMISSION_PREFIX + username;
        // 尝试从缓存获取
        Set<String> cachedPermissions = RedisUtils.get(cacheKey, new TypeReference<Set<String>>(){});
        if (!CollectionUtils.isEmpty(cachedPermissions)) {
            return cachedPermissions;
        }
        // 缓存未命中，查询数据库
        List<String> permissionCodeList = permissionMapper.selectPermissionCodesByUsername(username);
        // 过滤并转换数据
        Set<String> permissionCodes = permissionCodeList.stream()
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());
        // 写入缓存
        RedisUtils.set(cacheKey, permissionCodes, CacheTtl.LONG_SECONDS);
        return permissionCodes;
    }

    /**
     * 清除指定用户的权限缓存
     *
     * @param userId 用户ID
     */
    @Override
    public void clearUserAuthorityCacheByUserId(Long userId) {
        try {
            UserEntity user = userMapper.selectById(userId);
            if (user == null) {
                return;
            }
            String username = user.getUsername();
            if (StringUtils.hasText(username)) {
                String roleCacheKey = UserAuthorityCacheKeyConstants.USER_ROLE_PREFIX + username;
                String permissionCacheKey = UserAuthorityCacheKeyConstants.USER_PERMISSION_PREFIX + username;
                RedisUtils.delete(roleCacheKey);
                RedisUtils.delete(permissionCacheKey);
                log.info("已清除用户 {} 的权限缓存", username);
            }
        } catch (Exception e) {
            // 缓存清除失败不影响主流程，只记录日志
            log.error("清除用户 {} 的权限缓存失败：{}", userId, e.getMessage(), e);
        }
    }

    /**
     * 清除拥有指定角色的所有用户的权限缓存
     *
     * @param roleId 角色ID
     */
    public void clearUserAuthorityCacheByRoleId(Long roleId) {
        try {
            if (roleId == null) {
                return;
            }
            // 查询拥有该角色的所有用户
            LambdaQueryWrapper<UserRoleEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserRoleEntity::getRoleId, roleId);
            List<UserRoleEntity> userRoleList = userRoleMapper.selectList(wrapper);
            if (CollectionUtils.isEmpty(userRoleList)) {
                return;
            }
            // 获取所有关联的用户ID
            List<Long> userIds = userRoleList.stream()
                    .map(UserRoleEntity::getUserId)
                    .distinct()
                    .toList();
            // 开始清除
            for (Long userId : userIds) {
                clearUserAuthorityCacheByUserId(userId);
            }
            log.info("已清除角色 {} 关联的 {} 个用户的权限缓存", roleId, userIds.size());
        } catch (Exception e) {
            // 缓存清除失败不影响主流程，只记录日志
            log.error("清除角色 {} 关联用户的权限缓存失败：{}", roleId, e.getMessage(), e);
        }
    }

    /**
     * 清除包含指定权限的所有角色下用户的权限缓存
     *
     * @param permissionId 权限ID
     */
    @Override
    public void clearUserAuthorityCacheByPermissionId(Long permissionId) {
        try {
            if (permissionId == null) {
                return;
            }
            // 查询拥有该权限的所有角色
            LambdaQueryWrapper<RolePermissionEntity> rpWrapper = new LambdaQueryWrapper<>();
            rpWrapper.eq(RolePermissionEntity::getPermissionId, permissionId);
            List<RolePermissionEntity> relations = rolePermissionMapper.selectList(rpWrapper);
            if (CollectionUtils.isEmpty(relations)) {
                return;
            }
            List<Long> roleIds = relations.stream()
                    .map(RolePermissionEntity::getRoleId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();
            // 开始清除
            for (Long roleId : roleIds) {
                clearUserAuthorityCacheByRoleId(roleId);
            }
            log.info("已清除权限 {} 关联的 {} 个角色下用户的权限缓存", permissionId, roleIds.size());
        } catch (Exception e) {
            // 缓存清除失败不影响主流程，只记录日志
            log.error("清除权限 {} 关联角色下用户的权限缓存失败：{}", permissionId, e.getMessage(), e);
        }
    }
}