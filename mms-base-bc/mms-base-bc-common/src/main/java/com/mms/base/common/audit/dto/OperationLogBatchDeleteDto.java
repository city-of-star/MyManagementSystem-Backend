package com.mms.base.common.audit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 实现功能【批量删除用户操作日志请求 DTO】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-05-29 10:00:00
 */
@Data
@Schema(description = "批量删除用户操作日志请求参数")
public class OperationLogBatchDeleteDto {

    @NotEmpty(message = "操作日志ID列表不能为空")
    @Schema(description = "操作日志ID列表", requiredMode = Schema.RequiredMode.REQUIRED, example = "[1, 2, 3]")
    private List<Long> logIds;
}
