package com.mms.usercenter.service.org.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.usercenter.common.org.dto.DeptPageQueryDto;
import com.mms.usercenter.common.org.entity.DeptEntity;
import com.mms.usercenter.common.org.vo.DeptVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 实现功能【部门实体 Mapper】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-01-23 10:32:53
 */
@Mapper
public interface DeptMapper extends BaseMapper<DeptEntity> {

    /**
     * 分页查询部门列表
     *
     * @param page 分页对象
     * @param dto  查询条件
     * @return 分页部门列表
     */
    Page<DeptVo> getDeptPage(@Param("page") Page<DeptVo> page, @Param("dto") DeptPageQueryDto dto);
}