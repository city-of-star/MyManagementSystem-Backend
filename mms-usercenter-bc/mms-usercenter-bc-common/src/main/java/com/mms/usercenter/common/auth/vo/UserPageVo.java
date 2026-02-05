package com.mms.usercenter.common.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实现功能【用户分页信息 VO】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-02-05 14:39:48
 */
@Data
@Schema(description = "用户分页信息")
public class UserPageVo {

    @Schema(description = "用户ID", example = "1")
    private Long id;

    @Schema(description = "用户名（登录账号）", example = "admin")
    private String username;

    @Schema(description = "昵称", example = "超级管理员")
    private String nickname;

    @Schema(description = "真实姓名", example = "张三")
    private String realName;

    @Schema(description = "性别：0-未知，1-男，2-女", example = "1")
    private Integer gender;

    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @Schema(description = "邮箱", example = "admin@example.com")
    private String email;

    @Schema(description = "主部门名称", example = "技术中心")
    private String primaryDeptName;

    @Schema(description = "主岗位名称", example = "首席技术官")
    private String primaryPostName;

    @Schema(description = "状态：0-禁用，1-启用", example = "1")
    private Integer status;

    @Schema(description = "是否锁定：0-未锁定，1-已锁定", example = "0")
    private Integer locked;

    @Schema(description = "最后登录时间", example = "2025-12-19 10:00:00")
    private LocalDateTime lastLoginTime;

    @Schema(description = "创建时间", example = "2025-12-01 09:00:00")
    private LocalDateTime createTime;

    @Schema(description = "备注", example = "管理员备注")
    private String remark;
}