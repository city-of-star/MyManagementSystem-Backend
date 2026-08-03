package com.mms.usercenter.common.message.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实现功能【私信会话 VO】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-08-03 23:10:00
 */
@Data
@Schema(description = "私信会话")
public class MsgDmConversationVo {

    @Schema(description = "会话ID")
    private Long id;

    @Schema(description = "对方用户ID")
    private Long peerUserId;

    @Schema(description = "对方用户名")
    private String peerUsername;

    @Schema(description = "对方昵称")
    private String peerNickname;

    @Schema(description = "对方头像附件ID")
    private Long peerAvatarId;

    @Schema(description = "最近消息预览")
    private String lastMsgPreview;

    @Schema(description = "最近消息时间")
    private LocalDateTime lastMsgTime;

    @Schema(description = "未读数")
    private Integer unreadCount;

    @Schema(description = "是否置顶")
    private Integer pinned;

    @Schema(description = "对方是否可用")
    private Boolean peerAvailable;
}
