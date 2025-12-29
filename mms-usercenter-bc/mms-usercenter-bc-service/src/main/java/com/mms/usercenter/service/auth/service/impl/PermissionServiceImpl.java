package com.mms.usercenter.service.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.core.exceptions.ServerException;
import com.mms.usercenter.common.auth.dto.PermissionBatchDeleteDto;
import com.mms.usercenter.common.auth.dto.PermissionCreateDto;
import com.mms.usercenter.common.auth.dto.PermissionPageQueryDto;
import com.mms.usercenter.common.auth.dto.PermissionStatusSwitchDto;
import com.mms.usercenter.common.auth.dto.PermissionUpdateDto;
import com.mms.usercenter.common.auth.entity.PermissionEntity;
import com.mms.usercenter.common.auth.entity.RolePermissionEntity;
import com.mms.usercenter.common.auth.vo.PermissionVo;
import com.mms.usercenter.service.auth.mapper.PermissionMapper;
import com.mms.usercenter.service.auth.mapper.RolePermissionMapper;
import com.mms.usercenter.service.auth.service.PermissionService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 实现功能【权限服务实现类】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2025-12-12 09:27:05
 */
@Slf4j
@Service
public class PermissionServiceImpl implements PermissionService {

    @Resource
    private PermissionMapper permissionMapper;

    @Resource
    private RolePermissionMapper rolePermissionMapper;

    @Override
    public Page<PermissionVo> getPermissionPage(PermissionPageQueryDto dto) {
        try {
            log.info("分页查询权限列表，参数：{}", dto);
            Page<PermissionEntity> page = new Page<>(dto.getPageNum(), dto.getPageSize());
            LambdaQueryWrapper<PermissionEntity> wrapper = new LambdaQueryWrapper<>();
            if (dto.getParentId() != null) {
                wrapper.eq(PermissionEntity::getParentId, dto.getParentId());
            }
            if (StringUtils.hasText(dto.getPermissionType())) {
                wrapper.eq(PermissionEntity::getPermissionType, dto.getPermissionType());
            }
            if (StringUtils.hasText(dto.getPermissionName())) {
                wrapper.like(PermissionEntity::getPermissionName, dto.getPermissionName());
            }
            if (StringUtils.hasText(dto.getPermissionCode())) {
                wrapper.like(PermissionEntity::getPermissionCode, dto.getPermissionCode());
            }
            if (dto.getStatus() != null) {
                wrapper.eq(PermissionEntity::getStatus, dto.getStatus());
            }
            if (dto.getVisible() != null) {
                wrapper.eq(PermissionEntity::getVisible, dto.getVisible());
            }
            if (dto.getCreateTimeStart() != null) {
                wrapper.ge(PermissionEntity::getCreateTime, dto.getCreateTimeStart());
            }
            if (dto.getCreateTimeEnd() != null) {
                wrapper.le(PermissionEntity::getCreateTime, dto.getCreateTimeEnd());
            }
            wrapper.eq(PermissionEntity::getDeleted, 0)
                    .orderByAsc(PermissionEntity::getParentId)
                    .orderByAsc(PermissionEntity::getSortOrder)
                    .orderByDesc(PermissionEntity::getCreateTime);
            Page<PermissionEntity> entityPage = permissionMapper.selectPage(page, wrapper);
            Page<PermissionVo> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
            List<PermissionVo> records = entityPage.getRecords().stream().map(this::convertToVo).collect(Collectors.toList());
            voPage.setRecords(records);
            return voPage;
        } catch (Exception e) {
            log.error("分页查询权限列表失败：{}", e.getMessage(), e);
            throw new ServerException("查询权限列表失败", e);
        }
    }

