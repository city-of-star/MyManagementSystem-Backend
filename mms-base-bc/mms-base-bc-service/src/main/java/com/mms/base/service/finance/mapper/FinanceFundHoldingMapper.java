package com.mms.base.service.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.finance.dto.FinanceFundHoldingPageQueryDto;
import com.mms.base.common.finance.entity.FinanceFundHoldingEntity;
import com.mms.base.common.finance.vo.FinanceFundAccountMarketVo;
import com.mms.base.common.finance.vo.FinanceFundHoldingSummaryVo;
import com.mms.base.common.finance.vo.FinanceFundHoldingVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 实现功能【基金持仓 Mapper】
 *
 * @author li.hongyu
 * @date 2026-07-31
 */
@Mapper
public interface FinanceFundHoldingMapper extends BaseMapper<FinanceFundHoldingEntity> {

    Page<FinanceFundHoldingVo> getHoldingPage(Page<FinanceFundHoldingVo> page,
                                              @Param("dto") FinanceFundHoldingPageQueryDto dto,
                                              @Param("userId") Long userId);

    FinanceFundHoldingVo getHoldingById(@Param("id") Long id, @Param("userId") Long userId);

    FinanceFundHoldingSummaryVo sumHoldings(@Param("userId") Long userId);

    List<FinanceFundAccountMarketVo> listConfirmedMarketByAccount(@Param("userId") Long userId);

    long countByAccountId(@Param("accountId") Long accountId, @Param("userId") Long userId);
}
