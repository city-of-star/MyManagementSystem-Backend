package com.mms.base.service.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.system.dto.JobBatchDeleteDto;
import com.mms.base.common.system.dto.JobCreateDto;
import com.mms.base.common.system.dto.JobPageQueryDto;
import com.mms.base.common.system.dto.JobStatusSwitchDto;
import com.mms.base.common.system.dto.JobUpdateDto;
import com.mms.base.common.system.entity.JobEntity;
import com.mms.base.common.system.vo.JobVo;
import com.mms.base.service.system.mapper.JobMapper;
import com.mms.base.service.system.service.JobService;
import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.core.exceptions.ServerException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 实现功能【定时任务服务实现类】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-02-11 10:09:59
 */
@Slf4j
@Service
public class JobServiceImpl implements JobService {

    @Resource
    private JobMapper jobMapper;

    @Override
    public Page<JobVo> getJobPage(JobPageQueryDto dto) {
        try {
            log.info("分页查询定时任务列表，参数：{}", dto);
            Page<JobVo> page = new Page<>(dto.getPageNum(), dto.getPageSize());
            return jobMapper.getJobPage(page, dto);
        } catch (Exception e) {
            log.error("分页查询定时任务列表失败：{}", e.getMessage(), e);
            throw new ServerException("查询定时任务列表失败", e);
        }
    }

    @Override
    public JobVo getJobById(Long jobId) {
        try {
            log.info("根据ID查询定时任务，jobId：{}", jobId);
            if (jobId == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "任务ID不能为空");
            }
            JobEntity job = jobMapper.selectById(jobId);
            if (job == null || Objects.equals(job.getDeleted(), 1)) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "定时任务不存在");
            }
            return convertToVo(job);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("根据ID查询定时任务失败：{}", e.getMessage(), e);
            throw new ServerException("查询定时任务失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JobVo createJob(JobCreateDto dto) {
        try {
            log.info("创建定时任务，参数：{}", dto);
            if (!StringUtils.hasText(dto.getServiceName())) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "所属服务不能为空");
            }
            if (!StringUtils.hasText(dto.getJobCode())) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "任务编码不能为空");
            }
            if (existsByServiceAndJobCode(dto.getServiceName(), dto.getJobCode())) {
                throw new BusinessException(ErrorCode.UNIQUE_CONSTRAINT_ERROR, "同一服务下任务编码已存在");
            }
            JobEntity entity = new JobEntity();
            entity.setServiceName(dto.getServiceName());
            entity.setJobCode(dto.getJobCode());
            entity.setJobName(dto.getJobName());
            entity.setCronExpr(dto.getCronExpr());
            entity.setRunMode(dto.getRunMode());
            entity.setEnabled(dto.getEnabled() == null ? 1 : dto.getEnabled());
            entity.setTimeoutMs(dto.getTimeoutMs() == null ? 0 : dto.getTimeoutMs());
            entity.setRemark(dto.getRemark());
            entity.setParamsJson(dto.getParamsJson());
            entity.setDeleted(0);
            jobMapper.insert(entity);
            return convertToVo(entity);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建定时任务失败：{}", e.getMessage(), e);
            throw new ServerException("创建定时任务失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JobVo updateJob(JobUpdateDto dto) {
        try {
            log.info("更新定时任务，参数：{}", dto);
            JobEntity job = jobMapper.selectById(dto.getId());
            if (job == null || Objects.equals(job.getDeleted(), 1)) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "定时任务不存在");
            }
            if (StringUtils.hasText(dto.getServiceName())) {
                job.setServiceName(dto.getServiceName());
            }
            if (StringUtils.hasText(dto.getJobName())) {
                job.setJobName(dto.getJobName());
            }
            if (StringUtils.hasText(dto.getCronExpr())) {
                job.setCronExpr(dto.getCronExpr());
            }
            if (StringUtils.hasText(dto.getRunMode())) {
                job.setRunMode(dto.getRunMode());
            }
            if (dto.getEnabled() != null) {
                if (dto.getEnabled() != 0 && dto.getEnabled() != 1) {
                    throw new BusinessException(ErrorCode.PARAM_INVALID, "启用状态值只能是0或1");
                }
                job.setEnabled(dto.getEnabled());
            }
            if (dto.getTimeoutMs() != null) {
                job.setTimeoutMs(dto.getTimeoutMs());
            }
            if (StringUtils.hasText(dto.getRemark())) {
                job.setRemark(dto.getRemark());
            }
            if (StringUtils.hasText(dto.getParamsJson())) {
                job.setParamsJson(dto.getParamsJson());
            }
            jobMapper.updateById(job);
            return convertToVo(job);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新定时任务失败：{}", e.getMessage(), e);
            throw new ServerException("更新定时任务失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteJob(Long jobId) {
        try {
            log.info("删除定时任务，jobId：{}", jobId);
            if (jobId == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "任务ID不能为空");
            }
            JobEntity job = jobMapper.selectById(jobId);
            if (job == null || Objects.equals(job.getDeleted(), 1)) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "定时任务不存在");
            }
            jobMapper.deleteById(jobId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除定时任务失败：{}", e.getMessage(), e);
            throw new ServerException("删除定时任务失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteJob(JobBatchDeleteDto dto) {
        try {
            log.info("批量删除定时任务，jobIds：{}", dto.getJobIds());
            if (dto.getJobIds() == null || dto.getJobIds().isEmpty()) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "任务ID列表不能为空");
            }
            for (Long jobId : dto.getJobIds()) {
                deleteJob(jobId);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量删除定时任务失败：{}", e.getMessage(), e);
            throw new ServerException("批量删除定时任务失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void switchJobStatus(JobStatusSwitchDto dto) {
        try {
            log.info("切换定时任务状态，jobId：{}，enabled：{}", dto.getJobId(), dto.getEnabled());
            JobEntity job = jobMapper.selectById(dto.getJobId());
            if (job == null || Objects.equals(job.getDeleted(), 1)) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "定时任务不存在");
            }
            if (dto.getEnabled() != 0 && dto.getEnabled() != 1) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "启用状态值只能是0或1");
            }
            job.setEnabled(dto.getEnabled());
            job.setUpdateTime(LocalDateTime.now());
            jobMapper.updateById(job);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("切换定时任务状态失败：{}", e.getMessage(), e);
            throw new ServerException("切换定时任务状态失败", e);
        }
    }

    @Override
    public boolean existsByServiceAndJobCode(String serviceName, String jobCode) {
        if (!StringUtils.hasText(serviceName) || !StringUtils.hasText(jobCode)) {
            return false;
        }
        LambdaQueryWrapper<JobEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(JobEntity::getServiceName, serviceName)
                .eq(JobEntity::getJobCode, jobCode)
                .eq(JobEntity::getDeleted, 0);
        return jobMapper.selectCount(wrapper) > 0;
    }

    private JobVo convertToVo(JobEntity entity) {
        if (entity == null) {
            return null;
        }
        JobVo vo = new JobVo();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}