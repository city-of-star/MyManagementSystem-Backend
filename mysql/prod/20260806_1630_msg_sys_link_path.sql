-- 系统通知 / 公告：可选站内跳转路径

SET @col_exists := (
    SELECT COUNT(1) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'msg_sys_announce'
      AND COLUMN_NAME = 'link_path'
);
SET @sql := IF(@col_exists = 0,
    'ALTER TABLE `msg_sys_announce`
        ADD COLUMN `link_path` varchar(200) DEFAULT NULL COMMENT ''可选站内跳转路径，如 /finance/recurrings'' AFTER `content_text`',
    'SELECT ''skip msg_sys_announce.link_path'' AS info');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists := (
    SELECT COUNT(1) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'msg_sys_inbox'
      AND COLUMN_NAME = 'link_path'
);
SET @sql := IF(@col_exists = 0,
    'ALTER TABLE `msg_sys_inbox`
        ADD COLUMN `link_path` varchar(200) DEFAULT NULL COMMENT ''可选站内跳转路径，如 /finance/recurrings'' AFTER `content_text`',
    'SELECT ''skip msg_sys_inbox.link_path'' AS info');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
