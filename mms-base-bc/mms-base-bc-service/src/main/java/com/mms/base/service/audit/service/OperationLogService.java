package com.mms.base.service.audit.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.audit.dto.OperationLogBatchDeleteDto;
import com.mms.base.common.audit.dto.OperationLogPageQueryDto;
import com.mms.base.common.audit.vo.OperationLogVo;

/**
 * 实现功能【用户操作日志服务】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-05-29 10:00:00
 */
public interface OperationLogService {

    /**
     * 分页查询用户操作日志
     */
    Page<OperationLogVo> getOperationLogPage(OperationLogPageQueryDto dto);

    /**
     * 根据ID查询用户操作日志详情
     */
    OperationLogVo getOperationLogById(Long logId);

    /**
     * 删除单条用户操作日志
     */
    void deleteOperationLog(Long logId);

    /**
     * 批量删除用户操作日志
     */
    void batchDeleteOperationLog(OperationLogBatchDeleteDto dto);

    /**
     * 导出用户操作日志
     */
    byte[] exportOperationLog(OperationLogPageQueryDto dto);
}
