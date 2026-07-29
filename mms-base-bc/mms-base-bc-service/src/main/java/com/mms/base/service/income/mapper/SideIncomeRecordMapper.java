package com.mms.base.service.income.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.income.dto.SideIncomePageQueryDto;
import com.mms.base.common.income.entity.SideIncomeRecordEntity;
import com.mms.base.common.income.vo.SideIncomeDailyStatVo;
import com.mms.base.common.income.vo.SideIncomeRecordVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 实现功能【副业收入记录 Mapper】
 *
 * @author li.hongyu
 * @date 2026-07-29
 */
@Mapper
public interface SideIncomeRecordMapper extends BaseMapper<SideIncomeRecordEntity> {

    /**
     * 分页查询副业收入记录
     */
    Page<SideIncomeRecordVo> getSideIncomePage(Page<SideIncomeRecordVo> page, @Param("dto") SideIncomePageQueryDto dto);

    /**
     * 按日统计收入（已到账 / 待结算）
     */
    List<SideIncomeDailyStatVo> listDailyStats(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
