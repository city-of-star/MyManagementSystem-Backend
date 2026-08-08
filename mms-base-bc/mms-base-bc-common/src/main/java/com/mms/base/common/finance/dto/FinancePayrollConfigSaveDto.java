package com.mms.base.common.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 实现功能【工资录入配置保存 DTO】
 *
 * @author li.hongyu
 * @date 2026-08-01
 */
@Data
@Schema(description = "工资录入配置保存参数")
public class FinancePayrollConfigSaveDto {

    @NotNull(message = "工资账户不能为空")
    @Schema(description = "工资到手账户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long salaryAccountId;

    @NotNull(message = "工资分类不能为空")
    @Schema(description = "先记到手/基本工资分类ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long salaryCategoryId;

    @NotEmpty(message = "明细行不能为空")
    @Valid
    @Schema(description = "明细行", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<FinancePayrollLineSaveDto> lines;
}
