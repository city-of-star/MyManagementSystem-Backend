-- 实习菜单调整：下线「实习材料」；新增「我的实习」「导师待批周志」
-- 在已执行 internship_menu_permissions.sql 的基础上执行；请按环境调整 id 避免冲突。

USE `internship_management`;

-- 隐藏原实习材料菜单（若需物理删除可改为 DELETE）
UPDATE `system_permission` SET `deleted` = 1, `update_time` = NOW() WHERE `id` = 9007;

-- 我的实习（学生）：我的申请 + 周志
INSERT IGNORE INTO `system_permission` (`id`, `parent_id`, `permission_type`, `permission_name`, `permission_code`,
                                 `path`, `component`, `icon`, `sort_order`, `visible`, `status`, `deleted`, `create_time`, `update_time`)
VALUES
    (9009, 9001, 'menu', '我的实习', 'INTERNSHIP_STUDENT', '/internship/studentInternshipPage', '/internship/student/StudentInternshipPage.vue', 'User', 208, 1, 1, 0, NOW(), NOW());

-- 导师待批周志（与管理员「周志批阅」区分：本页仅 pending 接口）
INSERT IGNORE INTO `system_permission` (`id`, `parent_id`, `permission_type`, `permission_name`, `permission_code`,
                                 `path`, `component`, `icon`, `sort_order`, `visible`, `status`, `deleted`, `create_time`, `update_time`)
VALUES
    (9010, 9001, 'menu', '待批周志', 'INTERNSHIP_MENTOR_WEEKLY', '/internship/mentorWeeklyLogPage', '/internship/weekly/WeeklyLogMentorPage.vue', 'Edit', 209, 1, 1, 0, NOW(), NOW());

-- 超级管理员、系统管理员：补上新菜单
INSERT IGNORE INTO `system_role_permission` (`id`, `role_id`, `permission_id`, `create_time`)
VALUES
    (200017, 1, 9009, NOW()),
    (200018, 2, 9009, NOW()),
    (200019, 1, 9010, NOW()),
    (200020, 2, 9010, NOW());

-- 学生角色 role_id=6：我的实习；去掉旧菜单中的实习材料（若曾授 9007）
INSERT IGNORE INTO `system_role_permission` (`id`, `role_id`, `permission_id`, `create_time`)
VALUES (200021, 6, 9009, NOW());

-- 校内导师 role_id=5：待批周志；可保留 9006 管理端全量周志或仅保留 9010（按本校需要自行删 role_permission 行）
INSERT IGNORE INTO `system_role_permission` (`id`, `role_id`, `permission_id`, `create_time`)
VALUES (200022, 5, 9010, NOW());
