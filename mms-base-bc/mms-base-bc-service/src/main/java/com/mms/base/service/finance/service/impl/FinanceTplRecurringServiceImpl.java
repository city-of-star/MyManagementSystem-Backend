package com.mms.base.service.finance.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.finance.dto.FinanceTplRecurringCreateDto;
import com.mms.base.common.finance.dto.FinanceTplRecurringPageQueryDto;
import com.mms.base.common.finance.dto.FinanceTplRecurringUpdateDto;
import com.mms.base.common.finance.entity.FinanceTplAccountEntity;
import com.mms.base.common.finance.entity.FinanceTplCategoryEntity;
import com.mms.base.common.finance.entity.FinanceTplRecurringEntity;
import com.mms.base.common.finance.vo.FinanceTplRecurringVo;
import com.mms.base.service.finance.mapper.FinanceTplAccountMapper;
import com.mms.base.service.finance.mapper.FinanceTplCategoryMapper;
import com.mms.base.service.finance.mapper.FinanceTplRecurringMapper;
import com.mms.base.service.finance.service.FinanceTplRecurringService;
import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.core.exceptions.ServerException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Set;

/**
 * 实现功能【记账初始化模板-快捷项服务实现】
 *
 * @author li.hongyu
 * @date 2026-07-31
 */
@Slf4j
@Service
public class FinanceTplRecurringServiceImpl implements FinanceTplRecurringService {

    private static final Set<String> DIRECTIONS = Set.of("income", "expense", "transfer");

    @Resource
    private FinanceTplRecurringMapper financeTplRecurringMapper;

    @Resource
    private FinanceTplAccountMapper financeTplAccountMapper;

    @Resource
    private FinanceTplCategoryMapper financeTplCategoryMapper;

    @Override
    public Page<FinanceTplRecurringVo> getPage(FinanceTplRecurringPageQueryDto dto) {
        try {
            Page<FinanceTplRecurringVo> page = new Page<>(dto.getPageNum(), dto.getPageSize());
            return financeTplRecurringMapper.getRecurringPage(page, dto);
        } catch (Exception e) {
            log.error("分页查询记账快捷项模板失败：{}", e.getMessage(), e);
            throw new ServerException("查询记账快捷项模板失败", e);
        }
    }

    @Override
    public FinanceTplRecurringVo getById(Long id) {
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "模板ID不能为空");
        }
        FinanceTplRecurringVo vo = financeTplRecurringMapper.getRecurringById(id);
        if (vo == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "快捷项模板不存在");
        }
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinanceTplRecurringVo create(FinanceTplRecurringCreateDto dto) {
        try {
            validateDirectionFields(dto.getDirection(), dto.getCategoryId(), dto.getAccountId(),
                    dto.getFromAccountId(), dto.getToAccountId());
            FinanceTplRecurringEntity entity = new FinanceTplRecurringEntity();
            entity.setName(dto.getName());
            entity.setDirection(dto.getDirection());
            entity.setCategoryId(dto.getCategoryId());
            entity.setAccountId(dto.getAccountId());
            entity.setFromAccountId(dto.getFromAccountId());
            entity.setToAccountId(dto.getToAccountId());
            entity.setSortOrder(dto.getSortOrder() == null ? 0 : dto.getSortOrder());
            entity.setEnabled(dto.getEnabled() == null ? 1 : dto.getEnabled());
            entity.setNote(dto.getNote());
            financeTplRecurringMapper.insert(entity);
            return financeTplRecurringMapper.getRecurringById(entity.getId());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建记账快捷项模板失败：{}", e.getMessage(), e);
            throw new ServerException("创建记账快捷项模板失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinanceTplRecurringVo update(FinanceTplRecurringUpdateDto dto) {
        try {
            FinanceTplRecurringEntity entity = financeTplRecurringMapper.selectById(dto.getId());
            if (entity == null) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "快捷项模板不存在");
            }
            if (StringUtils.hasText(dto.getName())) {
                entity.setName(dto.getName());
            }
            if (StringUtils.hasText(dto.getDirection())) {
                entity.setDirection(dto.getDirection());
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
            // 按最终方向整理账户/分类引用（转账清空收入支出字段，反之亦然）
            if ("transfer".equals(entity.getDirection())) {
                entity.setCategoryId(null);
                entity.setAccountId(null);
                if (dto.getFromAccountId() != null) {
                    entity.setFromAccountId(dto.getFromAccountId());
                }
                if (dto.getToAccountId() != null) {
                    entity.setToAccountId(dto.getToAccountId());
                }
            } else {
                entity.setFromAccountId(null);
                entity.setToAccountId(null);
                if (dto.getCategoryId() != null) {
                    entity.setCategoryId(dto.getCategoryId());
                }
                if (dto.getAccountId() != null) {
                    entity.setAccountId(dto.getAccountId());
                }
            }
            validateDirectionFields(entity.getDirection(), entity.getCategoryId(), entity.getAccountId(),
                    entity.getFromAccountId(), entity.getToAccountId());
            financeTplRecurringMapper.updateById(entity);
            return financeTplRecurringMapper.getRecurringById(entity.getId());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新记账快捷项模板失败：{}", e.getMessage(), e);
            throw new ServerException("更新记账快捷项模板失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "模板ID不能为空");
        }
        FinanceTplRecurringEntity entity = financeTplRecurringMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "快捷项模板不存在");
        }
        financeTplRecurringMapper.deleteById(id);
    }

    private void validateDirectionFields(String direction, Long categoryId, Long accountId,
                                         Long fromAccountId, Long toAccountId) {
        if (!StringUtils.hasText(direction) || !DIRECTIONS.contains(direction)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "方向不合法，仅支持 income/expense/transfer");
        }
        if ("transfer".equals(direction)) {
            if (fromAccountId == null || toAccountId == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "转账模板须指定转出/转入账户");
            }
            if (fromAccountId.equals(toAccountId)) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "转出与转入账户不能相同");
            }
            ensureAccount(fromAccountId);
            ensureAccount(toAccountId);
        } else {
            if (categoryId == null || accountId == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "收入/支出模板须指定分类与账户");
            }
            ensureCategory(categoryId, direction);
            ensureAccount(accountId);
        }
    }

    private void ensureAccount(Long id) {
        FinanceTplAccountEntity account = financeTplAccountMapper.selectById(id);
        if (account == null) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "模板账户不存在：" + id);
        }
    }

    private void ensureCategory(Long id, String direction) {
        FinanceTplCategoryEntity category = financeTplCategoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "模板分类不存在：" + id);
        }
        if (!direction.equals(category.getDirection())) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "模板分类方向与快捷项方向不一致");
        }
    }
}
