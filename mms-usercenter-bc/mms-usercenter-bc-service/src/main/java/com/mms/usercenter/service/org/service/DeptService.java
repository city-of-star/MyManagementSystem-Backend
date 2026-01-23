package com.mms.usercenter.service.org.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.usercenter.common.org.dto.*;
import com.mms.usercenter.common.org.vo.DeptVo;

/**
 * 实现功能【部门服务】
 * <p>
 * 提供部门管理的核心业务方法
 * </p>
 *
 * @author li.hongyu
 * @date 2026-01-23 10:33:19
 */
public interface DeptService {

    /**
     * 分页查询部门列表
     *
     * @param dto 查询条件
     * @return 分页部门列表
     */
    Page<DeptVo> getDeptPage(DeptPageQueryDto dto);

    /**
     * 根据部门ID查询部门详情
     *
     * @param deptId 部门ID
     * @return 部门信息
     */
    DeptVo getDeptById(Long deptId);

    /**
     * 创建部门
     *
     * @param dto 部门创建参数
     * @return 创建的部门信息
     */
    DeptVo createDept(DeptCreateDto dto);

    /**
     * 更新部门信息
     *
     * @param dto 部门更新参数
     * @return 更新后的部门信息
     */
    DeptVo updateDept(DeptUpdateDto dto);

    /**
     * 删除部门（逻辑删除）
     *
     * @param deptId 部门ID
     */
    void deleteDept(Long deptId);

    /**
     * 批量删除部门（逻辑删除）
     *
     * @param dto 批量删除参数
     */
    void batchDeleteDept(DeptBatchDeleteDto dto);

    /**
     * 切换部门状态（启用/禁用）
     *
     * @param dto 状态切换参数
     */
    void switchDeptStatus(DeptStatusSwitchDto dto);
}