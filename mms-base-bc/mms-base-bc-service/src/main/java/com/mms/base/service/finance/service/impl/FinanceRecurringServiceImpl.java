package com.mms.base.service.finance.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.finance.dto.FinanceRecurringBatchDeleteDto;
import com.mms.base.common.finance.dto.FinanceRecurringCreateDto;
import com.mms.base.common.finance.dto.FinanceRecurringPageQueryDto;
import com.mms.base.common.finance.dto.FinanceRecurringUpdateDto;
import com.mms.base.common.finance.entity.FinanceAccountEntity;
import com.mms.base.common.finance.entity.FinanceCategoryEntity;
import com.mms.base.common.finance.entity.FinanceRecurringEntity;
import com.mms.base.common.finance.vo.FinanceRecurringVo;
import com.mms.base.service.finance.mapper.FinanceAccountMapper;
import com.mms.base.service.finance.mapper.FinanceCategoryMapper;
import com.mms.base.service.finance.mapper.FinanceRecurringMapper;
import com.mms.base.service.finance.service.FinanceRecurringService;
import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.core.exceptions.ServerException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

/**
 * 实现功能【周期记账模板服务实现类】
 *
 * @author li.hongyu
 * @date 2026-07-30
 */
@Slf4j
@Service
public class FinanceRecurringServiceImpl implements FinanceRecurringService {

    private static final Set<String> DIRECTIONS = Set.of("income", "expense");
    private static final Set<String> CYCLES = Set.of("daily", "weekly", "monthly");

    @Resource
    private FinanceRecurringMapper financeRecurringMapper;

    @Resource
    private FinanceAccountMapper financeAccountMapper;

    @Resource
    private FinanceCategoryMapper financeCategoryMapper;

    @Override
    public Page<FinanceRecurringVo> getRecurringPage(FinanceRecurringPageQueryDto dto) {
        try {
            log.info("分页查询周期模板，参数：{}", dto);
            Page<FinanceRecurringVo> page = new Page<>(dto.getPageNum(), dto.getPageSize());
            return financeRecurringMapper.getRecurringPage(page, dto);
        } catch (Exception e) {
            log.error("分页查询周期模板失败：{}", e.getMessage(), e);
            throw new ServerException("查询周期模板列表失败", e);
        }
    }

    @Override
    public FinanceRecurringVo getById(Long id) {
        try {
            if (id == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "模板ID不能为空");
            }
            FinanceRecurringVo vo = financeRecurringMapper.getRecurringById(id);
            if (vo == null) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "周期模板不存在");
            }
            return vo;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询周期模板详情失败：{}", e.getMessage(), e);
            throw new ServerException("查询周期模板详情失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinanceRecurringVo create(FinanceRecurringCreateDto dto) {
        try {
            log.info("创建周期模板，参数：{}", dto);
            validateDirectionAndCycle(dto.getDirection(), dto.getCycle());
            validateCycleFields(dto.getCycle(), dto.getDayOfMonth(), dto.getWeekday());
            ensureAccountExists(dto.getAccountId());
            ensureCategoryExists(dto.getCategoryId());

            FinanceRecurringEntity entity = new FinanceRecurringEntity();
            entity.setName(dto.getName());
            entity.setDirection(dto.getDirection());
            entity.setAmount(scaleMoney(dto.getAmount()));
            entity.setCategoryId(dto.getCategoryId());
            entity.setAccountId(dto.getAccountId());
            entity.setCycle(dto.getCycle());
            entity.setDayOfMonth(dto.getDayOfMonth());
            entity.setWeekday(dto.getWeekday());
            entity.setEnabled(dto.getEnabled() == null ? 1 : dto.getEnabled());
            entity.setNote(dto.getNote());
            financeRecurringMapper.insert(entity);
            return financeRecurringMapper.getRecurringById(entity.getId());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建周期模板失败：{}", e.getMessage(), e);
            throw new ServerException("创建周期模板失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinanceRecurringVo update(FinanceRecurringUpdateDto dto) {
        try {
            log.info("更新周期模板，参数：{}", dto);
            FinanceRecurringEntity entity = financeRecurringMapper.selectById(dto.getId());
            if (entity == null) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "周期模板不存在");
            }
            if (StringUtils.hasText(dto.getName())) {
                entity.setName(dto.getName());
            }
            if (StringUtils.hasText(dto.getDirection())) {
                validateDirectionAndCycle(dto.getDirection(), null);
                entity.setDirection(dto.getDirection());
            }
            if (dto.getAmount() != null) {
                entity.setAmount(scaleMoney(dto.getAmount()));
            }
            if (dto.getCategoryId() != null) {
                ensureCategoryExists(dto.getCategoryId());
                entity.setCategoryId(dto.getCategoryId());
            }
            if (dto.getAccountId() != null) {
                ensureAccountExists(dto.getAccountId());
                entity.setAccountId(dto.getAccountId());
            }
            if (StringUtils.hasText(dto.getCycle())) {
                validateDirectionAndCycle(null, dto.getCycle());
                entity.setCycle(dto.getCycle());
            }
            if (dto.getDayOfMonth() != null) {
                entity.setDayOfMonth(dto.getDayOfMonth());
            }
            if (dto.getWeekday() != null) {
                entity.setWeekday(dto.getWeekday());
            }
            if (dto.getEnabled() != null) {
                entity.setEnabled(dto.getEnabled());
            }
            if (dto.getNote() != null) {
                entity.setNote(dto.getNote());
            }
            validateCycleFields(entity.getCycle(), entity.getDayOfMonth(), entity.getWeekday());
            financeRecurringMapper.updateById(entity);
            return financeRecurringMapper.getRecurringById(entity.getId());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新周期模板失败：{}", e.getMessage(), e);
            throw new ServerException("更新周期模板失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        try {
            if (id == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "模板ID不能为空");
            }
            FinanceRecurringEntity entity = financeRecurringMapper.selectById(id);
            if (entity == null) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "周期模板不存在");
            }
            financeRecurringMapper.deleteById(id);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除周期模板失败：{}", e.getMessage(), e);
            throw new ServerException("删除周期模板失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(FinanceRecurringBatchDeleteDto dto) {
        try {
            if (dto.getIds() == null || dto.getIds().isEmpty()) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "模板ID列表不能为空");
            }
            for (Long id : dto.getIds()) {
                delete(id);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量删除周期模板失败：{}", e.getMessage(), e);
            throw new ServerException("批量删除周期模板失败", e);
        }
    }

    private void validateDirectionAndCycle(String direction, String cycle) {
        if (direction != null && !DIRECTIONS.contains(direction)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "方向不合法，仅支持 income/expense");
        }
        if (cycle != null && !CYCLES.contains(cycle)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "周期不合法，仅支持 daily/weekly/monthly");
        }
    }

    private void validateCycleFields(String cycle, Integer dayOfMonth, Integer weekday) {
        if ("monthly".equals(cycle) && dayOfMonth != null && (dayOfMonth < 1 || dayOfMonth > 31)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "每月日期须在 1-31 之间");
        }
        if ("weekly".equals(cycle) && weekday != null && (weekday < 1 || weekday > 7)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "星期须在 1-7 之间");
        }
    }

    private void ensureAccountExists(Long accountId) {
        FinanceAccountEntity account = financeAccountMapper.selectById(accountId);
        if (account == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "账户不存在");
        }
    }

    private void ensureCategoryExists(Long categoryId) {
        FinanceCategoryEntity category = financeCategoryMapper.selectById(categoryId);
        if (category == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "分类不存在");
        }
    }

    private BigDecimal scaleMoney(BigDecimal value) {
        if (value == null) {
            return null;
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
