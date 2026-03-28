package com.mms.intern.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.intern.common.api.material.MaterialRequests;
import com.mms.intern.common.api.material.MaterialResponses;
import com.mms.intern.common.constant.InternConstants;
import com.mms.intern.common.entity.InternApplicationEntity;
import com.mms.intern.common.entity.InternMaterialEntity;
import com.mms.intern.common.vo.PageResultVo;
import com.mms.intern.core.mapper.InternApplicationMapper;
import com.mms.intern.core.mapper.InternMaterialMapper;
import com.mms.intern.core.service.InternMaterialService;
import com.mms.intern.core.util.InternPageSupport;
import com.mms.intern.core.util.InternUserHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InternMaterialServiceImpl implements InternMaterialService {

    private final InternMaterialMapper materialMapper;
    private final InternApplicationMapper applicationMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submit(MaterialRequests.Submit dto) {
        Long uid = InternUserHelper.requireUserId();
        InternApplicationEntity app = loadApplication(dto.getApplicationId());
        assertStudent(app, uid);
        InternMaterialEntity m = new InternMaterialEntity();
        m.setApplicationId(dto.getApplicationId());
        m.setMaterialType(dto.getMaterialType());
        m.setMaterialName(StringUtils.hasText(dto.getMaterialName()) ? dto.getMaterialName() : dto.getMaterialType());
        m.setAttachmentId(dto.getAttachmentId());
        m.setRemark(dto.getRemark());
        m.setStatus(InternConstants.LOG_SUBMITTED);
        materialMapper.insert(m);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, MaterialRequests.Submit dto) {
        Long uid = InternUserHelper.requireUserId();
        InternMaterialEntity m = getEntity(id);
        InternApplicationEntity app = loadApplication(m.getApplicationId());
        assertStudent(app, uid);
        if (InternConstants.LOG_APPROVED.equals(m.getStatus())) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "已通过不可修改");
        }
        m.setMaterialType(dto.getMaterialType());
        m.setMaterialName(StringUtils.hasText(dto.getMaterialName()) ? dto.getMaterialName() : dto.getMaterialType());
        m.setAttachmentId(dto.getAttachmentId());
        m.setRemark(dto.getRemark());
        if (InternConstants.LOG_REJECTED.equals(m.getStatus())) {
            m.setStatus(InternConstants.LOG_SUBMITTED);
            m.setAuditRemark(null);
            m.setAuditBy(null);
            m.setAuditTime(null);
        }
        materialMapper.updateById(m);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        InternMaterialEntity m = getEntity(id);
        Long uid = InternUserHelper.requireUserId();
        InternApplicationEntity app = loadApplication(m.getApplicationId());
        if (!Objects.equals(app.getStudentUserId(), uid)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "无权限删除");
        }
        materialMapper.deleteById(id);
    }

    @Override
    public List<MaterialResponses.ListItem> list(Long applicationId) {
        Long uid = InternUserHelper.requireUserId();
        InternApplicationEntity app = loadApplication(applicationId);
        if (!Objects.equals(app.getStudentUserId(), uid) && !Objects.equals(app.getSchoolMentorUserId(), uid)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "无权限查看材料");
        }
        LambdaQueryWrapper<InternMaterialEntity> w = new LambdaQueryWrapper<>();
        w.eq(InternMaterialEntity::getApplicationId, applicationId).orderByDesc(InternMaterialEntity::getCreateTime);
        return materialMapper.selectList(w).stream().map(this::toListItem).collect(Collectors.toList());
    }

    @Override
    public PageResultVo<MaterialResponses.ListItem> page(MaterialRequests.PageQuery q) {
        LambdaQueryWrapper<InternMaterialEntity> w = new LambdaQueryWrapper<>();
        if (q.getApplicationId() != null) {
            w.eq(InternMaterialEntity::getApplicationId, q.getApplicationId());
        }
        if (StringUtils.hasText(q.getMaterialType())) {
            w.eq(InternMaterialEntity::getMaterialType, q.getMaterialType());
        }
        if (StringUtils.hasText(q.getStatus())) {
            w.eq(InternMaterialEntity::getStatus, q.getStatus());
        }
        if (q.getBatchId() != null) {
            LambdaQueryWrapper<InternApplicationEntity> aw = new LambdaQueryWrapper<>();
            aw.eq(InternApplicationEntity::getBatchId, q.getBatchId());
            List<Long> ids = applicationMapper.selectList(aw).stream().map(InternApplicationEntity::getId).toList();
            if (ids.isEmpty()) {
                Page<MaterialResponses.ListItem> empty = new Page<>(q.getPageNum(), q.getPageSize(), 0);
                return InternPageSupport.wrap(empty);
            }
            w.in(InternMaterialEntity::getApplicationId, ids);
        }
        w.orderByDesc(InternMaterialEntity::getCreateTime);
        Page<InternMaterialEntity> page = materialMapper.selectPage(new Page<>(q.getPageNum(), q.getPageSize()), w);
        Page<MaterialResponses.ListItem> vo = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        vo.setRecords(page.getRecords().stream().map(this::toListItem).collect(Collectors.toList()));
        return InternPageSupport.wrap(vo);
    }

    @Override
    public MaterialResponses.Detail get(Long id) {
        InternMaterialEntity m = getEntity(id);
        Long uid = InternUserHelper.requireUserId();
        InternApplicationEntity app = loadApplication(m.getApplicationId());
        if (!Objects.equals(app.getStudentUserId(), uid) && !Objects.equals(app.getSchoolMentorUserId(), uid)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "无权限查看");
        }
        MaterialResponses.Detail d = new MaterialResponses.Detail();
        BeanUtils.copyProperties(toListItem(m), d);
        d.setAuditBy(m.getAuditBy());
        d.setAuditTime(m.getAuditTime());
        d.setUpdateTime(m.getUpdateTime());
        return d;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void audit(Long id, MaterialRequests.Audit dto) {
        InternMaterialEntity m = getEntity(id);
        if (!InternConstants.LOG_APPROVED.equals(dto.getStatus()) && !InternConstants.LOG_REJECTED.equals(dto.getStatus())) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "审核状态无效");
        }
        m.setStatus(dto.getStatus());
        m.setAuditRemark(dto.getAuditRemark());
        m.setAuditBy(InternUserHelper.requireUserId());
        m.setAuditTime(LocalDateTime.now());
        materialMapper.updateById(m);
    }

    private void assertStudent(InternApplicationEntity app, Long uid) {
        if (!Objects.equals(app.getStudentUserId(), uid)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "仅学生可提交材料");
        }
    }

    private InternApplicationEntity loadApplication(Long id) {
        InternApplicationEntity a = applicationMapper.selectById(id);
        if (a == null || Objects.equals(a.getDeleted(), 1)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "实习申请不存在");
        }
        return a;
    }

    private InternMaterialEntity getEntity(Long id) {
        InternMaterialEntity m = materialMapper.selectById(id);
        if (m == null || Objects.equals(m.getDeleted(), 1)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "材料不存在");
        }
        return m;
    }

    private MaterialResponses.ListItem toListItem(InternMaterialEntity m) {
        MaterialResponses.ListItem x = new MaterialResponses.ListItem();
        x.setId(m.getId());
        x.setApplicationId(m.getApplicationId());
        x.setMaterialType(m.getMaterialType());
        x.setMaterialName(m.getMaterialName());
        x.setAttachmentId(m.getAttachmentId());
        x.setStatus(m.getStatus());
        x.setAuditRemark(m.getAuditRemark());
        x.setCreateTime(m.getCreateTime());
        return x;
    }
}
