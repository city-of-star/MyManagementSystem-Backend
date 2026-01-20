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
import com.mms.usercenter.common.security.constants.SuperAdminInfoConstants;
import com.mms.usercenter.service.auth.mapper.PermissionMapper;
import com.mms.usercenter.service.auth.mapper.RoleMapper;
import com.mms.usercenter.service.auth.mapper.RolePermissionMapper;
import com.mms.usercenter.service.auth.mapper.UserMapper;
import com.mms.usercenter.service.auth.mapper.UserRoleMapper;
import com.mms.usercenter.service.auth.service.PermissionService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import com.mms.usercenter.service.security.utils.SecurityUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;

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
            Page<PermissionVo> page = new Page<>(dto.getPageNum(), dto.getPageSize());
            return permissionMapper.getPermissionPage(page, dto);
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
                throw new BusinessException(ErrorCode.PERMISSION_CODE_EXISTS);
            }
            PermissionEntity entity = new PermissionEntity();
            BeanUtils.copyProperties(dto, entity);
            entity.setParentId(parentId);
            entity.setSortOrder(dto.getSortOrder() == null ? 0 : dto.getSortOrder());
            entity.setVisible(dto.getVisible() == null ? 1 : dto.getVisible());
            entity.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
            entity.setDeleted(0);
            permissionMapper.insert(entity);
            log.info("创建权限成功，permissionId：{}", entity.getId());
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
            // 查询权限
            PermissionEntity permission = permissionMapper.selectById(dto.getId());
            if (permission == null || Objects.equals(permission.getDeleted(), 1)) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "权限不存在");
            }
            // 系统核心权限不可修改
