package com.mms.usercenter.service.auth.service;

import com.mms.usercenter.common.auth.dto.UserPreferenceBatchSaveDto;
import com.mms.usercenter.common.auth.dto.UserPreferenceSaveDto;
import com.mms.usercenter.common.auth.vo.UserPreferenceVo;

import java.util.List;

/**
 * 实现功能【用户偏好配置服务】
 * <p>
 * 提供当前登录用户的偏好配置查询与保存能力
 * </p>
 *
 * @author li.hongyu
 * @date 2026-04-27 15:00:00
 */
public interface UserPreferenceService {

    /**
     * 查询当前用户偏好配置列表
     *
     * @return 偏好配置列表
     */
    List<UserPreferenceVo> getMyPreferences();

    /**
     * 保存当前用户单个偏好配置
     *
     * @param dto 保存参数
     * @return 保存后的偏好配置
     */
    UserPreferenceVo saveMyPreference(UserPreferenceSaveDto dto);

    /**
     * 批量保存当前用户偏好配置
     *
     * @param dto 批量保存参数
     * @return 保存后的偏好配置列表
     */
    List<UserPreferenceVo> batchSaveMyPreferences(UserPreferenceBatchSaveDto dto);
}
