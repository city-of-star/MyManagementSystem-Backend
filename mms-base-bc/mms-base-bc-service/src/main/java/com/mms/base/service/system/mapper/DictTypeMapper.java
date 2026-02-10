package com.mms.base.service.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.system.dto.DictTypePageQueryDto;
import com.mms.base.common.system.entity.DictTypeEntity;
import com.mms.base.common.system.vo.DictTypeVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 实现功能【数据字典类型实体 Mapper】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2025-12-23 11:21:50
 */
@Mapper
public interface DictTypeMapper extends BaseMapper<DictTypeEntity> {

    /**
     * 分页查询数据字典类型列表
     *
     * @param page 分页参数
     * @param dto  查询条件
     * @return 分页结果
     */
    Page<DictTypeVo> getDictTypePage(Page<DictTypeVo> page, @Param("dto") DictTypePageQueryDto dto);
}
