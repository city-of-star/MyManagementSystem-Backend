package com.mms.base.service.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mms.base.common.finance.entity.FinanceAccountEntity;
import com.mms.base.common.finance.entity.FinanceCategoryEntity;
import com.mms.base.common.finance.entity.FinanceRecurringEntity;
import com.mms.base.common.finance.entity.FinanceTplAccountEntity;
import com.mms.base.common.finance.entity.FinanceTplCategoryEntity;
import com.mms.base.common.finance.entity.FinanceTplRecurringEntity;
import com.mms.base.common.finance.entity.FinanceUserInitEntity;
import com.mms.base.service.finance.mapper.FinanceAccountMapper;
import com.mms.base.service.finance.mapper.FinanceCategoryMapper;
import com.mms.base.service.finance.mapper.FinanceRecurringMapper;
import com.mms.base.service.finance.mapper.FinanceTplAccountMapper;
import com.mms.base.service.finance.mapper.FinanceTplCategoryMapper;
import com.mms.base.service.finance.mapper.FinanceTplRecurringMapper;
import com.mms.base.service.finance.mapper.FinanceUserInitMapper;
import com.mms.base.service.finance.service.FinanceInitService;
import com.mms.base.service.finance.support.FinanceUserSupport;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.core.exceptions.ServerException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 实现功能【记账用户初始化服务实现】
 *
 * @author li.hongyu
 * @date 2026-07-31
 */
@Slf4j
@Service
public class FinanceInitServiceImpl implements FinanceInitService {

    @Resource
    private FinanceUserInitMapper financeUserInitMapper;

    @Resource
    private FinanceTplAccountMapper financeTplAccountMapper;

    @Resource
    private FinanceTplCategoryMapper financeTplCategoryMapper;

    @Resource
    private FinanceTplRecurringMapper financeTplRecurringMapper;

    @Resource
    private FinanceAccountMapper financeAccountMapper;

    @Resource
    private FinanceCategoryMapper financeCategoryMapper;

    @Resource
    private FinanceRecurringMapper financeRecurringMapper;

    @Override
    public boolean isInitialized() {
        Long userId = FinanceUserSupport.requireUserId();
        return financeUserInitMapper.selectById(userId) != null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void ensureInitialized() {
        try {
            Long userId = FinanceUserSupport.requireUserId();
            if (financeUserInitMapper.selectById(userId) != null) {
                return;
            }
            log.info("开始为用户初始化记账模板，userId={}", userId);

            Map<Long, Long> accountIdMap = copyAccounts(userId);
            Map<Long, Long> categoryIdMap = copyCategories(userId);
            copyRecurrings(userId, accountIdMap, categoryIdMap);

            FinanceUserInitEntity init = new FinanceUserInitEntity();
            init.setUserId(userId);
            init.setInitTime(LocalDateTime.now());
            financeUserInitMapper.insert(init);
            log.info("用户记账模板初始化完成，userId={}", userId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("初始化记账模板失败：{}", e.getMessage(), e);
            throw new ServerException("初始化记账模板失败", e);
        }
    }

    private Map<Long, Long> copyAccounts(Long userId) {
        List<FinanceTplAccountEntity> tpls = financeTplAccountMapper.selectList(
                new LambdaQueryWrapper<FinanceTplAccountEntity>()
                        .eq(FinanceTplAccountEntity::getEnabled, 1)
                        .orderByAsc(FinanceTplAccountEntity::getSortOrder)
                        .orderByAsc(FinanceTplAccountEntity::getId));
        Map<Long, Long> idMap = new HashMap<>();
        for (FinanceTplAccountEntity tpl : tpls) {
            FinanceAccountEntity entity = new FinanceAccountEntity();
            entity.setUserId(userId);
            entity.setName(tpl.getName());
            entity.setAccountType(tpl.getAccountType());
            entity.setOpeningBalance(BigDecimal.ZERO.setScale(2));
            entity.setAccountNo(null);
            entity.setNote(tpl.getNote());
            entity.setSortOrder(tpl.getSortOrder() == null ? 0 : tpl.getSortOrder());
            entity.setEnabled(tpl.getEnabled() == null ? 1 : tpl.getEnabled());
            financeAccountMapper.insert(entity);
            idMap.put(tpl.getId(), entity.getId());
        }
        return idMap;
    }

    private Map<Long, Long> copyCategories(Long userId) {
        List<FinanceTplCategoryEntity> tpls = financeTplCategoryMapper.selectList(
                new LambdaQueryWrapper<FinanceTplCategoryEntity>()
                        .eq(FinanceTplCategoryEntity::getEnabled, 1)
                        .orderByAsc(FinanceTplCategoryEntity::getSortOrder)
                        .orderByAsc(FinanceTplCategoryEntity::getId));
        Map<Long, Long> idMap = new HashMap<>();
        for (FinanceTplCategoryEntity tpl : tpls) {
            FinanceCategoryEntity entity = new FinanceCategoryEntity();
            entity.setUserId(userId);
            entity.setName(tpl.getName());
            entity.setDirection(tpl.getDirection());
            entity.setIcon(tpl.getIcon());
            entity.setSortOrder(tpl.getSortOrder() == null ? 0 : tpl.getSortOrder());
            entity.setEnabled(tpl.getEnabled() == null ? 1 : tpl.getEnabled());
            entity.setIsSystem(0);
            financeCategoryMapper.insert(entity);
            idMap.put(tpl.getId(), entity.getId());
        }
        return idMap;
    }

    private void copyRecurrings(Long userId, Map<Long, Long> accountIdMap, Map<Long, Long> categoryIdMap) {
        List<FinanceTplRecurringEntity> tpls = financeTplRecurringMapper.selectList(
                new LambdaQueryWrapper<FinanceTplRecurringEntity>()
                        .eq(FinanceTplRecurringEntity::getEnabled, 1)
                        .orderByAsc(FinanceTplRecurringEntity::getSortOrder)
                        .orderByAsc(FinanceTplRecurringEntity::getId));
        for (FinanceTplRecurringEntity tpl : tpls) {
            FinanceRecurringEntity entity = new FinanceRecurringEntity();
            entity.setUserId(userId);
            entity.setName(tpl.getName());
            entity.setDirection(tpl.getDirection());
            entity.setAmount(BigDecimal.ZERO.setScale(2));
            entity.setCategoryId(remap(categoryIdMap, tpl.getCategoryId()));
            entity.setAccountId(remap(accountIdMap, tpl.getAccountId()));
            entity.setFromAccountId(remap(accountIdMap, tpl.getFromAccountId()));
            entity.setToAccountId(remap(accountIdMap, tpl.getToAccountId()));
            entity.setSortOrder(tpl.getSortOrder() == null ? 0 : tpl.getSortOrder());
            entity.setEnabled(tpl.getEnabled() == null ? 1 : tpl.getEnabled());
            entity.setNote(tpl.getNote());
            financeRecurringMapper.insert(entity);
        }
    }

    private Long remap(Map<Long, Long> idMap, Long tplId) {
        if (tplId == null) {
            return null;
        }
        return idMap.get(tplId);
    }
}
