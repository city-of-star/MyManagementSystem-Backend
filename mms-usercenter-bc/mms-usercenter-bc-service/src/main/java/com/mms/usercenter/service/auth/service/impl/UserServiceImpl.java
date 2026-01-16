package com.mms.usercenter.service.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.core.exceptions.ServerException;
import com.mms.usercenter.common.auth.dto.*;
import com.mms.usercenter.common.auth.entity.RoleEntity;
import com.mms.usercenter.common.auth.entity.UserEntity;
import com.mms.usercenter.common.auth.entity.UserRoleEntity;
import com.mms.usercenter.common.auth.vo.UserVo;
import com.mms.common.core.constants.usercenter.UserAuthorityConstants;
import com.mms.usercenter.common.security.constants.SuperAdminInfoConstants;
import com.mms.usercenter.service.auth.mapper.RoleMapper;
import com.mms.usercenter.service.auth.mapper.UserMapper;
import com.mms.usercenter.service.auth.mapper.UserRoleMapper;
import com.mms.usercenter.service.auth.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
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

/**
 * 实现功能【用户服务实现类】
 * <p>
 * 提供用户管理的核心业务逻辑实现
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-19 09:51:27
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Page<UserVo> getUserPage(UserPageQueryDto dto) {
        try {
            log.info("分页查询用户列表，参数：{}", dto);
            Page<UserVo> page = new Page<>(dto.getPageNum(), dto.getPageSize());
            return userMapper.getUserPage(page, dto);
        } catch (Exception e) {
            log.error("分页查询用户列表失败：{}", e.getMessage(), e);
            throw new ServerException("查询用户列表失败", e);
        }
    }

    @Override
    public UserVo getUserById(Long userId) {
        try {
            log.info("根据ID查询用户，userId：{}", userId);
            if (userId == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "用户ID不能为空");
            }
            UserEntity user = userMapper.selectById(userId);
            if (user == null) {
                throw new BusinessException(ErrorCode.USER_NOT_FOUND);
            }
            return convertToVo(user);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("根据ID查询用户失败：{}", e.getMessage(), e);
            throw new ServerException("查询用户失败", e);
        }
    }

    @Override
    public UserVo getUserByUsername(String username) {
        try {
            log.info("根据用户名查询用户，username：{}", username);
            if (!StringUtils.hasText(username)) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "用户名不能为空");
            }
            UserEntity user = userMapper.selectByUsername(username);
            if (user == null) {
                throw new BusinessException(ErrorCode.USER_NOT_FOUND);
            }
            return convertToVo(user);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("根据用户名查询用户失败：{}", e.getMessage(), e);
            throw new ServerException("查询用户失败", e);
        }
    }

    @Override
    public UserVo getUserByEmail(String email) {
        try {
            log.info("根据邮箱查询用户，email：{}", email);
            if (!StringUtils.hasText(email)) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "邮箱不能为空");
            }
            UserEntity user = userMapper.selectByEmail(email);
            if (user == null) {
                throw new BusinessException(ErrorCode.USER_NOT_FOUND);
            }
            return convertToVo(user);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("根据邮箱查询用户失败：{}", e.getMessage(), e);
            throw new ServerException("查询用户失败", e);
        }
    }

    @Override
    public UserVo getUserByPhone(String phone) {
        try {
            log.info("根据手机号查询用户，phone：{}", phone);
            if (!StringUtils.hasText(phone)) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "手机号不能为空");
            }
            UserEntity user = userMapper.selectByPhone(phone);
            if (user == null) {
                throw new BusinessException(ErrorCode.USER_NOT_FOUND);
            }
            return convertToVo(user);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("根据手机号查询用户失败：{}", e.getMessage(), e);
            throw new ServerException("查询用户失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVo createUser(UserCreateDto dto) {
        try {
            log.info("创建用户，参数：{}", dto);
            // 检查用户名是否存在
            if (existsByUsername(dto.getUsername())) {
                throw new BusinessException(ErrorCode.USERNAME_EXISTS);
            }
            // 检查邮箱是否存在
            if (StringUtils.hasText(dto.getEmail()) && existsByEmail(dto.getEmail())) {
                throw new BusinessException(ErrorCode.EMAIL_EXISTS);
            }
            // 检查手机号是否存在
            if (StringUtils.hasText(dto.getPhone()) && existsByPhone(dto.getPhone())) {
                throw new BusinessException(ErrorCode.PHONE_EXISTS);
            }
            // 创建用户实体
            UserEntity user = new UserEntity();
            BeanUtils.copyProperties(dto, user);
            // 加密密码
            user.setPassword(BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt()));
            // 设置默认值
            if (user.getStatus() == null) {
                user.setStatus(1);
            }
            user.setLocked(0);
            user.setDeleted(0);
            // 保存用户
            userMapper.insert(user);
            log.info("创建用户成功，userId：{}", user.getId());
            return convertToVo(user);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建用户失败：{}", e.getMessage(), e);
            throw new ServerException("创建用户失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVo updateUser(UserUpdateDto dto) {
        try {
            log.info("更新用户信息，参数：{}", dto);
            // 查询用户
            UserEntity user = userMapper.selectById(dto.getId());
            if (user == null) {
                throw new BusinessException(ErrorCode.USER_NOT_FOUND);
            }
            // 超级管理员用户不可修改
            if (Objects.equals(dto.getId(), SuperAdminInfoConstants.SUPER_ADMIN_USER_ID)) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "超级管理员用户不可修改");
            }
            // 检查邮箱是否被其他用户使用（排除当前用户）
            if (StringUtils.hasText(dto.getEmail()) && !dto.getEmail().equals(user.getEmail())) {
                LambdaQueryWrapper<UserEntity> emailWrapper = new LambdaQueryWrapper<>();
                emailWrapper.eq(UserEntity::getEmail, dto.getEmail())
                        .eq(UserEntity::getDeleted, 0)
                        .ne(UserEntity::getId, dto.getId());
                if (userMapper.selectCount(emailWrapper) > 0) {
                    throw new BusinessException(ErrorCode.EMAIL_EXISTS);
                }
            }
            // 检查手机号是否被其他用户使用（排除当前用户）
            if (StringUtils.hasText(dto.getPhone()) && !dto.getPhone().equals(user.getPhone())) {
                LambdaQueryWrapper<UserEntity> phoneWrapper = new LambdaQueryWrapper<>();
                phoneWrapper.eq(UserEntity::getPhone, dto.getPhone())
                        .eq(UserEntity::getDeleted, 0)
                        .ne(UserEntity::getId, dto.getId());
                if (userMapper.selectCount(phoneWrapper) > 0) {
                    throw new BusinessException(ErrorCode.PHONE_EXISTS);
                }
            }
            if (StringUtils.hasText(dto.getNickname())) {
                user.setNickname(dto.getNickname());
            }
            if (StringUtils.hasText(dto.getRealName())) {
                user.setRealName(dto.getRealName());
            }
            if (StringUtils.hasText(dto.getAvatar())) {
                user.setAvatar(dto.getAvatar());
            }
            if (StringUtils.hasText(dto.getEmail())) {
                user.setEmail(dto.getEmail());
            }
            if (StringUtils.hasText(dto.getPhone())) {
                user.setPhone(dto.getPhone());
            }
            if (dto.getGender() != null) {
                user.setGender(dto.getGender());
            }
            if (dto.getBirthday() != null) {
                user.setBirthday(dto.getBirthday());
            }
            if (dto.getStatus() != null) {
                user.setStatus(dto.getStatus());
            }
            if (StringUtils.hasText(dto.getRemark())) {
                user.setRemark(dto.getRemark());
            }
            userMapper.updateById(user);
            log.info("更新用户信息成功，userId：{}", user.getId());
            return convertToVo(user);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新用户信息失败：{}", e.getMessage(), e);
            throw new ServerException("更新用户信息失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long userId) {
        try {
            log.info("删除用户，userId：{}", userId);
            if (userId == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "用户ID不能为空");
            }
            if (userId.equals(SuperAdminInfoConstants.SUPER_ADMIN_USER_ID)) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "超级管理员用户不可删除");
            }
            UserEntity user = userMapper.selectById(userId);
            if (user == null) {
                throw new BusinessException(ErrorCode.USER_NOT_FOUND);
            }
            // 逻辑删除
            userMapper.deleteById(userId);
            log.info("删除用户成功，userId：{}", userId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除用户失败：{}", e.getMessage(), e);
            throw new ServerException("删除用户失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteUser(UserBatchDeleteDto dto) {
        try {
            log.info("批量删除用户，userIds：{}", dto.getUserIds());
            if (dto.getUserIds() == null || dto.getUserIds().isEmpty()) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "用户ID列表不能为空");
            }
            // 批量逻辑删除
            for (Long userId : dto.getUserIds()) {
                if (userId.equals(SuperAdminInfoConstants.SUPER_ADMIN_USER_ID)) {
                    throw new BusinessException(ErrorCode.PARAM_INVALID, "超级管理员用户不可删除，其他误删数据已恢复");
                }
                userMapper.deleteById(userId);
            }
            log.info("批量删除用户成功，删除数量：{}", dto.getUserIds().size());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量删除用户失败：{}", e.getMessage(), e);
            throw new ServerException("批量删除用户失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void switchUserStatus(UserStatusSwitchDto dto) {
        try {
            log.info("切换用户状态，userId：{}，status：{}", dto.getUserId(), dto.getStatus());
            UserEntity user = userMapper.selectById(dto.getUserId());
            if (user == null) {
                throw new BusinessException(ErrorCode.USER_NOT_FOUND);
            }
            // 超级管理员用户不可被禁用
            if (Objects.equals(dto.getUserId(), SuperAdminInfoConstants.SUPER_ADMIN_USER_ID) && dto.getStatus() == 0) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "超级管理员用户不可禁用");
            }
            if (dto.getStatus() != 0 && dto.getStatus() != 1) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "状态值只能是0或1");
            }
            user.setStatus(dto.getStatus());
            userMapper.updateById(user);
            log.info("切换用户状态成功，userId：{}，status：{}", dto.getUserId(), dto.getStatus());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("切换用户状态失败：{}", e.getMessage(), e);
            throw new ServerException("切换用户状态失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void lockOrUnlockUser(UserLockDto dto) {
        try {
            log.info("锁定/解锁用户，userId：{}，locked：{}", dto.getUserId(), dto.getLocked());
            UserEntity user = userMapper.selectById(dto.getUserId());
            if (user == null) {
                throw new BusinessException(ErrorCode.USER_NOT_FOUND);
            }
            // 超级管理员用户不可被锁定
            if (Objects.equals(dto.getUserId(), SuperAdminInfoConstants.SUPER_ADMIN_USER_ID) && dto.getLocked() == 1) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "超级管理员用户不可锁定");
            }
            if (dto.getLocked() != 0 && dto.getLocked() != 1) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "锁定状态值只能是0或1");
            }
            user.setLocked(dto.getLocked());
            if (dto.getLocked() == 1) {
                // 锁定
                if (!StringUtils.hasText(dto.getLockReason())) {
                    throw new BusinessException(ErrorCode.PARAM_INVALID, "锁定原因不能为空");
                }
                user.setLockTime(LocalDateTime.now());
                user.setLockReason(dto.getLockReason());
            } else {
                // 解锁
                user.setLockTime(null);
                user.setLockReason(null);
            }
            userMapper.updateById(user);
            log.info("锁定/解锁用户成功，userId：{}，locked：{}", dto.getUserId(), dto.getLocked());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("锁定/解锁用户失败：{}", e.getMessage(), e);
            throw new ServerException("锁定/解锁用户失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(UserPasswordResetDto dto) {
        try {
            log.info("重置用户密码，userId：{}", dto.getUserId());
            UserEntity user = userMapper.selectById(dto.getUserId());
            if (user == null) {
                throw new BusinessException(ErrorCode.USER_NOT_FOUND);
            }
            // 加密新密码
            String hashedPassword = BCrypt.hashpw(dto.getNewPassword(), BCrypt.gensalt());
            user.setPassword(hashedPassword);
            user.setPasswordUpdateTime(LocalDateTime.now());
            userMapper.updateById(user);
            log.info("重置用户密码成功，userId：{}", dto.getUserId());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("重置用户密码失败：{}", e.getMessage(), e);
            throw new ServerException("重置用户密码失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(Long userId, UserPasswordChangeDto dto) {
        try {
            log.info("修改用户密码，userId：{}", userId);
            UserEntity user = userMapper.selectById(userId);
            if (user == null) {
                throw new BusinessException(ErrorCode.USER_NOT_FOUND);
            }
            // 验证旧密码
            if (!BCrypt.checkpw(dto.getOldPassword(), user.getPassword())) {
                throw new BusinessException(ErrorCode.PWD_MISMATCH, "旧密码错误");
            }
            // 加密新密码
            String hashedPassword = BCrypt.hashpw(dto.getNewPassword(), BCrypt.gensalt());
            user.setPassword(hashedPassword);
            user.setPasswordUpdateTime(LocalDateTime.now());
            userMapper.updateById(user);
            log.info("修改用户密码成功，userId：{}", userId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("修改用户密码失败：{}", e.getMessage(), e);
            throw new ServerException("修改用户密码失败", e);
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        try {
            if (!StringUtils.hasText(username)) {
                return false;
            }
            LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserEntity::getUsername, username)
                    .eq(UserEntity::getDeleted, 0);
            return userMapper.selectCount(wrapper) > 0;
        } catch (Exception e) {
            log.error("检查用户名是否存在失败：{}", e.getMessage(), e);
            throw new ServerException("检查用户名是否存在失败", e);
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        try {
            if (!StringUtils.hasText(email)) {
                return false;
            }
            LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserEntity::getEmail, email)
                    .eq(UserEntity::getDeleted, 0);
            return userMapper.selectCount(wrapper) > 0;
        } catch (Exception e) {
            log.error("检查邮箱是否存在失败：{}", e.getMessage(), e);
            throw new ServerException("检查邮箱是否存在失败", e);
        }
    }

    @Override
    public boolean existsByPhone(String phone) {
        try {
            if (!StringUtils.hasText(phone)) {
                return false;
            }
            LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserEntity::getPhone, phone)
                    .eq(UserEntity::getDeleted, 0);
            return userMapper.selectCount(wrapper) > 0;
        } catch (Exception e) {
            log.error("检查手机号是否存在失败：{}", e.getMessage(), e);
            throw new ServerException("检查手机号是否存在失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(UserAssignRoleDto dto) {
        try {
            log.info("为用户分配角色，userId：{}，roleIds：{}", dto.getUserId(), dto.getRoleIds());
            if (dto.getUserId() == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "用户ID不能为空");
            }
            UserEntity user = userMapper.selectById(dto.getUserId());
            if (user == null) {
                throw new BusinessException(ErrorCode.USER_NOT_FOUND);
            }
            if (CollectionUtils.isEmpty(dto.getRoleIds())) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "角色ID列表不能为空");
            }
            // 验证角色ID是否存在且未删除
            List<RoleEntity> roles = roleMapper.selectBatchIds(dto.getRoleIds());
            if (roles.size() != dto.getRoleIds().size()) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "存在无效的角色ID");
            }
            // 检查是否有已删除的角色
            for (RoleEntity role : roles) {
                if (Objects.equals(role.getDeleted(), 1)) {
                    throw new BusinessException(ErrorCode.PARAM_INVALID, "角色ID " + role.getId() + " 已被删除");
                }
            }
            if (Objects.equals(dto.getUserId(), SuperAdminInfoConstants.SUPER_ADMIN_USER_ID) 
                    && !dto.getRoleIds().contains(SuperAdminInfoConstants.SUPER_ADMIN_ROLE_ID)) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "超级管理员用户必须有超级管理员角色，请重新分配");
            }
            saveUserRoles(dto.getUserId(), dto.getRoleIds());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("分配用户角色失败：{}", e.getMessage(), e);
            throw new ServerException("分配用户角色失败", e);
        }
    }

    @Override
    public List<Long> listRoleIdsByUserId(Long userId) {
        try {
            log.info("查询用户角色ID列表，userId：{}", userId);
            LambdaQueryWrapper<UserRoleEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserRoleEntity::getUserId, userId);
            return userRoleMapper.selectList(wrapper).stream()
                    .map(UserRoleEntity::getRoleId)
                    .toList();
        } catch (Exception e) {
            log.error("查询用户角色ID列表失败：{}", e.getMessage(), e);
            throw new ServerException("查询用户角色ID列表失败", e);
        }
    }

    // ==================== 私有工具方法 ====================

    /**
     * 保存用户角色关联
     */
    private void saveUserRoles(Long userId, List<Long> roleIds) {
        // 先清空旧关联
        LambdaQueryWrapper<UserRoleEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRoleEntity::getUserId, userId);
        userRoleMapper.delete(wrapper);
        if (CollectionUtils.isEmpty(roleIds)) {
            // 即使角色列表为空，也需要清除用户的权限缓存
            clearUserAuthorityCacheByUserId(userId);
            return;
        }
        List<UserRoleEntity> entities = new ArrayList<>();
        // 去重，避免唯一键冲突
        List<Long> distinctIds = roleIds.stream().distinct().toList();
        for (Long roleId : distinctIds) {
            UserRoleEntity entity = new UserRoleEntity();
            entity.setUserId(userId);
            entity.setRoleId(roleId);
            entity.setCreateTime(LocalDateTime.now());
            entities.add(entity);
        }
        // 批量插入（如果数据量较大，可以考虑使用 MyBatis-Plus 批量插入插件）
        for (UserRoleEntity entity : entities) {
            userRoleMapper.insert(entity);
        }
        // 清除该用户的权限缓存，确保角色变更立即生效
        clearUserAuthorityCacheByUserId(userId);
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
            if (user == null) {
                log.debug("用户 {} 不存在，无需清除缓存", userId);
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

    // ==================== 实体转换方法 ====================

    /**
     * 将 UserEntity 转换为 UserVo
     *
     * @param entity 用户实体
     * @return 用户VO
     */
    private UserVo convertToVo(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        UserVo vo = new UserVo();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}