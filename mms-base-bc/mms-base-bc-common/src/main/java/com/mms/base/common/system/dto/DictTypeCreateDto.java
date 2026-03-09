package com.mms.base.common.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * 实现功能【创建数据字典类型请求 DTO】
 * <p>
 * 用于创建新数据字典类型的请求参数
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-23 11:21:50
 */
@Data
@Schema(description = "创建数据字典类型请求参数")
public class DictTypeCreateDto {

    @NotBlank(message = "字典类型编码不能为空")
    @Size(max = 64, message = "字典类型编码长度不能超过64个字符")
    @Schema(description = "字典类型编码（唯一标识）", requiredMode = Schema.RequiredMode.REQUIRED, example = "user_status")
    private String dictTypeCode;

    @NotBlank(message = "字典类型名称不能为空")
    @Size(max = 128, message = "字典类型名称长度不能超过128个字符")
    @Schema(description = "字典类型名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "用户状态")
    private String dictTypeName;

    @Range(min = 0, max = 1, message = "状态值只能是0或1")
    @Schema(description = "状态：0-禁用，1-启用，默认为1", example = "1")
    private Integer status = 1;

    @Schema(description = "排序号，默认为0", example = "0")
    private Integer sortOrder = 0;

    @Schema(description = "备注", example = "用户账号状态")
    private String remark;
}

