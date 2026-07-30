-- 个人账本 v1：账户/分类/流水/固定账单 + 菜单权限（生产库）
-- 替换旧副业收入 side_income_record

USE `mms_side_income_prod_core`;

-- ========== 清理旧副业 ==========
DELETE FROM `system_role_permission`
WHERE `permission_id` IN (
    SELECT `id` FROM (
        SELECT `id` FROM `system_permission`
        WHERE `permission_code` LIKE 'SIDE_INCOME%'
    ) t
);

DELETE FROM `system_permission` WHERE `permission_code` LIKE 'SIDE_INCOME%';

DROP TABLE IF EXISTS `side_income_record`;

-- ========== 建表 ==========
CREATE TABLE IF NOT EXISTS `finance_account` (
    `id` bigint NOT NULL COMMENT '主键ID',
    `name` varchar(64) NOT NULL COMMENT '账户名称',
    `account_type` varchar(32) NOT NULL COMMENT '账户类型：cash/wechat/qq/bank/housing_fund/social_security/other',
    `opening_balance` decimal(12, 2) NOT NULL DEFAULT 0.00 COMMENT '期初余额',
    `account_no` varchar(128) DEFAULT NULL COMMENT '账号/卡号',
    `note` varchar(512) DEFAULT NULL COMMENT '备注',
    `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序号',
    `enabled` tinyint NOT NULL DEFAULT 1 COMMENT '是否启用：0-禁用，1-启用',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_account_type` (`account_type`),
    KEY `idx_enabled` (`enabled`),
    KEY `idx_deleted` (`deleted`),
    KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='记账账户表';

CREATE TABLE IF NOT EXISTS `finance_category` (
    `id` bigint NOT NULL COMMENT '主键ID',
    `name` varchar(64) NOT NULL COMMENT '分类名称',
    `direction` varchar(16) NOT NULL COMMENT '方向：income/expense',
    `icon` varchar(64) DEFAULT NULL COMMENT '图标',
    `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序号',
    `enabled` tinyint NOT NULL DEFAULT 1 COMMENT '是否启用：0-禁用，1-启用',
    `is_system` tinyint NOT NULL DEFAULT 0 COMMENT '是否系统种子：0-否，1-是（不可删）',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_direction` (`direction`),
    KEY `idx_enabled` (`enabled`),
    KEY `idx_deleted` (`deleted`),
    KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='记账分类表';

CREATE TABLE IF NOT EXISTS `finance_transaction` (
    `id` bigint NOT NULL COMMENT '主键ID',
    `txn_date` date NOT NULL COMMENT '业务日期',
    `txn_type` varchar(16) NOT NULL COMMENT '类型：income/expense/transfer',
    `amount` decimal(12, 2) NOT NULL COMMENT '金额（元）',
    `category_id` bigint DEFAULT NULL COMMENT '分类ID（划转可空）',
    `account_id` bigint DEFAULT NULL COMMENT '账户ID（收入/支出）',
    `from_account_id` bigint DEFAULT NULL COMMENT '转出账户ID（划转）',
    `to_account_id` bigint DEFAULT NULL COMMENT '转入账户ID（划转）',
    `status` varchar(16) NOT NULL DEFAULT 'settled' COMMENT '状态：settled-已结算，pending-待结算',
    `note` varchar(512) DEFAULT NULL COMMENT '备注',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_txn_date` (`txn_date`),
    KEY `idx_txn_type` (`txn_type`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_account_id` (`account_id`),
    KEY `idx_from_account_id` (`from_account_id`),
    KEY `idx_to_account_id` (`to_account_id`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`),
    KEY `idx_date_type_status` (`txn_date`, `txn_type`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='记账流水表';

CREATE TABLE IF NOT EXISTS `finance_recurring` (
    `id` bigint NOT NULL COMMENT '主键ID',
    `name` varchar(64) NOT NULL COMMENT '模板名称',
    `direction` varchar(16) NOT NULL COMMENT '方向：income/expense',
    `amount` decimal(12, 2) NOT NULL DEFAULT 0.00 COMMENT '默认金额（可为0，落账时改）',
    `category_id` bigint NOT NULL COMMENT '分类ID',
    `account_id` bigint NOT NULL COMMENT '账户ID',
    `cycle` varchar(16) NOT NULL COMMENT '周期：daily/weekly/monthly',
    `day_of_month` int DEFAULT NULL COMMENT '每月第几天（monthly）',
    `weekday` int DEFAULT NULL COMMENT '星期几 1-7（weekly）',
    `enabled` tinyint NOT NULL DEFAULT 1 COMMENT '是否启用：0-禁用，1-启用',
    `note` varchar(512) DEFAULT NULL COMMENT '备注',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_direction` (`direction`),
    KEY `idx_cycle` (`cycle`),
    KEY `idx_enabled` (`enabled`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='固定账单模板表';

-- ========== 种子：账户 ==========
INSERT IGNORE INTO `finance_account`
(`id`, `name`, `account_type`, `opening_balance`, `account_no`, `note`, `sort_order`, `enabled`, `deleted`, `create_time`, `update_time`)
VALUES
    (1, '微信', 'wechat', 0.00, NULL, '微信零钱/收款', 10, 1, 0, NOW(), NOW()),
    (2, 'QQ', 'qq', 0.00, NULL, 'QQ钱包/红包', 20, 1, 0, NOW(), NOW()),
    (3, '银行卡', 'bank', 0.00, NULL, '工资卡/储蓄卡', 30, 1, 0, NOW(), NOW()),
    (4, '公积金', 'housing_fund', 0.00, NULL, '一般不提取，用于累计查看', 40, 1, 0, NOW(), NOW()),
    (5, '社保', 'social_security', 0.00, NULL, '账号可写在「账号」字段', 50, 1, 0, NOW(), NOW());

-- ========== 种子：收入分类 ==========
INSERT IGNORE INTO `finance_category`
(`id`, `name`, `direction`, `icon`, `sort_order`, `enabled`, `is_system`, `deleted`, `create_time`, `update_time`)
VALUES
    (11, '赞赏码', 'income', NULL, 10, 1, 1, 0, NOW(), NOW()),
    (12, '微信转账', 'income', NULL, 20, 1, 1, 0, NOW(), NOW()),
    (13, 'QQ红包', 'income', NULL, 30, 1, 1, 0, NOW(), NOW()),
    (14, '网盘推广', 'income', NULL, 40, 1, 1, 0, NOW(), NOW()),
    (15, '工资', 'income', NULL, 50, 1, 1, 0, NOW(), NOW()),
    (16, '租房补贴', 'income', NULL, 60, 1, 1, 0, NOW(), NOW()),
    (17, '其他收入', 'income', NULL, 70, 1, 1, 0, NOW(), NOW());

-- ========== 种子：支出分类 ==========
INSERT IGNORE INTO `finance_category`
(`id`, `name`, `direction`, `icon`, `sort_order`, `enabled`, `is_system`, `deleted`, `create_time`, `update_time`)
VALUES
    (21, '饭钱', 'expense', NULL, 10, 1, 1, 0, NOW(), NOW()),
    (22, '买药', 'expense', NULL, 20, 1, 1, 0, NOW(), NOW()),
    (23, '话费', 'expense', NULL, 30, 1, 1, 0, NOW(), NOW()),
    (24, 'Cursor登录助手', 'expense', NULL, 40, 1, 1, 0, NOW(), NOW()),
    (25, '房租', 'expense', NULL, 50, 1, 1, 0, NOW(), NOW()),
    (26, '社保扣款', 'expense', NULL, 60, 1, 1, 0, NOW(), NOW()),
    (27, '公积金扣款', 'expense', NULL, 70, 1, 1, 0, NOW(), NOW()),
    (28, '大额花费', 'expense', NULL, 80, 1, 1, 0, NOW(), NOW()),
    (29, '其他支出', 'expense', NULL, 90, 1, 1, 0, NOW(), NOW());

-- ========== 种子：固定账单 ==========
INSERT IGNORE INTO `finance_recurring`
(`id`, `name`, `direction`, `amount`, `category_id`, `account_id`, `cycle`, `day_of_month`, `weekday`, `enabled`, `note`, `deleted`, `create_time`, `update_time`)
VALUES
    (31, '每日饭钱', 'expense', 0.00, 21, 1, 'daily', NULL, NULL, 1, '每天记一笔，金额可改', 0, NOW(), NOW()),
    (32, '话费', 'expense', 0.00, 23, 1, 'monthly', 1, NULL, 1, '每月话费，请改成真实金额', 0, NOW(), NOW()),
    (33, 'Cursor登录助手', 'expense', 0.00, 24, 3, 'monthly', 1, NULL, 1, '每月助手费，请改成真实金额', 0, NOW(), NOW()),
    (34, '房租', 'expense', 0.00, 25, 3, 'monthly', 1, NULL, 1, '每月房租，请改成真实金额', 0, NOW(), NOW());

-- ========== 菜单权限（81-103） ==========
INSERT IGNORE INTO `system_permission` (`id`, `parent_id`, `permission_type`, `permission_name`, `permission_code`,
                                 `path`, `component`, `icon`, `sort_order`, `visible`, `status`, `deleted`, `create_time`, `update_time`)
VALUES
    (81, 0, 'catalog', '个人账本', 'FINANCE', NULL, NULL, 'Wallet', 5, 1, 1, 0, NOW(), NOW()),
    (82, 81, 'menu', '概览', 'FINANCE_DASHBOARD', '/finance/dashboard', '/finance/DashboardPage.vue', 'DataAnalysis', 10, 1, 1, 0, NOW(), NOW()),
    (83, 82, 'button', '概览-查看', 'FINANCE_DASHBOARD_VIEW', NULL, NULL, NULL, 11, 1, 1, 0, NOW(), NOW()),
    (84, 81, 'menu', '流水', 'FINANCE_TRANSACTION', '/finance/transactions', '/finance/TransactionPage.vue', 'List', 20, 1, 1, 0, NOW(), NOW()),
    (85, 84, 'button', '流水-查看', 'FINANCE_TRANSACTION_VIEW', NULL, NULL, NULL, 21, 1, 1, 0, NOW(), NOW()),
    (86, 84, 'button', '流水-新增', 'FINANCE_TRANSACTION_CREATE', NULL, NULL, NULL, 22, 1, 1, 0, NOW(), NOW()),
    (87, 84, 'button', '流水-编辑', 'FINANCE_TRANSACTION_UPDATE', NULL, NULL, NULL, 23, 1, 1, 0, NOW(), NOW()),
    (88, 84, 'button', '流水-删除', 'FINANCE_TRANSACTION_DELETE', NULL, NULL, NULL, 24, 1, 1, 0, NOW(), NOW()),
    (89, 81, 'menu', '账户', 'FINANCE_ACCOUNT', '/finance/accounts', '/finance/AccountPage.vue', 'CreditCard', 30, 1, 1, 0, NOW(), NOW()),
    (90, 89, 'button', '账户-查看', 'FINANCE_ACCOUNT_VIEW', NULL, NULL, NULL, 31, 1, 1, 0, NOW(), NOW()),
    (91, 89, 'button', '账户-新增', 'FINANCE_ACCOUNT_CREATE', NULL, NULL, NULL, 32, 1, 1, 0, NOW(), NOW()),
    (92, 89, 'button', '账户-编辑', 'FINANCE_ACCOUNT_UPDATE', NULL, NULL, NULL, 33, 1, 1, 0, NOW(), NOW()),
    (93, 89, 'button', '账户-删除', 'FINANCE_ACCOUNT_DELETE', NULL, NULL, NULL, 34, 1, 1, 0, NOW(), NOW()),
    (94, 81, 'menu', '分类', 'FINANCE_CATEGORY', '/finance/categories', '/finance/CategoryPage.vue', 'Menu', 40, 1, 1, 0, NOW(), NOW()),
    (95, 94, 'button', '分类-查看', 'FINANCE_CATEGORY_VIEW', NULL, NULL, NULL, 41, 1, 1, 0, NOW(), NOW()),
    (96, 94, 'button', '分类-新增', 'FINANCE_CATEGORY_CREATE', NULL, NULL, NULL, 42, 1, 1, 0, NOW(), NOW()),
    (97, 94, 'button', '分类-编辑', 'FINANCE_CATEGORY_UPDATE', NULL, NULL, NULL, 43, 1, 1, 0, NOW(), NOW()),
    (98, 94, 'button', '分类-删除', 'FINANCE_CATEGORY_DELETE', NULL, NULL, NULL, 44, 1, 1, 0, NOW(), NOW()),
    (99, 81, 'menu', '固定账单', 'FINANCE_RECURRING', '/finance/recurrings', '/finance/RecurringPage.vue', 'Calendar', 50, 1, 1, 0, NOW(), NOW()),
    (100, 99, 'button', '固定账单-查看', 'FINANCE_RECURRING_VIEW', NULL, NULL, NULL, 51, 1, 1, 0, NOW(), NOW()),
    (101, 99, 'button', '固定账单-新增', 'FINANCE_RECURRING_CREATE', NULL, NULL, NULL, 52, 1, 1, 0, NOW(), NOW()),
    (102, 99, 'button', '固定账单-编辑', 'FINANCE_RECURRING_UPDATE', NULL, NULL, NULL, 53, 1, 1, 0, NOW(), NOW()),
    (103, 99, 'button', '固定账单-删除', 'FINANCE_RECURRING_DELETE', NULL, NULL, NULL, 54, 1, 1, 0, NOW(), NOW());

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
    'FINANCE',
    'FINANCE_DASHBOARD', 'FINANCE_DASHBOARD_VIEW',
    'FINANCE_TRANSACTION', 'FINANCE_TRANSACTION_VIEW', 'FINANCE_TRANSACTION_CREATE', 'FINANCE_TRANSACTION_UPDATE', 'FINANCE_TRANSACTION_DELETE',
    'FINANCE_ACCOUNT', 'FINANCE_ACCOUNT_VIEW', 'FINANCE_ACCOUNT_CREATE', 'FINANCE_ACCOUNT_UPDATE', 'FINANCE_ACCOUNT_DELETE',
    'FINANCE_CATEGORY', 'FINANCE_CATEGORY_VIEW', 'FINANCE_CATEGORY_CREATE', 'FINANCE_CATEGORY_UPDATE', 'FINANCE_CATEGORY_DELETE',
    'FINANCE_RECURRING', 'FINANCE_RECURRING_VIEW', 'FINANCE_RECURRING_CREATE', 'FINANCE_RECURRING_UPDATE', 'FINANCE_RECURRING_DELETE'
)
AND NOT EXISTS (
    SELECT 1
    FROM `system_role_permission` e
    WHERE e.`role_id` = rp.role_id
      AND e.`permission_id` = p.id
);
