package com.mms.base.common.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 实现功能【切换附件状态请求 DTO】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-02-06 14:36:38
 */
@Data
@Schema(description = "切换附件状态请求参数")
public class AttachmentStatusSwitchDto {

    @NotNull(message = "附件ID不能为空")
    @Schema(description = "附件ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @NotNull(message = "状态不能为空")
    @Schema(description = "状态：0-禁用，1-启用", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;
}

