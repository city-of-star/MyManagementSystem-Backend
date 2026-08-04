package com.mms.usercenter.common.message.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 实现功能【系统收件箱分页查询 DTO】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-08-03 23:10:00
 */
@Data
@Schema(description = "系统收件箱分页查询参数")
public class MsgSysInboxPageQueryDto {

    @Schema(description = "页码，从1开始", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "20")
    private Integer pageSize = 20;

    @Schema(description = "关键词（标题/摘要）")
    private String keyword;

    @Schema(description = "仅看收藏：1是")
    private Integer starred;

    @Schema(description = "排序：unread（默认，未读优先）/ time（纯时间倒序）")
    private String sortMode;
}
