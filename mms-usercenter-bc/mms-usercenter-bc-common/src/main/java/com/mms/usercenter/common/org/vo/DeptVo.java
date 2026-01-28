package com.mms.usercenter.common.org.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 实现功能【部门信息响应 VO】
 * <p>
 * 用于返回部门信息的响应对象
 * </p>
 *
 * @author li.hongyu
 * @date 2026-01-23 10:31:22
 */
@Data
@Schema(description = "部门信息响应对象")
public class DeptVo {

    @Schema(description = "部门ID", example = "1")
    private Long id;

    @Schema(description = "父部门ID，0表示顶级部门", example = "0")
    private Long parentId;

    @Schema(description = "部门名称", example = "技术部")
    private String deptName;

    @Schema(description = "部门编码", example = "TECH")
    private String deptCode;

    @Schema(description = "负责人", example = "张三")
    private String leader;

    @Schema(description = "联系电话", example = "13800138000")
    private String phone;

    @Schema(description = "邮箱", example = "tech@example.com")
    private String email;

    @Schema(description = "排序号", example = "1")
    private Integer sortOrder;

    @Schema(description = "状态：0-禁用，1-启用", example = "1")
    private Integer status;

    @Schema(description = "备注", example = "技术部门备注")
    private String remark;

    @Schema(description = "创建人ID", example = "1")
    private Long createBy;

    @Schema(description = "创建时间", example = "2026-01-23 10:00:00")
    private LocalDateTime createTime;

    @Schema(description = "更新人ID", example = "1")
    private Long updateBy;

    @Schema(description = "更新时间", example = "2026-01-23 10:00:00")
    private LocalDateTime updateTime;

    @Schema(description = "子部门列表")
    private List<DeptVo> children = new ArrayList<>();
}
