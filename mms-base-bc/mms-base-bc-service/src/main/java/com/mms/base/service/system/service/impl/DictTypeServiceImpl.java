package com.mms.base.service.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.core.exceptions.ServerException;
import com.mms.base.common.system.dto.*;
import com.mms.base.common.system.entity.DictDataEntity;
import com.mms.base.common.system.entity.DictTypeEntity;
import com.mms.base.common.system.vo.DictTypeVo;
import com.mms.base.service.system.mapper.DictDataMapper;
import com.mms.base.service.system.mapper.DictTypeMapper;
import com.mms.base.service.system.service.DictTypeService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 实现功能【数据字典类型服务实现类】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2025-12-23 11:21:50
 */
@Slf4j
@Service
public class DictTypeServiceImpl implements DictTypeService {

    @Resource
    private DictTypeMapper dictTypeMapper;

    @Resource
    private DictDataMapper dictDataMapper;

    @Override
    public Page<DictTypeVo> getDictTypePage(DictTypePageQueryDto dto) {
        try {
            log.info("分页查询数据字典类型列表，参数：{}", dto);
            Page<DictTypeEntity> page = new Page<>(dto.getPageNum(), dto.getPageSize());
            LambdaQueryWrapper<DictTypeEntity> wrapper = new LambdaQueryWrapper<>();
            if (StringUtils.hasText(dto.getDictTypeCode())) {
                wrapper.like(DictTypeEntity::getDictTypeCode, dto.getDictTypeCode());
            }
            if (StringUtils.hasText(dto.getDictTypeName())) {
                wrapper.like(DictTypeEntity::getDictTypeName, dto.getDictTypeName());
            }
            if (dto.getStatus() != null) {
                wrapper.eq(DictTypeEntity::getStatus, dto.getStatus());
            }
            if (dto.getCreateTimeStart() != null) {
                wrapper.ge(DictTypeEntity::getCreateTime, dto.getCreateTimeStart());
            }
            if (dto.getCreateTimeEnd() != null) {
                wrapper.le(DictTypeEntity::getCreateTime, dto.getCreateTimeEnd());
            }
            wrapper.eq(DictTypeEntity::getDeleted, 0)
                    .orderByAsc(DictTypeEntity::getSortOrder)
                    .orderByDesc(DictTypeEntity::getCreateTime);
            Page<DictTypeEntity> entityPage = dictTypeMapper.selectPage(page, wrapper);
            Page<DictTypeVo> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
            voPage.setRecords(entityPage.getRecords().stream().map(this::convertToVo).collect(Collectors.toList()));
            return voPage;
        } catch (Exception e) {
            log.error("分页查询数据字典类型列表失败：{}", e.getMessage(), e);
            throw new ServerException("查询数据字典类型列表失败", e);
        }
    }

