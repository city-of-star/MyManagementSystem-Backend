package com.mms.base.service.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.system.dto.ConfigPageQueryDto;
import com.mms.base.common.system.entity.ConfigEntity;
import com.mms.base.common.system.vo.ConfigVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 实现功能【系统配置实体 Mapper】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2025-12-23 11:21:50
 */
@Mapper
public interface ConfigMapper extends BaseMapper<ConfigEntity> {

    /**
     * 分页查询系统配置列表
     *
     * @param page 分页参数
     * @param dto  查询条件
     * @return 分页结果
     */
    Page<ConfigVo> getConfigPage(Page<ConfigVo> page, @Param("dto") ConfigPageQueryDto dto);
}