//            if (SuperAdminInfoConstants.isCorePermission(dto.getId())) {
//                throw new BusinessException(ErrorCode.CORE_PERMISSION_UPDATE_FORBIDDEN);
//            }
            if (dto.getParentId() != null) {
                if (Objects.equals(dto.getParentId(), dto.getId())) {
                    throw new BusinessException(ErrorCode.PARAM_INVALID, "父权限不能是自身");
                }
                if (dto.getParentId() > 0 && !existsById(dto.getParentId())) {
                    throw new BusinessException(ErrorCode.PARAM_INVALID, "父权限不存在");
                }
                permission.setParentId(dto.getParentId());
            }
            // 更新字段
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
            log.info("更新权限成功，permissionId：{}", permission.getId());
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
            // 查询权限
            PermissionEntity permission = permissionMapper.selectById(permissionId);
            if (permission == null || Objects.equals(permission.getDeleted(), 1)) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "权限不存在");
            }
            // 系统核心权限不可删除
            if (SuperAdminInfoConstants.isCorePermission(permissionId)) {
                throw new BusinessException(ErrorCode.CORE_PERMISSION_DELETE_FORBIDDEN);
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
            log.info("删除权限成功，permissionId：{}", permissionId);
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
                // 系统核心权限不可删除
                if (SuperAdminInfoConstants.isCorePermission(permissionId)) {
                    throw new BusinessException(ErrorCode.CORE_PERMISSION_DELETE_FORBIDDEN,
                            "系统核心权限不可删除，其他误删数据已恢复");
                }
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
            // 系统核心权限不可禁用
            if (SuperAdminInfoConstants.isCorePermission(dto.getPermissionId())) {
                throw new BusinessException(ErrorCode.CORE_PERMISSION_SWITCH_FORBIDDEN);
            }
            if (dto.getStatus() != 0 && dto.getStatus() != 1) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "状态值只能是0或1");
            }
            permission.setStatus(dto.getStatus());
            permission.setUpdateTime(LocalDateTime.now());
            permissionMapper.updateById(permission);
            log.info("切换权限状态成功，permissionId：{}，status：{}", dto.getPermissionId(), dto.getStatus());
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
            log.info("查询权限树，入参：permissionType={}，status={}，visible={}", permissionType, status, visible);

            // 查询权限列表
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

            // 转换成 PermissionVo
            List<PermissionVo> voList = allPermissions.stream().map(this::convertToVo).toList();

            // 构建权限树
            return buildPermissionTree(voList);
        } catch (Exception e) {
            log.error("查询权限树失败：{}", e.getMessage(), e);
            throw new ServerException("查询权限树失败", e);
        }
    }

    @Override
    public List<PermissionVo> listCurrentUserPermissionTree() {
        try {
            // 获取当前用户的权限编码集合
            Set<String> userPermissionCodes = SecurityUtils.getPermissions();
            if (CollectionUtils.isEmpty(userPermissionCodes)) {
                log.info("当前用户没有任何权限，返回空权限树");
                return new ArrayList<>();
            }

            // 获取当前用户的用户名
            String username = SecurityUtils.getUsername();
            log.info("查询用户 {} 的菜单权限树，权限编码数量：{}", username, userPermissionCodes.size());

            // 固定查询条件：启用、可见、目录或菜单类型
            LambdaQueryWrapper<PermissionEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PermissionEntity::getDeleted, 0)
                    .eq(PermissionEntity::getStatus, 1)
                    .eq(PermissionEntity::getVisible, 1)
                    .in(PermissionEntity::getPermissionType, "catalog", "menu")
                    .orderByAsc(PermissionEntity::getParentId)
                    .orderByAsc(PermissionEntity::getSortOrder)
                    .orderByDesc(PermissionEntity::getCreateTime);
            
            List<PermissionEntity> allPermissions = permissionMapper.selectList(wrapper);
            if (CollectionUtils.isEmpty(allPermissions)) {
                return new ArrayList<>();
            }

            // 过滤出用户有权限的节点
            List<PermissionVo> filteredVoList = allPermissions.stream()
                    .filter(p -> userPermissionCodes.contains(p.getPermissionCode()))
                    .map(this::convertToVo)
                    .toList();

            // 构建权限树
            List<PermissionVo> roots = buildPermissionTree(filteredVoList);
            log.info("用户 {} 的菜单权限树构建完成，共 {} 个根节点", username, roots.size());
            return roots;
        } catch (Exception e) {
            log.error("查询当前用户权限树失败：{}", e.getMessage(), e);
            throw new ServerException("查询当前用户权限树失败", e);
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
                    .toList();
            List<RoleEntity> roleList = roleMapper.selectBatchIds(roleIds);
            return roleList.stream()
                    .filter(role -> role != null && !Objects.equals(role.getDeleted(), 1))
                    .map(this::convertRoleToVo)
                    .toList();
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
            // 超级管理员角色必须拥有核心权限，不可移除，防止误操作
            if (Objects.equals(dto.getRoleId(), SuperAdminInfoConstants.SUPER_ADMIN_ROLE_ID)
                    && SuperAdminInfoConstants.isCorePermission(dto.getPermissionId())) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "此权限是核心权限，超级管理员角色必须拥有，不可移除");
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

    // ==================== 私有工具方法 ====================

    /**
     * 构建权限树
     *
     * @param voList 权限VO列表
     * @return 权限树根节点列表
     */
    private List<PermissionVo> buildPermissionTree(List<PermissionVo> voList) {
        if (CollectionUtils.isEmpty(voList)) {
            return new ArrayList<>();
        }
        Map<Long, PermissionVo> voMap = voList.stream()
                .collect(Collectors.toMap(PermissionVo::getId, v -> v));
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
                    // 父节点不在列表中，可能是数据不一致，仍然添加到根节点
                    log.warn("权限树数据不一致：节点 {} 的父节点 {} 不在权限列表中", vo.getId(), parentId);
                    roots.add(vo);
                }
            }
        }
        return roots;
    }

    /**
     * 判断权限编码是否存在
     */
    private boolean existsByPermissionCode(String permissionCode) {
        if (!StringUtils.hasText(permissionCode)) {
            return false;
        }
        LambdaQueryWrapper<PermissionEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PermissionEntity::getPermissionCode, permissionCode)
                .eq(PermissionEntity::getDeleted, 0);
        return permissionMapper.selectCount(wrapper) > 0;
    }

    /**
     * 判断权限ID是否存在
     */
    private boolean existsById(Long id) {
        if (id == null) {
            return false;
        }
        PermissionEntity entity = permissionMapper.selectById(id);
        return entity != null && !Objects.equals(entity.getDeleted(), 1);
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
                    .toList();
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
                    log.debug("已清除用户 {} 的权限缓存（角色：{}）", username, roleId);
                }
            }
            log.info("已清除角色 {} 关联的 {} 个用户的权限缓存", roleId, users.size());
        } catch (Exception e) {
            // 缓存清除失败不应该影响主流程，只记录日志
            log.error("清除角色 {} 关联用户权限缓存失败：{}", roleId, e.getMessage(), e);
        }
    }

    // ==================== 实体转换方法 ====================

    /**
     * 将 PermissionEntity 转换为 PermissionVo
     *
     * @param entity 权限实体
     * @return 权限VO
     */
    private PermissionVo convertToVo(PermissionEntity entity) {
        if (entity == null) {
            return null;
        }
        PermissionVo vo = new PermissionVo();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    /**
     * 将 RoleEntity 转换为 RoleVo
     * <p>
     * 注意：此方法存在于 PermissionServiceImpl 中是为了避免循环依赖。
     * 当需要查询权限关联的角色时，不能依赖 RoleService，因此在此处进行转换。
     *
     * @param entity 角色实体
     * @return 角色VO
     */
    private RoleVo convertRoleToVo(RoleEntity entity) {
        if (entity == null) {
            return null;
        }
        RoleVo vo = new RoleVo();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}