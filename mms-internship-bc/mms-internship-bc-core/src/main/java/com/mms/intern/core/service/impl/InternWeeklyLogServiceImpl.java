package com.mms.intern.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.intern.common.api.weekly.WeeklyLogRequests;
import com.mms.intern.common.api.weekly.WeeklyLogResponses;
import com.mms.intern.common.constant.InternConstants;
import com.mms.intern.common.entity.InternApplicationEntity;
import com.mms.intern.common.entity.InternWeeklyLogEntity;
import com.mms.intern.common.vo.PageResultVo;
import com.mms.intern.core.mapper.InternApplicationMapper;
import com.mms.intern.core.mapper.InternWeeklyLogMapper;
import com.mms.intern.core.service.InternWeeklyLogService;
import com.mms.intern.core.util.InternJsonUtils;
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
public class InternWeeklyLogServiceImpl implements InternWeeklyLogService {

    private final InternWeeklyLogMapper weeklyLogMapper;
    private final InternApplicationMapper applicationMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(WeeklyLogRequests.Save dto) {
        if (dto.getApplicationId() == null) {
            throw new BusinessException(ErrorCode.PARAM_MISSING, "applicationId 不能为空");
        }
        Long uid = InternUserHelper.requireUserId();
        InternApplicationEntity app = loadApplication(dto.getApplicationId());
        assertStudent(app, uid);
        if (existsWeek(app.getId(), dto.getWeekIndex())) {
            throw new BusinessException(ErrorCode.UNIQUE_CONSTRAINT_ERROR, "该周次周志已存在");
        }
        InternWeeklyLogEntity e = new InternWeeklyLogEntity();
        e.setApplicationId(dto.getApplicationId());
        e.setWeekIndex(dto.getWeekIndex());
        e.setTitle(dto.getTitle());
        e.setContent(dto.getContent());
        e.setAttachmentIds(InternJsonUtils.toJsonArray(dto.getAttachmentIds()));
        e.setStatus(InternConstants.LOG_SUBMITTED);
        weeklyLogMapper.insert(e);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, WeeklyLogRequests.Save dto) {
        Long uid = InternUserHelper.requireUserId();
        InternWeeklyLogEntity e = getEntity(id);
        InternApplicationEntity app = loadApplication(e.getApplicationId());
        assertStudent(app, uid);
        if (InternConstants.LOG_APPROVED.equals(e.getStatus())) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "已通过不可修改");
        }
        e.setWeekIndex(dto.getWeekIndex());
        e.setTitle(dto.getTitle());
        e.setContent(dto.getContent());
        e.setAttachmentIds(InternJsonUtils.toJsonArray(dto.getAttachmentIds()));
        if (InternConstants.LOG_REJECTED.equals(e.getStatus())) {
            e.setStatus(InternConstants.LOG_SUBMITTED);
            e.setReviewComment(null);
            e.setReviewBy(null);
            e.setReviewTime(null);
        }
        weeklyLogMapper.updateById(e);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        InternWeeklyLogEntity e = getEntity(id);
        Long uid = InternUserHelper.requireUserId();
        InternApplicationEntity app = loadApplication(e.getApplicationId());
        if (!Objects.equals(app.getStudentUserId(), uid)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "无权限删除");
        }
        weeklyLogMapper.deleteById(id);
    }

    @Override
    public WeeklyLogResponses.Detail get(Long id) {
        InternWeeklyLogEntity e = getEntity(id);
        InternApplicationEntity app = loadApplication(e.getApplicationId());
        Long uid = InternUserHelper.requireUserId();
        if (!Objects.equals(app.getStudentUserId(), uid) && !Objects.equals(app.getSchoolMentorUserId(), uid)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "无权限查看");
        }
        return toDetail(e);
    }

    @Override
    public WeeklyLogResponses.Detail getAdminDetail(Long id) {
        InternUserHelper.requireUserId();
        InternWeeklyLogEntity e = getEntity(id);
        return toDetail(e);
    }

    @Override
    public PageResultVo<WeeklyLogResponses.ListItem> myPage(WeeklyLogRequests.MyPageQuery q) {
        Long uid = InternUserHelper.requireUserId();
        InternApplicationEntity app = loadApplication(q.getApplicationId());
        assertStudent(app, uid);
        return pageInternal(q.getPageNum(), q.getPageSize(), q.getApplicationId(), null, null, null);
    }

    @Override
    public PageResultVo<WeeklyLogResponses.ListItem> pendingPage(WeeklyLogRequests.PendingPageQuery q) {
        Long uid = InternUserHelper.requireUserId();
        LambdaQueryWrapper<InternApplicationEntity> aw = new LambdaQueryWrapper<>();
        aw.eq(InternApplicationEntity::getSchoolMentorUserId, uid);
        if (q.getBatchId() != null) {
            aw.eq(InternApplicationEntity::getBatchId, q.getBatchId());
        }
        if (q.getApplicationId() != null) {
            aw.eq(InternApplicationEntity::getId, q.getApplicationId());
        }
        List<Long> appIds = applicationMapper.selectList(aw).stream().map(InternApplicationEntity::getId).toList();
        if (appIds.isEmpty()) {
            Page<WeeklyLogResponses.ListItem> empty = new Page<>(q.getPageNum(), q.getPageSize(), 0);
            return InternPageSupport.wrap(empty);
        }
        LambdaQueryWrapper<InternWeeklyLogEntity> w = new LambdaQueryWrapper<>();
        w.in(InternWeeklyLogEntity::getApplicationId, appIds)
                .eq(InternWeeklyLogEntity::getStatus, InternConstants.LOG_SUBMITTED)
                .orderByDesc(InternWeeklyLogEntity::getCreateTime);
        Page<InternWeeklyLogEntity> page = weeklyLogMapper.selectPage(new Page<>(q.getPageNum(), q.getPageSize()), w);
        Page<WeeklyLogResponses.ListItem> vo = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        vo.setRecords(page.getRecords().stream().map(this::toListItem).collect(Collectors.toList()));
        return InternPageSupport.wrap(vo);
    }

    @Override
    public PageResultVo<WeeklyLogResponses.ListItem> adminPage(WeeklyLogRequests.AdminPageQuery q) {
        return pageInternal(q.getPageNum(), q.getPageSize(), q.getApplicationId(), q.getBatchId(), q.getStatus(), q.getStudentKeyword());
    }

    @Override
    public List<WeeklyLogResponses.ListItem> listByApplication(Long applicationId) {
        Long uid = InternUserHelper.requireUserId();
        InternApplicationEntity app = loadApplication(applicationId);
        if (!Objects.equals(app.getStudentUserId(), uid) && !Objects.equals(app.getSchoolMentorUserId(), uid)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "无权限查看该申请下周志列表");
        }
        LambdaQueryWrapper<InternWeeklyLogEntity> w = new LambdaQueryWrapper<>();
        w.eq(InternWeeklyLogEntity::getApplicationId, applicationId).orderByAsc(InternWeeklyLogEntity::getWeekIndex);
        return weeklyLogMapper.selectList(w).stream().map(this::toListItem).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void review(Long id, WeeklyLogRequests.Review dto) {
        Long uid = InternUserHelper.requireUserId();
        InternWeeklyLogEntity e = getEntity(id);
        InternApplicationEntity app = loadApplication(e.getApplicationId());
        if (!Objects.equals(app.getSchoolMentorUserId(), uid)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "仅校内导师可批阅");
        }
        if (!InternConstants.LOG_APPROVED.equals(dto.getStatus()) && !InternConstants.LOG_REJECTED.equals(dto.getStatus())) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "批阅状态无效");
        }
        e.setStatus(dto.getStatus());
        e.setReviewComment(dto.getReviewComment());
        e.setReviewBy(uid);
        e.setReviewTime(LocalDateTime.now());
        weeklyLogMapper.updateById(e);
    }

    private PageResultVo<WeeklyLogResponses.ListItem> pageInternal(int pageNum, int pageSize, Long applicationId,
                                                                    Long batchId, String status, String studentKeyword) {
        LambdaQueryWrapper<InternWeeklyLogEntity> w = new LambdaQueryWrapper<>();
        if (applicationId != null) {
            w.eq(InternWeeklyLogEntity::getApplicationId, applicationId);
        }
        if (batchId != null || StringUtils.hasText(studentKeyword)) {
            LambdaQueryWrapper<InternApplicationEntity> aw = new LambdaQueryWrapper<>();
            if (batchId != null) {
                aw.eq(InternApplicationEntity::getBatchId, batchId);
            }
            if (StringUtils.hasText(studentKeyword)) {
                try {
                    aw.eq(InternApplicationEntity::getStudentUserId, Long.parseLong(studentKeyword.trim()));
                } catch (NumberFormatException ex) {
                    aw.like(InternApplicationEntity::getRemark, studentKeyword.trim());
                }
            }
            List<Long> ids = applicationMapper.selectList(aw).stream().map(InternApplicationEntity::getId).toList();
            if (ids.isEmpty()) {
                Page<WeeklyLogResponses.ListItem> empty = new Page<>(pageNum, pageSize, 0);
                return InternPageSupport.wrap(empty);
            }
            w.in(InternWeeklyLogEntity::getApplicationId, ids);
        }
        if (StringUtils.hasText(status)) {
            w.eq(InternWeeklyLogEntity::getStatus, status);
        }
        w.orderByDesc(InternWeeklyLogEntity::getCreateTime);
        Page<InternWeeklyLogEntity> page = weeklyLogMapper.selectPage(new Page<>(pageNum, pageSize), w);
        Page<WeeklyLogResponses.ListItem> vo = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        vo.setRecords(page.getRecords().stream().map(this::toListItem).collect(Collectors.toList()));
        return InternPageSupport.wrap(vo);
    }

    private boolean existsWeek(Long applicationId, int weekIndex) {
        return weeklyLogMapper.selectCount(new LambdaQueryWrapper<InternWeeklyLogEntity>()
                .eq(InternWeeklyLogEntity::getApplicationId, applicationId)
                .eq(InternWeeklyLogEntity::getWeekIndex, weekIndex)) > 0;
    }

    private void assertStudent(InternApplicationEntity app, Long uid) {
        if (!Objects.equals(app.getStudentUserId(), uid)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "仅本人可操作该实习申请下的周志");
        }
    }

    private InternApplicationEntity loadApplication(Long id) {
        InternApplicationEntity a = applicationMapper.selectById(id);
        if (a == null || Objects.equals(a.getDeleted(), 1)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "实习申请不存在");
        }
        return a;
    }

    private InternWeeklyLogEntity getEntity(Long id) {
        InternWeeklyLogEntity e = weeklyLogMapper.selectById(id);
        if (e == null || Objects.equals(e.getDeleted(), 1)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "周志不存在");
        }
        return e;
    }

    private WeeklyLogResponses.ListItem toListItem(InternWeeklyLogEntity e) {
        WeeklyLogResponses.ListItem x = new WeeklyLogResponses.ListItem();
        x.setId(e.getId());
        x.setApplicationId(e.getApplicationId());
        x.setWeekIndex(e.getWeekIndex());
        x.setTitle(e.getTitle());
        x.setStatus(e.getStatus());
        x.setCreateTime(e.getCreateTime());
        x.setReviewTime(e.getReviewTime());
        return x;
    }

    private WeeklyLogResponses.Detail toDetail(InternWeeklyLogEntity e) {
        WeeklyLogResponses.Detail d = new WeeklyLogResponses.Detail();
        BeanUtils.copyProperties(toListItem(e), d);
        d.setContent(e.getContent());
        d.setAttachmentIds(InternJsonUtils.parseLongList(e.getAttachmentIds()));
        d.setReviewComment(e.getReviewComment());
        d.setReviewBy(e.getReviewBy());
        d.setUpdateTime(e.getUpdateTime());
        return d;
    }
}
