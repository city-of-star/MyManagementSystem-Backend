package com.mms.base.controller.finance;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.finance.dto.FinanceCategoryBatchDeleteDto;
import com.mms.base.common.finance.dto.FinanceCategoryCreateDto;
import com.mms.base.common.finance.dto.FinanceCategoryListQueryDto;
import com.mms.base.common.finance.dto.FinanceCategoryPageQueryDto;
import com.mms.base.common.finance.dto.FinanceCategoryUpdateDto;
import com.mms.base.common.finance.vo.FinanceCategoryVo;
import com.mms.base.service.finance.service.FinanceCategoryService;
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
 * 实现功能【记账分类 Controller】
 *
 * @author li.hongyu
 * @date 2026-07-30
 */
@Tag(name = "记账分类", description = "个人记账分类管理接口")
@RestController
@RequestMapping("/finance/category")
public class FinanceCategoryController {

    @Resource
    private FinanceCategoryService financeCategoryService;

    @Operation(summary = "分页查询分类")
    @RequiresPermission(PermissionConstants.FINANCE_CATEGORY_VIEW)
    @PostMapping("/page")
    public Response<Page<FinanceCategoryVo>> page(@RequestBody @Valid FinanceCategoryPageQueryDto dto) {
        return Response.success(financeCategoryService.getCategoryPage(dto));
    }

    @Operation(summary = "分类列表")
    @RequiresPermission(PermissionConstants.FINANCE_CATEGORY_VIEW)
    @GetMapping("/list")
    public Response<List<FinanceCategoryVo>> list(FinanceCategoryListQueryDto dto) {
        return Response.success(financeCategoryService.listCategories(dto));
    }

    @Operation(summary = "查询分类详情")
    @RequiresPermission(PermissionConstants.FINANCE_CATEGORY_VIEW)
    @GetMapping("/{id}")
    public Response<FinanceCategoryVo> getById(@PathVariable Long id) {
        return Response.success(financeCategoryService.getById(id));
    }

    @Operation(summary = "新增分类")
    @RequiresPermission(PermissionConstants.FINANCE_CATEGORY_CREATE)
    @PostMapping("/create")
    public Response<FinanceCategoryVo> create(@RequestBody @Valid FinanceCategoryCreateDto dto) {
        return Response.success(financeCategoryService.create(dto));
    }

    @Operation(summary = "更新分类")
    @RequiresPermission(PermissionConstants.FINANCE_CATEGORY_UPDATE)
    @PutMapping("/update")
    public Response<FinanceCategoryVo> update(@RequestBody @Valid FinanceCategoryUpdateDto dto) {
        return Response.success(financeCategoryService.update(dto));
    }

    @Operation(summary = "删除分类")
    @RequiresPermission(PermissionConstants.FINANCE_CATEGORY_DELETE)
    @DeleteMapping("/{id}")
    public Response<Void> delete(@PathVariable Long id) {
        financeCategoryService.delete(id);
        return Response.success();
    }

    @Operation(summary = "批量删除分类")
    @RequiresPermission(PermissionConstants.FINANCE_CATEGORY_DELETE)
    @PostMapping("/batch-delete")
    public Response<Void> batchDelete(@RequestBody @Valid FinanceCategoryBatchDeleteDto dto) {
        financeCategoryService.batchDelete(dto);
        return Response.success();
    }
}
