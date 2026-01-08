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
import com.mms.usercenter.common.auth.entity.RoleEntity;
import com.mms.usercenter.common.auth.entity.RolePermissionEntity;
import com.mms.usercenter.common.auth.entity.UserEntity;
import com.mms.usercenter.common.auth.entity.UserRoleEntity;
import com.mms.usercenter.common.auth.vo.RoleVo;
import com.mms.usercenter.common.auth.vo.UserVo;
import com.mms.common.core.constants.usercenter.UserAuthorityConstants;
import com.mms.usercenter.service.auth.mapper.RoleMapper;
import com.mms.usercenter.service.auth.mapper.RolePermissionMapper;
import com.mms.usercenter.service.auth.mapper.UserMapper;
import com.mms.usercenter.service.auth.mapper.UserRoleMapper;
import com.mms.usercenter.service.auth.service.RoleService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    private RoleMapper roleMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private RolePermissionMapper rolePermissionMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Page<RoleVo> getRolePage(RolePageQueryDto dto) {
        try {
            log.info("分页查询角色列表，参数：{}", dto);
            Page<RoleEntity> page = new Page<>(dto.getPageNum(), dto.getPageSize());
            LambdaQueryWrapper<RoleEntity> wrapper = new LambdaQueryWrapper<>();
            if (StringUtils.hasText(dto.getRoleCode())) {
                wrapper.like(RoleEntity::getRoleCode, dto.getRoleCode());
            }
            if (StringUtils.hasText(dto.getRoleName())) {
                wrapper.like(RoleEntity::getRoleName, dto.getRoleName());
            }
            if (StringUtils.hasText(dto.getRoleType())) {
                wrapper.eq(RoleEntity::getRoleType, dto.getRoleType());
            }
            if (dto.getStatus() != null) {
                wrapper.eq(RoleEntity::getStatus, dto.getStatus());
            }
            if (dto.getCreateTimeStart() != null) {
                wrapper.ge(RoleEntity::getCreateTime, dto.getCreateTimeStart());
            }
            if (dto.getCreateTimeEnd() != null) {
                wrapper.le(RoleEntity::getCreateTime, dto.getCreateTimeEnd());
            }
            wrapper.eq(RoleEntity::getDeleted, 0)
                    .orderByAsc(RoleEntity::getSortOrder)
                    .orderByDesc(RoleEntity::getCreateTime);
            Page<RoleEntity> entityPage = roleMapper.selectPage(page, wrapper);
            Page<RoleVo> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
            List<RoleVo> records = entityPage.getRecords().stream().map(this::convertToVo).collect(Collectors.toList());
            voPage.setRecords(records);
            return voPage;
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
            if (existsByRoleCode(dto.getRoleCode())) {
                throw new BusinessException(ErrorCode.ROLE_CODE_EXISTS);
            }
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
            // 处理权限绑定
            if (!CollectionUtils.isEmpty(dto.getPermissionIds())) {
                saveRolePermissions(role.getId(), dto.getPermissionIds());
            }
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
            RoleEntity role = roleMapper.selectById(dto.getId());
            if (role == null || Objects.equals(role.getDeleted(), 1)) {
                throw new BusinessException(ErrorCode.ROLE_NOT_FOUND);
            }
            if (StringUtils.hasText(dto.getRoleCode()) && !dto.getRoleCode().equals(role.getRoleCode())) {
                if (existsByRoleCode(dto.getRoleCode())) {
                    throw new BusinessException(ErrorCode.ROLE_CODE_EXISTS);
                }
                role.setRoleCode(dto.getRoleCode());
            }
            if (StringUtils.hasText(dto.getRoleName()) && !dto.getRoleName().equals(role.getRoleName())) {
                if (existsByRoleName(dto.getRoleName())) {
                    throw new BusinessException(ErrorCode.ROLE_NAME_EXISTS);
                }
                role.setRoleName(dto.getRoleName());
            }
            if (StringUtils.hasText(dto.getRoleType())) {
                role.setRoleType(dto.getRoleType());
            }
            if (dto.getSortOrder() != null) {
                role.setSortOrder(dto.getSortOrder());
            }
            if (dto.getStatus() != null) {
                if (dto.getStatus() != 0 && dto.getStatus() != 1) {
                    throw new BusinessException(ErrorCode.PARAM_INVALID, "状态值只能是0或1");
                }
                role.setStatus(dto.getStatus());
            }
            if (StringUtils.hasText(dto.getRemark())) {
                role.setRemark(dto.getRemark());
            }
            roleMapper.updateById(role);
            // 权限覆盖
            if (dto.getPermissionIds() != null) {
                saveRolePermissions(role.getId(), dto.getPermissionIds());
            }
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
            RoleEntity role = roleMapper.selectById(roleId);
            if (role == null || Objects.equals(role.getDeleted(), 1)) {
                throw new BusinessException(ErrorCode.ROLE_NOT_FOUND);
            }
            // 判断是否有用户关联
            LambdaQueryWrapper<com.mms.usercenter.common.auth.entity.UserRoleEntity> userRoleWrapper =
                    new LambdaQueryWrapper<com.mms.usercenter.common.auth.entity.UserRoleEntity>()
                            .eq(com.mms.usercenter.common.auth.entity.UserRoleEntity::getRoleId, roleId);
            long count = userRoleMapper.selectCount(userRoleWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.ROLE_IN_USE, "角色存在关联用户，无法删除");
            }
            // 逻辑删除角色
            roleMapper.deleteById(roleId);
            // 清理角色权限关联
            LambdaQueryWrapper<RolePermissionEntity> rpWrapper = new LambdaQueryWrapper<>();
            rpWrapper.eq(RolePermissionEntity::getRoleId, roleId);
            rolePermissionMapper.delete(rpWrapper);
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
            if (dto.getStatus() != 0 && dto.getStatus() != 1) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "状态值只能是0或1");
            }
            role.setStatus(dto.getStatus());
            roleMapper.updateById(role);
            // 角色状态变更时，清除拥有该角色的所有用户的权限缓存
            // 因为禁用角色后，用户不应该再拥有该角色的权限
            clearUserAuthorityCacheByRoleId(dto.getRoleId());
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
                    .collect(Collectors.toList());
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
                    .collect(Collectors.toList());
            // 查询用户信息
            List<UserEntity> userList = userMapper.selectBatchIds(userIds);
            // 转换为VO
            return userList.stream()
                    .filter(user -> !Objects.equals(user.getDeleted(), 1))
                    .map(this::convertUserToVo)
                    .collect(Collectors.toList());
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
            clearUserAuthorityCacheByUserId(dto.getUserId());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("移除角色的用户关联失败：{}", e.getMessage(), e);
            throw new ServerException("移除角色的用户关联失败", e);
        }
    }

    private boolean existsByRoleCode(String roleCode) {
        if (!StringUtils.hasText(roleCode)) {
            return false;
        }
        LambdaQueryWrapper<RoleEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleEntity::getRoleCode, roleCode)
                .eq(RoleEntity::getDeleted, 0);
        return roleMapper.selectCount(wrapper) > 0;
    }

    private boolean existsByRoleName(String roleName) {
        if (!StringUtils.hasText(roleName)) {
            return false;
        }
        LambdaQueryWrapper<RoleEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleEntity::getRoleName, roleName)
                .eq(RoleEntity::getDeleted, 0);
        return roleMapper.selectCount(wrapper) > 0;
    }

    private void saveRolePermissions(Long roleId, List<Long> permissionIds) {
        // 先清空旧关联
        LambdaQueryWrapper<RolePermissionEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermissionEntity::getRoleId, roleId);
        rolePermissionMapper.delete(wrapper);
        if (CollectionUtils.isEmpty(permissionIds)) {
            // 即使权限列表为空，也需要清除相关用户的缓存
            clearUserAuthorityCacheByRoleId(roleId);
            return;
        }
        List<RolePermissionEntity> entities = new ArrayList<>();
        // 去重，避免唯一键冲突
        List<Long> distinctIds = permissionIds.stream().distinct().collect(Collectors.toList());
        for (Long permissionId : distinctIds) {
            RolePermissionEntity entity = new RolePermissionEntity();
            entity.setRoleId(roleId);
            entity.setPermissionId(permissionId);
            entity.setCreateTime(LocalDateTime.now());
            entities.add(entity);
        }
        for (RolePermissionEntity entity : entities) {
            rolePermissionMapper.insert(entity);
        }
        // 清除拥有该角色的所有用户的权限缓存，确保权限变更立即生效
        clearUserAuthorityCacheByRoleId(roleId);
    }

    /**
     * 清除拥有指定角色的所有用户的权限缓存
     * 当角色权限变更时，需要清除相关用户的缓存，确保下次请求时重新从数据库加载最新权限
     *
     * @param roleId 角色ID
     */
    private void clearUserAuthorityCacheByRoleId(Long roleId) {
        try {
            // 查询拥有该角色的所有用户
            LambdaQueryWrapper<UserRoleEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserRoleEntity::getRoleId, roleId);
            List<UserRoleEntity> userRoleList = userRoleMapper.selectList(wrapper);
            
            if (CollectionUtils.isEmpty(userRoleList)) {
                log.debug("角色 {} 没有关联用户，无需清除缓存", roleId);
                return;
            }

            // 获取所有关联的用户ID
            List<Long> userIds = userRoleList.stream()
                    .map(UserRoleEntity::getUserId)
                    .distinct()
                    .collect(Collectors.toList());

            // 查询用户信息，获取用户名
            List<UserEntity> users = userMapper.selectBatchIds(userIds);
            if (CollectionUtils.isEmpty(users)) {
                log.warn("角色 {} 关联的用户不存在，userIdList={}", roleId, userIds);
                return;
            }

            // 清除每个用户的权限缓存
            for (UserEntity user : users) {
                if (user.getDeleted() != null && user.getDeleted() == 1) {
                    continue; // 跳过已删除的用户
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
            log.error("清除角色 {} 关联用户的权限缓存失败：{}", roleId, e.getMessage(), e);
        }
    }

    /**
     * 清除指定用户的权限缓存
     * 当用户角色关联变更时，需要清除该用户的缓存，确保下次请求时重新从数据库加载最新权限
     *
     * @param userId 用户ID
     */
    private void clearUserAuthorityCacheByUserId(Long userId) {
        try {
            UserEntity user = userMapper.selectById(userId);
            if (user == null || (user.getDeleted() != null && user.getDeleted() == 1)) {
                log.debug("用户 {} 不存在或已删除，无需清除缓存", userId);
                return;
            }
            String username = user.getUsername();
            if (StringUtils.hasText(username)) {
                String roleCacheKey = UserAuthorityConstants.USER_ROLE_PREFIX + username;
                String permissionCacheKey = UserAuthorityConstants.USER_PERMISSION_PREFIX + username;
                redisTemplate.delete(roleCacheKey);
                redisTemplate.delete(permissionCacheKey);
                log.info("已清除用户 {} 的权限缓存", username);
            }
        } catch (Exception e) {
            // 缓存清除失败不应该影响主流程，只记录日志
            log.error("清除用户 {} 的权限缓存失败：{}", userId, e.getMessage(), e);
        }
    }

    private RoleVo convertToVo(RoleEntity role) {
        if (role == null) {
            return null;
        }
        RoleVo vo = new RoleVo();
        BeanUtils.copyProperties(role, vo);
        return vo;
    }

    private UserVo convertUserToVo(UserEntity user) {
        if (user == null) {
            return null;
        }
        UserVo vo = new UserVo();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}