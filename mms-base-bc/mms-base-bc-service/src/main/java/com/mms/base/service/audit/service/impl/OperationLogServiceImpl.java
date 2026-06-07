package com.mms.base.service.audit.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.audit.dto.OperationLogBatchDeleteDto;
import com.mms.base.common.audit.dto.OperationLogPageQueryDto;
import com.mms.base.common.audit.entity.OperationLogEntity;
import com.mms.base.common.audit.vo.OperationLogVo;
import com.mms.base.service.audit.mapper.OperationLogMapper;
import com.mms.base.service.audit.service.OperationLogService;
import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.core.exceptions.ServerException;
import com.mms.common.document.service.ExcelExportService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 实现功能【用户操作日志服务实现类】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-05-29 10:00:00
 */
@Slf4j
@Service
public class OperationLogServiceImpl implements OperationLogService {

    @Resource
    private OperationLogMapper operationLogMapper;

    @Resource
    private ExcelExportService excelExportService;

    @Override
    public Page<OperationLogVo> getOperationLogPage(OperationLogPageQueryDto dto) {
        try {
            Page<OperationLogVo> page = new Page<>(dto.getPageNum(), dto.getPageSize());
            return operationLogMapper.getOperationLogPage(page, dto);
        } catch (Exception e) {
            log.error("分页查询用户操作日志失败：{}", e.getMessage(), e);
            throw new ServerException("分页查询用户操作日志失败", e);
        }
    }

    @Override
    public OperationLogVo getOperationLogById(Long logId) {
        try {
            if (logId == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "操作日志ID不能为空");
            }
            OperationLogVo vo = operationLogMapper.getOperationLogById(logId);
            if (vo == null) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "用户操作日志不存在");
            }
            return vo;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询用户操作日志失败：{}", e.getMessage(), e);
            throw new ServerException("查询用户操作日志失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOperationLog(Long logId) {
        try {
            if (logId == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "操作日志ID不能为空");
            }
            OperationLogEntity entity = operationLogMapper.selectById(logId);
            if (entity == null) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "用户操作日志不存在");
            }
            operationLogMapper.deleteById(logId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除用户操作日志失败：{}", e.getMessage(), e);
            throw new ServerException("删除用户操作日志失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteOperationLog(OperationLogBatchDeleteDto dto) {
        try {
            if (dto == null || dto.getLogIds() == null || dto.getLogIds().isEmpty()) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "操作日志ID列表不能为空");
            }
            for (Long id : dto.getLogIds()) {
                deleteOperationLog(id);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量删除用户操作日志失败：{}", e.getMessage(), e);
            throw new ServerException("批量删除用户操作日志失败", e);
        }
    }

    @Override
    public byte[] exportOperationLog(OperationLogPageQueryDto dto) {
        try {
            Page<OperationLogVo> page = new Page<>(1, 10000);
            Page<OperationLogVo> resultPage = operationLogMapper.getOperationLogPage(page, dto);
            return excelExportService.exportToBytes("用户操作日志", OperationLogVo.class, resultPage.getRecords());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出用户操作日志失败：{}", e.getMessage(), e);
            throw new ServerException("导出用户操作日志失败", e);
        }
    }
}
