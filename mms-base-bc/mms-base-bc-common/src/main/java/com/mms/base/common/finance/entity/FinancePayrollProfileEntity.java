package com.mms.base.common.finance.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mms.common.datasource.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 实现功能【个人工资录入配置头】
 *
 * @author li.hongyu
 * @date 2026-08-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("finance_payroll_profile")
@Schema(description = "个人工资录入配置头")
public class FinancePayrollProfileEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("user_id")
    @Schema(description = "归属用户ID")
    private Long userId;

    @TableField("salary_account_id")
    @Schema(description = "工资到手账户ID")
    private Long salaryAccountId;

    @TableField("company_card_account_id")
    @Schema(description = "公司卡账户ID")
    private Long companyCardAccountId;

    @TableField("medical_account_id")
    @Schema(description = "医保账户ID")
    private Long medicalAccountId;

    @TableField("housing_fund_account_id")
    @Schema(description = "公积金账户ID")
    private Long housingFundAccountId;

    @TableField("salary_category_id")
    @Schema(description = "先记到手/基本工资分类ID")
    private Long salaryCategoryId;
}
