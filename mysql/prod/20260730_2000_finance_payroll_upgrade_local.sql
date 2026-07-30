-- 个人账本增量升级（本地已有数据专用）
-- 库：mms_side_income_dev_core
-- 前提：已有 finance_* 表与历史流水
-- 安全：不删流水；账户改名保留原 id（历史流水仍挂原银行卡 id）
-- 可重复执行：列/索引存在则跳过；种子按名称判断后插入

USE `mms_side_income_dev_core`;

-- ========== 1. finance_recurring：转账字段 + 周期可空 ==========
SET @col_exists := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'finance_recurring'
      AND COLUMN_NAME = 'from_account_id'
);
SET @sql := IF(@col_exists = 0,
    'ALTER TABLE `finance_recurring` ADD COLUMN `from_account_id` bigint DEFAULT NULL COMMENT ''转出账户ID（转账模板）'' AFTER `account_id`',
    'SELECT ''skip from_account_id'' AS info');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @col_exists := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'finance_recurring'
      AND COLUMN_NAME = 'to_account_id'
);
SET @sql := IF(@col_exists = 0,
    'ALTER TABLE `finance_recurring` ADD COLUMN `to_account_id` bigint DEFAULT NULL COMMENT ''转入账户ID（转账模板）'' AFTER `from_account_id`',
    'SELECT ''skip to_account_id'' AS info');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `finance_recurring`
    MODIFY COLUMN `direction` varchar(16) NOT NULL COMMENT '方向：income/expense/transfer',
    MODIFY COLUMN `category_id` bigint DEFAULT NULL COMMENT '分类ID（收入/支出必填，转账可空）',
    MODIFY COLUMN `account_id` bigint DEFAULT NULL COMMENT '账户ID（收入/支出必填，转账可空）',
    MODIFY COLUMN `cycle` varchar(16) DEFAULT NULL COMMENT '提醒标签：daily/weekly/monthly，空=无提醒（不自动扣款）';

ALTER TABLE `finance_recurring` COMMENT = '快捷记账模板表（手动点一次生成流水，不自动扣款）';

SET @idx_exists := (
    SELECT COUNT(*) FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'finance_recurring'
      AND INDEX_NAME = 'idx_from_account_id'
);
SET @sql := IF(@idx_exists = 0,
    'ALTER TABLE `finance_recurring` ADD KEY `idx_from_account_id` (`from_account_id`)',
    'SELECT ''skip idx_from_account_id'' AS info');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @idx_exists := (
    SELECT COUNT(*) FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'finance_recurring'
      AND INDEX_NAME = 'idx_to_account_id'
);
SET @sql := IF(@idx_exists = 0,
    'ALTER TABLE `finance_recurring` ADD KEY `idx_to_account_id` (`to_account_id`)',
    'SELECT ''skip idx_to_account_id'' AS info');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ========== 2. 账户迁移（保留原 id，不动流水） ==========
UPDATE `finance_account`
SET `name` = '招商卡',
    `note` = '工资卡（招商银行）',
    `account_type` = 'bank',
    `update_time` = NOW()
WHERE `deleted` = 0
  AND `name` = '银行卡';

UPDATE `finance_account`
SET `note` = IF(IFNULL(`note`, '') = '', '工资卡（招商银行）', `note`),
    `update_time` = NOW()
WHERE `deleted` = 0
  AND `name` = '招商卡';

UPDATE `finance_account`
SET `enabled` = 0,
    `note` = '已停用：养老金等记为支出；医保请用医保卡账户',
    `update_time` = NOW()
WHERE `deleted` = 0
  AND `name` = '社保'
  AND `account_type` = 'social_security';

UPDATE `finance_account`
SET `note` = '一般不提取，个人+公司缴纳均计入',
    `update_time` = NOW()
WHERE `deleted` = 0
  AND `name` = '公积金'
  AND `account_type` = 'housing_fund';

INSERT INTO `finance_account`
(`id`, `name`, `account_type`, `opening_balance`, `account_no`, `note`, `sort_order`, `enabled`, `deleted`, `create_time`, `update_time`)
SELECT 6, '建行卡', 'bank', 0.00, NULL, '租房补贴等到账（建设银行）', 35, 1, 0, NOW(), NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `finance_account` WHERE `deleted` = 0 AND `name` = '建行卡')
  AND NOT EXISTS (SELECT 1 FROM `finance_account` WHERE `id` = 6);

