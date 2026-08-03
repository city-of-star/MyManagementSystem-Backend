package com.mms.usercenter.common.message.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mms.common.datasource.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 实现功能【私信成员态实体】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-08-03 23:10:00
 */
@Data
@TableName("msg_dm_member")
@Schema(description = "私信成员态实体")
public class MsgDmMemberEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("conversation_id")
    @Schema(description = "会话ID")
    private Long conversationId;

    @TableField("user_id")
    @Schema(description = "用户ID")
    private Long userId;

    @TableField("peer_id")
    @Schema(description = "对方用户ID")
    private Long peerId;

    @Schema(description = "不显示：0否 1是")
    private Integer hidden;

    @Schema(description = "置顶：0否 1是")
    private Integer pinned;

    @TableField("pinned_time")
    @Schema(description = "置顶时间")
    private LocalDateTime pinnedTime;

    @TableField("unread_count")
    @Schema(description = "未读数")
    private Integer unreadCount;

    @TableField("last_read_msg_id")
    @Schema(description = "最后已读消息ID")
    private Long lastReadMsgId;

    @TableField("cleared_before_id")
    @Schema(description = "清除游标")
    private Long clearedBeforeId;

    @TableField("last_msg_id")
    @Schema(description = "自己可见最新消息ID")
    private Long lastMsgId;

    @TableField("last_msg_preview")
    @Schema(description = "最近消息预览")
    private String lastMsgPreview;

    @TableField("last_msg_time")
    @Schema(description = "最近消息时间")
    private LocalDateTime lastMsgTime;
}
