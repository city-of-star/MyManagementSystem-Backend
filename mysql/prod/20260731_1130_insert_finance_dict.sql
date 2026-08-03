-- 个人记账枚举接入系统字典（生产）
-- 库：mms_side_income_prod_core
-- 可重复执行：按 dict_type_code / dict_value 判重

USE `mms_side_income_prod_core`;

-- ========== 字典类型 ==========
INSERT INTO `system_dict_type` (
    `id`, `dict_type_code`, `dict_type_name`, `status`, `sort_order`, `remark`, `deleted`, `create_time`, `update_time`
)
SELECT t.next_id, 'finance_account_type', '记账账户类型', 1, 30, '个人记账账户类型', 0, NOW(), NOW()
FROM (SELECT COALESCE(MAX(`id`), 0) + 1 AS next_id FROM `system_dict_type`) t
WHERE NOT EXISTS (SELECT 1 FROM `system_dict_type` WHERE `dict_type_code` = 'finance_account_type');

INSERT INTO `system_dict_type` (
    `id`, `dict_type_code`, `dict_type_name`, `status`, `sort_order`, `remark`, `deleted`, `create_time`, `update_time`
)
SELECT t.next_id, 'finance_txn_type', '记账流水类型', 1, 31, '个人记账流水类型', 0, NOW(), NOW()
FROM (SELECT COALESCE(MAX(`id`), 0) + 1 AS next_id FROM `system_dict_type`) t
WHERE NOT EXISTS (SELECT 1 FROM `system_dict_type` WHERE `dict_type_code` = 'finance_txn_type');

INSERT INTO `system_dict_type` (
    `id`, `dict_type_code`, `dict_type_name`, `status`, `sort_order`, `remark`, `deleted`, `create_time`, `update_time`
)
SELECT t.next_id, 'finance_txn_status', '记账流水状态', 1, 32, '个人记账流水入账状态', 0, NOW(), NOW()
FROM (SELECT COALESCE(MAX(`id`), 0) + 1 AS next_id FROM `system_dict_type`) t
WHERE NOT EXISTS (SELECT 1 FROM `system_dict_type` WHERE `dict_type_code` = 'finance_txn_status');

INSERT INTO `system_dict_type` (
    `id`, `dict_type_code`, `dict_type_name`, `status`, `sort_order`, `remark`, `deleted`, `create_time`, `update_time`
)
SELECT t.next_id, 'finance_direction', '记账分类方向', 1, 33, '个人记账分类收入/支出方向', 0, NOW(), NOW()
FROM (SELECT COALESCE(MAX(`id`), 0) + 1 AS next_id FROM `system_dict_type`) t
WHERE NOT EXISTS (SELECT 1 FROM `system_dict_type` WHERE `dict_type_code` = 'finance_direction');

INSERT INTO `system_dict_type` (
    `id`, `dict_type_code`, `dict_type_name`, `status`, `sort_order`, `remark`, `deleted`, `create_time`, `update_time`
)
SELECT t.next_id, 'finance_recurring_direction', '记账模板方向', 1, 34, '个人记账快捷模板方向', 0, NOW(), NOW()
FROM (SELECT COALESCE(MAX(`id`), 0) + 1 AS next_id FROM `system_dict_type`) t
WHERE NOT EXISTS (SELECT 1 FROM `system_dict_type` WHERE `dict_type_code` = 'finance_recurring_direction');

SET @finance_account_type_id = (SELECT `id` FROM `system_dict_type` WHERE `dict_type_code` = 'finance_account_type' LIMIT 1);
SET @finance_txn_type_id = (SELECT `id` FROM `system_dict_type` WHERE `dict_type_code` = 'finance_txn_type' LIMIT 1);
SET @finance_txn_status_id = (SELECT `id` FROM `system_dict_type` WHERE `dict_type_code` = 'finance_txn_status' LIMIT 1);
SET @finance_direction_id = (SELECT `id` FROM `system_dict_type` WHERE `dict_type_code` = 'finance_direction' LIMIT 1);
SET @finance_recurring_direction_id = (SELECT `id` FROM `system_dict_type` WHERE `dict_type_code` = 'finance_recurring_direction' LIMIT 1);

