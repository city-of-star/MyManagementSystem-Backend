package com.mms.base.common.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 实现功能【工资条批量入账请求 DTO】
 *
 * @author li.hongyu
 * @date 2026-07-30
 */
@Data
@Schema(description = "工资条批量入账请求参数")
public class FinancePayrollBatchDto {

    @NotBlank(message = "录入模式不能为空")
    @Schema(description = "模式：net_only-先记到手，detail-工资条明细", requiredMode = Schema.RequiredMode.REQUIRED,
            example = "detail")
    private String mode;

    @NotNull(message = "交易日期不能为空")
    @Schema(description = "交易日期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026-07-10")
    private LocalDate txnDate;

    @Schema(description = "冲销的「先记到手」流水ID（完善工资条时可选）")
    private Long voidTxnId;

    @Size(max = 512, message = "备注长度不能超过512个字符")
    @Schema(description = "备注")
    private String note;

    // ----- 账户 -----
    @NotNull(message = "工资账户不能为空")
    @Schema(description = "招商卡账户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long salaryAccountId;

    @Schema(description = "公司卡账户ID（明细模式必填）")
    private Long companyCardAccountId;

    @Schema(description = "医保卡账户ID（明细模式必填）")
    private Long medicalAccountId;

    @Schema(description = "公积金账户ID（明细模式必填）")
    private Long housingFundAccountId;

    // ----- 分类 -----
    @NotNull(message = "工资分类不能为空")
    @Schema(description = "工资分类ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long salaryCategoryId;

    @Schema(description = "电脑补贴分类ID")
    private Long computerSubsidyCategoryId;

    @Schema(description = "加班费分类ID")
    private Long overtimeCategoryId;

    @Schema(description = "餐补分类ID")
    private Long mealAllowanceCategoryId;

    @Schema(description = "社保其他分类ID")
    private Long socialOtherCategoryId;

    @Schema(description = "个税分类ID")
    private Long taxCategoryId;

    @Schema(description = "公司公积金分类ID")
    private Long companyHousingFundCategoryId;

    @Schema(description = "公司医保分类ID")
    private Long companyMedicalCategoryId;

    // ----- 金额 -----
    @Schema(description = "先记到手金额（net_only）")
    @DecimalMin(value = "0.01", message = "到手金额必须大于0")
    private BigDecimal netAmount;

    @Schema(description = "基本工资", example = "7200.00")
    private BigDecimal baseSalary;

    @Schema(description = "电脑补贴", example = "100.00")
    private BigDecimal computerSubsidy;

    @Schema(description = "加班/绩效", example = "0.00")
    private BigDecimal overtime;

    @Schema(description = "餐补（入公司卡）", example = "0.00")
    private BigDecimal mealAllowance;

    @Schema(description = "个人医保（转账招商→医保）", example = "100.00")
    private BigDecimal personalMedical;

    @Schema(description = "社保其他（养老金等支出）", example = "425.00")
    private BigDecimal socialOther;

    @Schema(description = "个人公积金（转账招商→公积金）", example = "300.00")
    private BigDecimal personalHousingFund;

    @Schema(description = "公司公积金（入公积金）", example = "300.00")
    private BigDecimal companyHousingFund;

    @Schema(description = "公司医保（入医保卡）", example = "320.00")
    private BigDecimal companyMedical;

    @Schema(description = "个税", example = "0.00")
    private BigDecimal tax;
}
