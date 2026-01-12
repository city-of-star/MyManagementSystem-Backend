package com.mms.usercenter.service.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.usercenter.common.auth.dto.UserPageQueryDto;
import com.mms.usercenter.common.auth.entity.UserEntity;
import com.mms.usercenter.common.auth.vo.UserVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 实现功能【用户实体 Mapper】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2025-11-07 17:30:28
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户实体
     */
    UserEntity selectByUsername(@Param("username") String username);

    /**
     * 分页查询用户列表
     *
     * @param page 分页对象
     * @param dto  查询条件
     * @return 分页用户列表
     */
    Page<UserVo> getUserPage(@Param("page") Page<UserVo> page, @Param("dto") UserPageQueryDto dto);

    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户实体
     */
    UserEntity selectByEmail(@Param("email") String email);

    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     * @return 用户实体
     */
    UserEntity selectByPhone(@Param("phone") String phone);
}

