package com.mms.intern.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.intern.common.api.application.ApplicationRequests;
import com.mms.intern.common.api.application.ApplicationResponses;
import com.mms.intern.common.constant.InternConstants;
import com.mms.intern.common.entity.*;
import com.mms.intern.common.vo.PageResultVo;
import com.mms.intern.core.mapper.*;
import com.mms.intern.core.service.InternApplicationService;
import com.mms.intern.core.service.impl.support.InternPositionQuotaSupport;
import com.mms.intern.core.util.InternPageSupport;
import com.mms.intern.core.util.InternUserHelper;
import com.mms.common.core.response.Response;
import com.mms.usercenter.feign.UserInfoFeign;
import com.mms.usercenter.feign.vo.UserInfoVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InternApplicationServiceImpl implements InternApplicationService {

    private final InternApplicationMapper applicationMapper;
    private final InternPositionMapper positionMapper;
    private final InternBatchMapper batchMapper;
    private final InternEnterpriseMapper enterpriseMapper;
    private final InternPositionQuotaSupport quotaSupport;
    private final UserInfoFeign userInfoFeign;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void apply(ApplicationRequests.Apply dto) {
        Long studentId = InternUserHelper.requireUserId();
        InternPositionEntity pos = loadPosition(dto.getPositionId());
        if (!InternConstants.POSITION_PUBLISHED.equals(pos.getStatus())) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "岗位未发布，无法报名");
        }
        InternBatchEntity batch = loadBatch(pos.getBatchId());
        checkSignupWindow(batch);
        if (hasActiveApplication(studentId, dto.getPositionId())) {
            throw new BusinessException(ErrorCode.UNIQUE_CONSTRAINT_ERROR, "不可重复报名该岗位");
        }
        if (!quotaSupport.hasQuota(dto.getPositionId())) {
            throw new BusinessException(ErrorCode.QUOTA_EXCEEDED, "岗位名额已满");
        }
        InternApplicationEntity a = new InternApplicationEntity();
        a.setBatchId(pos.getBatchId());
        a.setPositionId(dto.getPositionId());
        a.setStudentUserId(studentId);
        a.setStatus(InternConstants.APP_PENDING);
        applicationMapper.insert(a);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assign(ApplicationRequests.Assign dto) {
        InternPositionEntity pos = loadPosition(dto.getPositionId());
        if (!InternConstants.POSITION_PUBLISHED.equals(pos.getStatus())) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "岗位未发布，无法分配");
        }
        if (hasActiveApplication(dto.getStudentUserId(), dto.getPositionId())) {
            throw new BusinessException(ErrorCode.UNIQUE_CONSTRAINT_ERROR, "该学生已报名此岗位");
        }
        if (!quotaSupport.hasQuota(dto.getPositionId())) {
            throw new BusinessException(ErrorCode.QUOTA_EXCEEDED, "岗位名额已满");
        }
        InternApplicationEntity a = new InternApplicationEntity();
        a.setBatchId(pos.getBatchId());
        a.setPositionId(dto.getPositionId());
        a.setStudentUserId(dto.getStudentUserId());
        a.setSchoolMentorUserId(dto.getSchoolMentorUserId());
        a.setStatus(InternConstants.APP_IN_PROGRESS);
        a.setAuditBy(InternUserHelper.requireUserId());
        a.setAuditTime(LocalDateTime.now());
        a.setRemark(dto.getRemark());
        applicationMapper.insert(a);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long id) {
        Long uid = InternUserHelper.requireUserId();
        InternApplicationEntity a = getEntity(id);
        if (!Objects.equals(a.getStudentUserId(), uid)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "只能撤销本人报名");
        }
        if (!InternConstants.APP_PENDING.equals(a.getStatus())) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "当前状态不可撤销");
        }
        a.setStatus(InternConstants.APP_CANCELLED);
        applicationMapper.updateById(a);
    }

    @Override
    public PageResultVo<ApplicationResponses.ListItem> myPage(ApplicationRequests.MyPageQuery q) {
        Long uid = InternUserHelper.requireUserId();
        LambdaQueryWrapper<InternApplicationEntity> w = new LambdaQueryWrapper<>();
        w.eq(InternApplicationEntity::getStudentUserId, uid);
        if (q.getBatchId() != null) {
            w.eq(InternApplicationEntity::getBatchId, q.getBatchId());
        }
        if (StringUtils.hasText(q.getStatus())) {
            w.eq(InternApplicationEntity::getStatus, q.getStatus());
        }
        w.orderByDesc(InternApplicationEntity::getCreateTime);
        Page<InternApplicationEntity> page = applicationMapper.selectPage(new Page<>(q.getPageNum(), q.getPageSize()), w);
        Page<ApplicationResponses.ListItem> vo = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<ApplicationResponses.ListItem> rows = page.getRecords().stream().map(this::toListItem).collect(Collectors.toList());
        fillUserDisplayNames(rows);
        vo.setRecords(rows);
        return InternPageSupport.wrap(vo);
    }

    @Override
    public PageResultVo<ApplicationResponses.ListItem> adminPage(ApplicationRequests.AdminPageQuery q) {
        LambdaQueryWrapper<InternApplicationEntity> w = new LambdaQueryWrapper<>();
        if (q.getBatchId() != null) {
            w.eq(InternApplicationEntity::getBatchId, q.getBatchId());
        }
        if (q.getPositionId() != null) {
            w.eq(InternApplicationEntity::getPositionId, q.getPositionId());
        }
        if (StringUtils.hasText(q.getStatus())) {
            w.eq(InternApplicationEntity::getStatus, q.getStatus());
        }
        if (q.getSchoolMentorUserId() != null) {
            w.eq(InternApplicationEntity::getSchoolMentorUserId, q.getSchoolMentorUserId());
        }
        if (StringUtils.hasText(q.getStudentKeyword())) {
            String kw = q.getStudentKeyword().trim();
            try {
                Long sid = Long.parseLong(kw);
                w.eq(InternApplicationEntity::getStudentUserId, sid);
            } catch (NumberFormatException ignored) {
                w.like(InternApplicationEntity::getRemark, kw);
            }
        }
        w.orderByDesc(InternApplicationEntity::getCreateTime);
        Page<InternApplicationEntity> page = applicationMapper.selectPage(new Page<>(q.getPageNum(), q.getPageSize()), w);
        Page<ApplicationResponses.ListItem> vo = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<ApplicationResponses.ListItem> rows = page.getRecords().stream().map(this::toListItem).collect(Collectors.toList());
        fillUserDisplayNames(rows);
        vo.setRecords(rows);
        return InternPageSupport.wrap(vo);
    }

    @Override
    public ApplicationResponses.Detail get(Long id) {
        InternApplicationEntity a = getEntity(id);
        ApplicationResponses.Detail d = new ApplicationResponses.Detail();
        BeanUtils.copyProperties(toListItem(a), d);
        fillUserDisplayNames(d);
        d.setAuditRemark(a.getAuditRemark());
        d.setAuditBy(a.getAuditBy());
        d.setRemark(a.getRemark());
        d.setUpdateTime(a.getUpdateTime());
        InternPositionEntity p = positionMapper.selectById(a.getPositionId());
        if (p != null) {
            d.setRequirement(p.getRequirement());
        }
        return d;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void audit(Long id, ApplicationRequests.Audit dto) {
        InternApplicationEntity a = getEntity(id);
        if (!InternConstants.APP_PENDING.equals(a.getStatus())) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "仅待审核可申请审核");
        }
        if (!InternConstants.APP_APPROVED.equals(dto.getStatus()) && !InternConstants.APP_REJECTED.equals(dto.getStatus())) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "审核结果无效");
        }
        if (InternConstants.APP_APPROVED.equals(dto.getStatus()) && !quotaSupport.hasQuota(a.getPositionId())) {
            throw new BusinessException(ErrorCode.QUOTA_EXCEEDED, "岗位名额已满");
        }
        // 管理员点「通过」后直接进入「实习中」，与业务上「审批通过即可实习」一致
        if (InternConstants.APP_APPROVED.equals(dto.getStatus())) {
            a.setStatus(InternConstants.APP_IN_PROGRESS);
        } else {
            a.setStatus(dto.getStatus());
        }
        a.setAuditRemark(dto.getAuditRemark());
        a.setAuditBy(InternUserHelper.requireUserId());
        a.setAuditTime(LocalDateTime.now());
        applicationMapper.updateById(a);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setMentor(Long id, ApplicationRequests.Mentor dto) {
        InternApplicationEntity a = getEntity(id);
        a.setSchoolMentorUserId(dto.getSchoolMentorUserId());
        applicationMapper.updateById(a);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setEnterpriseMentor(Long id, ApplicationRequests.EnterpriseMentor dto) {
        InternApplicationEntity a = getEntity(id);
        a.setEnterpriseMentorUserId(dto.getEnterpriseMentorUserId());
        applicationMapper.updateById(a);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void lifecycle(Long id, ApplicationRequests.Lifecycle dto) {
        InternApplicationEntity a = getEntity(id);
        String target = dto.getStatus();
        if (InternConstants.APP_IN_PROGRESS.equals(target)) {
            if (!InternConstants.APP_APPROVED.equals(a.getStatus())) {
                throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "仅已通过可申请可标记为实习中");
            }
            a.setStatus(InternConstants.APP_IN_PROGRESS);
        } else if (InternConstants.APP_COMPLETED.equals(target)) {
            if (!InternConstants.APP_IN_PROGRESS.equals(a.getStatus())) {
                throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "仅实习中可标记为已完成");
            }
            a.setStatus(InternConstants.APP_COMPLETED);
        } else {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "状态无效");
        }
        applicationMapper.updateById(a);
    }

    private void checkSignupWindow(InternBatchEntity batch) {
        LocalDateTime now = LocalDateTime.now();
        if (batch.getSignUpStart() != null && now.isBefore(batch.getSignUpStart())) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "报名尚未开始");
        }
        if (batch.getSignUpEnd() != null && now.isAfter(batch.getSignUpEnd())) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "报名已结束");
        }
    }

    private boolean hasActiveApplication(Long studentId, Long positionId) {
        LambdaQueryWrapper<InternApplicationEntity> w = new LambdaQueryWrapper<>();
        w.eq(InternApplicationEntity::getStudentUserId, studentId)
                .eq(InternApplicationEntity::getPositionId, positionId)
                .notIn(InternApplicationEntity::getStatus,
                        InternConstants.APP_REJECTED, InternConstants.APP_CANCELLED);
        return applicationMapper.selectCount(w) > 0;
    }

    private InternApplicationEntity getEntity(Long id) {
        InternApplicationEntity a = applicationMapper.selectById(id);
        if (a == null || Objects.equals(a.getDeleted(), 1)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "申请记录不存在");
        }
        return a;
    }

    private InternPositionEntity loadPosition(Long id) {
        InternPositionEntity p = positionMapper.selectById(id);
        if (p == null || Objects.equals(p.getDeleted(), 1)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "岗位不存在");
        }
        return p;
    }

    private InternBatchEntity loadBatch(Long id) {
        InternBatchEntity b = batchMapper.selectById(id);
        if (b == null || Objects.equals(b.getDeleted(), 1)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "批次不存在");
        }
        return b;
    }

    private ApplicationResponses.ListItem toListItem(InternApplicationEntity a) {
        ApplicationResponses.ListItem x = new ApplicationResponses.ListItem();
        x.setId(a.getId());
        x.setBatchId(a.getBatchId());
        x.setPositionId(a.getPositionId());
        x.setStudentUserId(a.getStudentUserId());
        x.setSchoolMentorUserId(a.getSchoolMentorUserId());
        x.setEnterpriseMentorUserId(a.getEnterpriseMentorUserId());
        x.setStatus(a.getStatus());
        x.setCreateTime(a.getCreateTime());
        x.setAuditTime(a.getAuditTime());
        InternBatchEntity b = batchMapper.selectById(a.getBatchId());
        if (b != null) {
            x.setBatchName(b.getBatchName());
        }
        InternPositionEntity p = positionMapper.selectById(a.getPositionId());
        if (p != null) {
            x.setPositionTitle(p.getTitle());
            InternEnterpriseEntity e = enterpriseMapper.selectById(p.getEnterpriseId());
            if (e != null) {
                x.setEnterpriseName(e.getEnterpriseName());
            }
        }
        return x;
    }

    /**
     * 列表/详情展示学生、校内导师姓名（用户中心），避免前端只能展示用户 ID。
     */
    private void fillUserDisplayNames(ApplicationResponses.ListItem x) {
        if (x == null) {
            return;
        }
        fillUserDisplayNames(Collections.singletonList(x));
    }

    private void fillUserDisplayNames(List<ApplicationResponses.ListItem> items) {
        if (items == null || items.isEmpty()) {
            return;
        }
        Set<Long> ids = new HashSet<>();
        for (ApplicationResponses.ListItem x : items) {
            if (x.getStudentUserId() != null) {
                ids.add(x.getStudentUserId());
            }
            if (x.getSchoolMentorUserId() != null) {
                ids.add(x.getSchoolMentorUserId());
            }
        }
        Map<Long, UserInfoVo> cache = new HashMap<>();
        for (Long id : ids) {
            UserInfoVo u = safeGetUser(id);
            if (u != null) {
                cache.put(id, u);
            }
        }
        for (ApplicationResponses.ListItem x : items) {
            UserInfoVo s = cache.get(x.getStudentUserId());
            if (s != null) {
                x.setStudentName(displayName(s));
            }
            UserInfoVo m = cache.get(x.getSchoolMentorUserId());
            if (m != null) {
                x.setSchoolMentorName(displayName(m));
            }
        }
    }

    private static String displayName(UserInfoVo u) {
        if (u == null) {
            return null;
        }
        if (StringUtils.hasText(u.getRealName())) {
            return u.getRealName();
        }
        if (StringUtils.hasText(u.getNickname())) {
            return u.getNickname();
        }
        return u.getUsername();
    }

    private UserInfoVo safeGetUser(Long id) {
        if (id == null) {
            return null;
        }
        try {
            Response<UserInfoVo> r = userInfoFeign.getUserById(id);
            if (r != null && Response.SUCCESS_CODE.equals(r.getCode()) && r.getData() != null) {
                return r.getData();
            }
        } catch (Exception ignored) {
            // 用户中心不可用时列表仍返回，仅缺少姓名
        }
        return null;
    }
}