    @Override
    public DictTypeVo getDictTypeById(Long dictTypeId) {
        try {
            log.info("根据ID查询数据字典类型，dictTypeId：{}", dictTypeId);
            if (dictTypeId == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "字典类型ID不能为空");
            }
            DictTypeEntity dictType = dictTypeMapper.selectById(dictTypeId);
            if (dictType == null || Objects.equals(dictType.getDeleted(), 1)) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "数据字典类型不存在");
            }
            return convertToVo(dictType);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("根据ID查询数据字典类型失败：{}", e.getMessage(), e);
            throw new ServerException("查询数据字典类型失败", e);
        }
    }

    @Override
    public DictTypeVo getDictTypeByCode(String dictTypeCode) {
        try {
            log.info("根据字典类型编码查询数据字典类型，dictTypeCode：{}", dictTypeCode);
            if (!StringUtils.hasText(dictTypeCode)) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "字典类型编码不能为空");
            }
            LambdaQueryWrapper<DictTypeEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DictTypeEntity::getDictTypeCode, dictTypeCode)
                    .eq(DictTypeEntity::getDeleted, 0);
            DictTypeEntity dictType = dictTypeMapper.selectOne(wrapper);
            if (dictType == null) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "数据字典类型不存在");
            }
            return convertToVo(dictType);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("根据字典类型编码查询数据字典类型失败：{}", e.getMessage(), e);
            throw new ServerException("查询数据字典类型失败", e);
        }
    }

    @Override
    public List<DictTypeVo> listAllEnabledDictTypes() {
        try {
            log.info("查询所有启用的数据字典类型列表");
            LambdaQueryWrapper<DictTypeEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DictTypeEntity::getStatus, 1)
                    .eq(DictTypeEntity::getDeleted, 0)
                    .orderByAsc(DictTypeEntity::getSortOrder);
            List<DictTypeEntity> list = dictTypeMapper.selectList(wrapper);
            return list.stream().map(this::convertToVo).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查询所有启用的数据字典类型列表失败：{}", e.getMessage(), e);
            throw new ServerException("查询数据字典类型列表失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DictTypeVo createDictType(DictTypeCreateDto dto) {
        try {
            log.info("创建数据字典类型，参数：{}", dto);
            if (existsByDictTypeCode(dto.getDictTypeCode())) {
                throw new BusinessException(ErrorCode.UNIQUE_CONSTRAINT_ERROR, "字典类型编码已存在");
            }
            DictTypeEntity entity = new DictTypeEntity();
            entity.setDictTypeCode(dto.getDictTypeCode());
            entity.setDictTypeName(dto.getDictTypeName());
            entity.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
            entity.setSortOrder(dto.getSortOrder() == null ? 0 : dto.getSortOrder());
            entity.setRemark(dto.getRemark());
            entity.setDeleted(0);
            dictTypeMapper.insert(entity);
            return convertToVo(entity);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建数据字典类型失败：{}", e.getMessage(), e);
            throw new ServerException("创建数据字典类型失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DictTypeVo updateDictType(DictTypeUpdateDto dto) {
        try {
            log.info("更新数据字典类型，参数：{}", dto);
            DictTypeEntity dictType = dictTypeMapper.selectById(dto.getId());
            if (dictType == null || Objects.equals(dictType.getDeleted(), 1)) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "数据字典类型不存在");
            }
            if (StringUtils.hasText(dto.getDictTypeCode()) && !dto.getDictTypeCode().equals(dictType.getDictTypeCode())) {
                if (existsByDictTypeCode(dto.getDictTypeCode())) {
                    throw new BusinessException(ErrorCode.UNIQUE_CONSTRAINT_ERROR, "字典类型编码已存在");
                }
                dictType.setDictTypeCode(dto.getDictTypeCode());
            }
            if (StringUtils.hasText(dto.getDictTypeName())) {
                dictType.setDictTypeName(dto.getDictTypeName());
            }
            if (dto.getStatus() != null) {
                if (dto.getStatus() != 0 && dto.getStatus() != 1) {
                    throw new BusinessException(ErrorCode.PARAM_INVALID, "状态值只能是0或1");
                }
                dictType.setStatus(dto.getStatus());
            }
            if (dto.getSortOrder() != null) {
                dictType.setSortOrder(dto.getSortOrder());
            }
            if (StringUtils.hasText(dto.getRemark())) {
                dictType.setRemark(dto.getRemark());
            }
            dictTypeMapper.updateById(dictType);
            return convertToVo(dictType);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新数据字典类型失败：{}", e.getMessage(), e);
            throw new ServerException("更新数据字典类型失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictType(Long dictTypeId) {
        try {
            log.info("删除数据字典类型，dictTypeId：{}", dictTypeId);
            if (dictTypeId == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "字典类型ID不能为空");
            }
            DictTypeEntity dictType = dictTypeMapper.selectById(dictTypeId);
            if (dictType == null || Objects.equals(dictType.getDeleted(), 1)) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "数据字典类型不存在");
            }
            // 检查是否存在关联的字典数据
            LambdaQueryWrapper<DictDataEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DictDataEntity::getDictTypeId, dictTypeId)
                    .eq(DictDataEntity::getDeleted, 0);
            if (dictDataMapper.selectCount(wrapper) > 0) {
                throw new BusinessException(ErrorCode.DATA_IN_USE, "存在关联的字典数据，无法删除");
            }
            dictTypeMapper.deleteById(dictTypeId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除数据字典类型失败：{}", e.getMessage(), e);
            throw new ServerException("删除数据字典类型失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteDictType(DictTypeBatchDeleteDto dto) {
        try {
            log.info("批量删除数据字典类型，dictTypeIds：{}", dto.getDictTypeIds());
            if (dto.getDictTypeIds() == null || dto.getDictTypeIds().isEmpty()) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "字典类型ID列表不能为空");
            }
            for (Long dictTypeId : dto.getDictTypeIds()) {
                deleteDictType(dictTypeId);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量删除数据字典类型失败：{}", e.getMessage(), e);
            throw new ServerException("批量删除数据字典类型失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void switchDictTypeStatus(DictTypeStatusSwitchDto dto) {
        try {
            log.info("切换数据字典类型状态，dictTypeId：{}，status：{}", dto.getDictTypeId(), dto.getStatus());
            DictTypeEntity dictType = dictTypeMapper.selectById(dto.getDictTypeId());
            if (dictType == null || Objects.equals(dictType.getDeleted(), 1)) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "数据字典类型不存在");
            }
            if (dto.getStatus() != 0 && dto.getStatus() != 1) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "状态值只能是0或1");
            }
            dictType.setStatus(dto.getStatus());
            dictType.setUpdateTime(LocalDateTime.now());
            dictTypeMapper.updateById(dictType);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("切换数据字典类型状态失败：{}", e.getMessage(), e);
            throw new ServerException("切换数据字典类型状态失败", e);
        }
    }

    @Override
    public boolean existsByDictTypeCode(String dictTypeCode) {
        if (!StringUtils.hasText(dictTypeCode)) {
            return false;
        }
        LambdaQueryWrapper<DictTypeEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DictTypeEntity::getDictTypeCode, dictTypeCode)
                .eq(DictTypeEntity::getDeleted, 0);
        return dictTypeMapper.selectCount(wrapper) > 0;
    }

    private DictTypeVo convertToVo(DictTypeEntity entity) {
        if (entity == null) {
            return null;
        }
        DictTypeVo vo = new DictTypeVo();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
