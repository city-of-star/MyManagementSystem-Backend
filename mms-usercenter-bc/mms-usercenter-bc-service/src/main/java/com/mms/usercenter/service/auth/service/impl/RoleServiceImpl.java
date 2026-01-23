package com.mms.usercenter.service.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.core.exceptions.ServerException;
import com.mms.usercenter.common.auth.dto.RoleAssignPermissionDto;
import com.mms.usercenter.common.auth.dto.RoleBatchDeleteDto;
import com.mms.usercenter.common.auth.dto.RoleCreateDto;
import com.mms.usercenter.common.auth.dto.RolePageQueryDto;
import com.mms.usercenter.common.auth.dto.RoleRemoveUserDto;
import com.mms.usercenter.common.auth.dto.RoleStatusSwitchDto;
import com.mms.usercenter.common.auth.dto.RoleUpdateDto;
import com.mms.usercenter.common.auth.entity.PermissionEntity;
import com.mms.usercenter.common.auth.entity.RoleEntity;
import com.mms.usercenter.common.auth.entity.RolePermissionEntity;
import com.mms.usercenter.common.auth.entity.UserEntity;
import com.mms.usercenter.common.auth.entity.UserRoleEntity;
import com.mms.usercenter.common.auth.vo.RoleVo;
import com.mms.usercenter.common.auth.vo.UserVo;
import com.mms.usercenter.common.security.constants.SuperAdminInfoConstants;
import com.mms.usercenter.service.auth.mapper.PermissionMapper;
import com.mms.usercenter.service.auth.mapper.RoleMapper;
import com.mms.usercenter.service.auth.mapper.RolePermissionMapper;
import com.mms.usercenter.service.auth.mapper.UserMapper;
import com.mms.usercenter.service.auth.mapper.UserRoleMapper;
import com.mms.usercenter.service.auth.service.RoleService;
import com.mms.usercenter.service.security.service.UserAuthorityService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/**
 * 实现功能【角色服务实现类】
 * <p>
 * 提供角色管理的核心业务逻辑实现
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-12 09:26:57
 */
