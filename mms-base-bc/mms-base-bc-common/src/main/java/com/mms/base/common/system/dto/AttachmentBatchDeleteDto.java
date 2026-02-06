package com.mms.base.common.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 实现功能【批量删除附件请求 DTO】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-02-06 14:36:38
 */
@Data
@Schema(description = "批量删除附件请求参数")
public class AttachmentBatchDeleteDto {

    @NotEmpty(message = "附件ID列表不能为空")
    @Schema(description = "附件ID列表", requiredMode = Schema.RequiredMode.REQUIRED, example = "[1,2,3]")
    private List<Long> ids;
}

