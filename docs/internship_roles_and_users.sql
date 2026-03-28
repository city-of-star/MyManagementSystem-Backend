-- 实习业务演示：角色 + 账号 + 菜单权限绑定
-- 依赖：已执行 init_mms_dev_core.sql 与 internship_menu_permissions.sql（权限 id 9001–9008）
-- 数据库名与 init 一致；若你使用其他库名，请修改下一行 USE。
USE `internship_management`;

-- =============================================================================
-- 初始密码（与 init 中 superAdmin 规则一致：MMS2025_ + 语义后缀）
-- 以下密码均已做 BCrypt(12) 加密，算法与 usercenter 一致（jbcrypt 校验）。
--
-- 用户名            角色               登录密码
-- intern_admin      实习管理员         MMS2025_internAdmin
-- enterprise_user   合作企业用户      MMS2025_enterprise
-- teacher_user      校内实习导师      MMS2025_teacher
-- student_user      学生（演示）      MMS2025_student
-- =============================================================================

-- 角色（id 3–6，避免与超级管理员 1、管理员 2 冲突）
INSERT IGNORE INTO `system_role` (`id`, `role_code`, `role_name`, `role_type`, `sort_order`, `status`, `remark`, `deleted`, `create_time`, `update_time`)
VALUES
    (3, 'internAdmin', '实习管理员', 'custom', 11, 1, '实习模块全菜单（演示）', 0, NOW(), NOW()),
    (4, 'internEnterprise', '合作企业用户', 'custom', 12, 1, '维护企业与岗位、处理报名（演示）', 0, NOW(), NOW()),
    (5, 'internTeacher', '校内实习导师', 'custom', 13, 1, '申请/周志相关（演示）', 0, NOW(), NOW()),
    (6, 'internStudent', '实习学生', 'custom', 14, 1, '申请/周志/材料（演示）', 0, NOW(), NOW());

-- 用户（id 5–8；密码为  MMS2025_用户名）
INSERT IGNORE INTO `system_user` (`id`, `username`, `password`, `nickname`, `real_name`, `gender`, `email`, `phone`, `status`, `locked`, `remark`, `deleted`, `create_time`, `update_time`)
VALUES
    (5, 'intern_admin', '$2a$12$YvuONFwJZshkucrCH.oMoOij5yAr4OrGs7RiveCtF8OUhilDAedPe', '实习管理员', '实习管理员', 1, 'intern_admin@demo.local', '19900001001', 1, 0, '实习模块管理员演示账号', 0, NOW(), NOW()),
    (6, 'enterprise_user', '$2a$12$Pa3nXwBqahFrxHUaGKbseuhNdAHxT1/3fWDT7i5pebKHo6O9ckZti', '企业用户', '某科技公司对接人', 1, 'enterprise@demo.local', '19900001002', 1, 0, '合作企业演示账号', 0, NOW(), NOW()),
    (7, 'teacher_user', '$2a$12$gpx1xBAF3enNQcE1SJT1MeGkYJLP5MuE2.Pp/p.jGP8Mn48wTs2su', '校内导师', '张老师', 1, 'teacher@demo.local', '19900001003', 1, 0, '校内导师演示账号', 0, NOW(), NOW()),
    (8, 'student_user', '$2a$12$/EQVqG8ih4NrSDlg.fKb4uii2VkiO6u6YJzviNOUcJlF6kMy14rpa', '学生', '李同学', 1, 'student@demo.local', '19900001004', 1, 0, '学生演示账号', 0, NOW(), NOW());

-- 用户–角色（一人一角色）
INSERT IGNORE INTO `system_user_role` (`id`, `user_id`, `role_id`, `create_time`)
VALUES
    (5, 5, 3, NOW()),
    (6, 6, 4, NOW()),
    (7, 7, 5, NOW()),
    (8, 8, 6, NOW());

-- 部门 / 岗位（与 init 中已有 dept、post 对齐，可按需改 id）
INSERT IGNORE INTO `system_user_dept` (`id`, `user_id`, `dept_id`, `is_primary`, `create_time`)
VALUES
    (5, 5, 2, 1, NOW()),
    (6, 6, 4, 1, NOW()),
    (7, 7, 2, 1, NOW()),
    (8, 8, 14, 1, NOW());

INSERT IGNORE INTO `system_user_post` (`id`, `user_id`, `post_id`, `is_primary`, `create_time`)
VALUES
    (5, 5, 6, 1, NOW()),
    (6, 6, 20, 1, NOW()),
    (7, 7, 5, 1, NOW()),
    (8, 8, 32, 1, NOW());

-- 各角色可访问的实习菜单权限（permission_id 见 internship_menu_permissions.sql）
-- 实习管理员：目录 + 全部 7 个子菜单
-- 企业用户：目录 + 合作企业、岗位、申请与流程
-- 校内导师：目录 + 申请与流程、周志批阅
-- 学生：目录 + 申请与流程、周志批阅、实习材料（无批次/企业维护、无统计）

INSERT IGNORE INTO `system_role_permission` (`id`, `role_id`, `permission_id`, `create_time`)
VALUES
    -- role 3 实习管理员 → 9001–9008
    (210001, 3, 9001, NOW()),
    (210002, 3, 9002, NOW()),
    (210003, 3, 9003, NOW()),
    (210004, 3, 9004, NOW()),
    (210005, 3, 9005, NOW()),
    (210006, 3, 9006, NOW()),
    (210007, 3, 9007, NOW()),
    (210008, 3, 9008, NOW()),
    -- role 4 企业用户 → 9001,9002,9004,9005
    (210009, 4, 9001, NOW()),
    (210010, 4, 9002, NOW()),
    (210011, 4, 9004, NOW()),
    (210012, 4, 9005, NOW()),
    -- role 5 导师 → 9001,9005,9006
    (210013, 5, 9001, NOW()),
    (210014, 5, 9005, NOW()),
    (210015, 5, 9006, NOW()),
    -- role 6 学生 → 9001,9005,9006,9007
    (210016, 6, 9001, NOW()),
    (210017, 6, 9005, NOW()),
    (210018, 6, 9006, NOW()),
    (210019, 6, 9007, NOW());
