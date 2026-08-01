package com.mms.base.controller.finance;

import com.mms.base.common.finance.dto.FinancePayrollConfigSaveDto;
import com.mms.base.common.finance.vo.FinancePayrollConfigVo;
import com.mms.base.service.finance.service.FinancePayrollConfigService;
import com.mms.common.core.response.Response;
import com.mms.common.security.servlet.annotations.RequiresPermission;
import com.mms.common.security.servlet.constants.PermissionConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 实现功能【工资录入配置 Controller】
 *
 * @author li.hongyu
 * @date 2026-08-01
 */
@Tag(name = "工资录入配置", description = "个人记账工资条字段与账户绑定配置")
@RestController
@RequestMapping("/finance/payroll-config")
public class FinancePayrollConfigController {

    @Resource
    private FinancePayrollConfigService financePayrollConfigService;

    @Operation(summary = "获取当前用户工资录入配置（无则生成默认）")
    @RequiresPermission(PermissionConstants.FINANCE_TRANSACTION_VIEW)
    @GetMapping("/current")
    public Response<FinancePayrollConfigVo> current() {
        return Response.success(financePayrollConfigService.getCurrent());
    }

    @Operation(summary = "保存当前用户工资录入配置")
    @RequiresPermission(PermissionConstants.FINANCE_PAYROLL_CONFIG_UPDATE)
    @PutMapping("/save")
    public Response<FinancePayrollConfigVo> save(@RequestBody @Valid FinancePayrollConfigSaveDto dto) {
        return Response.success(financePayrollConfigService.save(dto));
    }
}
