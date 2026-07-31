package com.mms.base.service.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.finance.dto.FinanceTransactionPageQueryDto;
import com.mms.base.common.finance.entity.FinanceTransactionEntity;
import com.mms.base.common.finance.vo.FinanceCategoryStatVo;
import com.mms.base.common.finance.vo.FinanceDailyTrendVo;
import com.mms.base.common.finance.vo.FinanceTransactionVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 实现功能【记账流水 Mapper】
 *
 * @author li.hongyu
 * @date 2026-07-30
 */
@Mapper
public interface FinanceTransactionMapper extends BaseMapper<FinanceTransactionEntity> {

    /**
     * 分页查询流水（关联账户/分类名称）
     */
    Page<FinanceTransactionVo> getTransactionPage(Page<FinanceTransactionVo> page,
                                                  @Param("dto") FinanceTransactionPageQueryDto dto,
                                                  @Param("userId") Long userId);

    /**
     * 按ID查询流水详情（关联名称）
     */
    FinanceTransactionVo getTransactionById(@Param("id") Long id, @Param("userId") Long userId);

    /**
     * 近 N 日收支趋势（仅 settled）
     */
    List<FinanceDailyTrendVo> listDailyTrends(@Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate,
                                              @Param("userId") Long userId);

    /**
     * 统计指定区间、类型、状态的金额合计
     */
    BigDecimal sumAmount(@Param("txnType") String txnType,
                         @Param("status") String status,
                         @Param("startDate") LocalDate startDate,
                         @Param("endDate") LocalDate endDate,
                         @Param("userId") Long userId);

    /**
     * 本月支出分类 TOP
     */
    List<FinanceCategoryStatVo> listExpenseCategoryStats(@Param("startDate") LocalDate startDate,
                                                         @Param("endDate") LocalDate endDate,
                                                         @Param("limit") Integer limit,
                                                         @Param("userId") Long userId);

    /**
     * 统计引用某账户的流水数量
     */
    long countByAccountId(@Param("accountId") Long accountId, @Param("userId") Long userId);

    /**
     * 统计引用某分类的流水数量
     */
    long countByCategoryId(@Param("categoryId") Long categoryId, @Param("userId") Long userId);
}
