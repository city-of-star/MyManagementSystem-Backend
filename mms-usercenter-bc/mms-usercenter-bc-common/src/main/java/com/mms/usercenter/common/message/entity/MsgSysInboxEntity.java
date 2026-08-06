package com.mms.usercenter.common.message.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mms.common.datasource.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 实现功能【系统通知收件箱实体】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-08-03 23:10:00
 */
@Data
@TableName("msg_sys_inbox")
@Schema(description = "系统通知收件箱实体")
public class MsgSysInboxEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("user_id")
    @Schema(description = "收件人")
    private Long userId;

    @TableField("announce_id")
    @Schema(description = "关联公告ID")
    private Long announceId;

    @TableField("biz_type")
    @Schema(description = "业务类型")
    private String bizType;

    @TableField("biz_id")
    @Schema(description = "业务关联ID")
    private String bizId;

    @Schema(description = "标题")
    private String title;

    @TableField("content_html")
    @Schema(description = "富文本正文")
    private String contentHtml;

    @TableField("content_text")
    @Schema(description = "纯文本或摘要")
    private String contentText;

    @TableField(value = "link_path", updateStrategy = FieldStrategy.ALWAYS)
    @Schema(description = "可选站内跳转路径")
    private String linkPath;

    @Schema(description = "是否收藏：0否 1是")
    private Integer starred;

    @TableField("read_flag")
    @Schema(description = "是否已读：0未读 1已读")
    private Integer readFlag;

    @TableField("read_time")
    @Schema(description = "已读时间")
    private LocalDateTime readTime;
}
