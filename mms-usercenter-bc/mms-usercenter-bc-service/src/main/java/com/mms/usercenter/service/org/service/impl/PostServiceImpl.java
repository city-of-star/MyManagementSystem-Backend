package com.mms.usercenter.service.org.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.core.exceptions.ServerException;
import com.mms.usercenter.common.auth.dto.UserAssignPostDto;
import com.mms.usercenter.common.auth.entity.UserEntity;
import com.mms.usercenter.common.org.dto.*;
import com.mms.usercenter.common.org.entity.DeptEntity;
import com.mms.usercenter.common.org.entity.PostEntity;
import com.mms.usercenter.common.org.entity.UserPostEntity;
import com.mms.usercenter.common.org.vo.PostVo;
import com.mms.usercenter.service.auth.mapper.UserMapper;
import com.mms.usercenter.service.org.mapper.PostMapper;
import com.mms.usercenter.service.org.mapper.UserPostMapper;
import com.mms.usercenter.service.org.service.PostService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
 * 实现功能【岗位服务实现类】
 * <p>
 * 提供岗位管理的核心业务逻辑实现
 * </p>
 *
 * @author li.hongyu
 * @date 2026-01-23 10:33:58
 */
@Slf4j
@Service
public class PostServiceImpl implements PostService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private PostMapper postMapper;

    @Resource
    private UserPostMapper userPostMapper;

    @Override
    public Page<PostVo> getPostPage(PostPageQueryDto dto) {
        try {
            log.info("分页查询岗位列表，参数：{}", dto);
            Page<PostVo> page = new Page<>(dto.getPageNum(), dto.getPageSize());
            return postMapper.getPostPage(page, dto);
        } catch (Exception e) {
            log.error("分页查询岗位列表失败：{}", e.getMessage(), e);
            throw new ServerException("查询岗位列表失败", e);
        }
    }

    @Override
    public PostVo getPostById(Long postId) {
        try {
            log.info("根据ID查询岗位，postId：{}", postId);
            if (postId == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "岗位ID不能为空");
            }
            PostEntity post = postMapper.selectById(postId);
            if (post == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "岗位不存在");
            }
            return convertToVo(post);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("根据ID查询岗位失败：{}", e.getMessage(), e);
            throw new ServerException("查询岗位失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PostVo createPost(PostCreateDto dto) {
        try {
            log.info("创建岗位，参数：{}", dto);
            // 检查岗位编码是否存在
            if (existsByPostCode(dto.getPostCode())) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "岗位编码已存在");
            }
            // 创建岗位实体
            PostEntity post = new PostEntity();
            BeanUtils.copyProperties(dto, post);
            // 设置默认值
            if (post.getStatus() == null) {
                post.setStatus(1);
            }
            if (post.getSortOrder() == null) {
                post.setSortOrder(0);
            }
            post.setDeleted(0);
            // 保存岗位
            postMapper.insert(post);
            log.info("创建岗位成功，postId：{}", post.getId());
            return convertToVo(post);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建岗位失败：{}", e.getMessage(), e);
            throw new ServerException("创建岗位失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PostVo updatePost(PostUpdateDto dto) {
        try {
            log.info("更新岗位信息，参数：{}", dto);
            // 查询岗位
            PostEntity post = postMapper.selectById(dto.getId());
            if (post == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "岗位不存在");
            }
            // 更新字段
            if (StringUtils.hasText(dto.getPostName())) {
                post.setPostName(dto.getPostName());
            }
            if (StringUtils.hasText(dto.getPostLevel())) {
                post.setPostLevel(dto.getPostLevel());
            }
            if (dto.getSortOrder() != null) {
                post.setSortOrder(dto.getSortOrder());
            }
            if (StringUtils.hasText(dto.getRemark())) {
                post.setRemark(dto.getRemark());
            }
            postMapper.updateById(post);
            log.info("更新岗位信息成功，postId：{}", post.getId());
            return convertToVo(post);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新岗位信息失败：{}", e.getMessage(), e);
            throw new ServerException("更新岗位信息失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePost(Long postId) {
        try {
            log.info("删除岗位，postId：{}", postId);
            if (postId == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "岗位ID不能为空");
            }
            // 查询岗位
            PostEntity post = postMapper.selectById(postId);
            if (post == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "岗位不存在");
            }
            // 逻辑删除
            postMapper.deleteById(postId);
            log.info("删除岗位成功，postId：{}", postId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除岗位失败：{}", e.getMessage(), e);
            throw new ServerException("删除岗位失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeletePost(PostBatchDeleteDto dto) {
        try {
            log.info("批量删除岗位，postIds：{}", dto.getPostIds());
            if (dto.getPostIds() == null || dto.getPostIds().isEmpty()) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "岗位ID列表不能为空");
            }
            // 批量逻辑删除
            for (Long postId : dto.getPostIds()) {
                deletePost(postId);
            }
            log.info("批量删除岗位成功，删除数量：{}", dto.getPostIds().size());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量删除岗位失败：{}", e.getMessage(), e);
            throw new ServerException("批量删除岗位失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void switchPostStatus(PostStatusSwitchDto dto) {
        try {
            log.info("切换岗位状态，postId：{}，status：{}", dto.getPostId(), dto.getStatus());
            PostEntity post = postMapper.selectById(dto.getPostId());
            if (post == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "岗位不存在");
            }
            if (dto.getStatus() != 0 && dto.getStatus() != 1) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "状态值只能是0或1");
            }
            post.setStatus(dto.getStatus());
            postMapper.updateById(post);
            log.info("切换岗位状态成功，postId：{}，status：{}", dto.getPostId(), dto.getStatus());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("切换岗位状态失败：{}", e.getMessage(), e);
            throw new ServerException("切换岗位状态失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPosts(UserAssignPostDto dto) {
        try {
            log.info("为用户分配岗位，userId：{}，postIds：{}", dto.getUserId(), dto.getPostIds());
            if (dto.getUserId() == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "用户ID不能为空");
            }
            UserEntity user = userMapper.selectById(dto.getUserId());
            if (user == null) {
                throw new BusinessException(ErrorCode.USER_NOT_FOUND);
            }
            saveUserPosts(dto.getUserId(), dto.getPostIds(), dto.getPrimaryPostId());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("分配用户岗位失败：{}", e.getMessage(), e);
            throw new ServerException("分配用户岗位失败", e);
        }
    }

    @Override
    public List<Long> listPostIdsByUserId(Long userId) {
        try {
            log.info("查询用户岗位ID列表，userId：{}", userId);
            LambdaQueryWrapper<UserPostEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserPostEntity::getUserId, userId);
            return userPostMapper.selectList(wrapper).stream()
                    .map(UserPostEntity::getPostId)
                    .toList();
        } catch (Exception e) {
            log.error("查询用户岗位ID列表失败：{}", e.getMessage(), e);
            throw new ServerException("查询用户岗位ID列表失败", e);
        }
    }

    @Override
    public List<PostVo> getPostListByUserId(Long userId) {
        try {
            log.info("查询用户岗位信息列表，userId：{}", userId);
            // 先获取岗位ID列表
            List<Long> postIds = listPostIdsByUserId(userId);
            if (CollectionUtils.isEmpty(postIds)) {
                return new ArrayList<>();
            }
            // 批量查询岗位实体
            List<PostEntity> posts = postMapper.selectBatchIds(postIds);
            // 创建ID到实体的映射
            Map<Long, PostEntity> postMap = posts.stream()
                    .collect(Collectors.toMap(PostEntity::getId, post -> post));
            // 按照原始ID列表的顺序转换为VO
            return postIds.stream()
                    .map(postMap::get)
                    .filter(Objects::nonNull)
                    .map(this::convertToVo)
                    .toList();
        } catch (Exception e) {
            log.error("查询用户岗位信息列表失败：{}", e.getMessage(), e);
            throw new ServerException("查询用户岗位信息列表失败", e);
        }
    }

    @Override
    public PostVo getPrimaryPostByUserId(Long userId) {
        try {
            log.info("查询用户主岗位信息，userId：{}", userId);
            return postMapper.getPrimaryPostByUserId(userId);
        } catch (Exception e) {
            log.error("查询用户主岗位信息失败：{}", e.getMessage(), e);
            throw new ServerException("查询用户主岗位信息失败", e);
        }
    }

    // ==================== 私有工具方法 ====================

    /**
     * 保存用户岗位关联（覆盖）
     */
    private void saveUserPosts(Long userId, List<Long> postIds, Long primaryPostId) {
        // 先清空旧关联
        LambdaQueryWrapper<UserPostEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPostEntity::getUserId, userId);
        userPostMapper.delete(wrapper);

        if (CollectionUtils.isEmpty(postIds)) {
            return;
        }

        // 校验岗位是否存在且未删除、已启用
        List<PostEntity> posts = postMapper.selectBatchIds(postIds);
        if (posts.size() != postIds.size()) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "存在无效的岗位ID");
        }
        for (PostEntity post : posts) {
            if (Objects.equals(post.getDeleted(), 1)) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "岗位 " + post.getPostName() + " 已被删除");
            }
            if (Objects.equals(post.getStatus(), 0)) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "岗位 " + post.getPostName() + " 已被禁用，无法分配");
            }
        }

        // 主岗位校验
        if (primaryPostId != null && !postIds.contains(primaryPostId)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "主岗位ID必须在岗位ID列表中");
        }

        List<Long> distinctIds = postIds.stream().distinct().toList();
        for (Long postId : distinctIds) {
            UserPostEntity entity = new UserPostEntity();
            entity.setUserId(userId);
            entity.setPostId(postId);
            entity.setIsPrimary(primaryPostId != null && Objects.equals(primaryPostId, postId) ? 1 : 0);
            entity.setCreateTime(LocalDateTime.now());
            userPostMapper.insert(entity);
        }
    }

    /**
     * 判断岗位编码是否存在
     */
    private boolean existsByPostCode(String postCode) {
        if (!StringUtils.hasText(postCode)) {
            return false;
        }
        LambdaQueryWrapper<PostEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostEntity::getPostCode, postCode)
                .eq(PostEntity::getDeleted, 0);
        return postMapper.selectCount(wrapper) > 0;
    }

    // ==================== 实体转换方法 ====================

    /**
     * 将 PostEntity 转换为 PostVo
     *
     * @param entity 岗位实体
     * @return 岗位VO
     */
    private PostVo convertToVo(PostEntity entity) {
        if (entity == null) {
            return null;
        }
        PostVo vo = new PostVo();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}