package com.mms.usercenter.service.auth.service;

import com.mms.usercenter.common.auth.vo.UserLoginLogVo;

import java.util.List;

/**
 * 实现功能【用户登录日志服务】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-01-29 17:16:02
 */
public interface LoginLogService {

    /**
     * 查询最近的登录失败记录
     *
     * @param limit 返回数量
     * @return 登录日志列表
     */
    List<UserLoginLogVo> getRecentFailedLogs(int limit);
}

