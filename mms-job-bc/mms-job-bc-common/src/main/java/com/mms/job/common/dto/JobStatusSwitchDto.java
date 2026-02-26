package com.mms.job.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 实现功能【切换定时任务启用状态请求 DTO】
 * <p>
 * 用于启用/禁用定时任务定义的请求参数
 * </p>
 *
 * @author li.hongyu
 * @date 2026-02-11 10:30:40
 */
@Data
@Schema(description = "切换定时任务启用状态请求参数")
public class JobStatusSwitchDto {

    @NotNull(message = "任务ID不能为空")
    @Schema(description = "任务ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long jobId;

    @NotNull(message = "状态不能为空")
    @Schema(description = "是否启用：0-禁用，1-启用", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer enabled;
}

