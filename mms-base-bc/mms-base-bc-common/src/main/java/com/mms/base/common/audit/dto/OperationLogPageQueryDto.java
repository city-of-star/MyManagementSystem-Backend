package com.mms.base.common.audit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实现功能【用户操作日志分页查询请求 DTO】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-05-29 10:00:00
 */
@Data
@Schema(description = "用户操作日志分页查询请求参数")
public class OperationLogPageQueryDto {

    @Schema(description = "页码，从1开始", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer pageSize = 10;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "用户名（模糊查询）", example = "admin")
    private String username;

    @Schema(description = "业务模块（模糊查询）", example = "用户管理")
    private String module;

    @Schema(description = "操作类型：create/update/delete/export等", example = "create")
    private String operationType;

    @Schema(description = "操作状态：0-失败，1-成功", example = "1")
    private Integer operationStatus;

    @Schema(description = "请求IP（模糊查询）", example = "127.0.0.1")
    private String requestIp;

    @Schema(description = "链路追踪ID（模糊查询）", example = "abc123")
    private String traceId;

    @Schema(description = "操作时间开始", example = "2026-05-01 00:00:00")
    private LocalDateTime operationTimeStart;

    @Schema(description = "操作时间结束", example = "2026-05-31 23:59:59")
    private LocalDateTime operationTimeEnd;
}
