package com.mms.base.common.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 实现功能【记账初始化模板-账户创建 DTO】
 *
 * @author li.hongyu
 * @date 2026-07-31
 */
@Data
@Schema(description = "记账初始化模板-账户创建参数")
public class FinanceTplAccountCreateDto {

    @NotBlank(message = "账户名称不能为空")
    @Size(max = 64, message = "账户名称长度不能超过64个字符")
    @Schema(description = "账户名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotBlank(message = "账户类型不能为空")
    @Size(max = 32, message = "账户类型长度不能超过32个字符")
    @Schema(description = "账户类型（字典 finance_account_type）", requiredMode = Schema.RequiredMode.REQUIRED)
    private String accountType;

    @Size(max = 512, message = "备注长度不能超过512个字符")
    @Schema(description = "备注")
    private String note;

    @Schema(description = "排序号", example = "0")
    private Integer sortOrder;

    @Schema(description = "是否启用：1-启用，0-禁用", example = "1")
    private Integer enabled;
}
