package com.mms.base.common.finance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 实现功能【周期记账模板响应 VO】
 *
 * @author li.hongyu
 * @date 2026-07-30
 */
@Data
@Schema(description = "周期记账模板响应对象")
public class FinanceRecurringVo {

    @Schema(description = "模板ID")
    private Long id;

    @Schema(description = "模板名称")
    private String name;

    @Schema(description = "方向：income/expense/transfer")
    private String direction;

    @Schema(description = "金额")
    private BigDecimal amount;

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

    @Schema(description = "周期：none/daily/weekly/monthly")
    private String cycle;

    @Schema(description = "每月几号（1-31）")
    private Integer dayOfMonth;

    @Schema(description = "星期几（1=周一 … 7=周日）")
    private Integer weekday;

    @Schema(description = "排序号")
    private Integer sortOrder;

    @Schema(description = "是否启用：1/0")
    private Integer enabled;

    @Schema(description = "备注")
    private String note;

    @Schema(description = "创建人ID")
    private Long createBy;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新人ID")
    private Long updateBy;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