-- ========== 账户类型 ==========
SET @dict_data_base_id = (SELECT COALESCE(MAX(`id`), 0) FROM `system_dict_data`);
INSERT INTO `system_dict_data` (
    `id`, `dict_type_id`, `dict_label`, `dict_value`, `dict_sort`, `is_default`, `status`, `remark`, `deleted`, `create_time`, `update_time`
)
SELECT `id`, `dict_type_id`, `dict_label`, `dict_value`, `dict_sort`, `is_default`, `status`, `remark`, `deleted`, `create_time`, `update_time`
FROM (
    SELECT @dict_data_base_id + 1 AS `id`, @finance_account_type_id AS `dict_type_id`, '现金' AS `dict_label`, 'cash' AS `dict_value`, 1 AS `dict_sort`, 0 AS `is_default`, 1 AS `status`, '现金账户' AS `remark`, 0 AS `deleted`, NOW() AS `create_time`, NOW() AS `update_time`
    UNION ALL SELECT @dict_data_base_id + 2, @finance_account_type_id, '微信', 'wechat', 2, 0, 1, '微信钱包', 0, NOW(), NOW()
    UNION ALL SELECT @dict_data_base_id + 3, @finance_account_type_id, 'QQ', 'qq', 3, 0, 1, 'QQ钱包', 0, NOW(), NOW()
    UNION ALL SELECT @dict_data_base_id + 4, @finance_account_type_id, '银行卡', 'bank', 4, 0, 1, '银行卡', 0, NOW(), NOW()
    UNION ALL SELECT @dict_data_base_id + 5, @finance_account_type_id, '支付宝', 'alipay', 5, 1, 1, '支付宝/余额宝等', 0, NOW(), NOW()
    UNION ALL SELECT @dict_data_base_id + 6, @finance_account_type_id, '公积金', 'housing_fund', 6, 0, 1, '住房公积金', 0, NOW(), NOW()
    UNION ALL SELECT @dict_data_base_id + 7, @finance_account_type_id, '社保', 'social_security', 7, 0, 1, '社保账户', 0, NOW(), NOW()
    UNION ALL SELECT @dict_data_base_id + 8, @finance_account_type_id, '公司卡', 'company_card', 8, 0, 1, '公司卡', 0, NOW(), NOW()
    UNION ALL SELECT @dict_data_base_id + 9, @finance_account_type_id, '医保', 'medical', 9, 0, 1, '医保账户', 0, NOW(), NOW()
    UNION ALL SELECT @dict_data_base_id + 10, @finance_account_type_id, '基金', 'fund', 10, 0, 1, '基金账户', 0, NOW(), NOW()
    UNION ALL SELECT @dict_data_base_id + 11, @finance_account_type_id, '其他', 'other', 99, 0, 1, '其他账户', 0, NOW(), NOW()
) d
WHERE NOT EXISTS (
    SELECT 1 FROM `system_dict_data` e
    WHERE e.`dict_type_id` = d.`dict_type_id` AND e.`dict_value` = d.`dict_value` AND e.`deleted` = 0
);

-- ========== 流水类型 ==========
SET @dict_data_base_id = (SELECT COALESCE(MAX(`id`), 0) FROM `system_dict_data`);
INSERT INTO `system_dict_data` (
    `id`, `dict_type_id`, `dict_label`, `dict_value`, `dict_sort`, `is_default`, `status`, `remark`, `deleted`, `create_time`, `update_time`
)
SELECT `id`, `dict_type_id`, `dict_label`, `dict_value`, `dict_sort`, `is_default`, `status`, `remark`, `deleted`, `create_time`, `update_time`
FROM (
    SELECT @dict_data_base_id + 1 AS `id`, @finance_txn_type_id AS `dict_type_id`, '收入' AS `dict_label`, 'income' AS `dict_value`, 1 AS `dict_sort`, 0 AS `is_default`, 1 AS `status`, '收入流水' AS `remark`, 0 AS `deleted`, NOW() AS `create_time`, NOW() AS `update_time`
    UNION ALL SELECT @dict_data_base_id + 2, @finance_txn_type_id, '支出', 'expense', 2, 1, 1, '支出流水', 0, NOW(), NOW()
    UNION ALL SELECT @dict_data_base_id + 3, @finance_txn_type_id, '转账', 'transfer', 3, 0, 1, '账户间转账', 0, NOW(), NOW()
    UNION ALL SELECT @dict_data_base_id + 4, @finance_txn_type_id, '平账', 'adjustment', 4, 0, 1, '余额对齐差额', 0, NOW(), NOW()
) d
WHERE NOT EXISTS (
    SELECT 1 FROM `system_dict_data` e
    WHERE e.`dict_type_id` = d.`dict_type_id` AND e.`dict_value` = d.`dict_value` AND e.`deleted` = 0
);

