package com.mms.base.common.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 实现功能【记账初始化模板-分类创建 DTO】
 *
 * @author li.hongyu
 * @date 2026-07-31
 */
@Data
@Schema(description = "记账初始化模板-分类创建参数")
public class FinanceTplCategoryCreateDto {

    @NotBlank(message = "分类名称不能为空")
    @Size(max = 64, message = "分类名称长度不能超过64个字符")
    @Schema(description = "分类名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotBlank(message = "方向不能为空")
    @Size(max = 16, message = "方向长度不能超过16个字符")
    @Schema(description = "方向：income/expense", requiredMode = Schema.RequiredMode.REQUIRED)
    private String direction;

    @Size(max = 64, message = "图标长度不能超过64个字符")
    @Schema(description = "图标")
    private String icon;

    @Schema(description = "排序号", example = "0")
    private Integer sortOrder;

    @Schema(description = "是否启用：1-启用，0-禁用", example = "1")
    private Integer enabled;
}
