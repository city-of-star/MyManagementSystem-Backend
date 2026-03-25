package com.mms.usercenter.service.audit.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.usercenter.common.audit.dto.UserLoginLogBatchDeleteDto;
import com.mms.usercenter.common.audit.dto.UserLoginLogPageQueryDto;
import com.mms.usercenter.common.audit.vo.UserLoginLogVo;

/**
 * 实现功能【用户登录日志服务】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-03-24 10:51:11
 */
public interface UserLoginLogService {

    /**
     * 分页查询用户登录日志
     */
    Page<UserLoginLogVo> getUserLoginLogPage(UserLoginLogPageQueryDto dto);

    /**
     * 根据ID查询用户登录日志详情
     */
    UserLoginLogVo getUserLoginLogById(Long logId);

    /**
     * 删除单条用户登录日志
     */
    void deleteUserLoginLog(Long logId);

    /**
     * 批量删除用户登录日志
     */
    void batchDeleteUserLoginLog(UserLoginLogBatchDeleteDto dto);

    /**
     * 导出用户登录日志
     */
    void exportUserLoginLog(UserLoginLogPageQueryDto dto);
}