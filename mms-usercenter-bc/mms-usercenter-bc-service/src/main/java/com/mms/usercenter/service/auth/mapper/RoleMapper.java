package com.mms.usercenter.service.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mms.usercenter.common.auth.entity.RoleEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 实现功能【角色实体 Mapper】
 */
@Mapper
public interface RoleMapper extends BaseMapper<RoleEntity> {

    /**
     * 根据用户名查询该用户拥有的角色编码集合
     */
    List<String> selectRoleCodesByUsername(String username);
}