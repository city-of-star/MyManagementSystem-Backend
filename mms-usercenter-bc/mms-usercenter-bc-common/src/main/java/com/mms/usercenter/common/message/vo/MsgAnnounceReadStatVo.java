package com.mms.usercenter.common.message.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 实现功能【公告已读统计聚合 VO】
 * <p>
 * Mapper 批量查询用，不对外暴露。
 * </p>
 *
 * @author li.hongyu
 * @date 2026-08-04 22:40:00
 */
@Data
@Schema(description = "公告已读统计聚合")
public class MsgAnnounceReadStatVo {

    @Schema(description = "公告ID")
    private Long announceId;

    @Schema(description = "已读数")
    private Integer readCount;

    @Schema(description = "未读数")
    private Integer unreadCount;
}
