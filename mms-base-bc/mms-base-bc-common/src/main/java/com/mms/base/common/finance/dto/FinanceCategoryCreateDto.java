package com.mms.base.common.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 实现功能【创建记账分类请求 DTO】
 *
 * @author li.hongyu
 * @date 2026-07-30
 */
@Data
@Schema(description = "创建记账分类请求参数")
public class FinanceCategoryCreateDto {

    @NotBlank(message = "分类名称不能为空")
    @Size(max = 64, message = "分类名称长度不能超过64个字符")
    @Schema(description = "分类名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "餐饮")
    private String name;

    @NotBlank(message = "方向不能为空")
    @Size(max = 16, message = "方向长度不能超过16个字符")
    @Schema(description = "方向：income/expense", requiredMode = Schema.RequiredMode.REQUIRED, example = "expense")
    private String direction;

    @Size(max = 64, message = "图标长度不能超过64个字符")
    @Schema(description = "图标", example = "food")
    private String icon;

    @Schema(description = "排序号", example = "1")
    private Integer sortOrder;

    @Schema(description = "是否启用：1-启用，0-禁用", example = "1")
    private Integer enabled;
}
