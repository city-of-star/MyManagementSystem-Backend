package com.mms.usercenter.common.message.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 实现功能【打开/发起私信会话 DTO】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-08-03 23:10:00
 */
@Data
@Schema(description = "打开私信会话参数")
public class MsgDmOpenDto {

    @NotNull(message = "对方用户ID不能为空")
    @Schema(description = "对方用户ID")
    private Long peerUserId;
}
