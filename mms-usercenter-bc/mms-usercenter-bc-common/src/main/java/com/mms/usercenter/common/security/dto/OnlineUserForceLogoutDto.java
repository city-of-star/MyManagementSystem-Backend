package com.mms.usercenter.common.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 实现功能【强制下线请求 DTO】
 */
@Data
@Schema(description = "在线用户强制下线请求参数")
public class OnlineUserForceLogoutDto {

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long userId;
}

