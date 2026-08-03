package com.mms.usercenter.common.message.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实现功能【私信消息 VO】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-08-03 23:10:00
 */
@Data
@Schema(description = "私信消息")
public class MsgDmMessageVo {

    @Schema(description = "消息ID")
    private Long id;

    @Schema(description = "会话ID")
    private Long conversationId;

    @Schema(description = "发送人ID")
    private Long senderId;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "是否自己发送")
    private Boolean mine;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
