package com.mms.usercenter.common.message.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 实现功能【业务系统通知投递 DTO】
 * <p>
 * 服务间内部调用：直接写入系统收件箱，不创建公告发件记录。
 * </p>
 *
 * @author li.hongyu
 * @date 2026-08-06
 */
@Data
@Schema(description = "业务系统通知投递参数")
public class MsgBizNotifyDto {

    @NotNull(message = "收件人不能为空")
    @Schema(description = "收件人用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @NotBlank(message = "业务类型不能为空")
    @Size(max = 64, message = "业务类型长度不能超过64个字符")
    @Schema(description = "业务类型，如 FINANCE_RECURRING_DUE", requiredMode = Schema.RequiredMode.REQUIRED)
    private String bizType;

    @NotBlank(message = "业务关联ID不能为空")
    @Size(max = 64, message = "业务关联ID长度不能超过64个字符")
    @Schema(description = "业务幂等键，如 userId:yyyyMMdd", requiredMode = Schema.RequiredMode.REQUIRED)
    private String bizId;

    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200个字符")
    @Schema(description = "标题", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @NotBlank(message = "正文不能为空")
    @Size(max = 2000, message = "正文长度不能超过2000个字符")
    @Schema(description = "纯文本正文", requiredMode = Schema.RequiredMode.REQUIRED)
    private String contentText;
}
