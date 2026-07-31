package com.mms.base.service.finance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.finance.dto.FinanceTplRecurringCreateDto;
import com.mms.base.common.finance.dto.FinanceTplRecurringPageQueryDto;
import com.mms.base.common.finance.dto.FinanceTplRecurringUpdateDto;
import com.mms.base.common.finance.vo.FinanceTplRecurringVo;

/**
 * 实现功能【记账初始化模板-快捷项服务】
 *
 * @author li.hongyu
 * @date 2026-07-31
 */
public interface FinanceTplRecurringService {

    Page<FinanceTplRecurringVo> getPage(FinanceTplRecurringPageQueryDto dto);

    FinanceTplRecurringVo getById(Long id);

    FinanceTplRecurringVo create(FinanceTplRecurringCreateDto dto);

    FinanceTplRecurringVo update(FinanceTplRecurringUpdateDto dto);

    void delete(Long id);
}
