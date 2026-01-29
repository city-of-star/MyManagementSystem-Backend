package com.mms.usercenter.common.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实现功能【用户分页查询请求 DTO】
 * <p>
 * 用于分页查询用户列表的请求参数
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-19 10:00:00
 */
@Data
@Schema(description = "用户分页查询请求参数")
public class UserPageQueryDto {

    @Schema(description = "页码，从1开始", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer pageSize = 10;

    @Schema(description = "用户名（模糊查询）", example = "admin")
    private String username;

    @Schema(description = "昵称（模糊查询）", example = "管理员")
    private String nickname;

    @Schema(description = "真实姓名（模糊查询）", example = "张三")
    private String realName;

    @Schema(description = "邮箱（精确查询）", example = "admin@example.com")
    private String email;

    @Schema(description = "手机号（精确查询）", example = "13800138000")
    private String phone;

    @Schema(description = "状态：0-禁用，1-启用", example = "1")
    private Integer status;

    @Schema(description = "是否锁定：0-未锁定，1-已锁定", example = "0")
    private Integer locked;

    @Schema(description = "性别：0-未知，1-男，2-女", example = "1")
    private Integer gender;

    @Schema(description = "创建时间开始", example = "2025-01-01 00:00:00")
    private LocalDateTime createTimeStart;

    @Schema(description = "创建时间结束", example = "2025-12-31 23:59:59")
    private LocalDateTime createTimeEnd;

    @Schema(description = "最后登录时间开始", example = "2025-01-01 00:00:00")
    private LocalDateTime lastLoginTimeStart;

    @Schema(description = "最后登录时间结束", example = "2025-12-31 23:59:59")
    private LocalDateTime lastLoginTimeEnd;

    @Schema(description = "所属部门ID（根据部门筛选）", example = "1")
    private Long deptId;

    @Schema(description = "所属岗位ID（根据岗位筛选）", example = "1")
    private Long postId;
}

