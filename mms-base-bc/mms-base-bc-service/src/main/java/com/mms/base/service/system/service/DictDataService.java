package com.mms.base.service.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.system.dto.*;
import com.mms.base.common.system.vo.DictDataVo;

import java.util.List;

/**
 * 实现功能【数据字典数据服务】
 * <p>
 * 提供数据字典数据管理的核心业务方法
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-23 11:21:50
 */
public interface DictDataService {

    /**
     * 分页查询数据字典数据列表
     *
     * @param dto 查询条件
     * @return 分页字典数据列表
     */
    Page<DictDataVo> getDictDataPage(DictDataPageQueryDto dto);

    /**
     * 根据字典数据ID查询字典数据详情
     *
     * @param dictDataId 字典数据ID
     * @return 字典数据信息
     */
    DictDataVo getDictDataById(Long dictDataId);

    /**
     * 根据字典类型ID查询字典数据列表
     *
     * @param dictTypeId 字典类型ID
     * @return 字典数据列表
     */
    List<DictDataVo> getDictDataListByTypeId(Long dictTypeId);

    /**
     * 根据字典类型编码查询启用的字典数据列表
     *
     * @param dictTypeCode 字典类型编码
     * @return 字典数据列表
     */
    List<DictDataVo> getDictDataListByTypeCode(String dictTypeCode);

    /**
     * 创建数据字典数据
     *
     * @param dto 字典数据创建参数
     * @return 创建的字典数据信息
     */
    DictDataVo createDictData(DictDataCreateDto dto);

    /**
     * 更新数据字典数据信息
     *
     * @param dto 字典数据更新参数
     * @return 更新后的字典数据信息
     */
    DictDataVo updateDictData(DictDataUpdateDto dto);

    /**
     * 删除数据字典数据（逻辑删除）
     *
     * @param dictDataId 字典数据ID
     */
    void deleteDictData(Long dictDataId);

    /**
     * 批量删除数据字典数据（逻辑删除）
     *
     * @param dto 批量删除参数
     */
    void batchDeleteDictData(DictDataBatchDeleteDto dto);

    /**
     * 切换数据字典数据状态（启用/禁用）
     *
     * @param dto 状态切换参数
     */
    void switchDictDataStatus(DictDataStatusSwitchDto dto);
}
