package com.mms.usercenter.common.org.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 实现功能【更新岗位请求 DTO】
 * <p>
 * 用于更新岗位信息的请求参数
 * </p>
 *
 * @author li.hongyu
 * @date 2026-01-23 10:32:01
 */
@Data
@Schema(description = "更新岗位请求参数")
public class PostUpdateDto {

    @NotNull(message = "岗位ID不能为空")
    @Schema(description = "岗位ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "岗位名称", example = "Java开发工程师")
    private String postName;

    @Schema(description = "岗位等级", example = "P5")
    private String postLevel;

    @Schema(description = "排序号", example = "1")
    private Integer sortOrder;

    @Schema(description = "备注", example = "Java开发岗位备注")
    private String remark;
}
