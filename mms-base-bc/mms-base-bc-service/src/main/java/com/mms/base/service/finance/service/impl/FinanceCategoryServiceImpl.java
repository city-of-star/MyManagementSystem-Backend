package com.mms.base.service.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.finance.dto.FinanceCategoryBatchDeleteDto;
import com.mms.base.common.finance.dto.FinanceCategoryCreateDto;
import com.mms.base.common.finance.dto.FinanceCategoryListQueryDto;
import com.mms.base.common.finance.dto.FinanceCategoryPageQueryDto;
import com.mms.base.common.finance.dto.FinanceCategoryUpdateDto;
import com.mms.base.common.finance.entity.FinanceCategoryEntity;
import com.mms.base.common.finance.vo.FinanceCategoryVo;
import com.mms.base.service.finance.mapper.FinanceCategoryMapper;
import com.mms.base.service.finance.mapper.FinanceTransactionMapper;
import com.mms.base.service.finance.service.FinanceCategoryService;
import com.mms.base.service.finance.support.FinanceUserSupport;
import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.core.exceptions.ServerException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 实现功能【记账分类服务实现类】
 *
 * @author li.hongyu
 * @date 2026-07-30
 */
@Slf4j
@Service
public class FinanceCategoryServiceImpl implements FinanceCategoryService {

    private static final Set<String> DIRECTIONS = Set.of("income", "expense");

    @Resource
    private FinanceCategoryMapper financeCategoryMapper;

    @Resource
    private FinanceTransactionMapper financeTransactionMapper;

    @Override
    public Page<FinanceCategoryVo> getCategoryPage(FinanceCategoryPageQueryDto dto) {
        try {
            Long userId = FinanceUserSupport.requireUserId();
            log.info("分页查询记账分类，userId={}，参数：{}", userId, dto);
            Page<FinanceCategoryEntity> page = new Page<>(dto.getPageNum(), dto.getPageSize());
            LambdaQueryWrapper<FinanceCategoryEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(FinanceCategoryEntity::getUserId, userId);
            wrapper.like(StringUtils.hasText(dto.getName()), FinanceCategoryEntity::getName, dto.getName());
            wrapper.eq(StringUtils.hasText(dto.getDirection()), FinanceCategoryEntity::getDirection, dto.getDirection());
            wrapper.eq(dto.getEnabled() != null, FinanceCategoryEntity::getEnabled, dto.getEnabled());
            wrapper.orderByAsc(FinanceCategoryEntity::getSortOrder).orderByDesc(FinanceCategoryEntity::getId);
            Page<FinanceCategoryEntity> entityPage = financeCategoryMapper.selectPage(page, wrapper);
            Page<FinanceCategoryVo> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
            voPage.setRecords(entityPage.getRecords().stream().map(this::convertToVo).collect(Collectors.toList()));
            return voPage;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("分页查询记账分类失败：{}", e.getMessage(), e);
            throw new ServerException("查询记账分类列表失败", e);
        }
    }

    @Override
    public List<FinanceCategoryVo> listCategories(FinanceCategoryListQueryDto dto) {
        try {
            Long userId = FinanceUserSupport.requireUserId();
            LambdaQueryWrapper<FinanceCategoryEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(FinanceCategoryEntity::getUserId, userId);
            if (dto != null) {
                wrapper.eq(StringUtils.hasText(dto.getDirection()), FinanceCategoryEntity::getDirection, dto.getDirection());
                wrapper.eq(dto.getEnabled() != null, FinanceCategoryEntity::getEnabled, dto.getEnabled());
            }
            wrapper.orderByAsc(FinanceCategoryEntity::getSortOrder).orderByDesc(FinanceCategoryEntity::getId);
            return financeCategoryMapper.selectList(wrapper).stream().map(this::convertToVo).collect(Collectors.toList());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询记账分类列表失败：{}", e.getMessage(), e);
            throw new ServerException("查询记账分类列表失败", e);
        }
    }

