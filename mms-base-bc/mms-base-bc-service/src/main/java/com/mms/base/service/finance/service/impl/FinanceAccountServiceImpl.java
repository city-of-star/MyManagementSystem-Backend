package com.mms.base.service.finance.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.finance.dto.FinanceAccountBatchDeleteDto;
import com.mms.base.common.finance.dto.FinanceAccountCreateDto;
import com.mms.base.common.finance.dto.FinanceAccountPageQueryDto;
import com.mms.base.common.finance.dto.FinanceAccountUpdateDto;
import com.mms.base.common.finance.entity.FinanceAccountEntity;
import com.mms.base.common.finance.vo.FinanceAccountVo;
import com.mms.base.common.system.vo.DictDataVo;
import com.mms.base.service.finance.mapper.FinanceAccountMapper;
import com.mms.base.service.finance.mapper.FinanceFundHoldingMapper;
import com.mms.base.service.finance.mapper.FinanceTransactionMapper;
import com.mms.base.service.finance.service.FinanceAccountService;
import com.mms.base.service.finance.support.FinanceUserSupport;
import com.mms.base.service.system.service.DictDataService;
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
import java.util.List;

/**
 * 实现功能【记账账户服务实现类】
 *
 * @author li.hongyu
 * @date 2026-07-30
 */
@Slf4j
@Service
public class FinanceAccountServiceImpl implements FinanceAccountService {

    private static final String DICT_ACCOUNT_TYPE = "finance_account_type";

    @Resource
    private FinanceAccountMapper financeAccountMapper;

    @Resource
    private FinanceTransactionMapper financeTransactionMapper;

    @Resource
    private FinanceFundHoldingMapper financeFundHoldingMapper;

    @Resource
    private DictDataService dictDataService;

    @Override
    public Page<FinanceAccountVo> getAccountPage(FinanceAccountPageQueryDto dto) {
        try {
            Long userId = FinanceUserSupport.requireUserId();
            log.info("分页查询记账账户，userId={}，参数：{}", userId, dto);
            Page<FinanceAccountVo> page = new Page<>(dto.getPageNum(), dto.getPageSize());
            return financeAccountMapper.getAccountPage(page, dto, userId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("分页查询记账账户失败：{}", e.getMessage(), e);
            throw new ServerException("查询记账账户列表失败", e);
        }
    }

    @Override
    public List<FinanceAccountVo> listAccounts(Integer enabled) {
        try {
            Long userId = FinanceUserSupport.requireUserId();
            return financeAccountMapper.listAccountsWithBalance(enabled, userId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询记账账户列表失败：{}", e.getMessage(), e);
            throw new ServerException("查询记账账户列表失败", e);
        }
    }

    @Override
    public FinanceAccountVo getById(Long id) {
        try {
            Long userId = FinanceUserSupport.requireUserId();
            if (id == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "账户ID不能为空");
            }
            FinanceAccountVo vo = financeAccountMapper.getAccountWithBalance(id, userId);
            if (vo == null) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "账户不存在");
            }
            return vo;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询记账账户详情失败：{}", e.getMessage(), e);
            throw new ServerException("查询记账账户详情失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinanceAccountVo create(FinanceAccountCreateDto dto) {
        try {
            Long userId = FinanceUserSupport.requireUserId();
            log.info("创建记账账户，userId={}，参数：{}", userId, dto);
            validateAccountType(dto.getAccountType());
            FinanceAccountEntity entity = new FinanceAccountEntity();
            entity.setUserId(userId);
            entity.setName(dto.getName());
            entity.setAccountType(dto.getAccountType());
            entity.setOpeningBalance(scaleMoney(dto.getOpeningBalance()));
            entity.setAccountNo(dto.getAccountNo());
            entity.setNote(dto.getNote());
            entity.setSortOrder(dto.getSortOrder() == null ? 0 : dto.getSortOrder());
            entity.setEnabled(dto.getEnabled() == null ? 1 : dto.getEnabled());
            financeAccountMapper.insert(entity);
            return financeAccountMapper.getAccountWithBalance(entity.getId(), userId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建记账账户失败：{}", e.getMessage(), e);
            throw new ServerException("创建记账账户失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinanceAccountVo update(FinanceAccountUpdateDto dto) {
        try {
            Long userId = FinanceUserSupport.requireUserId();
            log.info("更新记账账户，userId={}，参数：{}", userId, dto);
            FinanceAccountEntity entity = financeAccountMapper.selectById(dto.getId());
            if (entity == null) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "账户不存在");
            }
            FinanceUserSupport.requireOwned(entity.getUserId(), "账户不存在");
            if (StringUtils.hasText(dto.getName())) {
                entity.setName(dto.getName());
            }
            if (StringUtils.hasText(dto.getAccountType())) {
                validateAccountType(dto.getAccountType());
                entity.setAccountType(dto.getAccountType());
            }
            if (dto.getOpeningBalance() != null) {
                entity.setOpeningBalance(scaleMoney(dto.getOpeningBalance()));
            }
            if (dto.getAccountNo() != null) {
                entity.setAccountNo(dto.getAccountNo());
            }
            if (dto.getNote() != null) {
                entity.setNote(dto.getNote());
            }
            if (dto.getSortOrder() != null) {
                entity.setSortOrder(dto.getSortOrder());
            }
            if (dto.getEnabled() != null) {
                entity.setEnabled(dto.getEnabled());
            }
            financeAccountMapper.updateById(entity);
            return financeAccountMapper.getAccountWithBalance(entity.getId(), userId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新记账账户失败：{}", e.getMessage(), e);
            throw new ServerException("更新记账账户失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        try {
            Long userId = FinanceUserSupport.requireUserId();
            if (id == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "账户ID不能为空");
            }
            FinanceAccountEntity entity = financeAccountMapper.selectById(id);
            if (entity == null) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "账户不存在");
            }
            FinanceUserSupport.requireOwned(entity.getUserId(), "账户不存在");
            long refCount = financeTransactionMapper.countByAccountId(id, userId);
            if (refCount > 0) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "账户存在关联流水，无法删除");
            }
            long holdingCount = financeFundHoldingMapper.countByAccountId(id, userId);
            if (holdingCount > 0) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "账户存在关联基金持仓，无法删除");
            }
            financeAccountMapper.deleteById(id);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除记账账户失败：{}", e.getMessage(), e);
            throw new ServerException("删除记账账户失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(FinanceAccountBatchDeleteDto dto) {
        try {
            if (dto.getIds() == null || dto.getIds().isEmpty()) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "账户ID列表不能为空");
            }
            for (Long id : dto.getIds()) {
                delete(id);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量删除记账账户失败：{}", e.getMessage(), e);
            throw new ServerException("批量删除记账账户失败", e);
        }
    }

    private void validateAccountType(String accountType) {
        if (!StringUtils.hasText(accountType)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "账户类型不能为空");
        }
        List<DictDataVo> dictList = dictDataService.getDictDataListByTypeCode(DICT_ACCOUNT_TYPE);
        boolean valid = dictList != null && dictList.stream()
                .anyMatch(item -> accountType.equals(item.getDictValue()));
        if (!valid) {
            throw new BusinessException(ErrorCode.PARAM_INVALID,
                    "账户类型不合法，请在数据字典【finance_account_type】中维护");
        }
    }

    private BigDecimal scaleMoney(BigDecimal value) {
        if (value == null) {
            return null;
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
