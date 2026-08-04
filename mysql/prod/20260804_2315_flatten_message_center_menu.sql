-- 消息中心：去掉空壳 catalog，提升为顶层单菜单（与首页同级）
-- 执行库：当前业务库
-- 影响：system_permission / system_role_permission；接口权限码不变（仍 MESSAGE_CENTER / MESSAGE_VIEW 等）

-- 1) 将 MESSAGE_CENTER 提升为顶层 menu
UPDATE `system_permission`
SET `parent_id` = 0,
    `permission_type` = 'menu',
    `icon` = 'Bell',
    `sort_order` = 150,
    `path` = COALESCE(NULLIF(`path`, ''), '/message/centerPage'),
    `component` = COALESCE(NULLIF(`component`, ''), '/message/MessageCenterPage.vue'),
    `update_time` = NOW()
WHERE `permission_code` = 'MESSAGE_CENTER'
  AND `deleted` = 0;

-- 2) 若角色曾只勾了旧目录 MESSAGE，补挂 MESSAGE_CENTER（幂等）
INSERT IGNORE INTO `system_role_permission` (`id`, `role_id`, `permission_id`, `create_time`)
SELECT
    (SELECT COALESCE(MAX(`id`), 0) FROM `system_role_permission` r2)
        + ROW_NUMBER() OVER (ORDER BY rp.role_id),
    rp.role_id,
    menu.id,
    NOW()
FROM `system_role_permission` rp
JOIN `system_permission` catalog_perm
  ON catalog_perm.id = rp.permission_id
 AND catalog_perm.permission_code = 'MESSAGE'
JOIN `system_permission` menu
  ON menu.permission_code = 'MESSAGE_CENTER'
 AND menu.deleted = 0
WHERE NOT EXISTS (
    SELECT 1
    FROM `system_role_permission` x
    WHERE x.role_id = rp.role_id
      AND x.permission_id = menu.id
);

-- 3) 清理角色上的旧目录授权
DELETE rp
FROM `system_role_permission` rp
JOIN `system_permission` p ON p.id = rp.permission_id
WHERE p.permission_code = 'MESSAGE'
  AND p.permission_type = 'catalog';

-- 4) 软删空壳目录 MESSAGE
UPDATE `system_permission`
SET `deleted` = 1,
    `visible` = 0,
    `update_time` = NOW()
WHERE `permission_code` = 'MESSAGE'
  AND `permission_type` = 'catalog'
  AND `deleted` = 0;
