package com.mms.job.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 实现功能【批量删除定时任务请求 DTO】
 * <p>
 * 用于批量删除定时任务定义的请求参数
 * </p>
 *
 * @author li.hongyu
 * @date 2026-02-11 10:30:30
 */
@Data
@Schema(description = "批量删除定时任务请求参数")
public class JobBatchDeleteDto {

    @NotEmpty(message = "任务ID列表不能为空")
    @Schema(description = "任务ID列表", requiredMode = Schema.RequiredMode.REQUIRED, example = "[1, 2, 3]")
    private List<Long> jobIds;
}

