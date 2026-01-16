package com.mms.common.core.enums.error;

import lombok.Getter;

/**
 * 实现功能【错误码枚举】
 * <p>
 * 设计原则：
 * - 按业务模块分类，每个模块分配一个码段
 * - 按错误类型分类，便于维护和查找
 * - 语义化命名，清晰表达错误含义，控制长度在合理范围内
 * </p>
 *
 * @author li.hongyu
 * @date 2025-10-28 20:11:55
 */
@Getter
public enum ErrorCode {
    
    // ==================== 用户相关错误 (1xxx) ====================
    // 用户认证错误 (1001-1010)
    USER_NOT_FOUND(1001, "用户不存在"),
    LOGIN_FAILED(1002, "用户名或密码错误"),
    LOGIN_EXPIRED(1003, "登录信息已过期，请重新登录"),
    INVALID_TOKEN(1004, "无效的token"),
    INVALID_AUTH_HEADER(1005, "无效的认证头"),
    
    // 用户注册错误 (1011-1020)
    USERNAME_EXISTS(1011, "用户名已存在"),
    EMAIL_EXISTS(1012, "邮箱已被注册"),
    PHONE_EXISTS(1013, "手机号码已存在"),
    PWD_MISMATCH(1014, "两次输入的密码不一致"),
    EMAIL_FORMAT_ERROR(1015, "邮箱格式不正确"),
    PHONE_FORMAT_ERROR(1016, "手机号格式不正确"),
    PWD_WEAK(1017, "密码强度不够"),
    PWD_EMPTY(1018, "密码不能为空"),
    PWD_INVALID(1019, "密码不合法"),
    
    // 用户状态错误 (1021-1030)
    ACCOUNT_DISABLED(1021, "账号已停用"),
    ACCOUNT_LOCKED(1022, "账号已锁定"),
    
    // ==================== 权限相关错误 (2xxx) ====================
    // 权限验证错误 (2001-2010)
    NO_PERMISSION(2001, "没有操作权限"),
    ACCESS_DENIED(2002, "访问被拒绝"),
    
    // 角色管理错误 (2011-2020)
    ROLE_NOT_FOUND(2011, "角色不存在"),
    ROLE_NAME_EXISTS(2012, "角色名称已存在"),
    ROLE_CODE_EXISTS(2013, "角色编码已存在"),
    ROLE_CODE_UPDATE_FORBIDDEN(2014, "角色编码不可修改"),
    ROLE_IN_USE(2015, "角色存在关联用户，无法删除"),

    // 权限管理错误 (2021-2030)
    PERMISSION_CODE_EXISTS(2021, "权限编码已存在"),
    PERMISSION_CODE_UPDATE_FORBIDDEN(2022, "权限编码不可修改"),
    CORE_PERMISSION_UPDATE_FORBIDDEN(2023, "系统核心权限不可修改"),
    CORE_PERMISSION_DELETE_FORBIDDEN(2023, "系统核心权限不可删除"),
    CORE_PERMISSION_SWITCH_FORBIDDEN(2023, "系统核心权限不可禁用"),
    
    // ==================== 系统管理错误 (3xxx) ====================
    // 文件处理错误 (3001-3010)
    FILE_EMPTY(3001, "上传文件为空"),
    FILE_FORMAT_ERROR(3002, "无法解析该类型的文件"),
    FILE_SIZE_EXCEEDED(3003, "文件大小超出限制"),
    FILE_UPLOAD_FAILED(3004, "文件上传失败"),
    
    // 数据导入导出错误 (3011-3020)
    IMPORT_TEMPLATE_ERROR(3011, "导入模板错误"),
    IMPORT_DATA_ERROR(3012, "导入数据错误"),
    FIELD_TRANSFORM_ERROR(3013, "字段转换失败"),
    EXPORT_DATA_ERROR(3014, "导出数据失败"),
    
    // 系统配置错误 (3021-3030)
    CONFIG_NOT_FOUND(3021, "配置项不存在"),
    CONFIG_VALUE_INVALID(3022, "配置值无效"),
    
    // ==================== 业务逻辑错误 (4xxx) ====================
    // 通用业务错误 (4001-4010)
    INVALID_OPERATION(4001, "非法操作"),
    OPERATION_NOT_ALLOWED(4002, "当前状态不允许此操作"),
    RESOURCE_NOT_FOUND(4003, "资源不存在"),
    RESOURCE_EXISTS(4004, "资源已存在"),
    DATA_IN_USE(4005, "数据正在使用中，无法删除"),
    DUPLICATE_SUBMIT(4006, "请勿重复提交"),
    DATA_MODIFIED(4007, "数据已被修改，请刷新后重试"),
    
    // 参数验证错误 (4011-4020)
    PARAM_INVALID(4011, "请求参数无效"),
    PARAM_MISSING(4012, "缺少必要参数"),
    PARAM_FORMAT_ERROR(4013, "参数格式错误"),
    PARAM_LENGTH_ERROR(4014, "参数长度不符合要求"),
    
    // 业务状态错误 (4021-4030)
    STATUS_INVALID(4021, "状态不合法"),
    STATUS_NOT_ALLOWED(4022, "当前状态不允许此操作"),
    WORKFLOW_ERROR(4023, "工作流处理失败"),
    
    // 数据完整性错误 (4031-4040)
    DATA_CONSTRAINT_ERROR(4031, "数据约束违反"),
    FOREIGN_KEY_ERROR(4032, "存在关联数据，无法删除"),
    UNIQUE_CONSTRAINT_ERROR(4033, "数据已存在，不能重复"),
    
    // 限流相关错误 (4041-4050)
    RATE_LIMIT_EXCEEDED(4041, "请求过于频繁，请稍后重试"),
    QUOTA_EXCEEDED(4042, "超出使用限制"),
    
    // ==================== 系统错误 (5xxx) ====================
    // 系统内部错误 (5001-5010)
    SYSTEM_ERROR(5001, "系统内部错误"),
    DB_ERROR(5002, "数据库操作失败"),
    NETWORK_ERROR(5003, "网络连接失败"),
    EXTERNAL_SERVICE_ERROR(5004, "外部服务调用失败"),
    CACHE_ERROR(5005, "缓存操作失败"),
    
    // 系统繁忙 (5011-5020)
    SYSTEM_BUSY(5011, "系统繁忙，请稍后重试"),
    SERVICE_UNAVAILABLE(5012, "服务暂时不可用"),
    TIMEOUT_ERROR(5013, "请求超时"),
    
    // 未知错误 (5099)
    UNKNOWN_ERROR(5099, "未知错误"),
    
    // ==================== HTTP 状态码相关错误 (6xxx) ====================
    // HTTP 状态码错误 (6001-6010)
    NOT_FOUND(6001, "接口不存在"),
    METHOD_NOT_ALLOWED(6002, "请求方法不被支持");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}