    @Override
    public PermissionVo getPermissionById(Long permissionId) {
        try {
            log.info("根据ID查询权限，permissionId：{}", permissionId);
            if (permissionId == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "权限ID不能为空");
            }
            PermissionEntity permission = permissionMapper.selectById(permissionId);
            if (permission == null || Objects.equals(permission.getDeleted(), 1)) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "权限不存在");
            }
            return convertToVo(permission);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("根据ID查询权限失败：{}", e.getMessage(), e);
            throw new ServerException("查询权限失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PermissionVo createPermission(PermissionCreateDto dto) {
        try {
            log.info("创建权限，参数：{}", dto);
            Long parentId = dto.getParentId() == null ? 0L : dto.getParentId();
            if (parentId > 0 && !existsById(parentId)) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "父权限不存在");
            }
            if (existsByPermissionCode(dto.getPermissionCode())) {
                throw new BusinessException(ErrorCode.UNIQUE_CONSTRAINT_ERROR, "权限编码已存在");
            }
            PermissionEntity entity = new PermissionEntity();
            entity.setParentId(parentId);
            entity.setPermissionType(dto.getPermissionType());
            entity.setPermissionName(dto.getPermissionName());
            entity.setPermissionCode(dto.getPermissionCode());
            entity.setPath(dto.getPath());
            entity.setComponent(dto.getComponent());
            entity.setIcon(dto.getIcon());
            entity.setApiUrl(dto.getApiUrl());
            entity.setApiMethod(dto.getApiMethod());
            entity.setSortOrder(dto.getSortOrder() == null ? 0 : dto.getSortOrder());
            entity.setVisible(dto.getVisible() == null ? 1 : dto.getVisible());
            entity.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
            entity.setRemark(dto.getRemark());
            entity.setDeleted(0);
            permissionMapper.insert(entity);
            return convertToVo(entity);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建权限失败：{}", e.getMessage(), e);
            throw new ServerException("创建权限失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PermissionVo updatePermission(PermissionUpdateDto dto) {
        try {
            log.info("更新权限，参数：{}", dto);
            PermissionEntity permission = permissionMapper.selectById(dto.getId());
            if (permission == null || Objects.equals(permission.getDeleted(), 1)) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "权限不存在");
            }
            if (dto.getParentId() != null) {
                if (Objects.equals(dto.getParentId(), dto.getId())) {
                    throw new BusinessException(ErrorCode.PARAM_INVALID, "父权限不能是自身");
                }
                if (dto.getParentId() > 0 && !existsById(dto.getParentId())) {
                    throw new BusinessException(ErrorCode.PARAM_INVALID, "父权限不存在");
                }
                permission.setParentId(dto.getParentId());
            }
            if (StringUtils.hasText(dto.getPermissionCode()) && !dto.getPermissionCode().equals(permission.getPermissionCode())) {
                if (existsByPermissionCode(dto.getPermissionCode())) {
                    throw new BusinessException(ErrorCode.UNIQUE_CONSTRAINT_ERROR, "权限编码已存在");
                }
                permission.setPermissionCode(dto.getPermissionCode());
            }
            if (StringUtils.hasText(dto.getPermissionType())) {
                permission.setPermissionType(dto.getPermissionType());
            }
            if (StringUtils.hasText(dto.getPermissionName())) {
                permission.setPermissionName(dto.getPermissionName());
            }
            if (StringUtils.hasText(dto.getPath())) {
                permission.setPath(dto.getPath());
            }
            if (StringUtils.hasText(dto.getComponent())) {
                permission.setComponent(dto.getComponent());
            }
            if (StringUtils.hasText(dto.getIcon())) {
                permission.setIcon(dto.getIcon());
            }
            if (StringUtils.hasText(dto.getApiUrl())) {
                permission.setApiUrl(dto.getApiUrl());
            }
            if (StringUtils.hasText(dto.getApiMethod())) {
                permission.setApiMethod(dto.getApiMethod());
            }
            if (dto.getSortOrder() != null) {
                permission.setSortOrder(dto.getSortOrder());
            }
            if (dto.getVisible() != null) {
                if (dto.getVisible() != 0 && dto.getVisible() != 1) {
                    throw new BusinessException(ErrorCode.PARAM_INVALID, "显示状态值只能是0或1");
                }
                permission.setVisible(dto.getVisible());
            }
            if (dto.getStatus() != null) {
                if (dto.getStatus() != 0 && dto.getStatus() != 1) {
                    throw new BusinessException(ErrorCode.PARAM_INVALID, "状态值只能是0或1");
                }
                permission.setStatus(dto.getStatus());
            }
            if (StringUtils.hasText(dto.getRemark())) {
                permission.setRemark(dto.getRemark());
            }
            permissionMapper.updateById(permission);
            return convertToVo(permission);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新权限失败：{}", e.getMessage(), e);
            throw new ServerException("更新权限失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePermission(Long permissionId) {
        try {
            log.info("删除权限，permissionId：{}", permissionId);
            if (permissionId == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "权限ID不能为空");
            }
            PermissionEntity permission = permissionMapper.selectById(permissionId);
            if (permission == null || Objects.equals(permission.getDeleted(), 1)) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "权限不存在");
            }
            LambdaQueryWrapper<PermissionEntity> childWrapper = new LambdaQueryWrapper<>();
            childWrapper.eq(PermissionEntity::getParentId, permissionId)
                    .eq(PermissionEntity::getDeleted, 0);
            if (permissionMapper.selectCount(childWrapper) > 0) {
                throw new BusinessException(ErrorCode.DATA_IN_USE, "存在子权限，无法删除");
            }
            LambdaQueryWrapper<RolePermissionEntity> rpWrapper = new LambdaQueryWrapper<>();
            rpWrapper.eq(RolePermissionEntity::getPermissionId, permissionId);
            if (rolePermissionMapper.selectCount(rpWrapper) > 0) {
                throw new BusinessException(ErrorCode.DATA_IN_USE, "权限存在关联角色，无法删除");
            }
            permissionMapper.deleteById(permissionId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除权限失败：{}", e.getMessage(), e);
            throw new ServerException("删除权限失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeletePermission(PermissionBatchDeleteDto dto) {
        try {
            log.info("批量删除权限，permissionIds：{}", dto.getPermissionIds());
            if (CollectionUtils.isEmpty(dto.getPermissionIds())) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "权限ID列表不能为空");
            }
            for (Long permissionId : dto.getPermissionIds()) {
                deletePermission(permissionId);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量删除权限失败：{}", e.getMessage(), e);
            throw new ServerException("批量删除权限失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void switchPermissionStatus(PermissionStatusSwitchDto dto) {
        try {
            log.info("切换权限状态，permissionId：{}，status：{}", dto.getPermissionId(), dto.getStatus());
            PermissionEntity permission = permissionMapper.selectById(dto.getPermissionId());
            if (permission == null || Objects.equals(permission.getDeleted(), 1)) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "权限不存在");
            }
            if (dto.getStatus() != 0 && dto.getStatus() != 1) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "状态值只能是0或1");
            }
            permission.setStatus(dto.getStatus());
            permission.setUpdateTime(LocalDateTime.now());
            permissionMapper.updateById(permission);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("切换权限状态失败：{}", e.getMessage(), e);
            throw new ServerException("切换权限状态失败", e);
        }
    }

    @Override
    public List<PermissionVo> listPermissionTree(String permissionType, Integer status, Integer visible) {
        try {
            log.info("查询权限树");
            LambdaQueryWrapper<PermissionEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PermissionEntity::getDeleted, 0)
                    .orderByAsc(PermissionEntity::getParentId)
                    .orderByAsc(PermissionEntity::getSortOrder)
                    .orderByDesc(PermissionEntity::getCreateTime);
            if (StringUtils.hasText(permissionType)) {
                wrapper.eq(PermissionEntity::getPermissionType, permissionType);
            }
            if (status != null) {
                wrapper.eq(PermissionEntity::getStatus, status);
            }
            if (visible != null) {
                wrapper.eq(PermissionEntity::getVisible, visible);
            }
            List<PermissionEntity> allPermissions = permissionMapper.selectList(wrapper);
            if (CollectionUtils.isEmpty(allPermissions)) {
                return new ArrayList<>();
            }
            List<PermissionVo> voList = allPermissions.stream().map(this::convertToVo).collect(Collectors.toList());
            Map<Long, PermissionVo> voMap = voList.stream().collect(Collectors.toMap(PermissionVo::getId, v -> v));
            List<PermissionVo> roots = new ArrayList<>();
            for (PermissionVo vo : voList) {
                Long parentId = vo.getParentId() == null ? 0L : vo.getParentId();
                if (parentId == 0L) {
                    roots.add(vo);
                } else {
                    PermissionVo parent = voMap.get(parentId);
                    if (parent != null) {
                        parent.getChildren().add(vo);
                    } else {
                        roots.add(vo);
                    }
                }
            }
            return roots;
        } catch (Exception e) {
            log.error("查询权限树失败：{}", e.getMessage(), e);
            throw new ServerException("查询权限树失败", e);
        }
    }

    private boolean existsByPermissionCode(String permissionCode) {
        if (!StringUtils.hasText(permissionCode)) {
            return false;
        }
        LambdaQueryWrapper<PermissionEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PermissionEntity::getPermissionCode, permissionCode)
                .eq(PermissionEntity::getDeleted, 0);
        return permissionMapper.selectCount(wrapper) > 0;
    }

    private boolean existsById(Long id) {
        if (id == null) {
            return false;
        }
        PermissionEntity entity = permissionMapper.selectById(id);
        return entity != null && !Objects.equals(entity.getDeleted(), 1);
    }

    private PermissionVo convertToVo(PermissionEntity entity) {
        if (entity == null) {
            return null;
        }
        PermissionVo vo = new PermissionVo();
        vo.setId(entity.getId());
        vo.setParentId(entity.getParentId());
        vo.setPermissionType(entity.getPermissionType());
        vo.setPermissionName(entity.getPermissionName());
        vo.setPermissionCode(entity.getPermissionCode());
        vo.setPath(entity.getPath());
        vo.setComponent(entity.getComponent());
        vo.setIcon(entity.getIcon());
        vo.setApiUrl(entity.getApiUrl());
        vo.setApiMethod(entity.getApiMethod());
        vo.setSortOrder(entity.getSortOrder());
        vo.setVisible(entity.getVisible());
        vo.setStatus(entity.getStatus());
        vo.setRemark(entity.getRemark());
        vo.setCreateBy(entity.getCreateBy());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateBy(entity.getUpdateBy());
        vo.setUpdateTime(entity.getUpdateTime());
        return vo;
    }
}