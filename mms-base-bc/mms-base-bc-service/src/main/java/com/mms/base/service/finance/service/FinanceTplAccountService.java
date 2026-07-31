package com.mms.base.service.finance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.finance.dto.FinanceTplAccountCreateDto;
import com.mms.base.common.finance.dto.FinanceTplAccountPageQueryDto;
import com.mms.base.common.finance.dto.FinanceTplAccountUpdateDto;
import com.mms.base.common.finance.vo.FinanceTplAccountVo;

import java.util.List;

/**
 * 实现功能【记账初始化模板-账户服务】
 *
 * @author li.hongyu
 * @date 2026-07-31
 */
public interface FinanceTplAccountService {

    Page<FinanceTplAccountVo> getPage(FinanceTplAccountPageQueryDto dto);

    List<FinanceTplAccountVo> list(Integer enabled);

    FinanceTplAccountVo getById(Long id);

    FinanceTplAccountVo create(FinanceTplAccountCreateDto dto);

    FinanceTplAccountVo update(FinanceTplAccountUpdateDto dto);

    void delete(Long id);
}
