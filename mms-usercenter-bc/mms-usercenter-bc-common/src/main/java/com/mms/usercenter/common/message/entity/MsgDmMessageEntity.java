package com.mms.usercenter.common.message.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mms.common.datasource.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;

/**
 * 实现功能【私信消息实体】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-08-03 23:10:00
 */
@Data
@TableName("msg_dm_message")
@Schema(description = "私信消息实体")
public class MsgDmMessageEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("conversation_id")
    @Schema(description = "会话ID")
    private Long conversationId;

    @TableField("sender_id")
    @Schema(description = "发送人ID")
    private Long senderId;

    @Schema(description = "纯文本内容")
    private String content;
}
