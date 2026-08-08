package com.mms.base.common.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 实现功能【工资条批量入账请求 DTO】
 *
 * @author li.hongyu
 * @date 2026-07-30
 */
@Data
@Schema(description = "工资条批量入账请求参数")
public class FinancePayrollBatchDto {

    @NotBlank(message = "录入模式不能为空")
    @Schema(description = "模式：net_only-先记到手，detail-工资条明细", requiredMode = Schema.RequiredMode.REQUIRED,
            example = "detail")
    private String mode;

    @NotNull(message = "交易日期不能为空")
    @Schema(description = "交易日期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026-07-10")
    private LocalDate txnDate;

    @Schema(description = "冲销的「先记到手」流水ID（完善工资条时可选）")
    private Long voidTxnId;

    @Size(max = 512, message = "备注长度不能超过512个字符")
    @Schema(description = "备注")
    private String note;

    @NotNull(message = "工资账户不能为空")
    @Schema(description = "工资到手账户ID（先记到手必用）", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long salaryAccountId;

    @NotNull(message = "工资分类不能为空")
    @Schema(description = "工资分类ID（先记到手必用）", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long salaryCategoryId;

    @Schema(description = "先记到手金额（net_only）")
    @DecimalMin(value = "0.01", message = "到手金额必须大于0")
    private BigDecimal netAmount;

    @Valid
    @Schema(description = "明细行（detail 模式）；金额大于 0 的行才会入账")
    private List<FinancePayrollBatchLineDto> lines = new ArrayList<>();
}
