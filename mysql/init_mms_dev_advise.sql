-- ==================== 意见管理服务相关表 ====================

-- 1. 意见表
CREATE TABLE IF NOT EXISTS `advise` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '意见ID',
    `title` varchar(256) NOT NULL COMMENT '意见标题',
    `content` text NOT NULL COMMENT '意见内容',
    `category` varchar(64) NOT NULL COMMENT '分类（字典值：advise_category）',
    `priority` varchar(32) NOT NULL DEFAULT '中' COMMENT '优先级（字典值：advise_priority，低/中/高/紧急）',
    `status` varchar(32) NOT NULL DEFAULT '待处理' COMMENT '状态（字典值：advise_status，待处理/处理中/已处理/已关闭）',
    `submitter_id` bigint NOT NULL COMMENT '提交人ID',
    `assignee_id` bigint DEFAULT NULL COMMENT '处理人ID（可为空，未分配）',
    `is_important` tinyint NOT NULL DEFAULT 0 COMMENT '是否重要：0-否，1-是',
    `knowledge_id` bigint DEFAULT NULL COMMENT '关联知识库ID（可为空，预留字段）',
    `version` int NOT NULL DEFAULT 1 COMMENT '版本号（乐观锁）',
    `remark` varchar(512) DEFAULT NULL COMMENT '备注',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_submitter_id` (`submitter_id`),
    KEY `idx_assignee_id` (`assignee_id`),
    KEY `idx_status` (`status`),
    KEY `idx_category` (`category`),
    KEY `idx_priority` (`priority`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_status_assignee` (`status`, `assignee_id`),
    KEY `idx_submitter_status` (`submitter_id`, `status`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='意见表';

-- 2. 意见回复表
CREATE TABLE IF NOT EXISTS `advise_reply` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '回复ID',
    `advise_id` bigint NOT NULL COMMENT '意见ID',
    `reply_type` varchar(32) NOT NULL COMMENT '回复类型：处理回复-处理人的回复，追问-提交人的追问',
    `replyer_id` bigint NOT NULL COMMENT '回复人ID',
    `content` text NOT NULL COMMENT '回复内容',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_advise_id` (`advise_id`),
    KEY `idx_replyer_id` (`replyer_id`),
    KEY `idx_reply_type` (`reply_type`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_advise_create_time` (`advise_id`, `create_time`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='意见回复表';

-- 3. 通知表（站内通知）
CREATE TABLE IF NOT EXISTS `notification` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '通知ID',
    `user_id` bigint NOT NULL COMMENT '接收人ID',
    `type` varchar(32) NOT NULL COMMENT '通知类型：意见分配-意见被分配，意见回复-意见有回复，意见追问-意见有追问，状态变更-意见状态变更，意见转派-意见被转派，优先级调整-优先级被调整',
    `title` varchar(256) NOT NULL COMMENT '通知标题',
    `content` varchar(512) DEFAULT NULL COMMENT '通知内容',
    `related_id` bigint DEFAULT NULL COMMENT '关联ID（意见ID）',
    `is_read` tinyint NOT NULL DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_type` (`type`),
    KEY `idx_is_read` (`is_read`),
    KEY `idx_related_id` (`related_id`),
    KEY `idx_user_read` (`user_id`, `is_read`),
    KEY `idx_type_related` (`type`, `related_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='通知表（站内通知）';

-- ==================== 初始化数据字典配置 ====================

-- 注意：以下数据字典需要在系统初始化时配置，这里仅作说明
-- 1. 意见分类字典 (advise_category)
--    字典类型编码：advise_category
--    字典数据示例：
--      - 功能建议
--      - Bug反馈
--      - 用户体验
--      - 流程优化
--      - 其他

-- 2. 意见优先级字典 (advise_priority)
--    字典类型编码：advise_priority
--    字典数据：
--      - 低
--      - 中
--      - 高
--      - 紧急

-- 3. 意见状态字典 (advise_status)
--    字典类型编码：advise_status
--    字典数据：
--      - 待处理
--      - 处理中
--      - 已处理
--      - 已关闭

-- 4. 智能分配规则字典 (advise_assign_rule)
--    字典类型编码：advise_assign_rule
--    字典数据：分类 -> 默认处理人ID的映射
--    示例：
--      - 功能建议 -> 1001 (产品经理ID)
--      - Bug反馈 -> 1002 (开发负责人ID)
--      - 流程优化 -> 1003 (流程管理负责人ID)
--    注意：字典数据的 dict_value 存储处理人ID，dict_label 存储分类名称
