package com.mms.usercenter.common.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实现功能【用户登录日志 VO】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-01-29 17:16:02
 */
@Data
@Schema(description = "用户登录日志 VO")
public class UserLoginLogVo {

    @Schema(description = "日志ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "登录IP")
    private String loginIp;

    @Schema(description = "登录地点")
    private String loginLocation;

    @Schema(description = "用户代理（浏览器信息）")
    private String userAgent;

    @Schema(description = "登录状态：0-失败，1-成功")
    private Integer loginStatus;

    @Schema(description = "登录消息（失败原因等）")
    private String loginMessage;

    @Schema(description = "登录时间")
    private LocalDateTime loginTime;
}

