package com.mms.base.common.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实现功能【定时任务分页查询请求 DTO】
 * <p>
 * 用于分页查询定时任务定义列表的请求参数
 * </p>
 *
 * @author li.hongyu
 * @date 2026-02-11 10:30:00
 */
@Data
@Schema(description = "定时任务分页查询请求参数")
public class JobPageQueryDto {

    @Schema(description = "页码，从1开始", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer pageSize = 10;

    @Schema(description = "所属服务（模糊查询）", example = "mms-base-bc")
    private String serviceName;

    @Schema(description = "任务编码（模糊查询）", example = "daily_report")
    private String jobCode;

    @Schema(description = "任务名称（模糊查询）", example = "日报任务")
    private String jobName;

    @Schema(description = "是否启用：0-禁用，1-启用", example = "1")
    private Integer enabled;

    @Schema(description = "创建时间开始", example = "2026-01-01 00:00:00")
    private LocalDateTime createTimeStart;

    @Schema(description = "创建时间结束", example = "2026-12-31 23:59:59")
    private LocalDateTime createTimeEnd;
}