INSERT INTO `finance_account`
(`id`, `name`, `account_type`, `opening_balance`, `account_no`, `note`, `sort_order`, `enabled`, `deleted`, `create_time`, `update_time`)
SELECT 7, '公司卡', 'company_card', 0.00, NULL, '餐补等到账，可再转出到微信等', 45, 1, 0, NOW(), NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `finance_account` WHERE `deleted` = 0 AND `name` = '公司卡')
  AND NOT EXISTS (SELECT 1 FROM `finance_account` WHERE `id` = 7);

INSERT INTO `finance_account`
(`id`, `name`, `account_type`, `opening_balance`, `account_no`, `note`, `sort_order`, `enabled`, `deleted`, `create_time`, `update_time`)
SELECT 8, '医保卡', 'medical', 1044.22, NULL, '个人医保账户；期初已计入 opening_balance', 55, 1, 0, NOW(), NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `finance_account` WHERE `deleted` = 0 AND `name` = '医保卡')
  AND NOT EXISTS (SELECT 1 FROM `finance_account` WHERE `id` = 8);

UPDATE `finance_account` a
SET a.`opening_balance` = 1044.22,
    a.`note` = '个人医保账户；期初已计入 opening_balance',
    a.`update_time` = NOW()
WHERE a.`deleted` = 0
  AND a.`name` = '医保卡'
  AND a.`opening_balance` = 0
  AND NOT EXISTS (
      SELECT 1 FROM `finance_transaction` t
      WHERE t.`deleted` = 0
        AND (t.`account_id` = a.`id` OR t.`from_account_id` = a.`id` OR t.`to_account_id` = a.`id`)
  );

-- ========== 3. 分类补齐 ==========
INSERT INTO `finance_category`
(`id`, `name`, `direction`, `icon`, `sort_order`, `enabled`, `is_system`, `deleted`, `create_time`, `update_time`)
SELECT 18, '电脑补贴', 'income', NULL, 52, 1, 1, 0, NOW(), NOW() FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `finance_category` WHERE `deleted` = 0 AND `name` = '电脑补贴' AND `direction` = 'income')
  AND NOT EXISTS (SELECT 1 FROM `finance_category` WHERE `id` = 18);

INSERT INTO `finance_category`
(`id`, `name`, `direction`, `icon`, `sort_order`, `enabled`, `is_system`, `deleted`, `create_time`, `update_time`)
SELECT 19, '加班费', 'income', NULL, 54, 1, 1, 0, NOW(), NOW() FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `finance_category` WHERE `deleted` = 0 AND `name` = '加班费' AND `direction` = 'income')
  AND NOT EXISTS (SELECT 1 FROM `finance_category` WHERE `id` = 19);

INSERT INTO `finance_category`
(`id`, `name`, `direction`, `icon`, `sort_order`, `enabled`, `is_system`, `deleted`, `create_time`, `update_time`)
SELECT 20, '餐补', 'income', NULL, 56, 1, 1, 0, NOW(), NOW() FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `finance_category` WHERE `deleted` = 0 AND `name` = '餐补' AND `direction` = 'income')
  AND NOT EXISTS (SELECT 1 FROM `finance_category` WHERE `id` = 20);

INSERT INTO `finance_category`
(`id`, `name`, `direction`, `icon`, `sort_order`, `enabled`, `is_system`, `deleted`, `create_time`, `update_time`)
SELECT 35, '公司公积金', 'income', NULL, 58, 1, 1, 0, NOW(), NOW() FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `finance_category` WHERE `deleted` = 0 AND `name` = '公司公积金' AND `direction` = 'income')
  AND NOT EXISTS (SELECT 1 FROM `finance_category` WHERE `id` = 35);

INSERT INTO `finance_category`
(`id`, `name`, `direction`, `icon`, `sort_order`, `enabled`, `is_system`, `deleted`, `create_time`, `update_time`)
SELECT 36, '公司医保', 'income', NULL, 59, 1, 1, 0, NOW(), NOW() FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `finance_category` WHERE `deleted` = 0 AND `name` = '公司医保' AND `direction` = 'income')
  AND NOT EXISTS (SELECT 1 FROM `finance_category` WHERE `id` = 36);

INSERT INTO `finance_category`
(`id`, `name`, `direction`, `icon`, `sort_order`, `enabled`, `is_system`, `deleted`, `create_time`, `update_time`)
SELECT 37, '个税', 'expense', NULL, 62, 1, 1, 0, NOW(), NOW() FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `finance_category` WHERE `deleted` = 0 AND `name` = '个税' AND `direction` = 'expense')
  AND NOT EXISTS (SELECT 1 FROM `finance_category` WHERE `id` = 37);

