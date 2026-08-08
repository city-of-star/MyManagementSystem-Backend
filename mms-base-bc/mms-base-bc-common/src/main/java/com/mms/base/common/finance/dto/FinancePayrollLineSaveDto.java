package com.mms.base.common.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 实现功能【工资录入明细行保存 DTO】
 *
 * @author li.hongyu
 * @date 2026-08-01
 */
@Data
@Schema(description = "工资录入明细行保存参数")
public class FinancePayrollLineSaveDto {

    @Size(max = 32, message = "行键长度不能超过32")
    @Schema(description = "稳定键；为空时由服务端生成")
    private String lineKey;

    @NotBlank(message = "展示名称不能为空")
    @Size(max = 64, message = "展示名称长度不能超过64")
    @Schema(description = "展示名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String label;

    @NotBlank(message = "行类型不能为空")
    @Schema(description = "income/expense/transfer", requiredMode = Schema.RequiredMode.REQUIRED)
    private String lineType;

    @Schema(description = "分类ID（收入/支出；启用时必填）")
    private Long categoryId;

    @Schema(description = "账户ID（收入/支出；启用时必填）")
    private Long accountId;

    @Schema(description = "转出账户ID（转账；启用时必填）")
    private Long fromAccountId;

    @Schema(description = "转入账户ID（转账；启用时必填）")
    private Long toAccountId;

    @NotNull(message = "是否计入到手不能为空")
    @Schema(description = "是否计入预估到手：1/0", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer countInNet;

    @NotNull(message = "默认金额不能为空")
    @DecimalMin(value = "0", message = "默认金额不能为负")
    @Schema(description = "默认金额", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal defaultAmount;

    @Schema(description = "排序号")
    private Integer sortOrder;

    @NotNull(message = "启用状态不能为空")
    @Schema(description = "是否启用：1/0", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer enabled;
}
