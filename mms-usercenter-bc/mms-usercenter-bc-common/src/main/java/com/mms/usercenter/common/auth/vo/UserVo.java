package com.mms.usercenter.common.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 实现功能【用户信息响应 VO】
 * <p>
 * 用于返回用户信息的响应对象
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-19 10:00:00
 */
@Data
@Schema(description = "用户信息响应对象")
public class UserVo {

    @Schema(description = "用户ID", example = "1")
    private Long id;

    @Schema(description = "用户名（登录账号）", example = "admin")
    private String username;

    @Schema(description = "昵称", example = "超级管理员")
    private String nickname;

    @Schema(description = "真实姓名", example = "张三")
    private String realName;

    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatar;

    @Schema(description = "邮箱", example = "admin@example.com")
    private String email;

    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @Schema(description = "性别：0-未知，1-男，2-女", example = "1")
    private Integer gender;

    @Schema(description = "生日", example = "1990-01-01")
    private LocalDate birthday;

    @Schema(description = "状态：0-禁用，1-启用", example = "1")
    private Integer status;

    @Schema(description = "是否锁定：0-未锁定，1-已锁定", example = "0")
    private Integer locked;

    @Schema(description = "锁定时间", example = "2025-12-19 10:00:00")
    private LocalDateTime lockTime;

    @Schema(description = "锁定原因", example = "多次登录失败")
    private String lockReason;

    @Schema(description = "最后登录时间", example = "2025-12-19 10:00:00")
    private LocalDateTime lastLoginTime;

    @Schema(description = "最后登录IP", example = "192.168.1.1")
    private String lastLoginIp;

    @Schema(description = "密码更新时间", example = "2025-12-19 10:00:00")
    private LocalDateTime passwordUpdateTime;

    @Schema(description = "备注", example = "管理员备注")
    private String remark;

    @Schema(description = "创建人ID", example = "1")
    private Long createBy;

    @Schema(description = "创建时间", example = "2025-12-19 10:00:00")
    private LocalDateTime createTime;

    @Schema(description = "更新人ID", example = "1")
    private Long updateBy;

    @Schema(description = "更新时间", example = "2025-12-19 10:00:00")
    private LocalDateTime updateTime;

    @Schema(description = "主部门ID", example = "1")
    private Long primaryDeptId;

    @Schema(description = "主岗位ID", example = "1")
    private Long primaryPostId;

    @Schema(description = "所属部门ID列表", example = "[1, 2, 3]")
    private List<Long> deptIds;

    @Schema(description = "所属岗位ID列表", example = "[1, 2, 3]")
    private List<Long> postIds;

    @Schema(description = "主部门名称", example = "技术部")
    private String primaryDeptName;

    @Schema(description = "主岗位名称", example = "Java开发工程师")
    private String primaryPostName;
}

