package com.mms.base.service.finance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.finance.dto.FinanceTransactionBatchDeleteDto;
import com.mms.base.common.finance.dto.FinanceTransactionCreateDto;
import com.mms.base.common.finance.dto.FinanceTransactionFromRecurringDto;
import com.mms.base.common.finance.dto.FinanceTransactionPageQueryDto;
import com.mms.base.common.finance.dto.FinanceTransactionUpdateDto;
import com.mms.base.common.finance.vo.FinanceTransactionVo;

/**
 * 实现功能【记账流水服务】
 *
 * @author li.hongyu
 * @date 2026-07-30
 */
public interface FinanceTransactionService {

    Page<FinanceTransactionVo> getTransactionPage(FinanceTransactionPageQueryDto dto);

    FinanceTransactionVo getById(Long id);

    FinanceTransactionVo create(FinanceTransactionCreateDto dto);

    FinanceTransactionVo update(FinanceTransactionUpdateDto dto);

    void delete(Long id);

    void batchDelete(FinanceTransactionBatchDeleteDto dto);

    FinanceTransactionVo createFromRecurring(FinanceTransactionFromRecurringDto dto);
}
