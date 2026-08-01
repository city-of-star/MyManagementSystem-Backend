-- 记账流水：业务订单字段（基金赎回等），可重复执行
-- 库：mms_side_income_prod_core

USE `mms_side_income_prod_core`;

SET @col_exists := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'finance_transaction' AND COLUMN_NAME = 'biz_type'
);
SET @sql := IF(@col_exists = 0,
    'ALTER TABLE `finance_transaction`
        ADD COLUMN `biz_type` varchar(32) DEFAULT NULL COMMENT ''业务类型：fund_redeem 等'' AFTER `status`,
        ADD COLUMN `ref_id` bigint DEFAULT NULL COMMENT ''业务关联ID（如持仓ID）'' AFTER `biz_type`,
        ADD COLUMN `biz_extra` varchar(512) DEFAULT NULL COMMENT ''业务扩展（赎回份额/扣减成本等）'' AFTER `ref_id`,
        ADD KEY `idx_biz_type` (`biz_type`),
        ADD KEY `idx_ref_id` (`ref_id`)',
    'SELECT ''skip finance_transaction.biz_type'' AS info');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 历史 pending 赎回：仅打标，无份额快照（撤单时可能无法自动回滚份额）
UPDATE `finance_transaction`
SET `biz_type` = 'fund_redeem'
WHERE `deleted` = 0
  AND `txn_type` = 'transfer'
  AND `status` = 'pending'
  AND `biz_type` IS NULL
  AND `note` LIKE '%赎回%';
