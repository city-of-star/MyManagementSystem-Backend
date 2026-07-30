package com.mms.base.common.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 实现功能【批量删除记账流水请求 DTO】
 *
 * @author li.hongyu
 * @date 2026-07-30
 */
@Data
@Schema(description = "批量删除记账流水请求参数")
public class FinanceTransactionBatchDeleteDto {

    @NotEmpty(message = "流水ID列表不能为空")
    @Schema(description = "流水ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Long> ids;
}
