package com.mms.usercenter.common.message.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 实现功能【私信会话分页查询 DTO】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-08-03 23:10:00
 */
@Data
@Schema(description = "私信会话分页查询参数")
public class MsgDmConversationPageQueryDto {

    @Schema(description = "页码，从1开始", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "20")
    private Integer pageSize = 20;

    @Schema(description = "对方昵称/用户名关键词")
    private String keyword;

    @Schema(description = "排序：unread（默认，置顶+未读优先）/ time（纯时间倒序）")
    private String sortMode;
}
