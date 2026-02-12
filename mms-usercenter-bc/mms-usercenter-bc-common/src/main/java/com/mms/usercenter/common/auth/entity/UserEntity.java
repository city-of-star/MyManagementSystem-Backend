package com.mms.usercenter.common.auth.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mms.common.datasource.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 实现功能【用户实体】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2025-11-07 09:41:03
 */
@Data
@TableName("user")
@Schema(description = "用户实体")
public class UserEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户名（登录账号）")
    private String username;

    @Schema(description = "密码（加密后）")
    private String password;

    @Schema(description = "昵称")
    private String nickname;

    @TableField("real_name")
    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "邮箱（可为空但唯一）")
    private String email;

    @Schema(description = "手机号（可为空但唯一）")
    private String phone;

    @Schema(description = "性别：0-未知，1-男，2-女")
    private Integer gender;

    @Schema(description = "生日")
    private LocalDate birthday;

    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;

    @Schema(description = "是否锁定：0-未锁定，1-已锁定")
    private Integer locked;

    @TableField("lock_time")
    @Schema(description = "锁定时间")
    private LocalDateTime lockTime;

    @TableField("lock_reason")
    @Schema(description = "锁定原因")
    private String lockReason;

    @TableField("last_login_time")
    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginTime;

    @TableField("last_login_ip")
    @Schema(description = "最后登录IP")
    private String lastLoginIp;

    @TableField("password_update_time")
    @Schema(description = "密码更新时间")
    private LocalDateTime passwordUpdateTime;

    @Schema(description = "备注")
    private String remark;
}