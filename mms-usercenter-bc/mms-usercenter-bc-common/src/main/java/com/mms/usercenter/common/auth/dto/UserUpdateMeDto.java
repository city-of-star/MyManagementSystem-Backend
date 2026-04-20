package com.mms.usercenter.common.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

/**
 * 实现功能【更新用户请求 DTO】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-19 10:00:00
 */
@Data
@Schema(description = "更新用户请求参数（用户更新自己的基本信息）")
public class UserUpdateMeDto {

    @Schema(description = "昵称", example = "测试用户")
    private String nickname;

    @Schema(description = "真实姓名", example = "张三")
    private String realName;

    @Schema(description = "头像附件ID", example = "1")
    private Long avatarId;

    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "邮箱格式不正确")
    @Schema(description = "邮箱", example = "test@example.com")
    private String email;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @Schema(description = "性别：0-未知，1-男，2-女", example = "1")
    private Integer gender;

    @Schema(description = "生日", example = "1990-01-01")
    private LocalDate birthday;

    @Schema(description = "备注", example = "个人备注")
    private String remark;

}

