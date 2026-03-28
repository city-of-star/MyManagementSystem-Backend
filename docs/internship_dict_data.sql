-- 实习模块数据字典（依赖已有 system_dict_type / system_dict_data 表结构，与 init_mms_dev_core 一致）
-- 执行库：与 usercenter/base 相同（如 internship_management）
-- 类型 id 从 17 起（init 中 dict_type 最大为 16）；数据 id 从 100 起避免与 init 1–53 冲突。
-- 若 id 已占用请调整。

INSERT IGNORE INTO `system_dict_type` (`id`, `dict_type_code`, `dict_type_name`, `status`, `sort_order`, `remark`, `deleted`, `create_time`, `update_time`)
VALUES
    (17, 'intern_enterprise_audit_status', '实习-企业审核状态', 1, 100, '合作企业入驻审核', 0, NOW(), NOW()),
    (18, 'intern_position_status', '实习-岗位状态', 1, 101, '岗位草稿/发布/结束', 0, NOW(), NOW()),
    (19, 'intern_application_status', '实习-申请流程状态', 1, 102, '学生报名与实习流程', 0, NOW(), NOW()),
    (20, 'intern_submission_review_status', '实习-提交审阅状态', 1, 103, '周志/材料等提交后审核', 0, NOW(), NOW()),
    (21, 'intern_material_type', '实习-材料类型', 1, 104, '实习材料分类', 0, NOW(), NOW());

INSERT IGNORE INTO `system_dict_data` (`id`, `dict_type_id`, `dict_label`, `dict_value`, `dict_sort`, `is_default`, `status`, `remark`, `deleted`, `create_time`, `update_time`)
VALUES
    -- 企业审核
    (100, 17, '待审核', 'PENDING', 1, 1, 1, NULL, 0, NOW(), NOW()),
    (101, 17, '已通过', 'APPROVED', 2, 0, 1, NULL, 0, NOW(), NOW()),
    (102, 17, '已拒绝', 'REJECTED', 3, 0, 1, NULL, 0, NOW(), NOW()),
    -- 岗位状态
    (103, 18, '草稿', 'DRAFT', 1, 1, 1, NULL, 0, NOW(), NOW()),
    (104, 18, '已发布', 'PUBLISHED', 2, 0, 1, NULL, 0, NOW(), NOW()),
    (105, 18, '已结束', 'CLOSED', 3, 0, 1, NULL, 0, NOW(), NOW()),
    -- 申请状态
    (106, 19, '待审核', 'PENDING', 1, 1, 1, NULL, 0, NOW(), NOW()),
    (107, 19, '已通过', 'APPROVED', 2, 0, 1, NULL, 0, NOW(), NOW()),
    (108, 19, '已拒绝', 'REJECTED', 3, 0, 1, NULL, 0, NOW(), NOW()),
    (109, 19, '已取消', 'CANCELLED', 4, 0, 1, NULL, 0, NOW(), NOW()),
    (110, 19, '实习中', 'IN_PROGRESS', 5, 0, 1, NULL, 0, NOW(), NOW()),
    (111, 19, '已完成', 'COMPLETED', 6, 0, 1, NULL, 0, NOW(), NOW()),
    -- 周志/材料审核态
    (112, 20, '已提交', 'SUBMITTED', 1, 1, 1, NULL, 0, NOW(), NOW()),
    (113, 20, '已通过', 'APPROVED', 2, 0, 1, NULL, 0, NOW(), NOW()),
    (114, 20, '退回', 'REJECTED', 3, 0, 1, NULL, 0, NOW(), NOW()),
    -- 材料类型
    (115, 21, '三方协议', 'AGREEMENT', 1, 1, 1, NULL, 0, NOW(), NOW()),
    (116, 21, '安全告知', 'SAFETY', 2, 0, 1, NULL, 0, NOW(), NOW()),
    (117, 21, '其他', 'OTHER', 3, 0, 1, NULL, 0, NOW(), NOW());
