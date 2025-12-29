package com.mms.common.core.constants.usercenter;

/**
 * 实现功能【权限常量类】
 * <p>
 * 定义核心权限编码常量，避免硬编码，提供 IDE 代码提示和编译时检查。
 * <p>
 * <b>使用说明：</b>
 * <ul>
 *   <li>此常量类主要用于<strong>核心的、稳定的权限</strong>（如用户管理、角色管理等）</li>
 *   <li>新增权限时，<strong>可以直接在 {@code @RequiresPermission} 注解中使用字符串</strong>，无需每次都更新此常量类</li>
 *   <li>权限编码必须与数据库 {@code permission} 表中的 {@code permission_code} 字段保持一致</li>
 * </ul>
 * <p>
 * <b>新增权限流程：</b>
 * <ol>
 *   <li>在数据库 {@code permission} 表中添加权限记录（通过权限管理界面或 SQL）</li>
 *   <li>在 Controller 方法上使用 {@code @RequiresPermission("your:permission:code")} 注解</li>
 *   <li>（可选）如果该权限是核心权限，可在此常量类中添加常量定义</li>
 * </ol>
 * <p>
 * <b>示例：</b>
 * <pre>
 * // 方式1：使用常量（推荐用于核心权限）
 * &#64;RequiresPermission(PermissionConstants.USER_VIEW)
 * 
 * // 方式2：直接使用字符串（适用于新增权限）
 * &#64;RequiresPermission("order:view")
 * </pre>
 *
 * @author li.hongyu
 * @date 2025-12-09 11:39:08
 */
public final class PermissionConstants {

    /**
     * 系统管理（菜单）
     */
    public static final String SYSTEM_MANAGE = "system:manage";

    /**
     * 用户管理（菜单）
     */
    public static final String SYSTEM_USER_MANAGE = "system:user:manage";

    /**
     * 用户管理（按钮权限）
     */
    public static final String USER_VIEW = "user:view";
    public static final String USER_CREATE = "user:create";
    public static final String USER_UPDATE = "user:update";
    public static final String USER_DELETE = "user:delete";
    public static final String USER_RESET_PASSWORD = "user:reset-password";
    public static final String USER_UNLOCK = "user:unlock";

    /**
     * 角色管理（菜单）
     */
    public static final String SYSTEM_ROLE_MANAGE = "system:role:manage";

    /**
     * 角色管理（按钮权限）
     */
    public static final String ROLE_VIEW = "role:view";
    public static final String ROLE_CREATE = "role:create";
    public static final String ROLE_UPDATE = "role:update";
    public static final String ROLE_DELETE = "role:delete";
    public static final String ROLE_ASSIGN = "role:assign";

    /**
     * 菜单管理（菜单）
     */
    public static final String SYSTEM_MENU_MANAGE = "system:menu:manage";

    /**
     * 权限/菜单管理（按钮权限）
     */
    public static final String PERMISSION_VIEW = "permission:view";
    public static final String PERMISSION_CREATE = "permission:create";
    public static final String PERMISSION_UPDATE = "permission:update";
    public static final String PERMISSION_DELETE = "permission:delete";

    /**
     * 系统配置管理（菜单）
     */
    public static final String SYSTEM_CONFIG_MANAGE = "system:config:manage";

    /**
     * 系统配置管理（按钮权限）
     */
    public static final String CONFIG_VIEW = "config:view";
    public static final String CONFIG_CREATE = "config:create";
    public static final String CONFIG_UPDATE = "config:update";
    public static final String CONFIG_DELETE = "config:delete";

    /**
     * 数据字典管理（菜单）
     */
    public static final String SYSTEM_DICT_MANAGE = "system:dict:manage";

    /**
     * 数据字典管理（按钮权限）
     */
    public static final String DICT_VIEW = "dict:view";
    public static final String DICT_CREATE = "dict:create";
    public static final String DICT_UPDATE = "dict:update";
    public static final String DICT_DELETE = "dict:delete";

    /**
     * 私有构造函数，防止实例化
     */
    private PermissionConstants() {
        throw new UnsupportedOperationException("常量类不允许实例化");
    }
}

