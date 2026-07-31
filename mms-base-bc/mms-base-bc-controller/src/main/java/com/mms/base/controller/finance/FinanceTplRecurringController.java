package com.mms.base.controller.finance;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.finance.dto.FinanceTplRecurringCreateDto;
import com.mms.base.common.finance.dto.FinanceTplRecurringPageQueryDto;
import com.mms.base.common.finance.dto.FinanceTplRecurringUpdateDto;
import com.mms.base.common.finance.vo.FinanceTplRecurringVo;
import com.mms.base.service.finance.service.FinanceTplRecurringService;
import com.mms.common.core.response.Response;
import com.mms.common.security.servlet.annotations.RequiresPermission;
import com.mms.common.security.servlet.constants.PermissionConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 实现功能【记账初始化模板-快捷项 Controller】
 *
 * @author li.hongyu
 * @date 2026-07-31
 */
@Tag(name = "记账初始化配置-快捷项", description = "管理员维护全局快捷模板（金额恒为0）")
@RestController
@RequestMapping("/finance/tpl/recurring")
public class FinanceTplRecurringController {

    @Resource
    private FinanceTplRecurringService financeTplRecurringService;

    @Operation(summary = "分页查询快捷项模板")
    @RequiresPermission(PermissionConstants.SYSTEM_FINANCE_SETUP_VIEW)
    @PostMapping("/page")
    public Response<Page<FinanceTplRecurringVo>> page(@RequestBody @Valid FinanceTplRecurringPageQueryDto dto) {
        return Response.success(financeTplRecurringService.getPage(dto));
    }

    @Operation(summary = "快捷项模板详情")
    @RequiresPermission(PermissionConstants.SYSTEM_FINANCE_SETUP_VIEW)
    @GetMapping("/{id}")
    public Response<FinanceTplRecurringVo> getById(@PathVariable Long id) {
        return Response.success(financeTplRecurringService.getById(id));
    }

    @Operation(summary = "新增快捷项模板")
    @RequiresPermission(PermissionConstants.SYSTEM_FINANCE_SETUP_CREATE)
    @PostMapping("/create")
    public Response<FinanceTplRecurringVo> create(@RequestBody @Valid FinanceTplRecurringCreateDto dto) {
        return Response.success(financeTplRecurringService.create(dto));
    }

    @Operation(summary = "更新快捷项模板")
    @RequiresPermission(PermissionConstants.SYSTEM_FINANCE_SETUP_UPDATE)
    @PutMapping("/update")
    public Response<FinanceTplRecurringVo> update(@RequestBody @Valid FinanceTplRecurringUpdateDto dto) {
        return Response.success(financeTplRecurringService.update(dto));
    }

    @Operation(summary = "删除快捷项模板")
    @RequiresPermission(PermissionConstants.SYSTEM_FINANCE_SETUP_DELETE)
    @DeleteMapping("/{id}")
    public Response<Void> delete(@PathVariable Long id) {
        financeTplRecurringService.delete(id);
        return Response.success();
    }
}
