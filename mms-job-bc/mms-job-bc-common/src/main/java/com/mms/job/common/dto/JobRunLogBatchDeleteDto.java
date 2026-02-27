package com.mms.job.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 实现功能【批量删除定时任务执行记录请求 DTO】
 * <p>
 * 用于批量删除定时任务执行记录的请求参数
 * </p>
 *
 * @author li.hongyu
 * @date 2026-02-27 22:00:00
 */
@Data
@Schema(description = "批量删除定时任务执行记录请求参数")
public class JobRunLogBatchDeleteDto {

    @NotEmpty(message = "执行记录ID列表不能为空")
    @Schema(description = "执行记录ID列表", requiredMode = Schema.RequiredMode.REQUIRED, example = "[1, 2, 3]")
    private List<Long> logIds;
}

