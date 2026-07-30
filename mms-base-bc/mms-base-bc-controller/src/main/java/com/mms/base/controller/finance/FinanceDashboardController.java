package com.mms.base.controller.finance;

import com.mms.base.common.finance.vo.FinanceDashboardSummaryVo;
import com.mms.base.service.finance.service.FinanceDashboardService;
import com.mms.common.core.response.Response;
import com.mms.common.security.servlet.annotations.RequiresPermission;
import com.mms.common.security.servlet.constants.PermissionConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 实现功能【记账看板 Controller】
 *
 * @author li.hongyu
 * @date 2026-07-30
 */
@Tag(name = "记账看板", description = "个人记账看板汇总接口")
@RestController
@RequestMapping("/finance/dashboard")
public class FinanceDashboardController {

    @Resource
    private FinanceDashboardService financeDashboardService;

    @Operation(summary = "看板汇总")
    @RequiresPermission(PermissionConstants.FINANCE_DASHBOARD_VIEW)
    @GetMapping("/summary")
    public Response<FinanceDashboardSummaryVo> summary(@RequestParam(required = false, defaultValue = "30") Integer days) {
        return Response.success(financeDashboardService.getSummary(days));
    }
}
