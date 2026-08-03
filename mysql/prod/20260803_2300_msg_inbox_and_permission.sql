USE `mms_prod_core`;

-- 系统公告发件表
CREATE TABLE IF NOT EXISTS `msg_sys_announce` (
    `id` bigint NOT NULL COMMENT '主键ID',
    `title` varchar(200) NOT NULL COMMENT '公告标题',
    `content_html` mediumtext NOT NULL COMMENT '净化后的富文本',
    `content_text` varchar(500) DEFAULT NULL COMMENT '纯文本摘要',
    `scope_type` tinyint NOT NULL COMMENT '范围：1指定人 2角色 3全员',
    `scope_payload` json DEFAULT NULL COMMENT '用户ID/角色ID列表快照',
    `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0待发送 1发送中 2已完成 3失败',
    `total_target` int NOT NULL DEFAULT 0 COMMENT '目标人数',
    `success_count` int NOT NULL DEFAULT 0 COMMENT '成功人数',
    `fail_count` int NOT NULL DEFAULT 0 COMMENT '失败人数',
    `cursor_json` json DEFAULT NULL COMMENT '扇出进度',
    `error_msg` varchar(500) DEFAULT NULL COMMENT '错误信息',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_status_ctime` (`status`, `create_time`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='系统公告发件表';

-- 系统通知收件箱
CREATE TABLE IF NOT EXISTS `msg_sys_inbox` (
    `id` bigint NOT NULL COMMENT '主键ID',
    `user_id` bigint NOT NULL COMMENT '收件人',
    `announce_id` bigint DEFAULT NULL COMMENT '关联公告ID',
    `biz_type` varchar(64) DEFAULT NULL COMMENT '业务类型',
    `biz_id` varchar(64) DEFAULT NULL COMMENT '业务关联ID',
    `title` varchar(200) NOT NULL COMMENT '标题',
    `content_html` mediumtext DEFAULT NULL COMMENT '富文本正文',
    `content_text` varchar(2000) NOT NULL COMMENT '纯文本或摘要',
    `starred` tinyint NOT NULL DEFAULT 0 COMMENT '是否收藏：0否 1是',
    `read_flag` tinyint NOT NULL DEFAULT 0 COMMENT '是否已读：0未读 1已读',
    `read_time` datetime DEFAULT NULL COMMENT '已读时间',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_announce_user` (`announce_id`, `user_id`),
    KEY `idx_user_list` (`user_id`, `deleted`, `read_flag`, `create_time`),
    KEY `idx_user_star` (`user_id`, `deleted`, `starred`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='系统通知收件箱';

-- 私信会话表
CREATE TABLE IF NOT EXISTS `msg_dm_conversation` (
    `id` bigint NOT NULL COMMENT '主键ID',
    `user_low_id` bigint NOT NULL COMMENT '较小用户ID',
    `user_high_id` bigint NOT NULL COMMENT '较大用户ID',
    `last_msg_id` bigint DEFAULT NULL COMMENT '最近消息ID',
    `last_msg_time` datetime DEFAULT NULL COMMENT '最近消息时间',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_pair` (`user_low_id`, `user_high_id`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='私信会话表';

-- 私信成员态表
CREATE TABLE IF NOT EXISTS `msg_dm_member` (
    `id` bigint NOT NULL COMMENT '主键ID',
    `conversation_id` bigint NOT NULL COMMENT '会话ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `peer_id` bigint NOT NULL COMMENT '对方用户ID',
    `hidden` tinyint NOT NULL DEFAULT 0 COMMENT '不显示：0否 1是',
    `pinned` tinyint NOT NULL DEFAULT 0 COMMENT '置顶：0否 1是',
    `pinned_time` datetime DEFAULT NULL COMMENT '置顶时间',
    `unread_count` int NOT NULL DEFAULT 0 COMMENT '未读数',
    `last_read_msg_id` bigint NOT NULL DEFAULT 0 COMMENT '最后已读消息ID',
    `cleared_before_id` bigint NOT NULL DEFAULT 0 COMMENT '清除游标',
    `last_msg_id` bigint DEFAULT NULL COMMENT '自己可见最新消息ID',
    `last_msg_preview` varchar(200) DEFAULT NULL COMMENT '最近消息预览',
    `last_msg_time` datetime DEFAULT NULL COMMENT '最近消息时间',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_conv_user` (`conversation_id`, `user_id`),
    KEY `idx_user_inbox` (`user_id`, `deleted`, `hidden`, `pinned`, `last_msg_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='私信成员态表';

-- 私信消息表
CREATE TABLE IF NOT EXISTS `msg_dm_message` (
    `id` bigint NOT NULL COMMENT '主键ID',
    `conversation_id` bigint NOT NULL COMMENT '会话ID',
    `sender_id` bigint NOT NULL COMMENT '发送人ID',
    `content` varchar(2000) NOT NULL COMMENT '纯文本内容',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_conv_id` (`conversation_id`, `id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='私信消息表';

-- 幂等插入消息相关权限
INSERT INTO `system_permission` (
    `id`, `parent_id`, `permission_type`, `permission_name`, `permission_code`,
    `path`, `component`, `icon`, `sort_order`, `visible`, `status`, `deleted`, `create_time`, `update_time`
)
SELECT t.id, t.parent_id, t.permission_type, t.permission_name, t.permission_code,
       t.path, t.component, t.icon, t.sort_order, 1, 1, 0, NOW(), NOW()
FROM (
    SELECT
        (SELECT COALESCE(MAX(`id`), 0) FROM `system_permission`) + 1 AS id,
        0 AS parent_id, 'catalog' AS permission_type, '消息中心' AS permission_name, 'MESSAGE' AS permission_code,
        NULL AS path, NULL AS component, 'Bell' AS icon, 150 AS sort_order
) t
WHERE NOT EXISTS (SELECT 1 FROM `system_permission` WHERE `permission_code` = 'MESSAGE');

SET @msg_catalog_id = (SELECT `id` FROM `system_permission` WHERE `permission_code` = 'MESSAGE' LIMIT 1);

INSERT INTO `system_permission` (
    `id`, `parent_id`, `permission_type`, `permission_name`, `permission_code`,
    `path`, `component`, `icon`, `sort_order`, `visible`, `status`, `deleted`, `create_time`, `update_time`
)
SELECT t.id, t.parent_id, t.permission_type, t.permission_name, t.permission_code,
       t.path, t.component, t.icon, t.sort_order, 1, 1, 0, NOW(), NOW()
FROM (
    SELECT
        (SELECT COALESCE(MAX(`id`), 0) FROM `system_permission`) + 1 AS id,
        @msg_catalog_id AS parent_id, 'menu' AS permission_type, '消息中心' AS permission_name, 'MESSAGE_CENTER' AS permission_code,
        '/message/centerPage' AS path, '/message/MessageCenterPage.vue' AS component, 'ChatDotRound' AS icon, 151 AS sort_order
) t
WHERE NOT EXISTS (SELECT 1 FROM `system_permission` WHERE `permission_code` = 'MESSAGE_CENTER');

SET @msg_menu_id = (SELECT `id` FROM `system_permission` WHERE `permission_code` = 'MESSAGE_CENTER' LIMIT 1);

INSERT INTO `system_permission` (
    `id`, `parent_id`, `permission_type`, `permission_name`, `permission_code`,
    `path`, `component`, `icon`, `sort_order`, `visible`, `status`, `deleted`, `create_time`, `update_time`
)
SELECT d.id, d.parent_id, d.permission_type, d.permission_name, d.permission_code,
       NULL, NULL, NULL, d.sort_order, 1, 1, 0, NOW(), NOW()
FROM (
    SELECT @msg_menu_id AS parent_id, 'button' AS permission_type, '消息-查看' AS permission_name, 'MESSAGE_VIEW' AS permission_code, 152 AS sort_order,
           (SELECT COALESCE(MAX(`id`), 0) FROM `system_permission`) + 1 AS id
    UNION ALL
    SELECT @msg_menu_id, 'button', '消息-发私信', 'MESSAGE_SEND_PRIVATE', 153,
           (SELECT COALESCE(MAX(`id`), 0) FROM `system_permission`) + 2
    UNION ALL
    SELECT @msg_menu_id, 'button', '消息-已读', 'MESSAGE_READ', 154,
           (SELECT COALESCE(MAX(`id`), 0) FROM `system_permission`) + 3
    UNION ALL
    SELECT @msg_menu_id, 'button', '消息-删除', 'MESSAGE_DELETE', 155,
           (SELECT COALESCE(MAX(`id`), 0) FROM `system_permission`) + 4
) d
WHERE NOT EXISTS (SELECT 1 FROM `system_permission` e WHERE e.`permission_code` = d.permission_code);

SET @system_catalog_id = (SELECT `id` FROM `system_permission` WHERE `permission_code` = 'SYSTEM' LIMIT 1);

INSERT INTO `system_permission` (
    `id`, `parent_id`, `permission_type`, `permission_name`, `permission_code`,
    `path`, `component`, `icon`, `sort_order`, `visible`, `status`, `deleted`, `create_time`, `update_time`
)
SELECT t.id, t.parent_id, t.permission_type, t.permission_name, t.permission_code,
       t.path, t.component, t.icon, t.sort_order, 1, 1, 0, NOW(), NOW()
FROM (
    SELECT
        (SELECT COALESCE(MAX(`id`), 0) FROM `system_permission`) + 1 AS id,
        @system_catalog_id AS parent_id, 'menu' AS permission_type, '公告管理' AS permission_name, 'MESSAGE_ANNOUNCE' AS permission_code,
        '/system/announcePage' AS path, '/system/announce/AnnouncePage.vue' AS component, 'Notification' AS icon, 86 AS sort_order
) t
WHERE NOT EXISTS (SELECT 1 FROM `system_permission` WHERE `permission_code` = 'MESSAGE_ANNOUNCE');

SET @announce_menu_id = (SELECT `id` FROM `system_permission` WHERE `permission_code` = 'MESSAGE_ANNOUNCE' LIMIT 1);

INSERT INTO `system_permission` (
    `id`, `parent_id`, `permission_type`, `permission_name`, `permission_code`,
    `path`, `component`, `icon`, `sort_order`, `visible`, `status`, `deleted`, `create_time`, `update_time`
)
SELECT d.id, d.parent_id, d.permission_type, d.permission_name, d.permission_code,
       NULL, NULL, NULL, d.sort_order, 1, 1, 0, NOW(), NOW()
FROM (
    SELECT @announce_menu_id AS parent_id, 'button' AS permission_type, '公告-查看' AS permission_name, 'MESSAGE_ANNOUNCE_VIEW' AS permission_code, 87 AS sort_order,
           (SELECT COALESCE(MAX(`id`), 0) FROM `system_permission`) + 1 AS id
    UNION ALL
    SELECT @announce_menu_id, 'button', '公告-发布', 'MESSAGE_ANNOUNCE_CREATE', 88,
           (SELECT COALESCE(MAX(`id`), 0) FROM `system_permission`) + 2
    UNION ALL
    SELECT @announce_menu_id, 'button', '公告-重试发送', 'MESSAGE_ANNOUNCE_RETRY', 89,
           (SELECT COALESCE(MAX(`id`), 0) FROM `system_permission`) + 3
) d
WHERE NOT EXISTS (SELECT 1 FROM `system_permission` e WHERE e.`permission_code` = d.permission_code);

INSERT IGNORE INTO `system_role_permission` (`id`, `role_id`, `permission_id`, `create_time`)
SELECT
    (SELECT COALESCE(MAX(`id`), 0) FROM `system_role_permission` r2) + ROW_NUMBER() OVER (ORDER BY rp.role_id, p.id),
    rp.role_id,
    p.id,
    NOW()
FROM `system_permission` p
CROSS JOIN (SELECT 1 AS role_id UNION ALL SELECT 2) rp
WHERE p.`permission_code` IN (
    'MESSAGE', 'MESSAGE_CENTER', 'MESSAGE_VIEW', 'MESSAGE_SEND_PRIVATE', 'MESSAGE_READ', 'MESSAGE_DELETE',
    'MESSAGE_ANNOUNCE', 'MESSAGE_ANNOUNCE_VIEW', 'MESSAGE_ANNOUNCE_CREATE', 'MESSAGE_ANNOUNCE_RETRY'
)
AND NOT EXISTS (
    SELECT 1 FROM `system_role_permission` rp0
    WHERE rp0.`role_id` = rp.role_id AND rp0.`permission_id` = p.id
);
