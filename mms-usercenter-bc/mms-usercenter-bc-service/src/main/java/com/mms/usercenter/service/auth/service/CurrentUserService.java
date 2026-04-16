package com.mms.usercenter.service.auth.service;

import com.mms.usercenter.common.auth.entity.UserEntity;
import com.mms.usercenter.common.auth.vo.UserDetailVo;

/**
 * 实现功能【当前登录用户服务】
 * <p>
 * 
 * </p>
 *
 * @author li.hongyu
 * @date 2026-04-16 10:00:00
 */
public interface CurrentUserService {

    /**
     * 获取当前用户实体信息
     */
    UserEntity getCurrentUserEntity();

    /**
     * 获取当前用户详细信息（包含部门、岗位）
     */
    UserDetailVo getCurrentUserDetail();
}
