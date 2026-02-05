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
import com.mms.usercenter.common.auth.vo.UserDetailVo;
import com.mms.usercenter.common.auth.vo.UserPageVo;
import com.mms.usercenter.common.org.entity.UserDeptEntity;
import com.mms.usercenter.common.org.entity.UserPostEntity;
import com.mms.usercenter.service.auth.mapper.RoleMapper;
import com.mms.usercenter.service.auth.mapper.UserMapper;
import com.mms.usercenter.service.auth.mapper.UserRoleMapper;
import com.mms.usercenter.service.auth.service.UserService;
import com.mms.usercenter.service.auth.utils.PasswordValidatorUtils;
import com.mms.usercenter.service.org.mapper.UserDeptMapper;
import com.mms.usercenter.service.org.mapper.UserPostMapper;
import com.mms.usercenter.service.org.service.DeptService;
import com.mms.usercenter.service.org.service.PostService;
import com.mms.usercenter.service.security.service.UserAuthorityService;
import com.mms.usercenter.service.security.utils.SecurityUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.BeanUtils;
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
    private RoleMapper roleMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private UserDeptMapper userDeptMapper;

    @Resource
    private UserPostMapper userPostMapper;

    @Resource
    private DeptService deptService;

    @Resource
    private PostService postService;

    @Resource
    private UserAuthorityService userAuthorityService;

    @Override
    public Page<UserPageVo> getUserPage(UserPageQueryDto dto) {
        try {
            log.info("分页查询用户列表，参数：{}", dto);
            Page<UserPageVo> page = new Page<>(dto.getPageNum(), dto.getPageSize());
            return userMapper.getUserPage(page, dto);
        } catch (Exception e) {
            log.error("分页查询用户列表失败：{}", e.getMessage(), e);
            throw new ServerException("查询用户列表失败", e);
        }
    }

    @Override
    public UserDetailVo getUserById(Long userId) {
        try {
            log.info("根据ID查询用户，userId：{}", userId);
            if (userId == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "用户ID不能为空");
            }
            UserEntity user = userMapper.selectById(userId);
            if (user == null) {
                throw new BusinessException(ErrorCode.USER_NOT_FOUND);
            }
            UserDetailVo vo = convertToVo(user);
            // 查询部门、岗位信息
            vo.setPrimaryDept(deptService.getPrimaryDeptByUserId(userId));
            vo.setPrimaryPost(postService.getPrimaryPostByUserId(userId));
            vo.setDepts(deptService.getDeptListByUserId(userId));
            vo.setPosts(postService.getPostListByUserId(userId));
            return vo;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("根据ID查询用户失败：{}", e.getMessage(), e);
            throw new ServerException("查询用户失败", e);
        }
    }

    @Override
    public UserDetailVo getUserByUsername(String username) {
        try {
            log.info("根据用户名查询用户，username：{}", username);
            if (!StringUtils.hasText(username)) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "用户名不能为空");
            }
            UserEntity user = userMapper.selectByUsername(username);
            if (user == null) {
                throw new BusinessException(ErrorCode.USER_NOT_FOUND);
            }
            UserDetailVo vo = convertToVo(user);
            Long userId = user.getId();
            // 查询部门、岗位信息
            vo.setPrimaryDept(deptService.getPrimaryDeptByUserId(userId));
            vo.setPrimaryPost(postService.getPrimaryPostByUserId(userId));
            vo.setDepts(deptService.getDeptListByUserId(userId));
            vo.setPosts(postService.getPostListByUserId(userId));
            return vo;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("根据用户名查询用户失败：{}", e.getMessage(), e);
            throw new ServerException("查询用户失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDetailVo createUser(UserCreateDto dto) {
        try {
            log.info("创建用户，参数：{}", dto);
            // 校验密码复杂度
            PasswordValidatorUtils.validate(dto.getPassword());
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
            // 处理部门关联
            if (!CollectionUtils.isEmpty(dto.getDeptIds())) {
                UserAssignDeptDto userAssignDeptDto = new UserAssignDeptDto();
                userAssignDeptDto.setUserId(user.getId());
                userAssignDeptDto.setDeptIds(dto.getDeptIds());
                userAssignDeptDto.setPrimaryDeptId(dto.getPrimaryDeptId());
                deptService.assignDepts(userAssignDeptDto);
            }
            // 处理岗位关联
            if (!CollectionUtils.isEmpty(dto.getPostIds())) {
                UserAssignPostDto userAssignPostDto = new UserAssignPostDto();
                userAssignPostDto.setUserId(user.getId());
                userAssignPostDto.setPostIds(dto.getPostIds());
                userAssignPostDto.setPrimaryPostId(dto.getPrimaryPostId());
                postService.assignPosts(userAssignPostDto);
            }
            UserDetailVo vo = convertToVo(user);
            // 回显部门、岗位信息
            UserDetailVo userInfo = getUserByUsername(dto.getUsername());
            vo.setPrimaryDept(deptService.getPrimaryDeptByUserId(userInfo.getId()));
            vo.setPrimaryPost(postService.getPrimaryPostByUserId(userInfo.getId()));
            vo.setDepts(deptService.getDeptListByUserId(userInfo.getId()));
            vo.setPosts(postService.getPostListByUserId(userInfo.getId()));
            log.info("创建用户成功，userId：{}", user.getId());
            return vo;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建用户失败：{}", e.getMessage(), e);
            throw new ServerException("创建用户失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDetailVo updateUser(UserUpdateDto dto) {
        try {
            log.info("更新用户信息，参数：{}", dto);
            // 查询用户
            UserEntity user = userMapper.selectById(dto.getId());
            if (user == null) {
                throw new BusinessException(ErrorCode.USER_NOT_FOUND);
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
            if (StringUtils.hasText(dto.getRemark())) {
                user.setRemark(dto.getRemark());
            }
            userMapper.updateById(user);
            // 如果前端传了部门/岗位列表，则覆盖更新关联关系
            if (dto.getDeptIds() != null) {
                UserAssignDeptDto userAssignDeptDto = new UserAssignDeptDto();
                if (CollectionUtils.isEmpty(dto.getDeptIds())) {
                    // 清空部门关联
                    userAssignDeptDto.setUserId(user.getId());
                    userAssignDeptDto.setDeptIds(new ArrayList<>());
                    userAssignDeptDto.setPrimaryDeptId(null);
                    deptService.assignDepts(userAssignDeptDto);
                } else {
                    userAssignDeptDto.setUserId(user.getId());
                    userAssignDeptDto.setDeptIds(dto.getDeptIds());
                    userAssignDeptDto.setPrimaryDeptId(dto.getPrimaryDeptId());
                    deptService.assignDepts(userAssignDeptDto);
                }
            }
            if (dto.getPostIds() != null) {
                UserAssignPostDto userAssignPostDto = new UserAssignPostDto();
                if (CollectionUtils.isEmpty(dto.getPostIds())) {
                    // 清空岗位关联
                    userAssignPostDto.setUserId(user.getId());
                    userAssignPostDto.setPostIds(new ArrayList<>());
                    userAssignPostDto.setPrimaryPostId(null);
                    postService.assignPosts(userAssignPostDto);
                } else {
                    userAssignPostDto.setUserId(user.getId());
                    userAssignPostDto.setPostIds(dto.getPostIds());
                    userAssignPostDto.setPrimaryPostId(dto.getPrimaryPostId());
                    postService.assignPosts(userAssignPostDto);
                }
            }
            UserDetailVo vo = convertToVo(user);
            // 回显部门、岗位信息
            vo.setPrimaryDept(deptService.getPrimaryDeptByUserId(dto.getId()));
            vo.setPrimaryPost(postService.getPrimaryPostByUserId(dto.getId()));
            vo.setDepts(deptService.getDeptListByUserId(dto.getId()));
            vo.setPosts(postService.getPostListByUserId(dto.getId()));
            log.info("更新用户信息成功，userId：{}", user.getId());
            return vo;
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

            // 删除部门关联
            LambdaQueryWrapper<UserDeptEntity> deptWrapper = new LambdaQueryWrapper<>();
            deptWrapper.eq(UserDeptEntity::getUserId, userId);
            userDeptMapper.delete(deptWrapper);

            // 删除岗位关联
            LambdaQueryWrapper<UserPostEntity> postWrapper = new LambdaQueryWrapper<>();
            postWrapper.eq(UserPostEntity::getUserId, userId);
            userPostMapper.delete(postWrapper);

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
                deleteUser(userId);
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
            String hashedPassword = BCrypt.hashpw("123456", BCrypt.gensalt());
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
    public void changePassword(UserPasswordChangeDto dto) {
        try {
            Long currentUserId = SecurityUtils.getUserId();
            log.info("修改用户密码，userId：{}", currentUserId);
            UserEntity user = userMapper.selectById(currentUserId);
            if (user == null) {
                throw new BusinessException(ErrorCode.USER_NOT_FOUND);
            }
            // 验证旧密码
            if (!BCrypt.checkpw(dto.getOldPassword(), user.getPassword())) {
                throw new BusinessException(ErrorCode.PWD_MISMATCH, "旧密码错误");
            }
            // 校验新密码复杂度
            PasswordValidatorUtils.validate(dto.getNewPassword());
            // 加密新密码
            String hashedPassword = BCrypt.hashpw(dto.getNewPassword(), BCrypt.gensalt());
            user.setPassword(hashedPassword);
            user.setPasswordUpdateTime(LocalDateTime.now());
            userMapper.updateById(user);
            log.info("修改用户密码成功，userId：{}", currentUserId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("修改用户密码失败：{}", e.getMessage(), e);
            throw new ServerException("修改用户密码失败", e);
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
            // 检查角色ID是否存在且未删除
            List<RoleEntity> roles = roleMapper.selectBatchIds(dto.getRoleIds());
            if (roles.size() != dto.getRoleIds().size()) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "存在无效的角色ID");
            }
            // 检查是否有已删除或者被禁用的角色
            for (RoleEntity role : roles) {
                if (Objects.equals(role.getDeleted(), 1)) {
                    throw new BusinessException(ErrorCode.PARAM_INVALID, "角色 " + role.getRoleCode() + " 已被删除");
                }
                if (Objects.equals(role.getStatus(), 0)) {
                    throw new BusinessException(ErrorCode.PARAM_INVALID, "角色 " + role.getRoleCode() + " 已被禁用，无法分配给角色");
                }
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
            userAuthorityService.clearUserAuthorityCacheByUserId(userId);
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
        // 批量插入
        for (UserRoleEntity entity : entities) {
            userRoleMapper.insert(entity);
        }
        // 清除该用户的权限缓存，确保角色变更立即生效
        userAuthorityService.clearUserAuthorityCacheByUserId(userId);
    }

    // ==================== 实体转换方法 ====================

    /**
     * 将 UserEntity 转换为 UserDetailVo
     *
     * @param entity 用户实体
     * @return 用户VO
     */
    private UserDetailVo convertToVo(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        UserDetailVo vo = new UserDetailVo();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

}