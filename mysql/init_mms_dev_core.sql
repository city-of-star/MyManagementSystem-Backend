-- 创建 mms_dev_core 数据库
CREATE DATABASE IF NOT EXISTS `mms_dev_core` CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;

-- 使用该数据库
USE `mms_dev_core`;

-- ==================== test 表 ====================

CREATE TABLE IF NOT EXISTS `test`
(
    `id`          bigint auto_increment comment '主键'
        primary key,
    `title`       varchar(512) not null default '' comment '测试标题',
    `content`     text comment '测试内容',
    `create_time` datetime     not null default CURRENT_TIMESTAMP comment '创建时间',
    `update_time` datetime     not null default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP comment '更新时间',
    INDEX `idx_title` (`title`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='测试表-用于测试服务基础功能';

-- ==================== 用户中心服务相关表 ====================

-- 1. 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` varchar(64) NOT NULL COMMENT '用户名（登录账号）',
    `password` varchar(255) NOT NULL COMMENT '密码（加密后）',
    `nickname` varchar(64) DEFAULT NULL COMMENT '昵称',
    `real_name` varchar(64) DEFAULT NULL COMMENT '真实姓名',
    `avatar` varchar(1024) DEFAULT NULL COMMENT '头像URL',
    `email` varchar(128) DEFAULT NULL COMMENT '邮箱（可为空，但填写后必须唯一）',
    `phone` varchar(32) DEFAULT NULL COMMENT '手机号（可为空，但填写后必须唯一）',
    `gender` tinyint DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
    `birthday` date DEFAULT NULL COMMENT '生日',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `locked` tinyint NOT NULL DEFAULT 0 COMMENT '是否锁定：0-未锁定，1-已锁定',
    `lock_time` datetime DEFAULT NULL COMMENT '锁定时间',
    `lock_reason` varchar(255) DEFAULT NULL COMMENT '锁定原因',
    `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
    `last_login_ip` varchar(64) DEFAULT NULL COMMENT '最后登录IP',
    `password_update_time` datetime DEFAULT NULL COMMENT '密码更新时间',
    `remark` varchar(512) DEFAULT NULL COMMENT '备注',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_username` (`username`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_status_deleted` (`status`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='用户表';

-- 2. 用户登录日志表
CREATE TABLE IF NOT EXISTS `user_login_log` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `user_id` bigint DEFAULT NULL COMMENT '用户ID',
    `username` varchar(64) DEFAULT NULL COMMENT '用户名',
    `login_type` varchar(32) DEFAULT NULL COMMENT '登录类型：password-密码登录，sms-短信登录，email-邮箱登录',
    `login_ip` varchar(64) DEFAULT NULL COMMENT '登录IP',
    `login_location` varchar(128) DEFAULT NULL COMMENT '登录地点',
    `user_agent` text DEFAULT NULL COMMENT '用户代理（浏览器信息）',
    `login_status` tinyint NOT NULL DEFAULT 0 COMMENT '登录状态：0-失败，1-成功',
    `login_message` varchar(255) DEFAULT NULL COMMENT '登录消息（失败原因等）',
    `login_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_username` (`username`),
    KEY `idx_login_time` (`login_time`),
    KEY `idx_login_status` (`login_status`),
    KEY `idx_user_login_time` (`user_id`, `login_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='用户登录日志表';

-- ==================== 组织权限服务相关表 ====================

-- 3. 部门表
CREATE TABLE IF NOT EXISTS `dept` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门ID',
    `parent_id` bigint NOT NULL DEFAULT 0 COMMENT '父部门ID，0表示顶级部门',
    `dept_name` varchar(64) NOT NULL COMMENT '部门名称',
    `dept_code` varchar(64) NOT NULL COMMENT '部门编码',
    `leader` varchar(64) DEFAULT NULL COMMENT '负责人',
    `phone` varchar(32) DEFAULT NULL COMMENT '联系电话',
    `email` varchar(128) DEFAULT NULL COMMENT '邮箱',
    `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序号',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `remark` varchar(512) DEFAULT NULL COMMENT '备注',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_dept_code` (`dept_code`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='部门表';

-- 4. 岗位表
CREATE TABLE IF NOT EXISTS `post` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
    `post_code` varchar(64) NOT NULL COMMENT '岗位编码',
    `post_name` varchar(64) NOT NULL COMMENT '岗位名称',
    `post_level` varchar(32) DEFAULT NULL COMMENT '岗位等级',
    `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序号',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `remark` varchar(512) DEFAULT NULL COMMENT '备注',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_post_code` (`post_code`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='岗位表';

-- 5. 角色表
CREATE TABLE IF NOT EXISTS `role` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `role_code` varchar(64) NOT NULL COMMENT '角色编码',
    `role_name` varchar(64) NOT NULL COMMENT '角色名称',
    `role_type` varchar(32) DEFAULT NULL COMMENT '角色类型：system-系统角色，custom-自定义角色',
    `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序号',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `remark` varchar(512) DEFAULT NULL COMMENT '备注',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_role_code` (`role_code`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='角色表';

-- 6. 权限表（菜单/按钮/接口权限）
CREATE TABLE IF NOT EXISTS `permission` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '权限ID',
    `parent_id` bigint NOT NULL DEFAULT 0 COMMENT '父权限ID，0表示顶级权限',
    `permission_type` varchar(32) NOT NULL COMMENT '权限类型：catalog-目录，menu-菜单，button-按钮，api-接口',
    `permission_name` varchar(64) NOT NULL COMMENT '权限名称',
    `permission_code` varchar(128) NOT NULL COMMENT '权限编码（唯一标识）',
    `path` varchar(255) DEFAULT NULL COMMENT '路由路径（菜单类型使用）',
    `component` varchar(255) DEFAULT NULL COMMENT '组件路径（菜单类型使用）',
    `icon` varchar(64) DEFAULT NULL COMMENT '图标（菜单类型使用）',
    `api_url` varchar(255) DEFAULT NULL COMMENT '接口URL（接口类型使用）',
    `api_method` varchar(16) DEFAULT NULL COMMENT '接口请求方式：GET,POST,PUT,DELETE等（接口类型使用）',
    `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序号',
    `visible` tinyint NOT NULL DEFAULT 1 COMMENT '是否显示：0-隐藏，1-显示',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `remark` varchar(512) DEFAULT NULL COMMENT '备注',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_permission_code` (`permission_code`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_permission_type` (`permission_type`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`),
    KEY `idx_status_deleted_type` (`status`, `deleted`, `permission_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='权限表';

-- 7. 用户角色关联表
CREATE TABLE IF NOT EXISTS `user_role` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `role_id` bigint NOT NULL COMMENT '角色ID',
    `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='用户角色关联表';

-- 8. 角色权限关联表
CREATE TABLE IF NOT EXISTS `role_permission` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID',
    `role_id` bigint NOT NULL COMMENT '角色ID',
    `permission_id` bigint NOT NULL COMMENT '权限ID',
    `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='角色权限关联表';

-- 9. 用户部门关联表
CREATE TABLE IF NOT EXISTS `user_dept` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `dept_id` bigint NOT NULL COMMENT '部门ID',
    `is_primary` tinyint NOT NULL DEFAULT 0 COMMENT '是否主部门：0-否，1-是',
    `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_dept` (`user_id`, `dept_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_dept_id` (`dept_id`),
    KEY `idx_is_primary` (`is_primary`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='用户部门关联表';

-- 10. 用户岗位关联表
CREATE TABLE IF NOT EXISTS `user_post` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `post_id` bigint NOT NULL COMMENT '岗位ID',
    `is_primary` tinyint NOT NULL DEFAULT 0 COMMENT '是否主岗位：0-否，1-是',
    `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_post` (`user_id`, `post_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_post_id` (`post_id`),
    KEY `idx_is_primary` (`is_primary`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='用户岗位关联表';

-- 11. 系统配置表（用于系统配置，单个键值对）
CREATE TABLE IF NOT EXISTS `config` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '配置ID',
    `config_key` varchar(128) NOT NULL COMMENT '配置键（唯一标识）',
    `config_value` text COMMENT '配置值',
    `config_type` varchar(32) NOT NULL DEFAULT 'string' COMMENT '配置类型：string-字符串，number-数字，boolean-布尔值，json-JSON对象',
    `config_name` varchar(128) NOT NULL COMMENT '配置名称/描述',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `editable` tinyint NOT NULL DEFAULT 1 COMMENT '是否可编辑：0-否（系统配置），1-是（用户配置）',
    `remark` varchar(512) DEFAULT NULL COMMENT '备注',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_config_key` (`config_key`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`),
    KEY `idx_config_type` (`config_type`),
    KEY `idx_status_deleted` (`status`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='系统配置表';

-- 12. 数据字典类型表（字典分类）
CREATE TABLE IF NOT EXISTS `dict_type` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典类型ID',
    `dict_type_code` varchar(64) NOT NULL COMMENT '字典类型编码（唯一标识）',
    `dict_type_name` varchar(128) NOT NULL COMMENT '字典类型名称',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序号',
    `remark` varchar(512) DEFAULT NULL COMMENT '备注',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_dict_type_code` (`dict_type_code`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`),
    KEY `idx_status_deleted` (`status`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='数据字典类型表（字典分类）';

-- 13. 数据字典数据表（字典键值对，用于下拉框等）
CREATE TABLE IF NOT EXISTS `dict_data` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典数据ID',
    `dict_type_id` bigint NOT NULL COMMENT '字典类型ID',
    `dict_label` varchar(128) NOT NULL COMMENT '字典标签（显示文本）',
    `dict_value` varchar(128) NOT NULL COMMENT '字典值（实际值）',
    `dict_sort` int NOT NULL DEFAULT 0 COMMENT '排序号',
    `is_default` tinyint NOT NULL DEFAULT 0 COMMENT '是否默认值：0-否，1-是',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `remark` varchar(512) DEFAULT NULL COMMENT '备注',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_dict_type_id` (`dict_type_id`),
    KEY `idx_dict_value` (`dict_value`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`),
    KEY `idx_dict_type_status_deleted` (`dict_type_id`, `status`, `deleted`),
    CONSTRAINT `fk_dict_data_type` FOREIGN KEY (`dict_type_id`) REFERENCES `dict_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='数据字典数据表（字典键值对）';

-- ==================== 初始化数据 ====================

-- 初始化超级管理员用户（密码：123456）
INSERT IGNORE INTO `user` (`id`, `username`, `password`, `nickname`, `real_name`, `email`, `phone`, `status`, `locked`, `remark`, `deleted`, `create_time`, `update_time`)
VALUES (1, 'admin', '$2a$10$cU5acgjEYlHA.2cql1DmiOVcTKexIR0iKpKAIda0gJyLdKxeE8Lt.', '超级管理员', '超级管理员', '2722562862@qq.com', '18255097030', 1, 0, '今天又是一个晴朗的一天', 0, NOW(), NOW());

-- 初始化系统管理员角色
INSERT IGNORE INTO `role` (`id`, `role_code`, `role_name`, `role_type`, `status`, `deleted`, `create_time`, `update_time`)
VALUES (1, 'admin', '超级管理员', 'system', 1, 0, NOW(), NOW());

-- 初始化普通用户角色
INSERT IGNORE INTO `role` (`id`, `role_code`, `role_name`, `role_type`, `status`, `deleted`, `create_time`, `update_time`)
VALUES (2, 'user', '普通用户', 'system', 1, 0, NOW(), NOW());

-- 给超级管理员分配角色
INSERT IGNORE INTO `user_role` (`user_id`, `role_id`, `create_time`)
VALUES (1, 1, NOW());

-- 初始化权限数据（菜单 + 按钮）
INSERT IGNORE INTO `permission` (`id`, `parent_id`, `permission_type`, `permission_name`, `permission_code`,
                                 `path`, `component`, `icon`, `sort_order`, `visible`, `status`, `deleted`, `create_time`, `update_time`)
VALUES
    -- 系统管理（父菜单）
    (1, 0, 'catalog', '系统管理', 'SYSTEM', null, null, 'system', 1, 1, 1, 0, NOW(), NOW()),

    -- 用户管理（菜单 + 按钮）
    (2, 1, 'menu', '用户管理', 'SYSTEM_USER', '/system/userPage', '/system/user/UserPage', 'user', 10, 1, 1, 0, NOW(), NOW()),
    (3, 2, 'button', '用户-查看', 'SYSTEM_USER_VIEW', NULL, NULL, NULL, 10, 1, 1, 0, NOW(), NOW()),
    (4, 2, 'button', '用户-新增', 'SYSTEM_USER_CREATE', NULL, NULL, NULL, 11, 1, 1, 0, NOW(), NOW()),
    (5, 2, 'button', '用户-编辑', 'SYSTEM_USER_UPDATE', NULL, NULL, NULL, 12, 1, 1, 0, NOW(), NOW()),
    (6, 2, 'button', '用户-删除', 'SYSTEM_USER_DELETE', NULL, NULL, NULL, 13, 1, 1, 0, NOW(), NOW()),
    (7, 2, 'button', '用户-重置密码', 'SYSTEM_USER_RESET_PASSWORD', NULL, NULL, NULL, 14, 1, 1, 0, NOW(), NOW()),
    (8, 2, 'button', '用户-解锁', 'SYSTEM_USER_UNLOCK', NULL, NULL, NULL, 15, 1, 1, 0, NOW(), NOW()),

    -- 角色管理（菜单 + 按钮）
    (9, 1, 'menu', '角色管理', 'SYSTEM_ROLE', '/system/rolePage', '/system/role/RolePage', 'peoples', 20, 1, 1, 0, NOW(), NOW()),
    (10, 9, 'button', '角色-查看', 'SYSTEM_ROLE_VIEW', NULL, NULL, NULL, 20, 1, 1, 0, NOW(), NOW()),
    (11, 9, 'button', '角色-新增', 'SYSTEM_ROLE_CREATE', NULL, NULL, NULL, 21, 1, 1, 0, NOW(), NOW()),
    (12, 9, 'button', '角色-编辑', 'SYSTEM_ROLE_UPDATE', NULL, NULL, NULL, 22, 1, 1, 0, NOW(), NOW()),
    (13, 9, 'button', '角色-删除', 'SYSTEM_ROLE_DELETE', NULL, NULL, NULL, 23, 1, 1, 0, NOW(), NOW()),
    (14, 9, 'button', '角色-分配权限', 'SYSTEM_ROLE_ASSIGN', NULL, NULL, NULL, 24, 1, 1, 0, NOW(), NOW()),

    -- 菜单管理（菜单 + 按钮）
    (15, 1, 'menu', '菜单管理', 'SYSTEM_MENU', '/system/menuPage', '/system/menu/MenuPage', 'tree-table', 30, 1, 1, 0, NOW(), NOW()),
    (16, 15, 'button', '权限-查看', 'SYSTEM_MENU_VIEW', NULL, NULL, NULL, 30, 1, 1, 0, NOW(), NOW()),
    (17, 15, 'button', '权限-新增', 'SYSTEM_MENU_CREATE', NULL, NULL, NULL, 31, 1, 1, 0, NOW(), NOW()),
    (18, 15, 'button', '权限-编辑', 'SYSTEM_MENU_UPDATE', NULL, NULL, NULL, 32, 1, 1, 0, NOW(), NOW()),
    (19, 15, 'button', '权限-删除', 'SYSTEM_MENU_DELETE', NULL, NULL, NULL, 33, 1, 1, 0, NOW(), NOW()),

    -- 系统配置管理（菜单 + 按钮）
    (20, 1, 'menu', '系统配置管理', 'SYSTEM_CONFIG', '/system/configPage', '/system/config/ConfigPage', 'setting', 50, 1, 1, 0, NOW(), NOW()),
    (21, 20, 'button', '配置-查看', 'SYSTEM_CONFIG_VIEW', NULL, NULL, NULL, 50, 1, 1, 0, NOW(), NOW()),
    (22, 20, 'button', '配置-新增', 'SYSTEM_CONFIG_CREATE', NULL, NULL, NULL, 51, 1, 1, 0, NOW(), NOW()),
    (23, 20, 'button', '配置-编辑', 'SYSTEM_CONFIG_UPDATE', NULL, NULL, NULL, 52, 1, 1, 0, NOW(), NOW()),
    (24, 20, 'button', '配置-删除', 'SYSTEM_CONFIG_DELETE', NULL, NULL, NULL, 53, 1, 1, 0, NOW(), NOW()),

    -- 数据字典管理（菜单 + 按钮）
    (25, 1, 'menu', '数据字典管理', 'SYSTEM_DICT', '/system/dictPage', '/system/dict/DictPage', 'dict', 60, 1, 1, 0, NOW(), NOW()),
    (26, 25, 'button', '字典-查看', 'SYSTEM_DICT_VIEW', NULL, NULL, NULL, 60, 1, 1, 0, NOW(), NOW()),
    (27, 25, 'button', '字典-新增', 'SYSTEM_DICT_CREATE', NULL, NULL, NULL, 61, 1, 1, 0, NOW(), NOW()),
    (28, 25, 'button', '字典-编辑', 'SYSTEM_DICT_UPDATE', NULL, NULL, NULL, 62, 1, 1, 0, NOW(), NOW()),
    (29, 25, 'button', '字典-删除', 'SYSTEM_DICT_DELETE', NULL, NULL, NULL, 63, 1, 1, 0, NOW(), NOW());

-- 将所有初始化的权限授予超级管理员角色（role_id = 1）
INSERT IGNORE INTO `role_permission` (`role_id`, `permission_id`, `create_time`)
VALUES
    -- 系统管理菜单
    (1, 1, NOW()),
    -- 用户管理菜单及按钮（ID: 2-8）
    (1, 2, NOW()),
    (1, 3, NOW()),
    (1, 4, NOW()),
    (1, 5, NOW()),
    (1, 6, NOW()),
    (1, 7, NOW()),
    (1, 8, NOW()),
    -- 角色管理菜单及按钮（ID: 9-14）
    (1, 9, NOW()),
    (1, 10, NOW()),
    (1, 11, NOW()),
    (1, 12, NOW()),
    (1, 13, NOW()),
    (1, 14, NOW()),
    -- 菜单管理菜单及按钮（ID: 15-19）
    (1, 15, NOW()),
    (1, 16, NOW()),
    (1, 17, NOW()),
    (1, 18, NOW()),
    (1, 19, NOW()),
    -- 系统配置管理菜单及按钮（ID: 20-24）
    (1, 20, NOW()),
    (1, 21, NOW()),
    (1, 22, NOW()),
    (1, 23, NOW()),
    (1, 24, NOW()),
    -- 数据字典管理菜单及按钮（ID: 25-29）
    (1, 25, NOW()),
    (1, 26, NOW()),
    (1, 27, NOW()),
    (1, 28, NOW()),
    (1, 29, NOW());

-- 初始化部门数据
INSERT IGNORE INTO `dept` (`id`, `parent_id`, `dept_name`, `dept_code`, `leader`, `phone`, `email`, `sort_order`, `status`, `remark`, `deleted`, `create_time`, `update_time`)
VALUES
    (1, 0, '总公司', 'ROOT', '系统管理员', NULL, NULL, 1, 1, '根部门', 0, NOW(), NOW()),
    (2, 1, '技术部', 'TECH', '技术总监', NULL, NULL, 10, 1, '技术部门', 0, NOW(), NOW()),
    (3, 1, '产品部', 'PRODUCT', '产品总监', NULL, NULL, 20, 1, '产品部门', 0, NOW(), NOW()),
    (4, 1, '运营部', 'OPERATION', '运营总监', NULL, NULL, 30, 1, '运营部门', 0, NOW(), NOW()),
    (5, 2, '前端组', 'FRONTEND', '前端组长', NULL, NULL, 11, 1, '前端开发组', 0, NOW(), NOW()),
    (6, 2, '后端组', 'BACKEND', '后端组长', NULL, NULL, 12, 1, '后端开发组', 0, NOW(), NOW());

-- 初始化岗位数据
INSERT IGNORE INTO `post` (`id`, `post_code`, `post_name`, `post_level`, `sort_order`, `status`, `remark`, `deleted`, `create_time`, `update_time`)
VALUES
    (1, 'CEO', '首席执行官', 'P1', 1, 1, '公司最高管理者', 0, NOW(), NOW()),
    (2, 'CTO', '首席技术官', 'P2', 2, 1, '技术负责人', 0, NOW(), NOW()),
    (3, 'PM', '产品经理', 'P3', 3, 1, '产品管理', 0, NOW(), NOW()),
    (4, 'DEV', '开发工程师', 'P4', 4, 1, '软件开发', 0, NOW(), NOW()),
    (5, 'TEST', '测试工程师', 'P4', 5, 1, '软件测试', 0, NOW(), NOW()),
    (6, 'OP', '运营专员', 'P4', 6, 1, '运营工作', 0, NOW(), NOW());

-- 给超级管理员分配部门（主部门：技术部）
INSERT IGNORE INTO `user_dept` (`user_id`, `dept_id`, `is_primary`, `create_time`)
VALUES (1, 2, 1, NOW());

-- 给超级管理员分配岗位（主岗位：CTO）
INSERT IGNORE INTO `user_post` (`user_id`, `post_id`, `is_primary`, `create_time`)
VALUES (1, 2, 1, NOW());

-- 初始化系统配置数据
INSERT IGNORE INTO `config` (`config_key`, `config_value`, `config_type`, `config_name`, `status`, `editable`, `remark`, `deleted`, `create_time`, `update_time`)
VALUES
    ('system.name', 'MyManagementSystem', 'string', '系统名称', 1, 0, '系统名称配置', 0, NOW(), NOW()),
    ('system.version', '1.0.0', 'string', '系统版本', 1, 0, '系统版本号', 0, NOW(), NOW()),
    ('system.copyright', '© 2025 MyManagementSystem', 'string', '版权信息', 1, 1, '系统版权信息', 0, NOW(), NOW()),
    ('login.password.minLength', '6', 'number', '密码最小长度', 1, 1, '用户密码最小长度要求', 0, NOW(), NOW()),
    ('login.password.maxLength', '20', 'number', '密码最大长度', 1, 1, '用户密码最大长度要求', 0, NOW(), NOW()),
    ('file.upload.maxSize', '10485760', 'number', '文件上传最大大小（字节）', 1, 1, '单个文件上传的最大大小，默认10MB', 0, NOW(), NOW()),
    ('file.upload.allowedTypes', 'jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx', 'string', '允许上传的文件类型', 1, 1, '允许上传的文件扩展名，用逗号分隔', 0, NOW(), NOW()),
    ('cache.permission.ttl', '30', 'number', '权限缓存过期时间（分钟）', 1, 1, '用户权限信息在缓存中的过期时间', 0, NOW(), NOW());

-- 初始化数据字典类型（通用字典类型放在前面）
INSERT IGNORE INTO `dict_type` (`id`, `dict_type_code`, `dict_type_name`, `status`, `sort_order`, `remark`, `deleted`, `create_time`, `update_time`)
VALUES
    -- 通用字典类型（放前面）
    (1, 'common_status', '通用状态', 1, 1, '通用的启用/禁用状态', 0, NOW(), NOW()),
    (2, 'yes_no', '是否', 1, 2, '通用布尔值（是/否）', 0, NOW(), NOW()),
    (3, 'menu_visible', '菜单显示状态', 1, 3, '菜单/权限显示状态', 0, NOW(), NOW()),
    (4, 'config_type', '参数配置类型', 1, 4, 'system config 中的类型', 0, NOW(), NOW()),
    -- 业务相关字典类型
    (5, 'user_lock_status', '用户锁定状态', 1, 10, '用户是否被锁定', 0, NOW(), NOW()),
    (6, 'user_gender', '用户性别', 1, 11, '用户性别选项', 0, NOW(), NOW()),
    (7, 'role_type', '角色类型', 1, 12, '角色类型分类', 0, NOW(), NOW()),
    (8, 'permission_type', '权限类型', 1, 13, '权限类型分类', 0, NOW(), NOW());

-- 初始化数据字典数据
INSERT IGNORE INTO `dict_data` (`dict_type_id`, `dict_label`, `dict_value`, `dict_sort`, `is_default`, `status`, `remark`, `deleted`, `create_time`, `update_time`)
VALUES
    -- 通用状态
    (1, '启用', '1', 1, 1, 1, '启用状态', 0, NOW(), NOW()),
    (1, '禁用', '0', 2, 0, 1, '禁用状态', 0, NOW(), NOW()),
    -- 是否
    (2, '是', '1', 1, 1, 1, '是 / true', 0, NOW(), NOW()),
    (2, '否', '0', 2, 0, 1, '否 / false', 0, NOW(), NOW()),
    -- 菜单显示状态
    (3, '显示', '1', 1, 1, 1, '菜单在前端展示', 0, NOW(), NOW()),
    (3, '隐藏', '0', 2, 0, 1, '菜单在前端隐藏', 0, NOW(), NOW()),
    -- 参数配置类型
    (4, '字符串', 'string', 1, 1, 1, '字符串类型配置', 0, NOW(), NOW()),
    (4, '数字', 'number', 2, 0, 1, '数字类型配置', 0, NOW(), NOW()),
    (4, '布尔', 'boolean', 3, 0, 1, '布尔类型配置', 0, NOW(), NOW()),
    (4, 'JSON', 'json', 4, 0, 1, 'JSON 类型配置', 0, NOW(), NOW()),
    -- 用户锁定状态
    (5, '未锁定', '0', 1, 1, 1, '用户正常', 0, NOW(), NOW()),
    (5, '已锁定', '1', 2, 0, 1, '用户被锁定', 0, NOW(), NOW()),
    -- 用户性别
    (6, '未知', '0', 1, 1, 1, '性别未知', 0, NOW(), NOW()),
    (6, '男', '1', 2, 0, 1, '男性', 0, NOW(), NOW()),
    (6, '女', '2', 3, 0, 1, '女性', 0, NOW(), NOW()),
    -- 角色类型
    (7, '系统角色', 'system', 1, 0, 1, '系统内置角色，不可删除', 0, NOW(), NOW()),
    (7, '自定义角色', 'custom', 2, 1, 1, '用户自定义角色', 0, NOW(), NOW()),
    -- 权限类型
    (8, '目录', 'catalog', 0, 0, 1, '目录权限', 0, NOW(), NOW()),
    (8, '菜单', 'menu', 1, 0, 1, '菜单权限', 0, NOW(), NOW()),
    (8, '按钮', 'button', 2, 0, 1, '按钮权限', 0, NOW(), NOW()),
    (8, '接口', 'api', 3, 0, 1, '接口权限', 0, NOW(), NOW());