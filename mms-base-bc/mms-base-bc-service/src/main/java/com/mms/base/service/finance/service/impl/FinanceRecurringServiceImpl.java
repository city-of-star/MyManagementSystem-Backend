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
import java.util.Objects;
import java.util.Set;

/**
 * 实现功能【快捷记账模板服务实现类】
 *
 * @author li.hongyu
 * @date 2026-07-30
 */
@Slf4j
@Service
public class FinanceRecurringServiceImpl implements FinanceRecurringService {

    private static final Set<String> DIRECTIONS = Set.of("income", "expense", "transfer");

    @Resource
    private FinanceRecurringMapper financeRecurringMapper;

    @Resource
    private FinanceAccountMapper financeAccountMapper;

    @Resource
    private FinanceCategoryMapper financeCategoryMapper;

    @Override
    public Page<FinanceRecurringVo> getRecurringPage(FinanceRecurringPageQueryDto dto) {
        try {
            log.info("分页查询快捷模板，参数：{}", dto);
            Page<FinanceRecurringVo> page = new Page<>(dto.getPageNum(), dto.getPageSize());
            return financeRecurringMapper.getRecurringPage(page, dto);
        } catch (Exception e) {
            log.error("分页查询快捷模板失败：{}", e.getMessage(), e);
            throw new ServerException("查询快捷模板列表失败", e);
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
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "快捷模板不存在");
            }
            return vo;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询快捷模板详情失败：{}", e.getMessage(), e);
            throw new ServerException("查询快捷模板详情失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinanceRecurringVo create(FinanceRecurringCreateDto dto) {
        try {
            log.info("创建快捷模板，参数：{}", dto);
            validateDirection(dto.getDirection());
            validateDirectionFields(dto.getDirection(), dto.getCategoryId(), dto.getAccountId(),
                    dto.getFromAccountId(), dto.getToAccountId());
            ensureAccountExists(dto.getAccountId());
            ensureAccountExists(dto.getFromAccountId());
            ensureAccountExists(dto.getToAccountId());
            ensureCategoryExists(dto.getCategoryId());

            FinanceRecurringEntity entity = new FinanceRecurringEntity();
            entity.setName(dto.getName());
            entity.setDirection(dto.getDirection());
            entity.setAmount(scaleMoney(dto.getAmount()));
            entity.setCategoryId(dto.getCategoryId());
            entity.setAccountId(dto.getAccountId());
            entity.setFromAccountId(dto.getFromAccountId());
            entity.setToAccountId(dto.getToAccountId());
            entity.setCycle(null);
            entity.setDayOfMonth(null);
            entity.setWeekday(null);
            entity.setSortOrder(dto.getSortOrder() == null ? 0 : dto.getSortOrder());
            entity.setEnabled(dto.getEnabled() == null ? 1 : dto.getEnabled());
            entity.setNote(dto.getNote());
            normalizeByDirection(entity);
            financeRecurringMapper.insert(entity);
            return financeRecurringMapper.getRecurringById(entity.getId());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建快捷模板失败：{}", e.getMessage(), e);
            throw new ServerException("创建快捷模板失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinanceRecurringVo update(FinanceRecurringUpdateDto dto) {
        try {
            log.info("更新快捷模板，参数：{}", dto);
            FinanceRecurringEntity entity = financeRecurringMapper.selectById(dto.getId());
            if (entity == null) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "快捷模板不存在");
            }
            if (StringUtils.hasText(dto.getName())) {
                entity.setName(dto.getName());
            }
            if (StringUtils.hasText(dto.getDirection())) {
                validateDirection(dto.getDirection());
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
            if (dto.getFromAccountId() != null) {
                ensureAccountExists(dto.getFromAccountId());
                entity.setFromAccountId(dto.getFromAccountId());
            }
            if (dto.getToAccountId() != null) {
                ensureAccountExists(dto.getToAccountId());
                entity.setToAccountId(dto.getToAccountId());
            }
            if (dto.getSortOrder() != null) {
                entity.setSortOrder(dto.getSortOrder());
            }
            if (dto.getEnabled() != null) {
                entity.setEnabled(dto.getEnabled());
            }
            if (dto.getNote() != null) {
                entity.setNote(dto.getNote());
            }
            // 提醒字段已废弃：读写忽略并清空
            entity.setCycle(null);
            entity.setDayOfMonth(null);
            entity.setWeekday(null);
            validateDirectionFields(entity.getDirection(), entity.getCategoryId(), entity.getAccountId(),
                    entity.getFromAccountId(), entity.getToAccountId());
            normalizeByDirection(entity);
            financeRecurringMapper.updateById(entity);
            return financeRecurringMapper.getRecurringById(entity.getId());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新快捷模板失败：{}", e.getMessage(), e);
            throw new ServerException("更新快捷模板失败", e);
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
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "快捷模板不存在");
            }
            financeRecurringMapper.deleteById(id);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除快捷模板失败：{}", e.getMessage(), e);
            throw new ServerException("删除快捷模板失败", e);
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
            log.error("批量删除快捷模板失败：{}", e.getMessage(), e);
            throw new ServerException("批量删除快捷模板失败", e);
        }
    }

    private void validateDirection(String direction) {
        if (direction != null && !DIRECTIONS.contains(direction)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "方向不合法，仅支持 income/expense/transfer");
        }
    }

    private void validateDirectionFields(String direction, Long categoryId, Long accountId,
                                         Long fromAccountId, Long toAccountId) {
        if ("income".equals(direction) || "expense".equals(direction)) {
            if (categoryId == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "收入/支出模板必须指定分类");
            }
            if (accountId == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "收入/支出模板必须指定账户");
            }
        } else if ("transfer".equals(direction)) {
            if (fromAccountId == null || toAccountId == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "转账模板必须指定转出与转入账户");
            }
            if (Objects.equals(fromAccountId, toAccountId)) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "转出与转入账户不能相同");
            }
        }
    }

    private void normalizeByDirection(FinanceRecurringEntity entity) {
        if ("income".equals(entity.getDirection()) || "expense".equals(entity.getDirection())) {
            entity.setFromAccountId(null);
            entity.setToAccountId(null);
        } else if ("transfer".equals(entity.getDirection())) {
            entity.setAccountId(null);
            entity.setCategoryId(null);
        }
    }

    private void ensureAccountExists(Long accountId) {
        if (accountId == null) {
            return;
        }
        FinanceAccountEntity account = financeAccountMapper.selectById(accountId);
        if (account == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "账户不存在");
        }
    }

    private void ensureCategoryExists(Long categoryId) {
        if (categoryId == null) {
            return;
        }
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
