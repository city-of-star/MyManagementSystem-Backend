package com.mms.base.common.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 实现功能【记账初始化模板-账户更新 DTO】
 *
 * @author li.hongyu
 * @date 2026-07-31
 */
@Data
@Schema(description = "记账初始化模板-账户更新参数")
public class FinanceTplAccountUpdateDto {

    @NotNull(message = "模板ID不能为空")
    @Schema(description = "模板ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Size(max = 64, message = "账户名称长度不能超过64个字符")
    @Schema(description = "账户名称")
    private String name;

    @Size(max = 32, message = "账户类型长度不能超过32个字符")
    @Schema(description = "账户类型")
    private String accountType;

    @Size(max = 512, message = "备注长度不能超过512个字符")
    @Schema(description = "备注")
    private String note;

    @Schema(description = "排序号")
    private Integer sortOrder;

    @Schema(description = "是否启用：1/0")
    private Integer enabled;
}
