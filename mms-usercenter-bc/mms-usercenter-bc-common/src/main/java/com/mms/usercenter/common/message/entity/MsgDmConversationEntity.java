package com.mms.usercenter.common.message.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mms.common.datasource.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 实现功能【私信会话实体】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-08-03 23:10:00
 */
@Data
@TableName("msg_dm_conversation")
@Schema(description = "私信会话实体")
public class MsgDmConversationEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("user_low_id")
    @Schema(description = "较小用户ID")
    private Long userLowId;

    @TableField("user_high_id")
    @Schema(description = "较大用户ID")
    private Long userHighId;

    @TableField("last_msg_id")
    @Schema(description = "最近消息ID")
    private Long lastMsgId;

    @TableField("last_msg_time")
    @Schema(description = "最近消息时间")
    private LocalDateTime lastMsgTime;
}
