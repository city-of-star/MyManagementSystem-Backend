-- 个人账本：快捷模板排序、平账类型说明（提醒字段保留但业务层忽略）
-- 执行库：mms_dev / mms_prod（按环境）

ALTER TABLE `finance_recurring`
    ADD COLUMN `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序号' AFTER `weekday`,
    ADD KEY `idx_sort_order` (`sort_order`);

-- 种子模板补排序（若已存在）
UPDATE `finance_recurring` SET `sort_order` = 10 WHERE `id` = 31 AND `deleted` = 0;
UPDATE `finance_recurring` SET `sort_order` = 20 WHERE `id` = 32 AND `deleted` = 0;
UPDATE `finance_recurring` SET `sort_order` = 30 WHERE `id` = 33 AND `deleted` = 0;
UPDATE `finance_recurring` SET `sort_order` = 40 WHERE `id` = 34 AND `deleted` = 0;
UPDATE `finance_recurring` SET `sort_order` = 50 WHERE `id` = 35 AND `deleted` = 0;
UPDATE `finance_recurring` SET `sort_order` = 60 WHERE `id` = 36 AND `deleted` = 0;

-- 清空历史提醒标签（功能已下线，仅备忘数据清理）
UPDATE `finance_recurring`
SET `cycle` = NULL,
    `day_of_month` = NULL,
    `weekday` = NULL
WHERE `deleted` = 0
  AND (`cycle` IS NOT NULL OR `day_of_month` IS NOT NULL OR `weekday` IS NOT NULL);
