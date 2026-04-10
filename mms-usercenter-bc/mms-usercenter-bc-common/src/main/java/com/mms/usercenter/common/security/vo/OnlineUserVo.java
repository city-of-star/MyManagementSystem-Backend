package com.mms.usercenter.common.security.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 实现功能【在线用户信息】
 */
@Data
@Schema(description = "在线用户信息")
public class OnlineUserVo {

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "用户名", example = "admin")
    private String username;

    @Schema(description = "昵称", example = "系统管理员")
    private String nickname;

    @Schema(description = "真实姓名", example = "张三")
    private String realName;

    @Schema(description = "会话数", example = "2")
    private Integer sessionCount;

    @Schema(description = "最近登录IP", example = "127.0.0.1")
    private String loginIp;

    @Schema(description = "最近登录时间（yyyy-MM-dd HH:mm:ss）", example = "2026-04-10 17:00:00")
    private String loginTime;

    @Schema(description = "最后活跃时间（yyyy-MM-dd HH:mm:ss）", example = "2026-04-10 17:05:00")
    private String lastActiveTime;
}

