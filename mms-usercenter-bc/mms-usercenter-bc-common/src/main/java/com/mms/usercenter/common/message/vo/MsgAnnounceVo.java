package com.mms.usercenter.common.message.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实现功能【系统公告 VO】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-08-03 23:10:00
 */
@Data
@Schema(description = "系统公告信息")
public class MsgAnnounceVo {

    @Schema(description = "公告ID")
    private Long id;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "富文本内容")
    private String contentHtml;

    @Schema(description = "纯文本摘要")
    private String contentText;

    @Schema(description = "范围类型")
    private Integer scopeType;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "目标人数")
    private Integer totalTarget;

    @Schema(description = "成功人数")
    private Integer successCount;

    @Schema(description = "失败人数")
    private Integer failCount;

    @Schema(description = "已读人数")
    private Integer readCount;

    @Schema(description = "未读人数")
    private Integer unreadCount;

    @Schema(description = "错误信息")
    private String errorMsg;

    @Schema(description = "创建人ID")
    private Long createBy;

    @Schema(description = "创建人昵称")
    private String createByName;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
