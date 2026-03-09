package com.mms.base.common.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * 实现功能【切换数据字典类型状态请求 DTO】
 * <p>
 * 用于启用/禁用数据字典类型的请求参数
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-23 11:21:50
 */
@Data
@Schema(description = "切换数据字典类型状态请求参数")
public class DictTypeStatusSwitchDto {

    @NotNull(message = "字典类型ID不能为空")
    @Schema(description = "字典类型ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long dictTypeId;

    @NotNull(message = "状态不能为空")
    @Range(min = 0, max = 1, message = "状态值只能是0或1")
    @Schema(description = "状态：0-禁用，1-启用", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;
}

