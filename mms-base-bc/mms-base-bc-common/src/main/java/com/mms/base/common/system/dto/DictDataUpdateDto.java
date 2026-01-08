package com.mms.base.common.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 实现功能【更新数据字典数据请求 DTO】
 * <p>
 * 用于更新数据字典数据信息的请求参数
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-23 11:21:50
 */
@Data
@Schema(description = "更新数据字典数据请求参数")
public class DictDataUpdateDto {

    @NotNull(message = "字典数据ID不能为空")
    @Schema(description = "字典数据ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "字典类型ID", example = "1")
    private Long dictTypeId;

    @Size(max = 128, message = "字典标签长度不能超过128个字符")
    @Schema(description = "字典标签（显示文本）", example = "启用")
    private String dictLabel;

    @Size(max = 128, message = "字典值长度不能超过128个字符")
    @Schema(description = "字典值（实际值）", example = "1")
    private String dictValue;

    @Schema(description = "排序号", example = "0")
    private Integer dictSort;

    @Schema(description = "是否默认值：0-否，1-是", example = "0")
    private Integer isDefault;

    @Schema(description = "状态：0-禁用，1-启用", example = "1")
    private Integer status;

    @Schema(description = "备注", example = "用户账号启用")
    private String remark;
}

