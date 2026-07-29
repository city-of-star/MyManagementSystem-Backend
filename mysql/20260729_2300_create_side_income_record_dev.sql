-- 副业收入记录表 + 菜单权限（开发库）
-- 在已有 mms_dev_core 上执行本脚本

USE `mms_side_income_dev_core`;

CREATE TABLE IF NOT EXISTS `side_income_record` (
    `id` bigint NOT NULL COMMENT '主键ID',
    `record_date` date NOT NULL COMMENT '业务发生日期',
    `amount` decimal(12, 2) NOT NULL COMMENT '应得金额（元）',
    `gross_amount` decimal(12, 2) DEFAULT NULL COMMENT '整单流水（元），合作单可选',
    `source_type` varchar(32) NOT NULL COMMENT '来源：self-自销，partner-合作分成，other-其他',
    `status` varchar(32) NOT NULL COMMENT '状态：paid-已到账，pending-待结算',
    `note` varchar(512) DEFAULT NULL COMMENT '备注',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_record_date` (`record_date`),
    KEY `idx_source_type` (`source_type`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`),
    KEY `idx_date_status` (`record_date`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='副业收入记录表';

-- 权限：目录 / 菜单 / 按钮（固定 ID 81-86，与 init 脚本一致）
INSERT IGNORE INTO `system_permission` (`id`, `parent_id`, `permission_type`, `permission_name`, `permission_code`,
                                 `path`, `component`, `icon`, `sort_order`, `visible`, `status`, `deleted`, `create_time`, `update_time`)
VALUES
    (81, 0, 'catalog', '副业收入', 'SIDE_INCOME', NULL, NULL, 'Wallet', 5, 1, 1, 0, NOW(), NOW()),
    (82, 81, 'menu', '收入记录', 'SIDE_INCOME_RECORD', '/income/sideIncomePage', '/income/SideIncomePage.vue', 'Money', 10, 1, 1, 0, NOW(), NOW()),
    (83, 82, 'button', '收入记录-查看', 'SIDE_INCOME_RECORD_VIEW', NULL, NULL, NULL, 11, 1, 1, 0, NOW(), NOW()),
    (84, 82, 'button', '收入记录-新增', 'SIDE_INCOME_RECORD_CREATE', NULL, NULL, NULL, 12, 1, 1, 0, NOW(), NOW()),
    (85, 82, 'button', '收入记录-编辑', 'SIDE_INCOME_RECORD_UPDATE', NULL, NULL, NULL, 13, 1, 1, 0, NOW(), NOW()),
    (86, 82, 'button', '收入记录-删除', 'SIDE_INCOME_RECORD_DELETE', NULL, NULL, NULL, 14, 1, 1, 0, NOW(), NOW());

-- 授予超级管理员(1)、管理员(2)
INSERT INTO `system_role_permission` (`id`, `role_id`, `permission_id`, `create_time`)
SELECT
    (@base_id := @base_id + 1),
    rp.role_id,
    p.id,
    NOW()
FROM `system_permission` p
CROSS JOIN (
    SELECT 1 AS role_id
    UNION ALL
    SELECT 2
) rp
CROSS JOIN (SELECT @base_id := (SELECT COALESCE(MAX(`id`), 0) FROM `system_role_permission`)) init
WHERE p.`permission_code` IN (
    'SIDE_INCOME',
    'SIDE_INCOME_RECORD',
    'SIDE_INCOME_RECORD_VIEW',
    'SIDE_INCOME_RECORD_CREATE',
    'SIDE_INCOME_RECORD_UPDATE',
    'SIDE_INCOME_RECORD_DELETE'
)
AND NOT EXISTS (
    SELECT 1
    FROM `system_role_permission` e
    WHERE e.`role_id` = rp.role_id
      AND e.`permission_id` = p.id
);
