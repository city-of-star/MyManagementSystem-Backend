package com.mms.usercenter.service.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mms.usercenter.common.auth.entity.UserPreferenceEntity;
import com.mms.usercenter.common.auth.vo.UserPreferenceVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 实现功能【用户偏好配置 Mapper】
 * <p>
 * 提供用户偏好配置查询能力
 * </p>
 *
 * @author li.hongyu
 * @date 2026-04-27 15:00:00
 */
@Mapper
public interface UserPreferenceMapper extends BaseMapper<UserPreferenceEntity> {

    /**
     * 查询用户全部偏好配置
     *
     * @param userId 用户ID
     * @return 偏好配置列表
     */
    List<UserPreferenceVo> selectByUserId(@Param("userId") Long userId);

}