-- ========== 流水状态 ==========
SET @dict_data_base_id = (SELECT COALESCE(MAX(`id`), 0) FROM `system_dict_data`);
INSERT INTO `system_dict_data` (
    `id`, `dict_type_id`, `dict_label`, `dict_value`, `dict_sort`, `is_default`, `status`, `remark`, `deleted`, `create_time`, `update_time`
)
SELECT `id`, `dict_type_id`, `dict_label`, `dict_value`, `dict_sort`, `is_default`, `status`, `remark`, `deleted`, `create_time`, `update_time`
FROM (
    SELECT @dict_data_base_id + 1 AS `id`, @finance_txn_status_id AS `dict_type_id`, '已入账' AS `dict_label`, 'settled' AS `dict_value`, 1 AS `dict_sort`, 1 AS `is_default`, 1 AS `status`, '已结算入账' AS `remark`, 0 AS `deleted`, NOW() AS `create_time`, NOW() AS `update_time`
    UNION ALL SELECT @dict_data_base_id + 2, @finance_txn_status_id, '待结算', 'pending', 2, 0, 1, '待结算（仅收入）', 0, NOW(), NOW()
) d
WHERE NOT EXISTS (
    SELECT 1 FROM `system_dict_data` e
    WHERE e.`dict_type_id` = d.`dict_type_id` AND e.`dict_value` = d.`dict_value` AND e.`deleted` = 0
);

-- ========== 分类方向 ==========
SET @dict_data_base_id = (SELECT COALESCE(MAX(`id`), 0) FROM `system_dict_data`);
INSERT INTO `system_dict_data` (
    `id`, `dict_type_id`, `dict_label`, `dict_value`, `dict_sort`, `is_default`, `status`, `remark`, `deleted`, `create_time`, `update_time`
)
SELECT `id`, `dict_type_id`, `dict_label`, `dict_value`, `dict_sort`, `is_default`, `status`, `remark`, `deleted`, `create_time`, `update_time`
FROM (
    SELECT @dict_data_base_id + 1 AS `id`, @finance_direction_id AS `dict_type_id`, '收入' AS `dict_label`, 'income' AS `dict_value`, 1 AS `dict_sort`, 0 AS `is_default`, 1 AS `status`, '收入分类' AS `remark`, 0 AS `deleted`, NOW() AS `create_time`, NOW() AS `update_time`
    UNION ALL SELECT @dict_data_base_id + 2, @finance_direction_id, '支出', 'expense', 2, 1, 1, '支出分类', 0, NOW(), NOW()
) d
WHERE NOT EXISTS (
    SELECT 1 FROM `system_dict_data` e
    WHERE e.`dict_type_id` = d.`dict_type_id` AND e.`dict_value` = d.`dict_value` AND e.`deleted` = 0
);

-- ========== 模板方向 ==========
SET @dict_data_base_id = (SELECT COALESCE(MAX(`id`), 0) FROM `system_dict_data`);
INSERT INTO `system_dict_data` (
    `id`, `dict_type_id`, `dict_label`, `dict_value`, `dict_sort`, `is_default`, `status`, `remark`, `deleted`, `create_time`, `update_time`
)
SELECT `id`, `dict_type_id`, `dict_label`, `dict_value`, `dict_sort`, `is_default`, `status`, `remark`, `deleted`, `create_time`, `update_time`
FROM (
    SELECT @dict_data_base_id + 1 AS `id`, @finance_recurring_direction_id AS `dict_type_id`, '收入' AS `dict_label`, 'income' AS `dict_value`, 1 AS `dict_sort`, 0 AS `is_default`, 1 AS `status`, '收入模板' AS `remark`, 0 AS `deleted`, NOW() AS `create_time`, NOW() AS `update_time`
    UNION ALL SELECT @dict_data_base_id + 2, @finance_recurring_direction_id, '支出', 'expense', 2, 1, 1, '支出模板', 0, NOW(), NOW()
    UNION ALL SELECT @dict_data_base_id + 3, @finance_recurring_direction_id, '转账', 'transfer', 3, 0, 1, '转账模板', 0, NOW(), NOW()
) d
WHERE NOT EXISTS (
    SELECT 1 FROM `system_dict_data` e
    WHERE e.`dict_type_id` = d.`dict_type_id` AND e.`dict_value` = d.`dict_value` AND e.`deleted` = 0
);
