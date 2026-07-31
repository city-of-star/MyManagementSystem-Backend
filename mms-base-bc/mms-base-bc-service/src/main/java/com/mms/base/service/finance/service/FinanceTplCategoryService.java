package com.mms.base.service.finance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.finance.dto.FinanceTplCategoryCreateDto;
import com.mms.base.common.finance.dto.FinanceTplCategoryPageQueryDto;
import com.mms.base.common.finance.dto.FinanceTplCategoryUpdateDto;
import com.mms.base.common.finance.vo.FinanceTplCategoryVo;

import java.util.List;

/**
 * 实现功能【记账初始化模板-分类服务】
 *
 * @author li.hongyu
 * @date 2026-07-31
 */
public interface FinanceTplCategoryService {

    Page<FinanceTplCategoryVo> getPage(FinanceTplCategoryPageQueryDto dto);

    List<FinanceTplCategoryVo> list(String direction, Integer enabled);

    FinanceTplCategoryVo getById(Long id);

    FinanceTplCategoryVo create(FinanceTplCategoryCreateDto dto);

    FinanceTplCategoryVo update(FinanceTplCategoryUpdateDto dto);

    void delete(Long id);
}
