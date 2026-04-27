package com.mms.usercenter.controller.auth;

import com.mms.common.core.response.Response;
import com.mms.usercenter.common.auth.dto.UserPreferenceBatchSaveDto;
import com.mms.usercenter.common.auth.dto.UserPreferenceSaveDto;
import com.mms.usercenter.common.auth.vo.UserPreferenceVo;
import com.mms.usercenter.service.auth.service.UserPreferenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 实现功能【用户偏好配置 Controller】
 * <p>
 * 提供当前用户偏好配置查询与保存接口
 * </p>
 *
 * @author li.hongyu
 * @date 2026-04-27 15:00:00
 */
@Tag(name = "用户偏好配置", description = "用户偏好配置相关接口")
@RestController
@RequestMapping("/preference")
public class UserPreferenceController {

    @Resource
    private UserPreferenceService userPreferenceService;

    @Operation(summary = "查询当前用户全部偏好配置", description = "返回当前登录用户的偏好配置列表")
    @GetMapping("/my")
    public Response<List<UserPreferenceVo>> getMyPreferences() {
        return Response.success(userPreferenceService.getMyPreferences());
    }

    @Operation(summary = "保存当前用户偏好配置", description = "创建或更新当前登录用户单个偏好配置")
    @PostMapping("/my/save")
    public Response<UserPreferenceVo> saveMyPreference(@RequestBody @Valid UserPreferenceSaveDto dto) {
        return Response.success(userPreferenceService.saveMyPreference(dto));
    }

    @Operation(summary = "批量保存当前用户偏好配置", description = "批量创建或更新当前登录用户偏好配置")
    @PostMapping("/my/batch-save")
    public Response<List<UserPreferenceVo>> batchSaveMyPreferences(@RequestBody @Valid UserPreferenceBatchSaveDto dto) {
        return Response.success(userPreferenceService.batchSaveMyPreferences(dto));
    }
}
