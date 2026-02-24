package com.mms.base.service.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.system.dto.JobBatchDeleteDto;
import com.mms.base.common.system.dto.JobCreateDto;
import com.mms.base.common.system.dto.JobPageQueryDto;
import com.mms.base.common.system.dto.JobStatusSwitchDto;
import com.mms.base.common.system.dto.JobUpdateDto;
import com.mms.base.common.system.vo.JobVo;

/**
 * 实现功能【定时任务服务】
 * <p>
 * 提供定时任务定义的核心业务方法
 * </p>
 *
 * @author li.hongyu
 * @date 2026-02-11 10:09:47
 */
public interface JobService {

    /**
     * 分页查询定时任务列表
     *
     * @param dto 查询条件
     * @return 分页任务列表
     */
    Page<JobVo> getJobPage(JobPageQueryDto dto);

    /**
     * 根据任务ID查询任务详情
     *
     * @param jobId 任务ID
     * @return 任务信息
     */
    JobVo getJobById(Long jobId);

    /**
     * 创建定时任务
     *
     * @param dto 创建参数
     * @return 创建后的任务信息
     */
    JobVo createJob(JobCreateDto dto);

    /**
     * 更新定时任务
     *
     * @param dto 更新参数
     * @return 更新后的任务信息
     */
    JobVo updateJob(JobUpdateDto dto);

    /**
     * 删除定时任务（逻辑删除）
     *
     * @param jobId 任务ID
     */
    void deleteJob(Long jobId);

    /**
     * 批量删除定时任务（逻辑删除）
     *
     * @param dto 批量删除参数
     */
    void batchDeleteJob(JobBatchDeleteDto dto);

    /**
     * 切换定时任务启用状态
     *
     * @param dto 状态切换参数
     */
    void switchJobStatus(JobStatusSwitchDto dto);

    /**
     * 判断某服务下任务编码是否已存在
     *
     * @param serviceName 所属服务
     * @param jobCode     任务编码
     * @return true-存在，false-不存在
     */
    boolean existsByServiceAndJobCode(String serviceName, String jobCode);
}