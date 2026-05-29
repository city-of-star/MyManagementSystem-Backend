package com.mms.base.service.audit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.audit.dto.OperationLogPageQueryDto;
import com.mms.base.common.audit.entity.OperationLogEntity;
import com.mms.base.common.audit.vo.OperationLogVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 实现功能【用户操作日志 Mapper】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-05-29 10:00:00
 */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLogEntity> {

    /**
     * 分页查询用户操作日志
     */
    Page<OperationLogVo> getOperationLogPage(@Param("page") Page<OperationLogVo> page, @Param("dto") OperationLogPageQueryDto dto);

    /**
     * 根据ID查询用户操作日志详情
     */
    OperationLogVo getOperationLogById(@Param("logId") Long logId);
}
