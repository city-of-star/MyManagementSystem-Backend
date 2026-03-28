-- =============================================================================
-- 高校在线实习管理平台 — 业务表（增量脚本）+ 初始演示数据
-- 说明：在存放平台用户与组织机构等业务库中执行；库名请按实际环境修改。
-- 主键 ID：建议由应用层雪花/分布式 ID 生成（未使用 AUTO_INCREMENT）。
-- 初始数据 ID 段：1900000000000000xxx，与业务实现约定一致即可；重复执行请先执行「清除演示数据」一节。
-- =============================================================================

-- 将 USE 中的库名改为实际业务库名（若与用户、组织机构等表同库，须保持一致）
USE `internship_management`;

-- -----------------------------------------------------------------------------
-- 合作企业
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `intern_enterprise` (
    `id` bigint NOT NULL COMMENT '主键',
    `enterprise_name` varchar(128) NOT NULL COMMENT '企业名称',
    `credit_code` varchar(64) DEFAULT NULL COMMENT '统一社会信用代码',
    `contact_name` varchar(64) DEFAULT NULL COMMENT '联系人',
    `contact_phone` varchar(32) DEFAULT NULL COMMENT '联系电话',
    `address` varchar(255) DEFAULT NULL COMMENT '地址',
    `intro` text COMMENT '简介',
    `audit_status` varchar(32) NOT NULL DEFAULT 'PENDING' COMMENT '审核状态：PENDING-待审核，APPROVED-已通过，REJECTED-已拒绝',
    `audit_remark` varchar(512) DEFAULT NULL COMMENT '审核备注',
    `audit_by` bigint DEFAULT NULL COMMENT '审核人用户ID',
    `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '启用：0-停用，1-正常',
    `remark` varchar(512) DEFAULT NULL COMMENT '备注',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-否，1-是',
    `create_by` bigint DEFAULT NULL COMMENT '创建人',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint DEFAULT NULL COMMENT '更新人',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_audit_status` (`audit_status`),
    KEY `idx_deleted` (`deleted`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='实习合作企业';

-- -----------------------------------------------------------------------------
-- 实习批次
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `intern_batch` (
    `id` bigint NOT NULL COMMENT '主键',
    `batch_name` varchar(128) NOT NULL COMMENT '批次名称',
    `school_year` varchar(32) DEFAULT NULL COMMENT '学年，如 2025-2026',
    `term` varchar(32) DEFAULT NULL COMMENT '学期：1-春季，2-秋季 或自定义',
    `sign_up_start` datetime DEFAULT NULL COMMENT '报名开始时间',
    `sign_up_end` datetime DEFAULT NULL COMMENT '报名结束时间',
    `active` tinyint NOT NULL DEFAULT 1 COMMENT '是否当前可用：0-否，1-是',
    `remark` varchar(512) DEFAULT NULL COMMENT '备注',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_by` bigint DEFAULT NULL,
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_by` bigint DEFAULT NULL,
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_sign_up` (`sign_up_start`, `sign_up_end`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='实习批次';

-- -----------------------------------------------------------------------------
-- 实习岗位
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `intern_position` (
    `id` bigint NOT NULL COMMENT '主键',
    `batch_id` bigint NOT NULL COMMENT '批次ID',
    `enterprise_id` bigint NOT NULL COMMENT '企业ID',
    `title` varchar(128) NOT NULL COMMENT '岗位名称',
    `quota` int NOT NULL DEFAULT 1 COMMENT '计划人数',
    `requirement` text COMMENT '岗位要求',
    `start_date` date DEFAULT NULL COMMENT '实习开始日期',
    `end_date` date DEFAULT NULL COMMENT '实习结束日期',
    `status` varchar(32) NOT NULL DEFAULT 'DRAFT' COMMENT 'DRAFT-草稿，PUBLISHED-已发布，CLOSED-已结束',
    `remark` varchar(512) DEFAULT NULL,
    `deleted` tinyint NOT NULL DEFAULT 0,
    `create_by` bigint DEFAULT NULL,
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_by` bigint DEFAULT NULL,
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_batch_id` (`batch_id`),
    KEY `idx_enterprise_id` (`enterprise_id`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='实习岗位';

-- -----------------------------------------------------------------------------
-- 报名 / 实习申请（学生、导师 user_id 与平台用户主键一致）
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `intern_application` (
    `id` bigint NOT NULL COMMENT '主键',
    `batch_id` bigint NOT NULL COMMENT '批次ID（冗余便于查询）',
    `position_id` bigint NOT NULL COMMENT '岗位ID',
    `student_user_id` bigint NOT NULL COMMENT '学生用户ID',
    `school_mentor_user_id` bigint DEFAULT NULL COMMENT '校内指导教师用户ID',
    `enterprise_mentor_user_id` bigint DEFAULT NULL COMMENT '企业导师用户ID（可选）',
    `status` varchar(32) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING-待审核，APPROVED-已通过，REJECTED-已拒绝，CANCELLED-学生撤销，IN_PROGRESS-进行中，COMPLETED-已完成',
    `audit_remark` varchar(512) DEFAULT NULL COMMENT '审核说明',
    `audit_by` bigint DEFAULT NULL,
    `audit_time` datetime DEFAULT NULL,
    `remark` varchar(512) DEFAULT NULL,
    `deleted` tinyint NOT NULL DEFAULT 0,
    `create_by` bigint DEFAULT NULL,
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_by` bigint DEFAULT NULL,
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_student_position` (`student_user_id`, `position_id`),
    KEY `idx_batch_id` (`batch_id`),
    KEY `idx_position_id` (`position_id`),
    KEY `idx_student` (`student_user_id`),
    KEY `idx_mentor` (`school_mentor_user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='实习报名与申请';

-- 说明：同一学生同一岗位仅一条「有效」报名由应用层校验（deleted=0）。

-- -----------------------------------------------------------------------------
-- 周志
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `intern_weekly_log` (
    `id` bigint NOT NULL COMMENT '主键',
    `application_id` bigint NOT NULL COMMENT '实习申请ID',
    `week_index` int NOT NULL COMMENT '第几周（从1开始）',
    `title` varchar(255) DEFAULT NULL COMMENT '周志标题',
    `content` mediumtext COMMENT '周志正文（富文本）',
    `attachment_ids` varchar(1024) DEFAULT NULL COMMENT '附件ID列表，JSON 数组字符串',
    `status` varchar(32) NOT NULL DEFAULT 'SUBMITTED' COMMENT 'SUBMITTED-已提交，APPROVED-已通过，REJECTED-退回',
    `review_comment` varchar(512) DEFAULT NULL COMMENT '教师批阅意见',
    `review_by` bigint DEFAULT NULL,
    `review_time` datetime DEFAULT NULL,
    `deleted` tinyint NOT NULL DEFAULT 0,
    `create_by` bigint DEFAULT NULL,
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_by` bigint DEFAULT NULL,
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_application_id` (`application_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='实习周志';

-- -----------------------------------------------------------------------------
-- 实习材料（协议、安全告知等，一条记录对应一个附件）
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `intern_material` (
    `id` bigint NOT NULL COMMENT '主键',
    `application_id` bigint NOT NULL COMMENT '实习申请ID',
    `material_type` varchar(32) NOT NULL COMMENT 'AGREEMENT-实习协议，SAFETY-安全告知，OTHER-其他',
    `material_name` varchar(128) DEFAULT NULL COMMENT '材料名称（展示用，可与类型默认文案一致）',
    `attachment_id` bigint NOT NULL COMMENT '平台附件表主键',
    `status` varchar(32) NOT NULL DEFAULT 'SUBMITTED' COMMENT 'SUBMITTED-已提交，APPROVED-已通过，REJECTED-已退回',
    `audit_remark` varchar(512) DEFAULT NULL COMMENT '审核说明',
    `audit_by` bigint DEFAULT NULL,
    `audit_time` datetime DEFAULT NULL,
    `remark` varchar(512) DEFAULT NULL,
    `deleted` tinyint NOT NULL DEFAULT 0,
    `create_by` bigint DEFAULT NULL,
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_by` bigint DEFAULT NULL,
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_application_id` (`application_id`),
    KEY `idx_material_type` (`material_type`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='实习材料';

-- -----------------------------------------------------------------------------
-- 评价与成绩
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `intern_evaluation` (
    `id` bigint NOT NULL COMMENT '主键',
    `application_id` bigint NOT NULL COMMENT '实习申请ID',
    `school_score` decimal(5,2) DEFAULT NULL COMMENT '校内评分',
    `school_comment` text COMMENT '校内鉴定/评语',
    `school_by` bigint DEFAULT NULL,
    `school_time` datetime DEFAULT NULL,
    `enterprise_score` decimal(5,2) DEFAULT NULL COMMENT '企业评分',
    `enterprise_comment` text COMMENT '企业评语',
    `enterprise_by` bigint DEFAULT NULL,
    `enterprise_time` datetime DEFAULT NULL,
    `final_score` decimal(5,2) DEFAULT NULL COMMENT '综合成绩',
    `final_remark` varchar(512) DEFAULT NULL COMMENT '综合说明',
    `deleted` tinyint NOT NULL DEFAULT 0,
    `create_by` bigint DEFAULT NULL,
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_by` bigint DEFAULT NULL,
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_application` (`application_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='实习评价与成绩';

-- =============================================================================
-- 清除演示数据（主键冲突或需重置演示数据时执行；注意外键式依赖顺序）
-- =============================================================================
-- DELETE FROM `intern_evaluation` WHERE `id` = 1900000000000000022;
-- DELETE FROM `intern_material` WHERE `id` = 1900000000000000021;
-- DELETE FROM `intern_weekly_log` WHERE `id` = 1900000000000000020;
-- DELETE FROM `intern_application` WHERE `id` = 1900000000000000010;
-- DELETE FROM `intern_position` WHERE `id` IN (1900000000000000004, 1900000000000000005);
-- DELETE FROM `intern_batch` WHERE `id` = 1900000000000000003;
-- DELETE FROM `intern_enterprise` WHERE `id` IN (1900000000000000001, 1900000000000000002);

-- =============================================================================
-- 初始数据（演示环境；首次执行使用 INSERT。若报主键重复，请先执行上方 DELETE 再执行本节）
-- 说明：
--   1) 演示学生用户 ID：1900000000000000099 —— 请改为环境中真实学生用户主键，或先创建该 ID 的测试账号。
--   2) 演示教师用户 ID：1900000000000000088 —— 校内导师占位，可改为真实教师用户主键。
--   3) 附件 ID 1900000000000000071 为占位：需先在平台文件服务中上传文件得到真实 attachment_id 后再更新本行，或删除材料演示行。
-- =============================================================================

INSERT INTO `intern_enterprise` (`id`, `enterprise_name`, `credit_code`, `contact_name`, `contact_phone`, `address`, `intro`,
    `audit_status`, `audit_remark`, `audit_by`, `audit_time`, `status`, `remark`, `deleted`, `create_by`, `update_by`)
VALUES
    (1900000000000000001, '示例科技有限责任公司', '91110000MA00000001', '张经理', '13800000001', '北京市海淀区中关村大街1号',
     '从事软件开发与技术服务，长期接收高校实习。', 'PENDING', NULL, NULL, NULL, 1, '演示：待审核企业', 0, 1, 1),
    (1900000000000000002, '智慧教育科技股份有限公司', '91110000MA00000002', '李老师', '13800000002', '上海市浦东新区张江路100号',
     '教育信息化与在线教学平台研发。', 'APPROVED', '资质齐全', 1, NOW(), 1, '演示：已通过企业', 0, 1, 1);

INSERT INTO `intern_batch` (`id`, `batch_name`, `school_year`, `term`, `sign_up_start`, `sign_up_end`, `active`, `remark`, `deleted`, `create_by`, `update_by`)
VALUES
    (1900000000000000003, '2025年春季实习批次', '2024-2025', '1',
     '2025-03-01 00:00:00', '2025-06-30 23:59:59', 1, '演示批次', 0, 1, 1);

INSERT INTO `intern_position` (`id`, `batch_id`, `enterprise_id`, `title`, `quota`, `requirement`, `start_date`, `end_date`, `status`, `remark`, `deleted`, `create_by`, `update_by`)
VALUES
    (1900000000000000004, 1900000000000000003, 1900000000000000002, 'Java 后端开发实习', 5,
     '熟悉 Java、Spring Boot；了解 MySQL、Redis；有微服务项目经验优先。',
     '2025-04-01', '2025-06-30', 'PUBLISHED', '演示：已发布岗位', 0, 1, 1),
    (1900000000000000005, 1900000000000000003, 1900000000000000002, '前端开发实习（草稿）', 3,
     '熟悉 Vue3、TypeScript。',
     '2025-04-01', '2025-06-30', 'DRAFT', '演示：未发布', 0, 1, 1);

-- 以下依赖演示用户 ID：学生 1900000000000000099、校内导师 1900000000000000088；不存在时请先在平台创建或整段注释
INSERT INTO `intern_application` (`id`, `batch_id`, `position_id`, `student_user_id`, `school_mentor_user_id`, `enterprise_mentor_user_id`,
    `status`, `audit_remark`, `audit_by`, `audit_time`, `remark`, `deleted`, `create_by`, `update_by`)
VALUES
    (1900000000000000010, 1900000000000000003, 1900000000000000004, 1900000000000000099, 1900000000000000088, NULL,
     'IN_PROGRESS', NULL, 1, NOW(), '演示报名记录', 0, 1900000000000000099, 1);

INSERT INTO `intern_weekly_log` (`id`, `application_id`, `week_index`, `title`, `content`, `attachment_ids`, `status`, `review_comment`, `review_by`, `review_time`, `deleted`, `create_by`, `update_by`)
VALUES
    (1900000000000000020, 1900000000000000010, 1, '第1周实习周志',
     '<p>本周熟悉项目结构与开发规范，完成环境搭建。</p>', NULL, 'APPROVED', '内容符合要求', 1900000000000000088, NOW(), 0, 1900000000000000099, 1900000000000000088);

-- attachment_id 占位：请改为文件上传接口返回的真实 ID，或删除本行
INSERT INTO `intern_material` (`id`, `application_id`, `material_type`, `material_name`, `attachment_id`, `status`, `audit_remark`, `audit_by`, `audit_time`, `remark`, `deleted`, `create_by`, `update_by`)
VALUES
    (1900000000000000021, 1900000000000000010, 'AGREEMENT', '实习协议', 1900000000000000071, 'SUBMITTED', NULL, NULL, NULL, '演示：请替换 attachment_id 为真实附件ID', 0, 1900000000000000099, 1900000000000000099);

INSERT INTO `intern_evaluation` (`id`, `application_id`, `school_score`, `school_comment`, `school_by`, `school_time`,
    `enterprise_score`, `enterprise_comment`, `enterprise_by`, `enterprise_time`, `final_score`, `final_remark`, `deleted`, `create_by`, `update_by`)
VALUES
    (1900000000000000022, 1900000000000000010, 85.00, '学习态度认真，能独立完成分配任务。', 1900000000000000088, NOW(),
     NULL, NULL, NULL, NULL, NULL, NULL, 0, 1, 1);

-- =============================================================================
-- 结束
-- =============================================================================
