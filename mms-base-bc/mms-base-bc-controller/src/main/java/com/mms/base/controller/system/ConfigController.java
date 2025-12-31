package com.mms.base.controller.system;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.common.security.annotations.RequiresPermission;
import com.mms.common.core.constants.usercenter.PermissionConstants;
import com.mms.common.core.response.Response;
import com.mms.base.common.system.dto.*;
import com.mms.base.common.system.vo.ConfigVo;
import com.mms.base.service.system.service.ConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 实现功能【系统配置管理 Controller】
 * <p>
 * 提供系统配置管理的REST API接口
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-23 11:21:50
 */
@Tag(name = "系统配置管理", description = "系统配置管理相关接口")
@RestController
@RequestMapping("/config")
public class ConfigController {

    @Resource
    private ConfigService configService;

    @Operation(summary = "分页查询系统配置列表", description = "根据条件分页查询系统配置列表")
    @RequiresPermission(PermissionConstants.CONFIG_VIEW)
    @PostMapping("/page")
    public Response<Page<ConfigVo>> getConfigPage(@RequestBody @Valid ConfigPageQueryDto dto) {
        return Response.success(configService.getConfigPage(dto));
    }

    @Operation(summary = "根据ID查询系统配置详情", description = "根据配置ID查询系统配置详细信息")
    @RequiresPermission(PermissionConstants.CONFIG_VIEW)
    @GetMapping("/{configId}")
    public Response<ConfigVo> getConfigById(@PathVariable Long configId) {
        return Response.success(configService.getConfigById(configId));
    }

    @Operation(summary = "根据配置键查询系统配置", description = "根据配置键查询系统配置信息")
    @RequiresPermission(PermissionConstants.CONFIG_VIEW)
    @GetMapping("/key/{configKey}")
    public Response<ConfigVo> getConfigByKey(@PathVariable String configKey) {
        return Response.success(configService.getConfigByKey(configKey));
    }

    @Operation(summary = "创建系统配置", description = "创建新系统配置")
    @RequiresPermission(PermissionConstants.CONFIG_CREATE)
    @PostMapping("/create")
    public Response<ConfigVo> createConfig(@RequestBody @Valid ConfigCreateDto dto) {
        return Response.success(configService.createConfig(dto));
    }

    @Operation(summary = "更新系统配置信息", description = "更新系统配置的基本信息")
    @RequiresPermission(PermissionConstants.CONFIG_UPDATE)
    @PutMapping("/update")
    public Response<ConfigVo> updateConfig(@RequestBody @Valid ConfigUpdateDto dto) {
        return Response.success(configService.updateConfig(dto));
    }

    @Operation(summary = "删除系统配置", description = "逻辑删除系统配置（软删除）")
    @RequiresPermission(PermissionConstants.CONFIG_DELETE)
    @DeleteMapping("/{configId}")
    public Response<Void> deleteConfig(@PathVariable Long configId) {
        configService.deleteConfig(configId);
        return Response.success();
    }

    @Operation(summary = "批量删除系统配置", description = "批量逻辑删除系统配置（软删除）")
    @RequiresPermission(PermissionConstants.CONFIG_DELETE)
    @PostMapping("/batch-delete")
    public Response<Void> batchDeleteConfig(@RequestBody @Valid ConfigBatchDeleteDto dto) {
        configService.batchDeleteConfig(dto);
        return Response.success();
    }

    @Operation(summary = "切换系统配置状态", description = "启用或禁用系统配置")
    @RequiresPermission(PermissionConstants.CONFIG_UPDATE)
    @PostMapping("/switch-status")
    public Response<Void> switchConfigStatus(@RequestBody @Valid ConfigStatusSwitchDto dto) {
        configService.switchConfigStatus(dto);
        return Response.success();
    }

    @Operation(summary = "检查配置键是否存在", description = "检查配置键是否已被使用")
    @GetMapping("/check-key/{configKey}")
    public Response<Boolean> checkConfigKey(@PathVariable String configKey) {
        return Response.success(configService.existsByConfigKey(configKey));
    }
}
