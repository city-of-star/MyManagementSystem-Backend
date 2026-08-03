-- 硬删除基金持仓：表 / 权限 / 字典
-- 库：mms_side_income_prod_core

USE `mms_side_income_prod_core`;

-- 1. 表
DROP TABLE IF EXISTS `finance_fund_nav_snapshot`;
DROP TABLE IF EXISTS `finance_fund_holding`;

-- 2. 角色权限关联
DELETE rp FROM `system_role_permission` rp
INNER JOIN `system_permission` p ON p.`id` = rp.`permission_id`
WHERE p.`permission_code` IN (
    'FINANCE_FUND_HOLDING',
    'FINANCE_FUND_HOLDING_VIEW',
    'FINANCE_FUND_HOLDING_CREATE',
    'FINANCE_FUND_HOLDING_UPDATE',
    'FINANCE_FUND_HOLDING_DELETE'
);

-- 3. 权限菜单/按钮
DELETE FROM `system_permission`
WHERE `permission_code` IN (
    'FINANCE_FUND_HOLDING',
    'FINANCE_FUND_HOLDING_VIEW',
    'FINANCE_FUND_HOLDING_CREATE',
    'FINANCE_FUND_HOLDING_UPDATE',
    'FINANCE_FUND_HOLDING_DELETE'
);

-- 4. 字典数据 + 类型
DELETE d FROM `system_dict_data` d
INNER JOIN `system_dict_type` t ON t.`id` = d.`dict_type_id`
WHERE t.`dict_type` IN ('finance_fund_category', 'finance_fund_quote_status');

DELETE FROM `system_dict_type`
WHERE `dict_type` IN ('finance_fund_category', 'finance_fund_quote_status');

-- 5. 清理历史赎回业务标记（可选，流水本身保留）
UPDATE `finance_transaction`
SET `biz_type` = NULL,
    `ref_id` = NULL,
    `biz_extra` = NULL
WHERE `biz_type` = 'fund_redeem';
