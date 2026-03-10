package com.mms.base.service.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.common.cache.constants.CacheKeyPrefix;
import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.core.exceptions.ServerException;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
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
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
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

    @Resource
    private CacheManager cacheManager;

    @Override
    public Page<DictDataVo> getDictDataPage(DictDataPageQueryDto dto) {
        try {
            log.info("分页查询数据字典数据列表，参数：{}", dto);
            Page<DictDataVo> page = new Page<>(dto.getPageNum(), dto.getPageSize());
            return dictDataMapper.getDictDataPage(page, dto);
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
            if (dictData == null) {
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
    @Cacheable(cacheNames = CacheKeyPrefix.BASE + "dict", key = "#dictTypeCode", unless = "#result == null || #result.isEmpty()")
    public List<DictDataVo> getDictDataListByTypeCode(String dictTypeCode) {
        try {
            log.info("根据字典类型编码查询启用的数据字典数据列表，dictTypeCode：{}", dictTypeCode);
            if (!StringUtils.hasText(dictTypeCode)) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "字典类型编码不能为空");
            }
            // 先查询字典类型
            LambdaQueryWrapper<DictTypeEntity> typeWrapper = new LambdaQueryWrapper<>();
            typeWrapper.eq(DictTypeEntity::getDictTypeCode, dictTypeCode)
                    .eq(DictTypeEntity::getStatus, 1);
            DictTypeEntity dictType = dictTypeMapper.selectOne(typeWrapper);
            if (dictType == null) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "数据字典类型不存在或已禁用");
            }
            // 查询启用的字典数据
            LambdaQueryWrapper<DictDataEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DictDataEntity::getDictTypeId, dictType.getId())
                    .eq(DictDataEntity::getStatus, 1)
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
            if (dictType == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "字典类型不存在");
            }
            // 检查同一字典类型下字典值是否重复
            LambdaQueryWrapper<DictDataEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DictDataEntity::getDictTypeId, dto.getDictTypeId())
                    .eq(DictDataEntity::getDictValue, dto.getDictValue());
            if (dictDataMapper.selectCount(wrapper) > 0) {
                throw new BusinessException(ErrorCode.UNIQUE_CONSTRAINT_ERROR, "该字典类型下字典值已存在");
            }
            DictDataEntity entity = new DictDataEntity();
            entity.setDictTypeId(dto.getDictTypeId());
            entity.setDictLabel(dto.getDictLabel());
            entity.setDictValue(dto.getDictValue());
            entity.setDictSort(dto.getDictSort() == null ? 0 : dto.getDictSort());
            entity.setIsDefault(dto.getIsDefault() == null ? 0 : dto.getIsDefault());
            entity.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
            entity.setRemark(dto.getRemark());
            dictDataMapper.insert(entity);
            // 清除缓存
            evictDictCacheByCode(dictType.getDictTypeCode());
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
            if (dictData == null) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "数据字典数据不存在");
            }
            if (StringUtils.hasText(dto.getDictLabel())) {
                dictData.setDictLabel(dto.getDictLabel());
            }
            if (StringUtils.hasText(dto.getDictValue())) {
                // 检查同一字典类型下字典值是否重复
                LambdaQueryWrapper<DictDataEntity> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(DictDataEntity::getDictTypeId, dictData.getDictTypeId())
                        .eq(DictDataEntity::getDictValue, dto.getDictValue())
                        .ne(DictDataEntity::getId, dto.getId());
                if (dictDataMapper.selectCount(wrapper) > 0) {
                    throw new BusinessException(ErrorCode.UNIQUE_CONSTRAINT_ERROR, "该字典类型下字典值已存在");
                }
                dictData.setDictValue(dto.getDictValue());
            }
            if (dto.getDictSort() != null) {
                dictData.setDictSort(dto.getDictSort());
            }
            if (dto.getIsDefault() != null) {
                dictData.setIsDefault(dto.getIsDefault());
            }
            if (dto.getStatus() != null) {
                dictData.setStatus(dto.getStatus());
            }
            if (StringUtils.hasText(dto.getRemark())) {
                dictData.setRemark(dto.getRemark());
            }
            dictDataMapper.updateById(dictData);
            // 清除缓存
            evictDictCacheById(dictData.getDictTypeId());
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
            if (dictData == null) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "数据字典数据不存在");
            }
            dictDataMapper.deleteById(dictDataId);
            // 清除缓存
            evictDictCacheById(dictData.getDictTypeId());
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
            if (CollectionUtils.isEmpty(dto.getDictDataIds())) {
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
            if (dictData == null) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "数据字典数据不存在");
            }
            dictData.setStatus(dto.getStatus());
            dictData.setUpdateTime(LocalDateTime.now());
            dictDataMapper.updateById(dictData);
            // 清除缓存
            evictDictCacheById(dictData.getDictTypeId());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("切换数据字典数据状态失败：{}", e.getMessage(), e);
            throw new ServerException("切换数据字典数据状态失败", e);
        }
    }

    /**
     * 根据字典类型编码清除缓存
     */
    private void evictDictCacheByCode(String dictTypeCode) {
        if (!StringUtils.hasText(dictTypeCode)) {
            return;
        }
        Cache cache = cacheManager.getCache(CacheKeyPrefix.BASE + "dict");
        if (cache == null) {
            return;
        }
        cache.evict(dictTypeCode);
    }

    /**
     * 根据字典类型ID清除缓存
     */
    private void evictDictCacheById(Long dictTypeId) {
        if (dictTypeId == null) {
            return;
        }
        DictTypeEntity dictType = dictTypeMapper.selectById(dictTypeId);
        if (dictType == null) {
            return;
        }
        evictDictCacheByCode(dictType.getDictTypeCode());
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
