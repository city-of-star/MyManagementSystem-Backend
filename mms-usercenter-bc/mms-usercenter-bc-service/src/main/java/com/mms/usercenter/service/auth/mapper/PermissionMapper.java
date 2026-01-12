package com.mms.usercenter.service.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.usercenter.common.auth.dto.PermissionPageQueryDto;
import com.mms.usercenter.common.auth.entity.PermissionEntity;
import com.mms.usercenter.common.auth.vo.PermissionVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 实现功能【权限实体 Mapper】
 */
@Mapper
public interface PermissionMapper extends BaseMapper<PermissionEntity> {

    /**
     * 分页查询权限列表
     */
    Page<PermissionVo> getPermissionPage(@Param("page") Page<PermissionVo> page, @Param("dto") PermissionPageQueryDto dto);

    /**
     * 根据用户名查询该用户拥有的权限编码集合
     */
    List<String> selectPermissionCodesByUsername(String username);
}