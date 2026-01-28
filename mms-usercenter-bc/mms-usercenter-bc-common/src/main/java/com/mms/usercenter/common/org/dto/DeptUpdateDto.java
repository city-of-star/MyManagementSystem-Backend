package com.mms.usercenter.common.org.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 实现功能【更新部门请求 DTO】
 * <p>
 * 用于更新部门信息的请求参数
 * </p>
 *
 * @author li.hongyu
 * @date 2026-01-23 10:30:42
 */
@Data
@Schema(description = "更新部门请求参数")
public class DeptUpdateDto {

    @NotNull(message = "部门ID不能为空")
    @Schema(description = "部门ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "父部门ID，0表示顶级部门", example = "0")
    private Long parentId;

    @Schema(description = "部门名称", example = "技术部")
    private String deptName;

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

    @Schema(description = "备注", example = "技术部门备注")
    private String remark;
}
