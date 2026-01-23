package com.mms.usercenter.controller.org;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.common.core.response.Response;
import com.mms.usercenter.common.org.dto.*;
import com.mms.usercenter.common.org.vo.PostVo;
import com.mms.usercenter.service.org.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 实现功能【岗位管理 Controller】
 * <p>
 * 提供岗位管理的REST API接口
 * </p>
 *
 * @author li.hongyu
 * @date 2026-01-23 10:34:25
 */
@Tag(name = "岗位管理", description = "岗位管理相关接口")
@RestController
@RequestMapping("/post")
public class PostController {

    @Resource
    private PostService postService;

    @Operation(summary = "分页查询岗位列表", description = "根据条件分页查询岗位列表")
    @PostMapping("/page")
    public Response<Page<PostVo>> getPostPage(@RequestBody @Valid PostPageQueryDto dto) {
        return Response.success(postService.getPostPage(dto));
    }

    @Operation(summary = "根据ID查询岗位详情", description = "根据岗位ID查询岗位详细信息")
    @GetMapping("/{postId}")
    public Response<PostVo> getPostById(@PathVariable Long postId) {
        return Response.success(postService.getPostById(postId));
    }

    @Operation(summary = "创建岗位", description = "创建新岗位")
    @PostMapping("/create")
    public Response<PostVo> createPost(@RequestBody @Valid PostCreateDto dto) {
        return Response.success(postService.createPost(dto));
    }

    @Operation(summary = "更新岗位信息", description = "更新岗位的基本信息")
    @PutMapping("/update")
    public Response<PostVo> updatePost(@RequestBody @Valid PostUpdateDto dto) {
        return Response.success(postService.updatePost(dto));
    }

    @Operation(summary = "删除岗位", description = "逻辑删除岗位（软删除）")
    @DeleteMapping("/{postId}")
    public Response<Void> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return Response.success();
    }

    @Operation(summary = "批量删除岗位", description = "批量逻辑删除岗位（软删除）")
    @PostMapping("/batch-delete")
    public Response<Void> batchDeletePost(@RequestBody @Valid PostBatchDeleteDto dto) {
        postService.batchDeletePost(dto);
        return Response.success();
    }

    @Operation(summary = "切换岗位状态", description = "启用或禁用岗位")
    @PostMapping("/switch-status")
    public Response<Void> switchPostStatus(@RequestBody @Valid PostStatusSwitchDto dto) {
        postService.switchPostStatus(dto);
        return Response.success();
    }
}