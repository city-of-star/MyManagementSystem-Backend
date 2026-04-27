package com.mms.usercenter.common.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实现功能【用户偏好配置响应 VO】
 * <p>
 * 返回用户偏好配置数据
 * </p>
 *
 * @author li.hongyu
 * @date 2026-04-27 15:00:00
 */
@Data
@Schema(description = "用户偏好配置响应对象")
public class UserPreferenceVo {

    @Schema(description = "偏好配置ID", example = "1")
    private Long id;

    @Schema(description = "用户ID", example = "1001")
    private Long userId;

    @Schema(description = "偏好键", example = "theme.color")
    private String prefKey;

    @Schema(description = "偏好值", example = "#1677ff")
    private String prefValue;

    @Schema(description = "值类型：string/number/boolean/json", example = "string")
    private String valueType;

    @Schema(description = "备注", example = "主题色偏好")
    private String remark;

    @Schema(description = "创建人ID", example = "1")
    private Long createBy;

    @Schema(description = "创建时间", example = "2026-04-27T15:00:00")
    private LocalDateTime createTime;

    @Schema(description = "更新人ID", example = "1")
    private Long updateBy;

    @Schema(description = "更新时间", example = "2026-04-27T15:00:00")
    private LocalDateTime updateTime;
}
