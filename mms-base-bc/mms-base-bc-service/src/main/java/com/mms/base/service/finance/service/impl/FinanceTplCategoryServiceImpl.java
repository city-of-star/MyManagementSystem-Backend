package com.mms.base.service.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.finance.dto.FinanceTplCategoryCreateDto;
import com.mms.base.common.finance.dto.FinanceTplCategoryPageQueryDto;
import com.mms.base.common.finance.dto.FinanceTplCategoryUpdateDto;
import com.mms.base.common.finance.entity.FinanceTplCategoryEntity;
import com.mms.base.common.finance.vo.FinanceTplCategoryVo;
import com.mms.base.service.finance.mapper.FinanceTplCategoryMapper;
import com.mms.base.service.finance.service.FinanceTplCategoryService;
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
 * 实现功能【记账初始化模板-分类服务实现】
 *
 * @author li.hongyu
 * @date 2026-07-31
 */
@Slf4j
@Service
public class FinanceTplCategoryServiceImpl implements FinanceTplCategoryService {

    private static final Set<String> DIRECTIONS = Set.of("income", "expense");

    @Resource
    private FinanceTplCategoryMapper financeTplCategoryMapper;

    @Override
    public Page<FinanceTplCategoryVo> getPage(FinanceTplCategoryPageQueryDto dto) {
        try {
            Page<FinanceTplCategoryEntity> page = new Page<>(dto.getPageNum(), dto.getPageSize());
            LambdaQueryWrapper<FinanceTplCategoryEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.like(StringUtils.hasText(dto.getName()), FinanceTplCategoryEntity::getName, dto.getName());
            wrapper.eq(StringUtils.hasText(dto.getDirection()), FinanceTplCategoryEntity::getDirection, dto.getDirection());
            wrapper.eq(dto.getEnabled() != null, FinanceTplCategoryEntity::getEnabled, dto.getEnabled());
            wrapper.orderByAsc(FinanceTplCategoryEntity::getSortOrder).orderByDesc(FinanceTplCategoryEntity::getId);
            Page<FinanceTplCategoryEntity> entityPage = financeTplCategoryMapper.selectPage(page, wrapper);
            Page<FinanceTplCategoryVo> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
            voPage.setRecords(entityPage.getRecords().stream().map(this::toVo).collect(Collectors.toList()));
            return voPage;
        } catch (Exception e) {
            log.error("分页查询记账分类模板失败：{}", e.getMessage(), e);
            throw new ServerException("查询记账分类模板失败", e);
        }
    }

    @Override
    public List<FinanceTplCategoryVo> list(String direction, Integer enabled) {
        try {
            LambdaQueryWrapper<FinanceTplCategoryEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(StringUtils.hasText(direction), FinanceTplCategoryEntity::getDirection, direction);
            wrapper.eq(enabled != null, FinanceTplCategoryEntity::getEnabled, enabled);
            wrapper.orderByAsc(FinanceTplCategoryEntity::getSortOrder).orderByDesc(FinanceTplCategoryEntity::getId);
            return financeTplCategoryMapper.selectList(wrapper).stream().map(this::toVo).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查询记账分类模板列表失败：{}", e.getMessage(), e);
            throw new ServerException("查询记账分类模板列表失败", e);
        }
    }

    @Override
    public FinanceTplCategoryVo getById(Long id) {
        return toVo(requireEntity(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinanceTplCategoryVo create(FinanceTplCategoryCreateDto dto) {
        try {
            validateDirection(dto.getDirection());
            FinanceTplCategoryEntity entity = new FinanceTplCategoryEntity();
            entity.setName(dto.getName());
            entity.setDirection(dto.getDirection());
            entity.setIcon(dto.getIcon());
            entity.setSortOrder(dto.getSortOrder() == null ? 0 : dto.getSortOrder());
            entity.setEnabled(dto.getEnabled() == null ? 1 : dto.getEnabled());
            financeTplCategoryMapper.insert(entity);
            return toVo(entity);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建记账分类模板失败：{}", e.getMessage(), e);
            throw new ServerException("创建记账分类模板失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinanceTplCategoryVo update(FinanceTplCategoryUpdateDto dto) {
        try {
            FinanceTplCategoryEntity entity = requireEntity(dto.getId());
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
            financeTplCategoryMapper.updateById(entity);
            return toVo(entity);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新记账分类模板失败：{}", e.getMessage(), e);
            throw new ServerException("更新记账分类模板失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        requireEntity(id);
        financeTplCategoryMapper.deleteById(id);
    }

    private void validateDirection(String direction) {
        if (!DIRECTIONS.contains(direction)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "方向不合法，仅支持 income/expense");
        }
    }

    private FinanceTplCategoryEntity requireEntity(Long id) {
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "模板ID不能为空");
        }
        FinanceTplCategoryEntity entity = financeTplCategoryMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "分类模板不存在");
        }
        return entity;
    }

    private FinanceTplCategoryVo toVo(FinanceTplCategoryEntity entity) {
        FinanceTplCategoryVo vo = new FinanceTplCategoryVo();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
