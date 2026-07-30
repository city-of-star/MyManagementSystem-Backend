package com.mms.base.controller.finance;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.finance.dto.FinancePayrollBatchDto;
import com.mms.base.common.finance.dto.FinanceTransactionBatchDeleteDto;
import com.mms.base.common.finance.dto.FinanceTransactionCreateDto;
import com.mms.base.common.finance.dto.FinanceTransactionFromRecurringDto;
import com.mms.base.common.finance.dto.FinanceTransactionPageQueryDto;
import com.mms.base.common.finance.dto.FinanceTransactionUpdateDto;
import com.mms.base.common.finance.vo.FinanceTransactionVo;
import com.mms.base.service.finance.service.FinanceTransactionService;
import com.mms.common.core.response.Response;
import com.mms.common.security.servlet.annotations.RequiresPermission;
import com.mms.common.security.servlet.constants.PermissionConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 实现功能【记账流水 Controller】
 *
 * @author li.hongyu
 * @date 2026-07-30
 */
@Tag(name = "记账流水", description = "个人记账流水管理接口")
@RestController
@RequestMapping("/finance/transaction")
public class FinanceTransactionController {

    @Resource
    private FinanceTransactionService financeTransactionService;

    @Operation(summary = "分页查询流水")
    @RequiresPermission(PermissionConstants.FINANCE_TRANSACTION_VIEW)
    @PostMapping("/page")
    public Response<Page<FinanceTransactionVo>> page(@RequestBody @Valid FinanceTransactionPageQueryDto dto) {
        return Response.success(financeTransactionService.getTransactionPage(dto));
    }

    @Operation(summary = "查询流水详情")
    @RequiresPermission(PermissionConstants.FINANCE_TRANSACTION_VIEW)
    @GetMapping("/{id}")
    public Response<FinanceTransactionVo> getById(@PathVariable Long id) {
        return Response.success(financeTransactionService.getById(id));
    }

    @Operation(summary = "新增流水")
    @RequiresPermission(PermissionConstants.FINANCE_TRANSACTION_CREATE)
    @PostMapping("/create")
    public Response<FinanceTransactionVo> create(@RequestBody @Valid FinanceTransactionCreateDto dto) {
        return Response.success(financeTransactionService.create(dto));
    }

    @Operation(summary = "由快捷模板生成流水")
    @RequiresPermission(PermissionConstants.FINANCE_TRANSACTION_CREATE)
    @PostMapping("/from-recurring")
    public Response<FinanceTransactionVo> fromRecurring(@RequestBody @Valid FinanceTransactionFromRecurringDto dto) {
        return Response.success(financeTransactionService.createFromRecurring(dto));
    }

    @Operation(summary = "工资条批量入账")
    @RequiresPermission(PermissionConstants.FINANCE_TRANSACTION_CREATE)
    @PostMapping("/payroll-batch")
    public Response<List<FinanceTransactionVo>> payrollBatch(@RequestBody @Valid FinancePayrollBatchDto dto) {
        return Response.success(financeTransactionService.createPayrollBatch(dto));
    }

    @Operation(summary = "更新流水")
    @RequiresPermission(PermissionConstants.FINANCE_TRANSACTION_UPDATE)
    @PutMapping("/update")
    public Response<FinanceTransactionVo> update(@RequestBody @Valid FinanceTransactionUpdateDto dto) {
        return Response.success(financeTransactionService.update(dto));
    }

    @Operation(summary = "删除流水")
    @RequiresPermission(PermissionConstants.FINANCE_TRANSACTION_DELETE)
    @DeleteMapping("/{id}")
    public Response<Void> delete(@PathVariable Long id) {
        financeTransactionService.delete(id);
        return Response.success();
    }

    @Operation(summary = "批量删除流水")
    @RequiresPermission(PermissionConstants.FINANCE_TRANSACTION_DELETE)
    @PostMapping("/batch-delete")
    public Response<Void> batchDelete(@RequestBody @Valid FinanceTransactionBatchDeleteDto dto) {
        financeTransactionService.batchDelete(dto);
        return Response.success();
    }
}
