package com.mms.usercenter.service.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mms.common.core.constants.usercenter.UserAuthorityConstants;
import com.mms.common.core.exceptions.ServerException;
import com.mms.usercenter.common.auth.entity.UserEntity;
import com.mms.usercenter.common.auth.entity.UserRoleEntity;
import com.mms.usercenter.common.security.vo.UserAuthorityVo;
import com.mms.usercenter.service.auth.mapper.PermissionMapper;
import com.mms.usercenter.service.auth.mapper.RoleMapper;
import com.mms.usercenter.service.auth.mapper.UserMapper;
import com.mms.usercenter.service.auth.mapper.UserRoleMapper;
import com.mms.usercenter.service.security.service.UserAuthorityService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
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
    private RedisTemplate<String, Object> redisTemplate;

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
     * 清除指定用户的权限缓存
     *
     * @param userId 用户ID
     */
    @Override
    public void clearUserAuthorityCacheByUserId(Long userId) {
        try {
            UserEntity user = userMapper.selectById(userId);
            if (user == null) {
                log.debug("用户 {} 不存在，无需清除缓存", userId);
                return;
            }
            String username = user.getUsername();
            if (StringUtils.hasText(username)) {
                String roleCacheKey = UserAuthorityConstants.USER_ROLE_PREFIX + username;
                String permissionCacheKey = UserAuthorityConstants.USER_PERMISSION_PREFIX + username;
                redisTemplate.delete(roleCacheKey);
                redisTemplate.delete(permissionCacheKey);
                log.info("已清除用户 {} 的权限缓存", username);
            }
        } catch (Exception e) {
            // 缓存清除失败不应该影响主流程，只记录日志
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
            // 查询拥有该角色的所有用户
            LambdaQueryWrapper<UserRoleEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserRoleEntity::getRoleId, roleId);
            List<UserRoleEntity> userRoleList = userRoleMapper.selectList(wrapper);

            if (CollectionUtils.isEmpty(userRoleList)) {
                log.debug("角色 {} 没有关联用户，无需清除缓存", roleId);
                return;
            }

            // 获取所有关联的用户ID
            List<Long> userIds = userRoleList.stream()
                    .map(UserRoleEntity::getUserId)
                    .distinct()
                    .toList();

            // 查询用户信息，获取用户名
            List<UserEntity> users = userMapper.selectBatchIds(userIds);
            if (CollectionUtils.isEmpty(users)) {
                log.warn("角色 {} 关联的用户不存在，userIdList={}", roleId, userIds);
                return;
            }

            // 清除每个用户的权限缓存
            for (UserEntity user : users) {
                if (user.getDeleted() != null && user.getDeleted() == 1) {
                    continue; // 跳过已删除的用户
                }
                String username = user.getUsername();
                if (StringUtils.hasText(username)) {
                    String roleCacheKey = UserAuthorityConstants.USER_ROLE_PREFIX + username;
                    String permissionCacheKey = UserAuthorityConstants.USER_PERMISSION_PREFIX + username;
                    redisTemplate.delete(roleCacheKey);
                    redisTemplate.delete(permissionCacheKey);
                    log.debug("已清除用户 {} 的权限缓存（角色：{}）", username, roleId);
                }
            }
            log.info("已清除角色 {} 关联的 {} 个用户的权限缓存", roleId, users.size());
        } catch (Exception e) {
            // 缓存清除失败不应该影响主流程，只记录日志
            log.error("清除角色 {} 关联用户的权限缓存失败：{}", roleId, e.getMessage(), e);
        }
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

        // 构建缓存键
        String cacheKey = UserAuthorityConstants.USER_ROLE_PREFIX + username;

        // 先尝试从缓存获取
        Set<String> cachedRoles = convertToStringSet(redisTemplate.opsForValue().get(cacheKey));
        if (!CollectionUtils.isEmpty(cachedRoles)) {
            return cachedRoles;
        }

        // 缓存未命中，查询数据库
        List<String> roleCodeList = roleMapper.selectRoleCodesByUsername(username);
        if (CollectionUtils.isEmpty(roleCodeList)) {
            // 空结果也进行缓存，防止缓存穿透
            cacheEmptySet(cacheKey);
            return Collections.emptySet();
        }

        // 过滤并转换数据
        Set<String> roleCodes = roleCodeList.stream()
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());

        // 写入缓存
        cacheSet(roleCodes, cacheKey);
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

        // 构建缓存键
        String cacheKey = UserAuthorityConstants.USER_PERMISSION_PREFIX + username;

        // 先尝试从缓存获取
        Set<String> cachedPermissions = convertToStringSet(redisTemplate.opsForValue().get(cacheKey));
        if (!CollectionUtils.isEmpty(cachedPermissions)) {
            return cachedPermissions;
        }

        // 缓存未命中，查询数据库
        List<String> permissionCodeList = permissionMapper.selectPermissionCodesByUsername(username);
        if (CollectionUtils.isEmpty(permissionCodeList)) {
            // 空结果也进行缓存，防止缓存穿透
            cacheEmptySet(cacheKey);
            return Collections.emptySet();
        }

        // 过滤并转换数据
        Set<String> permissionCodes = permissionCodeList.stream()
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());

        // 写入缓存
        cacheSet(permissionCodes, cacheKey);
        return permissionCodes;
    }

    /**
     * 将缓存对象转换为字符串集合
     * 支持Set、List和单对象等多种格式的转换
     *
     * @param cached 缓存对象
     * @return 字符串集合
     */
    private Set<String> convertToStringSet(Object cached) {
        if (cached == null) {
            return Collections.emptySet();
        }

        // 处理Set类型
        if (cached instanceof Set<?> set) {
            return set.stream()
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .collect(Collectors.toSet());
        }

        // 处理List类型
        if (cached instanceof List<?> list) {
            return list.stream()
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .collect(Collectors.toSet());
        }

        // 处理单对象类型
        return Collections.singleton(cached.toString());
    }

    /**
     * 缓存集合数据
     * 支持同时缓存到多个key（批量操作）
     *
     * @param values 要缓存的值集合
     * @param keys 缓存键（可变参数）
     */
    private void cacheSet(Set<String> values, String... keys) {
        if (values == null) {
            values = Collections.emptySet();
        }

        for (String key : keys) {
            redisTemplate.opsForValue().set(
                    key,
                    values,
                    UserAuthorityConstants.AUTHORITY_CACHE_TTL_MINUTES,
                    TimeUnit.MINUTES
            );
        }
    }

    /**
     * 缓存空集合
     * 用于防止缓存穿透，对空结果也进行缓存
     *
     * @param keys 缓存键（可变参数）
     */
    private void cacheEmptySet(String... keys) {
        for (String key : keys) {
            redisTemplate.opsForValue().set(
                    key,
                    new HashSet<>(),
                    UserAuthorityConstants.AUTHORITY_CACHE_TTL_MINUTES,
                    TimeUnit.MINUTES
            );
        }
    }
}