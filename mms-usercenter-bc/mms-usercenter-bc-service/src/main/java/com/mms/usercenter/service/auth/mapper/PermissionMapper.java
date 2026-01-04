package com.mms.usercenter.service.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mms.usercenter.common.auth.entity.PermissionEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 实现功能【权限实体 Mapper】
 */
@Mapper
public interface PermissionMapper extends BaseMapper<PermissionEntity> {

    /**
     * 根据用户名查询该用户拥有的权限编码集合
     */
    List<String> selectPermissionCodesByUsername(String username);
}