package com.mms.usercenter.service.audit.service.impl;

import com.mms.usercenter.service.audit.mapper.UserLoginLogMapper;
import com.mms.usercenter.service.audit.service.UserLoginLogService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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


}