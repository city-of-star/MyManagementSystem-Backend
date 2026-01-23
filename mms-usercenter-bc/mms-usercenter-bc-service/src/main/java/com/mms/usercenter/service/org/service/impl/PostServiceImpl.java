package com.mms.usercenter.service.org.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.core.exceptions.ServerException;
import com.mms.usercenter.common.org.dto.*;
import com.mms.usercenter.common.org.entity.PostEntity;
import com.mms.usercenter.common.org.vo.PostVo;
import com.mms.usercenter.service.org.mapper.PostMapper;
import com.mms.usercenter.service.org.service.PostService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
    private PostMapper postMapper;

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
            LambdaQueryWrapper<PostEntity> codeWrapper = new LambdaQueryWrapper<>();
            codeWrapper.eq(PostEntity::getPostCode, dto.getPostCode())
                    .eq(PostEntity::getDeleted, 0);
            if (postMapper.selectCount(codeWrapper) > 0) {
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
            // 检查岗位编码是否被其他岗位使用（排除当前岗位）
            if (StringUtils.hasText(dto.getPostCode()) && !dto.getPostCode().equals(post.getPostCode())) {
                LambdaQueryWrapper<PostEntity> codeWrapper = new LambdaQueryWrapper<>();
                codeWrapper.eq(PostEntity::getPostCode, dto.getPostCode())
                        .eq(PostEntity::getDeleted, 0)
                        .ne(PostEntity::getId, dto.getId());
                if (postMapper.selectCount(codeWrapper) > 0) {
                    throw new BusinessException(ErrorCode.PARAM_INVALID, "岗位编码已被使用");
                }
            }
            // 更新字段
            if (StringUtils.hasText(dto.getPostCode())) {
                post.setPostCode(dto.getPostCode());
            }
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