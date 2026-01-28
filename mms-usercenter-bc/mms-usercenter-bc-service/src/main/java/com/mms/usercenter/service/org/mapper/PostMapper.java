package com.mms.usercenter.service.org.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.usercenter.common.org.dto.PostPageQueryDto;
import com.mms.usercenter.common.org.entity.PostEntity;
import com.mms.usercenter.common.org.vo.PostVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 实现功能【岗位实体 Mapper】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-01-23 10:33:06
 */
@Mapper
public interface PostMapper extends BaseMapper<PostEntity> {

    /**
     * 分页查询岗位列表
     *
     * @param page 分页对象
     * @param dto  查询条件
     * @return 分页岗位列表
     */
    Page<PostVo> getPostPage(@Param("page") Page<PostVo> page, @Param("dto") PostPageQueryDto dto);

    /**
     * 根据用户ID查询主岗位信息
     *
     * @param userId 用户ID
     * @return 主岗位VO（可能为null）
     */
    PostVo getPrimaryPostByUserId(@Param("userId") Long userId);
}