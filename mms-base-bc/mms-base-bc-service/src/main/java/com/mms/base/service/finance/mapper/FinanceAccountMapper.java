package com.mms.base.service.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.finance.dto.FinanceAccountPageQueryDto;
import com.mms.base.common.finance.entity.FinanceAccountEntity;
import com.mms.base.common.finance.vo.FinanceAccountBalanceVo;
import com.mms.base.common.finance.vo.FinanceAccountVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 实现功能【记账账户 Mapper】
 *
 * @author li.hongyu
 * @date 2026-07-30
 */
@Mapper
public interface FinanceAccountMapper extends BaseMapper<FinanceAccountEntity> {

    /**
     * 分页查询账户（含余额）
     */
    Page<FinanceAccountVo> getAccountPage(Page<FinanceAccountVo> page,
                                          @Param("dto") FinanceAccountPageQueryDto dto,
                                          @Param("userId") Long userId);

    /**
     * 查询账户列表（含余额）
     */
    List<FinanceAccountVo> listAccountsWithBalance(@Param("enabled") Integer enabled,
                                                   @Param("userId") Long userId);

    /**
     * 查询账户余额简表
     */
    List<FinanceAccountBalanceVo> listAccountBalances(@Param("enabled") Integer enabled,
                                                      @Param("userId") Long userId);

    /**
     * 按ID查询账户（含余额）
     */
    FinanceAccountVo getAccountWithBalance(@Param("id") Long id, @Param("userId") Long userId);
}
