package com.mms.base.common.finance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 实现功能【工资录入明细行 VO】
 *
 * @author li.hongyu
 * @date 2026-08-01
 */
@Data
@Schema(description = "工资录入明细行")
public class FinancePayrollLineVo {

    @Schema(description = "行ID")
    private Long id;

    @Schema(description = "稳定键")
    private String lineKey;

    @Schema(description = "展示名称")
    private String label;

    @Schema(description = "income/expense/transfer")
    private String lineType;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "账户ID")
    private Long accountId;

    @Schema(description = "账户名称")
    private String accountName;

    @Schema(description = "转出账户ID")
    private Long fromAccountId;

    @Schema(description = "转出账户名称")
    private String fromAccountName;

    @Schema(description = "转入账户ID")
    private Long toAccountId;

    @Schema(description = "转入账户名称")
    private String toAccountName;

    @Schema(description = "是否计入预估到手：1/0")
    private Integer countInNet;

    @Schema(description = "默认金额")
    private BigDecimal defaultAmount;

    @Schema(description = "排序号")
    private Integer sortOrder;

    @Schema(description = "是否启用：1/0")
    private Integer enabled;
}
