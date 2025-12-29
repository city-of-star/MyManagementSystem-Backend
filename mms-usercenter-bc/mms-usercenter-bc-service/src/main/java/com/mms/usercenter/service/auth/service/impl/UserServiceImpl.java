package com.mms.usercenter.service.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.core.exceptions.ServerException;
import com.mms.usercenter.common.auth.dto.*;
import com.mms.usercenter.common.auth.entity.UserEntity;
import com.mms.usercenter.common.auth.vo.UserVo;
import com.mms.usercenter.service.auth.mapper.UserMapper;
import com.mms.usercenter.service.auth.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

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
            // 检查用户名是否被其他用户使用
            if (StringUtils.hasText(dto.getUsername()) && !dto.getUsername().equals(user.getUsername())) {
                if (existsByUsername(dto.getUsername())) {
                    throw new BusinessException(ErrorCode.USERNAME_EXISTS);
                }
            }
            // 检查邮箱是否被其他用户使用
            if (StringUtils.hasText(dto.getEmail()) && !dto.getEmail().equals(user.getEmail())) {
                if (existsByEmail(dto.getEmail())) {
                    throw new BusinessException(ErrorCode.EMAIL_EXISTS);
                }
            }
            // 检查手机号是否被其他用户使用
            if (StringUtils.hasText(dto.getPhone()) && !dto.getPhone().equals(user.getPhone())) {
                if (existsByPhone(dto.getPhone())) {
                    throw new BusinessException(ErrorCode.PHONE_EXISTS);
                }
            }
            // 更新用户信息
            if (StringUtils.hasText(dto.getUsername())) {
                user.setUsername(dto.getUsername());
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

    /**
     * 将 UserEntity 转换为 UserVo
     *
     * @param user 用户实体
     * @return 用户VO
     */
    private UserVo convertToVo(UserEntity user) {
        if (user == null) {
            return null;
        }
        UserVo vo = new UserVo();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}