@Slf4j
@Service
public class RoleServiceImpl implements RoleService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private PermissionMapper permissionMapper;

    @Resource
    private RolePermissionMapper rolePermissionMapper;

    @Resource
    private UserAuthorityService userAuthorityService;

    @Override
    public Page<RoleVo> getRolePage(RolePageQueryDto dto) {
        try {
            log.info("分页查询角色列表，参数：{}", dto);
            Page<RoleVo> page = new Page<>(dto.getPageNum(), dto.getPageSize());
            return roleMapper.getRolePage(page, dto);
        } catch (Exception e) {
            log.error("分页查询角色列表失败：{}", e.getMessage(), e);
            throw new ServerException("查询角色列表失败", e);
        }
    }

    @Override
    public RoleVo getRoleById(Long roleId) {
        try {
            log.info("根据ID查询角色，roleId：{}", roleId);
            if (roleId == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "角色ID不能为空");
            }
            RoleEntity role = roleMapper.selectById(roleId);
            if (role == null || Objects.equals(role.getDeleted(), 1)) {
                throw new BusinessException(ErrorCode.ROLE_NOT_FOUND);
            }
            return convertToVo(role);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("根据ID查询角色失败：{}", e.getMessage(), e);
            throw new ServerException("查询角色失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoleVo createRole(RoleCreateDto dto) {
        try {
            log.info("创建角色，参数：{}", dto);
            // 检查角色编码是否已存在
            if (existsByRoleCode(dto.getRoleCode())) {
                throw new BusinessException(ErrorCode.ROLE_CODE_EXISTS);
            }
            // 检查角色名称是否已存在
            if (existsByRoleName(dto.getRoleName())) {
                throw new BusinessException(ErrorCode.ROLE_NAME_EXISTS);
            }
            RoleEntity role = new RoleEntity();
            BeanUtils.copyProperties(dto, role);
            if (role.getStatus() == null) {
                role.setStatus(1);
            }
            if (role.getSortOrder() == null) {
                role.setSortOrder(0);
            }
            role.setDeleted(0);
            roleMapper.insert(role);
            log.info("创建角色成功，roleId：{}", role.getId());
            return convertToVo(role);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建角色失败：{}", e.getMessage(), e);
            throw new ServerException("创建角色失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoleVo updateRole(RoleUpdateDto dto) {
        try {
            log.info("更新角色，参数：{}", dto);
            // 查询角色
            RoleEntity role = roleMapper.selectById(dto.getId());
            if (role == null || Objects.equals(role.getDeleted(), 1)) {
                throw new BusinessException(ErrorCode.ROLE_NOT_FOUND);
            }
            // 检查角色名称是否已存在
            if (existsByRoleName(dto.getRoleName())) {
                throw new BusinessException(ErrorCode.ROLE_NAME_EXISTS);
            }
            // 更新字段
            if (StringUtils.hasText(dto.getRoleName())) {
                role.setRoleName(dto.getRoleName());
            }
            if (StringUtils.hasText(dto.getRoleType())) {
                role.setRoleType(dto.getRoleType());
            }
            if (dto.getSortOrder() != null) {
                role.setSortOrder(dto.getSortOrder());
            }
            if (StringUtils.hasText(dto.getRemark())) {
                role.setRemark(dto.getRemark());
            }
            roleMapper.updateById(role);
            log.info("更新角色成功，roleId：{}", role.getId());
            return convertToVo(role);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新角色失败：{}", e.getMessage(), e);
            throw new ServerException("更新角色失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long roleId) {
        try {
            log.info("删除角色，roleId：{}", roleId);
            if (roleId == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "角色ID不能为空");
            }
            // 检查角色是否存在
            RoleEntity role = roleMapper.selectById(roleId);
            if (role == null || Objects.equals(role.getDeleted(), 1)) {
                throw new BusinessException(ErrorCode.ROLE_NOT_FOUND);
            }
            // 检查是否是超级管理员角色
            if (roleId.equals(SuperAdminInfoConstants.SUPER_ADMIN_ROLE_ID)) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "超级管理员角色不可删除");
            }
            // 检查是否有用户关联
            LambdaQueryWrapper<UserRoleEntity> userRoleWrapper = new LambdaQueryWrapper<UserRoleEntity>()
                            .eq(UserRoleEntity::getRoleId, roleId);
            long count = userRoleMapper.selectCount(userRoleWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.ROLE_IN_USE, "角色存在关联用户，无法删除");
            }
            // 逻辑删除
            roleMapper.deleteById(roleId);
            // 清理角色权限关联
            LambdaQueryWrapper<RolePermissionEntity> rpWrapper = new LambdaQueryWrapper<>();
            rpWrapper.eq(RolePermissionEntity::getRoleId, roleId);
            rolePermissionMapper.delete(rpWrapper);
            log.info("删除角色成功，roleId：{}", roleId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除角色失败：{}", e.getMessage(), e);
            throw new ServerException("删除角色失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteRole(RoleBatchDeleteDto dto) {
        try {
            log.info("批量删除角色，roleIds：{}", dto.getRoleIds());
            if (CollectionUtils.isEmpty(dto.getRoleIds())) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "角色ID列表不能为空");
            }
            for (Long roleId : dto.getRoleIds()) {
                deleteRole(roleId);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量删除角色失败：{}", e.getMessage(), e);
            throw new ServerException("批量删除角色失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void switchRoleStatus(RoleStatusSwitchDto dto) {
        try {
            log.info("切换角色状态，roleId：{}，status：{}", dto.getRoleId(), dto.getStatus());
            RoleEntity role = roleMapper.selectById(dto.getRoleId());
            if (role == null || Objects.equals(role.getDeleted(), 1)) {
                throw new BusinessException(ErrorCode.ROLE_NOT_FOUND);
            }
            // 检查
            if (Objects.equals(dto.getRoleId(), SuperAdminInfoConstants.SUPER_ADMIN_ROLE_ID) && dto.getStatus() == 0) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "超级管理员角色不可禁用");
            }
            if (dto.getStatus() != 0 && dto.getStatus() != 1) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "状态值只能是0或1");
            }
            role.setStatus(dto.getStatus());
            roleMapper.updateById(role);
            // 角色状态变更时，清除拥有该角色的所有用户的权限缓存
            // 因为禁用角色后，用户不应该再拥有该角色的权限
            userAuthorityService.clearUserAuthorityCacheByRoleId(dto.getRoleId());
            log.info("切换角色状态成功，roleId：{}，status：{}", dto.getRoleId(), dto.getStatus());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("切换角色状态失败：{}", e.getMessage(), e);
            throw new ServerException("切换角色状态失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(RoleAssignPermissionDto dto) {
        try {
            log.info("为角色分配权限，roleId：{}，permissionIds：{}", dto.getRoleId(), dto.getPermissionIds());
            if (dto.getRoleId() == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "角色ID不能为空");
            }
            RoleEntity role = roleMapper.selectById(dto.getRoleId());
            if (role == null || Objects.equals(role.getDeleted(), 1)) {
                throw new BusinessException(ErrorCode.ROLE_NOT_FOUND);
            }
            if (CollectionUtils.isEmpty(dto.getPermissionIds())) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "权限ID列表不能为空");
            }
            // 检查权限ID是否存在且未删除
            List<PermissionEntity> permissions = permissionMapper.selectBatchIds(dto.getPermissionIds());
            if (permissions.size() != dto.getPermissionIds().size()) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "存在无效的权限ID");
            }
            // 检查是否有已删除或者被禁用的权限
            for (PermissionEntity permission : permissions) {
                if (Objects.equals(permission.getDeleted(), 1)) {
                    throw new BusinessException(ErrorCode.PARAM_INVALID, "权限 " + permission.getPermissionCode() + " 已被删除");
                }
                if (Objects.equals(permission.getStatus(), 0)) {
                    throw new BusinessException(ErrorCode.PARAM_INVALID, "权限 " + permission.getPermissionCode() + " 已被禁用，无法分配给角色");
                }
            }
            // 超级管理员角色必须拥有所有的核心权限，防止误操作
            if (Objects.equals(dto.getRoleId(), SuperAdminInfoConstants.SUPER_ADMIN_ROLE_ID) &&
                    !new HashSet<>(dto.getPermissionIds()).containsAll(SuperAdminInfoConstants.SYSTEM_CORE_PERMISSION_IDS)) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "超级管理员角色必须拥有所有的核心权限");
            }
            saveRolePermissions(dto.getRoleId(), dto.getPermissionIds());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("分配角色权限失败：{}", e.getMessage(), e);
            throw new ServerException("分配角色权限失败", e);
        }
    }

    @Override
    public List<Long> listPermissionIdsByRoleId(Long roleId) {
        try {
            log.info("查询角色权限ID列表，roleId：{}", roleId);
            LambdaQueryWrapper<RolePermissionEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(RolePermissionEntity::getRoleId, roleId);
            return rolePermissionMapper.selectList(wrapper).stream()
                    .map(RolePermissionEntity::getPermissionId)
                    .toList();
        } catch (Exception e) {
            log.error("查询角色权限ID列表失败：{}", e.getMessage(), e);
            throw new ServerException("查询角色权限ID列表失败", e);
        }
    }

    @Override
    public List<UserVo> listUsersByRoleId(Long roleId) {
        try {
            log.info("查询角色关联的用户列表，roleId：{}", roleId);
            if (roleId == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "角色ID不能为空");
            }
            // 验证角色是否存在
            RoleEntity role = roleMapper.selectById(roleId);
            if (role == null || Objects.equals(role.getDeleted(), 1)) {
                throw new BusinessException(ErrorCode.ROLE_NOT_FOUND);
            }
            // 查询角色关联的用户ID列表
            LambdaQueryWrapper<UserRoleEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserRoleEntity::getRoleId, roleId);
            List<UserRoleEntity> userRoleList = userRoleMapper.selectList(wrapper);
            if (CollectionUtils.isEmpty(userRoleList)) {
                return new ArrayList<>();
            }
            // 获取用户ID列表
            List<Long> userIds = userRoleList.stream()
                    .map(UserRoleEntity::getUserId)
                    .distinct()
                    .toList();
            // 查询用户信息
            List<UserEntity> userList = userMapper.selectBatchIds(userIds);
            // 转换为VO
            return userList.stream()
                    .filter(user -> !Objects.equals(user.getDeleted(), 1))
                    .map(this::convertUserToVo)
                    .toList();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询角色关联的用户列表失败：{}", e.getMessage(), e);
            throw new ServerException("查询角色关联的用户列表失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeUserFromRole(RoleRemoveUserDto dto) {
        try {
            log.info("移除角色的用户关联，roleId：{}，userId：{}", dto.getRoleId(), dto.getUserId());
            if (dto.getRoleId() == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "角色ID不能为空");
            }
            if (dto.getUserId() == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "用户ID不能为空");
            }
            if (dto.getUserId().equals(SuperAdminInfoConstants.SUPER_ADMIN_USER_ID)
                    && dto.getRoleId().equals(SuperAdminInfoConstants.SUPER_ADMIN_ROLE_ID)) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "超级管理员角色不可移除超级管理用户");
            }
            // 验证角色是否存在
            RoleEntity role = roleMapper.selectById(dto.getRoleId());
            if (role == null || Objects.equals(role.getDeleted(), 1)) {
                throw new BusinessException(ErrorCode.ROLE_NOT_FOUND);
            }
            // 验证用户是否存在
            UserEntity user = userMapper.selectById(dto.getUserId());
            if (user == null || Objects.equals(user.getDeleted(), 1)) {
                throw new BusinessException(ErrorCode.USER_NOT_FOUND);
            }
            // 删除用户角色关联
            LambdaQueryWrapper<UserRoleEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserRoleEntity::getRoleId, dto.getRoleId())
                    .eq(UserRoleEntity::getUserId, dto.getUserId());
            int deleted = userRoleMapper.delete(wrapper);
            if (deleted == 0) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "用户未关联该角色");
            }
            // 清除该用户的权限缓存，确保权限变更立即生效
            userAuthorityService.clearUserAuthorityCacheByUserId(dto.getUserId());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("移除角色的用户关联失败：{}", e.getMessage(), e);
            throw new ServerException("移除角色的用户关联失败", e);
        }
    }

    // ==================== 私有工具方法 ====================

    /**
     * 判断角色编码是否存在
     */
    private boolean existsByRoleCode(String roleCode) {
        if (!StringUtils.hasText(roleCode)) {
            return false;
        }
        LambdaQueryWrapper<RoleEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleEntity::getRoleCode, roleCode)
                .eq(RoleEntity::getDeleted, 0);
        return roleMapper.selectCount(wrapper) > 0;
    }

    /**
     * 判断角色名称是否存在
     */
    private boolean existsByRoleName(String roleName) {
        if (!StringUtils.hasText(roleName)) {
            return false;
        }
        LambdaQueryWrapper<RoleEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleEntity::getRoleName, roleName)
                .eq(RoleEntity::getDeleted, 0);
        return roleMapper.selectCount(wrapper) > 0;
    }

    /**
     * 保存角色权限关联
     */
    private void saveRolePermissions(Long roleId, List<Long> permissionIds) {
        // 先清空旧关联
        LambdaQueryWrapper<RolePermissionEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermissionEntity::getRoleId, roleId);
        rolePermissionMapper.delete(wrapper);
        if (CollectionUtils.isEmpty(permissionIds)) {
            // 即使权限列表为空，也需要清除相关用户的缓存
            userAuthorityService.clearUserAuthorityCacheByRoleId(roleId);
            return;
        }
        List<RolePermissionEntity> entities = new ArrayList<>();
        // 去重，避免唯一键冲突
        List<Long> distinctIds = permissionIds.stream().distinct().toList();
        for (Long permissionId : distinctIds) {
            RolePermissionEntity entity = new RolePermissionEntity();
            entity.setRoleId(roleId);
            entity.setPermissionId(permissionId);
            entity.setCreateTime(LocalDateTime.now());
            entities.add(entity);
        }
        // 批量插入（如果数据量较大，可以考虑使用 MyBatis-Plus 批量插入插件）
        for (RolePermissionEntity entity : entities) {
            rolePermissionMapper.insert(entity);
        }
        // 清除拥有该角色的所有用户的权限缓存，确保权限变更立即生效
        userAuthorityService.clearUserAuthorityCacheByRoleId(roleId);
    }

    // ==================== 实体转换方法 ====================

    /**
     * 将 RoleEntity 转换为 RoleVo
     *
     * @param entity 角色实体
     * @return 角色VO
     */
    private RoleVo convertToVo(RoleEntity entity) {
        if (entity == null) {
            return null;
        }
        RoleVo vo = new RoleVo();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    /**
     * 将 UserEntity 转换为 UserVo
     * <p>
     * 注意：此方法存在于 RoleServiceImpl 中是为了避免循环依赖。
     * 当需要查询角色关联的用户时，不能依赖 UserService，因此在此处进行转换。
     *
     * @param entity 用户实体
     * @return 用户VO
     */
    private UserVo convertUserToVo(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        UserVo vo = new UserVo();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}