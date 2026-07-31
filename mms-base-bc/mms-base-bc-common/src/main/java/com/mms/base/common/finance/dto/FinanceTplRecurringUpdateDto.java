package com.mms.base.common.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 实现功能【记账初始化模板-快捷项更新 DTO】
 *
 * @author li.hongyu
 * @date 2026-07-31
 */
@Data
@Schema(description = "记账初始化模板-快捷项更新参数")
public class FinanceTplRecurringUpdateDto {

    @NotNull(message = "模板ID不能为空")
    @Schema(description = "模板ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Size(max = 64, message = "模板名称长度不能超过64个字符")
    @Schema(description = "模板名称")
    private String name;

    @Size(max = 16, message = "方向长度不能超过16个字符")
    @Schema(description = "方向：income/expense/transfer")
    private String direction;

    @Schema(description = "模板分类ID")
    private Long categoryId;

    @Schema(description = "模板账户ID")
    private Long accountId;

    @Schema(description = "模板转出账户ID")
    private Long fromAccountId;

    @Schema(description = "模板转入账户ID")
    private Long toAccountId;

    @Schema(description = "排序号")
    private Integer sortOrder;

    @Schema(description = "是否启用：1/0")
    private Integer enabled;

    @Size(max = 512, message = "备注长度不能超过512个字符")
    @Schema(description = "备注")
    private String note;
}
