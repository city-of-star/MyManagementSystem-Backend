package com.mms.intern.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.intern.common.api.position.PositionRequests;
import com.mms.intern.common.api.position.PositionResponses;
import com.mms.intern.common.constant.InternConstants;
import com.mms.intern.common.entity.*;
import com.mms.intern.common.vo.PageResultVo;
import com.mms.intern.core.mapper.*;
import com.mms.intern.core.service.InternPositionService;
import com.mms.intern.core.util.InternPageSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InternPositionServiceImpl implements InternPositionService {

    private final InternPositionMapper positionMapper;
    private final InternBatchMapper batchMapper;
    private final InternEnterpriseMapper enterpriseMapper;
    private final InternApplicationMapper applicationMapper;

    @Override
    public PageResultVo<PositionResponses.ListItem> page(PositionRequests.PageQuery q) {
        LambdaQueryWrapper<InternPositionEntity> w = new LambdaQueryWrapper<>();
        if (q.getBatchId() != null) {
            w.eq(InternPositionEntity::getBatchId, q.getBatchId());
        }
        if (q.getEnterpriseId() != null) {
            w.eq(InternPositionEntity::getEnterpriseId, q.getEnterpriseId());
        }
        if (StringUtils.hasText(q.getKeyword())) {
            w.like(InternPositionEntity::getTitle, q.getKeyword().trim());
        }
        if (StringUtils.hasText(q.getStatus())) {
            w.eq(InternPositionEntity::getStatus, q.getStatus());
        }
        w.orderByDesc(InternPositionEntity::getCreateTime);
        Page<InternPositionEntity> page = positionMapper.selectPage(new Page<>(q.getPageNum(), q.getPageSize()), w);
        Page<PositionResponses.ListItem> vo = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        vo.setRecords(page.getRecords().stream().map(this::toListItem).collect(Collectors.toList()));
        return InternPageSupport.wrap(vo);
    }

    @Override
    public PageResultVo<PositionResponses.OpenItem> openPage(PositionRequests.OpenQuery q) {
        LambdaQueryWrapper<InternPositionEntity> w = new LambdaQueryWrapper<>();
        w.eq(InternPositionEntity::getStatus, InternConstants.POSITION_PUBLISHED);
        if (q.getBatchId() != null) {
            w.eq(InternPositionEntity::getBatchId, q.getBatchId());
        }
        if (StringUtils.hasText(q.getKeyword())) {
            w.like(InternPositionEntity::getTitle, q.getKeyword().trim());
        }
        w.orderByDesc(InternPositionEntity::getCreateTime);
        Page<InternPositionEntity> page = positionMapper.selectPage(new Page<>(q.getPageNum(), q.getPageSize()), w);
        Page<PositionResponses.OpenItem> vo = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        vo.setRecords(page.getRecords().stream().map(this::toOpenItem).collect(Collectors.toList()));
        return InternPageSupport.wrap(vo);
    }

    @Override
    public PositionResponses.Detail get(Long id) {
        InternPositionEntity p = getEntity(id);
        PositionResponses.Detail d = new PositionResponses.Detail();
        BeanUtils.copyProperties(toListItem(p), d);
        d.setRequirement(p.getRequirement());
        d.setRemark(p.getRemark());
        d.setUpdateTime(p.getUpdateTime());
        return d;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(PositionRequests.Save dto) {
        validateBatch(dto.getBatchId());
        validateEnterprise(dto.getEnterpriseId());
        InternPositionEntity p = new InternPositionEntity();
        p.setBatchId(dto.getBatchId());
        p.setEnterpriseId(dto.getEnterpriseId());
        p.setTitle(dto.getTitle());
        p.setQuota(dto.getQuota());
        p.setRequirement(dto.getRequirement());
        p.setStartDate(dto.getStartDate());
        p.setEndDate(dto.getEndDate());
        p.setRemark(dto.getRemark());
        p.setStatus(InternConstants.POSITION_DRAFT);
        positionMapper.insert(p);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, PositionRequests.Save dto) {
        InternPositionEntity p = getEntity(id);
        validateBatch(dto.getBatchId());
        validateEnterprise(dto.getEnterpriseId());
        p.setBatchId(dto.getBatchId());
        p.setEnterpriseId(dto.getEnterpriseId());
        p.setTitle(dto.getTitle());
        p.setQuota(dto.getQuota());
        p.setRequirement(dto.getRequirement());
        p.setStartDate(dto.getStartDate());
        p.setEndDate(dto.getEndDate());
        p.setRemark(dto.getRemark());
        positionMapper.updateById(p);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        getEntity(id);
        long cnt = applicationMapper.selectCount(new LambdaQueryWrapper<InternApplicationEntity>()
                .eq(InternApplicationEntity::getPositionId, id));
        if (cnt > 0) {
            throw new BusinessException(ErrorCode.DATA_IN_USE, "岗位下存在报名记录，无法删除");
        }
        positionMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, PositionRequests.StatusBody dto) {
        InternPositionEntity p = getEntity(id);
        String s = dto.getStatus();
        if (!InternConstants.POSITION_DRAFT.equals(s)
                && !InternConstants.POSITION_PUBLISHED.equals(s)
                && !InternConstants.POSITION_CLOSED.equals(s)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "岗位状态无效");
        }
        p.setStatus(s);
        positionMapper.updateById(p);
    }

    private void validateBatch(Long batchId) {
        InternBatchEntity b = batchMapper.selectById(batchId);
        if (b == null || Objects.equals(b.getDeleted(), 1)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "批次不存在");
        }
    }

    private void validateEnterprise(Long enterpriseId) {
        InternEnterpriseEntity e = enterpriseMapper.selectById(enterpriseId);
        if (e == null || Objects.equals(e.getDeleted(), 1)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "企业不存在");
        }
        if (!InternConstants.ENTERPRISE_APPROVED.equals(e.getAuditStatus())) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "企业未通过审核，不可关联岗位");
        }
    }

    private InternPositionEntity getEntity(Long id) {
        InternPositionEntity p = positionMapper.selectById(id);
        if (p == null || Objects.equals(p.getDeleted(), 1)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "岗位不存在");
        }
        return p;
    }

    private int countOccupied(Long positionId) {
        LambdaQueryWrapper<InternApplicationEntity> w = new LambdaQueryWrapper<>();
        w.eq(InternApplicationEntity::getPositionId, positionId)
                .in(InternApplicationEntity::getStatus,
                        List.of(InternConstants.APP_PENDING, InternConstants.APP_APPROVED,
                                InternConstants.APP_IN_PROGRESS, InternConstants.APP_COMPLETED));
        return Math.toIntExact(applicationMapper.selectCount(w));
    }

    private PositionResponses.ListItem toListItem(InternPositionEntity p) {
        PositionResponses.ListItem x = new PositionResponses.ListItem();
        x.setId(p.getId());
        x.setBatchId(p.getBatchId());
        x.setEnterpriseId(p.getEnterpriseId());
        x.setTitle(p.getTitle());
        x.setQuota(p.getQuota());
        x.setStatus(p.getStatus());
        x.setStartDate(p.getStartDate());
        x.setEndDate(p.getEndDate());
        x.setAppliedCount(countOccupied(p.getId()));
        x.setCreateTime(p.getCreateTime());
        InternBatchEntity b = batchMapper.selectById(p.getBatchId());
        if (b != null) {
            x.setBatchName(b.getBatchName());
        }
        InternEnterpriseEntity e = enterpriseMapper.selectById(p.getEnterpriseId());
        if (e != null) {
            x.setEnterpriseName(e.getEnterpriseName());
        }
        return x;
    }

    private PositionResponses.OpenItem toOpenItem(InternPositionEntity p) {
        PositionResponses.OpenItem x = new PositionResponses.OpenItem();
        x.setId(p.getId());
        x.setBatchId(p.getBatchId());
        x.setStatus(p.getStatus());
        x.setTitle(p.getTitle());
        x.setQuota(p.getQuota());
        x.setAppliedCount(countOccupied(p.getId()));
        x.setRequirement(p.getRequirement());
        x.setStartDate(p.getStartDate());
        x.setEndDate(p.getEndDate());
        InternBatchEntity b = batchMapper.selectById(p.getBatchId());
        if (b != null) {
            x.setBatchName(b.getBatchName());
        }
        InternEnterpriseEntity e = enterpriseMapper.selectById(p.getEnterpriseId());
        if (e != null) {
            x.setEnterpriseName(e.getEnterpriseName());
        }
        return x;
    }
}
