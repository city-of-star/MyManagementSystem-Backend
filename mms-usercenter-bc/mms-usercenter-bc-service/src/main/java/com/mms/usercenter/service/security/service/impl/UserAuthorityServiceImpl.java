package com.mms.usercenter.service.security.service.impl;

import com.mms.common.core.constants.usercenter.UserAuthorityConstants;
import com.mms.usercenter.common.security.vo.UserAuthorityVo;
import com.mms.usercenter.service.auth.mapper.PermissionMapper;
import com.mms.usercenter.service.auth.mapper.RoleMapper;
import com.mms.usercenter.service.security.service.UserAuthorityService;
import jakarta.annotation.Resource;
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
@Service
public class UserAuthorityServiceImpl implements UserAuthorityService {

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private PermissionMapper permissionMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取用户权限信息（包含角色和权限）
     * 采用读写分离的事务，提高查询性能
     *
     * @param userId 用户ID
     * @return UserAuthorityVo 用户权限信息对象
     */
    @Override
    @Transactional(readOnly = true)
    public UserAuthorityVo getUserAuthorities(Long userId) {
        UserAuthorityVo vo = new UserAuthorityVo();
        vo.setUserId(userId);
        vo.setRoles(loadUserRoles(userId));
        vo.setPermissions(loadUserPermissions(userId));
        return vo;
    }

    /**
     * 加载用户角色集合
     * 采用缓存优先策略：先查缓存，缓存不存在再查数据库
     *
     * @param userId 用户ID
     * @return 用户角色编码集合
     */
    private Set<String> loadUserRoles(Long userId) {
        if (userId == null) {
            return Collections.emptySet();
        }

        // 构建缓存键
        String cacheKey = UserAuthorityConstants.USER_ROLE_PREFIX + userId;

        // 先尝试从缓存获取
        Set<String> cachedRoles = convertToStringSet(redisTemplate.opsForValue().get(cacheKey));
        if (!CollectionUtils.isEmpty(cachedRoles)) {
            return cachedRoles;
        }

        // 缓存未命中，查询数据库
        List<String> roleCodeList = roleMapper.selectRoleCodesByUserId(userId);
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
     * @param userId 用户ID
     * @return 用户权限编码集合
     */
    private Set<String> loadUserPermissions(Long userId) {
        if (userId == null) {
            return Collections.emptySet();
        }

        // 构建缓存键
        String cacheKey = UserAuthorityConstants.USER_PERMISSION_PREFIX + userId;

        // 先尝试从缓存获取
        Set<String> cachedPermissions = convertToStringSet(redisTemplate.opsForValue().get(cacheKey));
        if (!CollectionUtils.isEmpty(cachedPermissions)) {
            return cachedPermissions;
        }

        // 缓存未命中，查询数据库
        List<String> permissionCodeList = permissionMapper.selectPermissionCodesByUserId(userId);
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
                    UserAuthorityConstants.ROLE_PERMISSION_CACHE_TTL_MINUTES,
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
                    UserAuthorityConstants.ROLE_PERMISSION_CACHE_TTL_MINUTES,
                    TimeUnit.MINUTES
            );
        }
    }
}