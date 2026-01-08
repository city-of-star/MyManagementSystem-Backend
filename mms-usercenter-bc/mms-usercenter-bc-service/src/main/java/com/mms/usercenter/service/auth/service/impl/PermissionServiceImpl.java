package com.mms.usercenter.service.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.core.exceptions.ServerException;
import com.mms.common.core.constants.usercenter.UserAuthorityConstants;
import com.mms.usercenter.common.auth.dto.PermissionBatchDeleteDto;
import com.mms.usercenter.common.auth.dto.PermissionCreateDto;
import com.mms.usercenter.common.auth.dto.PermissionPageQueryDto;
import com.mms.usercenter.common.auth.dto.PermissionRemoveRoleDto;
import com.mms.usercenter.common.auth.dto.PermissionStatusSwitchDto;
import com.mms.usercenter.common.auth.dto.PermissionUpdateDto;
import com.mms.usercenter.common.auth.entity.PermissionEntity;
import com.mms.usercenter.common.auth.entity.RolePermissionEntity;
import com.mms.usercenter.common.auth.entity.RoleEntity;
import com.mms.usercenter.common.auth.entity.UserEntity;
import com.mms.usercenter.common.auth.entity.UserRoleEntity;
import com.mms.usercenter.common.auth.vo.PermissionVo;
import com.mms.usercenter.common.auth.vo.RoleVo;
import com.mms.usercenter.service.auth.mapper.PermissionMapper;
import com.mms.usercenter.service.auth.mapper.RoleMapper;
import com.mms.usercenter.service.auth.mapper.RolePermissionMapper;
import com.mms.usercenter.service.auth.mapper.UserMapper;
import com.mms.usercenter.service.auth.mapper.UserRoleMapper;
import com.mms.usercenter.service.auth.service.PermissionService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

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

    @Override
    public List<RoleVo> listRolesByPermissionId(Long permissionId) {
        try {
            log.info("查询权限关联的角色列表，permissionId：{}", permissionId);
            if (permissionId == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "权限ID不能为空");
            }
            PermissionEntity permission = permissionMapper.selectById(permissionId);
            if (permission == null || Objects.equals(permission.getDeleted(), 1)) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "权限不存在");
            }
            LambdaQueryWrapper<RolePermissionEntity> rpWrapper = new LambdaQueryWrapper<>();
            rpWrapper.eq(RolePermissionEntity::getPermissionId, permissionId);
            List<RolePermissionEntity> relations = rolePermissionMapper.selectList(rpWrapper);
            if (CollectionUtils.isEmpty(relations)) {
                return new ArrayList<>();
            }
            List<Long> roleIds = relations.stream()
                    .map(RolePermissionEntity::getRoleId)
                    .distinct()
                    .collect(Collectors.toList());
            List<RoleEntity> roleList = roleMapper.selectBatchIds(roleIds);
            return roleList.stream()
                    .filter(role -> role != null && !Objects.equals(role.getDeleted(), 1))
                    .map(this::convertRoleToVo)
                    .collect(Collectors.toList());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询权限关联的角色列表失败：{}", e.getMessage(), e);
            throw new ServerException("查询权限关联的角色列表失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeRoleFromPermission(PermissionRemoveRoleDto dto) {
        try {
            log.info("移除权限与角色关联，permissionId：{}，roleId：{}", dto.getPermissionId(), dto.getRoleId());
            if (dto.getPermissionId() == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "权限ID不能为空");
            }
            if (dto.getRoleId() == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "角色ID不能为空");
            }
            PermissionEntity permission = permissionMapper.selectById(dto.getPermissionId());
            if (permission == null || Objects.equals(permission.getDeleted(), 1)) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "权限不存在");
            }
            RoleEntity role = roleMapper.selectById(dto.getRoleId());
            if (role == null || Objects.equals(role.getDeleted(), 1)) {
                throw new BusinessException(ErrorCode.ROLE_NOT_FOUND);
            }
            LambdaQueryWrapper<RolePermissionEntity> rpWrapper = new LambdaQueryWrapper<>();
            rpWrapper.eq(RolePermissionEntity::getPermissionId, dto.getPermissionId())
                    .eq(RolePermissionEntity::getRoleId, dto.getRoleId());
            int deleted = rolePermissionMapper.delete(rpWrapper);
            if (deleted == 0) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "角色未关联该权限");
            }
            // 清除拥有该角色的用户缓存，确保权限变更生效
            clearUserAuthorityCacheByRoleId(dto.getRoleId());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("移除权限与角色关联失败：{}", e.getMessage(), e);
            throw new ServerException("移除权限与角色关联失败", e);
        }
    }

    @Override
    public List<PermissionVo> listCurrentUserPermissionTree(String permissionType, Integer status, Integer visible) {
        try {
            // 获取当前用户身份
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || authentication.getName() == null) {
                log.warn("获取当前用户权限树失败：未找到认证信息");
                return new ArrayList<>();
            }

            String username = authentication.getName();
            log.info("查询当前用户 {} 的权限树", username);

            // 获取当前用户的所有权限编码
            Set<String> userPermissionCodes = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(authority -> !authority.startsWith("ROLE_")) // 过滤掉角色，只保留权限编码
                    .collect(Collectors.toSet());

            if (CollectionUtils.isEmpty(userPermissionCodes)) {
                log.info("用户 {} 没有任何权限，返回空权限树", username);
                return new ArrayList<>();
            }

            // 查询所有符合条件的权限
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

            // 先过滤出用户直接拥有权限的节点（或无权限码的目录/菜单）
            Set<Long> allowedIds = new HashSet<>();
            for (PermissionEntity permission : allPermissions) {
                if (!StringUtils.hasText(permission.getPermissionCode()) ||
                        userPermissionCodes.contains(permission.getPermissionCode())) {
                    allowedIds.add(permission.getId());
                }
            }

            // 为了保持目录层级，把被保留节点的所有祖先节点也加入
            if (!allowedIds.isEmpty()) {
                Map<Long, PermissionEntity> entityMap = allPermissions.stream()
                        .collect(Collectors.toMap(PermissionEntity::getId, p -> p));

                for (Long id : new ArrayList<>(allowedIds)) {
                    PermissionEntity current = entityMap.get(id);
                    while (current != null && current.getParentId() != null && current.getParentId() != 0L) {
                        Long parentId = current.getParentId();
                        if (allowedIds.add(parentId)) {
                            current = entityMap.get(parentId);
                        } else {
                            break;
                        }
                    }
                }
            }

            // 将保留的节点转换为 VO
            List<PermissionVo> filteredVoList = allPermissions.stream()
                    .filter(p -> allowedIds.contains(p.getId()))
                    .map(this::convertToVo)
                    .collect(Collectors.toList());

            // 构建权限树，但只保留用户有权限的节点
            Map<Long, PermissionVo> voMap = filteredVoList.stream()
                    .collect(Collectors.toMap(PermissionVo::getId, v -> v));
            
            List<PermissionVo> roots = new ArrayList<>();
            for (PermissionVo vo : filteredVoList) {
                Long parentId = vo.getParentId() == null ? 0L : vo.getParentId();
                if (parentId == 0L) {
                    roots.add(vo);
                } else {
                    PermissionVo parent = voMap.get(parentId);
                    if (parent != null) {
                        parent.getChildren().add(vo);
                    } else {
                        // 父节点不在过滤结果中，但子节点有权限，这种情况不应该出现
                        // 如果出现，说明父节点没有权限编码，我们仍然添加到根节点
                        roots.add(vo);
                    }
                }
            }

            // 清理空目录：如果目录节点下没有任何子节点，则移除该目录
            roots = filterEmptyDirectories(roots);
            
            log.info("用户 {} 的权限树构建完成，共 {} 个根节点", username, roots.size());
            return roots;
        } catch (Exception e) {
            log.error("查询当前用户权限树失败：{}", e.getMessage(), e);
            throw new ServerException("查询当前用户权限树失败", e);
        }
    }

    /**
     * 过滤掉空的目录节点（没有子节点的目录）
     * 递归处理，确保子目录也被清理
     */
    private List<PermissionVo> filterEmptyDirectories(List<PermissionVo> nodes) {
        if (CollectionUtils.isEmpty(nodes)) {
            return new ArrayList<>();
        }
        
        List<PermissionVo> filtered = new ArrayList<>();
        for (PermissionVo node : nodes) {
            // 如果是目录类型，需要检查是否有子节点
            if ("directory".equals(node.getPermissionType())) {
                List<PermissionVo> filteredChildren = filterEmptyDirectories(node.getChildren());
                if (!CollectionUtils.isEmpty(filteredChildren)) {
                    node.setChildren(filteredChildren);
                    filtered.add(node);
                }
                // 如果目录下没有子节点，则不添加该目录
            } else {
                // 菜单或按钮类型，直接添加
                filtered.add(node);
            }
        }
        return filtered;
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

    private RoleVo convertRoleToVo(RoleEntity role) {
        if (role == null) {
            return null;
        }
        RoleVo vo = new RoleVo();
        org.springframework.beans.BeanUtils.copyProperties(role, vo);
        return vo;
    }

    /**
     * 清除拥有指定角色的用户权限缓存，确保角色权限调整即时生效
     */
    private void clearUserAuthorityCacheByRoleId(Long roleId) {
        try {
            LambdaQueryWrapper<UserRoleEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserRoleEntity::getRoleId, roleId);
            List<UserRoleEntity> userRoleList = userRoleMapper.selectList(wrapper);
            if (CollectionUtils.isEmpty(userRoleList)) {
                return;
            }
            List<Long> userIds = userRoleList.stream()
                    .map(UserRoleEntity::getUserId)
                    .distinct()
                    .collect(Collectors.toList());
            List<UserEntity> users = userMapper.selectBatchIds(userIds);
            for (UserEntity user : users) {
                if (user == null || Objects.equals(user.getDeleted(), 1)) {
                    continue;
                }
                String username = user.getUsername();
                if (StringUtils.hasText(username)) {
                    String roleCacheKey = UserAuthorityConstants.USER_ROLE_PREFIX + username;
                    String permissionCacheKey = UserAuthorityConstants.USER_PERMISSION_PREFIX + username;
                    redisTemplate.delete(roleCacheKey);
                    redisTemplate.delete(permissionCacheKey);
                }
            }
        } catch (Exception e) {
            log.error("清除角色 {} 关联用户权限缓存失败：{}", roleId, e.getMessage(), e);
        }
    }
}