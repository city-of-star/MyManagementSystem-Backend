package com.mms.base.service.finance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.finance.dto.FinanceCategoryBatchDeleteDto;
import com.mms.base.common.finance.dto.FinanceCategoryCreateDto;
import com.mms.base.common.finance.dto.FinanceCategoryListQueryDto;
import com.mms.base.common.finance.dto.FinanceCategoryPageQueryDto;
import com.mms.base.common.finance.dto.FinanceCategoryUpdateDto;
import com.mms.base.common.finance.vo.FinanceCategoryVo;

import java.util.List;

/**
 * 实现功能【记账分类服务】
 *
 * @author li.hongyu
 * @date 2026-07-30
 */
public interface FinanceCategoryService {

    Page<FinanceCategoryVo> getCategoryPage(FinanceCategoryPageQueryDto dto);

    List<FinanceCategoryVo> listCategories(FinanceCategoryListQueryDto dto);

    FinanceCategoryVo getById(Long id);

    FinanceCategoryVo create(FinanceCategoryCreateDto dto);

    FinanceCategoryVo update(FinanceCategoryUpdateDto dto);

    void delete(Long id);

    void batchDelete(FinanceCategoryBatchDeleteDto dto);
}
