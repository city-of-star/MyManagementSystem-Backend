package com.mms.usercenter.service.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mms.common.cache.constants.CacheNameConstants;
import com.mms.common.core.exceptions.ServerException;
import com.mms.usercenter.common.auth.entity.UserEntity;
import com.mms.usercenter.common.auth.entity.UserRoleEntity;
import com.mms.usercenter.common.auth.entity.RolePermissionEntity;
import com.mms.common.security.vo.UserAuthorityVo;
import com.mms.usercenter.service.auth.mapper.PermissionMapper;
import com.mms.usercenter.service.auth.mapper.RoleMapper;
import com.mms.usercenter.service.auth.mapper.RolePermissionMapper;
import com.mms.usercenter.service.auth.mapper.UserMapper;
import com.mms.usercenter.service.auth.mapper.UserRoleMapper;
import com.mms.usercenter.service.security.service.UserAuthorityService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
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
 * 负责用户认证信息、角色、权限的查询和缓存管理
 * </p>
 * @author li.hongyu
 * @date 2025-12-23 20:21:55
 */
@Slf4j
@Service
public class UserAuthorityServiceImpl implements UserAuthorityService {

    /**
     * 通过代理对象触发 Spring Cache AOP（避免类内自调用导致 @CacheEvict 不生效）
     */
    @Resource
    @Lazy
    private UserAuthorityService userAuthorityServiceProxy;

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
     * 根据用户名查询用户角色和权限（带缓存）
     *
     * @param username 用户名
     * @return UserAuthorityVo 用户权限信息对象
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNameConstants.UserCenter.USER_AUTHORITY, key = "#username", unless = "#result == null")
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
     * 清除指定用户的角色和权限缓存（通过用户名）
     *
     * @param username 用户名
     */
    @Override
    @CacheEvict(cacheNames = CacheNameConstants.UserCenter.USER_AUTHORITY, key = "#username")
    public void clearUserAuthorityCacheByUsername(String username) {}

    /**
     * 清除指定用户的权限缓存（通过用户ID）
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
                userAuthorityServiceProxy.clearUserAuthorityCacheByUsername(username);
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
    @Override
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

    /**
     * 加载用户角色列表
     *
     * @param username 用户名
     * @return 用户角色编码列表
     */
    public Set<String> loadUserRoles(String username) {
        if (!StringUtils.hasText(username)) {
            return Collections.emptySet();
        }
        // 查询角色列表
        List<String> roleCodeList = roleMapper.selectRoleCodesByUsername(username);
        // 过滤并转换成Set集合
        return roleCodeList.stream()
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());
    }

    /**
     * 加载用户权限列表
     *
     * @param username 用户名
     * @return 用户权限编码列表
     */
    public Set<String> loadUserPermissions(String username) {
        if (!StringUtils.hasText(username)) {
            return Collections.emptySet();
        }
        // 查询权限列表
        List<String> permissionCodeList = permissionMapper.selectPermissionCodesByUsername(username);
        // 过滤并转换数据
        return permissionCodeList.stream()
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());
    }
}