    @Override
    public FinanceCategoryVo getById(Long id) {
        try {
            if (id == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "分类ID不能为空");
            }
            FinanceCategoryEntity entity = financeCategoryMapper.selectById(id);
            if (entity == null) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "分类不存在");
            }
            FinanceUserSupport.requireOwned(entity.getUserId(), "分类不存在");
            return convertToVo(entity);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询记账分类详情失败：{}", e.getMessage(), e);
            throw new ServerException("查询记账分类详情失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinanceCategoryVo create(FinanceCategoryCreateDto dto) {
        try {
            Long userId = FinanceUserSupport.requireUserId();
            log.info("创建记账分类，userId={}，参数：{}", userId, dto);
            validateDirection(dto.getDirection());
            FinanceCategoryEntity entity = new FinanceCategoryEntity();
            entity.setUserId(userId);
            entity.setName(dto.getName());
            entity.setDirection(dto.getDirection());
            entity.setIcon(dto.getIcon());
            entity.setSortOrder(dto.getSortOrder() == null ? 0 : dto.getSortOrder());
            entity.setEnabled(dto.getEnabled() == null ? 1 : dto.getEnabled());
            entity.setIsSystem(0);
            financeCategoryMapper.insert(entity);
            return convertToVo(entity);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建记账分类失败：{}", e.getMessage(), e);
            throw new ServerException("创建记账分类失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinanceCategoryVo update(FinanceCategoryUpdateDto dto) {
        try {
            log.info("更新记账分类，参数：{}", dto);
            FinanceCategoryEntity entity = financeCategoryMapper.selectById(dto.getId());
            if (entity == null) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "分类不存在");
            }
            FinanceUserSupport.requireOwned(entity.getUserId(), "分类不存在");
            if (StringUtils.hasText(dto.getName())) {
                entity.setName(dto.getName());
            }
            if (StringUtils.hasText(dto.getDirection())) {
                validateDirection(dto.getDirection());
                entity.setDirection(dto.getDirection());
            }
            if (dto.getIcon() != null) {
                entity.setIcon(dto.getIcon());
            }
            if (dto.getSortOrder() != null) {
                entity.setSortOrder(dto.getSortOrder());
            }
            if (dto.getEnabled() != null) {
                entity.setEnabled(dto.getEnabled());
            }
            financeCategoryMapper.updateById(entity);
            return convertToVo(entity);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新记账分类失败：{}", e.getMessage(), e);
            throw new ServerException("更新记账分类失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        try {
            Long userId = FinanceUserSupport.requireUserId();
            if (id == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "分类ID不能为空");
            }
            FinanceCategoryEntity entity = financeCategoryMapper.selectById(id);
            if (entity == null) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "分类不存在");
            }
            FinanceUserSupport.requireOwned(entity.getUserId(), "分类不存在");
            long refCount = financeTransactionMapper.countByCategoryId(id, userId);
            if (refCount > 0) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "分类存在关联流水，无法删除");
            }
            financeCategoryMapper.deleteById(id);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除记账分类失败：{}", e.getMessage(), e);
            throw new ServerException("删除记账分类失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(FinanceCategoryBatchDeleteDto dto) {
        try {
            if (dto.getIds() == null || dto.getIds().isEmpty()) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "分类ID列表不能为空");
            }
            for (Long id : dto.getIds()) {
                delete(id);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量删除记账分类失败：{}", e.getMessage(), e);
            throw new ServerException("批量删除记账分类失败", e);
        }
    }

    private void validateDirection(String direction) {
        if (direction != null && !DIRECTIONS.contains(direction)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "方向不合法，仅支持 income/expense");
        }
    }

    private FinanceCategoryVo convertToVo(FinanceCategoryEntity entity) {
        if (entity == null) {
            return null;
        }
        FinanceCategoryVo vo = new FinanceCategoryVo();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
