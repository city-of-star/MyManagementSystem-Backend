package com.mms.common.webmvc.audit;

import com.mms.common.security.servlet.constants.PermissionConstants;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 实现功能【操作日志权限码映射表】
 * <p>
 * 将 {@link PermissionConstants} 中的写操作权限码映射为审计展示字段。
 * TODO: 细化权限编码（如 assign-roles、switch-status 等独立 button 权限），提升 operation_desc 准确度。
 * </p>
 *
 * @author li.hongyu
 * @date 2026-05-29 14:00:00
 */
public final class OperationLogPermissionMappings {

    private static final Map<String, OperationLogPermissionMeta> MAPPINGS;

    static {
        Map<String, OperationLogPermissionMeta> map = new LinkedHashMap<>();
        // 用户管理
        put(map, PermissionConstants.SYSTEM_USER_CREATE, "用户管理", "create", "新增用户");
        put(map, PermissionConstants.SYSTEM_USER_UPDATE, "用户管理", "update", "修改用户");
        put(map, PermissionConstants.SYSTEM_USER_DELETE, "用户管理", "delete", "删除用户");
        put(map, PermissionConstants.SYSTEM_USER_RESET_PASSWORD, "用户管理", "update", "重置用户密码");
        put(map, PermissionConstants.SYSTEM_USER_UNLOCK, "用户管理", "update", "解锁用户");
        // 角色管理
        put(map, PermissionConstants.SYSTEM_ROLE_CREATE, "角色管理", "create", "新增角色");
        put(map, PermissionConstants.SYSTEM_ROLE_UPDATE, "角色管理", "update", "修改角色");
        put(map, PermissionConstants.SYSTEM_ROLE_DELETE, "角色管理", "delete", "删除角色");
        put(map, PermissionConstants.SYSTEM_ROLE_ASSIGN, "角色管理", "assign", "分配角色权限");
        // 菜单管理
        put(map, PermissionConstants.SYSTEM_PERMISSION_CREATE, "菜单管理", "create", "新增菜单");
        put(map, PermissionConstants.SYSTEM_PERMISSION_UPDATE, "菜单管理", "update", "修改菜单");
        put(map, PermissionConstants.SYSTEM_PERMISSION_DELETE, "菜单管理", "delete", "删除菜单");
        // 部门管理
        put(map, PermissionConstants.SYSTEM_DEPT_CREATE, "部门管理", "create", "新增部门");
        put(map, PermissionConstants.SYSTEM_DEPT_UPDATE, "部门管理", "update", "修改部门");
        put(map, PermissionConstants.SYSTEM_DEPT_DELETE, "部门管理", "delete", "删除部门");
        // 岗位管理
        put(map, PermissionConstants.SYSTEM_POST_CREATE, "岗位管理", "create", "新增岗位");
        put(map, PermissionConstants.SYSTEM_POST_UPDATE, "岗位管理", "update", "修改岗位");
        put(map, PermissionConstants.SYSTEM_POST_DELETE, "岗位管理", "delete", "删除岗位");
        // 系统配置
        put(map, PermissionConstants.SYSTEM_CONFIG_CREATE, "系统配置", "create", "新增系统配置");
        put(map, PermissionConstants.SYSTEM_CONFIG_UPDATE, "系统配置", "update", "修改系统配置");
        put(map, PermissionConstants.SYSTEM_CONFIG_DELETE, "系统配置", "delete", "删除系统配置");
        // 数据字典
        put(map, PermissionConstants.SYSTEM_DICT_CREATE, "数据字典", "create", "新增字典");
        put(map, PermissionConstants.SYSTEM_DICT_UPDATE, "数据字典", "update", "修改字典");
        put(map, PermissionConstants.SYSTEM_DICT_DELETE, "数据字典", "delete", "删除字典");
        // 附件管理
        put(map, PermissionConstants.SYSTEM_ATTACHMENT_UPLOAD, "附件管理", "create", "上传附件");
        put(map, PermissionConstants.SYSTEM_ATTACHMENT_UPDATE, "附件管理", "update", "修改附件");
        put(map, PermissionConstants.SYSTEM_ATTACHMENT_DELETE, "附件管理", "delete", "删除附件");
        put(map, PermissionConstants.SYSTEM_ATTACHMENT_DOWNLOAD, "附件管理", "export", "下载附件");
        // 定时任务
        put(map, PermissionConstants.JOB_MANAGEMENT_CREATE, "定时任务", "create", "新增定时任务");
        put(map, PermissionConstants.JOB_MANAGEMENT_UPDATE, "定时任务", "update", "修改定时任务");
        put(map, PermissionConstants.JOB_MANAGEMENT_DELETE, "定时任务", "delete", "删除定时任务");
        put(map, PermissionConstants.JOB_MANAGEMENT_RUN, "定时任务", "update", "立即执行任务");
        // 定时任务执行记录
        put(map, PermissionConstants.JOB_RUN_LOG_DELETE, "定时任务执行记录", "delete", "删除执行记录");
        put(map, PermissionConstants.JOB_RUN_LOG_EXPORT, "定时任务执行记录", "export", "导出执行记录");
        put(map, PermissionConstants.JOB_RUN_LOG_RETRY, "定时任务执行记录", "update", "重试执行任务");
        put(map, PermissionConstants.JOB_RUN_LOG_TERMINATE, "定时任务执行记录", "update", "终止任务执行");
        // 登录日志
        put(map, PermissionConstants.AUDIT_LOGIN_LOG_DELETE, "登录日志", "delete", "删除登录日志");
        put(map, PermissionConstants.AUDIT_LOGIN_LOG_EXPORT, "登录日志", "export", "导出登录日志");
        // 操作日志
        put(map, PermissionConstants.AUDIT_OPERATION_LOG_DELETE, "操作日志", "delete", "删除操作日志");
        put(map, PermissionConstants.AUDIT_OPERATION_LOG_EXPORT, "操作日志", "export", "导出操作日志");
        // 异常日志
        put(map, PermissionConstants.AUDIT_EXCEPTION_LOG_DELETE, "异常日志", "delete", "删除异常日志");
        put(map, PermissionConstants.AUDIT_EXCEPTION_LOG_RESOLVE, "异常日志", "update", "标记异常已处理");
        // 接口访问日志
        put(map, PermissionConstants.AUDIT_API_ACCESS_LOG_DELETE, "接口访问日志", "delete", "删除接口访问日志");
        put(map, PermissionConstants.AUDIT_API_ACCESS_LOG_EXPORT, "接口访问日志", "export", "导出接口访问日志");
        put(map, PermissionConstants.AUDIT_API_ACCESS_LOG_ANALYZE, "接口访问日志", "export", "接口访问统计分析");
        // 在线用户
        put(map, PermissionConstants.SECURITY_ONLINE_USER_FORCE_LOGOUT, "在线用户", "logout", "强制用户下线");
        MAPPINGS = Collections.unmodifiableMap(map);
    }

    /**
     * 根据权限码解析操作日志元数据
     */
    public static Optional<OperationLogPermissionMeta> resolve(String permissionCode) {
        if (permissionCode == null || permissionCode.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(MAPPINGS.get(permissionCode));
    }

    private static void put(Map<String, OperationLogPermissionMeta> map,
                            String permissionCode,
                            String module,
                            String operationType,
                            String operationDesc) {
        map.put(permissionCode, new OperationLogPermissionMeta(module, operationType, operationDesc));
    }

    private OperationLogPermissionMappings() {
        throw new UnsupportedOperationException("常量类不允许实例化");
    }
}
