-- 公告管理：修改 / 撤回 / 删除 按钮权限
-- 执行库：当前业务库

SET @announce_menu_id = (SELECT `id` FROM `system_permission` WHERE `permission_code` = 'MESSAGE_ANNOUNCE' AND `deleted` = 0 LIMIT 1);

INSERT INTO `system_permission` (
    `id`, `parent_id`, `permission_type`, `permission_name`, `permission_code`,
    `path`, `component`, `icon`, `sort_order`, `visible`, `status`, `deleted`, `create_time`, `update_time`
)
SELECT
    (SELECT COALESCE(MAX(`id`), 0) FROM `system_permission`) + 1,
    @announce_menu_id, 'button', '公告-修改', 'MESSAGE_ANNOUNCE_UPDATE',
    NULL, NULL, NULL, 90, 1, 1, 0, NOW(), NOW()
WHERE @announce_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM `system_permission` WHERE `permission_code` = 'MESSAGE_ANNOUNCE_UPDATE');

INSERT INTO `system_permission` (
    `id`, `parent_id`, `permission_type`, `permission_name`, `permission_code`,
    `path`, `component`, `icon`, `sort_order`, `visible`, `status`, `deleted`, `create_time`, `update_time`
)
SELECT
    (SELECT COALESCE(MAX(`id`), 0) FROM `system_permission`) + 1,
    @announce_menu_id, 'button', '公告-撤回', 'MESSAGE_ANNOUNCE_RECALL',
    NULL, NULL, NULL, 91, 1, 1, 0, NOW(), NOW()
WHERE @announce_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM `system_permission` WHERE `permission_code` = 'MESSAGE_ANNOUNCE_RECALL');

INSERT INTO `system_permission` (
    `id`, `parent_id`, `permission_type`, `permission_name`, `permission_code`,
    `path`, `component`, `icon`, `sort_order`, `visible`, `status`, `deleted`, `create_time`, `update_time`
)
SELECT
    (SELECT COALESCE(MAX(`id`), 0) FROM `system_permission`) + 1,
    @announce_menu_id, 'button', '公告-删除', 'MESSAGE_ANNOUNCE_DELETE',
    NULL, NULL, NULL, 92, 1, 1, 0, NOW(), NOW()
WHERE @announce_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM `system_permission` WHERE `permission_code` = 'MESSAGE_ANNOUNCE_DELETE');

-- 给已有「公告管理」菜单权限的角色补齐新按钮
INSERT IGNORE INTO `system_role_permission` (`id`, `role_id`, `permission_id`, `create_time`)
SELECT
    (SELECT COALESCE(MAX(`id`), 0) FROM `system_role_permission` r2) + ROW_NUMBER() OVER (ORDER BY rp.role_id, p.id),
    rp.`role_id`,
    p.`id`,
    NOW()
FROM `system_role_permission` rp
JOIN `system_permission` menu ON menu.`id` = rp.`permission_id`
    AND menu.`permission_code` = 'MESSAGE_ANNOUNCE'
    AND menu.`deleted` = 0
JOIN `system_permission` p ON p.`permission_code` IN (
    'MESSAGE_ANNOUNCE_UPDATE', 'MESSAGE_ANNOUNCE_RECALL', 'MESSAGE_ANNOUNCE_DELETE'
) AND p.`deleted` = 0
WHERE NOT EXISTS (
    SELECT 1 FROM `system_role_permission` x
    WHERE x.`role_id` = rp.`role_id` AND x.`permission_id` = p.`id`
);
