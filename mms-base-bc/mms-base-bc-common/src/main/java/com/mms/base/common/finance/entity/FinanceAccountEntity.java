package com.mms.base.common.finance.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mms.common.datasource.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * 实现功能【记账账户实体】
 *
 * @author li.hongyu
 * @date 2026-07-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("finance_account")
@Schema(description = "记账账户实体")
public class FinanceAccountEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "账户名称")
    private String name;

    @TableField("account_type")
    @Schema(description = "账户类型：cash/wechat/qq/bank/housing_fund/social_security/company_card/medical/other")
    private String accountType;

    @TableField("opening_balance")
    @Schema(description = "期初余额")
    private BigDecimal openingBalance;

    @TableField("account_no")
    @Schema(description = "账号/卡号")
    private String accountNo;

    @Schema(description = "备注")
    private String note;

    @TableField("sort_order")
    @Schema(description = "排序号")
    private Integer sortOrder;

    @Schema(description = "是否启用：1-启用，0-禁用")
    private Integer enabled;
}
