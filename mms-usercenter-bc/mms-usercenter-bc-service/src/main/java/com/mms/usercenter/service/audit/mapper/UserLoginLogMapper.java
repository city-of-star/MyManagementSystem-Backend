package com.mms.usercenter.service.audit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.usercenter.common.audit.dto.UserLoginLogPageQueryDto;
import com.mms.usercenter.common.audit.entity.UserLoginLogEntity;
import com.mms.usercenter.common.audit.vo.UserLoginLogVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 实现功能【用户登录日志实体 Mapper】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2025-12-11 09:36:05
 */
@Mapper
public interface UserLoginLogMapper extends BaseMapper<UserLoginLogEntity> {

    /**
     * 分页查询用户登录日志
     *
     * @param page 分页对象
     * @param dto  查询条件
     * @return 分页结果
     */
    Page<UserLoginLogVo> getUserLoginLogPage(@Param("page") Page<UserLoginLogVo> page, @Param("dto") UserLoginLogPageQueryDto dto);

    /**
     * 根据ID查询用户登录日志详情（VO）
     *
     * @param logId 日志ID
     * @return 登录日志VO
     */
    UserLoginLogVo getUserLoginLogById(@Param("logId") Long logId);
}