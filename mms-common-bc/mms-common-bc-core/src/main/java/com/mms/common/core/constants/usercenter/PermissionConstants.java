package com.mms.common.core.constants.usercenter;

/**
 * 实现功能【权限常量类】
 * <p>
 * 定义核心权限编码常量，避免硬编码，提供 IDE 代码提示和编译时检查。
 * 
 * 使用说明：
 * - 此常量类主要用于核心的、稳定的权限（如用户管理、角色管理等）
 * - 新增权限时，可以直接在 @RequiresPermission 注解中使用字符串，无需每次都更新此常量类
 * - 权限编码必须与数据库 permission 表中的 permission_code 字段保持一致
 * 新增权限流程：
 * - 在数据库 permission 表中添加权限记录（通过权限管理界面或 SQL）
 * - 在 Controller 方法上使用 @RequiresPermission("your:permission:code") 注解
 * - 如果该权限是核心权限，可在此常量类中添加常量定义
 * 示例：
 * // 方式1：使用常量（用于核心权限）
 * @RequiresPermission(PermissionConstants.USER_VIEW)
 * // 方式2：直接使用字符串（用于新增权限）
 * @RequiresPermission("order:view")
 * <p>
 *
 * @author li.hongyu
 * @date 2025-12-09 11:39:08
 */
public final class PermissionConstants {

    /**
     * 系统管理（目录）
     */
    public static final String SYSTEM_MANAGE = "SYSTEM";

    /**
     * 用户管理（菜单+按钮）
     */
    public static final String SYSTEM_USER_MANAGE = "SYSTEM_USER";
    public static final String USER_VIEW = "SYSTEM_USER_VIEW";
    public static final String USER_CREATE = "SYSTEM_USER_CREATE";
    public static final String USER_UPDATE = "SYSTEM_USER_UPDATE";
    public static final String USER_DELETE = "SYSTEM_USER_DELETE";
    public static final String USER_RESET_PASSWORD = "SYSTEM_USER_RESET_PASSWORD";
    public static final String USER_UNLOCK = "SYSTEM_USER_UNLOCK";

    /**
     * 角色管理（菜单+按钮）
     */
    public static final String SYSTEM_ROLE_MANAGE = "SYSTEM_ROLE";
    public static final String ROLE_VIEW = "SYSTEM_ROLE_VIEW";
    public static final String ROLE_CREATE = "SYSTEM_ROLE_CREATE";
    public static final String ROLE_UPDATE = "SYSTEM_ROLE_UPDATE";
    public static final String ROLE_DELETE = "SYSTEM_ROLE_DELETE";
    public static final String ROLE_ASSIGN = "SYSTEM_ROLE_ASSIGN";

    /**
     * 菜单管理（菜单+按钮）
     */
    public static final String SYSTEM_MENU_MANAGE = "SYSTEM_MENU";
    public static final String PERMISSION_VIEW = "SYSTEM_MENU_VIEW";
    public static final String PERMISSION_CREATE = "SYSTEM_MENU_CREATE";
    public static final String PERMISSION_UPDATE = "SYSTEM_MENU_UPDATE";
    public static final String PERMISSION_DELETE = "SYSTEM_MENU_DELETE";

    /**
     * 部门管理（菜单+按钮）
     */
    public static final String SYSTEM_DEPT_MANAGE = "SYSTEM_DEPT";
    public static final String DEPT_VIEW = "SYSTEM_DEPT_VIEW";
    public static final String DEPT_CREATE = "SYSTEM_DEPT_CREATE";
    public static final String DEPT_UPDATE = "SYSTEM_DEPT_UPDATE";
    public static final String DEPT_DELETE = "SYSTEM_DEPT_DELETE";

    /**
     * 岗位管理（菜单+按钮）
     */
    public static final String SYSTEM_POST_MANAGE = "SYSTEM_POST";
    public static final String POST_VIEW = "SYSTEM_POST_VIEW";
    public static final String POST_CREATE = "SYSTEM_POST_CREATE";
    public static final String POST_UPDATE = "SYSTEM_POST_UPDATE";
    public static final String POST_DELETE = "SYSTEM_POST_DELETE";

    /**
     * 系统配置管理（菜单+按钮）
     */
    public static final String SYSTEM_CONFIG_MANAGE = "SYSTEM_CONFIG";
    public static final String CONFIG_VIEW = "SYSTEM_CONFIG_VIEW";
    public static final String CONFIG_CREATE = "SYSTEM_CONFIG_CREATE";
    public static final String CONFIG_UPDATE = "SYSTEM_CONFIG_UPDATE";
    public static final String CONFIG_DELETE = "SYSTEM_CONFIG_DELETE";

    /**
     * 数据字典管理（菜单+按钮）
     */
    public static final String SYSTEM_DICT_MANAGE = "SYSTEM_DICT";
    public static final String DICT_VIEW = "SYSTEM_DICT_VIEW";
    public static final String DICT_CREATE = "SYSTEM_DICT_CREATE";
    public static final String DICT_UPDATE = "SYSTEM_DICT_UPDATE";
    public static final String DICT_DELETE = "SYSTEM_DICT_DELETE";

    /**
     * 附件管理（菜单+按钮）
     */
    public static final String SYSTEM_ATTACHMENT_MANAGE = "SYSTEM_ATTACHMENT";
    public static final String ATTACHMENT_VIEW = "SYSTEM_ATTACHMENT_VIEW";
    public static final String ATTACHMENT_UPLOAD = "SYSTEM_ATTACHMENT_UPLOAD";
    public static final String ATTACHMENT_UPDATE = "SYSTEM_ATTACHMENT_UPDATE";
    public static final String ATTACHMENT_DELETE = "SYSTEM_ATTACHMENT_DELETE";
    public static final String ATTACHMENT_DOWNLOAD = "SYSTEM_ATTACHMENT_DOWNLOAD";

    /**
     * 私有构造函数，防止实例化
     */
    private PermissionConstants() {
        throw new UnsupportedOperationException("常量类不允许实例化");
    }
}

