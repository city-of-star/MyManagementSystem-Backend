package com.mms.usercenter.service.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.usercenter.common.auth.dto.RolePageQueryDto;
import com.mms.usercenter.common.auth.entity.RoleEntity;
import com.mms.usercenter.common.auth.vo.RoleVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 实现功能【角色实体 Mapper】
 */
@Mapper
public interface RoleMapper extends BaseMapper<RoleEntity> {

    /**
     * 分页查询角色列表
     */
    Page<RoleVo> getRolePage(@Param("page") Page<RoleVo> page, @Param("dto") RolePageQueryDto dto);

    /**
     * 根据用户名查询该用户拥有的角色编码集合
     */
    List<String> selectRoleCodesByUsername(String username);
}