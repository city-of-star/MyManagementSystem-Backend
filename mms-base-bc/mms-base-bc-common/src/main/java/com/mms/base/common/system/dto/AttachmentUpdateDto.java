package com.mms.base.common.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 实现功能【更新附件请求 DTO】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-02-06 14:36:38
 */
@Data
@Schema(description = "更新附件请求参数")
public class AttachmentUpdateDto {

    @NotNull(message = "附件ID不能为空")
    @Schema(description = "附件ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Size(max = 64, message = "业务类型长度不能超过64个字符")
    @Schema(description = "业务类型（用于区分不同业务场景）", example = "NOTICE_ATTACHMENT")
    private String businessType;

    @Schema(description = "关联业务ID", example = "1001")
    private Long businessId;

    @Size(max = 512, message = "备注长度不能超过512个字符")
    @Schema(description = "备注", example = "公告附件")
    private String remark;
}

