package com.mms.base.common.finance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 实现功能【记账账户响应 VO】
 *
 * @author li.hongyu
 * @date 2026-07-30
 */
@Data
@Schema(description = "记账账户响应对象")
public class FinanceAccountVo {

    @Schema(description = "账户ID")
    private Long id;

    @Schema(description = "账户名称")
    private String name;

    @Schema(description = "账户类型")
    private String accountType;

    @Schema(description = "期初余额")
    private BigDecimal openingBalance;

    @Schema(description = "当前余额（计算字段）")
    private BigDecimal balance;

    @Schema(description = "账号/卡号")
    private String accountNo;

    @Schema(description = "备注")
    private String note;

    @Schema(description = "排序号")
    private Integer sortOrder;

    @Schema(description = "是否启用：1/0")
    private Integer enabled;

    @Schema(description = "创建人ID")
    private Long createBy;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新人ID")
    private Long updateBy;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
