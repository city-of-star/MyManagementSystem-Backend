-- 本地增量：业务系统通知幂等索引 + 快捷模板提醒时刻 + 提醒 Job
USE `mms_dev_core`;

SET @idx_exists := (
    SELECT COUNT(1) FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'msg_sys_inbox'
      AND INDEX_NAME = 'uk_biz_user'
);
SET @sql := IF(@idx_exists = 0,
    'ALTER TABLE `msg_sys_inbox` ADD UNIQUE KEY `uk_biz_user` (`biz_type`, `biz_id`, `user_id`)',
    'SELECT ''skip uk_biz_user'' AS info');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists := (
    SELECT COUNT(1) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'finance_recurring'
      AND COLUMN_NAME = 'remind_minute_of_day'
);
SET @sql := IF(@col_exists = 0,
    'ALTER TABLE `finance_recurring`
        ADD COLUMN `remind_minute_of_day` int DEFAULT NULL COMMENT ''提醒时刻：距当日0点分钟数，半小时一档，如480=08:00'' AFTER `weekday`',
    'SELECT ''skip finance_recurring.remind_minute_of_day'' AS info');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

UPDATE `finance_recurring`
SET `remind_minute_of_day` = 480
WHERE `deleted` = 0
  AND `cycle` IS NOT NULL
  AND `cycle` <> ''
  AND `cycle` <> 'none'
  AND `remind_minute_of_day` IS NULL;

SET @job_type_id = (
    SELECT `id` FROM `system_dict_type`
    WHERE `dict_type_code` = 'job_type' AND `deleted` = 0
    LIMIT 1
);
SET @dict_data_base_id = (
    SELECT IFNULL(MAX(`id`), 0) FROM `system_dict_data`
);
INSERT INTO `system_dict_data` (
    `id`, `dict_type_id`, `dict_label`, `dict_value`, `dict_sort`, `is_default`, `status`,
    `remark`, `deleted`, `create_time`, `update_time`
)
SELECT @dict_data_base_id + 1, @job_type_id, '记账快捷模板提醒', 'FINANCE_RECURRING_REMIND', 3, 0, 1,
       '扫描到期快捷模板并发系统通知', 0, NOW(), NOW()
WHERE @job_type_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM `system_dict_data` e
    WHERE e.`dict_type_id` = @job_type_id AND e.`dict_value` = 'FINANCE_RECURRING_REMIND' AND e.`deleted` = 0
);

INSERT IGNORE INTO `job_def` (
    `id`, `service_name`, `job_code`, `job_name`, `job_type`, `cron_expr`, `run_mode`,
    `enabled`, `timeout_ms`, `remark`, `params_json`,
    `deleted`, `create_by`, `create_time`, `update_by`, `update_time`
) VALUES (
    3, 'base', 'FINANCE_RECURRING_REMIND', '记账快捷模板提醒', 'FINANCE_RECURRING_REMIND',
    '0 0/30 * * * ?', 'single', 1, 120000,
    '每半小时扫描到期快捷模板，按人发系统通知（不自动记账）', '{}',
    0, 1, NOW(), 1, NOW()
);
