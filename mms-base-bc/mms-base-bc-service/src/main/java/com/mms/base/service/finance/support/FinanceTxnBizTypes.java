package com.mms.base.service.finance.support;

import com.mms.base.common.finance.entity.FinanceTransactionEntity;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

/**
 * 实现功能【记账流水业务类型常量与解析】
 *
 * @author li.hongyu
 * @date 2026-08-01
 */
public final class FinanceTxnBizTypes {

    public static final String FUND_REDEEM = "fund_redeem";

    private FinanceTxnBizTypes() {
    }

    public static boolean isFundRedeem(FinanceTransactionEntity entity) {
        if (entity == null) {
            return false;
        }
        if (FUND_REDEEM.equals(entity.getBizType())) {
            return true;
        }
        // 兼容升级前：pending 转账且备注含「赎回」
        return "transfer".equals(entity.getTxnType())
                && "pending".equals(entity.getStatus())
                && StringUtils.hasText(entity.getNote())
                && entity.getNote().contains("赎回");
    }

    public static String buildRedeemExtra(BigDecimal shares, BigDecimal cost) {
        return "shares=" + shares.toPlainString() + ";cost=" + cost.toPlainString();
    }

    public static BigDecimal parseExtraDecimal(String extra, String key) {
        if (!StringUtils.hasText(extra) || !StringUtils.hasText(key)) {
            return null;
        }
        for (String part : extra.split(";")) {
            if (!StringUtils.hasText(part)) {
                continue;
            }
            String[] kv = part.split("=", 2);
            if (kv.length == 2 && key.equals(kv[0].trim())) {
                try {
                    return new BigDecimal(kv[1].trim());
                } catch (NumberFormatException ignored) {
                    return null;
                }
            }
        }
        return null;
    }
}
