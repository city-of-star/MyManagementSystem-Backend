package com.mms.base.common.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 实现功能【更新记账账户请求 DTO】
 *
 * @author li.hongyu
 * @date 2026-07-30
 */
@Data
@Schema(description = "更新记账账户请求参数")
public class FinanceAccountUpdateDto {

    @NotNull(message = "账户ID不能为空")
    @Schema(description = "账户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Size(max = 64, message = "账户名称长度不能超过64个字符")
    @Schema(description = "账户名称", example = "微信零钱")
    private String name;

    @Size(max = 32, message = "账户类型长度不能超过32个字符")
    @Schema(description = "账户类型：cash/wechat/qq/bank/housing_fund/social_security/company_card/medical/other", example = "wechat")
    private String accountType;

    @DecimalMin(value = "0", message = "期初余额不能小于0")
    @Schema(description = "期初余额", example = "100.00")
    private BigDecimal openingBalance;

    @Size(max = 128, message = "账号长度不能超过128个字符")
    @Schema(description = "账号/卡号")
    private String accountNo;

    @Size(max = 512, message = "备注长度不能超过512个字符")
    @Schema(description = "备注")
    private String note;

    @Schema(description = "排序号", example = "1")
    private Integer sortOrder;

    @Schema(description = "是否启用：1-启用，0-禁用", example = "1")
    private Integer enabled;
}
