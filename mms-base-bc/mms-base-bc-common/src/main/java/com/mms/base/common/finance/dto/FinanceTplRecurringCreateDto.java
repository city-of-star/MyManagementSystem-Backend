package com.mms.base.common.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 实现功能【记账初始化模板-快捷项创建 DTO】
 *
 * @author li.hongyu
 * @date 2026-07-31
 */
@Data
@Schema(description = "记账初始化模板-快捷项创建参数")
public class FinanceTplRecurringCreateDto {

    @NotBlank(message = "模板名称不能为空")
    @Size(max = 64, message = "模板名称长度不能超过64个字符")
    @Schema(description = "模板名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotBlank(message = "方向不能为空")
    @Size(max = 16, message = "方向长度不能超过16个字符")
    @Schema(description = "方向：income/expense/transfer", requiredMode = Schema.RequiredMode.REQUIRED)
    private String direction;

    @Schema(description = "模板分类ID")
    private Long categoryId;

    @Schema(description = "模板账户ID")
    private Long accountId;

    @Schema(description = "模板转出账户ID")
    private Long fromAccountId;

    @Schema(description = "模板转入账户ID")
    private Long toAccountId;

    @Schema(description = "排序号", example = "0")
    private Integer sortOrder;

    @Schema(description = "是否启用：1-启用，0-禁用", example = "1")
    private Integer enabled;

    @Size(max = 512, message = "备注长度不能超过512个字符")
    @Schema(description = "备注")
    private String note;
}
