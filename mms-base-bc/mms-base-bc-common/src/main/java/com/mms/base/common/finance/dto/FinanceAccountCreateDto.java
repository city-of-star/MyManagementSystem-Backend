package com.mms.base.common.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 实现功能【创建记账账户请求 DTO】
 *
 * @author li.hongyu
 * @date 2026-07-30
 */
@Data
@Schema(description = "创建记账账户请求参数")
public class FinanceAccountCreateDto {

    @NotBlank(message = "账户名称不能为空")
    @Size(max = 64, message = "账户名称长度不能超过64个字符")
    @Schema(description = "账户名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "微信零钱")
    private String name;

    @NotBlank(message = "账户类型不能为空")
    @Size(max = 32, message = "账户类型长度不能超过32个字符")
    @Schema(description = "账户类型（字典 finance_account_type）",
            requiredMode = Schema.RequiredMode.REQUIRED, example = "alipay")
    private String accountType;

    @NotNull(message = "期初余额不能为空")
    @DecimalMin(value = "0", message = "期初余额不能小于0")
    @Schema(description = "期初余额", requiredMode = Schema.RequiredMode.REQUIRED, example = "0.00")
    private BigDecimal openingBalance;

    @Size(max = 128, message = "账号长度不能超过128个字符")
    @Schema(description = "账号/卡号", example = "6222****")
    private String accountNo;

    @Size(max = 512, message = "备注长度不能超过512个字符")
    @Schema(description = "备注")
    private String note;

    @Schema(description = "排序号", example = "1")
    private Integer sortOrder;

    @Schema(description = "是否启用：1-启用，0-禁用", example = "1")
    private Integer enabled;
}
