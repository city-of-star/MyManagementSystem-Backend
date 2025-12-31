package com.mms.base.controller.system;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.common.security.annotations.RequiresPermission;
import com.mms.common.core.constants.usercenter.PermissionConstants;
import com.mms.common.core.response.Response;
import com.mms.base.common.system.dto.*;
import com.mms.base.common.system.vo.DictTypeVo;
import com.mms.base.service.system.service.DictTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 实现功能【数据字典类型管理 Controller】
 * <p>
 * 提供数据字典类型管理的REST API接口
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-23 11:21:50
 */
@Tag(name = "数据字典类型管理", description = "数据字典类型管理相关接口")
@RestController
@RequestMapping("/dict-type")
public class DictTypeController {

    @Resource
    private DictTypeService dictTypeService;

    @Operation(summary = "分页查询数据字典类型列表", description = "根据条件分页查询数据字典类型列表")
    @RequiresPermission(PermissionConstants.DICT_VIEW)
    @PostMapping("/page")
    public Response<Page<DictTypeVo>> getDictTypePage(@RequestBody @Valid DictTypePageQueryDto dto) {
        return Response.success(dictTypeService.getDictTypePage(dto));
    }

    @Operation(summary = "根据ID查询数据字典类型详情", description = "根据字典类型ID查询数据字典类型详细信息")
    @RequiresPermission(PermissionConstants.DICT_VIEW)
    @GetMapping("/{dictTypeId}")
    public Response<DictTypeVo> getDictTypeById(@PathVariable Long dictTypeId) {
        return Response.success(dictTypeService.getDictTypeById(dictTypeId));
    }

    @Operation(summary = "根据字典类型编码查询数据字典类型", description = "根据字典类型编码查询数据字典类型信息")
    @RequiresPermission(PermissionConstants.DICT_VIEW)
    @GetMapping("/code/{dictTypeCode}")
    public Response<DictTypeVo> getDictTypeByCode(@PathVariable String dictTypeCode) {
        return Response.success(dictTypeService.getDictTypeByCode(dictTypeCode));
    }

    @Operation(summary = "查询所有启用的数据字典类型列表", description = "查询所有启用的数据字典类型列表")
    @RequiresPermission(PermissionConstants.DICT_VIEW)
    @GetMapping("/list-enabled")
    public Response<List<DictTypeVo>> listAllEnabledDictTypes() {
        return Response.success(dictTypeService.listAllEnabledDictTypes());
    }

    @Operation(summary = "创建数据字典类型", description = "创建新数据字典类型")
    @RequiresPermission(PermissionConstants.DICT_CREATE)
    @PostMapping("/create")
    public Response<DictTypeVo> createDictType(@RequestBody @Valid DictTypeCreateDto dto) {
        return Response.success(dictTypeService.createDictType(dto));
    }

    @Operation(summary = "更新数据字典类型信息", description = "更新数据字典类型的基本信息")
    @RequiresPermission(PermissionConstants.DICT_UPDATE)
    @PutMapping("/update")
    public Response<DictTypeVo> updateDictType(@RequestBody @Valid DictTypeUpdateDto dto) {
        return Response.success(dictTypeService.updateDictType(dto));
    }

    @Operation(summary = "删除数据字典类型", description = "逻辑删除数据字典类型（软删除）")
    @RequiresPermission(PermissionConstants.DICT_DELETE)
    @DeleteMapping("/{dictTypeId}")
    public Response<Void> deleteDictType(@PathVariable Long dictTypeId) {
        dictTypeService.deleteDictType(dictTypeId);
        return Response.success();
    }

    @Operation(summary = "批量删除数据字典类型", description = "批量逻辑删除数据字典类型（软删除）")
    @RequiresPermission(PermissionConstants.DICT_DELETE)
    @PostMapping("/batch-delete")
    public Response<Void> batchDeleteDictType(@RequestBody @Valid DictTypeBatchDeleteDto dto) {
        dictTypeService.batchDeleteDictType(dto);
        return Response.success();
    }

    @Operation(summary = "切换数据字典类型状态", description = "启用或禁用数据字典类型")
    @RequiresPermission(PermissionConstants.DICT_UPDATE)
    @PostMapping("/switch-status")
    public Response<Void> switchDictTypeStatus(@RequestBody @Valid DictTypeStatusSwitchDto dto) {
        dictTypeService.switchDictTypeStatus(dto);
        return Response.success();
    }

    @Operation(summary = "检查字典类型编码是否存在", description = "检查字典类型编码是否已被使用")
    @GetMapping("/check-code/{dictTypeCode}")
    public Response<Boolean> checkDictTypeCode(@PathVariable String dictTypeCode) {
        return Response.success(dictTypeService.existsByDictTypeCode(dictTypeCode));
    }
}
