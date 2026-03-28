-- 实习管理：目录 + 菜单（与 init_mms_dev_core 中 system_permission 结构一致）
-- 执行前请确认 id 不与现有数据冲突；若已占用，请调整本脚本中的 id。
-- 路由 path 与 component 需与前端 menuUtils.loadComponent 规则一致（@/views 下相对路径）。

INSERT IGNORE INTO `system_permission` (`id`, `parent_id`, `permission_type`, `permission_name`, `permission_code`,
                                 `path`, `component`, `icon`, `sort_order`, `visible`, `status`, `deleted`, `create_time`, `update_time`)
VALUES
    (9001, 0, 'catalog', '实习管理', 'INTERNSHIP', NULL, NULL, 'Notebook', 200, 1, 1, 0, NOW(), NOW()),
    (9002, 9001, 'menu', '合作企业', 'INTERNSHIP_ENTERPRISE', '/internship/enterprisePage', '/internship/enterprise/EnterprisePage.vue', 'OfficeBuilding', 201, 1, 1, 0, NOW(), NOW()),
    (9003, 9001, 'menu', '实习批次', 'INTERNSHIP_BATCH', '/internship/batchPage', '/internship/batch/BatchPage.vue', 'Calendar', 202, 1, 1, 0, NOW(), NOW()),
    (9004, 9001, 'menu', '岗位管理', 'INTERNSHIP_POSITION', '/internship/positionPage', '/internship/position/PositionPage.vue', 'Briefcase', 203, 1, 1, 0, NOW(), NOW()),
    (9005, 9001, 'menu', '申请与流程', 'INTERNSHIP_APPLICATION', '/internship/applicationPage', '/internship/application/ApplicationPage.vue', 'Document', 204, 1, 1, 0, NOW(), NOW()),
    (9006, 9001, 'menu', '周志批阅', 'INTERNSHIP_WEEKLY_LOG', '/internship/weeklyLogPage', '/internship/weekly/WeeklyLogAdminPage.vue', 'EditPen', 205, 1, 1, 0, NOW(), NOW()),
    (9007, 9001, 'menu', '实习材料', 'INTERNSHIP_MATERIAL', '/internship/materialPage', '/internship/material/MaterialPage.vue', 'FolderOpened', 206, 1, 1, 0, NOW(), NOW()),
    (9008, 9001, 'menu', '数据统计', 'INTERNSHIP_STATS', '/internship/statsPage', '/internship/stats/StatsPage.vue', 'DataAnalysis', 207, 1, 1, 0, NOW(), NOW());

-- 将上述权限授予超级管理员(role_id=1)与管理员(role_id=2)；若角色 id 不同请自行修改
INSERT IGNORE INTO `system_role_permission` (`id`, `role_id`, `permission_id`, `create_time`)
VALUES
    (200001, 1, 9001, NOW()),
    (200002, 2, 9001, NOW()),
    (200003, 1, 9002, NOW()),
    (200004, 2, 9002, NOW()),
    (200005, 1, 9003, NOW()),
    (200006, 2, 9003, NOW()),
    (200007, 1, 9004, NOW()),
    (200008, 2, 9004, NOW()),
    (200009, 1, 9005, NOW()),
    (200010, 2, 9005, NOW()),
    (200011, 1, 9006, NOW()),
    (200012, 2, 9006, NOW()),
    (200013, 1, 9007, NOW()),
    (200014, 2, 9007, NOW()),
    (200015, 1, 9008, NOW()),
    (200016, 2, 9008, NOW());
