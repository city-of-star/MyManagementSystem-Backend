package com.mms.base.controller.system;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.common.security.annotations.RequiresPermission;
import com.mms.common.core.constants.usercenter.PermissionConstants;
import com.mms.common.core.response.Response;
import com.mms.base.common.system.dto.*;
import com.mms.base.common.system.vo.DictDataVo;
import com.mms.base.service.system.service.DictDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 实现功能【数据字典数据管理 Controller】
 * <p>
 * 提供数据字典数据管理的REST API接口
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-23 11:21:50
 */
@Tag(name = "数据字典数据管理", description = "数据字典数据管理相关接口")
@RestController
@RequestMapping("/dict-data")
public class DictDataController {

    @Resource
    private DictDataService dictDataService;

    @Operation(summary = "分页查询数据字典数据列表", description = "根据条件分页查询数据字典数据列表")
    @RequiresPermission(PermissionConstants.DICT_VIEW)
    @PostMapping("/page")
    public Response<Page<DictDataVo>> getDictDataPage(@RequestBody @Valid DictDataPageQueryDto dto) {
        return Response.success(dictDataService.getDictDataPage(dto));
    }

    @Operation(summary = "根据ID查询数据字典数据详情", description = "根据字典数据ID查询数据字典数据详细信息")
    @RequiresPermission(PermissionConstants.DICT_VIEW)
    @GetMapping("/{dictDataId}")
    public Response<DictDataVo> getDictDataById(@PathVariable Long dictDataId) {
        return Response.success(dictDataService.getDictDataById(dictDataId));
    }

    @Operation(summary = "根据字典类型ID查询数据字典数据列表", description = "根据字典类型ID查询数据字典数据列表")
    @RequiresPermission(PermissionConstants.DICT_VIEW)
    @GetMapping("/type-id/{dictTypeId}")
    public Response<List<DictDataVo>> getDictDataListByTypeId(@PathVariable Long dictTypeId) {
        return Response.success(dictDataService.getDictDataListByTypeId(dictTypeId));
    }

    @Operation(summary = "根据字典类型编码查询启用的数据字典数据列表", description = "根据字典类型编码查询启用的数据字典数据列表（用于前端下拉框等）")
    @GetMapping("/type-code/{dictTypeCode}")
    public Response<List<DictDataVo>> getDictDataListByTypeCode(@PathVariable String dictTypeCode) {
        return Response.success(dictDataService.getDictDataListByTypeCode(dictTypeCode));
    }

    @Operation(summary = "创建数据字典数据", description = "创建新数据字典数据")
    @RequiresPermission(PermissionConstants.DICT_CREATE)
    @PostMapping("/create")
    public Response<DictDataVo> createDictData(@RequestBody @Valid DictDataCreateDto dto) {
        return Response.success(dictDataService.createDictData(dto));
    }

    @Operation(summary = "更新数据字典数据信息", description = "更新数据字典数据的基本信息")
    @RequiresPermission(PermissionConstants.DICT_UPDATE)
    @PutMapping("/update")
    public Response<DictDataVo> updateDictData(@RequestBody @Valid DictDataUpdateDto dto) {
        return Response.success(dictDataService.updateDictData(dto));
    }

    @Operation(summary = "删除数据字典数据", description = "逻辑删除数据字典数据（软删除）")
    @RequiresPermission(PermissionConstants.DICT_DELETE)
    @DeleteMapping("/{dictDataId}")
    public Response<Void> deleteDictData(@PathVariable Long dictDataId) {
        dictDataService.deleteDictData(dictDataId);
        return Response.success();
    }

    @Operation(summary = "批量删除数据字典数据", description = "批量逻辑删除数据字典数据（软删除）")
    @RequiresPermission(PermissionConstants.DICT_DELETE)
    @PostMapping("/batch-delete")
    public Response<Void> batchDeleteDictData(@RequestBody @Valid DictDataBatchDeleteDto dto) {
        dictDataService.batchDeleteDictData(dto);
        return Response.success();
    }

    @Operation(summary = "切换数据字典数据状态", description = "启用或禁用数据字典数据")
    @RequiresPermission(PermissionConstants.DICT_UPDATE)
    @PostMapping("/switch-status")
    public Response<Void> switchDictDataStatus(@RequestBody @Valid DictDataStatusSwitchDto dto) {
        dictDataService.switchDictDataStatus(dto);
        return Response.success();
    }
}
