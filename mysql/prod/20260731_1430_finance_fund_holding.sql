-- 个人记账：基金持仓（生产）
-- 库：mms_side_income_prod_core
-- 可重复执行：表/字典/权限按存在性跳过

USE `mms_side_income_prod_core`;

-- ========== 1. 持仓表 ==========
CREATE TABLE IF NOT EXISTS `finance_fund_holding` (
    `id` bigint NOT NULL COMMENT '主键（雪花）',
    `user_id` bigint NOT NULL COMMENT '归属用户ID',
    `account_id` bigint NOT NULL COMMENT '基金账户壳ID（finance_account.account_type=fund）',
    `fund_code` varchar(32) DEFAULT NULL COMMENT '基金代码',
    `fund_name` varchar(128) NOT NULL COMMENT '基金名称',
    `fund_category` varchar(32) NOT NULL COMMENT '分类（字典 finance_fund_category）',
    `shares` decimal(18, 6) NOT NULL DEFAULT 0.000000 COMMENT '持有份额',
    `cost_amount` decimal(12, 2) NOT NULL DEFAULT 0.00 COMMENT '持仓成本合计',
    `nav` decimal(12, 4) DEFAULT NULL COMMENT '最近净值',
    `nav_date` date DEFAULT NULL COMMENT '净值日期',
    `market_value` decimal(12, 2) NOT NULL DEFAULT 0.00 COMMENT '最近确认/已知市值',
    `quote_status` varchar(16) NOT NULL DEFAULT 'confirmed' COMMENT '估值状态：confirmed/delayed',
    `estimated_market_value` decimal(12, 2) DEFAULT NULL COMMENT '滞后估算市值（可选）',
    `note` varchar(512) DEFAULT NULL COMMENT '备注',
    `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序号',
    `enabled` tinyint NOT NULL DEFAULT 1 COMMENT '是否启用：1/0',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_by` bigint DEFAULT NULL COMMENT '创建人',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint DEFAULT NULL COMMENT '更新人',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_account_id` (`account_id`),
    KEY `idx_fund_category` (`fund_category`),
    KEY `idx_quote_status` (`quote_status`),
    KEY `idx_enabled` (`enabled`),
    KEY `idx_deleted` (`deleted`),
    KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='基金持仓';

-- ========== 2. 估值快照（手填更新时可选落库） ==========
CREATE TABLE IF NOT EXISTS `finance_fund_nav_snapshot` (
    `id` bigint NOT NULL COMMENT '主键（雪花）',
    `user_id` bigint NOT NULL COMMENT '归属用户ID',
    `holding_id` bigint NOT NULL COMMENT '持仓ID',
    `nav_date` date NOT NULL COMMENT '净值日期',
    `nav` decimal(12, 4) NOT NULL COMMENT '净值',
    `market_value` decimal(12, 2) NOT NULL COMMENT '市值',
    `quote_status` varchar(16) NOT NULL COMMENT '估值状态',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_by` bigint DEFAULT NULL COMMENT '创建人',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint DEFAULT NULL COMMENT '更新人',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_holding_id` (`holding_id`),
    KEY `idx_nav_date` (`nav_date`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='基金净值快照';

-- ========== 3. 字典：基金分类 ==========
INSERT INTO `system_dict_type` (
    `id`, `dict_type_code`, `dict_type_name`, `status`, `sort_order`, `remark`, `deleted`, `create_time`, `update_time`
)
SELECT t.next_id, 'finance_fund_category', '基金分类', 1, 35, '个人记账基金持仓分类', 0, NOW(), NOW()
FROM (SELECT COALESCE(MAX(`id`), 0) + 1 AS next_id FROM `system_dict_type`) t
WHERE NOT EXISTS (SELECT 1 FROM `system_dict_type` WHERE `dict_type_code` = 'finance_fund_category');

INSERT INTO `system_dict_type` (
    `id`, `dict_type_code`, `dict_type_name`, `status`, `sort_order`, `remark`, `deleted`, `create_time`, `update_time`
)
SELECT t.next_id, 'finance_fund_quote_status', '基金估值状态', 1, 36, '确认/滞后', 0, NOW(), NOW()
FROM (SELECT COALESCE(MAX(`id`), 0) + 1 AS next_id FROM `system_dict_type`) t
WHERE NOT EXISTS (SELECT 1 FROM `system_dict_type` WHERE `dict_type_code` = 'finance_fund_quote_status');

SET @finance_fund_category_id = (SELECT `id` FROM `system_dict_type` WHERE `dict_type_code` = 'finance_fund_category' LIMIT 1);
SET @finance_fund_quote_status_id = (SELECT `id` FROM `system_dict_type` WHERE `dict_type_code` = 'finance_fund_quote_status' LIMIT 1);

SET @dict_data_base_id = (SELECT COALESCE(MAX(`id`), 0) FROM `system_dict_data`);
INSERT INTO `system_dict_data` (
    `id`, `dict_type_id`, `dict_label`, `dict_value`, `dict_sort`, `is_default`, `status`, `remark`, `deleted`, `create_time`, `update_time`
)
SELECT `id`, `dict_type_id`, `dict_label`, `dict_value`, `dict_sort`, `is_default`, `status`, `remark`, `deleted`, `create_time`, `update_time`
FROM (
    SELECT @dict_data_base_id + 1 AS `id`, @finance_fund_category_id AS `dict_type_id`, '债基' AS `dict_label`, 'bond' AS `dict_value`, 1 AS `dict_sort`, 0 AS `is_default`, 1 AS `status`, NULL AS `remark`, 0 AS `deleted`, NOW() AS `create_time`, NOW() AS `update_time`
    UNION ALL SELECT @dict_data_base_id + 2, @finance_fund_category_id, '主题基金', 'theme', 2, 0, 1, NULL, 0, NOW(), NOW()
    UNION ALL SELECT @dict_data_base_id + 3, @finance_fund_category_id, '指数（国内）', 'index_cn', 3, 1, 1, NULL, 0, NOW(), NOW()
    UNION ALL SELECT @dict_data_base_id + 4, @finance_fund_category_id, '指数（海外）', 'index_overseas', 4, 0, 1, NULL, 0, NOW(), NOW()
    UNION ALL SELECT @dict_data_base_id + 5, @finance_fund_category_id, '美股场外', 'us_otc', 5, 0, 1, NULL, 0, NOW(), NOW()
    UNION ALL SELECT @dict_data_base_id + 6, @finance_fund_category_id, '其他', 'other', 99, 0, 1, NULL, 0, NOW(), NOW()
) d
WHERE NOT EXISTS (
    SELECT 1 FROM `system_dict_data` e
    WHERE e.`dict_type_id` = d.`dict_type_id` AND e.`dict_value` = d.`dict_value` AND e.`deleted` = 0
);

SET @dict_data_base_id = (SELECT COALESCE(MAX(`id`), 0) FROM `system_dict_data`);
INSERT INTO `system_dict_data` (
    `id`, `dict_type_id`, `dict_label`, `dict_value`, `dict_sort`, `is_default`, `status`, `remark`, `deleted`, `create_time`, `update_time`
)
SELECT `id`, `dict_type_id`, `dict_label`, `dict_value`, `dict_sort`, `is_default`, `status`, `remark`, `deleted`, `create_time`, `update_time`
FROM (
    SELECT @dict_data_base_id + 1 AS `id`, @finance_fund_quote_status_id AS `dict_type_id`, '已确认' AS `dict_label`, 'confirmed' AS `dict_value`, 1 AS `dict_sort`, 1 AS `is_default`, 1 AS `status`, '计入总资产' AS `remark`, 0 AS `deleted`, NOW() AS `create_time`, NOW() AS `update_time`
    UNION ALL SELECT @dict_data_base_id + 2, @finance_fund_quote_status_id, '滞后待更新', 'delayed', 2, 0, 1, '不计入总资产，单独展示', 0, NOW(), NOW()
) d
WHERE NOT EXISTS (
    SELECT 1 FROM `system_dict_data` e
    WHERE e.`dict_type_id` = d.`dict_type_id` AND e.`dict_value` = d.`dict_value` AND e.`deleted` = 0
);

-- ========== 4. 权限（菜单+按钮） ==========
INSERT IGNORE INTO `system_permission`
(`id`, `parent_id`, `permission_type`, `permission_name`, `permission_code`, `path`, `component`, `icon`, `sort_order`, `visible`, `status`, `deleted`, `create_time`, `update_time`)
VALUES
    (109, 81, 'menu', '基金持仓', 'FINANCE_FUND_HOLDING', '/finance/fundHoldings', '/finance/FundHoldingPage.vue', 'TrendCharts', 35, 1, 1, 0, NOW(), NOW()),
    (110, 109, 'button', '基金持仓-查看', 'FINANCE_FUND_HOLDING_VIEW', NULL, NULL, NULL, 36, 1, 1, 0, NOW(), NOW()),
    (111, 109, 'button', '基金持仓-新增', 'FINANCE_FUND_HOLDING_CREATE', NULL, NULL, NULL, 37, 1, 1, 0, NOW(), NOW()),
    (112, 109, 'button', '基金持仓-编辑', 'FINANCE_FUND_HOLDING_UPDATE', NULL, NULL, NULL, 38, 1, 1, 0, NOW(), NOW()),
    (113, 109, 'button', '基金持仓-删除', 'FINANCE_FUND_HOLDING_DELETE', NULL, NULL, NULL, 39, 1, 1, 0, NOW(), NOW());

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
    'FINANCE_FUND_HOLDING', 'FINANCE_FUND_HOLDING_VIEW', 'FINANCE_FUND_HOLDING_CREATE',
    'FINANCE_FUND_HOLDING_UPDATE', 'FINANCE_FUND_HOLDING_DELETE'
)
AND NOT EXISTS (
    SELECT 1
    FROM `system_role_permission` e
    WHERE e.`role_id` = rp.role_id
      AND e.`permission_id` = p.id
);
