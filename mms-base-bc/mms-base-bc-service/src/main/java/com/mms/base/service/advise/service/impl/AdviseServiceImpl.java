package com.mms.base.service.advise.service.impl;

import com.mms.base.service.advise.mapper.AdviseMapper;
import com.mms.base.service.advise.service.AdviseService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 实现功能【意见管理服务实现类】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-01-21 14:30:23
 */
@Slf4j
@Service
public class AdviseServiceImpl implements AdviseService {

    @Resource
    private AdviseMapper adviseMapper;
}