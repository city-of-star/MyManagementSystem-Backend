package com.mms.usercenter.service.auth.service.impl;

import com.mms.common.webmvc.utils.UserContextUtils;
import com.mms.usercenter.common.auth.entity.UserEntity;
import com.mms.usercenter.common.auth.vo.UserDetailVo;
import com.mms.usercenter.service.auth.mapper.UserMapper;
import com.mms.usercenter.service.auth.service.CurrentUserService;
import com.mms.usercenter.service.auth.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * 实现功能【当前登录用户服务实现类】
 * <p>
 * 
 * </p>
 *
 * @author li.hongyu
 * @date 2026-04-16 10:00:00
 */
@Service
public class CurrentUserServiceImpl implements CurrentUserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserService userService;

    @Override
    public UserEntity getCurrentUserEntity() {
        Long userId = UserContextUtils.getUserId();
        if (userId == null) {
            return null;
        }
        return userMapper.selectById(userId);
    }

    @Override
    public UserDetailVo getCurrentUserDetail() {
        Long userId = UserContextUtils.getUserId();
        if (userId == null) {
            return null;
        }
        return userService.getUserById(userId);
    }
}
