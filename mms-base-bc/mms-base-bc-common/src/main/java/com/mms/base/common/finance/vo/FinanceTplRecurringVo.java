package com.mms.base.common.finance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实现功能【记账初始化模板-快捷项 VO】
 *
 * @author li.hongyu
 * @date 2026-07-31
 */
@Data
@Schema(description = "记账初始化模板-快捷项响应对象")
public class FinanceTplRecurringVo {

    @Schema(description = "模板ID")
    private Long id;

    @Schema(description = "模板名称")
    private String name;

    @Schema(description = "方向：income/expense/transfer")
    private String direction;

    @Schema(description = "模板分类ID")
    private Long categoryId;

    @Schema(description = "模板分类名称")
    private String categoryName;

    @Schema(description = "模板账户ID")
    private Long accountId;

    @Schema(description = "模板账户名称")
    private String accountName;

    @Schema(description = "模板转出账户ID")
    private Long fromAccountId;

    @Schema(description = "模板转出账户名称")
    private String fromAccountName;

    @Schema(description = "模板转入账户ID")
    private Long toAccountId;

    @Schema(description = "模板转入账户名称")
    private String toAccountName;

    @Schema(description = "排序号")
    private Integer sortOrder;

    @Schema(description = "是否启用：1/0")
    private Integer enabled;

    @Schema(description = "备注")
    private String note;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
