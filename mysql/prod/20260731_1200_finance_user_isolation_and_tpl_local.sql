-- 个人记账：用户隔�?+ 全局初始化模板（生产�?
-- 库：mms_side_income_dev_core
-- 可重复执行：列存在则跳过；存量归 lhy；模板按固定 id 判重

USE `mms_side_income_dev_core`;

-- ========== 1. 业务表加 user_id ==========
SET @col_exists := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'finance_account' AND COLUMN_NAME = 'user_id'
);
SET @sql := IF(@col_exists = 0,
    'ALTER TABLE `finance_account` ADD COLUMN `user_id` bigint DEFAULT NULL COMMENT ''归属用户ID'' AFTER `id`, ADD KEY `idx_user_id` (`user_id`)',
    'SELECT ''skip finance_account.user_id'' AS info');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @col_exists := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'finance_category' AND COLUMN_NAME = 'user_id'
);
SET @sql := IF(@col_exists = 0,
    'ALTER TABLE `finance_category` ADD COLUMN `user_id` bigint DEFAULT NULL COMMENT ''归属用户ID'' AFTER `id`, ADD KEY `idx_user_id` (`user_id`)',
    'SELECT ''skip finance_category.user_id'' AS info');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @col_exists := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'finance_transaction' AND COLUMN_NAME = 'user_id'
);
SET @sql := IF(@col_exists = 0,
    'ALTER TABLE `finance_transaction` ADD COLUMN `user_id` bigint DEFAULT NULL COMMENT ''归属用户ID'' AFTER `id`, ADD KEY `idx_user_id` (`user_id`)',
    'SELECT ''skip finance_transaction.user_id'' AS info');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @col_exists := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'finance_recurring' AND COLUMN_NAME = 'user_id'
);
SET @sql := IF(@col_exists = 0,
    'ALTER TABLE `finance_recurring` ADD COLUMN `user_id` bigint DEFAULT NULL COMMENT ''归属用户ID'' AFTER `id`, ADD KEY `idx_user_id` (`user_id`)',
    'SELECT ''skip finance_recurring.user_id'' AS info');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ========== 2. 存量数据�?lhy ==========
SET @lhy_user_id := (SELECT `id` FROM `system_user` WHERE `username` = 'lhy' AND `deleted` = 0 LIMIT 1);

UPDATE `finance_account` SET `user_id` = @lhy_user_id WHERE `user_id` IS NULL AND @lhy_user_id IS NOT NULL;
UPDATE `finance_category` SET `user_id` = @lhy_user_id WHERE `user_id` IS NULL AND @lhy_user_id IS NOT NULL;
UPDATE `finance_transaction` SET `user_id` = @lhy_user_id WHERE `user_id` IS NULL AND @lhy_user_id IS NOT NULL;
UPDATE `finance_recurring` SET `user_id` = @lhy_user_id WHERE `user_id` IS NULL AND @lhy_user_id IS NOT NULL;

