package com.mms.job.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实现功能【定时任务执行记录分页查询请求 DTO】
 * <p>
 * 用于分页查询定时任务执行记录列表的请求参数
 * </p>
 *
 * @author li.hongyu
 * @date 2026-02-27 21:30:00
 */
@Data
@Schema(description = "定时任务执行记录分页查询请求参数")
public class JobRunLogPageQueryDto {

    @Schema(description = "页码，从1开始", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer pageSize = 10;

    @Schema(description = "任务定义ID", example = "1")
    private Long jobId;

    @Schema(description = "本次执行唯一ID（模糊查询）", example = "20260227_XXXXXX")
    private String runId;

    @Schema(description = "执行状态：RUNNING/SUCCESS/FAIL/TIMEOUT/SKIP", example = "SUCCESS")
    private String status;

    @Schema(description = "开始时间起始", example = "2026-02-27 00:00:00")
    private LocalDateTime startTimeStart;

    @Schema(description = "开始时间结束", example = "2026-02-27 23:59:59")
    private LocalDateTime startTimeEnd;
}

