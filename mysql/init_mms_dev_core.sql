-- 创建 mms_dev_core 数据库
CREATE DATABASE IF NOT EXISTS `mms_dev_core` CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;

-- 使用该数据库
USE `mms_dev_core`;

-- 用户表
CREATE TABLE IF NOT EXISTS `system_user` (
    `id` bigint NOT NULL COMMENT '用户ID',
    `username` varchar(64) NOT NULL COMMENT '用户名（登录账号）',
    `password` varchar(255) NOT NULL COMMENT '密码（加密后）',
    `nickname` varchar(64) DEFAULT NULL COMMENT '昵称',
    `real_name` varchar(64) DEFAULT NULL COMMENT '真实姓名',
    `avatar_id` bigint DEFAULT NULL COMMENT '头像附件ID',
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

-- 角色表
CREATE TABLE IF NOT EXISTS `system_role` (
    `id` bigint NOT NULL COMMENT '角色ID',
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

-- 权限表
CREATE TABLE IF NOT EXISTS `system_permission` (
    `id` bigint NOT NULL COMMENT '权限ID',
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

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS `system_user_role` (
    `id` bigint NOT NULL COMMENT '关联ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `role_id` bigint NOT NULL COMMENT '角色ID',
    `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='用户角色关联表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS `system_role_permission` (
    `id` bigint NOT NULL COMMENT '关联ID',
    `role_id` bigint NOT NULL COMMENT '角色ID',
    `permission_id` bigint NOT NULL COMMENT '权限ID',
    `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='角色权限关联表';

-- 部门表
CREATE TABLE IF NOT EXISTS `system_dept` (
    `id` bigint NOT NULL COMMENT '部门ID',
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

-- 岗位表
CREATE TABLE IF NOT EXISTS `system_post` (
    `id` bigint NOT NULL COMMENT '岗位ID',
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

-- 用户部门关联表
CREATE TABLE IF NOT EXISTS `system_user_dept` (
    `id` bigint NOT NULL COMMENT '关联ID',
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

-- 用户岗位关联表
CREATE TABLE IF NOT EXISTS `system_user_post` (
    `id` bigint NOT NULL COMMENT '关联ID',
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

-- 系统配置表
CREATE TABLE IF NOT EXISTS `system_config` (
    `id` bigint NOT NULL COMMENT '配置ID',
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

-- 数据字典类型表
CREATE TABLE IF NOT EXISTS `system_dict_type` (
    `id` bigint NOT NULL COMMENT '字典类型ID',
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

-- 数据字典数据表
CREATE TABLE IF NOT EXISTS `system_dict_data` (
    `id` bigint NOT NULL COMMENT '字典数据ID',
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
    CONSTRAINT `fk_dict_data_type` FOREIGN KEY (`dict_type_id`) REFERENCES `system_dict_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='数据字典数据表（字典键值对）';

-- 附件表
CREATE TABLE IF NOT EXISTS `system_attachment` (
    `id` bigint NOT NULL COMMENT '附件ID',
    `file_name` varchar(255) NOT NULL COMMENT '文件名（存储文件名）',
    `original_name` varchar(255) NOT NULL COMMENT '原始文件名',
    `file_path` varchar(1024) NOT NULL COMMENT '文件存储路径',
    `file_url` varchar(1024) NOT NULL COMMENT '文件访问URL',
    `file_size` bigint NOT NULL COMMENT '文件大小（字节）',
    `file_type` varchar(64) NOT NULL COMMENT '文件类型（扩展名）',
    `mime_type` varchar(128) DEFAULT NULL COMMENT 'MIME类型',
    `storage_type` varchar(32) NOT NULL DEFAULT 'local' COMMENT '存储类型：local-本地，oss-对象存储',
    `business_type` varchar(64) DEFAULT NULL COMMENT '业务类型（用于区分不同业务场景）',
    `business_id` bigint DEFAULT NULL COMMENT '关联业务ID',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `remark` varchar(512) DEFAULT NULL COMMENT '备注',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_file_type` (`file_type`),
    KEY `idx_business` (`business_type`, `business_id`),
    KEY `idx_create_by` (`create_by`),
    KEY `idx_deleted` (`deleted`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_status_deleted` (`status`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='附件表';

-- 定时任务定义表
CREATE TABLE IF NOT EXISTS `job_def` (
    `id` bigint NOT NULL COMMENT '任务定义ID',
    `service_name` varchar(64) NOT NULL COMMENT '所属服务',
    `job_code` varchar(128) NOT NULL COMMENT '任务编码',
    `job_name` varchar(255) NOT NULL COMMENT '任务名称',
    `job_type` varchar(255) NOT NULL COMMENT '任务类型',
    `cron_expr` varchar(128) NOT NULL COMMENT 'Cron表达式',
    `next_run_time` datetime DEFAULT NULL COMMENT '下一次触发时间',
    `run_mode` varchar(16) NOT NULL DEFAULT 'single' COMMENT '运行模式：single-集群单实例执行，all-全实例执行',
    `enabled` tinyint NOT NULL DEFAULT 1 COMMENT '是否启用：0-禁用，1-启用',
    `timeout_ms` int NOT NULL DEFAULT 0 COMMENT '超时毫秒（0表示不超时）',
    `remark` varchar(512) DEFAULT NULL COMMENT '备注',
    `params_json` text DEFAULT NULL COMMENT '任务参数JSON',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_service_job_code` (`service_name`, `job_code`),
    KEY `idx_service_enabled` (`service_name`, `enabled`),
    KEY `idx_next_run_time` (`next_run_time`),
    KEY `idx_deleted` (`deleted`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='定时任务定义表';

-- 定时任务执行记录表
CREATE TABLE IF NOT EXISTS `job_run_log` (
    `id` bigint NOT NULL COMMENT '执行记录ID',
    `job_id` bigint NOT NULL COMMENT '任务定义ID',
    `job_name` varchar(255) DEFAULT NULL COMMENT '任务名称（冗余）',
    `run_id` varchar(64) NOT NULL COMMENT '本次执行唯一ID',
    `status` varchar(16) NOT NULL COMMENT '状态：running/success/fail/timeout/skip',
    `start_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '开始时间',
    `end_time` datetime DEFAULT NULL COMMENT '结束时间',
    `duration_ms` bigint DEFAULT NULL COMMENT '耗时毫秒',
    `instance_id` varchar(128) DEFAULT NULL COMMENT '执行实例ID',
    `host` varchar(128) DEFAULT NULL COMMENT '执行机器host/IP',
    `error_message` varchar(1024) DEFAULT NULL COMMENT '错误摘要',
    `error_stack` mediumtext DEFAULT NULL COMMENT '错误堆栈',
    `result_json` text DEFAULT NULL COMMENT '结果/统计JSON',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_run_id` (`run_id`),
    KEY `idx_job_start_time` (`job_id`, `start_time`),
    KEY `idx_status_start_time` (`status`, `start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='定时任务执行记录表';

-- 定时任务执行锁表
CREATE TABLE IF NOT EXISTS `job_lock` (
    `id` bigint NOT NULL COMMENT '锁ID',
    `job_id` bigint NOT NULL COMMENT '任务定义ID',
    `instance_id` varchar(128) NOT NULL COMMENT '持有锁的实例ID',
    `lock_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '锁定时间',
    `expire_time` datetime NOT NULL COMMENT '锁过期时间',
    `heartbeat_time` datetime DEFAULT NULL COMMENT '心跳时间（用于续期）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_job_id` (`job_id`),
    KEY `idx_expire_time` (`expire_time`),
    KEY `idx_instance_id` (`instance_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='任务执行锁表';

-- 用户登录日志表
CREATE TABLE IF NOT EXISTS `audit_user_login_log` (
    `id` bigint NOT NULL COMMENT '日志ID',
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

-- 用户操作日志表
CREATE TABLE IF NOT EXISTS `audit_operation_log` (
    `id` bigint NOT NULL COMMENT '日志ID',
    `trace_id` varchar(64) DEFAULT NULL COMMENT '链路追踪ID',
    `user_id` bigint DEFAULT NULL COMMENT '操作用户ID',
    `username` varchar(64) DEFAULT NULL COMMENT '操作用户名',
    `module` varchar(64) DEFAULT NULL COMMENT '业务模块',
    `operation_type` varchar(32) DEFAULT NULL COMMENT '操作类型：create/update/delete/export/assign/login/logout等',
    `operation_desc` varchar(255) DEFAULT NULL COMMENT '操作描述',
    `request_method` varchar(16) DEFAULT NULL COMMENT '请求方法：GET/POST/PUT/DELETE',
    `request_url` varchar(255) DEFAULT NULL COMMENT '请求URL',
    `request_ip` varchar(64) DEFAULT NULL COMMENT '请求IP',
    `request_params` text DEFAULT NULL COMMENT '请求参数（脱敏后）',
    `response_data` text DEFAULT NULL COMMENT '响应结果摘要',
    `operation_status` tinyint NOT NULL DEFAULT 1 COMMENT '操作状态：0-失败，1-成功',
    `error_message` varchar(512) DEFAULT NULL COMMENT '失败原因/异常摘要',
    `cost_ms` bigint DEFAULT NULL COMMENT '耗时（毫秒）',
    `operation_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (`id`),
    KEY `idx_trace_id` (`trace_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_username` (`username`),
    KEY `idx_module` (`module`),
    KEY `idx_operation_type` (`operation_type`),
    KEY `idx_operation_status` (`operation_status`),
    KEY `idx_operation_time` (`operation_time`),
    KEY `idx_user_operation_time` (`user_id`, `operation_time`),
KEY `idx_module_type_time` (`module`, `operation_type`, `operation_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='用户操作日志表';

-- 系统异常日志表
CREATE TABLE IF NOT EXISTS `audit_exception_log` (
    `id` bigint NOT NULL COMMENT '日志ID',
    `trace_id` varchar(64) DEFAULT NULL COMMENT '链路追踪ID',
    `service_name` varchar(64) DEFAULT NULL COMMENT '服务名称',
    `module` varchar(64) DEFAULT NULL COMMENT '业务模块',
    `exception_type` varchar(255) DEFAULT NULL COMMENT '异常类型',
    `exception_message` varchar(1024) DEFAULT NULL COMMENT '异常信息',
    `stack_trace` mediumtext DEFAULT NULL COMMENT '异常堆栈',
    `request_method` varchar(16) DEFAULT NULL COMMENT '请求方法',
    `request_url` varchar(255) DEFAULT NULL COMMENT '请求URL',
    `request_ip` varchar(64) DEFAULT NULL COMMENT '请求IP',
    `request_params` text DEFAULT NULL COMMENT '请求参数（脱敏后）',
    `user_id` bigint DEFAULT NULL COMMENT '操作用户ID',
    `username` varchar(64) DEFAULT NULL COMMENT '操作用户名',
    `resolved` tinyint NOT NULL DEFAULT 0 COMMENT '是否已处理：0-未处理，1-已处理',
    `resolve_by` bigint DEFAULT NULL COMMENT '处理人ID',
    `resolve_time` datetime DEFAULT NULL COMMENT '处理时间',
    `resolve_remark` varchar(512) DEFAULT NULL COMMENT '处理备注',
    `occur_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发生时间',
    PRIMARY KEY (`id`),
    KEY `idx_trace_id` (`trace_id`),
    KEY `idx_service_name` (`service_name`),
    KEY `idx_module` (`module`),
    KEY `idx_exception_type` (`exception_type`),
    KEY `idx_resolved` (`resolved`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_occur_time` (`occur_time`),
    KEY `idx_resolved_occur_time` (`resolved`, `occur_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='系统异常日志表';

-- 接口访问日志表
CREATE TABLE IF NOT EXISTS `audit_api_access_log` (
    `id` bigint NOT NULL COMMENT '日志ID',
    `trace_id` varchar(64) DEFAULT NULL COMMENT '链路追踪ID',
    `service_name` varchar(64) DEFAULT NULL COMMENT '服务名',
    `user_id` bigint DEFAULT NULL COMMENT '用户ID',
    `username` varchar(64) DEFAULT NULL COMMENT '用户名',
    `request_method` varchar(16) NOT NULL COMMENT '请求方法',
    `request_url` varchar(255) NOT NULL COMMENT '请求URL',
    `request_ip` varchar(64) DEFAULT NULL COMMENT '请求IP',
    `request_params` text DEFAULT NULL COMMENT '请求参数（脱敏后）',
    `http_status` int DEFAULT NULL COMMENT 'HTTP状态码',
    `business_code` varchar(64) DEFAULT NULL COMMENT '业务状态码',
    `access_status` tinyint NOT NULL DEFAULT 1 COMMENT '访问状态：0-失败，1-成功',
    `cost_ms` bigint DEFAULT NULL COMMENT '耗时（毫秒）',
    `access_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '访问时间',
    PRIMARY KEY (`id`),
    KEY `idx_trace_id` (`trace_id`),
    KEY `idx_service_name` (`service_name`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_username` (`username`),
    KEY `idx_request_method` (`request_method`),
    KEY `idx_access_status` (`access_status`),
    KEY `idx_access_time` (`access_time`),
    KEY `idx_url_time` (`request_url`, `access_time`),
KEY `idx_method_status_time` (`request_method`, `access_status`, `access_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='接口访问日志表';

-- 在线用户会话表
CREATE TABLE IF NOT EXISTS `security_online_user` (
    `id` bigint NOT NULL COMMENT '记录ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `username` varchar(64) NOT NULL COMMENT '用户名',
    `token_id` varchar(128) NOT NULL COMMENT '会话Token标识',
    `login_ip` varchar(64) DEFAULT NULL COMMENT '登录IP',
    `login_location` varchar(128) DEFAULT NULL COMMENT '登录地点',
    `user_agent` text DEFAULT NULL COMMENT '用户代理',
    `device_type` varchar(32) DEFAULT NULL COMMENT '设备类型：pc/mobile/tablet等',
    `browser` varchar(64) DEFAULT NULL COMMENT '浏览器',
    `os` varchar(64) DEFAULT NULL COMMENT '操作系统',
    `login_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
    `last_active_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后活跃时间',
    `offline_time` datetime DEFAULT NULL COMMENT '下线时间',
    `online_status` tinyint NOT NULL DEFAULT 1 COMMENT '在线状态：0-离线，1-在线',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_token_id` (`token_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_username` (`username`),
    KEY `idx_online_status` (`online_status`),
    KEY `idx_last_active_time` (`last_active_time`),
KEY `idx_user_status_active` (`user_id`, `online_status`, `last_active_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='在线用户会话表';

-- ==================== 初始化数据 ====================

-- 初始化用户（密码：MMS2025_ + username，例如 MMS2025_superAdmin）
INSERT IGNORE INTO `system_user` (`id`, `username`, `password`, `nickname`, `real_name`, `gender`, `email`, `phone`, `status`, `locked`, `remark`, `deleted`, `create_time`, `update_time`)
VALUES
    (1, 'superAdmin', '$2a$12$nFjZno1HedIH3SyVi1pv.uTspMocaWdTKcQ/SqmAh5x81Monhzdga', '超级管理员', '超级管理员', 1,  '18888888888@qq.com', '18888888888', 1, 0, '系统用户不可删除', 0, NOW(), NOW()),
    (2, 'lhy', '$2a$12$lKyKrCRR22NN.gybdvO3m.Oa08UNpJImvb6GB1C9v6oiXe6XYC7OS', 'redRain', '李鸿羽', 1,  '2722562862@qq.com', '18255097030', 1, 0, '今天又是一个晴朗的一天', 0, NOW(), NOW()),
    (3, 'lqh', '$2a$12$Eah/5KgMiLiZtTOqGcrrEOAfS3K62MoLs4oZUw5NkNVdklN.Ie...', '洛', '刘齐慧', 2,  '2825646787@qq.com', '13855605201', 1, 0, '我要喝可乐', 0, NOW(), NOW()),
    (4, 'ceshi', '$2a$12$5ly8VSaDSuTxhJZmbaX9yekAya69cdfldVCjOZo.hYVNKMsrxmSAW', '测试昵称', '测试用户', 0,  '1234567890@qq.com', '18866668888', 1, 0, '用于测试系统功能', 0, NOW(), NOW());

-- 初始化角色
INSERT IGNORE INTO `system_role` (`id`, `role_code`, `role_name`, `role_type`, `sort_order`, `status`, `remark`, `deleted`, `create_time`, `update_time`)
VALUES
    (1, 'superAdmin', '超级管理员', 'system', 1, 1, '系统角色不可删除', 0, NOW(), NOW()),
    (2, 'admin', '管理员', 'system', 2, 1, '系统角色不可删除', 0, NOW(), NOW());

-- 给用户分配角色
INSERT IGNORE INTO `system_user_role` (`id`, `user_id`, `role_id`, `create_time`)
VALUES
    (1, 1, 1, NOW()),
    (2, 2, 2, NOW()),
    (3, 3, 2, NOW()),
    (4, 4, 2, NOW());

-- 初始化权限数据
INSERT IGNORE INTO `system_permission` (`id`, `parent_id`, `permission_type`, `permission_name`, `permission_code`,
                                 `path`, `component`, `icon`, `sort_order`, `visible`, `status`, `deleted`, `create_time`, `update_time`)
VALUES
    -- 系统管理（目录）
    (1, 0, 'catalog', '系统管理', 'SYSTEM', null, null, 'Setting', 1, 1, 1, 0, NOW(), NOW()),

    -- 用户管理（菜单 + 按钮）
    (2, 1, 'menu', '用户管理', 'SYSTEM_USER', '/system/userPage', '/system/user/UserPage.vue', 'User', 10, 1, 1, 0, NOW(), NOW()),
    (3, 2, 'button', '用户-查看', 'SYSTEM_USER_VIEW', NULL, NULL, NULL, 11, 1, 1, 0, NOW(), NOW()),
    (4, 2, 'button', '用户-新增', 'SYSTEM_USER_CREATE', NULL, NULL, NULL, 12, 1, 1, 0, NOW(), NOW()),
    (5, 2, 'button', '用户-编辑', 'SYSTEM_USER_UPDATE', NULL, NULL, NULL, 13, 1, 1, 0, NOW(), NOW()),
    (6, 2, 'button', '用户-删除', 'SYSTEM_USER_DELETE', NULL, NULL, NULL, 14, 1, 1, 0, NOW(), NOW()),
    (7, 2, 'button', '用户-重置密码', 'SYSTEM_USER_RESET_PASSWORD', NULL, NULL, NULL, 15, 1, 1, 0, NOW(), NOW()),
    (8, 2, 'button', '用户-解锁', 'SYSTEM_USER_UNLOCK', NULL, NULL, NULL, 16, 1, 1, 0, NOW(), NOW()),

    -- 角色管理（菜单 + 按钮）
    (9, 1, 'menu', '角色管理', 'SYSTEM_ROLE', '/system/rolePage', '/system/role/RolePage.vue', 'Key', 20, 1, 1, 0, NOW(), NOW()),
    (10, 9, 'button', '角色-查看', 'SYSTEM_ROLE_VIEW', NULL, NULL, NULL, 21, 1, 1, 0, NOW(), NOW()),
    (11, 9, 'button', '角色-新增', 'SYSTEM_ROLE_CREATE', NULL, NULL, NULL, 22, 1, 1, 0, NOW(), NOW()),
    (12, 9, 'button', '角色-编辑', 'SYSTEM_ROLE_UPDATE', NULL, NULL, NULL, 23, 1, 1, 0, NOW(), NOW()),
    (13, 9, 'button', '角色-删除', 'SYSTEM_ROLE_DELETE', NULL, NULL, NULL, 24, 1, 1, 0, NOW(), NOW()),
    (14, 9, 'button', '角色-分配权限', 'SYSTEM_ROLE_ASSIGN', NULL, NULL, NULL, 25, 1, 1, 0, NOW(), NOW()),

    -- 菜单管理（菜单 + 按钮）
    (15, 1, 'menu', '菜单管理', 'SYSTEM_MENU', '/system/menuPage', '/system/menu/MenuPage.vue', 'Menu', 30, 1, 1, 0, NOW(), NOW()),
    (16, 15, 'button', '菜单-查看', 'SYSTEM_MENU_VIEW', NULL, NULL, NULL, 31, 1, 1, 0, NOW(), NOW()),
    (17, 15, 'button', '菜单-新增', 'SYSTEM_MENU_CREATE', NULL, NULL, NULL, 32, 1, 1, 0, NOW(), NOW()),
    (18, 15, 'button', '菜单-编辑', 'SYSTEM_MENU_UPDATE', NULL, NULL, NULL, 33, 1, 1, 0, NOW(), NOW()),
    (19, 15, 'button', '菜单-删除', 'SYSTEM_MENU_DELETE', NULL, NULL, NULL, 34, 1, 1, 0, NOW(), NOW()),

    -- 部门管理（菜单 + 按钮）
    (20, 1, 'menu', '部门管理', 'SYSTEM_DEPT', '/system/deptPage', '/system/dept/DeptPage.vue', 'OfficeBuilding', 40, 1, 1, 0, NOW(), NOW()),
    (21, 20, 'button', '部门-查看', 'SYSTEM_DEPT_VIEW', NULL, NULL, NULL, 41, 1, 1, 0, NOW(), NOW()),
    (22, 20, 'button', '部门-新增', 'SYSTEM_DEPT_CREATE', NULL, NULL, NULL, 42, 1, 1, 0, NOW(), NOW()),
    (23, 20, 'button', '部门-编辑', 'SYSTEM_DEPT_UPDATE', NULL, NULL, NULL, 43, 1, 1, 0, NOW(), NOW()),
    (24, 20, 'button', '部门-删除', 'SYSTEM_DEPT_DELETE', NULL, NULL, NULL, 44, 1, 1, 0, NOW(), NOW()),

    -- 岗位管理（菜单 + 按钮）
    (25, 1, 'menu', '岗位管理', 'SYSTEM_POST', '/system/postPage', '/system/post/PostPage.vue', 'Briefcase', 50, 1, 1, 0, NOW(), NOW()),
    (26, 25, 'button', '岗位-查看', 'SYSTEM_POST_VIEW', NULL, NULL, NULL, 51, 1, 1, 0, NOW(), NOW()),
    (27, 25, 'button', '岗位-新增', 'SYSTEM_POST_CREATE', NULL, NULL, NULL, 52, 1, 1, 0, NOW(), NOW()),
    (28, 25, 'button', '岗位-编辑', 'SYSTEM_POST_UPDATE', NULL, NULL, NULL, 53, 1, 1, 0, NOW(), NOW()),
    (29, 25, 'button', '岗位-删除', 'SYSTEM_POST_DELETE', NULL, NULL, NULL, 54, 1, 1, 0, NOW(), NOW()),

    -- 系统配置管理（菜单 + 按钮）
    (30, 1, 'menu', '系统配置管理', 'SYSTEM_CONFIG', '/system/configPage', '/system/config/ConfigPage.vue', 'Monitor', 60, 1, 1, 0, NOW(), NOW()),
    (31, 30, 'button', '系统配置-查看', 'SYSTEM_CONFIG_VIEW', NULL, NULL, NULL, 61, 1, 1, 0, NOW(), NOW()),
    (32, 30, 'button', '系统配置-新增', 'SYSTEM_CONFIG_CREATE', NULL, NULL, NULL, 62, 1, 1, 0, NOW(), NOW()),
    (33, 30, 'button', '系统配置-编辑', 'SYSTEM_CONFIG_UPDATE', NULL, NULL, NULL, 63, 1, 1, 0, NOW(), NOW()),
    (34, 30, 'button', '系统配置-删除', 'SYSTEM_CONFIG_DELETE', NULL, NULL, NULL, 64, 1, 1, 0, NOW(), NOW()),

    -- 数据字典管理（菜单 + 按钮）
    (35, 1, 'menu', '数据字典管理', 'SYSTEM_DICT', '/system/dictPage', '/system/dict/DictPage.vue', 'Document', 70, 1, 1, 0, NOW(), NOW()),
    (36, 35, 'button', '数据字典-查看', 'SYSTEM_DICT_VIEW', NULL, NULL, NULL, 71, 1, 1, 0, NOW(), NOW()),
    (37, 35, 'button', '数据字典-新增', 'SYSTEM_DICT_CREATE', NULL, NULL, NULL, 72, 1, 1, 0, NOW(), NOW()),
    (38, 35, 'button', '数据字典-编辑', 'SYSTEM_DICT_UPDATE', NULL, NULL, NULL, 73, 1, 1, 0, NOW(), NOW()),
    (39, 35, 'button', '数据字典-删除', 'SYSTEM_DICT_DELETE', NULL, NULL, NULL, 74, 1, 1, 0, NOW(), NOW()),

    -- 附件管理（菜单 + 按钮）
    (40, 1, 'menu', '附件管理', 'SYSTEM_ATTACHMENT', '/system/attachmentPage', '/system/attachment/AttachmentPage.vue', 'Folder', 80, 1, 1, 0, NOW(), NOW()),
    (41, 40, 'button', '附件-查看', 'SYSTEM_ATTACHMENT_VIEW', NULL, NULL, NULL, 81, 1, 1, 0, NOW(), NOW()),
    (42, 40, 'button', '附件-上传', 'SYSTEM_ATTACHMENT_UPLOAD', NULL, NULL, NULL, 82, 1, 1, 0, NOW(), NOW()),
    (43, 40, 'button', '附件-编辑', 'SYSTEM_ATTACHMENT_UPDATE', NULL, NULL, NULL, 83, 1, 1, 0, NOW(), NOW()),
    (44, 40, 'button', '附件-删除', 'SYSTEM_ATTACHMENT_DELETE', NULL, NULL, NULL, 84, 1, 1, 0, NOW(), NOW()),
    (45, 40, 'button', '附件-下载', 'SYSTEM_ATTACHMENT_DOWNLOAD', NULL, NULL, NULL, 85, 1, 1, 0, NOW(), NOW()),

    -- 定时任务（目录）
    (46, 0, 'catalog', '定时任务', 'JOB', NULL, NULL, 'Timer', 90, 1, 1, 0, NOW(), NOW()),

    -- 定时任务管理（菜单 + 按钮）
    (47, 46, 'menu', '定时任务管理', 'JOB_MANAGEMENT', '/mms-job/jobPage', '/job/management/JobPage.vue', 'Timer', 91, 1, 1, 0, NOW(), NOW()),
    (48, 47, 'button', '定时任务-查看', 'JOB_MANAGEMENT_VIEW', NULL, NULL, NULL, 92, 1, 1, 0, NOW(), NOW()),
    (49, 47, 'button', '定时任务-新增', 'JOB_MANAGEMENT_CREATE', NULL, NULL, NULL, 93, 1, 1, 0, NOW(), NOW()),
    (50, 47, 'button', '定时任务-编辑', 'JOB_MANAGEMENT_UPDATE', NULL, NULL, NULL, 94, 1, 1, 0, NOW(), NOW()),
    (51, 47, 'button', '定时任务-删除', 'JOB_MANAGEMENT_DELETE', NULL, NULL, NULL, 95, 1, 1, 0, NOW(), NOW()),
    (52, 47, 'button', '定时任务-执行', 'JOB_MANAGEMENT_RUN', NULL, NULL, NULL, 96, 1, 1, 0, NOW(), NOW()),

    -- 定时任务执行记录（菜单 + 按钮）
    (53, 46, 'menu', '定时任务执行记录', 'JOB_RUN_LOG', '/mms-job/jobRunLogPage', '/job/log/JobRunLogPage.vue', 'List', 100, 1, 1, 0, NOW(), NOW()),
    (54, 53, 'button', '定时任务执行记录-查看', 'JOB_RUN_LOG_VIEW', NULL, NULL, NULL, 101, 1, 1, 0, NOW(), NOW()),
    (55, 53, 'button', '定时任务执行记录-删除', 'JOB_RUN_LOG_DELETE', NULL, NULL, NULL, 102, 1, 1, 0, NOW(), NOW()),
    (56, 53, 'button', '定时任务执行记录-导出', 'JOB_RUN_LOG_EXPORT', NULL, NULL, NULL, 103, 1, 1, 0, NOW(), NOW()),
    (57, 53, 'button', '定时任务执行记录-重试执行', 'JOB_RUN_LOG_RETRY', NULL, NULL, NULL, 104, 1, 1, 0, NOW(), NOW()),
    (58, 53, 'button', '定时任务执行记录-终止执行', 'JOB_RUN_LOG_TERMINATE', NULL, NULL, NULL, 105, 1, 1, 0, NOW(), NOW()),

    -- 审计中心（目录）
    (59, 0, 'catalog', '审计中心', 'AUDIT', NULL, NULL, 'DataAnalysis', 110, 1, 1, 0, NOW(), NOW()),

    -- 登录日志（菜单 + 按钮）
    (60, 59, 'menu', '登录日志', 'AUDIT_LOGIN_LOG', '/audit/loginLogPage', '/audit/loginLog/LoginLogPage.vue', 'Lock', 111, 1, 1, 0, NOW(), NOW()),
    (61, 60, 'button', '登录日志-查看', 'AUDIT_LOGIN_LOG_VIEW', NULL, NULL, NULL, 112, 1, 1, 0, NOW(), NOW()),
    (62, 60, 'button', '登录日志-删除', 'AUDIT_LOGIN_LOG_DELETE', NULL, NULL, NULL, 113, 1, 1, 0, NOW(), NOW()),
    (63, 60, 'button', '登录日志-导出', 'AUDIT_LOGIN_LOG_EXPORT', NULL, NULL, NULL, 114, 1, 1, 0, NOW(), NOW()),

    -- 操作日志（菜单 + 按钮）
    (64, 59, 'menu', '操作日志', 'AUDIT_OPERATION_LOG', '/audit/operationLogPage', '/audit/operationLog/OperationLogPage.vue', 'DocumentCopy', 121, 1, 1, 0, NOW(), NOW()),
    (65, 64, 'button', '操作日志-查看', 'AUDIT_OPERATION_LOG_VIEW', NULL, NULL, NULL, 122, 1, 1, 0, NOW(), NOW()),
    (66, 64, 'button', '操作日志-删除', 'AUDIT_OPERATION_LOG_DELETE', NULL, NULL, NULL, 123, 1, 1, 0, NOW(), NOW()),
    (67, 64, 'button', '操作日志-导出', 'AUDIT_OPERATION_LOG_EXPORT', NULL, NULL, NULL, 124, 1, 1, 0, NOW(), NOW()),

    -- 异常日志（菜单 + 按钮）
    (68, 59, 'menu', '异常日志', 'AUDIT_EXCEPTION_LOG', '/audit/exceptionLogPage', '/audit/exceptionLog/ExceptionLogPage.vue', 'WarningFilled', 131, 1, 1, 0, NOW(), NOW()),
    (69, 68, 'button', '异常日志-查看', 'AUDIT_EXCEPTION_LOG_VIEW', NULL, NULL, NULL, 132, 1, 1, 0, NOW(), NOW()),
    (70, 68, 'button', '异常日志-删除', 'AUDIT_EXCEPTION_LOG_DELETE', NULL, NULL, NULL, 133, 1, 1, 0, NOW(), NOW()),
    (71, 68, 'button', '异常日志-标记已处理', 'AUDIT_EXCEPTION_LOG_RESOLVE', NULL, NULL, NULL, 134, 1, 1, 0, NOW(), NOW()),

    -- 接口访问日志（菜单 + 按钮）
    (72, 59, 'menu', '接口访问日志', 'AUDIT_API_ACCESS_LOG', '/audit/apiAccessLogPage', '/audit/apiAccessLog/ApiAccessLogPage.vue', 'Histogram', 151, 1, 1, 0, NOW(), NOW()),
    (73, 72, 'button', '接口访问日志-查看', 'AUDIT_API_ACCESS_LOG_VIEW', NULL, NULL, NULL, 152, 1, 1, 0, NOW(), NOW()),
    (74, 72, 'button', '接口访问日志-删除', 'AUDIT_API_ACCESS_LOG_DELETE', NULL, NULL, NULL, 153, 1, 1, 0, NOW(), NOW()),
    (75, 72, 'button', '接口访问日志-导出', 'AUDIT_API_ACCESS_LOG_EXPORT', NULL, NULL, NULL, 154, 1, 1, 0, NOW(), NOW()),
    (76, 72, 'button', '接口访问日志-统计分析', 'AUDIT_API_ACCESS_LOG_ANALYZE', NULL, NULL, NULL, 155, 1, 1, 0, NOW(), NOW()),

    -- 安全中心（目录）
    (77, 0, 'catalog', '安全中心', 'SECURITY', NULL, NULL, 'Lock', 140, 1, 1, 0, NOW(), NOW()),

    -- 在线用户（菜单 + 按钮）
    (78, 77, 'menu', '在线用户', 'SECURITY_ONLINE_USER', '/security/onlineUserPage', '/security/onlineUser/OnlineUserPage.vue', 'Connection', 141, 1, 1, 0, NOW(), NOW()),
    (79, 78, 'button', '在线用户-查看', 'SECURITY_ONLINE_USER_VIEW', NULL, NULL, NULL, 142, 1, 1, 0, NOW(), NOW()),
    (80, 78, 'button', '在线用户-强制下线', 'SECURITY_ONLINE_USER_FORCE_LOGOUT', NULL, NULL, NULL, 143, 1, 1, 0, NOW(), NOW());

-- 将所有权限授予【超级管理员角色】和【管理员角色】
INSERT IGNORE INTO `system_role_permission` (`id`, `role_id`, `permission_id`, `create_time`)
SELECT 
    ROW_NUMBER() OVER (ORDER BY rp.role_id, p.id) AS id,
    rp.role_id, 
    p.id AS permission_id, 
    NOW() AS create_time
FROM `system_permission` p
         CROSS JOIN (
             SELECT 1 AS role_id
             UNION ALL
             SELECT 2
         ) AS rp;

-- 初始化部门数据
INSERT IGNORE INTO `system_dept` (`id`, `parent_id`, `dept_name`, `dept_code`, `leader`, `phone`, `email`, `sort_order`, `status`, `remark`, `deleted`, `create_time`, `update_time`)
VALUES
    (1, 0, '董事会', 'ROOT', '李鸿羽', '18255097030', '2722562862@qq.com', 1, 1, '根部门', 0, NOW(), NOW()),
    -- 董事会直辖
    (2, 1, '技术中心', 'TECH_CENTER', '', '', '', 10, 1, '研发与技术体系', 0, NOW(), NOW()),
    (3, 1, '产品中心', 'PRODUCT_CENTER', '', '', '', 20, 1, '产品规划与需求管理', 0, NOW(), NOW()),
    (4, 1, '市场部', 'MARKETING', '', '', '', 30, 1, '市场推广与品牌建设', 0, NOW(), NOW()),
    (5, 1, '销售部', 'SALES', '', '', '', 40, 1, '销售与客户拓展', 0, NOW(), NOW()),
    (6, 1, '运营部', 'OPERATIONS', '', '', '', 50, 1, '用户/业务运营与活动执行', 0, NOW(), NOW()),
    (7, 1, '财务部', 'FINANCE', '', '', '', 60, 1, '财务核算与资金管理', 0, NOW(), NOW()),
    (8, 1, '人力行政部', 'HR_ADMIN', '', '', '', 70, 1, '人力资源与行政支持', 0, NOW(), NOW()),
    (9, 1, '总经办', 'GM_OFFICE', '', '', '', 80, 1, '经营管理与跨部门协调', 0, NOW(), NOW()),

    -- 技术中心下属
    (10, 2, '架构组', 'ARCH_TEAM', '', '', '', 101, 1, '架构设计与技术选型', 0, NOW(), NOW()),
    (11, 2, '后端组', 'BE_TEAM', '', '', '', 102, 1, '后端研发', 0, NOW(), NOW()),
    (12, 2, '前端组', 'FE_TEAM', '', '', '', 103, 1, '前端研发', 0, NOW(), NOW()),
    (13, 2, '移动端组', 'MOBILE_TEAM', '', '', '', 104, 1, 'iOS/Android 研发', 0, NOW(), NOW()),
    (14, 2, '测试质量部', 'QA', '', '', '', 105, 1, '测试与质量保障', 0, NOW(), NOW()),
    (15, 2, '运维安全部', 'DEVOPS_SEC', '', '', '', 106, 1, '运维与安全', 0, NOW(), NOW()),
    (16, 2, '数据平台组', 'DATA_PLATFORM', '', '', '', 107, 1, '数据平台与数据分析支撑', 0, NOW(), NOW()),

    -- 产品中心下属
    (17, 3, '产品一部', 'PRODUCT_1', '', '', '', 201, 1, '核心产品线', 0, NOW(), NOW()),
    (18, 3, '产品二部', 'PRODUCT_2', '', '', '', 202, 1, '增长/创新产品线', 0, NOW(), NOW()),
    (19, 3, '设计部', 'DESIGN', '', '', '', 203, 1, 'UI/UX 设计', 0, NOW(), NOW());


-- 初始化岗位数据
INSERT IGNORE INTO `system_post` (`id`, `post_code`, `post_name`, `post_level`, `sort_order`, `status`, `remark`, `deleted`, `create_time`, `update_time`)
VALUES
    -- P1级别：战略决策层
    (1, 'Chairman', '董事长', 'P1', 1, 1, '公司法人代表，董事会主席，最高决策者', 0, NOW(), NOW()),
    (2, 'CEO', '首席执行官', 'P1', 2, 1, '公司最高行政长官，全面负责公司战略和运营', 0, NOW(), NOW()),

    -- P2级别：执行管理层
    (3, 'President', '总裁', 'P2', 3, 1, '全面负责公司运营和管理', 0, NOW(), NOW()),
    (4, 'COO', '首席运营官', 'P2', 4, 1, '负责公司日常运营，向CEO汇报', 0, NOW(), NOW()),
    (5, 'CFO', '首席财务官', 'P2', 5, 1, '负责公司财务战略和资金管理', 0, NOW(), NOW()),
    (6, 'CTO', '首席技术官', 'P2', 6, 1, '负责公司技术战略和研发体系', 0, NOW(), NOW()),

    -- P3级别：高级管理层
    (7, 'VP_Engineering', '技术副总裁', 'P3', 7, 1, '负责技术团队管理和架构设计', 0, NOW(), NOW()),
    (8, 'VP_Product', '产品副总裁', 'P3', 8, 1, '负责产品战略和产品线管理', 0, NOW(), NOW()),
    (9, 'VP_Marketing', '市场副总裁', 'P3', 9, 1, '负责市场战略和品牌建设', 0, NOW(), NOW()),
    (10, 'VP_Sales', '销售副总裁', 'P3', 10, 1, '负责销售体系和业绩达成', 0, NOW(), NOW()),
    (11, 'VP_HR', '人力资源副总裁', 'P3', 11, 1, '负责人力资源战略和组织发展', 0, NOW(), NOW()),

    -- P4级别：总监层
    (12, 'Director_Tech', '技术总监', 'P4', 12, 1, '负责技术团队管理和项目管理', 0, NOW(), NOW()),
    (13, 'Director_Product', '产品总监', 'P4', 13, 1, '负责产品规划和团队管理', 0, NOW(), NOW()),
    (14, 'Director_Design', '设计总监', 'P4', 14, 1, '负责设计体系和创意管理', 0, NOW(), NOW()),
    (15, 'Director_Data', '数据总监', 'P4', 15, 1, '负责数据平台和数据分析体系', 0, NOW(), NOW()),
    (16, 'Director_Operations', '运营总监', 'P4', 16, 1, '负责用户运营和业务运营', 0, NOW(), NOW()),

    -- P5级别：经理层
    (17, 'Manager_Backend', '后端开发经理', 'P5', 17, 1, '负责后端技术团队管理', 0, NOW(), NOW()),
    (18, 'Manager_Frontend', '前端开发经理', 'P5', 18, 1, '负责前端技术团队管理', 0, NOW(), NOW()),
    (19, 'Manager_Mobile', '移动端开发经理', 'P5', 19, 1, '负责iOS/Android团队管理', 0, NOW(), NOW()),
    (20, 'Manager_Testing', '测试经理', 'P5', 20, 1, '负责测试团队和质量保障', 0, NOW(), NOW()),
    (21, 'Manager_DevOps', '运维经理', 'P5', 21, 1, '负责运维团队和系统稳定性', 0, NOW(), NOW()),

    -- P6级别：专家/高级工程师
    (22, 'Senior_Architect', '高级架构师', 'P6', 22, 1, '负责系统架构设计和技术选型', 0, NOW(), NOW()),
    (23, 'Senior_Engineer', '高级工程师', 'P6', 23, 1, '核心开发人员，解决复杂问题', 0, NOW(), NOW()),
    (24, 'Senior_Product', '高级产品经理', 'P6', 24, 1, '负责重要产品线的产品设计', 0, NOW(), NOW()),
    (25, 'Senior_Data', '高级数据分析师', 'P6', 25, 1, '深度数据分析和建模', 0, NOW(), NOW()),

    -- P7级别：工程师/专员
    (26, 'Engineer', '工程师', 'P7', 26, 1, '开发工程师，负责具体模块开发', 0, NOW(), NOW()),
    (27, 'Product_Manager', '产品经理', 'P7', 27, 1, '负责产品设计和需求管理', 0, NOW(), NOW()),
    (28, 'UI_Designer', 'UI设计师', 'P7', 28, 1, '用户界面设计', 0, NOW(), NOW()),
    (29, 'Data_Analyst', '数据分析师', 'P7', 29, 1, '基础数据分析工作', 0, NOW(), NOW()),
    (30, 'Operations', '运营专员', 'P7', 30, 1, '用户运营和活动执行', 0, NOW(), NOW()),

    -- P8级别：助理/初级
    (31, 'Assistant_Engineer', '助理工程师', 'P8', 31, 1, '初级开发人员，在指导下工作', 0, NOW(), NOW()),
    (32, 'Intern', '实习生', 'P8', 32, 1, '在校实习生', 0, NOW(), NOW());


-- 给用户分配部门
INSERT IGNORE INTO `system_user_dept` (`id`, `user_id`, `dept_id`, `is_primary`, `create_time`)
VALUES
    (1, 1, 2, 1, NOW()),
    (2, 2, 1, 1, NOW()),
    (3, 3, 1, 1, NOW()),
    (4, 4, 14, 1, NOW());

-- 给用户分配岗位
INSERT IGNORE INTO `system_user_post` (`id`, `user_id`, `post_id`, `is_primary`, `create_time`)
VALUES
    (1, 1, 6, 1, NOW()),
    (2, 2, 1, 1, NOW()),
    (3, 3, 2, 1, NOW()),
    (4, 4, 20, 1, NOW());

-- 初始化系统配置数据
INSERT IGNORE INTO `system_config` (`id`, `config_key`, `config_value`, `config_type`, `config_name`, `status`, `editable`, `remark`, `deleted`, `create_time`, `update_time`)
VALUES
    (1, 'system.name', 'MyManagementSystem', 'string', '系统名称', 1, 1, '系统名称配置', 0, NOW(), NOW()),
    (2, 'system.version', '1.0.0', 'string', '系统版本', 1, 1, '系统版本号', 0, NOW(), NOW()),
    (3, 'system.copyright', '© 2025 MyManagementSystem', 'string', '版权信息', 1, 1, '系统版权信息', 0, NOW(), NOW());

-- 初始化数据字典类型（通用字典类型放在前面）
INSERT IGNORE INTO `system_dict_type` (`id`, `dict_type_code`, `dict_type_name`, `status`, `sort_order`, `remark`, `deleted`, `create_time`, `update_time`)
VALUES
    -- 通用字典类型
    (1, 'common_status', '通用状态', 1, 1, '通用的启用/禁用状态', 0, NOW(), NOW()),
    (2, 'yes_no', '是否', 1, 2, '通用布尔值（是/否）', 0, NOW(), NOW()),
    (3, 'menu_visible', '菜单显示状态', 1, 3, '菜单/权限显示状态', 0, NOW(), NOW()),
    (4, 'config_type', '参数配置类型', 1, 4, 'system config 中的类型', 0, NOW(), NOW()),
    -- 业务相关字典类型
    (5, 'user_lock_status', '用户锁定状态', 1, 10, '用户是否被锁定', 0, NOW(), NOW()),
    (6, 'user_gender', '用户性别', 1, 11, '用户性别选项', 0, NOW(), NOW()),
    (7, 'role_type', '角色类型', 1, 12, '角色类型分类', 0, NOW(), NOW()),
    (8, 'attachment_business_type', '附件业务类型', 1, 14, '附件业务类型', 0, NOW(), NOW()),
    (9, 'attachment_file_type', '附件文件类型', 1, 15, '扩展名', 0, NOW(), NOW()),
    (10, 'permission_type', '权限类型', 1, 16, '权限类型分类', 0, NOW(), NOW()),
    (11, 'service_name', '服务名', 1, 17, '服务名', 0, NOW(), NOW()),
    (12, 'job_type', '定时任务类型', 1, 18, '定时任务类型', 0, NOW(), NOW()),
    (13, 'Job_run_mode', '定时任务运行模式', 1, 19, '定时任务运行模式', 0, NOW(), NOW()),
    (14, 'job_status', '定时任务状态', 1, 20, '定时任务状态', 0, NOW(), NOW()),
    (15, 'login_status', '登录状态', 1, 13, '用户登录操作的结果状态', 0, NOW(), NOW()),
    (16, 'login_type', '登录类型', 1, 13, '用户登录的方式或类型', 0, NOW(), NOW());

-- 初始化数据字典数据
INSERT IGNORE INTO `system_dict_data` (`id`, `dict_type_id`, `dict_label`, `dict_value`, `dict_sort`, `is_default`, `status`, `remark`, `deleted`, `create_time`, `update_time`)
VALUES
    -- 通用状态
    (1, 1, '启用', '1', 1, 1, 1, '启用状态', 0, NOW(), NOW()),
    (2, 1, '禁用', '0', 2, 0, 1, '禁用状态', 0, NOW(), NOW()),
    -- 是否
    (3, 2, '是', '1', 1, 1, 1, '是 / true', 0, NOW(), NOW()),
    (4, 2, '否', '0', 2, 0, 1, '否 / false', 0, NOW(), NOW()),
    -- 菜单显示状态
    (5, 3, '显示', '1', 1, 1, 1, '菜单在前端展示', 0, NOW(), NOW()),
    (6, 3, '隐藏', '0', 2, 0, 1, '菜单在前端隐藏', 0, NOW(), NOW()),
    -- 参数配置类型
    (7, 4, '字符串', 'string', 1, 1, 1, '字符串类型配置', 0, NOW(), NOW()),
    (8, 4, '数字', 'number', 2, 0, 1, '数字类型配置', 0, NOW(), NOW()),
    (9, 4, '布尔', 'boolean', 3, 0, 1, '布尔类型配置', 0, NOW(), NOW()),
    (10, 4, 'JSON', 'json', 4, 0, 1, 'JSON 类型配置', 0, NOW(), NOW()),
    -- 用户锁定状态
    (11, 5, '未锁定', '0', 1, 1, 1, '用户正常', 0, NOW(), NOW()),
    (12, 5, '已锁定', '1', 2, 0, 1, '用户被锁定', 0, NOW(), NOW()),
    -- 用户性别
    (13, 6, '未知', '0', 1, 1, 1, '性别未知', 0, NOW(), NOW()),
    (14, 6, '男', '1', 2, 0, 1, '男性', 0, NOW(), NOW()),
    (15, 6, '女', '2', 3, 0, 1, '女性', 0, NOW(), NOW()),
    -- 角色类型
    (16, 7, '系统角色', 'system', 1, 0, 1, '系统内置角色，不可删除', 0, NOW(), NOW()),
    (17, 7, '自定义角色', 'custom', 2, 1, 1, '用户自定义角色', 0, NOW(), NOW()),
    -- 附件业务类型
    (18, 8, '用户头像', 'user_avatar', 1, 0, 1, '业务ID为用户ID', 0, NOW(), NOW()),
    (19, 8, '系统附件', 'system_attachment', 2, 0, 1, '系统附件管理-资源库上传', 0, NOW(), NOW()),
    -- 附件类型（扩展名）
    (20, 9, 'jpg', 'jpg', 1, 0, 1, 'jpg', 0, NOW(), NOW()),
    (21, 9, 'jpeg', 'jpeg', 2, 0, 1, 'jpeg', 0, NOW(), NOW()),
    (22, 9, 'png', 'png', 3, 0, 1, 'png', 0, NOW(), NOW()),
    (23, 9, 'gif', 'gif', 4, 0, 1, 'gif', 0, NOW(), NOW()),
    (24, 9, 'bmp', 'bmp', 5, 0, 1, 'bmp', 0, NOW(), NOW()),
    (25, 9, 'webp', 'webp', 6, 0, 1, 'webp', 0, NOW(), NOW()),
    (26, 9, 'doc', 'doc', 7, 0, 1, 'doc', 0, NOW(), NOW()),
    (27, 9, 'docx', 'docx', 8, 0, 1, 'docx', 0, NOW(), NOW()),
    (28, 9, 'xls', 'xls', 9, 0, 1, 'xls', 0, NOW(), NOW()),
    (29, 9, 'xlsx', 'xlsx', 10, 0, 1, 'xlsx', 0, NOW(), NOW()),
    (30, 9, 'ppt', 'ppt', 11, 0, 1, 'ppt', 0, NOW(), NOW()),
    (31, 9, 'pptx', 'pptx', 12, 0, 1, 'pptx', 0, NOW(), NOW()),
    (32, 9, 'pdf', 'pdf', 13, 0, 1, 'pdf', 0, NOW(), NOW()),
    (33, 9, 'txt', 'txt', 14, 0, 1, 'txt', 0, NOW(), NOW()),
    (34, 9, 'md', 'md', 15, 0, 1, 'md', 0, NOW(), NOW()),
    -- 权限类型
    (35, 10, '目录', 'catalog', 1, 0, 1, '目录权限', 0, NOW(), NOW()),
    (36, 10, '菜单', 'menu', 2, 0, 1, '菜单权限', 0, NOW(), NOW()),
    (37, 10, '按钮', 'button', 3, 0, 1, '按钮权限', 0, NOW(), NOW()),
    (38, 10, '接口', 'api', 4, 0, 1, '接口权限', 0, NOW(), NOW()),
    -- 服务名
    (39, 11, '基础数据服务', 'base', 1, 0, 1, '基础数据服务（base）', 0, NOW(), NOW()),
    (40, 11, '用户中心服务', 'usercenter', 1, 0, 1, '用户中心服务（usercenter）', 0, NOW(), NOW()),
    -- 定时任务类型
    (41, 12, '附件清理任务', 'ATTACHMENT_CLEAN', 1, 0, 1, '附件清理任务', 0, NOW(), NOW()),
    -- 定时任务运行模式
    (42, 13, '集群单实例执行', 'single', 1, 1, 1, '集群单实例执行', 0, NOW(), NOW()),
    (43, 13, '全实例执行', 'all', 2, 0, 1, '全实例执行', 0, NOW(), NOW()),
    -- 定时任务运行模式
    (44, 14, '运行中', 'running', 1, 0, 1, '运行中', 0, NOW(), NOW()),
    (45, 14, '成功', 'success', 2, 0, 1, '成功', 0, NOW(), NOW()),
    (46, 14, '失败', 'fail', 3, 0, 1, '失败', 0, NOW(), NOW()),
    (47, 14, '超时', 'timeout', 4, 0, 1, '超时''', 0, NOW(), NOW()),
    (48, 14, '跳过', 'skip', 5, 0, 1, '跳过', 0, NOW(), NOW()),
    (49, 15, '失败', '0', 1, 0, 1, '登录失败', 0, NOW(), NOW()),
    (50, 15, '成功', '1', 2, 1, 1, '登录成功', 0, NOW(), NOW()),
    (51, 16, '密码登录', 'password', 1, 1, 1, '通过账号密码登录', 0, NOW(), NOW()),
    (52, 16, '短信登录', 'sms', 2, 0, 1, '通过短信验证码登录', 0, NOW(), NOW()),
    (53, 16, '邮箱登录', 'email', 3, 0, 1, '通过邮箱验证码登录', 0, NOW(), NOW());
-- 初始化定时任务数据
INSERT IGNORE INTO `job_def` (`id`,`service_name`,`job_code`,`job_name`,`job_type`,`cron_expr`,`run_mode`,`enabled`,`timeout_ms`,`remark`,`params_json`,`deleted`,`create_by`,`create_time`,`update_by`,`update_time`)
VALUES
    (1, 'base', 'ATTACHMENT_CLEAN', '附件清理任务', 'ATTACHMENT_CLEAN', '0 0 2 * * ?', 'single', 1, 0, '定期清理已逻辑删除的附件，物理删除文件和记录', '{"batchSize": 100, "deletePhysicalFile": true, "storageType": "local", "businessType": null, "fileType": null, "maxFileSize": 10485760, "minFileSize": 1024, "pathPattern": null, "retryCount": 2, "continueOnError": true, "orderBy": "id"}', 0, 1, NOW(), 1, NOW());