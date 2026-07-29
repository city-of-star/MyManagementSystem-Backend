package com.mms.base.service.income.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.income.dto.SideIncomeBatchDeleteDto;
import com.mms.base.common.income.dto.SideIncomeCreateDto;
import com.mms.base.common.income.dto.SideIncomePageQueryDto;
import com.mms.base.common.income.dto.SideIncomeUpdateDto;
import com.mms.base.common.income.vo.SideIncomeDailyStatVo;
import com.mms.base.common.income.vo.SideIncomeRecordVo;
import com.mms.base.common.income.vo.SideIncomeSummaryVo;

import java.util.List;

/**
 * 实现功能【副业收入记录服务】
 *
 * @author li.hongyu
 * @date 2026-07-29
 */
public interface SideIncomeRecordService {

    Page<SideIncomeRecordVo> getSideIncomePage(SideIncomePageQueryDto dto);

    SideIncomeRecordVo getById(Long id);

    SideIncomeRecordVo create(SideIncomeCreateDto dto);

    SideIncomeRecordVo update(SideIncomeUpdateDto dto);

    void delete(Long id);

    void batchDelete(SideIncomeBatchDeleteDto dto);

    SideIncomeSummaryVo getSummary();

    List<SideIncomeDailyStatVo> getDailyStats(Integer days);
}
