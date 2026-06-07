USE `mms_prod_core`;

-- 插入操作类型字典
INSERT INTO `system_dict_type` (
    `id`, `dict_type_code`, `dict_type_name`, `status`, `sort_order`, `remark`, `deleted`, `create_time`, `update_time`
)
SELECT
    t.next_id, 'operation_type', '操作类型', 1, 22, '用户操作日志的操作类型', 0, NOW(), NOW()
FROM (SELECT COALESCE(MAX(`id`), 0) + 1 AS next_id FROM `system_dict_type`) t
WHERE NOT EXISTS (
    SELECT 1 FROM `system_dict_type` WHERE `dict_type_code` = 'operation_type'
);

-- 插入操作状态字典
INSERT INTO `system_dict_type` (
    `id`, `dict_type_code`, `dict_type_name`, `status`, `sort_order`, `remark`, `deleted`, `create_time`, `update_time`
)
SELECT
    t.next_id, 'operation_status', '操作状态', 1, 23, '用户操作日志的操作结果状态', 0, NOW(), NOW()
FROM (SELECT COALESCE(MAX(`id`), 0) + 1 AS next_id FROM `system_dict_type`) t
WHERE NOT EXISTS (
    SELECT 1 FROM `system_dict_type` WHERE `dict_type_code` = 'operation_status'
);

SET @operation_type_id = (
    SELECT `id` FROM `system_dict_type` WHERE `dict_type_code` = 'operation_type' LIMIT 1
);
SET @operation_status_id = (
    SELECT `id` FROM `system_dict_type` WHERE `dict_type_code` = 'operation_status' LIMIT 1
);
SET @dict_data_base_id = (SELECT COALESCE(MAX(`id`), 0) FROM `system_dict_data`);

-- 插入操作类型字典数据
INSERT INTO `system_dict_data` (
    `id`, `dict_type_id`, `dict_label`, `dict_value`, `dict_sort`, `is_default`, `status`, `remark`, `deleted`, `create_time`, `update_time`
)
SELECT `id`, `dict_type_id`, `dict_label`, `dict_value`, `dict_sort`, `is_default`, `status`, `remark`, `deleted`, `create_time`, `update_time`
FROM (
    SELECT
        @dict_data_base_id + 1 AS `id`,
        @operation_type_id AS `dict_type_id`,
        '新增' AS `dict_label`,
        'create' AS `dict_value`,
        1 AS `dict_sort`,
        0 AS `is_default`,
        1 AS `status`,
        '新增操作' AS `remark`,
        0 AS `deleted`,
        NOW() AS `create_time`,
        NOW() AS `update_time`
    UNION ALL
    SELECT @dict_data_base_id + 2, @operation_type_id, '修改', 'update', 2, 0, 1, '修改操作', 0, NOW(), NOW()
    UNION ALL
    SELECT @dict_data_base_id + 3, @operation_type_id, '删除', 'delete', 3, 0, 1, '删除操作', 0, NOW(), NOW()
    UNION ALL
    SELECT @dict_data_base_id + 4, @operation_type_id, '导出', 'export', 4, 0, 1, '导出操作', 0, NOW(), NOW()
    UNION ALL
    SELECT @dict_data_base_id + 5, @operation_type_id, '分配', 'assign', 5, 0, 1, '分配操作', 0, NOW(), NOW()
    UNION ALL
    SELECT @dict_data_base_id + 6, @operation_type_id, '登录', 'login', 6, 0, 1, '登录操作', 0, NOW(), NOW()
    UNION ALL
    SELECT @dict_data_base_id + 7, @operation_type_id, '登出', 'logout', 7, 0, 1, '登出操作', 0, NOW(), NOW()
) d
WHERE NOT EXISTS (
    SELECT 1
    FROM `system_dict_data` e
    WHERE e.`dict_type_id` = d.`dict_type_id`
      AND e.`dict_value` = d.`dict_value`
);

SET @dict_data_base_id = (SELECT COALESCE(MAX(`id`), 0) FROM `system_dict_data`);

-- 插入操作状态字典数据
INSERT INTO `system_dict_data` (
    `id`, `dict_type_id`, `dict_label`, `dict_value`, `dict_sort`, `is_default`, `status`, `remark`, `deleted`, `create_time`, `update_time`
)
SELECT `id`, `dict_type_id`, `dict_label`, `dict_value`, `dict_sort`, `is_default`, `status`, `remark`, `deleted`, `create_time`, `update_time`
FROM (
    SELECT
        @dict_data_base_id + 1 AS `id`,
        @operation_status_id AS `dict_type_id`,
        '失败' AS `dict_label`,
        '0' AS `dict_value`,
        1 AS `dict_sort`,
        0 AS `is_default`,
        1 AS `status`,
        '操作失败' AS `remark`,
        0 AS `deleted`,
        NOW() AS `create_time`,
        NOW() AS `update_time`
    UNION ALL
    SELECT @dict_data_base_id + 2, @operation_status_id, '成功', '1', 2, 1, 1, '操作成功', 0, NOW(), NOW()
) d
WHERE NOT EXISTS (
    SELECT 1
    FROM `system_dict_data` e
    WHERE e.`dict_type_id` = d.`dict_type_id`
      AND e.`dict_value` = d.`dict_value`
);
