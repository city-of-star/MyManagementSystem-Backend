-- 个人账本升级：账户拆分、医保/公司卡、快捷模板转账字段、工资分类、菜单文案
-- 适用于已执行 20260730_1410_create_finance_ledger.sql 的库

USE `mms_side_income_prod_core`;

-- ========== 快捷模板：支持转账 + 周期可空（提醒标签，不自动扣款） ==========
ALTER TABLE `finance_recurring`
    MODIFY COLUMN `direction` varchar(16) NOT NULL COMMENT '方向：income/expense/transfer',
    MODIFY COLUMN `category_id` bigint DEFAULT NULL COMMENT '分类ID（收入/支出必填，转账可空）',
    MODIFY COLUMN `account_id` bigint DEFAULT NULL COMMENT '账户ID（收入/支出必填，转账可空）',
    MODIFY COLUMN `cycle` varchar(16) DEFAULT NULL COMMENT '提醒标签：daily/weekly/monthly，空=无提醒（不自动扣款）',
    ADD COLUMN `from_account_id` bigint DEFAULT NULL COMMENT '转出账户ID（转账模板）' AFTER `account_id`,
    ADD COLUMN `to_account_id` bigint DEFAULT NULL COMMENT '转入账户ID（转账模板）' AFTER `from_account_id`;

ALTER TABLE `finance_recurring`
    ADD KEY `idx_from_account_id` (`from_account_id`),
    ADD KEY `idx_to_account_id` (`to_account_id`);

ALTER TABLE `finance_recurring` COMMENT = '快捷记账模板表（手动点一次生成流水，不自动扣款）';

-- ========== 账户迁移 ==========
-- 原银行卡 → 招商卡（工资卡），历史流水仍挂 account_id=3
UPDATE `finance_account`
SET `name` = '招商卡',
    `note` = '工资卡（招商银行）',
    `account_type` = 'bank',
    `sort_order` = 30,
    `update_time` = NOW()
WHERE `id` = 3 AND `deleted` = 0;

-- 禁用旧「社保」账户（养老金等当花费，不再用独立社保账户）
UPDATE `finance_account`
SET `enabled` = 0,
    `note` = '已停用：养老金等记为支出；医保请用医保卡账户',
    `update_time` = NOW()
WHERE `id` = 5 AND `deleted` = 0;

INSERT IGNORE INTO `finance_account`
(`id`, `name`, `account_type`, `opening_balance`, `account_no`, `note`, `sort_order`, `enabled`, `deleted`, `create_time`, `update_time`)
VALUES
    (6, '建行卡', 'bank', 0.00, NULL, '租房补贴等到账（建设银行）', 35, 1, 0, NOW(), NOW()),
    (7, '公司卡', 'company_card', 0.00, NULL, '餐补等到账，可再转出到微信等', 45, 1, 0, NOW(), NOW()),
    (8, '医保卡', 'medical', 1044.22, NULL, '个人医保账户；期初已计入 opening_balance', 55, 1, 0, NOW(), NOW());

UPDATE `finance_account`
SET `note` = '一般不提取，个人+公司缴纳均计入',
    `update_time` = NOW()
WHERE `id` = 4 AND `deleted` = 0;

-- ========== 分类补齐 ==========
INSERT IGNORE INTO `finance_category`
(`id`, `name`, `direction`, `icon`, `sort_order`, `enabled`, `is_system`, `deleted`, `create_time`, `update_time`)
VALUES
    (18, '电脑补贴', 'income', NULL, 52, 1, 1, 0, NOW(), NOW()),
    (19, '加班费', 'income', NULL, 54, 1, 1, 0, NOW(), NOW()),
    (20, '餐补', 'income', NULL, 56, 1, 1, 0, NOW(), NOW()),
    (35, '公司公积金', 'income', NULL, 58, 1, 1, 0, NOW(), NOW()),
    (36, '公司医保', 'income', NULL, 59, 1, 1, 0, NOW(), NOW()),
    (37, '个税', 'expense', NULL, 62, 1, 1, 0, NOW(), NOW());

UPDATE `finance_category`
SET `name` = '社保其他',
    `update_time` = NOW()
WHERE `id` = 26 AND `deleted` = 0;
-- ========== 快捷模板种子调整 + 新增 ==========
UPDATE `finance_recurring`
SET `note` = '快捷模板：点一次记一笔，不会自动扣款；金额可改',
    `update_time` = NOW()
WHERE `id` IN (31, 32, 33, 34) AND `deleted` = 0;

INSERT IGNORE INTO `finance_recurring`
(`id`, `name`, `direction`, `amount`, `category_id`, `account_id`, `from_account_id`, `to_account_id`,
 `cycle`, `day_of_month`, `weekday`, `enabled`, `note`, `deleted`, `create_time`, `update_time`)
VALUES
    (35, '租房补贴', 'income', 0.00, 16, 6, NULL, NULL, 'monthly', NULL, NULL, 1,
     '发到建行卡；金额不固定，落账时填写', 0, NOW(), NOW()),
    (36, '公司卡转出', 'transfer', 0.00, NULL, NULL, 7, 1, NULL, NULL, NULL, 1,
     '公司卡余额转到微信；金额按需填写', 0, NOW(), NOW());

-- ========== 菜单文案：固定账单 → 快捷模板 ==========
UPDATE `system_permission`
SET `permission_name` = '快捷模板',
    `update_time` = NOW()
WHERE `id` = 99 AND `permission_code` = 'FINANCE_RECURRING';

UPDATE `system_permission`
SET `permission_name` = REPLACE(`permission_name`, '固定账单', '快捷模板'),
    `update_time` = NOW()
WHERE `permission_code` LIKE 'FINANCE_RECURRING%'
  AND `permission_name` LIKE '%固定账单%';
