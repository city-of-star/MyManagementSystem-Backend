package com.mms.base.service.finance.service;

import com.mms.base.common.finance.vo.FinanceDashboardSummaryVo;

/**
 * 实现功能【记账看板服务】
 *
 * @author li.hongyu
 * @date 2026-07-30
 */
public interface FinanceDashboardService {

    FinanceDashboardSummaryVo getSummary(Integer days);
}
