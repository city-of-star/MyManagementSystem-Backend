package com.mms.base.controller.income;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.income.dto.SideIncomeBatchDeleteDto;
import com.mms.base.common.income.dto.SideIncomeCreateDto;
import com.mms.base.common.income.dto.SideIncomePageQueryDto;
import com.mms.base.common.income.dto.SideIncomeUpdateDto;
import com.mms.base.common.income.vo.SideIncomeDailyStatVo;
import com.mms.base.common.income.vo.SideIncomeRecordVo;
import com.mms.base.common.income.vo.SideIncomeSummaryVo;
import com.mms.base.service.income.service.SideIncomeRecordService;
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
 * 实现功能【副业收入记录 Controller】
 *
 * @author li.hongyu
 * @date 2026-07-29
 */
@Tag(name = "副业收入", description = "副业/被动收入记账相关接口")
@RestController
@RequestMapping("/side-income")
public class SideIncomeRecordController {

    @Resource
    private SideIncomeRecordService sideIncomeRecordService;

    @Operation(summary = "分页查询收入记录")
    @RequiresPermission(PermissionConstants.SIDE_INCOME_RECORD_VIEW)
    @PostMapping("/page")
    public Response<Page<SideIncomeRecordVo>> page(@RequestBody @Valid SideIncomePageQueryDto dto) {
        return Response.success(sideIncomeRecordService.getSideIncomePage(dto));
    }

    @Operation(summary = "查询收入记录详情")
    @RequiresPermission(PermissionConstants.SIDE_INCOME_RECORD_VIEW)
    @GetMapping("/{id}")
    public Response<SideIncomeRecordVo> getById(@PathVariable Long id) {
        return Response.success(sideIncomeRecordService.getById(id));
    }

    @Operation(summary = "新增收入记录")
    @RequiresPermission(PermissionConstants.SIDE_INCOME_RECORD_CREATE)
    @PostMapping("/create")
    public Response<SideIncomeRecordVo> create(@RequestBody @Valid SideIncomeCreateDto dto) {
        return Response.success(sideIncomeRecordService.create(dto));
    }

    @Operation(summary = "更新收入记录")
    @RequiresPermission(PermissionConstants.SIDE_INCOME_RECORD_UPDATE)
    @PutMapping("/update")
    public Response<SideIncomeRecordVo> update(@RequestBody @Valid SideIncomeUpdateDto dto) {
        return Response.success(sideIncomeRecordService.update(dto));
    }

    @Operation(summary = "删除收入记录")
    @RequiresPermission(PermissionConstants.SIDE_INCOME_RECORD_DELETE)
    @DeleteMapping("/{id}")
    public Response<Void> delete(@PathVariable Long id) {
        sideIncomeRecordService.delete(id);
        return Response.success();
    }

    @Operation(summary = "批量删除收入记录")
    @RequiresPermission(PermissionConstants.SIDE_INCOME_RECORD_DELETE)
    @PostMapping("/batch-delete")
    public Response<Void> batchDelete(@RequestBody @Valid SideIncomeBatchDeleteDto dto) {
        sideIncomeRecordService.batchDelete(dto);
        return Response.success();
    }

    @Operation(summary = "收入汇总（今日/本月/待结算/累计）")
    @RequiresPermission(PermissionConstants.SIDE_INCOME_RECORD_VIEW)
    @GetMapping("/summary")
    public Response<SideIncomeSummaryVo> summary() {
        return Response.success(sideIncomeRecordService.getSummary());
    }

    @Operation(summary = "近 N 日收入趋势")
    @RequiresPermission(PermissionConstants.SIDE_INCOME_RECORD_VIEW)
    @GetMapping("/daily-stats")
    public Response<List<SideIncomeDailyStatVo>> dailyStats(@RequestParam(required = false, defaultValue = "30") Integer days) {
        return Response.success(sideIncomeRecordService.getDailyStats(days));
    }
}
