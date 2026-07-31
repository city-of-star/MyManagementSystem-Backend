package com.mms.base.controller.finance;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.finance.dto.FinanceTplCategoryCreateDto;
import com.mms.base.common.finance.dto.FinanceTplCategoryPageQueryDto;
import com.mms.base.common.finance.dto.FinanceTplCategoryUpdateDto;
import com.mms.base.common.finance.vo.FinanceTplCategoryVo;
import com.mms.base.service.finance.service.FinanceTplCategoryService;
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
 * 实现功能【记账初始化模板-分类 Controller】
 *
 * @author li.hongyu
 * @date 2026-07-31
 */
@Tag(name = "记账初始化配置-分类", description = "管理员维护全局分类模板")
@RestController
@RequestMapping("/finance/tpl/category")
public class FinanceTplCategoryController {

    @Resource
    private FinanceTplCategoryService financeTplCategoryService;

    @Operation(summary = "分页查询分类模板")
    @RequiresPermission(PermissionConstants.SYSTEM_FINANCE_SETUP_VIEW)
    @PostMapping("/page")
    public Response<Page<FinanceTplCategoryVo>> page(@RequestBody @Valid FinanceTplCategoryPageQueryDto dto) {
        return Response.success(financeTplCategoryService.getPage(dto));
    }

    @Operation(summary = "分类模板列表")
    @RequiresPermission(PermissionConstants.SYSTEM_FINANCE_SETUP_VIEW)
    @GetMapping("/list")
    public Response<List<FinanceTplCategoryVo>> list(
            @RequestParam(required = false) String direction,
            @RequestParam(required = false) Integer enabled) {
        return Response.success(financeTplCategoryService.list(direction, enabled));
    }

    @Operation(summary = "分类模板详情")
    @RequiresPermission(PermissionConstants.SYSTEM_FINANCE_SETUP_VIEW)
    @GetMapping("/{id}")
    public Response<FinanceTplCategoryVo> getById(@PathVariable Long id) {
        return Response.success(financeTplCategoryService.getById(id));
    }

    @Operation(summary = "新增分类模板")
    @RequiresPermission(PermissionConstants.SYSTEM_FINANCE_SETUP_CREATE)
    @PostMapping("/create")
    public Response<FinanceTplCategoryVo> create(@RequestBody @Valid FinanceTplCategoryCreateDto dto) {
        return Response.success(financeTplCategoryService.create(dto));
    }

    @Operation(summary = "更新分类模板")
    @RequiresPermission(PermissionConstants.SYSTEM_FINANCE_SETUP_UPDATE)
    @PutMapping("/update")
    public Response<FinanceTplCategoryVo> update(@RequestBody @Valid FinanceTplCategoryUpdateDto dto) {
        return Response.success(financeTplCategoryService.update(dto));
    }

    @Operation(summary = "删除分类模板")
    @RequiresPermission(PermissionConstants.SYSTEM_FINANCE_SETUP_DELETE)
    @DeleteMapping("/{id}")
    public Response<Void> delete(@PathVariable Long id) {
        financeTplCategoryService.delete(id);
        return Response.success();
    }
}
