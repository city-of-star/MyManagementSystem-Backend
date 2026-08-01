package com.mms.base.service.finance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.finance.dto.*;
import com.mms.base.common.finance.vo.FinanceFundHoldingVo;
import com.mms.base.common.finance.vo.FinanceFundNavSnapshotVo;
import com.mms.base.common.finance.vo.FinanceFundRedeemResultVo;
import com.mms.base.common.finance.vo.FinanceTransactionVo;

import java.util.List;

/**
 * 实现功能【基金持仓服务】
 *
 * @author li.hongyu
 * @date 2026-07-31
 */
public interface FinanceFundHoldingService {

    Page<FinanceFundHoldingVo> getHoldingPage(FinanceFundHoldingPageQueryDto dto);

    FinanceFundHoldingVo getById(Long id);

    FinanceFundHoldingVo create(FinanceFundHoldingCreateDto dto);

    FinanceFundHoldingVo update(FinanceFundHoldingUpdateDto dto);

    void delete(Long id);

    void batchDelete(FinanceFundHoldingBatchDeleteDto dto);

    FinanceFundHoldingVo purchase(FinanceFundPurchaseDto dto);

    FinanceFundRedeemResultVo redeem(FinanceFundRedeemDto dto);

    FinanceTransactionVo settleRedeem(FinanceFundSettleRedeemDto dto);

    FinanceFundHoldingVo cancelRedeem(FinanceFundCancelRedeemDto dto);

    FinanceFundHoldingVo updateValuation(FinanceFundValuationDto dto);

    Page<FinanceFundNavSnapshotVo> getSnapshotPage(FinanceFundNavSnapshotPageQueryDto dto);

    List<FinanceFundNavSnapshotVo> listSnapshots(Long holdingId);

    FinanceFundNavSnapshotVo createSnapshot(FinanceFundNavSnapshotCreateDto dto);

    FinanceFundNavSnapshotVo updateSnapshot(FinanceFundNavSnapshotUpdateDto dto);

    void deleteSnapshot(Long id);
}
