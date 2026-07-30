package com.mms.base.service.finance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.finance.dto.FinanceRecurringBatchDeleteDto;
import com.mms.base.common.finance.dto.FinanceRecurringCreateDto;
import com.mms.base.common.finance.dto.FinanceRecurringPageQueryDto;
import com.mms.base.common.finance.dto.FinanceRecurringUpdateDto;
import com.mms.base.common.finance.vo.FinanceRecurringVo;

/**
 * 实现功能【周期记账模板服务】
 *
 * @author li.hongyu
 * @date 2026-07-30
 */
public interface FinanceRecurringService {

    Page<FinanceRecurringVo> getRecurringPage(FinanceRecurringPageQueryDto dto);

    FinanceRecurringVo getById(Long id);

    FinanceRecurringVo create(FinanceRecurringCreateDto dto);

    FinanceRecurringVo update(FinanceRecurringUpdateDto dto);

    void delete(Long id);

    void batchDelete(FinanceRecurringBatchDeleteDto dto);
}
