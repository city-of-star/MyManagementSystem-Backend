package com.mms.usercenter.common.audit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 实现功能【批量删除用户登录日志请求 DTO】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-03-24 17:49:26
 */
@Data
@Schema(description = "批量删除用户登录日志请求参数")
public class UserLoginLogBatchDeleteDto {

    @NotEmpty(message = "登录日志ID列表不能为空")
    @Schema(description = "登录日志ID列表", requiredMode = Schema.RequiredMode.REQUIRED, example = "[1, 2, 3]")
    private List<Long> logIds;
}

