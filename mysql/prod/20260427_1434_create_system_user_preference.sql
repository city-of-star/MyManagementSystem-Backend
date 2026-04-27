USE `mms_prod_core`;

-- 创建用户偏好配置表
CREATE TABLE IF NOT EXISTS `system_user_preference` (
    `id` bigint NOT NULL COMMENT '主键ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `pref_key` varchar(128) NOT NULL COMMENT '偏好键',
    `pref_value` text COMMENT '偏好值',
    `value_type` varchar(16) NOT NULL DEFAULT 'string' COMMENT '值类型：string/number/boolean/json',
    `remark` varchar(512) DEFAULT NULL COMMENT '备注',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_pref_key` (`user_id`, `pref_key`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_pref_key` (`pref_key`),
    KEY `idx_deleted` (`deleted`),
    KEY `idx_user_deleted` (`user_id`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='用户偏好配置表';

-- 插入偏好值类型字典数据
INSERT INTO `system_dict_type` (
    `id`, `dict_type_code`, `dict_type_name`, `status`, `sort_order`, `remark`, `deleted`, `create_time`, `update_time`
)
SELECT
    t.next_id, 'preference_value_type', '偏好值类型', 1, 21, '用户偏好配置值类型', 0, NOW(), NOW()
FROM (SELECT COALESCE(MAX(`id`), 0) + 1 AS next_id FROM `system_dict_type`) t
WHERE NOT EXISTS (
    SELECT 1 FROM `system_dict_type` WHERE `dict_type_code` = 'preference_value_type'
);

SET @preference_value_type_id = (
    SELECT `id` FROM `system_dict_type` WHERE `dict_type_code` = 'preference_value_type' LIMIT 1
);
SET @dict_data_base_id = (SELECT COALESCE(MAX(`id`), 0) FROM `system_dict_data`);

INSERT INTO `system_dict_data` (
    `id`, `dict_type_id`, `dict_label`, `dict_value`, `dict_sort`, `is_default`, `status`, `remark`, `deleted`, `create_time`, `update_time`
)
SELECT `id`, `dict_type_id`, `dict_label`, `dict_value`, `dict_sort`, `is_default`, `status`, `remark`, `deleted`, `create_time`, `update_time`
FROM (
    SELECT
        @dict_data_base_id + 1 AS `id`,
        @preference_value_type_id AS `dict_type_id`,
        '字符串' AS `dict_label`,
        'string' AS `dict_value`,
        1 AS `dict_sort`,
        1 AS `is_default`,
        1 AS `status`,
        '偏好值为字符串' AS `remark`,
        0 AS `deleted`,
        NOW() AS `create_time`,
        NOW() AS `update_time`
    UNION ALL
    SELECT
        @dict_data_base_id + 2, @preference_value_type_id, '数字', 'number', 2, 0, 1, '偏好值为数字', 0, NOW(), NOW()
    UNION ALL
    SELECT
        @dict_data_base_id + 3, @preference_value_type_id, '布尔', 'boolean', 3, 0, 1, '偏好值为布尔值', 0, NOW(), NOW()
    UNION ALL
    SELECT
        @dict_data_base_id + 4, @preference_value_type_id, 'JSON', 'json', 4, 0, 1, '偏好值为JSON字符串', 0, NOW(), NOW()
) d
WHERE NOT EXISTS (
    SELECT 1
    FROM `system_dict_data` e
    WHERE e.`dict_type_id` = d.`dict_type_id`
      AND e.`dict_value` = d.`dict_value`
);
