/*
 Navicat MySQL Dump SQL

 Source Server         : local_mysql
 Source Server Type    : MySQL
 Source Server Version : 80031 (8.0.31)
 Source Host           : localhost:3306
 Source Schema         : internship_management

 Target Server Type    : MySQL
 Target Server Version : 80031 (8.0.31)
 File Encoding         : 65001

 Date: 29/03/2026 15:50:25
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for audit_api_access_log
-- ----------------------------
DROP TABLE IF EXISTS `audit_api_access_log`;
CREATE TABLE `audit_api_access_log`  (
  `id` bigint NOT NULL COMMENT '日志ID',
  `trace_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '链路追踪ID',
  `service_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '服务名',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID',
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '用户名',
  `request_method` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '请求方法',
  `request_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '请求URL',
  `request_ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '请求IP',
  `request_params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '请求参数（脱敏后）',
  `http_status` int NULL DEFAULT NULL COMMENT 'HTTP状态码',
  `business_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '业务状态码',
  `access_status` tinyint NOT NULL DEFAULT 1 COMMENT '访问状态：0-失败，1-成功',
  `cost_ms` bigint NULL DEFAULT NULL COMMENT '耗时（毫秒）',
  `access_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '访问时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_trace_id`(`trace_id` ASC) USING BTREE,
  INDEX `idx_service_name`(`service_name` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_username`(`username` ASC) USING BTREE,
  INDEX `idx_request_method`(`request_method` ASC) USING BTREE,
  INDEX `idx_access_status`(`access_status` ASC) USING BTREE,
  INDEX `idx_access_time`(`access_time` ASC) USING BTREE,
  INDEX `idx_url_time`(`request_url` ASC, `access_time` ASC) USING BTREE,
  INDEX `idx_method_status_time`(`request_method` ASC, `access_status` ASC, `access_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '接口访问日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of audit_api_access_log
-- ----------------------------

-- ----------------------------
-- Table structure for audit_exception_log
-- ----------------------------
DROP TABLE IF EXISTS `audit_exception_log`;
CREATE TABLE `audit_exception_log`  (
  `id` bigint NOT NULL COMMENT '日志ID',
  `trace_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '链路追踪ID',
  `service_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '服务名称',
  `module` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '业务模块',
  `exception_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '异常类型',
  `exception_message` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '异常信息',
  `stack_trace` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '异常堆栈',
  `request_method` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '请求方法',
  `request_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '请求URL',
  `request_ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '请求IP',
  `request_params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '请求参数（脱敏后）',
  `user_id` bigint NULL DEFAULT NULL COMMENT '操作用户ID',
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '操作用户名',
  `resolved` tinyint NOT NULL DEFAULT 0 COMMENT '是否已处理：0-未处理，1-已处理',
  `resolve_by` bigint NULL DEFAULT NULL COMMENT '处理人ID',
  `resolve_time` datetime NULL DEFAULT NULL COMMENT '处理时间',
  `resolve_remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '处理备注',
  `occur_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发生时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_trace_id`(`trace_id` ASC) USING BTREE,
  INDEX `idx_service_name`(`service_name` ASC) USING BTREE,
  INDEX `idx_module`(`module` ASC) USING BTREE,
  INDEX `idx_exception_type`(`exception_type` ASC) USING BTREE,
  INDEX `idx_resolved`(`resolved` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_occur_time`(`occur_time` ASC) USING BTREE,
  INDEX `idx_resolved_occur_time`(`resolved` ASC, `occur_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '系统异常日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of audit_exception_log
-- ----------------------------

-- ----------------------------
-- Table structure for audit_operation_log
-- ----------------------------
DROP TABLE IF EXISTS `audit_operation_log`;
CREATE TABLE `audit_operation_log`  (
  `id` bigint NOT NULL COMMENT '日志ID',
  `trace_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '链路追踪ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '操作用户ID',
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '操作用户名',
  `module` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '业务模块',
  `operation_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '操作类型：create/update/delete/export/assign/login/logout等',
  `operation_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '操作描述',
  `request_method` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '请求方法：GET/POST/PUT/DELETE',
  `request_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '请求URL',
  `request_ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '请求IP',
  `request_params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '请求参数（脱敏后）',
  `response_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '响应结果摘要',
  `operation_status` tinyint NOT NULL DEFAULT 1 COMMENT '操作状态：0-失败，1-成功',
  `error_message` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '失败原因/异常摘要',
  `cost_ms` bigint NULL DEFAULT NULL COMMENT '耗时（毫秒）',
  `operation_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_trace_id`(`trace_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_username`(`username` ASC) USING BTREE,
  INDEX `idx_module`(`module` ASC) USING BTREE,
  INDEX `idx_operation_type`(`operation_type` ASC) USING BTREE,
  INDEX `idx_operation_status`(`operation_status` ASC) USING BTREE,
  INDEX `idx_operation_time`(`operation_time` ASC) USING BTREE,
  INDEX `idx_user_operation_time`(`user_id` ASC, `operation_time` ASC) USING BTREE,
  INDEX `idx_module_type_time`(`module` ASC, `operation_type` ASC, `operation_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '用户操作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of audit_operation_log
-- ----------------------------

-- ----------------------------
-- Table structure for audit_user_login_log
-- ----------------------------
DROP TABLE IF EXISTS `audit_user_login_log`;
CREATE TABLE `audit_user_login_log`  (
  `id` bigint NOT NULL COMMENT '日志ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID',
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '用户名',
  `login_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '登录类型：password-密码登录，sms-短信登录，email-邮箱登录',
  `login_ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '登录IP',
  `login_location` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '登录地点',
  `user_agent` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '用户代理（浏览器信息）',
  `login_status` tinyint NOT NULL DEFAULT 0 COMMENT '登录状态：0-失败，1-成功',
  `login_message` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '登录消息（失败原因等）',
  `login_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_username`(`username` ASC) USING BTREE,
  INDEX `idx_login_time`(`login_time` ASC) USING BTREE,
  INDEX `idx_login_status`(`login_status` ASC) USING BTREE,
  INDEX `idx_user_login_time`(`user_id` ASC, `login_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '用户登录日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of audit_user_login_log
-- ----------------------------
INSERT INTO `audit_user_login_log` VALUES (2037767173766344705, 1, 'superAdmin', 'password', '0:0:0:0:0:0:0:1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-28 13:42:09');
INSERT INTO `audit_user_login_log` VALUES (2037769778278133761, 2, 'lhy', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-28 13:52:30');
INSERT INTO `audit_user_login_log` VALUES (2037769966325559298, 2, 'lhy', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-28 13:53:15');
INSERT INTO `audit_user_login_log` VALUES (2037805301113778178, 2, 'lhy', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-28 16:13:39');
INSERT INTO `audit_user_login_log` VALUES (2037807489412198401, 2, 'lhy', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-28 16:22:21');
INSERT INTO `audit_user_login_log` VALUES (2037807579124166657, 2, 'lhy', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-28 16:22:42');
INSERT INTO `audit_user_login_log` VALUES (2037807933773541377, 2, 'lhy', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-28 16:24:07');
INSERT INTO `audit_user_login_log` VALUES (2037808505113243649, 2, 'lhy', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-28 16:26:23');
INSERT INTO `audit_user_login_log` VALUES (2037808638194315266, 2, 'lhy', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-28 16:26:55');
INSERT INTO `audit_user_login_log` VALUES (2037810405892124674, NULL, 'lhy', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 0, '账号不存在', '2026-03-28 16:33:56');
INSERT INTO `audit_user_login_log` VALUES (2037810420429582338, 1, 'superAdmin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-28 16:34:00');
INSERT INTO `audit_user_login_log` VALUES (2037812389336543233, 1, 'superAdmin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-28 16:41:49');
INSERT INTO `audit_user_login_log` VALUES (2037818255716610049, 1, 'superAdmin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-28 17:05:08');
INSERT INTO `audit_user_login_log` VALUES (2037820756763336706, 1, 'superAdmin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-28 17:15:04');
INSERT INTO `audit_user_login_log` VALUES (2037824796834390017, 1, 'superAdmin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-28 17:31:07');
INSERT INTO `audit_user_login_log` VALUES (2037825248183443457, 5, 'intern_admin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 0, '密码错误', '2026-03-28 17:32:55');
INSERT INTO `audit_user_login_log` VALUES (2037825259084439553, 5, 'intern_admin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 0, '密码错误', '2026-03-28 17:32:57');
INSERT INTO `audit_user_login_log` VALUES (2037825262251139074, 5, 'intern_admin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 0, '密码错误', '2026-03-28 17:32:58');
INSERT INTO `audit_user_login_log` VALUES (2037825266676129793, 5, 'intern_admin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 0, '密码错误', '2026-03-28 17:32:59');
INSERT INTO `audit_user_login_log` VALUES (2037825269767331842, 5, 'intern_admin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 0, '密码错误', '2026-03-28 17:33:00');
INSERT INTO `audit_user_login_log` VALUES (2037825273026306049, 5, 'intern_admin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 0, '账号已被临时锁定', '2026-03-28 17:33:01');
INSERT INTO `audit_user_login_log` VALUES (2037825293817470977, 5, 'intern_admin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 0, '账号已被临时锁定', '2026-03-28 17:33:06');
INSERT INTO `audit_user_login_log` VALUES (2037825758890287105, 5, 'intern_admin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 0, '密码错误', '2026-03-28 17:34:57');
INSERT INTO `audit_user_login_log` VALUES (2037825821565771778, 5, 'intern_admin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 0, '密码错误', '2026-03-28 17:35:11');
INSERT INTO `audit_user_login_log` VALUES (2037825997802037250, 5, 'intern_admin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-28 17:35:54');
INSERT INTO `audit_user_login_log` VALUES (2037826149744893954, 5, 'intern_admin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-28 17:36:30');
INSERT INTO `audit_user_login_log` VALUES (2037826498874564609, 6, 'enterprise_user', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-28 17:37:53');
INSERT INTO `audit_user_login_log` VALUES (2037826774348062721, 6, 'enterprise_user', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-28 17:38:59');
INSERT INTO `audit_user_login_log` VALUES (2037826832426590209, 7, 'teacher_user', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-28 17:39:13');
INSERT INTO `audit_user_login_log` VALUES (2037826970511466498, 8, 'student_user', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-28 17:39:45');
INSERT INTO `audit_user_login_log` VALUES (2037827124178182146, 6, 'enterprise_user', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 0, '密码错误', '2026-03-28 17:40:22');
INSERT INTO `audit_user_login_log` VALUES (2037827173511585793, 6, 'enterprise_user', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-28 17:40:34');
INSERT INTO `audit_user_login_log` VALUES (2037827212560556033, 5, 'intern_admin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 0, '密码错误', '2026-03-28 17:40:43');
INSERT INTO `audit_user_login_log` VALUES (2037827271566024706, 5, 'intern_admin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-28 17:40:57');
INSERT INTO `audit_user_login_log` VALUES (2037827435185823746, 7, 'teacher_user', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-28 17:41:36');
INSERT INTO `audit_user_login_log` VALUES (2037827497462849538, 8, 'student_user', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-28 17:41:51');
INSERT INTO `audit_user_login_log` VALUES (2037827515213144065, 8, 'student_user', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-28 17:41:55');
INSERT INTO `audit_user_login_log` VALUES (2037827757669081089, 7, 'teacher_user', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-28 17:42:53');
INSERT INTO `audit_user_login_log` VALUES (2037838324643389441, 7, 'teacher_user', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-28 18:24:52');
INSERT INTO `audit_user_login_log` VALUES (2037838835308290049, 1, 'superAdmin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 0, '密码错误', '2026-03-28 18:26:54');
INSERT INTO `audit_user_login_log` VALUES (2037838843986305025, 1, 'superAdmin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 0, '密码错误', '2026-03-28 18:26:56');
INSERT INTO `audit_user_login_log` VALUES (2037838848776200193, 1, 'superAdmin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 0, '密码错误', '2026-03-28 18:26:57');
INSERT INTO `audit_user_login_log` VALUES (2037838851754156033, 1, 'superAdmin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 0, '密码错误', '2026-03-28 18:26:58');
INSERT INTO `audit_user_login_log` VALUES (2037838856225284098, 1, 'superAdmin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 0, '密码错误', '2026-03-28 18:26:59');
INSERT INTO `audit_user_login_log` VALUES (2037838858381156354, 1, 'superAdmin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 0, '账号已被临时锁定', '2026-03-28 18:27:00');
INSERT INTO `audit_user_login_log` VALUES (2037838861833068545, 1, 'superAdmin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 0, '账号已被临时锁定', '2026-03-28 18:27:01');
INSERT INTO `audit_user_login_log` VALUES (2037838863267520513, 1, 'superAdmin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 0, '账号已被临时锁定', '2026-03-28 18:27:01');
INSERT INTO `audit_user_login_log` VALUES (2037838866702655489, 1, 'superAdmin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 0, '账号已被临时锁定', '2026-03-28 18:27:02');
INSERT INTO `audit_user_login_log` VALUES (2037838867726065666, 1, 'superAdmin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 0, '账号已被临时锁定', '2026-03-28 18:27:02');
INSERT INTO `audit_user_login_log` VALUES (2037838869856772098, 1, 'superAdmin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 0, '账号已被临时锁定', '2026-03-28 18:27:02');
INSERT INTO `audit_user_login_log` VALUES (2037838871010205698, 1, 'superAdmin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 0, '账号已被临时锁定', '2026-03-28 18:27:03');
INSERT INTO `audit_user_login_log` VALUES (2037838873795223554, 1, 'superAdmin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 0, '账号已被临时锁定', '2026-03-28 18:27:03');
INSERT INTO `audit_user_login_log` VALUES (2037838874827022338, 1, 'superAdmin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 0, '账号已被临时锁定', '2026-03-28 18:27:04');
INSERT INTO `audit_user_login_log` VALUES (2037838876047564802, 1, 'superAdmin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 0, '账号已被临时锁定', '2026-03-28 18:27:04');
INSERT INTO `audit_user_login_log` VALUES (2037838878610284545, 1, 'superAdmin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 0, '账号已被临时锁定', '2026-03-28 18:27:05');
INSERT INTO `audit_user_login_log` VALUES (2037838880099262465, 1, 'superAdmin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 0, '账号已被临时锁定', '2026-03-28 18:27:05');
INSERT INTO `audit_user_login_log` VALUES (2037839018444185601, 1, 'superAdmin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 0, '账号已被临时锁定', '2026-03-28 18:27:38');
INSERT INTO `audit_user_login_log` VALUES (2037839061964283906, 1, 'superAdmin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 0, '账号已被临时锁定', '2026-03-28 18:27:48');
INSERT INTO `audit_user_login_log` VALUES (2037839125977751554, 1, 'superAdmin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 0, '账号已被临时锁定', '2026-03-28 18:28:04');
INSERT INTO `audit_user_login_log` VALUES (2037839481113665538, 1, 'superAdmin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 0, '账号已被临时锁定', '2026-03-28 18:29:28');
INSERT INTO `audit_user_login_log` VALUES (2037839485840646146, 1, 'superAdmin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 0, '账号已被临时锁定', '2026-03-28 18:29:29');
INSERT INTO `audit_user_login_log` VALUES (2037839486998274049, 1, 'superAdmin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 0, '账号已被临时锁定', '2026-03-28 18:29:30');
INSERT INTO `audit_user_login_log` VALUES (2037839580669665282, 1, 'superAdmin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 0, '密码错误', '2026-03-28 18:29:52');
INSERT INTO `audit_user_login_log` VALUES (2037839638005800961, 1, 'superAdmin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-28 18:30:06');
INSERT INTO `audit_user_login_log` VALUES (2037840198532587522, 1, 'superAdmin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-28 18:32:19');
INSERT INTO `audit_user_login_log` VALUES (2037840393278316546, 1, 'superAdmin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-28 18:33:06');
INSERT INTO `audit_user_login_log` VALUES (2037843592500469762, 1, 'superAdmin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-28 18:45:48');
INSERT INTO `audit_user_login_log` VALUES (2037847170644398081, 1, 'superAdmin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-28 19:00:02');
INSERT INTO `audit_user_login_log` VALUES (2038079747120648193, 1, 'superAdmin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-29 10:24:12');
INSERT INTO `audit_user_login_log` VALUES (2038082070605680642, 1, 'superAdmin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-29 10:33:26');
INSERT INTO `audit_user_login_log` VALUES (2038127629114781698, 1, 'superAdmin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-29 13:34:28');
INSERT INTO `audit_user_login_log` VALUES (2038135286433312769, 8, 'student_user', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-29 14:04:54');
INSERT INTO `audit_user_login_log` VALUES (2038135381996335105, 7, 'teacher_user', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-29 14:05:16');
INSERT INTO `audit_user_login_log` VALUES (2038136709883621377, 7, 'teacher_user', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-29 14:10:33');
INSERT INTO `audit_user_login_log` VALUES (2038136723913568257, 7, 'teacher_user', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-29 14:10:36');
INSERT INTO `audit_user_login_log` VALUES (2038136740984385538, 7, 'teacher_user', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-29 14:10:40');
INSERT INTO `audit_user_login_log` VALUES (2038136768876507137, 1, 'superAdmin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-29 14:10:47');
INSERT INTO `audit_user_login_log` VALUES (2038137095512125442, 5, 'intern_admin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-29 14:12:05');
INSERT INTO `audit_user_login_log` VALUES (2038137474899505154, 8, 'student_user', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-29 14:13:35');
INSERT INTO `audit_user_login_log` VALUES (2038137665547399170, 7, 'teacher_user', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-29 14:14:21');
INSERT INTO `audit_user_login_log` VALUES (2038137853695488001, 5, 'intern_admin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-29 14:15:06');
INSERT INTO `audit_user_login_log` VALUES (2038138141529600002, 8, 'student_user', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-29 14:16:14');
INSERT INTO `audit_user_login_log` VALUES (2038138364591075329, 5, 'intern_admin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-29 14:17:08');
INSERT INTO `audit_user_login_log` VALUES (2038138409390436354, 1, 'superAdmin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-29 14:17:18');
INSERT INTO `audit_user_login_log` VALUES (2038138676898951169, 5, 'intern_admin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-29 14:18:22');
INSERT INTO `audit_user_login_log` VALUES (2038138813289328641, 8, 'student_user', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-29 14:18:55');
INSERT INTO `audit_user_login_log` VALUES (2038138953261641729, 7, 'teacher_user', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-29 14:19:28');
INSERT INTO `audit_user_login_log` VALUES (2038139038871580674, 8, 'student_user', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-29 14:19:48');
INSERT INTO `audit_user_login_log` VALUES (2038139098732687361, 5, 'intern_admin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-29 14:20:03');
INSERT INTO `audit_user_login_log` VALUES (2038141087487094785, 1, 'superAdmin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-29 14:27:57');
INSERT INTO `audit_user_login_log` VALUES (2038141185696722945, 5, 'intern_admin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-29 14:28:20');
INSERT INTO `audit_user_login_log` VALUES (2038141268219654146, 1, 'superAdmin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-29 14:28:40');
INSERT INTO `audit_user_login_log` VALUES (2038141354299355138, 5, 'intern_admin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-29 14:29:00');
INSERT INTO `audit_user_login_log` VALUES (2038141992995266561, 1, 'superAdmin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-29 14:31:33');
INSERT INTO `audit_user_login_log` VALUES (2038142100142956546, 5, 'intern_admin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-29 14:31:58');
INSERT INTO `audit_user_login_log` VALUES (2038142375771643906, 1, 'superAdmin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-29 14:33:04');
INSERT INTO `audit_user_login_log` VALUES (2038142691036504066, 5, 'intern_admin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-29 14:34:19');
INSERT INTO `audit_user_login_log` VALUES (2038144654562172929, 7, 'teacher_user', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-29 14:42:07');
INSERT INTO `audit_user_login_log` VALUES (2038144726251216897, 8, 'student_user', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-29 14:42:24');
INSERT INTO `audit_user_login_log` VALUES (2038144774041116674, 5, 'intern_admin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-29 14:42:36');
INSERT INTO `audit_user_login_log` VALUES (2038144890416275457, 8, 'student_user', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-29 14:43:03');
INSERT INTO `audit_user_login_log` VALUES (2038145217479712769, 8, 'student_user', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-29 14:44:21');
INSERT INTO `audit_user_login_log` VALUES (2038145252829306881, 1, 'superAdmin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-29 14:44:30');
INSERT INTO `audit_user_login_log` VALUES (2038145405355171842, 5, 'intern_admin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-29 14:45:06');
INSERT INTO `audit_user_login_log` VALUES (2038146577696686081, 1, 'superAdmin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-29 14:49:46');
INSERT INTO `audit_user_login_log` VALUES (2038148022999318529, 5, 'intern_admin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-29 14:55:30');
INSERT INTO `audit_user_login_log` VALUES (2038148086350086146, 5, 'intern_admin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-29 14:55:45');
INSERT INTO `audit_user_login_log` VALUES (2038148950217326594, 5, 'intern_admin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-29 14:59:11');
INSERT INTO `audit_user_login_log` VALUES (2038157675716018177, 1, 'superAdmin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-29 15:33:52');
INSERT INTO `audit_user_login_log` VALUES (2038158075257028610, 1, 'superAdmin', 'password', '127.0.0.1', 'unknown', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0', 1, '登录成功', '2026-03-29 15:35:27');

-- ----------------------------
-- Table structure for intern_application
-- ----------------------------
DROP TABLE IF EXISTS `intern_application`;
CREATE TABLE `intern_application`  (
  `id` bigint NOT NULL COMMENT '主键',
  `batch_id` bigint NOT NULL COMMENT '批次ID（冗余便于查询）',
  `position_id` bigint NOT NULL COMMENT '岗位ID',
  `student_user_id` bigint NOT NULL COMMENT '学生用户ID',
  `school_mentor_user_id` bigint NULL DEFAULT NULL COMMENT '校内指导教师用户ID',
  `enterprise_mentor_user_id` bigint NULL DEFAULT NULL COMMENT '企业导师用户ID（可选）',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING-待审核，APPROVED-已通过，REJECTED-已拒绝，CANCELLED-学生撤销，IN_PROGRESS-进行中，COMPLETED-已完成',
  `audit_remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '审核说明',
  `audit_by` bigint NULL DEFAULT NULL,
  `audit_time` datetime NULL DEFAULT NULL,
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `deleted` tinyint NOT NULL DEFAULT 0,
  `create_by` bigint NULL DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_by` bigint NULL DEFAULT NULL,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_student_position`(`student_user_id` ASC, `position_id` ASC) USING BTREE,
  INDEX `idx_batch_id`(`batch_id` ASC) USING BTREE,
  INDEX `idx_position_id`(`position_id` ASC) USING BTREE,
  INDEX `idx_student`(`student_user_id` ASC) USING BTREE,
  INDEX `idx_mentor`(`school_mentor_user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '实习报名与申请' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of intern_application
-- ----------------------------
INSERT INTO `intern_application` VALUES (1900000000000000010, 1900000000000000003, 1900000000000000004, 1900000000000000099, 7, NULL, 'COMPLETED', NULL, 1, '2026-03-28 13:08:55', '演示报名记录', 0, 1900000000000000099, '2026-03-28 13:08:55', 1, '2026-03-28 13:08:55');
INSERT INTO `intern_application` VALUES (2038138154284445698, 1900000000000000003, 1900000000000000004, 8, 7, NULL, 'COMPLETED', NULL, 5, '2026-03-29 14:18:39', NULL, 0, 8, '2026-03-29 14:16:17', 8, '2026-03-29 14:16:17');
INSERT INTO `intern_application` VALUES (2038138180305907713, 1900000000000000003, 1900000000000000005, 8, 7, NULL, 'COMPLETED', NULL, 5, '2026-03-29 14:18:37', NULL, 0, 8, '2026-03-29 14:16:24', 8, '2026-03-29 14:16:24');

-- ----------------------------
-- Table structure for intern_batch
-- ----------------------------
DROP TABLE IF EXISTS `intern_batch`;
CREATE TABLE `intern_batch`  (
  `id` bigint NOT NULL COMMENT '主键',
  `batch_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '批次名称',
  `school_year` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '学年，如 2025-2026',
  `term` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '学期：1-春季，2-秋季 或自定义',
  `sign_up_start` datetime NULL DEFAULT NULL COMMENT '报名开始时间',
  `sign_up_end` datetime NULL DEFAULT NULL COMMENT '报名结束时间',
  `active` tinyint NOT NULL DEFAULT 1 COMMENT '是否当前可用：0-否，1-是',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_by` bigint NULL DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_by` bigint NULL DEFAULT NULL,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sign_up`(`sign_up_start` ASC, `sign_up_end` ASC) USING BTREE,
  INDEX `idx_deleted`(`deleted` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '实习批次' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of intern_batch
-- ----------------------------
INSERT INTO `intern_batch` VALUES (1900000000000000003, '2025年春季实习批次', '2024-2025', '1', '2025-03-01 00:00:00', '2027-06-30 23:59:59', 1, '演示批次', 0, 1, '2026-03-28 13:08:55', 1, '2026-03-28 13:08:55');
INSERT INTO `intern_batch` VALUES (2038082159914995714, '测试', '2025-2026', '春季', '2026-03-29 00:00:00', '2026-03-30 00:00:00', 1, '123', 1, 1, '2026-03-29 10:33:47', 5, '2026-03-29 14:13:19');

-- ----------------------------
-- Table structure for intern_enterprise
-- ----------------------------
DROP TABLE IF EXISTS `intern_enterprise`;
CREATE TABLE `intern_enterprise`  (
  `id` bigint NOT NULL COMMENT '主键',
  `enterprise_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '企业名称',
  `credit_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '统一社会信用代码',
  `contact_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '联系电话',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '地址',
  `intro` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '简介',
  `audit_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT 'PENDING' COMMENT '审核状态：PENDING-待审核，APPROVED-已通过，REJECTED-已拒绝',
  `audit_remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '审核备注',
  `audit_by` bigint NULL DEFAULT NULL COMMENT '审核人用户ID',
  `audit_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '启用：0-停用，1-正常',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-否，1-是',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_audit_status`(`audit_status` ASC) USING BTREE,
  INDEX `idx_deleted`(`deleted` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '实习合作企业' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of intern_enterprise
-- ----------------------------
INSERT INTO `intern_enterprise` VALUES (1900000000000000001, '示例科技有限责任公司', '91110000MA00000001', '张经理', '13800000001', '北京市海淀区中关村大街1号', '从事软件开发与技术服务，长期接收高校实习。', 'APPROVED', NULL, 5, '2026-03-29 14:13:26', 1, '演示：待审核企业', 0, 1, '2026-03-28 13:08:55', 1, '2026-03-28 13:08:55');
INSERT INTO `intern_enterprise` VALUES (1900000000000000002, '智慧教育科技股份有限公司', '91110000MA00000002', '李老师', '13800000002', '上海市浦东新区张江路100号', '教育信息化与在线教学平台研发。', 'APPROVED', '资质齐全', 1, '2026-03-28 17:23:18', 1, '演示：已通过企业', 0, 1, '2026-03-28 13:08:55', 1, '2026-03-28 13:08:55');
INSERT INTO `intern_enterprise` VALUES (2038080640524177409, '测试', '123', '123', '123', '123', '123', 'APPROVED', '123', 1, '2026-03-29 10:27:50', 1, '123', 1, 1, '2026-03-29 10:27:45', 5, '2026-03-29 14:13:23');

-- ----------------------------
-- Table structure for intern_evaluation
-- ----------------------------
DROP TABLE IF EXISTS `intern_evaluation`;
CREATE TABLE `intern_evaluation`  (
  `id` bigint NOT NULL COMMENT '主键',
  `application_id` bigint NOT NULL COMMENT '实习申请ID',
  `school_score` decimal(5, 2) NULL DEFAULT NULL COMMENT '校内评分',
  `school_comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '校内鉴定/评语',
  `school_by` bigint NULL DEFAULT NULL,
  `school_time` datetime NULL DEFAULT NULL,
  `enterprise_score` decimal(5, 2) NULL DEFAULT NULL COMMENT '企业评分',
  `enterprise_comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '企业评语',
  `enterprise_by` bigint NULL DEFAULT NULL,
  `enterprise_time` datetime NULL DEFAULT NULL,
  `final_score` decimal(5, 2) NULL DEFAULT NULL COMMENT '综合成绩',
  `final_remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '综合说明',
  `deleted` tinyint NOT NULL DEFAULT 0,
  `create_by` bigint NULL DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_by` bigint NULL DEFAULT NULL,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_application`(`application_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '实习评价与成绩' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of intern_evaluation
-- ----------------------------
INSERT INTO `intern_evaluation` VALUES (1900000000000000022, 1900000000000000010, 85.00, '学习态度认真，能独立完成分配任务。', 1900000000000000088, '2026-03-28 13:08:55', NULL, NULL, NULL, NULL, NULL, NULL, 0, 1, '2026-03-28 13:08:55', 1, '2026-03-28 13:08:55');

-- ----------------------------
-- Table structure for intern_material
-- ----------------------------
DROP TABLE IF EXISTS `intern_material`;
CREATE TABLE `intern_material`  (
  `id` bigint NOT NULL COMMENT '主键',
  `application_id` bigint NOT NULL COMMENT '实习申请ID',
  `material_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'AGREEMENT-实习协议，SAFETY-安全告知，OTHER-其他',
  `material_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '材料名称（展示用，可与类型默认文案一致）',
  `attachment_id` bigint NOT NULL COMMENT '平台附件表主键',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT 'SUBMITTED' COMMENT 'SUBMITTED-已提交，APPROVED-已通过，REJECTED-已退回',
  `audit_remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '审核说明',
  `audit_by` bigint NULL DEFAULT NULL,
  `audit_time` datetime NULL DEFAULT NULL,
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `deleted` tinyint NOT NULL DEFAULT 0,
  `create_by` bigint NULL DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_by` bigint NULL DEFAULT NULL,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_application_id`(`application_id` ASC) USING BTREE,
  INDEX `idx_material_type`(`material_type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_deleted`(`deleted` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '实习材料' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of intern_material
-- ----------------------------
INSERT INTO `intern_material` VALUES (1900000000000000021, 1900000000000000010, 'AGREEMENT', '实习协议', 1900000000000000071, 'APPROVED', NULL, 1, '2026-03-28 17:25:59', '演示：请替换 attachment_id 为真实附件ID', 0, 1900000000000000099, '2026-03-28 13:08:55', 1900000000000000099, '2026-03-28 13:08:55');

-- ----------------------------
-- Table structure for intern_position
-- ----------------------------
DROP TABLE IF EXISTS `intern_position`;
CREATE TABLE `intern_position`  (
  `id` bigint NOT NULL COMMENT '主键',
  `batch_id` bigint NOT NULL COMMENT '批次ID',
  `enterprise_id` bigint NOT NULL COMMENT '企业ID',
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '岗位名称',
  `quota` int NOT NULL DEFAULT 1 COMMENT '计划人数',
  `requirement` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '岗位要求',
  `start_date` date NULL DEFAULT NULL COMMENT '实习开始日期',
  `end_date` date NULL DEFAULT NULL COMMENT '实习结束日期',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT 'DRAFT' COMMENT 'DRAFT-草稿，PUBLISHED-已发布，CLOSED-已结束',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `deleted` tinyint NOT NULL DEFAULT 0,
  `create_by` bigint NULL DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_by` bigint NULL DEFAULT NULL,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_batch_id`(`batch_id` ASC) USING BTREE,
  INDEX `idx_enterprise_id`(`enterprise_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_deleted`(`deleted` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '实习岗位' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of intern_position
-- ----------------------------
INSERT INTO `intern_position` VALUES (1900000000000000004, 1900000000000000003, 1900000000000000002, 'Java 后端开发实习', 5, '熟悉 Java、Spring Boot；了解 MySQL、Redis；有微服务项目经验优先。', '2025-04-01', '2027-06-30', 'PUBLISHED', '演示：已发布岗位', 0, 1, '2026-03-28 13:08:55', 1, '2026-03-29 14:13:10');
INSERT INTO `intern_position` VALUES (1900000000000000005, 1900000000000000003, 1900000000000000002, '前端开发实习（草稿）', 3, '熟悉 Vue3、TypeScript。', '2025-04-01', '2027-06-30', 'PUBLISHED', '演示：未发布', 0, 1, '2026-03-28 13:08:55', 1, '2026-03-29 14:13:10');

-- ----------------------------
-- Table structure for intern_weekly_log
-- ----------------------------
DROP TABLE IF EXISTS `intern_weekly_log`;
CREATE TABLE `intern_weekly_log`  (
  `id` bigint NOT NULL COMMENT '主键',
  `application_id` bigint NOT NULL COMMENT '实习申请ID',
  `week_index` int NOT NULL COMMENT '第几周（从1开始）',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '周志标题',
  `content` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '周志正文（富文本）',
  `attachment_ids` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '附件ID列表，JSON 数组字符串',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT 'SUBMITTED' COMMENT 'SUBMITTED-已提交，APPROVED-已通过，REJECTED-退回',
  `review_comment` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '教师批阅意见',
  `review_by` bigint NULL DEFAULT NULL,
  `review_time` datetime NULL DEFAULT NULL,
  `deleted` tinyint NOT NULL DEFAULT 0,
  `create_by` bigint NULL DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_by` bigint NULL DEFAULT NULL,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_application_id`(`application_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '实习周志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of intern_weekly_log
-- ----------------------------
INSERT INTO `intern_weekly_log` VALUES (1900000000000000020, 1900000000000000010, 1, '第1周实习周志', '<p>本周熟悉项目结构与开发规范，完成环境搭建。</p>', NULL, 'APPROVED', '内容符合要求', 1900000000000000088, '2026-03-28 13:08:55', 0, 1900000000000000099, '2026-03-28 13:08:55', 1900000000000000088, '2026-03-28 13:08:55');
INSERT INTO `intern_weekly_log` VALUES (2038138887180349442, 2038138180305907713, 1, '测试', '123', NULL, 'APPROVED', NULL, 7, '2026-03-29 14:42:17', 0, 8, '2026-03-29 14:19:12', 8, '2026-03-29 14:19:12');
INSERT INTO `intern_weekly_log` VALUES (2038138925562425345, 2038138180305907713, 2, '测试2', '123', NULL, 'APPROVED', NULL, 7, '2026-03-29 14:42:15', 0, 8, '2026-03-29 14:19:21', 8, '2026-03-29 14:19:21');

-- ----------------------------
-- Table structure for job_def
-- ----------------------------
DROP TABLE IF EXISTS `job_def`;
CREATE TABLE `job_def`  (
  `id` bigint NOT NULL COMMENT '任务定义ID',
  `service_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '所属服务',
  `job_code` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '任务编码',
  `job_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '任务名称',
  `job_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '任务类型',
  `cron_expr` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'Cron表达式',
  `next_run_time` datetime NULL DEFAULT NULL COMMENT '下一次触发时间',
  `run_mode` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT 'single' COMMENT '运行模式：single-集群单实例执行，all-全实例执行',
  `enabled` tinyint NOT NULL DEFAULT 1 COMMENT '是否启用：0-禁用，1-启用',
  `timeout_ms` int NOT NULL DEFAULT 0 COMMENT '超时毫秒（0表示不超时）',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
  `params_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '任务参数JSON',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_service_job_code`(`service_name` ASC, `job_code` ASC) USING BTREE,
  INDEX `idx_service_enabled`(`service_name` ASC, `enabled` ASC) USING BTREE,
  INDEX `idx_next_run_time`(`next_run_time` ASC) USING BTREE,
  INDEX `idx_deleted`(`deleted` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '定时任务定义表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of job_def
-- ----------------------------
INSERT INTO `job_def` VALUES (1, 'base', 'ATTACHMENT_CLEAN', '附件清理任务', 'ATTACHMENT_CLEAN', '0 0 2 * * ?', NULL, 'single', 1, 0, '定期清理已逻辑删除的附件，物理删除文件和记录', '{\"batchSize\": 100, \"deletedDays\": 30, \"deletePhysicalFile\": true, \"storageType\": \"local\", \"businessType\": null, \"fileType\": null, \"maxFileSize\": 10485760, \"minFileSize\": 1024, \"pathPattern\": null, \"retryCount\": 2, \"continueOnError\": true, \"orderBy\": \"id\"}', 0, 1, '2026-03-28 12:59:43', 1, '2026-03-28 12:59:43');

-- ----------------------------
-- Table structure for job_lock
-- ----------------------------
DROP TABLE IF EXISTS `job_lock`;
CREATE TABLE `job_lock`  (
  `id` bigint NOT NULL COMMENT '锁ID',
  `job_id` bigint NOT NULL COMMENT '任务定义ID',
  `instance_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '持有锁的实例ID',
  `lock_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '锁定时间',
  `expire_time` datetime NOT NULL COMMENT '锁过期时间',
  `heartbeat_time` datetime NULL DEFAULT NULL COMMENT '心跳时间（用于续期）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_job_id`(`job_id` ASC) USING BTREE,
  INDEX `idx_expire_time`(`expire_time` ASC) USING BTREE,
  INDEX `idx_instance_id`(`instance_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '任务执行锁表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of job_lock
-- ----------------------------

-- ----------------------------
-- Table structure for job_run_log
-- ----------------------------
DROP TABLE IF EXISTS `job_run_log`;
CREATE TABLE `job_run_log`  (
  `id` bigint NOT NULL COMMENT '执行记录ID',
  `job_id` bigint NOT NULL COMMENT '任务定义ID',
  `job_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '任务名称（冗余）',
  `run_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '本次执行唯一ID',
  `status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '状态：running/success/fail/timeout/skip',
  `start_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `duration_ms` bigint NULL DEFAULT NULL COMMENT '耗时毫秒',
  `instance_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '执行实例ID',
  `host` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '执行机器host/IP',
  `error_message` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '错误摘要',
  `error_stack` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '错误堆栈',
  `result_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '结果/统计JSON',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_run_id`(`run_id` ASC) USING BTREE,
  INDEX `idx_job_start_time`(`job_id` ASC, `start_time` ASC) USING BTREE,
  INDEX `idx_status_start_time`(`status` ASC, `start_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '定时任务执行记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of job_run_log
-- ----------------------------

-- ----------------------------
-- Table structure for security_online_user
-- ----------------------------
DROP TABLE IF EXISTS `security_online_user`;
CREATE TABLE `security_online_user`  (
  `id` bigint NOT NULL COMMENT '记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '用户名',
  `token_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '会话Token标识',
  `login_ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '登录IP',
  `login_location` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '登录地点',
  `user_agent` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '用户代理',
  `device_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '设备类型：pc/mobile/tablet等',
  `browser` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '浏览器',
  `os` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '操作系统',
  `login_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
  `last_active_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后活跃时间',
  `offline_time` datetime NULL DEFAULT NULL COMMENT '下线时间',
  `online_status` tinyint NOT NULL DEFAULT 1 COMMENT '在线状态：0-离线，1-在线',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_token_id`(`token_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_username`(`username` ASC) USING BTREE,
  INDEX `idx_online_status`(`online_status` ASC) USING BTREE,
  INDEX `idx_last_active_time`(`last_active_time` ASC) USING BTREE,
  INDEX `idx_user_status_active`(`user_id` ASC, `online_status` ASC, `last_active_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '在线用户会话表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of security_online_user
-- ----------------------------

-- ----------------------------
-- Table structure for system_attachment
-- ----------------------------
DROP TABLE IF EXISTS `system_attachment`;
CREATE TABLE `system_attachment`  (
  `id` bigint NOT NULL COMMENT '附件ID',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '文件名（存储文件名）',
  `original_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '原始文件名',
  `file_path` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '文件存储路径',
  `file_url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '文件访问URL',
  `file_size` bigint NOT NULL COMMENT '文件大小（字节）',
  `file_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '文件类型（扩展名）',
  `mime_type` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'MIME类型',
  `storage_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT 'local' COMMENT '存储类型：local-本地，oss-对象存储',
  `business_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '业务类型（用于区分不同业务场景）',
  `business_id` bigint NULL DEFAULT NULL COMMENT '关联业务ID',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_file_type`(`file_type` ASC) USING BTREE,
  INDEX `idx_business`(`business_type` ASC, `business_id` ASC) USING BTREE,
  INDEX `idx_create_by`(`create_by` ASC) USING BTREE,
  INDEX `idx_deleted`(`deleted` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_status_deleted`(`status` ASC, `deleted` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '附件表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_attachment
-- ----------------------------

-- ----------------------------
-- Table structure for system_config
-- ----------------------------
DROP TABLE IF EXISTS `system_config`;
CREATE TABLE `system_config`  (
  `id` bigint NOT NULL COMMENT '配置ID',
  `config_key` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '配置键（唯一标识）',
  `config_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '配置值',
  `config_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT 'string' COMMENT '配置类型：string-字符串，number-数字，boolean-布尔值，json-JSON对象',
  `config_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '配置名称/描述',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `editable` tinyint NOT NULL DEFAULT 1 COMMENT '是否可编辑：0-否（系统配置），1-是（用户配置）',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_config_key`(`config_key` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_deleted`(`deleted` ASC) USING BTREE,
  INDEX `idx_config_type`(`config_type` ASC) USING BTREE,
  INDEX `idx_status_deleted`(`status` ASC, `deleted` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '系统配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_config
-- ----------------------------
INSERT INTO `system_config` VALUES (1, 'system.name', '基于微服务架构的高校在线实习管理平台', 'string', '系统名称', 1, 1, '系统名称配置', 0, NULL, '2026-03-28 12:59:42', 1, '2026-03-28 12:59:42');
INSERT INTO `system_config` VALUES (2, 'system.version', '1.0.0', 'string', '系统版本', 1, 1, '系统版本号', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_config` VALUES (3, 'system.copyright', '© 2025 MyManagementSystem', 'string', '版权信息', 1, 1, '系统版权信息', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');

-- ----------------------------
-- Table structure for system_dept
-- ----------------------------
DROP TABLE IF EXISTS `system_dept`;
CREATE TABLE `system_dept`  (
  `id` bigint NOT NULL COMMENT '部门ID',
  `parent_id` bigint NOT NULL DEFAULT 0 COMMENT '父部门ID，0表示顶级部门',
  `dept_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '部门名称',
  `dept_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '部门编码',
  `leader` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '负责人',
  `phone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '联系电话',
  `email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '邮箱',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序号',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_dept_code`(`dept_code` ASC) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_deleted`(`deleted` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '部门表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_dept
-- ----------------------------
INSERT INTO `system_dept` VALUES (1, 0, '董事会', 'ROOT', '李鸿羽', '18255097030', '2722562862@qq.com', 1, 1, '根部门', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dept` VALUES (2, 1, '技术中心', 'TECH_CENTER', '', '', '', 10, 1, '研发与技术体系', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dept` VALUES (3, 1, '产品中心', 'PRODUCT_CENTER', '', '', '', 20, 1, '产品规划与需求管理', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dept` VALUES (4, 1, '市场部', 'MARKETING', '', '', '', 30, 1, '市场推广与品牌建设', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dept` VALUES (5, 1, '销售部', 'SALES', '', '', '', 40, 1, '销售与客户拓展', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dept` VALUES (6, 1, '运营部', 'OPERATIONS', '', '', '', 50, 1, '用户/业务运营与活动执行', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dept` VALUES (7, 1, '财务部', 'FINANCE', '', '', '', 60, 1, '财务核算与资金管理', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dept` VALUES (8, 1, '人力行政部', 'HR_ADMIN', '', '', '', 70, 1, '人力资源与行政支持', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dept` VALUES (9, 1, '总经办', 'GM_OFFICE', '', '', '', 80, 1, '经营管理与跨部门协调', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dept` VALUES (10, 2, '架构组', 'ARCH_TEAM', '', '', '', 101, 1, '架构设计与技术选型', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dept` VALUES (11, 2, '后端组', 'BE_TEAM', '', '', '', 102, 1, '后端研发', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dept` VALUES (12, 2, '前端组', 'FE_TEAM', '', '', '', 103, 1, '前端研发', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dept` VALUES (13, 2, '移动端组', 'MOBILE_TEAM', '', '', '', 104, 1, 'iOS/Android 研发', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dept` VALUES (14, 2, '测试质量部', 'QA', '', '', '', 105, 1, '测试与质量保障', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dept` VALUES (15, 2, '运维安全部', 'DEVOPS_SEC', '', '', '', 106, 1, '运维与安全', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dept` VALUES (16, 2, '数据平台组', 'DATA_PLATFORM', '', '', '', 107, 1, '数据平台与数据分析支撑', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dept` VALUES (17, 3, '产品一部', 'PRODUCT_1', '', '', '', 201, 1, '核心产品线', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dept` VALUES (18, 3, '产品二部', 'PRODUCT_2', '', '', '', 202, 1, '增长/创新产品线', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dept` VALUES (19, 3, '设计部', 'DESIGN', '', '', '', 203, 1, 'UI/UX 设计', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');

-- ----------------------------
-- Table structure for system_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `system_dict_data`;
CREATE TABLE `system_dict_data`  (
  `id` bigint NOT NULL COMMENT '字典数据ID',
  `dict_type_id` bigint NOT NULL COMMENT '字典类型ID',
  `dict_label` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '字典标签（显示文本）',
  `dict_value` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '字典值（实际值）',
  `dict_sort` int NOT NULL DEFAULT 0 COMMENT '排序号',
  `is_default` tinyint NOT NULL DEFAULT 0 COMMENT '是否默认值：0-否，1-是',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_dict_type_id`(`dict_type_id` ASC) USING BTREE,
  INDEX `idx_dict_value`(`dict_value` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_deleted`(`deleted` ASC) USING BTREE,
  INDEX `idx_dict_type_status_deleted`(`dict_type_id` ASC, `status` ASC, `deleted` ASC) USING BTREE,
  CONSTRAINT `fk_dict_data_type` FOREIGN KEY (`dict_type_id`) REFERENCES `system_dict_type` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '数据字典数据表（字典键值对）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_dict_data
-- ----------------------------
INSERT INTO `system_dict_data` VALUES (1, 1, '启用', '1', 1, 1, 1, '启用状态', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (2, 1, '禁用', '0', 2, 0, 1, '禁用状态', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (3, 2, '是', '1', 1, 1, 1, '是 / true', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (4, 2, '否', '0', 2, 0, 1, '否 / false', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (5, 3, '显示', '1', 1, 1, 1, '菜单在前端展示', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (6, 3, '隐藏', '0', 2, 0, 1, '菜单在前端隐藏', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (7, 4, '字符串', 'string', 1, 1, 1, '字符串类型配置', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (8, 4, '数字', 'number', 2, 0, 1, '数字类型配置', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (9, 4, '布尔', 'boolean', 3, 0, 1, '布尔类型配置', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (10, 4, 'JSON', 'json', 4, 0, 1, 'JSON 类型配置', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (11, 5, '未锁定', '0', 1, 1, 1, '用户正常', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (12, 5, '已锁定', '1', 2, 0, 1, '用户被锁定', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (13, 6, '未知', '0', 1, 1, 1, '性别未知', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (14, 6, '男', '1', 2, 0, 1, '男性', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (15, 6, '女', '2', 3, 0, 1, '女性', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (16, 7, '系统角色', 'system', 1, 0, 1, '系统内置角色，不可删除', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (17, 7, '自定义角色', 'custom', 2, 1, 1, '用户自定义角色', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (18, 8, '用户头像', 'user_avatar', 1, 0, 1, '业务ID为用户ID', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (19, 8, '系统附件', 'system_attachment', 2, 0, 1, '系统附件管理-资源库上传', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (20, 9, 'jpg', 'jpg', 1, 0, 1, 'jpg', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (21, 9, 'jpeg', 'jpeg', 2, 0, 1, 'jpeg', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (22, 9, 'png', 'png', 3, 0, 1, 'png', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (23, 9, 'gif', 'gif', 4, 0, 1, 'gif', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (24, 9, 'bmp', 'bmp', 5, 0, 1, 'bmp', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (25, 9, 'webp', 'webp', 6, 0, 1, 'webp', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (26, 9, 'doc', 'doc', 7, 0, 1, 'doc', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (27, 9, 'docx', 'docx', 8, 0, 1, 'docx', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (28, 9, 'xls', 'xls', 9, 0, 1, 'xls', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (29, 9, 'xlsx', 'xlsx', 10, 0, 1, 'xlsx', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (30, 9, 'ppt', 'ppt', 11, 0, 1, 'ppt', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (31, 9, 'pptx', 'pptx', 12, 0, 1, 'pptx', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (32, 9, 'pdf', 'pdf', 13, 0, 1, 'pdf', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (33, 9, 'txt', 'txt', 14, 0, 1, 'txt', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (34, 9, 'md', 'md', 15, 0, 1, 'md', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (35, 10, '目录', 'catalog', 1, 0, 1, '目录权限', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (36, 10, '菜单', 'menu', 2, 0, 1, '菜单权限', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (37, 10, '按钮', 'button', 3, 0, 1, '按钮权限', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (38, 10, '接口', 'api', 4, 0, 1, '接口权限', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (39, 11, '基础数据服务', 'base', 1, 0, 1, '基础数据服务（base）', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (40, 11, '用户中心服务', 'usercenter', 1, 0, 1, '用户中心服务（usercenter）', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (41, 12, '附件清理任务', 'ATTACHMENT_CLEAN', 1, 0, 1, '附件清理任务', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (42, 13, '集群单实例执行', 'single', 1, 1, 1, '集群单实例执行', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (43, 13, '全实例执行', 'all', 2, 0, 1, '全实例执行', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (44, 14, '运行中', 'running', 1, 0, 1, '运行中', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (45, 14, '成功', 'success', 2, 0, 1, '成功', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (46, 14, '失败', 'fail', 3, 0, 1, '失败', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (47, 14, '超时', 'timeout', 4, 0, 1, '超时\'', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (48, 14, '跳过', 'skip', 5, 0, 1, '跳过', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (49, 15, '失败', '0', 1, 0, 1, '登录失败', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (50, 15, '成功', '1', 2, 1, 1, '登录成功', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (51, 16, '密码登录', 'password', 1, 1, 1, '通过账号密码登录', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (52, 16, '短信登录', 'sms', 2, 0, 1, '通过短信验证码登录', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (53, 16, '邮箱登录', 'email', 3, 0, 1, '通过邮箱验证码登录', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_data` VALUES (100, 17, '待审核', 'PENDING', 1, 1, 1, NULL, 0, NULL, '2026-03-28 17:29:25', NULL, '2026-03-28 17:29:25');
INSERT INTO `system_dict_data` VALUES (101, 17, '已通过', 'APPROVED', 2, 0, 1, NULL, 0, NULL, '2026-03-28 17:29:25', NULL, '2026-03-28 17:29:25');
INSERT INTO `system_dict_data` VALUES (102, 17, '已拒绝', 'REJECTED', 3, 0, 1, NULL, 0, NULL, '2026-03-28 17:29:25', NULL, '2026-03-28 17:29:25');
INSERT INTO `system_dict_data` VALUES (103, 18, '草稿', 'DRAFT', 1, 1, 1, NULL, 0, NULL, '2026-03-28 17:29:25', NULL, '2026-03-28 17:29:25');
INSERT INTO `system_dict_data` VALUES (104, 18, '已发布', 'PUBLISHED', 2, 0, 1, NULL, 0, NULL, '2026-03-28 17:29:25', NULL, '2026-03-28 17:29:25');
INSERT INTO `system_dict_data` VALUES (105, 18, '已结束', 'CLOSED', 3, 0, 1, NULL, 0, NULL, '2026-03-28 17:29:25', NULL, '2026-03-28 17:29:25');
INSERT INTO `system_dict_data` VALUES (106, 19, '待审核', 'PENDING', 1, 1, 1, NULL, 0, NULL, '2026-03-28 17:29:25', NULL, '2026-03-28 17:29:25');
INSERT INTO `system_dict_data` VALUES (107, 19, '已通过', 'APPROVED', 2, 0, 1, NULL, 0, NULL, '2026-03-28 17:29:25', NULL, '2026-03-28 17:29:25');
INSERT INTO `system_dict_data` VALUES (108, 19, '已拒绝', 'REJECTED', 3, 0, 1, NULL, 0, NULL, '2026-03-28 17:29:25', NULL, '2026-03-28 17:29:25');
INSERT INTO `system_dict_data` VALUES (109, 19, '已取消', 'CANCELLED', 4, 0, 1, NULL, 0, NULL, '2026-03-28 17:29:25', NULL, '2026-03-28 17:29:25');
INSERT INTO `system_dict_data` VALUES (110, 19, '实习中', 'IN_PROGRESS', 5, 0, 1, NULL, 0, NULL, '2026-03-28 17:29:25', NULL, '2026-03-28 17:29:25');
INSERT INTO `system_dict_data` VALUES (111, 19, '已完成', 'COMPLETED', 6, 0, 1, NULL, 0, NULL, '2026-03-28 17:29:25', NULL, '2026-03-28 17:29:25');
INSERT INTO `system_dict_data` VALUES (112, 20, '已提交', 'SUBMITTED', 1, 1, 1, NULL, 0, NULL, '2026-03-28 17:29:25', NULL, '2026-03-28 17:29:25');
INSERT INTO `system_dict_data` VALUES (113, 20, '已通过', 'APPROVED', 2, 0, 1, NULL, 0, NULL, '2026-03-28 17:29:25', NULL, '2026-03-28 17:29:25');
INSERT INTO `system_dict_data` VALUES (114, 20, '退回', 'REJECTED', 3, 0, 1, NULL, 0, NULL, '2026-03-28 17:29:25', NULL, '2026-03-28 17:29:25');
INSERT INTO `system_dict_data` VALUES (115, 21, '三方协议', 'AGREEMENT', 1, 1, 1, NULL, 0, NULL, '2026-03-28 17:29:25', NULL, '2026-03-28 17:29:25');
INSERT INTO `system_dict_data` VALUES (116, 21, '安全告知', 'SAFETY', 2, 0, 1, NULL, 0, NULL, '2026-03-28 17:29:25', NULL, '2026-03-28 17:29:25');
INSERT INTO `system_dict_data` VALUES (117, 21, '其他', 'OTHER', 3, 0, 1, NULL, 0, NULL, '2026-03-28 17:29:25', NULL, '2026-03-28 17:29:25');

-- ----------------------------
-- Table structure for system_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `system_dict_type`;
CREATE TABLE `system_dict_type`  (
  `id` bigint NOT NULL COMMENT '字典类型ID',
  `dict_type_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '字典类型编码（唯一标识）',
  `dict_type_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '字典类型名称',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序号',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_dict_type_code`(`dict_type_code` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_deleted`(`deleted` ASC) USING BTREE,
  INDEX `idx_status_deleted`(`status` ASC, `deleted` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '数据字典类型表（字典分类）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_dict_type
-- ----------------------------
INSERT INTO `system_dict_type` VALUES (1, 'common_status', '通用状态', 1, 1, '通用的启用/禁用状态', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_type` VALUES (2, 'yes_no', '是否', 1, 2, '通用布尔值（是/否）', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_type` VALUES (3, 'menu_visible', '菜单显示状态', 1, 3, '菜单/权限显示状态', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_type` VALUES (4, 'config_type', '参数配置类型', 1, 4, 'system config 中的类型', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_type` VALUES (5, 'user_lock_status', '用户锁定状态', 1, 10, '用户是否被锁定', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_type` VALUES (6, 'user_gender', '用户性别', 1, 11, '用户性别选项', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_type` VALUES (7, 'role_type', '角色类型', 1, 12, '角色类型分类', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_type` VALUES (8, 'attachment_business_type', '附件业务类型', 1, 14, '附件业务类型', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_type` VALUES (9, 'attachment_file_type', '附件文件类型', 1, 15, '扩展名', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_type` VALUES (10, 'permission_type', '权限类型', 1, 16, '权限类型分类', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_type` VALUES (11, 'service_name', '服务名', 1, 17, '服务名', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_type` VALUES (12, 'job_type', '定时任务类型', 1, 18, '定时任务类型', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_type` VALUES (13, 'Job_run_mode', '定时任务运行模式', 1, 19, '定时任务运行模式', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_type` VALUES (14, 'job_status', '定时任务状态', 1, 20, '定时任务状态', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_type` VALUES (15, 'login_status', '登录状态', 1, 13, '用户登录操作的结果状态', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_type` VALUES (16, 'login_type', '登录类型', 1, 13, '用户登录的方式或类型', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_dict_type` VALUES (17, 'intern_enterprise_audit_status', '实习-企业审核状态', 1, 100, '合作企业入驻审核', 0, NULL, '2026-03-28 17:29:25', NULL, '2026-03-28 17:29:25');
INSERT INTO `system_dict_type` VALUES (18, 'intern_position_status', '实习-岗位状态', 1, 101, '岗位草稿/发布/结束', 0, NULL, '2026-03-28 17:29:25', NULL, '2026-03-28 17:29:25');
INSERT INTO `system_dict_type` VALUES (19, 'intern_application_status', '实习-申请流程状态', 1, 102, '学生报名与实习流程', 0, NULL, '2026-03-28 17:29:25', NULL, '2026-03-28 17:29:25');
INSERT INTO `system_dict_type` VALUES (20, 'intern_submission_review_status', '实习-提交审阅状态', 1, 103, '周志/材料等提交后审核', 0, NULL, '2026-03-28 17:29:25', NULL, '2026-03-28 17:29:25');
INSERT INTO `system_dict_type` VALUES (21, 'intern_material_type', '实习-材料类型', 1, 104, '实习材料分类', 0, NULL, '2026-03-28 17:29:25', NULL, '2026-03-28 17:29:25');

-- ----------------------------
-- Table structure for system_permission
-- ----------------------------
DROP TABLE IF EXISTS `system_permission`;
CREATE TABLE `system_permission`  (
  `id` bigint NOT NULL COMMENT '权限ID',
  `parent_id` bigint NOT NULL DEFAULT 0 COMMENT '父权限ID，0表示顶级权限',
  `permission_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '权限类型：catalog-目录，menu-菜单，button-按钮，api-接口',
  `permission_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '权限名称',
  `permission_code` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '权限编码（唯一标识）',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '路由路径（菜单类型使用）',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '组件路径（菜单类型使用）',
  `icon` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '图标（菜单类型使用）',
  `api_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '接口URL（接口类型使用）',
  `api_method` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '接口请求方式：GET,POST,PUT,DELETE等（接口类型使用）',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序号',
  `visible` tinyint NOT NULL DEFAULT 1 COMMENT '是否显示：0-隐藏，1-显示',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_permission_code`(`permission_code` ASC) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE,
  INDEX `idx_permission_type`(`permission_type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_deleted`(`deleted` ASC) USING BTREE,
  INDEX `idx_status_deleted_type`(`status` ASC, `deleted` ASC, `permission_type` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_permission
-- ----------------------------
INSERT INTO `system_permission` VALUES (1, 0, 'catalog', '系统管理', 'SYSTEM', NULL, NULL, 'Setting', NULL, NULL, 1, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (2, 1, 'menu', '用户管理', 'SYSTEM_USER', '/system/userPage', '/system/user/UserPage.vue', 'User', NULL, NULL, 10, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (3, 2, 'button', '用户-查看', 'SYSTEM_USER_VIEW', NULL, NULL, NULL, NULL, NULL, 11, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (4, 2, 'button', '用户-新增', 'SYSTEM_USER_CREATE', NULL, NULL, NULL, NULL, NULL, 12, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (5, 2, 'button', '用户-编辑', 'SYSTEM_USER_UPDATE', NULL, NULL, NULL, NULL, NULL, 13, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (6, 2, 'button', '用户-删除', 'SYSTEM_USER_DELETE', NULL, NULL, NULL, NULL, NULL, 14, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (7, 2, 'button', '用户-重置密码', 'SYSTEM_USER_RESET_PASSWORD', NULL, NULL, NULL, NULL, NULL, 15, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (8, 2, 'button', '用户-解锁', 'SYSTEM_USER_UNLOCK', NULL, NULL, NULL, NULL, NULL, 16, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (9, 1, 'menu', '角色管理', 'SYSTEM_ROLE', '/system/rolePage', '/system/role/RolePage.vue', 'Key', NULL, NULL, 20, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (10, 9, 'button', '角色-查看', 'SYSTEM_ROLE_VIEW', NULL, NULL, NULL, NULL, NULL, 21, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (11, 9, 'button', '角色-新增', 'SYSTEM_ROLE_CREATE', NULL, NULL, NULL, NULL, NULL, 22, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (12, 9, 'button', '角色-编辑', 'SYSTEM_ROLE_UPDATE', NULL, NULL, NULL, NULL, NULL, 23, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (13, 9, 'button', '角色-删除', 'SYSTEM_ROLE_DELETE', NULL, NULL, NULL, NULL, NULL, 24, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (14, 9, 'button', '角色-分配权限', 'SYSTEM_ROLE_ASSIGN', NULL, NULL, NULL, NULL, NULL, 25, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (15, 1, 'menu', '菜单管理', 'SYSTEM_MENU', '/system/menuPage', '/system/menu/MenuPage.vue', 'Menu', NULL, NULL, 30, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (16, 15, 'button', '菜单-查看', 'SYSTEM_MENU_VIEW', NULL, NULL, NULL, NULL, NULL, 31, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (17, 15, 'button', '菜单-新增', 'SYSTEM_MENU_CREATE', NULL, NULL, NULL, NULL, NULL, 32, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (18, 15, 'button', '菜单-编辑', 'SYSTEM_MENU_UPDATE', NULL, NULL, NULL, NULL, NULL, 33, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (19, 15, 'button', '菜单-删除', 'SYSTEM_MENU_DELETE', NULL, NULL, NULL, NULL, NULL, 34, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (20, 1, 'menu', '部门管理', 'SYSTEM_DEPT', '/system/deptPage', '/system/dept/DeptPage.vue', 'OfficeBuilding', NULL, NULL, 40, 0, 1, NULL, 0, NULL, '2026-03-28 12:59:42', 1, '2026-03-28 18:33:03');
INSERT INTO `system_permission` VALUES (21, 20, 'button', '部门-查看', 'SYSTEM_DEPT_VIEW', NULL, NULL, NULL, NULL, NULL, 41, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (22, 20, 'button', '部门-新增', 'SYSTEM_DEPT_CREATE', NULL, NULL, NULL, NULL, NULL, 42, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (23, 20, 'button', '部门-编辑', 'SYSTEM_DEPT_UPDATE', NULL, NULL, NULL, NULL, NULL, 43, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (24, 20, 'button', '部门-删除', 'SYSTEM_DEPT_DELETE', NULL, NULL, NULL, NULL, NULL, 44, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (25, 1, 'menu', '岗位管理', 'SYSTEM_POST', '/system/postPage', '/system/post/PostPage.vue', 'Briefcase', NULL, NULL, 50, 0, 1, NULL, 0, NULL, '2026-03-28 12:59:42', 1, '2026-03-28 18:33:03');
INSERT INTO `system_permission` VALUES (26, 25, 'button', '岗位-查看', 'SYSTEM_POST_VIEW', NULL, NULL, NULL, NULL, NULL, 51, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (27, 25, 'button', '岗位-新增', 'SYSTEM_POST_CREATE', NULL, NULL, NULL, NULL, NULL, 52, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (28, 25, 'button', '岗位-编辑', 'SYSTEM_POST_UPDATE', NULL, NULL, NULL, NULL, NULL, 53, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (29, 25, 'button', '岗位-删除', 'SYSTEM_POST_DELETE', NULL, NULL, NULL, NULL, NULL, 54, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (30, 1, 'menu', '系统配置管理', 'SYSTEM_CONFIG', '/system/configPage', '/system/config/ConfigPage.vue', 'Monitor', NULL, NULL, 60, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (31, 30, 'button', '系统配置-查看', 'SYSTEM_CONFIG_VIEW', NULL, NULL, NULL, NULL, NULL, 61, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (32, 30, 'button', '系统配置-新增', 'SYSTEM_CONFIG_CREATE', NULL, NULL, NULL, NULL, NULL, 62, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (33, 30, 'button', '系统配置-编辑', 'SYSTEM_CONFIG_UPDATE', NULL, NULL, NULL, NULL, NULL, 63, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (34, 30, 'button', '系统配置-删除', 'SYSTEM_CONFIG_DELETE', NULL, NULL, NULL, NULL, NULL, 64, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (35, 1, 'menu', '数据字典管理', 'SYSTEM_DICT', '/system/dictPage', '/system/dict/DictPage.vue', 'Document', NULL, NULL, 70, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (36, 35, 'button', '数据字典-查看', 'SYSTEM_DICT_VIEW', NULL, NULL, NULL, NULL, NULL, 71, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (37, 35, 'button', '数据字典-新增', 'SYSTEM_DICT_CREATE', NULL, NULL, NULL, NULL, NULL, 72, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (38, 35, 'button', '数据字典-编辑', 'SYSTEM_DICT_UPDATE', NULL, NULL, NULL, NULL, NULL, 73, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (39, 35, 'button', '数据字典-删除', 'SYSTEM_DICT_DELETE', NULL, NULL, NULL, NULL, NULL, 74, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (40, 1, 'menu', '附件管理', 'SYSTEM_ATTACHMENT', '/system/attachmentPage', '/system/attachment/AttachmentPage.vue', 'Folder', NULL, NULL, 80, 0, 1, NULL, 0, NULL, '2026-03-28 12:59:42', 1, '2026-03-28 18:33:03');
INSERT INTO `system_permission` VALUES (41, 40, 'button', '附件-查看', 'SYSTEM_ATTACHMENT_VIEW', NULL, NULL, NULL, NULL, NULL, 81, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (42, 40, 'button', '附件-上传', 'SYSTEM_ATTACHMENT_UPLOAD', NULL, NULL, NULL, NULL, NULL, 82, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (43, 40, 'button', '附件-编辑', 'SYSTEM_ATTACHMENT_UPDATE', NULL, NULL, NULL, NULL, NULL, 83, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (44, 40, 'button', '附件-删除', 'SYSTEM_ATTACHMENT_DELETE', NULL, NULL, NULL, NULL, NULL, 84, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (45, 40, 'button', '附件-下载', 'SYSTEM_ATTACHMENT_DOWNLOAD', NULL, NULL, NULL, NULL, NULL, 85, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (46, 0, 'catalog', '定时任务', 'JOB', NULL, NULL, 'Timer', NULL, NULL, 90, 0, 1, NULL, 0, NULL, '2026-03-28 12:59:42', 1, '2026-03-28 18:35:18');
INSERT INTO `system_permission` VALUES (47, 46, 'menu', '定时任务管理', 'JOB_MANAGEMENT', '/mms-job/jobPage', '/job/management/JobPage.vue', 'Timer', NULL, NULL, 91, 0, 1, NULL, 0, NULL, '2026-03-28 12:59:42', 1, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (48, 47, 'button', '定时任务-查看', 'JOB_MANAGEMENT_VIEW', NULL, NULL, NULL, NULL, NULL, 92, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (49, 47, 'button', '定时任务-新增', 'JOB_MANAGEMENT_CREATE', NULL, NULL, NULL, NULL, NULL, 93, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (50, 47, 'button', '定时任务-编辑', 'JOB_MANAGEMENT_UPDATE', NULL, NULL, NULL, NULL, NULL, 94, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (51, 47, 'button', '定时任务-删除', 'JOB_MANAGEMENT_DELETE', NULL, NULL, NULL, NULL, NULL, 95, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (52, 47, 'button', '定时任务-执行', 'JOB_MANAGEMENT_RUN', NULL, NULL, NULL, NULL, NULL, 96, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (53, 46, 'menu', '定时任务执行记录', 'JOB_RUN_LOG', '/mms-job/jobRunLogPage', '/job/log/JobRunLogPage.vue', 'List', NULL, NULL, 100, 0, 1, NULL, 0, NULL, '2026-03-28 12:59:42', 1, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (54, 53, 'button', '定时任务执行记录-查看', 'JOB_RUN_LOG_VIEW', NULL, NULL, NULL, NULL, NULL, 101, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (55, 53, 'button', '定时任务执行记录-删除', 'JOB_RUN_LOG_DELETE', NULL, NULL, NULL, NULL, NULL, 102, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (56, 53, 'button', '定时任务执行记录-导出', 'JOB_RUN_LOG_EXPORT', NULL, NULL, NULL, NULL, NULL, 103, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (57, 53, 'button', '定时任务执行记录-重试执行', 'JOB_RUN_LOG_RETRY', NULL, NULL, NULL, NULL, NULL, 104, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (58, 53, 'button', '定时任务执行记录-终止执行', 'JOB_RUN_LOG_TERMINATE', NULL, NULL, NULL, NULL, NULL, 105, 1, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_permission` VALUES (59, 0, 'catalog', '审计中心', 'AUDIT', NULL, NULL, 'DataAnalysis', NULL, NULL, 110, 0, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 18:45:26');
INSERT INTO `system_permission` VALUES (60, 59, 'menu', '登录日志', 'AUDIT_LOGIN_LOG', '/audit/loginLogPage', '/audit/loginLog/LoginLogPage.vue', 'Lock', NULL, NULL, 111, 0, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 18:45:26');
INSERT INTO `system_permission` VALUES (61, 60, 'button', '登录日志-查看', 'AUDIT_LOGIN_LOG_VIEW', NULL, NULL, NULL, NULL, NULL, 112, 0, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 18:45:26');
INSERT INTO `system_permission` VALUES (62, 60, 'button', '登录日志-删除', 'AUDIT_LOGIN_LOG_DELETE', NULL, NULL, NULL, NULL, NULL, 113, 0, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 18:45:26');
INSERT INTO `system_permission` VALUES (63, 60, 'button', '登录日志-导出', 'AUDIT_LOGIN_LOG_EXPORT', NULL, NULL, NULL, NULL, NULL, 114, 0, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 18:45:26');
INSERT INTO `system_permission` VALUES (64, 59, 'menu', '操作日志', 'AUDIT_OPERATION_LOG', '/audit/operationLogPage', '/audit/operationLog/OperationLogPage.vue', 'DocumentCopy', NULL, NULL, 121, 0, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 18:45:26');
INSERT INTO `system_permission` VALUES (65, 64, 'button', '操作日志-查看', 'AUDIT_OPERATION_LOG_VIEW', NULL, NULL, NULL, NULL, NULL, 122, 0, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 18:45:26');
INSERT INTO `system_permission` VALUES (66, 64, 'button', '操作日志-删除', 'AUDIT_OPERATION_LOG_DELETE', NULL, NULL, NULL, NULL, NULL, 123, 0, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 18:45:26');
INSERT INTO `system_permission` VALUES (67, 64, 'button', '操作日志-导出', 'AUDIT_OPERATION_LOG_EXPORT', NULL, NULL, NULL, NULL, NULL, 124, 0, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 18:45:26');
INSERT INTO `system_permission` VALUES (68, 59, 'menu', '异常日志', 'AUDIT_EXCEPTION_LOG', '/audit/exceptionLogPage', '/audit/exceptionLog/ExceptionLogPage.vue', 'WarningFilled', NULL, NULL, 131, 0, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 18:45:26');
INSERT INTO `system_permission` VALUES (69, 68, 'button', '异常日志-查看', 'AUDIT_EXCEPTION_LOG_VIEW', NULL, NULL, NULL, NULL, NULL, 132, 0, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 18:45:26');
INSERT INTO `system_permission` VALUES (70, 68, 'button', '异常日志-删除', 'AUDIT_EXCEPTION_LOG_DELETE', NULL, NULL, NULL, NULL, NULL, 133, 0, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 18:45:26');
INSERT INTO `system_permission` VALUES (71, 68, 'button', '异常日志-标记已处理', 'AUDIT_EXCEPTION_LOG_RESOLVE', NULL, NULL, NULL, NULL, NULL, 134, 0, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 18:45:26');
INSERT INTO `system_permission` VALUES (72, 59, 'menu', '接口访问日志', 'AUDIT_API_ACCESS_LOG', '/audit/apiAccessLogPage', '/audit/apiAccessLog/ApiAccessLogPage.vue', 'Histogram', NULL, NULL, 151, 0, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 18:45:26');
INSERT INTO `system_permission` VALUES (73, 72, 'button', '接口访问日志-查看', 'AUDIT_API_ACCESS_LOG_VIEW', NULL, NULL, NULL, NULL, NULL, 152, 0, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 18:45:26');
INSERT INTO `system_permission` VALUES (74, 72, 'button', '接口访问日志-删除', 'AUDIT_API_ACCESS_LOG_DELETE', NULL, NULL, NULL, NULL, NULL, 153, 0, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 18:45:26');
INSERT INTO `system_permission` VALUES (75, 72, 'button', '接口访问日志-导出', 'AUDIT_API_ACCESS_LOG_EXPORT', NULL, NULL, NULL, NULL, NULL, 154, 0, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 18:45:26');
INSERT INTO `system_permission` VALUES (76, 72, 'button', '接口访问日志-统计分析', 'AUDIT_API_ACCESS_LOG_ANALYZE', NULL, NULL, NULL, NULL, NULL, 155, 0, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 18:45:26');
INSERT INTO `system_permission` VALUES (77, 0, 'catalog', '安全中心', 'SECURITY', NULL, NULL, 'Lock', NULL, NULL, 140, 0, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 18:45:26');
INSERT INTO `system_permission` VALUES (78, 77, 'menu', '在线用户', 'SECURITY_ONLINE_USER', '/security/onlineUserPage', '/security/onlineUser/OnlineUserPage.vue', 'Connection', NULL, NULL, 141, 0, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 18:45:26');
INSERT INTO `system_permission` VALUES (79, 78, 'button', '在线用户-查看', 'SECURITY_ONLINE_USER_VIEW', NULL, NULL, NULL, NULL, NULL, 142, 0, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 18:45:26');
INSERT INTO `system_permission` VALUES (80, 78, 'button', '在线用户-强制下线', 'SECURITY_ONLINE_USER_FORCE_LOGOUT', NULL, NULL, NULL, NULL, NULL, 143, 0, 1, NULL, 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 18:45:26');
INSERT INTO `system_permission` VALUES (9001, 0, 'catalog', '实习管理', 'INTERNSHIP', NULL, NULL, 'Notebook', NULL, NULL, 200, 1, 1, NULL, 0, NULL, '2026-03-28 13:53:02', NULL, '2026-03-28 13:53:02');
INSERT INTO `system_permission` VALUES (9002, 9001, 'menu', '合作企业', 'INTERNSHIP_ENTERPRISE', '/internship/enterprisePage', '/internship/enterprise/EnterprisePage.vue', 'OfficeBuilding', NULL, NULL, 201, 1, 1, NULL, 0, NULL, '2026-03-28 13:53:02', NULL, '2026-03-28 13:53:02');
INSERT INTO `system_permission` VALUES (9003, 9001, 'menu', '实习批次', 'INTERNSHIP_BATCH', '/internship/batchPage', '/internship/batch/BatchPage.vue', 'Calendar', NULL, NULL, 202, 1, 1, NULL, 0, NULL, '2026-03-28 13:53:02', NULL, '2026-03-28 13:53:02');
INSERT INTO `system_permission` VALUES (9004, 9001, 'menu', '岗位管理', 'INTERNSHIP_POSITION', '/internship/positionPage', '/internship/position/PositionPage.vue', 'Briefcase', NULL, NULL, 203, 1, 1, NULL, 0, NULL, '2026-03-28 13:53:02', NULL, '2026-03-28 13:53:02');
INSERT INTO `system_permission` VALUES (9005, 9001, 'menu', '申请与流程', 'INTERNSHIP_APPLICATION', '/internship/applicationPage', '/internship/application/ApplicationPage.vue', 'Document', NULL, NULL, 204, 1, 1, NULL, 0, NULL, '2026-03-28 13:53:02', NULL, '2026-03-28 13:53:02');
INSERT INTO `system_permission` VALUES (9006, 9001, 'menu', '周志批阅', 'INTERNSHIP_WEEKLY_LOG', '/internship/weeklyLogPage', '/internship/weekly/WeeklyLogAdminPage.vue', 'EditPen', NULL, NULL, 205, 1, 1, NULL, 0, NULL, '2026-03-28 13:53:02', NULL, '2026-03-28 13:53:02');
INSERT INTO `system_permission` VALUES (9007, 9001, 'menu', '实习材料', 'INTERNSHIP_MATERIAL', '/internship/materialPage', '/internship/material/MaterialPage.vue', 'FolderOpened', NULL, NULL, 206, 1, 1, NULL, 1, NULL, '2026-03-28 13:53:02', NULL, '2026-03-29 14:02:33');
INSERT INTO `system_permission` VALUES (9008, 9001, 'menu', '数据统计', 'INTERNSHIP_STATS', '/internship/statsPage', '/internship/stats/StatsPage.vue', 'DataAnalysis', NULL, NULL, 207, 1, 1, NULL, 0, NULL, '2026-03-28 13:53:02', NULL, '2026-03-28 13:53:02');
INSERT INTO `system_permission` VALUES (9009, 9001, 'menu', '我的实习', 'INTERNSHIP_STUDENT', '/internship/studentInternshipPage', '/internship/student/StudentInternshipPage.vue', 'User', NULL, NULL, 208, 1, 1, NULL, 0, NULL, '2026-03-29 14:02:33', NULL, '2026-03-29 14:02:33');
INSERT INTO `system_permission` VALUES (9010, 9001, 'menu', '待批周志', 'INTERNSHIP_MENTOR_WEEKLY', '/internship/mentorWeeklyLogPage', '/internship/weekly/WeeklyLogMentorPage.vue', 'Edit', NULL, NULL, 209, 1, 1, NULL, 0, NULL, '2026-03-29 14:02:33', NULL, '2026-03-29 14:02:33');

-- ----------------------------
-- Table structure for system_post
-- ----------------------------
DROP TABLE IF EXISTS `system_post`;
CREATE TABLE `system_post`  (
  `id` bigint NOT NULL COMMENT '岗位ID',
  `post_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '岗位编码',
  `post_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '岗位名称',
  `post_level` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '岗位等级',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序号',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_post_code`(`post_code` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_deleted`(`deleted` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '岗位表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_post
-- ----------------------------
INSERT INTO `system_post` VALUES (1, 'Chairman', '董事长', 'P1', 1, 1, '公司法人代表，董事会主席，最高决策者', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_post` VALUES (2, 'CEO', '首席执行官', 'P1', 2, 1, '公司最高行政长官，全面负责公司战略和运营', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_post` VALUES (3, 'President', '总裁', 'P2', 3, 1, '全面负责公司运营和管理', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_post` VALUES (4, 'COO', '首席运营官', 'P2', 4, 1, '负责公司日常运营，向CEO汇报', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_post` VALUES (5, 'CFO', '首席财务官', 'P2', 5, 1, '负责公司财务战略和资金管理', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_post` VALUES (6, 'CTO', '首席技术官', 'P2', 6, 1, '负责公司技术战略和研发体系', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_post` VALUES (7, 'VP_Engineering', '技术副总裁', 'P3', 7, 1, '负责技术团队管理和架构设计', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_post` VALUES (8, 'VP_Product', '产品副总裁', 'P3', 8, 1, '负责产品战略和产品线管理', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_post` VALUES (9, 'VP_Marketing', '市场副总裁', 'P3', 9, 1, '负责市场战略和品牌建设', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_post` VALUES (10, 'VP_Sales', '销售副总裁', 'P3', 10, 1, '负责销售体系和业绩达成', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_post` VALUES (11, 'VP_HR', '人力资源副总裁', 'P3', 11, 1, '负责人力资源战略和组织发展', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_post` VALUES (12, 'Director_Tech', '技术总监', 'P4', 12, 1, '负责技术团队管理和项目管理', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_post` VALUES (13, 'Director_Product', '产品总监', 'P4', 13, 1, '负责产品规划和团队管理', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_post` VALUES (14, 'Director_Design', '设计总监', 'P4', 14, 1, '负责设计体系和创意管理', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_post` VALUES (15, 'Director_Data', '数据总监', 'P4', 15, 1, '负责数据平台和数据分析体系', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_post` VALUES (16, 'Director_Operations', '运营总监', 'P4', 16, 1, '负责用户运营和业务运营', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_post` VALUES (17, 'Manager_Backend', '后端开发经理', 'P5', 17, 1, '负责后端技术团队管理', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_post` VALUES (18, 'Manager_Frontend', '前端开发经理', 'P5', 18, 1, '负责前端技术团队管理', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_post` VALUES (19, 'Manager_Mobile', '移动端开发经理', 'P5', 19, 1, '负责iOS/Android团队管理', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_post` VALUES (20, 'Manager_Testing', '测试经理', 'P5', 20, 1, '负责测试团队和质量保障', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_post` VALUES (21, 'Manager_DevOps', '运维经理', 'P5', 21, 1, '负责运维团队和系统稳定性', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_post` VALUES (22, 'Senior_Architect', '高级架构师', 'P6', 22, 1, '负责系统架构设计和技术选型', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_post` VALUES (23, 'Senior_Engineer', '高级工程师', 'P6', 23, 1, '核心开发人员，解决复杂问题', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_post` VALUES (24, 'Senior_Product', '高级产品经理', 'P6', 24, 1, '负责重要产品线的产品设计', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_post` VALUES (25, 'Senior_Data', '高级数据分析师', 'P6', 25, 1, '深度数据分析和建模', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_post` VALUES (26, 'Engineer', '工程师', 'P7', 26, 1, '开发工程师，负责具体模块开发', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_post` VALUES (27, 'Product_Manager', '产品经理', 'P7', 27, 1, '负责产品设计和需求管理', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_post` VALUES (28, 'UI_Designer', 'UI设计师', 'P7', 28, 1, '用户界面设计', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_post` VALUES (29, 'Data_Analyst', '数据分析师', 'P7', 29, 1, '基础数据分析工作', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_post` VALUES (30, 'Operations', '运营专员', 'P7', 30, 1, '用户运营和活动执行', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_post` VALUES (31, 'Assistant_Engineer', '助理工程师', 'P8', 31, 1, '初级开发人员，在指导下工作', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_post` VALUES (32, 'Intern', '实习生', 'P8', 32, 1, '在校实习生', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');

-- ----------------------------
-- Table structure for system_role
-- ----------------------------
DROP TABLE IF EXISTS `system_role`;
CREATE TABLE `system_role`  (
  `id` bigint NOT NULL COMMENT '角色ID',
  `role_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '角色编码',
  `role_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '角色名称',
  `role_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '角色类型：system-系统角色，custom-自定义角色',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序号',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_role_code`(`role_code` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_deleted`(`deleted` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_role
-- ----------------------------
INSERT INTO `system_role` VALUES (1, 'superAdmin', '超级管理员', 'system', 1, 1, '系统角色不可删除', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role` VALUES (2, 'admin', '管理员', 'system', 2, 0, '系统角色不可删除', 0, NULL, '2026-03-28 12:59:42', 1, '2026-03-28 12:59:42');
INSERT INTO `system_role` VALUES (3, 'internAdmin', '实习管理员', 'custom', 11, 1, '实习模块全菜单（演示）', 0, NULL, '2026-03-28 16:21:31', 1, '2026-03-28 16:21:31');
INSERT INTO `system_role` VALUES (4, 'internEnterprise', '合作企业用户', 'custom', 12, 0, '维护企业与岗位、处理报名（演示）', 0, NULL, '2026-03-28 16:21:31', 1, '2026-03-28 16:21:31');
INSERT INTO `system_role` VALUES (5, 'internTeacher', '校内实习导师', 'custom', 13, 1, '申请/周志相关（演示）', 0, NULL, '2026-03-28 16:21:31', NULL, '2026-03-28 16:21:31');
INSERT INTO `system_role` VALUES (6, 'internStudent', '实习学生', 'custom', 14, 1, '申请/周志/材料（演示）', 0, NULL, '2026-03-28 16:21:31', NULL, '2026-03-28 16:21:31');

-- ----------------------------
-- Table structure for system_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `system_role_permission`;
CREATE TABLE `system_role_permission`  (
  `id` bigint NOT NULL COMMENT '关联ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `permission_id` bigint NOT NULL COMMENT '权限ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_permission`(`role_id` ASC, `permission_id` ASC) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE,
  INDEX `idx_permission_id`(`permission_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '角色权限关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_role_permission
-- ----------------------------
INSERT INTO `system_role_permission` VALUES (1, 1, 1, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (2, 1, 2, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (3, 1, 3, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (4, 1, 4, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (5, 1, 5, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (6, 1, 6, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (7, 1, 7, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (8, 1, 8, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (9, 1, 9, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (10, 1, 10, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (11, 1, 11, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (12, 1, 12, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (13, 1, 13, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (14, 1, 14, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (15, 1, 15, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (16, 1, 16, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (17, 1, 17, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (18, 1, 18, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (19, 1, 19, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (20, 1, 20, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (21, 1, 21, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (22, 1, 22, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (23, 1, 23, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (24, 1, 24, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (25, 1, 25, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (26, 1, 26, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (27, 1, 27, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (28, 1, 28, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (29, 1, 29, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (30, 1, 30, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (31, 1, 31, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (32, 1, 32, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (33, 1, 33, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (34, 1, 34, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (35, 1, 35, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (36, 1, 36, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (37, 1, 37, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (38, 1, 38, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (39, 1, 39, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (40, 1, 40, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (41, 1, 41, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (42, 1, 42, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (43, 1, 43, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (44, 1, 44, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (45, 1, 45, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (46, 1, 46, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (47, 1, 47, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (48, 1, 48, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (49, 1, 49, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (50, 1, 50, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (51, 1, 51, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (52, 1, 52, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (53, 1, 53, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (54, 1, 54, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (55, 1, 55, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (56, 1, 56, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (57, 1, 57, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (58, 1, 58, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (59, 1, 59, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (60, 1, 60, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (61, 1, 61, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (62, 1, 62, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (63, 1, 63, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (64, 1, 64, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (65, 1, 65, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (66, 1, 66, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (67, 1, 67, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (68, 1, 68, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (69, 1, 69, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (70, 1, 70, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (71, 1, 71, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (72, 1, 72, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (73, 1, 73, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (74, 1, 74, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (75, 1, 75, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (76, 1, 76, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (77, 1, 77, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (78, 1, 78, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (79, 1, 79, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (80, 1, 80, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_role_permission` VALUES (200001, 1, 9001, NULL, '2026-03-28 13:53:02');
INSERT INTO `system_role_permission` VALUES (200003, 1, 9002, NULL, '2026-03-28 13:53:02');
INSERT INTO `system_role_permission` VALUES (200005, 1, 9003, NULL, '2026-03-28 13:53:02');
INSERT INTO `system_role_permission` VALUES (200007, 1, 9004, NULL, '2026-03-28 13:53:02');
INSERT INTO `system_role_permission` VALUES (200009, 1, 9005, NULL, '2026-03-28 13:53:02');
INSERT INTO `system_role_permission` VALUES (200011, 1, 9006, NULL, '2026-03-28 13:53:02');
INSERT INTO `system_role_permission` VALUES (200013, 1, 9007, NULL, '2026-03-28 13:53:02');
INSERT INTO `system_role_permission` VALUES (200015, 1, 9008, NULL, '2026-03-28 13:53:02');
INSERT INTO `system_role_permission` VALUES (200017, 1, 9009, NULL, '2026-03-29 14:02:33');
INSERT INTO `system_role_permission` VALUES (200018, 2, 9009, NULL, '2026-03-29 14:02:33');
INSERT INTO `system_role_permission` VALUES (200019, 1, 9010, NULL, '2026-03-29 14:02:33');
INSERT INTO `system_role_permission` VALUES (200020, 2, 9010, NULL, '2026-03-29 14:02:33');
INSERT INTO `system_role_permission` VALUES (210009, 4, 9001, NULL, '2026-03-28 16:21:31');
INSERT INTO `system_role_permission` VALUES (210010, 4, 9002, NULL, '2026-03-28 16:21:31');
INSERT INTO `system_role_permission` VALUES (210011, 4, 9004, NULL, '2026-03-28 16:21:31');
INSERT INTO `system_role_permission` VALUES (210012, 4, 9005, NULL, '2026-03-28 16:21:31');
INSERT INTO `system_role_permission` VALUES (2037770044301864961, 2, 1, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044301864962, 2, 2, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044301864963, 2, 3, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044301864964, 2, 4, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044301864965, 2, 5, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044301864966, 2, 6, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044301864967, 2, 7, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044301864968, 2, 8, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044368973826, 2, 9, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044368973827, 2, 10, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044368973828, 2, 11, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044368973829, 2, 12, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044368973830, 2, 13, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044368973831, 2, 14, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044368973832, 2, 15, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044368973833, 2, 16, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044368973834, 2, 17, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044368973835, 2, 18, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044368973836, 2, 19, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044431888385, 2, 20, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044431888386, 2, 21, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044431888387, 2, 22, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044431888388, 2, 23, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044431888389, 2, 24, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044431888390, 2, 25, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044431888391, 2, 26, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044431888392, 2, 27, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044431888393, 2, 28, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044431888394, 2, 29, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044431888395, 2, 30, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044498997249, 2, 31, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044498997250, 2, 32, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044498997251, 2, 33, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044498997252, 2, 34, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044498997253, 2, 35, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044498997254, 2, 36, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044498997255, 2, 37, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044498997256, 2, 38, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044498997257, 2, 39, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044498997258, 2, 40, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044498997259, 2, 41, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044498997260, 2, 42, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044498997261, 2, 43, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044498997262, 2, 44, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044498997263, 2, 45, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044498997264, 2, 46, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044566106113, 2, 47, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044566106114, 2, 48, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044566106115, 2, 49, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044566106116, 2, 50, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044566106117, 2, 51, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044566106118, 2, 52, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044566106119, 2, 53, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044566106120, 2, 54, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044566106121, 2, 55, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044566106122, 2, 56, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044566106123, 2, 57, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044566106124, 2, 58, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044566106125, 2, 59, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044566106126, 2, 60, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044566106127, 2, 61, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044566106128, 2, 62, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044633214977, 2, 63, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044633214978, 2, 64, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044633214979, 2, 65, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044633214980, 2, 66, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044633214981, 2, 67, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044633214982, 2, 68, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044633214983, 2, 69, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044633214984, 2, 70, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044633214985, 2, 71, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044633214986, 2, 72, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044633214987, 2, 73, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044633214988, 2, 74, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044633214989, 2, 75, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044633214990, 2, 76, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044633214991, 2, 77, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044633214992, 2, 78, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044633214993, 2, 79, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044633214994, 2, 80, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044633214995, 2, 9001, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044633214996, 2, 9002, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044633214997, 2, 9003, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044696129538, 2, 9004, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044696129539, 2, 9005, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044696129540, 2, 9006, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044696129541, 2, 9007, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2037770044696129542, 2, 9008, 2, '2026-03-28 13:53:33');
INSERT INTO `system_role_permission` VALUES (2038138450268123137, 6, 9009, 1, '2026-03-29 14:17:28');
INSERT INTO `system_role_permission` VALUES (2038138450331037698, 6, 9001, 1, '2026-03-29 14:17:28');
INSERT INTO `system_role_permission` VALUES (2038138502558511106, 5, 9010, 1, '2026-03-29 14:17:40');
INSERT INTO `system_role_permission` VALUES (2038138502558511107, 5, 9001, 1, '2026-03-29 14:17:40');
INSERT INTO `system_role_permission` VALUES (2038147256272158722, 3, 3, 1, '2026-03-29 14:52:28');
INSERT INTO `system_role_permission` VALUES (2038147256272158723, 3, 35, 1, '2026-03-29 14:52:28');
INSERT INTO `system_role_permission` VALUES (2038147256272158724, 3, 36, 1, '2026-03-29 14:52:28');
INSERT INTO `system_role_permission` VALUES (2038147256272158725, 3, 37, 1, '2026-03-29 14:52:28');
INSERT INTO `system_role_permission` VALUES (2038147256339267586, 3, 38, 1, '2026-03-29 14:52:28');
INSERT INTO `system_role_permission` VALUES (2038147256339267587, 3, 39, 1, '2026-03-29 14:52:28');
INSERT INTO `system_role_permission` VALUES (2038147256339267588, 3, 9002, 1, '2026-03-29 14:52:28');
INSERT INTO `system_role_permission` VALUES (2038147256339267589, 3, 9003, 1, '2026-03-29 14:52:28');
INSERT INTO `system_role_permission` VALUES (2038147256339267590, 3, 9004, 1, '2026-03-29 14:52:28');
INSERT INTO `system_role_permission` VALUES (2038147256339267591, 3, 9005, 1, '2026-03-29 14:52:28');
INSERT INTO `system_role_permission` VALUES (2038147256339267592, 3, 9008, 1, '2026-03-29 14:52:28');
INSERT INTO `system_role_permission` VALUES (2038147256339267593, 3, 1, 1, '2026-03-29 14:52:28');
INSERT INTO `system_role_permission` VALUES (2038147256339267594, 3, 2, 1, '2026-03-29 14:52:28');
INSERT INTO `system_role_permission` VALUES (2038147256397987841, 3, 9001, 1, '2026-03-29 14:52:28');

-- ----------------------------
-- Table structure for system_user
-- ----------------------------
DROP TABLE IF EXISTS `system_user`;
CREATE TABLE `system_user`  (
  `id` bigint NOT NULL COMMENT '用户ID',
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '用户名（登录账号）',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '密码（加密后）',
  `nickname` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '昵称',
  `real_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '真实姓名',
  `avatar` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '头像URL',
  `email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '邮箱（可为空，但填写后必须唯一）',
  `phone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '手机号（可为空，但填写后必须唯一）',
  `gender` tinyint NULL DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
  `birthday` date NULL DEFAULT NULL COMMENT '生日',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `locked` tinyint NOT NULL DEFAULT 0 COMMENT '是否锁定：0-未锁定，1-已锁定',
  `lock_time` datetime NULL DEFAULT NULL COMMENT '锁定时间',
  `lock_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '锁定原因',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '最后登录IP',
  `password_update_time` datetime NULL DEFAULT NULL COMMENT '密码更新时间',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_username`(`username` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_deleted`(`deleted` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_status_deleted`(`status` ASC, `deleted` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_user
-- ----------------------------
INSERT INTO `system_user` VALUES (1, 'superAdmin', '$2a$12$nFjZno1HedIH3SyVi1pv.uTspMocaWdTKcQ/SqmAh5x81Monhzdga', '超级管理员', '超级管理员', NULL, '18888888888@qq.com', '18888888888', 1, NULL, 1, 0, NULL, NULL, '2026-03-29 15:35:27', '127.0.0.1', NULL, '系统用户不可删除', 0, NULL, '2026-03-28 12:59:42', NULL, '2026-03-28 12:59:42');
INSERT INTO `system_user` VALUES (2, 'lhy', '$2a$12$lKyKrCRR22NN.gybdvO3m.Oa08UNpJImvb6GB1C9v6oiXe6XYC7OS', 'redRain', '李鸿羽', NULL, '2722562862@qq.com', '18255097030', 1, NULL, 1, 0, NULL, NULL, '2026-03-28 16:26:55', '127.0.0.1', NULL, '今天又是一个晴朗的一天', 1, NULL, '2026-03-28 12:59:42', 2, '2026-03-28 16:33:53');
INSERT INTO `system_user` VALUES (3, 'lqh', '$2a$12$Eah/5KgMiLiZtTOqGcrrEOAfS3K62MoLs4oZUw5NkNVdklN.Ie...', '洛', '刘齐慧', NULL, '2825646787@qq.com', '13855605201', 2, NULL, 1, 0, NULL, NULL, NULL, NULL, NULL, '我要喝可乐', 1, NULL, '2026-03-28 12:59:42', 2, '2026-03-28 16:33:37');
INSERT INTO `system_user` VALUES (4, 'ceshi', '$2a$12$5ly8VSaDSuTxhJZmbaX9yekAya69cdfldVCjOZo.hYVNKMsrxmSAW', '测试昵称', '测试用户', NULL, '1234567890@qq.com', '18866668888', 0, NULL, 1, 0, NULL, NULL, NULL, NULL, NULL, '用于测试系统功能', 1, NULL, '2026-03-28 12:59:42', 2, '2026-03-28 16:33:41');
INSERT INTO `system_user` VALUES (5, 'intern_admin', '$2a$10$3726g2NNoN11dW2GFw9Lh.snHcvqzHjr7fRC74nhkaSsHZc2tGkMK', '实习管理员', '实习管理员', NULL, 'intern_admin@demo.local', '19900001001', 1, NULL, 1, 0, NULL, NULL, '2026-03-29 14:59:11', '127.0.0.1', '2026-03-28 17:36:19', '实习模块管理员演示账号', 0, NULL, '2026-03-28 16:21:31', 5, '2026-03-28 17:35:38');
INSERT INTO `system_user` VALUES (6, 'enterprise_user', '$2a$10$T5xCMc0LJVZBdkiHDUtZd.X8FYBQG0qOodvegpeXjwMpj.Hl8Q73.', '企业用户', '某科技公司对接人', NULL, 'enterprise@demo.local', '19900001002', 1, NULL, 1, 0, NULL, NULL, '2026-03-28 17:40:34', '127.0.0.1', '2026-03-28 17:38:33', '合作企业演示账号', 0, NULL, '2026-03-28 16:21:31', 6, '2026-03-28 17:35:38');
INSERT INTO `system_user` VALUES (7, 'teacher_user', '$2a$10$XP7U7j1U3huX2vD4dRnGN.YTIfInlUR7rV2JOsoHGrz4pyCjQ3T0S', '校内导师', '张老师', NULL, 'teacher@demo.local', '19900001003', 1, NULL, 1, 0, NULL, NULL, '2026-03-29 14:42:07', '127.0.0.1', '2026-03-28 17:39:34', '校内导师演示账号', 0, NULL, '2026-03-28 16:21:31', 7, '2026-03-28 17:35:38');
INSERT INTO `system_user` VALUES (8, 'student_user', '$2a$10$K5Q2ztYkm/WzcCdorVPUhOCK1aUnHZud8GLVUYdXGyK0116MS2bZa', '学生', '李同学', NULL, 'student@demo.local', '19900001004', 1, NULL, 1, 0, NULL, NULL, '2026-03-29 14:44:21', '127.0.0.1', '2026-03-28 17:40:06', '学生演示账号', 0, NULL, '2026-03-28 16:21:31', 8, '2026-03-28 17:35:38');

-- ----------------------------
-- Table structure for system_user_dept
-- ----------------------------
DROP TABLE IF EXISTS `system_user_dept`;
CREATE TABLE `system_user_dept`  (
  `id` bigint NOT NULL COMMENT '关联ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `dept_id` bigint NOT NULL COMMENT '部门ID',
  `is_primary` tinyint NOT NULL DEFAULT 0 COMMENT '是否主部门：0-否，1-是',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_dept`(`user_id` ASC, `dept_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_dept_id`(`dept_id` ASC) USING BTREE,
  INDEX `idx_is_primary`(`is_primary` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '用户部门关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_user_dept
-- ----------------------------
INSERT INTO `system_user_dept` VALUES (1, 1, 2, 1, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_user_dept` VALUES (5, 5, 2, 1, NULL, '2026-03-28 16:21:31');
INSERT INTO `system_user_dept` VALUES (6, 6, 4, 1, NULL, '2026-03-28 16:21:31');
INSERT INTO `system_user_dept` VALUES (7, 7, 2, 1, NULL, '2026-03-28 16:21:31');
INSERT INTO `system_user_dept` VALUES (8, 8, 14, 1, NULL, '2026-03-28 16:21:31');

-- ----------------------------
-- Table structure for system_user_post
-- ----------------------------
DROP TABLE IF EXISTS `system_user_post`;
CREATE TABLE `system_user_post`  (
  `id` bigint NOT NULL COMMENT '关联ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `post_id` bigint NOT NULL COMMENT '岗位ID',
  `is_primary` tinyint NOT NULL DEFAULT 0 COMMENT '是否主岗位：0-否，1-是',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_post`(`user_id` ASC, `post_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_post_id`(`post_id` ASC) USING BTREE,
  INDEX `idx_is_primary`(`is_primary` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '用户岗位关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_user_post
-- ----------------------------
INSERT INTO `system_user_post` VALUES (1, 1, 6, 1, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_user_post` VALUES (5, 5, 6, 1, NULL, '2026-03-28 16:21:31');
INSERT INTO `system_user_post` VALUES (6, 6, 20, 1, NULL, '2026-03-28 16:21:31');
INSERT INTO `system_user_post` VALUES (7, 7, 5, 1, NULL, '2026-03-28 16:21:31');
INSERT INTO `system_user_post` VALUES (8, 8, 32, 1, NULL, '2026-03-28 16:21:31');

-- ----------------------------
-- Table structure for system_user_role
-- ----------------------------
DROP TABLE IF EXISTS `system_user_role`;
CREATE TABLE `system_user_role`  (
  `id` bigint NOT NULL COMMENT '关联ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_role`(`user_id` ASC, `role_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '用户角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_user_role
-- ----------------------------
INSERT INTO `system_user_role` VALUES (1, 1, 1, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_user_role` VALUES (2, 2, 2, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_user_role` VALUES (3, 3, 2, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_user_role` VALUES (4, 4, 2, NULL, '2026-03-28 12:59:42');
INSERT INTO `system_user_role` VALUES (5, 5, 3, NULL, '2026-03-28 16:21:31');
INSERT INTO `system_user_role` VALUES (6, 6, 4, NULL, '2026-03-28 16:21:31');
INSERT INTO `system_user_role` VALUES (7, 7, 5, NULL, '2026-03-28 16:21:31');
INSERT INTO `system_user_role` VALUES (8, 8, 6, NULL, '2026-03-28 16:21:31');

SET FOREIGN_KEY_CHECKS = 1;
