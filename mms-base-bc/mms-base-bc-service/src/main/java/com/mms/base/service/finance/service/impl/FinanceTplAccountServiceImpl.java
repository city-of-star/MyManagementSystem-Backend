package com.mms.base.service.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.finance.dto.FinanceTplAccountCreateDto;
import com.mms.base.common.finance.dto.FinanceTplAccountPageQueryDto;
import com.mms.base.common.finance.dto.FinanceTplAccountUpdateDto;
import com.mms.base.common.finance.entity.FinanceTplAccountEntity;
import com.mms.base.common.finance.vo.FinanceTplAccountVo;
import com.mms.base.service.finance.mapper.FinanceTplAccountMapper;
import com.mms.base.service.finance.service.FinanceTplAccountService;
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
import java.util.stream.Collectors;

/**
 * 实现功能【记账初始化模板-账户服务实现】
 *
 * @author li.hongyu
 * @date 2026-07-31
 */
@Slf4j
@Service
public class FinanceTplAccountServiceImpl implements FinanceTplAccountService {

    @Resource
    private FinanceTplAccountMapper financeTplAccountMapper;

    @Override
    public Page<FinanceTplAccountVo> getPage(FinanceTplAccountPageQueryDto dto) {
        try {
            Page<FinanceTplAccountEntity> page = new Page<>(dto.getPageNum(), dto.getPageSize());
            LambdaQueryWrapper<FinanceTplAccountEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.like(StringUtils.hasText(dto.getName()), FinanceTplAccountEntity::getName, dto.getName());
            wrapper.eq(StringUtils.hasText(dto.getAccountType()), FinanceTplAccountEntity::getAccountType, dto.getAccountType());
            wrapper.eq(dto.getEnabled() != null, FinanceTplAccountEntity::getEnabled, dto.getEnabled());
            wrapper.orderByAsc(FinanceTplAccountEntity::getSortOrder).orderByDesc(FinanceTplAccountEntity::getId);
            Page<FinanceTplAccountEntity> entityPage = financeTplAccountMapper.selectPage(page, wrapper);
            Page<FinanceTplAccountVo> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
            voPage.setRecords(entityPage.getRecords().stream().map(this::toVo).collect(Collectors.toList()));
            return voPage;
        } catch (Exception e) {
            log.error("分页查询记账账户模板失败：{}", e.getMessage(), e);
            throw new ServerException("查询记账账户模板失败", e);
        }
    }

    @Override
    public List<FinanceTplAccountVo> list(Integer enabled) {
        try {
            LambdaQueryWrapper<FinanceTplAccountEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(enabled != null, FinanceTplAccountEntity::getEnabled, enabled);
            wrapper.orderByAsc(FinanceTplAccountEntity::getSortOrder).orderByDesc(FinanceTplAccountEntity::getId);
            return financeTplAccountMapper.selectList(wrapper).stream().map(this::toVo).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查询记账账户模板列表失败：{}", e.getMessage(), e);
            throw new ServerException("查询记账账户模板列表失败", e);
        }
    }

    @Override
    public FinanceTplAccountVo getById(Long id) {
        FinanceTplAccountEntity entity = requireEntity(id);
        return toVo(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinanceTplAccountVo create(FinanceTplAccountCreateDto dto) {
        try {
            FinanceTplAccountEntity entity = new FinanceTplAccountEntity();
            entity.setName(dto.getName());
            entity.setAccountType(dto.getAccountType());
            entity.setNote(dto.getNote());
            entity.setSortOrder(dto.getSortOrder() == null ? 0 : dto.getSortOrder());
            entity.setEnabled(dto.getEnabled() == null ? 1 : dto.getEnabled());
            financeTplAccountMapper.insert(entity);
            return toVo(entity);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建记账账户模板失败：{}", e.getMessage(), e);
            throw new ServerException("创建记账账户模板失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinanceTplAccountVo update(FinanceTplAccountUpdateDto dto) {
        try {
            FinanceTplAccountEntity entity = requireEntity(dto.getId());
            if (StringUtils.hasText(dto.getName())) {
                entity.setName(dto.getName());
            }
            if (StringUtils.hasText(dto.getAccountType())) {
                entity.setAccountType(dto.getAccountType());
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
            financeTplAccountMapper.updateById(entity);
            return toVo(entity);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新记账账户模板失败：{}", e.getMessage(), e);
            throw new ServerException("更新记账账户模板失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        requireEntity(id);
        financeTplAccountMapper.deleteById(id);
    }

    private FinanceTplAccountEntity requireEntity(Long id) {
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "模板ID不能为空");
        }
        FinanceTplAccountEntity entity = financeTplAccountMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "账户模板不存在");
        }
        return entity;
    }

    private FinanceTplAccountVo toVo(FinanceTplAccountEntity entity) {
        FinanceTplAccountVo vo = new FinanceTplAccountVo();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
