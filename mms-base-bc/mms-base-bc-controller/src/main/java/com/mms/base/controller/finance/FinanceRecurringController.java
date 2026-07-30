package com.mms.base.controller.finance;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.finance.dto.FinanceRecurringBatchDeleteDto;
import com.mms.base.common.finance.dto.FinanceRecurringCreateDto;
import com.mms.base.common.finance.dto.FinanceRecurringPageQueryDto;
import com.mms.base.common.finance.dto.FinanceRecurringUpdateDto;
import com.mms.base.common.finance.vo.FinanceRecurringVo;
import com.mms.base.service.finance.service.FinanceRecurringService;
import com.mms.common.core.response.Response;
import com.mms.common.security.servlet.annotations.RequiresPermission;
import com.mms.common.security.servlet.constants.PermissionConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 实现功能【周期记账模板 Controller】
 *
 * @author li.hongyu
 * @date 2026-07-30
 */
@Tag(name = "周期记账", description = "周期记账模板管理接口")
@RestController
@RequestMapping("/finance/recurring")
public class FinanceRecurringController {

    @Resource
    private FinanceRecurringService financeRecurringService;

    @Operation(summary = "分页查询周期模板")
    @RequiresPermission(PermissionConstants.FINANCE_RECURRING_VIEW)
    @PostMapping("/page")
    public Response<Page<FinanceRecurringVo>> page(@RequestBody @Valid FinanceRecurringPageQueryDto dto) {
        return Response.success(financeRecurringService.getRecurringPage(dto));
    }

    @Operation(summary = "查询周期模板详情")
    @RequiresPermission(PermissionConstants.FINANCE_RECURRING_VIEW)
    @GetMapping("/{id}")
    public Response<FinanceRecurringVo> getById(@PathVariable Long id) {
        return Response.success(financeRecurringService.getById(id));
    }

    @Operation(summary = "新增周期模板")
    @RequiresPermission(PermissionConstants.FINANCE_RECURRING_CREATE)
    @PostMapping("/create")
    public Response<FinanceRecurringVo> create(@RequestBody @Valid FinanceRecurringCreateDto dto) {
        return Response.success(financeRecurringService.create(dto));
    }

    @Operation(summary = "更新周期模板")
    @RequiresPermission(PermissionConstants.FINANCE_RECURRING_UPDATE)
    @PutMapping("/update")
    public Response<FinanceRecurringVo> update(@RequestBody @Valid FinanceRecurringUpdateDto dto) {
        return Response.success(financeRecurringService.update(dto));
    }

    @Operation(summary = "删除周期模板")
    @RequiresPermission(PermissionConstants.FINANCE_RECURRING_DELETE)
    @DeleteMapping("/{id}")
    public Response<Void> delete(@PathVariable Long id) {
        financeRecurringService.delete(id);
        return Response.success();
    }

    @Operation(summary = "批量删除周期模板")
    @RequiresPermission(PermissionConstants.FINANCE_RECURRING_DELETE)
    @PostMapping("/batch-delete")
    public Response<Void> batchDelete(@RequestBody @Valid FinanceRecurringBatchDeleteDto dto) {
        financeRecurringService.batchDelete(dto);
        return Response.success();
    }
}
