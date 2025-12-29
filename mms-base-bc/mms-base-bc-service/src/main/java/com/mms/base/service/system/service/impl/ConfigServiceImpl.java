package com.mms.base.service.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.core.exceptions.ServerException;
import com.mms.base.common.system.dto.*;
import com.mms.base.common.system.entity.ConfigEntity;
import com.mms.base.common.system.vo.ConfigVo;
import com.mms.base.service.system.mapper.ConfigMapper;
import com.mms.base.service.system.service.ConfigService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 实现功能【系统配置服务实现类】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2025-12-23 11:21:50
 */
@Slf4j
@Service
public class ConfigServiceImpl implements ConfigService {

    @Resource
    private ConfigMapper configMapper;

    @Override
    public Page<ConfigVo> getConfigPage(ConfigPageQueryDto dto) {
        try {
            log.info("分页查询系统配置列表，参数：{}", dto);
            Page<ConfigEntity> page = new Page<>(dto.getPageNum(), dto.getPageSize());
            LambdaQueryWrapper<ConfigEntity> wrapper = new LambdaQueryWrapper<>();
            if (StringUtils.hasText(dto.getConfigKey())) {
                wrapper.like(ConfigEntity::getConfigKey, dto.getConfigKey());
            }
            if (StringUtils.hasText(dto.getConfigName())) {
                wrapper.like(ConfigEntity::getConfigName, dto.getConfigName());
            }
            if (StringUtils.hasText(dto.getConfigType())) {
                wrapper.eq(ConfigEntity::getConfigType, dto.getConfigType());
            }
            if (dto.getStatus() != null) {
                wrapper.eq(ConfigEntity::getStatus, dto.getStatus());
            }
            if (dto.getEditable() != null) {
                wrapper.eq(ConfigEntity::getEditable, dto.getEditable());
            }
            if (dto.getCreateTimeStart() != null) {
                wrapper.ge(ConfigEntity::getCreateTime, dto.getCreateTimeStart());
            }
            if (dto.getCreateTimeEnd() != null) {
                wrapper.le(ConfigEntity::getCreateTime, dto.getCreateTimeEnd());
            }
            wrapper.eq(ConfigEntity::getDeleted, 0)
                    .orderByDesc(ConfigEntity::getCreateTime);
            Page<ConfigEntity> entityPage = configMapper.selectPage(page, wrapper);
            Page<ConfigVo> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
            voPage.setRecords(entityPage.getRecords().stream().map(this::convertToVo).toList());
            return voPage;
        } catch (Exception e) {
            log.error("分页查询系统配置列表失败：{}", e.getMessage(), e);
            throw new ServerException("查询系统配置列表失败", e);
        }
    }

