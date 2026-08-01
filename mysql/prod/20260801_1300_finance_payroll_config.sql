-- 个人记账：工资录入配置（生产）
-- 可重复执行

USE `mms_side_income_prod_core`;

CREATE TABLE IF NOT EXISTS `finance_payroll_profile` (
    `id` bigint NOT NULL COMMENT '主键ID',
    `user_id` bigint NOT NULL COMMENT '归属用户ID',
    `salary_account_id` bigint DEFAULT NULL COMMENT '工资到手账户ID',
    `company_card_account_id` bigint DEFAULT NULL COMMENT '公司卡账户ID',
    `medical_account_id` bigint DEFAULT NULL COMMENT '医保账户ID',
    `housing_fund_account_id` bigint DEFAULT NULL COMMENT '公积金账户ID',
    `salary_category_id` bigint DEFAULT NULL COMMENT '先记到手/基本工资分类ID',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='个人工资录入配置（账户绑定）';

CREATE TABLE IF NOT EXISTS `finance_payroll_line` (
    `id` bigint NOT NULL COMMENT '主键ID',
    `user_id` bigint NOT NULL COMMENT '归属用户ID',
    `profile_id` bigint NOT NULL COMMENT '配置头ID',
    `line_key` varchar(32) NOT NULL COMMENT '稳定键：对接 payroll-batch 字段',
    `label` varchar(64) NOT NULL COMMENT '展示名称',
    `line_type` varchar(16) NOT NULL COMMENT 'income/expense/transfer',
    `category_id` bigint DEFAULT NULL COMMENT '分类ID（收入/支出）',
    `account_id` bigint DEFAULT NULL COMMENT '账户ID（收入/支出）',
    `from_account_id` bigint DEFAULT NULL COMMENT '转出账户（转账）',
    `to_account_id` bigint DEFAULT NULL COMMENT '转入账户（转账）',
    `count_in_net` tinyint NOT NULL DEFAULT 1 COMMENT '是否计入预估到手：1/0',
    `default_amount` decimal(12, 2) NOT NULL DEFAULT 0.00 COMMENT '默认金额',
    `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序号',
    `enabled` tinyint NOT NULL DEFAULT 1 COMMENT '是否启用：1/0',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_profile_id` (`profile_id`),
    KEY `idx_line_key` (`line_key`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='个人工资录入明细行配置';

INSERT IGNORE INTO `system_permission`
(`id`, `parent_id`, `permission_type`, `permission_name`, `permission_code`, `path`, `component`, `icon`, `sort_order`, `visible`, `status`, `deleted`, `create_time`, `update_time`)
VALUES
    (114, 81, 'menu', '工资录入配置', 'FINANCE_PAYROLL_CONFIG', '/finance/payrollConfig', '/finance/PayrollConfigPage.vue', 'Wallet', 55, 1, 1, 0, NOW(), NOW()),
    (115, 114, 'button', '工资录入配置-查看', 'FINANCE_PAYROLL_CONFIG_VIEW', NULL, NULL, NULL, 56, 1, 1, 0, NOW(), NOW()),
    (116, 114, 'button', '工资录入配置-编辑', 'FINANCE_PAYROLL_CONFIG_UPDATE', NULL, NULL, NULL, 57, 1, 1, 0, NOW(), NOW());

INSERT INTO `system_role_permission` (`id`, `role_id`, `permission_id`, `create_time`)
SELECT (@rp_base := @rp_base + 1),
    rp.role_id,
    p.id,
    NOW()
FROM `system_permission` p
CROSS JOIN (
    SELECT 1 AS role_id
    UNION ALL
    SELECT 2
) rp
CROSS JOIN (SELECT @rp_base := (SELECT COALESCE(MAX(`id`), 0) FROM `system_role_permission`)) init
WHERE p.`permission_code` IN (
    'FINANCE_PAYROLL_CONFIG', 'FINANCE_PAYROLL_CONFIG_VIEW', 'FINANCE_PAYROLL_CONFIG_UPDATE'
)
AND NOT EXISTS (
    SELECT 1
    FROM `system_role_permission` e
    WHERE e.`role_id` = rp.role_id
      AND e.`permission_id` = p.id
);
