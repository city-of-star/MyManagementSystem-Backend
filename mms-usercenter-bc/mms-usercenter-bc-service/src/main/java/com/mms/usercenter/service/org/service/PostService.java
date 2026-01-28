package com.mms.usercenter.service.org.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.usercenter.common.auth.dto.UserAssignPostDto;
import com.mms.usercenter.common.org.dto.*;
import com.mms.usercenter.common.org.vo.PostVo;

import java.util.List;

/**
 * 实现功能【岗位服务】
 * <p>
 * 提供岗位管理的核心业务方法
 * </p>
 *
 * @author li.hongyu
 * @date 2026-01-23 10:33:32
 */
public interface PostService {

    /**
     * 分页查询岗位列表
     *
     * @param dto 查询条件
     * @return 分页岗位列表
     */
    Page<PostVo> getPostPage(PostPageQueryDto dto);

    /**
     * 根据岗位ID查询岗位详情
     *
     * @param postId 岗位ID
     * @return 岗位信息
     */
    PostVo getPostById(Long postId);

    /**
     * 创建岗位
     *
     * @param dto 岗位创建参数
     * @return 创建的岗位信息
     */
    PostVo createPost(PostCreateDto dto);

    /**
     * 更新岗位信息
     *
     * @param dto 岗位更新参数
     * @return 更新后的岗位信息
     */
    PostVo updatePost(PostUpdateDto dto);

    /**
     * 删除岗位（逻辑删除）
     *
     * @param postId 岗位ID
     */
    void deletePost(Long postId);

    /**
     * 批量删除岗位（逻辑删除）
     *
     * @param dto 批量删除参数
     */
    void batchDeletePost(PostBatchDeleteDto dto);

    /**
     * 切换岗位状态（启用/禁用）
     *
     * @param dto 状态切换参数
     */
    void switchPostStatus(PostStatusSwitchDto dto);

    /**
     * 为用户分配岗位（覆盖）
     *
     * @param dto 用户分配岗位参数
     */
    void assignPosts(UserAssignPostDto dto);

    /**
     * 查询用户当前所属的岗位ID列表
     *
     * @param userId 用户ID
     * @return 岗位ID列表
     */
    List<Long> listPostIdsByUserId(Long userId);

    /**
     * 查询用户所属的岗位列表
     *
     * @param userId 用户ID
     * @return 岗位列表
     */
    List<PostVo> getPostListByUserId(Long userId);

    /**
     * 查询用户所属的主岗位
     *
     * @param userId 用户ID
     * @return 主岗位（可能为null）
     */
    PostVo getPrimaryPostByUserId(Long userId);
}