    @Override
    public ConfigVo getConfigById(Long configId) {
        try {
            log.info("根据ID查询系统配置，configId：{}", configId);
            if (configId == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "配置ID不能为空");
            }
            ConfigEntity config = configMapper.selectById(configId);
            if (config == null || Objects.equals(config.getDeleted(), 1)) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "系统配置不存在");
            }
            return convertToVo(config);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("根据ID查询系统配置失败：{}", e.getMessage(), e);
            throw new ServerException("查询系统配置失败", e);
        }
    }

    @Override
    public ConfigVo getConfigByKey(String configKey) {
        try {
            log.info("根据配置键查询系统配置，configKey：{}", configKey);
            if (!StringUtils.hasText(configKey)) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "配置键不能为空");
            }
            LambdaQueryWrapper<ConfigEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ConfigEntity::getConfigKey, configKey)
                    .eq(ConfigEntity::getDeleted, 0);
            ConfigEntity config = configMapper.selectOne(wrapper);
            if (config == null) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "系统配置不存在");
            }
            return convertToVo(config);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("根据配置键查询系统配置失败：{}", e.getMessage(), e);
            throw new ServerException("查询系统配置失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ConfigVo createConfig(ConfigCreateDto dto) {
        try {
            log.info("创建系统配置，参数：{}", dto);
            if (existsByConfigKey(dto.getConfigKey())) {
                throw new BusinessException(ErrorCode.UNIQUE_CONSTRAINT_ERROR, "配置键已存在");
            }
            ConfigEntity entity = new ConfigEntity();
            entity.setConfigKey(dto.getConfigKey());
            entity.setConfigValue(dto.getConfigValue());
            entity.setConfigType(dto.getConfigType() == null ? "string" : dto.getConfigType());
            entity.setConfigName(dto.getConfigName());
            entity.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
            entity.setEditable(dto.getEditable() == null ? 1 : dto.getEditable());
            entity.setRemark(dto.getRemark());
            entity.setDeleted(0);
            configMapper.insert(entity);
            return convertToVo(entity);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建系统配置失败：{}", e.getMessage(), e);
            throw new ServerException("创建系统配置失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ConfigVo updateConfig(ConfigUpdateDto dto) {
        try {
            log.info("更新系统配置，参数：{}", dto);
            ConfigEntity config = configMapper.selectById(dto.getId());
            if (config == null || Objects.equals(config.getDeleted(), 1)) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "系统配置不存在");
            }
            if (config.getEditable() != null && config.getEditable() == 0) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "系统配置不可编辑");
            }
            if (StringUtils.hasText(dto.getConfigKey()) && !dto.getConfigKey().equals(config.getConfigKey())) {
                if (existsByConfigKey(dto.getConfigKey())) {
                    throw new BusinessException(ErrorCode.UNIQUE_CONSTRAINT_ERROR, "配置键已存在");
                }
                config.setConfigKey(dto.getConfigKey());
            }
            if (StringUtils.hasText(dto.getConfigValue())) {
                config.setConfigValue(dto.getConfigValue());
            }
            if (StringUtils.hasText(dto.getConfigType())) {
                config.setConfigType(dto.getConfigType());
            }
            if (StringUtils.hasText(dto.getConfigName())) {
                config.setConfigName(dto.getConfigName());
            }
            if (dto.getStatus() != null) {
                if (dto.getStatus() != 0 && dto.getStatus() != 1) {
                    throw new BusinessException(ErrorCode.PARAM_INVALID, "状态值只能是0或1");
                }
                config.setStatus(dto.getStatus());
            }
            if (dto.getEditable() != null) {
                if (dto.getEditable() != 0 && dto.getEditable() != 1) {
                    throw new BusinessException(ErrorCode.PARAM_INVALID, "可编辑值只能是0或1");
                }
                config.setEditable(dto.getEditable());
            }
            if (StringUtils.hasText(dto.getRemark())) {
                config.setRemark(dto.getRemark());
            }
            configMapper.updateById(config);
            return convertToVo(config);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新系统配置失败：{}", e.getMessage(), e);
            throw new ServerException("更新系统配置失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteConfig(Long configId) {
        try {
            log.info("删除系统配置，configId：{}", configId);
            if (configId == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "配置ID不能为空");
            }
            ConfigEntity config = configMapper.selectById(configId);
            if (config == null || Objects.equals(config.getDeleted(), 1)) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "系统配置不存在");
            }
            if (config.getEditable() != null && config.getEditable() == 0) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "系统配置不可删除");
            }
            configMapper.deleteById(configId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除系统配置失败：{}", e.getMessage(), e);
            throw new ServerException("删除系统配置失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteConfig(ConfigBatchDeleteDto dto) {
        try {
            log.info("批量删除系统配置，configIds：{}", dto.getConfigIds());
            if (dto.getConfigIds() == null || dto.getConfigIds().isEmpty()) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "配置ID列表不能为空");
            }
            for (Long configId : dto.getConfigIds()) {
                deleteConfig(configId);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量删除系统配置失败：{}", e.getMessage(), e);
            throw new ServerException("批量删除系统配置失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void switchConfigStatus(ConfigStatusSwitchDto dto) {
        try {
            log.info("切换系统配置状态，configId：{}，status：{}", dto.getConfigId(), dto.getStatus());
            ConfigEntity config = configMapper.selectById(dto.getConfigId());
            if (config == null || Objects.equals(config.getDeleted(), 1)) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "系统配置不存在");
            }
            if (dto.getStatus() != 0 && dto.getStatus() != 1) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "状态值只能是0或1");
            }
            config.setStatus(dto.getStatus());
            config.setUpdateTime(LocalDateTime.now());
            configMapper.updateById(config);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("切换系统配置状态失败：{}", e.getMessage(), e);
            throw new ServerException("切换系统配置状态失败", e);
        }
    }

    @Override
    public boolean existsByConfigKey(String configKey) {
        if (!StringUtils.hasText(configKey)) {
            return false;
        }
        LambdaQueryWrapper<ConfigEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ConfigEntity::getConfigKey, configKey)
                .eq(ConfigEntity::getDeleted, 0);
        return configMapper.selectCount(wrapper) > 0;
    }

    private ConfigVo convertToVo(ConfigEntity entity) {
        if (entity == null) {
            return null;
        }
        ConfigVo vo = new ConfigVo();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
