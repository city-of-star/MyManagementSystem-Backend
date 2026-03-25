package com.mms.usercenter.service.audit.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.core.exceptions.ServerException;
import com.mms.usercenter.common.audit.dto.UserLoginLogBatchDeleteDto;
import com.mms.usercenter.common.audit.dto.UserLoginLogPageQueryDto;
import com.mms.usercenter.common.audit.entity.UserLoginLogEntity;
import com.mms.usercenter.common.audit.vo.UserLoginLogVo;
import com.mms.usercenter.service.audit.mapper.UserLoginLogMapper;
import com.mms.usercenter.service.audit.service.UserLoginLogService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 实现功能【用户登录日志服务实现类】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-03-24 10:51:25
 */
@Slf4j
@Service
public class UserLoginLogServiceImpl implements UserLoginLogService {

    @Resource
    private UserLoginLogMapper userLoginLogMapper;

    @Override
    public Page<UserLoginLogVo> getUserLoginLogPage(UserLoginLogPageQueryDto dto) {
        try {
            Page<UserLoginLogVo> page = new Page<>(dto.getPageNum(), dto.getPageSize());
            return userLoginLogMapper.getUserLoginLogPage(page, dto);
        } catch (Exception e) {
            log.error("分页查询用户登录日志失败：{}", e.getMessage(), e);
            throw new ServerException("分页查询用户登录日志失败", e);
        }
    }

    @Override
    public UserLoginLogVo getUserLoginLogById(Long logId) {
        try {
            if (logId == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "登录日志ID不能为空");
            }
            UserLoginLogVo vo = userLoginLogMapper.getUserLoginLogById(logId);
            if (vo == null) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "用户登录日志不存在");
            }
            return vo;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询用户登录日志失败：{}", e.getMessage(), e);
            throw new ServerException("查询用户登录日志失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUserLoginLog(Long logId) {
        try {
            if (logId == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "登录日志ID不能为空");
            }
            UserLoginLogEntity entity = userLoginLogMapper.selectById(logId);
            if (entity == null) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "用户登录日志不存在");
            }
            userLoginLogMapper.deleteById(logId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除用户登录日志失败：{}", e.getMessage(), e);
            throw new ServerException("删除用户登录日志失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteUserLoginLog(UserLoginLogBatchDeleteDto dto) {
        try {
            if (dto == null || dto.getLogIds() == null || dto.getLogIds().isEmpty()) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "登录日志ID列表不能为空");
            }
            for (Long id : dto.getLogIds()) {
                deleteUserLoginLog(id);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量删除用户登录日志失败：{}", e.getMessage(), e);
            throw new ServerException("批量删除用户登录日志失败", e);
        }
    }

    @Override
    public void exportUserLoginLog(UserLoginLogPageQueryDto dto) {
        // TODO：后续集成 Excel 导出功能时实现
        log.warn("导出用户登录日志功能暂未实现，参数：{}", dto);
        throw new BusinessException(ErrorCode.INVALID_OPERATION, "用户登录日志导出功能暂未实现");
    }

}