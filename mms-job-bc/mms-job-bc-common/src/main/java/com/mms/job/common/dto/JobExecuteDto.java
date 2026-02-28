package com.mms.job.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 实现功能【执行定时任务请求 DTO】
 * <p>
 * 用于触发执行指定定时任务一次的请求参数
 * </p>
 *
 * @author li.hongyu
 * @date 2026-02-28 16:49:27
 */
@Data
@Schema(description = "执行定时任务请求参数")
public class JobExecuteDto {

    @NotNull(message = "任务ID不能为空")
    @Schema(description = "任务ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long jobId;
}

