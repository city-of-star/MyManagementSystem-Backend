-- 已上线库补丁：记账初始化模板对齐工资条默认明细（按名匹配）
-- 可重复执行；新环境无需执行（已并入 init_mms_prod_core.sql / init_mms_dev_core.sql）
-- 执行前先 USE 到目标库（生产 mms_side_income_prod_core / 开发 mms_side_income_dev_core）

INSERT IGNORE INTO `finance_tpl_account`
(`id`, `name`, `account_type`, `note`, `sort_order`, `enabled`, `deleted`, `create_time`, `update_time`)
VALUES
    (1007, '公司卡', 'company_card', '餐补等到账，可再转出', 70, 1, 0, NOW(), NOW());

UPDATE `finance_tpl_account`
SET `note` = '常用银行卡（工资到手默认可匹配）'
WHERE `id` = 1004 AND (`note` IS NULL OR `note` = '常用银行卡');

INSERT IGNORE INTO `finance_tpl_category`
(`id`, `name`, `direction`, `icon`, `sort_order`, `enabled`, `deleted`, `create_time`, `update_time`)
VALUES
    (2010, '电脑补贴', 'income', NULL, 12, 1, 0, NOW(), NOW()),
    (2011, '加班费', 'income', NULL, 13, 1, 0, NOW(), NOW()),
    (2012, '餐补', 'income', NULL, 14, 1, 0, NOW(), NOW()),
    (2013, '公司公积金', 'income', NULL, 15, 1, 0, NOW(), NOW()),
    (2110, '社保其他', 'expense', NULL, 71, 1, 0, NOW(), NOW()),
    (2111, '个税', 'expense', NULL, 72, 1, 0, NOW(), NOW());

SET @acct_type_id := (
    SELECT `id` FROM `system_dict_type` WHERE `dict_type_code` = 'finance_account_type' LIMIT 1
);
SET @dict_data_next_id := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `system_dict_data`);

INSERT INTO `system_dict_data`
(`id`, `dict_type_id`, `dict_label`, `dict_value`, `dict_sort`, `is_default`, `status`, `remark`, `deleted`, `create_time`, `update_time`)
SELECT
    @dict_data_next_id,
    @acct_type_id,
    '其他',
    'other',
    99,
    0,
    1,
    '其他账户类型',
    0,
    NOW(),
    NOW()
FROM DUAL
WHERE @acct_type_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM `system_dict_data` d
      WHERE d.`dict_type_id` = @acct_type_id AND d.`dict_value` = 'other'
  );
