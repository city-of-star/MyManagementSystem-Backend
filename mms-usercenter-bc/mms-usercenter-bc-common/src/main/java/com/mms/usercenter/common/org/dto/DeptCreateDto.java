package com.mms.usercenter.common.org.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 实现功能【创建部门请求 DTO】
 * <p>
 * 用于创建新部门的请求参数
 * </p>
 *
 * @author li.hongyu
 * @date 2026-01-23 10:30:28
 */
@Data
@Schema(description = "创建部门请求参数")
public class DeptCreateDto {

    @NotNull(message = "父部门ID不能为空")
    @Schema(description = "父部门ID，0表示顶级部门", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Long parentId;

    @NotBlank(message = "部门名称不能为空")
    @Schema(description = "部门名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "技术部")
    private String deptName;

    @NotBlank(message = "部门编码不能为空")
    @Schema(description = "部门编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "TECH")
    private String deptCode;

    @Schema(description = "负责人", example = "张三")
    private String leader;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "联系电话格式不正确")
    @Schema(description = "联系电话", example = "13800138000")
    private String phone;

    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "邮箱格式不正确")
    @Schema(description = "邮箱", example = "tech@example.com")
    private String email;

    @Schema(description = "排序号", example = "1")
    private Integer sortOrder;

    @Schema(description = "状态：0-禁用，1-启用，默认为1", example = "1")
    private Integer status = 1;

    @Schema(description = "备注", example = "技术部门备注")
    private String remark;
}
