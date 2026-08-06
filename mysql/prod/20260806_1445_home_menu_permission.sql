-- 首页：从前端硬编码改为顶层 menu 权限（与消息中心同范式）
-- 执行库：当前业务库
-- 影响：system_permission / system_role_permission

-- 1) 插入 HOME 菜单（幂等）
INSERT INTO `system_permission` (
    `id`, `parent_id`, `permission_type`, `permission_name`, `permission_code`,
    `path`, `component`, `icon`, `sort_order`, `visible`, `status`, `deleted`, `create_time`, `update_time`
)
SELECT
    (SELECT COALESCE(MAX(`id`), 0) FROM `system_permission`) + 1,
    0, 'menu', '首页', 'HOME',
    '/home', '/HomeView.vue', 'HomeFilled', 0, 1, 1, 0, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM `system_permission` WHERE `permission_code` = 'HOME' AND `deleted` = 0
);

-- 2) 给所有未删除角色挂上 HOME（保留「人人可见首页」的存量行为）
INSERT IGNORE INTO `system_role_permission` (`id`, `role_id`, `permission_id`, `create_time`)
SELECT
    (SELECT COALESCE(MAX(`id`), 0) FROM `system_role_permission` r2)
        + ROW_NUMBER() OVER (ORDER BY r.id),
    r.id,
    p.id,
    NOW()
FROM `system_role` r
CROSS JOIN `system_permission` p
WHERE r.deleted = 0
  AND p.permission_code = 'HOME'
  AND p.deleted = 0
  AND NOT EXISTS (
      SELECT 1
      FROM `system_role_permission` x
      WHERE x.role_id = r.id
        AND x.permission_id = p.id
  );
