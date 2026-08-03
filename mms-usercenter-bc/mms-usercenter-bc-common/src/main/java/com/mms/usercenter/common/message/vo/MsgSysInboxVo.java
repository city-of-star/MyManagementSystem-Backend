package com.mms.usercenter.common.message.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实现功能【系统收件箱 VO】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-08-03 23:10:00
 */
@Data
@Schema(description = "系统收件箱消息")
public class MsgSysInboxVo {

    @Schema(description = "消息ID")
    private Long id;

    @Schema(description = "关联公告ID")
    private Long announceId;

    @Schema(description = "业务类型")
    private String bizType;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "富文本内容")
    private String contentHtml;

    @Schema(description = "纯文本摘要")
    private String contentText;

    @Schema(description = "是否收藏")
    private Integer starred;

    @Schema(description = "是否已读")
    private Integer readFlag;

    @Schema(description = "已读时间")
    private LocalDateTime readTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
