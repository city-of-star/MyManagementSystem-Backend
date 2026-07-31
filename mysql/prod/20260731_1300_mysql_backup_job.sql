-- 生产增量：MySQL 备份定时任务（字典 + job_def）
-- 库：mms_side_income_prod_core（以实际 Nacos 为准）
-- 说明：部署含 MYSQL_BACKUP Handler 的 base/job 后执行本脚本；并在 Nacos base-PROD.yaml 配置 mms.backup.*

SET NAMES utf8mb4;

-- 1) 字典：定时任务类型 MYSQL_BACKUP
SET @job_type_id = (
    SELECT `id` FROM `system_dict_type`
    WHERE `dict_type_code` = 'job_type' AND `deleted` = 0
    LIMIT 1
);

SET @dict_data_base_id = (SELECT COALESCE(MAX(`id`), 0) FROM `system_dict_data`);
INSERT INTO `system_dict_data` (
    `id`, `dict_type_id`, `dict_label`, `dict_value`, `dict_sort`, `is_default`, `status`, `remark`, `deleted`, `create_time`, `update_time`
)
SELECT * FROM (
    SELECT @dict_data_base_id + 1 AS `id`, @job_type_id AS `dict_type_id`, 'MySQL备份任务' AS `dict_label`,
           'MYSQL_BACKUP' AS `dict_value`, 2 AS `dict_sort`, 0 AS `is_default`, 1 AS `status`,
           'MySQL库备份并推送Git仓库' AS `remark`, 0 AS `deleted`, NOW() AS `create_time`, NOW() AS `update_time`
) t
WHERE @job_type_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM `system_dict_data` e
    WHERE e.`dict_type_id` = @job_type_id AND e.`dict_value` = 'MYSQL_BACKUP' AND e.`deleted` = 0
);

-- 2) 任务定义：每天 03:00
INSERT IGNORE INTO `job_def` (
    `id`, `service_name`, `job_code`, `job_name`, `job_type`, `cron_expr`, `run_mode`,
    `enabled`, `timeout_ms`, `remark`, `params_json`, `deleted`,
    `create_by`, `create_time`, `update_by`, `update_time`
) VALUES (
    2, 'base', 'MYSQL_BACKUP', 'MySQL备份任务', 'MYSQL_BACKUP', '0 0 3 * * ?', 'single',
    1, 900000, '每天凌晨3点备份核心库并推送到GitHub私有仓，保留最近30天', '{"retainDays": 30}', 0,
    1, NOW(), 1, NOW()
);
