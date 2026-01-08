package com.mms.base.common.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 实现功能【创建数据字典数据请求 DTO】
 * <p>
 * 用于创建新数据字典数据的请求参数
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-23 11:21:50
 */
@Data
@Schema(description = "创建数据字典数据请求参数")
public class DictDataCreateDto {

    @NotNull(message = "字典类型ID不能为空")
    @Schema(description = "字典类型ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long dictTypeId;

    @NotBlank(message = "字典标签不能为空")
    @Size(max = 128, message = "字典标签长度不能超过128个字符")
    @Schema(description = "字典标签（显示文本）", requiredMode = Schema.RequiredMode.REQUIRED, example = "启用")
    private String dictLabel;

    @NotBlank(message = "字典值不能为空")
    @Size(max = 128, message = "字典值长度不能超过128个字符")
    @Schema(description = "字典值（实际值）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private String dictValue;

    @Schema(description = "排序号，默认为0", example = "0")
    private Integer dictSort = 0;

    @Schema(description = "是否默认值：0-否，1-是，默认为0", example = "0")
    private Integer isDefault = 0;

    @Schema(description = "状态：0-禁用，1-启用，默认为1", example = "1")
    private Integer status = 1;

    @Schema(description = "备注", example = "用户账号启用")
    private String remark;
}

