package com.mms.base.service.finance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.finance.dto.FinanceAccountBatchDeleteDto;
import com.mms.base.common.finance.dto.FinanceAccountCreateDto;
import com.mms.base.common.finance.dto.FinanceAccountPageQueryDto;
import com.mms.base.common.finance.dto.FinanceAccountUpdateDto;
import com.mms.base.common.finance.vo.FinanceAccountVo;

import java.util.List;

/**
 * 实现功能【记账账户服务】
 *
 * @author li.hongyu
 * @date 2026-07-30
 */
public interface FinanceAccountService {

    Page<FinanceAccountVo> getAccountPage(FinanceAccountPageQueryDto dto);

    List<FinanceAccountVo> listAccounts(Integer enabled);

    FinanceAccountVo getById(Long id);

    FinanceAccountVo create(FinanceAccountCreateDto dto);

    FinanceAccountVo update(FinanceAccountUpdateDto dto);

    void delete(Long id);

    void batchDelete(FinanceAccountBatchDeleteDto dto);
}
