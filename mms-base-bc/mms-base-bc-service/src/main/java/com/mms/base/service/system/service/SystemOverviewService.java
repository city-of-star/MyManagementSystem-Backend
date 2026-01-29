package com.mms.base.service.system.service;

import com.mms.base.common.system.vo.SystemOverviewVo;

/**
 * 实现功能【系统运行总览服务】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-01-29 17:02:33
 */
public interface SystemOverviewService {

    /**
     * 获取系统运行总览信息
     *
     * @return 概览信息
     */
    SystemOverviewVo getOverview();
}

