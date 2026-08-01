package com.mms.base.controller.finance;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.finance.dto.*;
import com.mms.base.common.finance.vo.FinanceFundHoldingVo;
import com.mms.base.common.finance.vo.FinanceFundNavSnapshotVo;
import com.mms.base.common.finance.vo.FinanceFundRedeemResultVo;
import com.mms.base.common.finance.vo.FinanceTransactionVo;
import com.mms.base.service.finance.service.FinanceFundHoldingService;
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
 * 实现功能【基金持仓 Controller】
 *
 * @author li.hongyu
 * @date 2026-07-31
 */
@Tag(name = "基金持仓", description = "个人记账基金持仓管理接口")
@RestController
@RequestMapping("/finance/fund-holding")
public class FinanceFundHoldingController {

    @Resource
    private FinanceFundHoldingService financeFundHoldingService;

    @Operation(summary = "分页查询基金持仓")
    @RequiresPermission(PermissionConstants.FINANCE_FUND_HOLDING_VIEW)
    @PostMapping("/page")
    public Response<Page<FinanceFundHoldingVo>> page(@RequestBody @Valid FinanceFundHoldingPageQueryDto dto) {
        return Response.success(financeFundHoldingService.getHoldingPage(dto));
    }

    @Operation(summary = "查询持仓详情")
    @RequiresPermission(PermissionConstants.FINANCE_FUND_HOLDING_VIEW)
    @GetMapping("/{id}")
    public Response<FinanceFundHoldingVo> getById(@PathVariable Long id) {
        return Response.success(financeFundHoldingService.getById(id));
    }

    @Operation(summary = "新建持仓")
    @RequiresPermission(PermissionConstants.FINANCE_FUND_HOLDING_CREATE)
    @PostMapping("/create")
    public Response<FinanceFundHoldingVo> create(@RequestBody @Valid FinanceFundHoldingCreateDto dto) {
        return Response.success(financeFundHoldingService.create(dto));
    }

    @Operation(summary = "更新持仓")
    @RequiresPermission(PermissionConstants.FINANCE_FUND_HOLDING_UPDATE)
    @PutMapping("/update")
    public Response<FinanceFundHoldingVo> update(@RequestBody @Valid FinanceFundHoldingUpdateDto dto) {
        return Response.success(financeFundHoldingService.update(dto));
    }

    @Operation(summary = "删除持仓")
    @RequiresPermission(PermissionConstants.FINANCE_FUND_HOLDING_DELETE)
    @DeleteMapping("/{id}")
    public Response<Void> delete(@PathVariable Long id) {
        financeFundHoldingService.delete(id);
        return Response.success();
    }

    @Operation(summary = "批量删除持仓")
    @RequiresPermission(PermissionConstants.FINANCE_FUND_HOLDING_DELETE)
    @PostMapping("/batch-delete")
    public Response<Void> batchDelete(@RequestBody @Valid FinanceFundHoldingBatchDeleteDto dto) {
        financeFundHoldingService.batchDelete(dto);
        return Response.success();
    }

    @Operation(summary = "申购（增份额，可选生成转账）")
    @RequiresPermission(PermissionConstants.FINANCE_FUND_HOLDING_UPDATE)
    @PostMapping("/purchase")
    public Response<FinanceFundHoldingVo> purchase(@RequestBody @Valid FinanceFundPurchaseDto dto) {
        return Response.success(financeFundHoldingService.purchase(dto));
    }

    @Operation(summary = "赎回（减份额 + pending 转账）")
    @RequiresPermission(PermissionConstants.FINANCE_FUND_HOLDING_UPDATE)
    @PostMapping("/redeem")
    public Response<FinanceFundRedeemResultVo> redeem(@RequestBody @Valid FinanceFundRedeemDto dto) {
        return Response.success(financeFundHoldingService.redeem(dto));
    }

    @Operation(summary = "确认赎回到账（pending → settled）")
    @RequiresPermission(PermissionConstants.FINANCE_FUND_HOLDING_UPDATE)
    @PostMapping("/settle-redeem")
    public Response<FinanceTransactionVo> settleRedeem(@RequestBody @Valid FinanceFundSettleRedeemDto dto) {
        return Response.success(financeFundHoldingService.settleRedeem(dto));
    }

    @Operation(summary = "手填估值")
    @RequiresPermission(PermissionConstants.FINANCE_FUND_HOLDING_UPDATE)
    @PostMapping("/valuation")
    public Response<FinanceFundHoldingVo> valuation(@RequestBody @Valid FinanceFundValuationDto dto) {
        return Response.success(financeFundHoldingService.updateValuation(dto));
    }

    @Operation(summary = "分页查询净值快照")
    @RequiresPermission(PermissionConstants.FINANCE_FUND_HOLDING_VIEW)
    @PostMapping("/snapshot/page")
    public Response<Page<FinanceFundNavSnapshotVo>> snapshotPage(
            @RequestBody @Valid FinanceFundNavSnapshotPageQueryDto dto) {
        return Response.success(financeFundHoldingService.getSnapshotPage(dto));
    }

    @Operation(summary = "净值快照列表（曲线用，按日期升序）")
    @RequiresPermission(PermissionConstants.FINANCE_FUND_HOLDING_VIEW)
    @GetMapping("/{holdingId}/snapshots")
    public Response<List<FinanceFundNavSnapshotVo>> listSnapshots(@PathVariable Long holdingId) {
        return Response.success(financeFundHoldingService.listSnapshots(holdingId));
    }

    @Operation(summary = "新增净值快照")
    @RequiresPermission(PermissionConstants.FINANCE_FUND_HOLDING_UPDATE)
    @PostMapping("/snapshot/create")
    public Response<FinanceFundNavSnapshotVo> createSnapshot(
            @RequestBody @Valid FinanceFundNavSnapshotCreateDto dto) {
        return Response.success(financeFundHoldingService.createSnapshot(dto));
    }

    @Operation(summary = "更新净值快照")
    @RequiresPermission(PermissionConstants.FINANCE_FUND_HOLDING_UPDATE)
    @PutMapping("/snapshot/update")
    public Response<FinanceFundNavSnapshotVo> updateSnapshot(
            @RequestBody @Valid FinanceFundNavSnapshotUpdateDto dto) {
        return Response.success(financeFundHoldingService.updateSnapshot(dto));
    }

    @Operation(summary = "删除净值快照")
    @RequiresPermission(PermissionConstants.FINANCE_FUND_HOLDING_UPDATE)
    @DeleteMapping("/snapshot/{id}")
    public Response<Void> deleteSnapshot(@PathVariable Long id) {
        financeFundHoldingService.deleteSnapshot(id);
        return Response.success();
    }
}
