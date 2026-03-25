package com.mms.usercenter.common.audit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实现功能【用户登录日志分页查询请求 DTO】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-03-24 17:49:26
 */
@Data
@Schema(description = "用户登录日志分页查询请求参数")
public class UserLoginLogPageQueryDto {

    @Schema(description = "页码，从1开始", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer pageSize = 10;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "用户名（模糊查询）", example = "admin")
    private String username;

    @Schema(description = "登录类型：password/sms/email", example = "password")
    private String loginType;

    @Schema(description = "登录IP（模糊查询）", example = "127.0.0.1")
    private String loginIp;

    @Schema(description = "登录状态：0-失败，1-成功", example = "1")
    private Integer loginStatus;

    @Schema(description = "登录时间开始", example = "2026-03-01 00:00:00")
    private LocalDateTime loginTimeStart;

    @Schema(description = "登录时间结束", example = "2026-03-31 23:59:59")
    private LocalDateTime loginTimeEnd;
}

