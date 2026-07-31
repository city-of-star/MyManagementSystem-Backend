package com.mms.base.common.finance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 实现功能【记账用户初始化状态 VO】
 *
 * @author li.hongyu
 * @date 2026-07-31
 */
@Data
@Schema(description = "记账用户初始化状态")
public class FinanceInitStatusVo {

    @Schema(description = "是否已从全局模板初始化")
    private boolean initialized;
}
