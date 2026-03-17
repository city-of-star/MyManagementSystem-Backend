package com.mms.base.common.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 实现功能【更新数据字典类型请求 DTO】
 * <p>
 * 用于更新数据字典类型信息的请求参数
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-23 11:21:50
 */
@Data
@Schema(description = "更新数据字典类型请求参数")
public class DictTypeUpdateDto {

    @NotNull(message = "字典类型ID不能为空")
    @Schema(description = "字典类型ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Size(max = 128, message = "字典类型名称长度不能超过128个字符")
    @Schema(description = "字典类型名称", example = "用户状态")
    private String dictTypeName;

    @Schema(description = "排序号", example = "0")
    private Integer sortOrder;

    @Schema(description = "备注", example = "用户账号状态")
    private String remark;
}

