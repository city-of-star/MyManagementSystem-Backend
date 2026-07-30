package com.mms.base.controller.finance;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.finance.dto.FinanceAccountBatchDeleteDto;
import com.mms.base.common.finance.dto.FinanceAccountCreateDto;
import com.mms.base.common.finance.dto.FinanceAccountPageQueryDto;
import com.mms.base.common.finance.dto.FinanceAccountUpdateDto;
import com.mms.base.common.finance.vo.FinanceAccountVo;
import com.mms.base.service.finance.service.FinanceAccountService;
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
 * 实现功能【记账账户 Controller】
 *
 * @author li.hongyu
 * @date 2026-07-30
 */
@Tag(name = "记账账户", description = "个人记账账户管理接口")
@RestController
@RequestMapping("/finance/account")
public class FinanceAccountController {

    @Resource
    private FinanceAccountService financeAccountService;

    @Operation(summary = "分页查询账户")
    @RequiresPermission(PermissionConstants.FINANCE_ACCOUNT_VIEW)
    @PostMapping("/page")
    public Response<Page<FinanceAccountVo>> page(@RequestBody @Valid FinanceAccountPageQueryDto dto) {
        return Response.success(financeAccountService.getAccountPage(dto));
    }

    @Operation(summary = "账户列表（含余额）")
    @RequiresPermission(PermissionConstants.FINANCE_ACCOUNT_VIEW)
    @GetMapping("/list")
    public Response<List<FinanceAccountVo>> list(@RequestParam(required = false) Integer enabled) {
        return Response.success(financeAccountService.listAccounts(enabled));
    }

    @Operation(summary = "查询账户详情")
    @RequiresPermission(PermissionConstants.FINANCE_ACCOUNT_VIEW)
    @GetMapping("/{id}")
    public Response<FinanceAccountVo> getById(@PathVariable Long id) {
        return Response.success(financeAccountService.getById(id));
    }

    @Operation(summary = "新增账户")
    @RequiresPermission(PermissionConstants.FINANCE_ACCOUNT_CREATE)
    @PostMapping("/create")
    public Response<FinanceAccountVo> create(@RequestBody @Valid FinanceAccountCreateDto dto) {
        return Response.success(financeAccountService.create(dto));
    }

    @Operation(summary = "更新账户")
    @RequiresPermission(PermissionConstants.FINANCE_ACCOUNT_UPDATE)
    @PutMapping("/update")
    public Response<FinanceAccountVo> update(@RequestBody @Valid FinanceAccountUpdateDto dto) {
        return Response.success(financeAccountService.update(dto));
    }

    @Operation(summary = "删除账户")
    @RequiresPermission(PermissionConstants.FINANCE_ACCOUNT_DELETE)
    @DeleteMapping("/{id}")
    public Response<Void> delete(@PathVariable Long id) {
        financeAccountService.delete(id);
        return Response.success();
    }

    @Operation(summary = "批量删除账户")
    @RequiresPermission(PermissionConstants.FINANCE_ACCOUNT_DELETE)
    @PostMapping("/batch-delete")
    public Response<Void> batchDelete(@RequestBody @Valid FinanceAccountBatchDeleteDto dto) {
        financeAccountService.batchDelete(dto);
        return Response.success();
    }
}
