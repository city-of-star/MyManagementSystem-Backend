package com.mms.usercenter.common.security.constants;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 超级管理员信息常量类
 * <p>
 * 用于定义超级管理员的用户ID和角色ID，这些值在建库时确定，一般不会变动。
 * 在进行删除、禁用等敏感操作时，通过判断这些常量来防止误操作，保护系统核心账户和角色。
 * </p>
 *
 * @author li.hongyu
 * @date 2026-01-16 10:33:09
 */
public class SuperAdminInfoConstants {

    /**
     * 超级管理员用户ID
     * <p>
     * 系统初始化时创建的第一个用户，拥有系统最高权限
     * 此用户不能被修改、删除、禁用、锁定，且必须拥有超级管理员角色
     * </p>
     */
    public static final Long SUPER_ADMIN_USER_ID = 1L;

    /**
     * 超级管理员角色ID
     * <p>
     * 系统初始化时创建的第一个角色，拥有系统所有权限
     * 此角色不能被修改、删除、禁用
     * </p>
     */
    public static final Long SUPER_ADMIN_ROLE_ID = 1L;

    /**
     * 系统核心权限ID集合
     * <p>
     * 系统初始化时创建的权限ID集合（1-29）
     * 同时也是超级管理员角色必须拥有的权限
     * 这些权限不能被修改、删除、禁用
     * </p>
     */
    public static final Set<Long> SYSTEM_CORE_PERMISSION_IDS = IntStream.rangeClosed(1, 29)
            .mapToObj(Long::valueOf)
            .collect(Collectors.toUnmodifiableSet());

    /**
     * 判断指定权限ID是否为系统核心权限
     *
     * @param permissionId 权限ID
     * @return true-是系统核心权限，false-不是系统核心权限
     */
    public static boolean isCorePermission(Long permissionId) {
        if (permissionId == null) {
            return false;
        }
        return SYSTEM_CORE_PERMISSION_IDS.contains(permissionId);
    }

    /**
     * 私有构造函数，防止实例化
     */
    private SuperAdminInfoConstants() {
        throw new UnsupportedOperationException("常量类不允许实例化");
    }
}