-- ========== 3. 用户初始化标记表 ==========
CREATE TABLE IF NOT EXISTS `finance_user_init` (
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `init_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '初始化时�?,
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='记账用户初始化标记（已从全局模板拷贝�?;

INSERT IGNORE INTO `finance_user_init` (`user_id`, `init_time`, `create_time`, `update_time`)
SELECT @lhy_user_id, NOW(), NOW(), NOW()
WHERE @lhy_user_id IS NOT NULL;

-- ========== 4. 全局模板�?==========
CREATE TABLE IF NOT EXISTS `finance_tpl_account` (
    `id` bigint NOT NULL COMMENT '主键ID',
    `name` varchar(64) NOT NULL COMMENT '账户名称',
    `account_type` varchar(32) NOT NULL COMMENT '账户类型（字�?finance_account_type�?,
    `note` varchar(512) DEFAULT NULL COMMENT '备注',
    `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序�?,
    `enabled` tinyint NOT NULL DEFAULT 1 COMMENT '是否启用�?-禁用�?-启用',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除�?-未删除，1-已删�?,
    `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_account_type` (`account_type`),
    KEY `idx_enabled` (`enabled`),
    KEY `idx_deleted` (`deleted`),
    KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='记账初始化模�?账户';

CREATE TABLE IF NOT EXISTS `finance_tpl_category` (
    `id` bigint NOT NULL COMMENT '主键ID',
    `name` varchar(64) NOT NULL COMMENT '分类名称',
    `direction` varchar(16) NOT NULL COMMENT '方向：income/expense',
    `icon` varchar(64) DEFAULT NULL COMMENT '图标',
    `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序�?,
    `enabled` tinyint NOT NULL DEFAULT 1 COMMENT '是否启用�?-禁用�?-启用',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除�?-未删除，1-已删�?,
    `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_direction` (`direction`),
    KEY `idx_enabled` (`enabled`),
    KEY `idx_deleted` (`deleted`),
    KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='记账初始化模�?分类';

CREATE TABLE IF NOT EXISTS `finance_tpl_recurring` (
    `id` bigint NOT NULL COMMENT '主键ID',
    `name` varchar(64) NOT NULL COMMENT '模板名称',
    `direction` varchar(16) NOT NULL COMMENT '方向：income/expense/transfer',
    `category_id` bigint DEFAULT NULL COMMENT '模板分类ID',
    `account_id` bigint DEFAULT NULL COMMENT '模板账户ID',
    `from_account_id` bigint DEFAULT NULL COMMENT '模板转出账户ID',
    `to_account_id` bigint DEFAULT NULL COMMENT '模板转入账户ID',
    `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序�?,
    `enabled` tinyint NOT NULL DEFAULT 1 COMMENT '是否启用�?-禁用�?-启用',
    `note` varchar(512) DEFAULT NULL COMMENT '备注',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除�?-未删除，1-已删�?,
    `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_direction` (`direction`),
    KEY `idx_enabled` (`enabled`),
    KEY `idx_deleted` (`deleted`),
    KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='记账初始化模�?快捷项（金额恒为0�?;

-- ========== 5. 通用模板种子 ==========
INSERT IGNORE INTO `finance_tpl_account`
(`id`, `name`, `account_type`, `note`, `sort_order`, `enabled`, `deleted`, `create_time`, `update_time`)
VALUES
    (1001, '现金', 'cash', '现金账户', 10, 1, 0, NOW(), NOW()),
    (1002, '微信', 'wechat', '微信零钱', 20, 1, 0, NOW(), NOW()),
    (1003, '支付�?, 'alipay', '支付�?余额�?, 30, 1, 0, NOW(), NOW()),
    (1004, '银行�?, 'bank', '常用银行�?, 40, 1, 0, NOW(), NOW()),
    (1005, '公积�?, 'housing_fund', '住房公积�?, 50, 1, 0, NOW(), NOW()),
    (1006, '医保', 'medical', '医保个人账户', 60, 1, 0, NOW(), NOW());

INSERT IGNORE INTO `finance_tpl_category`
(`id`, `name`, `direction`, `icon`, `sort_order`, `enabled`, `deleted`, `create_time`, `update_time`)
VALUES
    (2001, '工资', 'income', NULL, 10, 1, 0, NOW(), NOW()),
    (2002, '奖金补贴', 'income', NULL, 20, 1, 0, NOW(), NOW()),
    (2003, '理财利息', 'income', NULL, 30, 1, 0, NOW(), NOW()),
    (2004, '红包转账', 'income', NULL, 40, 1, 0, NOW(), NOW()),
    (2005, '其他收入', 'income', NULL, 90, 1, 0, NOW(), NOW()),
    (2101, '餐饮', 'expense', NULL, 10, 1, 0, NOW(), NOW()),
    (2102, '交�?, 'expense', NULL, 20, 1, 0, NOW(), NOW()),
    (2103, '住房房租', 'expense', NULL, 30, 1, 0, NOW(), NOW()),
    (2104, '话费网费', 'expense', NULL, 40, 1, 0, NOW(), NOW()),
    (2105, '日用购物', 'expense', NULL, 50, 1, 0, NOW(), NOW()),
    (2106, '医疗健康', 'expense', NULL, 60, 1, 0, NOW(), NOW()),
    (2107, '个税社保', 'expense', NULL, 70, 1, 0, NOW(), NOW()),
    (2108, '大额支出', 'expense', NULL, 80, 1, 0, NOW(), NOW()),
    (2109, '其他支出', 'expense', NULL, 90, 1, 0, NOW(), NOW());

INSERT IGNORE INTO `finance_tpl_recurring`
(`id`, `name`, `direction`, `category_id`, `account_id`, `from_account_id`, `to_account_id`, `sort_order`, `enabled`, `note`, `deleted`, `create_time`, `update_time`)
VALUES
    (3001, '餐饮', 'expense', 2101, 1002, NULL, NULL, 10, 1, '快捷记一笔；金额拷贝后为0，落账时填写', 0, NOW(), NOW()),
    (3002, '话费网费', 'expense', 2104, 1004, NULL, NULL, 20, 1, '快捷记一笔；金额拷贝后为0，落账时填写', 0, NOW(), NOW()),
    (3003, '房租', 'expense', 2103, 1004, NULL, NULL, 30, 1, '快捷记一笔；金额拷贝后为0，落账时填写', 0, NOW(), NOW());

-- ========== 6. 菜单权限（系统管理下�?==========
INSERT IGNORE INTO `system_permission`
(`id`, `parent_id`, `permission_type`, `permission_name`, `permission_code`, `path`, `component`, `icon`, `sort_order`, `visible`, `status`, `deleted`, `create_time`, `update_time`)
VALUES
    (104, 1, 'menu', '记账初始化配�?, 'SYSTEM_FINANCE_SETUP', '/system/financeSetupPage', '/system/financeSetup/FinanceSetupPage.vue', 'Setting', 75, 1, 1, 0, NOW(), NOW()),
    (105, 104, 'button', '记账初始�?查看', 'SYSTEM_FINANCE_SETUP_VIEW', NULL, NULL, NULL, 76, 1, 1, 0, NOW(), NOW()),
    (106, 104, 'button', '记账初始�?新增', 'SYSTEM_FINANCE_SETUP_CREATE', NULL, NULL, NULL, 77, 1, 1, 0, NOW(), NOW()),
    (107, 104, 'button', '记账初始�?编辑', 'SYSTEM_FINANCE_SETUP_UPDATE', NULL, NULL, NULL, 78, 1, 1, 0, NOW(), NOW()),
    (108, 104, 'button', '记账初始�?删除', 'SYSTEM_FINANCE_SETUP_DELETE', NULL, NULL, NULL, 79, 1, 1, 0, NOW(), NOW());

-- 重新授予超管/管理员全部权限（增量权限�?
INSERT IGNORE INTO `system_role_permission` (`id`, `role_id`, `permission_id`, `create_time`)
SELECT
    (SELECT COALESCE(MAX(id), 0) FROM `system_role_permission`) + ROW_NUMBER() OVER (ORDER BY rp.role_id, p.id),
    rp.role_id,
    p.id,
    NOW()
FROM `system_permission` p
CROSS JOIN (SELECT 1 AS role_id UNION ALL SELECT 2) AS rp
WHERE p.id BETWEEN 104 AND 108
  AND NOT EXISTS (
      SELECT 1 FROM `system_role_permission` x
      WHERE x.role_id = rp.role_id AND x.permission_id = p.id
  );
