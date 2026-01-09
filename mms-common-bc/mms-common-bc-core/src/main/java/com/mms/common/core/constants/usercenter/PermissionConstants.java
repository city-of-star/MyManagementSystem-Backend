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
     * 系统管理（目录）
     * <p>编码示例：SYSTEM</p>
     */
    public static final String SYSTEM_MANAGE = "SYSTEM";

    /**
     * 用户管理（菜单）
     * <p>目录编码_菜单编码：SYSTEM_USER</p>
     */
    public static final String SYSTEM_USER_MANAGE = "SYSTEM_USER";

    /**
     * 用户管理（按钮权限）
     * <p>菜单编码_按钮编码：SYSTEM_USER_VIEW / SYSTEM_USER_CREATE / ...</p>
     */
    public static final String USER_VIEW = "SYSTEM_USER_VIEW";
    public static final String USER_CREATE = "SYSTEM_USER_CREATE";
    public static final String USER_UPDATE = "SYSTEM_USER_UPDATE";
    public static final String USER_DELETE = "SYSTEM_USER_DELETE";
    public static final String USER_RESET_PASSWORD = "SYSTEM_USER_RESET_PASSWORD";
    public static final String USER_UNLOCK = "SYSTEM_USER_UNLOCK";

    /**
     * 角色管理（菜单）
     * <p>目录编码_菜单编码：SYSTEM_ROLE</p>
     */
    public static final String SYSTEM_ROLE_MANAGE = "SYSTEM_ROLE";

    /**
     * 角色管理（按钮权限）
     * <p>菜单编码_按钮编码：SYSTEM_ROLE_VIEW / ...</p>
     */
    public static final String ROLE_VIEW = "SYSTEM_ROLE_VIEW";
    public static final String ROLE_CREATE = "SYSTEM_ROLE_CREATE";
    public static final String ROLE_UPDATE = "SYSTEM_ROLE_UPDATE";
    public static final String ROLE_DELETE = "SYSTEM_ROLE_DELETE";
    public static final String ROLE_ASSIGN = "SYSTEM_ROLE_ASSIGN";

    /**
     * 菜单管理（菜单）
     * <p>目录编码_菜单编码：SYSTEM_MENU</p>
     */
    public static final String SYSTEM_MENU_MANAGE = "SYSTEM_MENU";

    /**
     * 权限/菜单管理（按钮权限）
     * <p>菜单编码_按钮编码：SYSTEM_MENU_VIEW / ...</p>
     */
    public static final String PERMISSION_VIEW = "SYSTEM_MENU_VIEW";
    public static final String PERMISSION_CREATE = "SYSTEM_MENU_CREATE";
    public static final String PERMISSION_UPDATE = "SYSTEM_MENU_UPDATE";
    public static final String PERMISSION_DELETE = "SYSTEM_MENU_DELETE";

    /**
     * 系统配置管理（菜单）
     * <p>目录编码_菜单编码：SYSTEM_CONFIG</p>
     */
    public static final String SYSTEM_CONFIG_MANAGE = "SYSTEM_CONFIG";

    /**
     * 系统配置管理（按钮权限）
     * <p>菜单编码_按钮编码：SYSTEM_CONFIG_VIEW / ...</p>
     */
    public static final String CONFIG_VIEW = "SYSTEM_CONFIG_VIEW";
    public static final String CONFIG_CREATE = "SYSTEM_CONFIG_CREATE";
    public static final String CONFIG_UPDATE = "SYSTEM_CONFIG_UPDATE";
    public static final String CONFIG_DELETE = "SYSTEM_CONFIG_DELETE";

    /**
     * 数据字典管理（菜单）
     * <p>目录编码_菜单编码：SYSTEM_DICT</p>
     */
    public static final String SYSTEM_DICT_MANAGE = "SYSTEM_DICT";

    /**
     * 数据字典管理（按钮权限）
     * <p>菜单编码_按钮编码：SYSTEM_DICT_VIEW / ...</p>
     */
    public static final String DICT_VIEW = "SYSTEM_DICT_VIEW";
    public static final String DICT_CREATE = "SYSTEM_DICT_CREATE";
    public static final String DICT_UPDATE = "SYSTEM_DICT_UPDATE";
    public static final String DICT_DELETE = "SYSTEM_DICT_DELETE";

    /**
     * 私有构造函数，防止实例化
     */
    private PermissionConstants() {
        throw new UnsupportedOperationException("常量类不允许实例化");
    }
}

