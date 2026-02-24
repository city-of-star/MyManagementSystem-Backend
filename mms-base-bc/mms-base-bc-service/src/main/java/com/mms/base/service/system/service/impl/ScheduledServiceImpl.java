package com.mms.base.service.system.service.impl;

import com.mms.base.service.system.mapper.ScheduledMapper;
import com.mms.base.service.system.service.ScheduledService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 实现功能【定时任务服务实现类】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-02-11 10:09:59
 */
@Slf4j
@Service
public class ScheduledServiceImpl implements ScheduledService {

    @Resource
    private ScheduledMapper scheduledMapper;
}