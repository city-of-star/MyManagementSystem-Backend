package com.mms.usercenter.common.message.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实现功能【公告已读/未读用户 VO】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-08-03 23:10:00
 */
@Data
@Schema(description = "公告已读未读用户")
public class MsgAnnounceUserVo {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "主部门名称")
    private String primaryDeptName;

    @Schema(description = "已读时间")
    private LocalDateTime readTime;
}
