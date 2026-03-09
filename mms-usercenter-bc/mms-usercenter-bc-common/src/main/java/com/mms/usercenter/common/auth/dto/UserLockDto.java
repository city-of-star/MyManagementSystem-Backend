package com.mms.usercenter.common.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * 实现功能【锁定/解锁用户请求 DTO】
 * <p>
 * 用于锁定或解锁用户的请求参数
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-19 10:00:00
 */
@Data
@Schema(description = "锁定/解锁用户请求参数")
public class UserLockDto {

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long userId;

    @NotNull(message = "锁定状态不能为空")
    @Range(min = 0, max = 1, message = "锁定状态值只能是0或1")
    @Schema(description = "是否锁定：0-解锁，1-锁定", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer locked;

    @Schema(description = "锁定原因（锁定时必须提供）", example = "多次登录失败")
    private String lockReason;
}

