package com.mms.usercenter.common.org.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 实现功能【创建岗位请求 DTO】
 * <p>
 * 用于创建新岗位的请求参数
 * </p>
 *
 * @author li.hongyu
 * @date 2026-01-23 10:31:48
 */
@Data
@Schema(description = "创建岗位请求参数")
public class PostCreateDto {

    @NotBlank(message = "岗位编码不能为空")
    @Schema(description = "岗位编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "JAVA_DEV")
    private String postCode;

    @NotBlank(message = "岗位名称不能为空")
    @Schema(description = "岗位名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "Java开发工程师")
    private String postName;

    @Schema(description = "岗位等级", example = "P5")
    private String postLevel;

    @Schema(description = "排序号", example = "1")
    private Integer sortOrder;

    @Schema(description = "状态：0-禁用，1-启用，默认为1", example = "1")
    private Integer status = 1;

    @Schema(description = "备注", example = "Java开发岗位备注")
    private String remark;
}
