package com.mms.base.service.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.finance.dto.FinanceTplRecurringPageQueryDto;
import com.mms.base.common.finance.entity.FinanceTplRecurringEntity;
import com.mms.base.common.finance.vo.FinanceTplRecurringVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 实现功能【记账初始化模板-快捷项 Mapper】
 *
 * @author li.hongyu
 * @date 2026-07-31
 */
@Mapper
public interface FinanceTplRecurringMapper extends BaseMapper<FinanceTplRecurringEntity> {

    Page<FinanceTplRecurringVo> getRecurringPage(Page<FinanceTplRecurringVo> page,
                                                 @Param("dto") FinanceTplRecurringPageQueryDto dto);

    FinanceTplRecurringVo getRecurringById(@Param("id") Long id);
}
