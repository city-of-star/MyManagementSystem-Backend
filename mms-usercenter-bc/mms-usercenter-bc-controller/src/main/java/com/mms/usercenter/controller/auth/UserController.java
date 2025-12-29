package com.mms.usercenter.controller.auth;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.common.core.annotations.RequiresPermission;
import com.mms.common.core.response.Response;
import com.mms.usercenter.common.auth.dto.*;
import com.mms.usercenter.common.auth.vo.UserVo;
import com.mms.common.core.constants.usercenter.PermissionConstants;
import com.mms.usercenter.service.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 实现功能【用户管理 Controller】
 * <p>
 * 提供用户管理的REST API接口
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-19 10:00:00
 */
@Tag(name = "用户管理", description = "用户管理相关接口")
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @Operation(summary = "分页查询用户列表", description = "根据条件分页查询用户列表")
    @RequiresPermission(PermissionConstants.USER_VIEW)
    @PostMapping("/page")
    public Response<Page<UserVo>> getUserPage(@RequestBody @Valid UserPageQueryDto dto) {
        return Response.success(userService.getUserPage(dto));
    }

    @Operation(summary = "根据ID查询用户详情", description = "根据用户ID查询用户详细信息")
    @RequiresPermission(PermissionConstants.USER_VIEW)
    @GetMapping("/{userId}")
    public Response<UserVo> getUserById(@PathVariable Long userId) {
        return Response.success(userService.getUserById(userId));
    }

    @Operation(summary = "根据用户名查询用户", description = "根据用户名查询用户信息")
    @RequiresPermission(PermissionConstants.USER_VIEW)
    @GetMapping("/username/{username}")
    public Response<UserVo> getUserByUsername(@PathVariable String username) {
        return Response.success(userService.getUserByUsername(username));
    }

    @Operation(summary = "根据邮箱查询用户", description = "根据邮箱查询用户信息")
    @RequiresPermission(PermissionConstants.USER_VIEW)
    @GetMapping("/email/{email}")
    public Response<UserVo> getUserByEmail(@PathVariable String email) {
        return Response.success(userService.getUserByEmail(email));
    }

    @Operation(summary = "根据手机号查询用户", description = "根据手机号查询用户信息")
    @RequiresPermission(PermissionConstants.USER_VIEW)
    @GetMapping("/phone/{phone}")
    public Response<UserVo> getUserByPhone(@PathVariable String phone) {
        return Response.success(userService.getUserByPhone(phone));
    }

    @Operation(summary = "创建用户", description = "创建新用户")
    @RequiresPermission(PermissionConstants.USER_CREATE)
    @PostMapping("/create")
    public Response<UserVo> createUser(@RequestBody @Valid UserCreateDto dto) {
        return Response.success(userService.createUser(dto));
    }

    @Operation(summary = "更新用户信息", description = "更新用户的基本信息")
    @RequiresPermission(PermissionConstants.USER_UPDATE)
    @PutMapping("/update")
    public Response<UserVo> updateUser(@RequestBody @Valid UserUpdateDto dto) {
        return Response.success(userService.updateUser(dto));
    }

    @Operation(summary = "删除用户", description = "逻辑删除用户（软删除）")
    @RequiresPermission(PermissionConstants.USER_DELETE)
    @DeleteMapping("/{userId}")
    public Response<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return Response.success();
    }

    @Operation(summary = "批量删除用户", description = "批量逻辑删除用户（软删除）")
    @RequiresPermission(PermissionConstants.USER_DELETE)
    @PostMapping("/batch-delete")
    public Response<Void> batchDeleteUser(@RequestBody @Valid UserBatchDeleteDto dto) {
        userService.batchDeleteUser(dto);
        return Response.success();
    }

    @Operation(summary = "切换用户状态", description = "启用或禁用用户")
    @RequiresPermission(PermissionConstants.USER_UPDATE)
    @PostMapping("/switch-status")
    public Response<Void> switchUserStatus(@RequestBody @Valid UserStatusSwitchDto dto) {
        userService.switchUserStatus(dto);
        return Response.success();
    }

    @Operation(summary = "锁定/解锁用户", description = "锁定或解锁用户账号")
    @RequiresPermission(PermissionConstants.USER_UNLOCK)
    @PostMapping("/lock")
    public Response<Void> lockOrUnlockUser(@RequestBody @Valid UserLockDto dto) {
        userService.lockOrUnlockUser(dto);
        return Response.success();
    }

    @Operation(summary = "重置用户密码", description = "管理员重置用户密码")
    @RequiresPermission(PermissionConstants.USER_RESET_PASSWORD)
    @PostMapping("/reset-password")
    public Response<Void> resetPassword(@RequestBody @Valid UserPasswordResetDto dto) {
        userService.resetPassword(dto);
        return Response.success();
    }

    @Operation(summary = "修改用户密码", description = "用户自己修改密码")
    @PostMapping("/change-password/{userId}")
    public Response<Void> changePassword(@PathVariable Long userId, @RequestBody @Valid UserPasswordChangeDto dto) {
        userService.changePassword(userId, dto);
        return Response.success();
    }

    @Operation(summary = "检查用户名是否存在", description = "检查用户名是否已被使用")
    @GetMapping("/check-username/{username}")
    public Response<Boolean> checkUsername(@PathVariable String username) {
        return Response.success(userService.existsByUsername(username));
    }

    @Operation(summary = "检查邮箱是否存在", description = "检查邮箱是否已被使用")
    @GetMapping("/check-email/{email}")
    public Response<Boolean> checkEmail(@PathVariable String email) {
        return Response.success(userService.existsByEmail(email));
    }

    @Operation(summary = "检查手机号是否存在", description = "检查手机号是否已被使用")
    @GetMapping("/check-phone/{phone}")
    public Response<Boolean> checkPhone(@PathVariable String phone) {
        return Response.success(userService.existsByPhone(phone));
    }
}
