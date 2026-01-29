package com.mms.usercenter.service.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.usercenter.common.auth.entity.UserLoginLogEntity;
import com.mms.usercenter.common.auth.vo.UserLoginLogVo;
import com.mms.usercenter.service.auth.mapper.UserLoginLogMapper;
import com.mms.usercenter.service.auth.service.LoginLogService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 实现功能【用户登录日志服务实现类】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-01-29 17:16:02
 */
@Slf4j
@Service
public class LoginLogServiceImpl implements LoginLogService {

    @Resource
    private UserLoginLogMapper userLoginLogMapper;

    @Override
    public List<UserLoginLogVo> getRecentFailedLogs(int limit) {
        int pageSize = Math.max(1, Math.min(limit, 20));

        LambdaQueryWrapper<UserLoginLogEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserLoginLogEntity::getLoginStatus, 0)
                .orderByDesc(UserLoginLogEntity::getLoginTime);

        Page<UserLoginLogEntity> page = new Page<>(1, pageSize);
        Page<UserLoginLogEntity> resultPage = userLoginLogMapper.selectPage(page, wrapper);

        return resultPage.getRecords().stream()
                .map(this::convertToVo)
                .toList();
    }

    private UserLoginLogVo convertToVo(UserLoginLogEntity entity) {
        if (entity == null) {
            return null;
        }
        UserLoginLogVo vo = new UserLoginLogVo();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}

