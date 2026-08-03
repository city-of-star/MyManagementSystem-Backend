package com.mms.usercenter.common.message.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 实现功能【系统通知收藏切换 DTO】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-08-03 23:10:00
 */
@Data
@Schema(description = "系统通知收藏切换参数")
public class MsgSysInboxStarDto {

    @NotNull(message = "消息ID不能为空")
    @Schema(description = "收件箱消息ID")
    private Long id;

    @NotNull(message = "收藏状态不能为空")
    @Schema(description = "是否收藏：0否 1是")
    private Integer starred;
}
