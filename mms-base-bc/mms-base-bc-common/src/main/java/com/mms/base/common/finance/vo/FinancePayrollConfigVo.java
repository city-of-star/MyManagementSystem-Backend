package com.mms.base.common.finance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 实现功能【工资录入配置 VO】
 *
 * @author li.hongyu
 * @date 2026-08-01
 */
@Data
@Schema(description = "工资录入配置")
public class FinancePayrollConfigVo {

    @Schema(description = "配置头ID")
    private Long id;

    @Schema(description = "工资到手账户ID")
    private Long salaryAccountId;

    @Schema(description = "工资到手账户名称")
    private String salaryAccountName;

    @Schema(description = "先记到手/基本工资分类ID")
    private Long salaryCategoryId;

    @Schema(description = "分类名称")
    private String salaryCategoryName;

    @Schema(description = "明细行")
    private List<FinancePayrollLineVo> lines = new ArrayList<>();
}
