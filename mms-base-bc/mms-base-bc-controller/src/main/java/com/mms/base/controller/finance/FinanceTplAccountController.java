package com.mms.base.controller.finance;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.finance.dto.FinanceTplAccountCreateDto;
import com.mms.base.common.finance.dto.FinanceTplAccountPageQueryDto;
import com.mms.base.common.finance.dto.FinanceTplAccountUpdateDto;
import com.mms.base.common.finance.vo.FinanceTplAccountVo;
import com.mms.base.service.finance.service.FinanceTplAccountService;
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
 * 实现功能【记账初始化模板-账户 Controller】
 *
 * @author li.hongyu
 * @date 2026-07-31
 */
@Tag(name = "记账初始化配置-账户", description = "管理员维护全局账户模板")
@RestController
@RequestMapping("/finance/tpl/account")
public class FinanceTplAccountController {

    @Resource
    private FinanceTplAccountService financeTplAccountService;

    @Operation(summary = "分页查询账户模板")
    @RequiresPermission(PermissionConstants.SYSTEM_FINANCE_SETUP_VIEW)
    @PostMapping("/page")
    public Response<Page<FinanceTplAccountVo>> page(@RequestBody @Valid FinanceTplAccountPageQueryDto dto) {
        return Response.success(financeTplAccountService.getPage(dto));
    }

    @Operation(summary = "账户模板列表")
    @RequiresPermission(PermissionConstants.SYSTEM_FINANCE_SETUP_VIEW)
    @GetMapping("/list")
    public Response<List<FinanceTplAccountVo>> list(@RequestParam(required = false) Integer enabled) {
        return Response.success(financeTplAccountService.list(enabled));
    }

    @Operation(summary = "账户模板详情")
    @RequiresPermission(PermissionConstants.SYSTEM_FINANCE_SETUP_VIEW)
    @GetMapping("/{id}")
    public Response<FinanceTplAccountVo> getById(@PathVariable Long id) {
        return Response.success(financeTplAccountService.getById(id));
    }

    @Operation(summary = "新增账户模板")
    @RequiresPermission(PermissionConstants.SYSTEM_FINANCE_SETUP_CREATE)
    @PostMapping("/create")
    public Response<FinanceTplAccountVo> create(@RequestBody @Valid FinanceTplAccountCreateDto dto) {
        return Response.success(financeTplAccountService.create(dto));
    }

    @Operation(summary = "更新账户模板")
    @RequiresPermission(PermissionConstants.SYSTEM_FINANCE_SETUP_UPDATE)
    @PutMapping("/update")
    public Response<FinanceTplAccountVo> update(@RequestBody @Valid FinanceTplAccountUpdateDto dto) {
        return Response.success(financeTplAccountService.update(dto));
    }

    @Operation(summary = "删除账户模板")
    @RequiresPermission(PermissionConstants.SYSTEM_FINANCE_SETUP_DELETE)
    @DeleteMapping("/{id}")
    public Response<Void> delete(@PathVariable Long id) {
        financeTplAccountService.delete(id);
        return Response.success();
    }
}
