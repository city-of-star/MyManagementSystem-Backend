package com.mms.base.service.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.core.exceptions.ServerException;
import com.mms.base.common.system.dto.*;
import com.mms.base.common.system.entity.DictDataEntity;
import com.mms.base.common.system.entity.DictTypeEntity;
import com.mms.base.common.system.vo.DictDataVo;
import com.mms.base.service.system.mapper.DictDataMapper;
import com.mms.base.service.system.mapper.DictTypeMapper;
import com.mms.base.service.system.service.DictDataService;
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
 * 实现功能【数据字典数据服务实现类】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2025-12-23 11:21:50
 */
@Slf4j
@Service
public class DictDataServiceImpl implements DictDataService {

    @Resource
    private DictDataMapper dictDataMapper;

    @Resource
    private DictTypeMapper dictTypeMapper;

    @Override
    public Page<DictDataVo> getDictDataPage(DictDataPageQueryDto dto) {
        try {
            log.info("分页查询数据字典数据列表，参数：{}", dto);
            Page<DictDataEntity> page = new Page<>(dto.getPageNum(), dto.getPageSize());
            LambdaQueryWrapper<DictDataEntity> wrapper = new LambdaQueryWrapper<>();
            if (dto.getDictTypeId() != null) {
                wrapper.eq(DictDataEntity::getDictTypeId, dto.getDictTypeId());
            }
            if (StringUtils.hasText(dto.getDictLabel())) {
                wrapper.like(DictDataEntity::getDictLabel, dto.getDictLabel());
            }
            if (StringUtils.hasText(dto.getDictValue())) {
                wrapper.like(DictDataEntity::getDictValue, dto.getDictValue());
            }
            if (dto.getStatus() != null) {
                wrapper.eq(DictDataEntity::getStatus, dto.getStatus());
            }
            if (dto.getCreateTimeStart() != null) {
                wrapper.ge(DictDataEntity::getCreateTime, dto.getCreateTimeStart());
            }
            if (dto.getCreateTimeEnd() != null) {
                wrapper.le(DictDataEntity::getCreateTime, dto.getCreateTimeEnd());
            }
            wrapper.eq(DictDataEntity::getDeleted, 0)
                    .orderByAsc(DictDataEntity::getDictSort)
                    .orderByDesc(DictDataEntity::getCreateTime);
            Page<DictDataEntity> entityPage = dictDataMapper.selectPage(page, wrapper);
            Page<DictDataVo> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
            voPage.setRecords(entityPage.getRecords().stream().map(this::convertToVo).collect(Collectors.toList()));
            return voPage;
        } catch (Exception e) {
            log.error("分页查询数据字典数据列表失败：{}", e.getMessage(), e);
            throw new ServerException("查询数据字典数据列表失败", e);
        }
    }

