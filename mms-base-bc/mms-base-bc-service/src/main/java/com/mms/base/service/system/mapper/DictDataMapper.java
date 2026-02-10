package com.mms.base.service.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.system.dto.DictDataPageQueryDto;
import com.mms.base.common.system.entity.DictDataEntity;
import com.mms.base.common.system.vo.DictDataVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 实现功能【数据字典数据实体 Mapper】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2025-12-23 11:21:50
 */
@Mapper
public interface DictDataMapper extends BaseMapper<DictDataEntity> {

    /**
     * 分页查询数据字典数据列表
     *
     * @param page 分页参数
     * @param dto  查询条件
     * @return 分页结果
     */
    Page<DictDataVo> getDictDataPage(Page<DictDataVo> page, @Param("dto") DictDataPageQueryDto dto);
}
