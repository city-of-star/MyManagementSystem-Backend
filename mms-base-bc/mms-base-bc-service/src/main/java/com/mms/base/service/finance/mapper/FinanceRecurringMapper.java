package com.mms.base.service.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.finance.dto.FinanceRecurringPageQueryDto;
import com.mms.base.common.finance.entity.FinanceRecurringEntity;
import com.mms.base.common.finance.vo.FinanceRecurringVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 实现功能【周期记账模板 Mapper】
 *
 * @author li.hongyu
 * @date 2026-07-30
 */
@Mapper
public interface FinanceRecurringMapper extends BaseMapper<FinanceRecurringEntity> {

    /**
     * 分页查询周期模板（关联分类/账户名称）
     */
    Page<FinanceRecurringVo> getRecurringPage(Page<FinanceRecurringVo> page,
                                              @Param("dto") FinanceRecurringPageQueryDto dto,
                                              @Param("userId") Long userId);

    /**
     * 按ID查询模板详情（关联名称）
     */
    FinanceRecurringVo getRecurringById(@Param("id") Long id, @Param("userId") Long userId);

    /**
     * 统计引用某账户的快捷模板数（含转出/转入）
     */
    @Select("""
            SELECT COUNT(1)
            FROM finance_recurring r
            WHERE r.deleted = 0
              AND r.user_id = #{userId}
              AND (r.account_id = #{accountId}
                OR r.from_account_id = #{accountId}
                OR r.to_account_id = #{accountId})
            """)
    long countByAccountId(@Param("accountId") Long accountId, @Param("userId") Long userId);

    /**
     * 统计引用某分类的快捷模板数
     */
    @Select("""
            SELECT COUNT(1)
            FROM finance_recurring r
            WHERE r.deleted = 0
              AND r.user_id = #{userId}
              AND r.category_id = #{categoryId}
            """)
    long countByCategoryId(@Param("categoryId") Long categoryId, @Param("userId") Long userId);
}