UPDATE `finance_category`
SET `name` = '社保其他',
    `update_time` = NOW()
WHERE `deleted` = 0
  AND `name` = '社保扣款'
  AND `direction` = 'expense';

-- ========== 4. 快捷模板 ==========
UPDATE `finance_recurring`
SET `note` = CASE
        WHEN IFNULL(`note`, '') = '' THEN '快捷模板：点一次记一笔，不会自动扣款；金额可改'
        WHEN `note` LIKE '%不会自动扣款%' THEN `note`
        ELSE CONCAT(`note`, '（不会自动扣款）')
    END,
    `update_time` = NOW()
WHERE `deleted` = 0
  AND `id` IN (31, 32, 33, 34);

INSERT INTO `finance_recurring`
(`id`, `name`, `direction`, `amount`, `category_id`, `account_id`, `from_account_id`, `to_account_id`,
 `cycle`, `day_of_month`, `weekday`, `enabled`, `note`, `deleted`, `create_time`, `update_time`)
SELECT
    35, '租房补贴', 'income', 0.00,
    (SELECT c.`id` FROM `finance_category` c WHERE c.`deleted` = 0 AND c.`name` = '租房补贴' AND c.`direction` = 'income' LIMIT 1),
    (SELECT a.`id` FROM `finance_account` a WHERE a.`deleted` = 0 AND a.`name` = '建行卡' LIMIT 1),
    NULL, NULL, 'monthly', NULL, NULL, 1,
    '发到建行卡；金额不固定，落账时填写', 0, NOW(), NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `finance_recurring` WHERE `id` = 35)
  AND NOT EXISTS (SELECT 1 FROM `finance_recurring` WHERE `deleted` = 0 AND `name` = '租房补贴')
  AND EXISTS (SELECT 1 FROM `finance_account` a WHERE a.`deleted` = 0 AND a.`name` = '建行卡')
  AND EXISTS (SELECT 1 FROM `finance_category` c WHERE c.`deleted` = 0 AND c.`name` = '租房补贴' AND c.`direction` = 'income');

INSERT INTO `finance_recurring`
(`id`, `name`, `direction`, `amount`, `category_id`, `account_id`, `from_account_id`, `to_account_id`,
 `cycle`, `day_of_month`, `weekday`, `enabled`, `note`, `deleted`, `create_time`, `update_time`)
SELECT
    36, '公司卡转出', 'transfer', 0.00, NULL, NULL,
    (SELECT a.`id` FROM `finance_account` a WHERE a.`deleted` = 0 AND a.`name` = '公司卡' LIMIT 1),
    (SELECT a.`id` FROM `finance_account` a WHERE a.`deleted` = 0 AND a.`name` = '微信' LIMIT 1),
    NULL, NULL, NULL, 1,
    '公司卡余额转到微信；金额按需填写', 0, NOW(), NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `finance_recurring` WHERE `id` = 36)
  AND NOT EXISTS (SELECT 1 FROM `finance_recurring` WHERE `deleted` = 0 AND `name` = '公司卡转出')
  AND EXISTS (SELECT 1 FROM `finance_account` a WHERE a.`deleted` = 0 AND a.`name` = '公司卡')
  AND EXISTS (SELECT 1 FROM `finance_account` a WHERE a.`deleted` = 0 AND a.`name` = '微信');

-- ========== 5. 菜单文案 ==========
UPDATE `system_permission`
SET `permission_name` = '快捷模板',
    `update_time` = NOW()
WHERE `permission_code` = 'FINANCE_RECURRING'
  AND `permission_name` = '固定账单';

UPDATE `system_permission`
SET `permission_name` = REPLACE(`permission_name`, '固定账单', '快捷模板'),
    `update_time` = NOW()
WHERE `permission_code` LIKE 'FINANCE_RECURRING%'
  AND `permission_name` LIKE '%固定账单%';

-- ========== 6. 自检 ==========
SELECT 'accounts' AS kind, id, name, account_type, opening_balance, enabled
FROM `finance_account`
WHERE `deleted` = 0
ORDER BY sort_order, id;

SELECT 'categories_new' AS kind, id, name, direction
FROM `finance_category`
WHERE `deleted` = 0
  AND `name` IN ('电脑补贴', '加班费', '餐补', '公司公积金', '公司医保', '个税', '社保其他', '社保扣款')
ORDER BY id;
