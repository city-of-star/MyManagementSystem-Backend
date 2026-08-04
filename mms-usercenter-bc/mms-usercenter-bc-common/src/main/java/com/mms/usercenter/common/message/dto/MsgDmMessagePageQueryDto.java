package com.mms.usercenter.common.message.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 实现功能【私信消息分页查询 DTO】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-08-03 23:10:00
 */
@Data
@Schema(description = "私信消息分页查询参数")
public class MsgDmMessagePageQueryDto {

    @Schema(description = "页码，从1开始（与 beforeId 互斥，有 beforeId 时忽略）", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "20")
    private Integer pageSize = 20;

    @Schema(description = "会话ID")
    private Long conversationId;

    @Schema(description = "游标：加载该消息ID之前（更早）的历史，IM 触顶加载用")
    private Long beforeId;
}