    @Override
    public DictDataVo getDictDataById(Long dictDataId) {
        try {
            log.info("根据ID查询数据字典数据，dictDataId：{}", dictDataId);
            if (dictDataId == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "字典数据ID不能为空");
            }
            DictDataEntity dictData = dictDataMapper.selectById(dictDataId);
            if (dictData == null || Objects.equals(dictData.getDeleted(), 1)) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "数据字典数据不存在");
            }
            return convertToVo(dictData);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("根据ID查询数据字典数据失败：{}", e.getMessage(), e);
            throw new ServerException("查询数据字典数据失败", e);
        }
    }

    @Override
    public List<DictDataVo> getDictDataListByTypeId(Long dictTypeId) {
        try {
            log.info("根据字典类型ID查询数据字典数据列表，dictTypeId：{}", dictTypeId);
            if (dictTypeId == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "字典类型ID不能为空");
            }
            LambdaQueryWrapper<DictDataEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DictDataEntity::getDictTypeId, dictTypeId)
                    .eq(DictDataEntity::getDeleted, 0)
                    .orderByAsc(DictDataEntity::getDictSort);
            List<DictDataEntity> list = dictDataMapper.selectList(wrapper);
            return list.stream().map(this::convertToVo).collect(Collectors.toList());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("根据字典类型ID查询数据字典数据列表失败：{}", e.getMessage(), e);
            throw new ServerException("查询数据字典数据列表失败", e);
        }
    }

    @Override
    public List<DictDataVo> getDictDataListByTypeCode(String dictTypeCode) {
        try {
            log.info("根据字典类型编码查询启用的数据字典数据列表，dictTypeCode：{}", dictTypeCode);
            if (!StringUtils.hasText(dictTypeCode)) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "字典类型编码不能为空");
            }
            // 先查询字典类型
            LambdaQueryWrapper<DictTypeEntity> typeWrapper = new LambdaQueryWrapper<>();
            typeWrapper.eq(DictTypeEntity::getDictTypeCode, dictTypeCode)
                    .eq(DictTypeEntity::getStatus, 1)
                    .eq(DictTypeEntity::getDeleted, 0);
            DictTypeEntity dictType = dictTypeMapper.selectOne(typeWrapper);
            if (dictType == null) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "数据字典类型不存在或已禁用");
            }
            // 查询启用的字典数据
            LambdaQueryWrapper<DictDataEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DictDataEntity::getDictTypeId, dictType.getId())
                    .eq(DictDataEntity::getStatus, 1)
                    .eq(DictDataEntity::getDeleted, 0)
                    .orderByAsc(DictDataEntity::getDictSort);
            List<DictDataEntity> list = dictDataMapper.selectList(wrapper);
            return list.stream().map(this::convertToVo).collect(Collectors.toList());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("根据字典类型编码查询数据字典数据列表失败：{}", e.getMessage(), e);
            throw new ServerException("查询数据字典数据列表失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DictDataVo createDictData(DictDataCreateDto dto) {
        try {
            log.info("创建数据字典数据，参数：{}", dto);
            // 检查字典类型是否存在
            DictTypeEntity dictType = dictTypeMapper.selectById(dto.getDictTypeId());
            if (dictType == null || Objects.equals(dictType.getDeleted(), 1)) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "字典类型不存在");
            }
            // 检查同一字典类型下字典值是否重复
            LambdaQueryWrapper<DictDataEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DictDataEntity::getDictTypeId, dto.getDictTypeId())
                    .eq(DictDataEntity::getDictValue, dto.getDictValue())
                    .eq(DictDataEntity::getDeleted, 0);
            if (dictDataMapper.selectCount(wrapper) > 0) {
                throw new BusinessException(ErrorCode.UNIQUE_CONSTRAINT_ERROR, "该字典类型下字典值已存在");
            }
            DictDataEntity entity = new DictDataEntity();
            entity.setDictTypeId(dto.getDictTypeId());
            entity.setDictLabel(dto.getDictLabel());
            entity.setDictValue(dto.getDictValue());
            entity.setDictSort(dto.getDictSort() == null ? 0 : dto.getDictSort());
            entity.setCssClass(dto.getCssClass());
            entity.setListClass(dto.getListClass());
            entity.setIsDefault(dto.getIsDefault() == null ? 0 : dto.getIsDefault());
            entity.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
            entity.setRemark(dto.getRemark());
            entity.setDeleted(0);
            dictDataMapper.insert(entity);
            return convertToVo(entity);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建数据字典数据失败：{}", e.getMessage(), e);
            throw new ServerException("创建数据字典数据失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DictDataVo updateDictData(DictDataUpdateDto dto) {
        try {
            log.info("更新数据字典数据，参数：{}", dto);
            DictDataEntity dictData = dictDataMapper.selectById(dto.getId());
            if (dictData == null || Objects.equals(dictData.getDeleted(), 1)) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "数据字典数据不存在");
            }
            if (dto.getDictTypeId() != null && !dto.getDictTypeId().equals(dictData.getDictTypeId())) {
                // 检查新的字典类型是否存在
                DictTypeEntity dictType = dictTypeMapper.selectById(dto.getDictTypeId());
                if (dictType == null || Objects.equals(dictType.getDeleted(), 1)) {
                    throw new BusinessException(ErrorCode.PARAM_INVALID, "字典类型不存在");
                }
                dictData.setDictTypeId(dto.getDictTypeId());
            }
            if (StringUtils.hasText(dto.getDictLabel())) {
                dictData.setDictLabel(dto.getDictLabel());
            }
            if (StringUtils.hasText(dto.getDictValue())) {
                // 检查同一字典类型下字典值是否重复
                LambdaQueryWrapper<DictDataEntity> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(DictDataEntity::getDictTypeId, dictData.getDictTypeId())
                        .eq(DictDataEntity::getDictValue, dto.getDictValue())
                        .ne(DictDataEntity::getId, dto.getId())
                        .eq(DictDataEntity::getDeleted, 0);
                if (dictDataMapper.selectCount(wrapper) > 0) {
                    throw new BusinessException(ErrorCode.UNIQUE_CONSTRAINT_ERROR, "该字典类型下字典值已存在");
                }
                dictData.setDictValue(dto.getDictValue());
            }
            if (dto.getDictSort() != null) {
                dictData.setDictSort(dto.getDictSort());
            }
            if (StringUtils.hasText(dto.getCssClass())) {
                dictData.setCssClass(dto.getCssClass());
            }
            if (StringUtils.hasText(dto.getListClass())) {
                dictData.setListClass(dto.getListClass());
            }
            if (dto.getIsDefault() != null) {
                if (dto.getIsDefault() != 0 && dto.getIsDefault() != 1) {
                    throw new BusinessException(ErrorCode.PARAM_INVALID, "是否默认值只能是0或1");
                }
                dictData.setIsDefault(dto.getIsDefault());
            }
            if (dto.getStatus() != null) {
                if (dto.getStatus() != 0 && dto.getStatus() != 1) {
                    throw new BusinessException(ErrorCode.PARAM_INVALID, "状态值只能是0或1");
                }
                dictData.setStatus(dto.getStatus());
            }
            if (StringUtils.hasText(dto.getRemark())) {
                dictData.setRemark(dto.getRemark());
            }
            dictDataMapper.updateById(dictData);
            return convertToVo(dictData);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新数据字典数据失败：{}", e.getMessage(), e);
            throw new ServerException("更新数据字典数据失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictData(Long dictDataId) {
        try {
            log.info("删除数据字典数据，dictDataId：{}", dictDataId);
            if (dictDataId == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "字典数据ID不能为空");
            }
            DictDataEntity dictData = dictDataMapper.selectById(dictDataId);
            if (dictData == null || Objects.equals(dictData.getDeleted(), 1)) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "数据字典数据不存在");
            }
            dictDataMapper.deleteById(dictDataId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除数据字典数据失败：{}", e.getMessage(), e);
            throw new ServerException("删除数据字典数据失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteDictData(DictDataBatchDeleteDto dto) {
        try {
            log.info("批量删除数据字典数据，dictDataIds：{}", dto.getDictDataIds());
            if (dto.getDictDataIds() == null || dto.getDictDataIds().isEmpty()) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "字典数据ID列表不能为空");
            }
            for (Long dictDataId : dto.getDictDataIds()) {
                deleteDictData(dictDataId);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量删除数据字典数据失败：{}", e.getMessage(), e);
            throw new ServerException("批量删除数据字典数据失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void switchDictDataStatus(DictDataStatusSwitchDto dto) {
        try {
            log.info("切换数据字典数据状态，dictDataId：{}，status：{}", dto.getDictDataId(), dto.getStatus());
            DictDataEntity dictData = dictDataMapper.selectById(dto.getDictDataId());
            if (dictData == null || Objects.equals(dictData.getDeleted(), 1)) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "数据字典数据不存在");
            }
            if (dto.getStatus() != 0 && dto.getStatus() != 1) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "状态值只能是0或1");
            }
            dictData.setStatus(dto.getStatus());
            dictData.setUpdateTime(LocalDateTime.now());
            dictDataMapper.updateById(dictData);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("切换数据字典数据状态失败：{}", e.getMessage(), e);
            throw new ServerException("切换数据字典数据状态失败", e);
        }
    }

    private DictDataVo convertToVo(DictDataEntity entity) {
        if (entity == null) {
            return null;
        }
        DictDataVo vo = new DictDataVo();
        BeanUtils.copyProperties(entity, vo);
        // 关联查询字典类型信息
        if (entity.getDictTypeId() != null) {
            DictTypeEntity dictType = dictTypeMapper.selectById(entity.getDictTypeId());
            if (dictType != null) {
                vo.setDictTypeCode(dictType.getDictTypeCode());
                vo.setDictTypeName(dictType.getDictTypeName());
            }
        }
        return vo;
    }
}
