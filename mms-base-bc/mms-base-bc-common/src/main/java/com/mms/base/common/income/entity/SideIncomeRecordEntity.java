package com.mms.base.common.income.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mms.common.datasource.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 实现功能【副业收入记录实体】
 *
 * @author li.hongyu
 * @date 2026-07-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("side_income_record")
@Schema(description = "副业收入记录实体")
public class SideIncomeRecordEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("record_date")
    @Schema(description = "业务发生日期")
    private LocalDate recordDate;

    @Schema(description = "应得金额（元）")
    private BigDecimal amount;

    @TableField("gross_amount")
    @Schema(description = "整单流水（元），合作单可选")
    private BigDecimal grossAmount;

    @TableField("source_type")
    @Schema(description = "来源：self-自销，partner-合作分成，other-其他")
    private String sourceType;

    @Schema(description = "状态：paid-已到账，pending-待结算")
    private String status;

    @Schema(description = "备注")
    private String note;
}
