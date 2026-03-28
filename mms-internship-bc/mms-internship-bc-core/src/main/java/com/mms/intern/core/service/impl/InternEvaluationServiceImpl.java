package com.mms.intern.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.intern.common.api.evaluation.EvaluationRequests;
import com.mms.intern.common.api.evaluation.EvaluationResponses;
import com.mms.intern.common.entity.InternApplicationEntity;
import com.mms.intern.common.entity.InternEvaluationEntity;
import com.mms.intern.core.mapper.InternApplicationMapper;
import com.mms.intern.core.mapper.InternEvaluationMapper;
import com.mms.intern.core.service.InternEvaluationService;
import com.mms.intern.core.util.InternUserHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class InternEvaluationServiceImpl implements InternEvaluationService {

    private final InternEvaluationMapper evaluationMapper;
    private final InternApplicationMapper applicationMapper;

    @Override
    public EvaluationResponses.Vo getByApplication(Long applicationId) {
        assertCanView(applicationId);
        InternEvaluationEntity e = selectByApplication(applicationId);
        if (e == null) {
            return null;
        }
        return toVo(e);
    }

    @Override
    public EvaluationResponses.Vo get(Long id) {
        InternEvaluationEntity e = evaluationMapper.selectById(id);
        if (e == null || Objects.equals(e.getDeleted(), 1)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "评价记录不存在");
        }
        assertCanView(e.getApplicationId());
        return toVo(e);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSchool(EvaluationRequests.School dto) {
        InternEvaluationEntity e = getOrCreate(dto.getApplicationId());
        e.setSchoolScore(dto.getSchoolScore());
        e.setSchoolComment(dto.getSchoolComment());
        e.setSchoolBy(InternUserHelper.requireUserId());
        e.setSchoolTime(LocalDateTime.now());
        persist(e);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveEnterprise(EvaluationRequests.Enterprise dto) {
        InternEvaluationEntity e = getOrCreate(dto.getApplicationId());
        e.setEnterpriseScore(dto.getEnterpriseScore());
        e.setEnterpriseComment(dto.getEnterpriseComment());
        e.setEnterpriseBy(InternUserHelper.requireUserId());
        e.setEnterpriseTime(LocalDateTime.now());
        persist(e);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finalizeScore(Long applicationId, EvaluationRequests.Finalize dto) {
        InternEvaluationEntity e = getOrCreate(applicationId);
        BigDecimal sw = dto.getSchoolWeight() != null ? dto.getSchoolWeight() : new BigDecimal("0.6");
        BigDecimal ew = dto.getEnterpriseWeight() != null ? dto.getEnterpriseWeight() : new BigDecimal("0.4");
        BigDecimal s = e.getSchoolScore() != null ? e.getSchoolScore() : BigDecimal.ZERO;
        BigDecimal en = e.getEnterpriseScore() != null ? e.getEnterpriseScore() : BigDecimal.ZERO;
        BigDecimal finalScore = s.multiply(sw).add(en.multiply(ew)).setScale(2, RoundingMode.HALF_UP);
        e.setFinalScore(finalScore);
        if (dto.getFinalRemark() != null) {
            e.setFinalRemark(dto.getFinalRemark());
        }
        persist(e);
    }

    private void persist(InternEvaluationEntity e) {
        if (e.getId() == null) {
            evaluationMapper.insert(e);
        } else {
            evaluationMapper.updateById(e);
        }
    }

    private InternEvaluationEntity getOrCreate(Long applicationId) {
        loadApplication(applicationId);
        InternEvaluationEntity e = selectByApplication(applicationId);
        if (e == null) {
            e = new InternEvaluationEntity();
            e.setApplicationId(applicationId);
        }
        return e;
    }

    private InternEvaluationEntity selectByApplication(Long applicationId) {
        return evaluationMapper.selectOne(new LambdaQueryWrapper<InternEvaluationEntity>()
                .eq(InternEvaluationEntity::getApplicationId, applicationId));
    }

    private void assertCanView(Long applicationId) {
        Long uid = InternUserHelper.requireUserId();
        InternApplicationEntity a = loadApplication(applicationId);
        if (!Objects.equals(a.getStudentUserId(), uid) && !Objects.equals(a.getSchoolMentorUserId(), uid)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "无权限查看评价");
        }
    }

    private InternApplicationEntity loadApplication(Long id) {
        InternApplicationEntity a = applicationMapper.selectById(id);
        if (a == null || Objects.equals(a.getDeleted(), 1)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "实习申请不存在");
        }
        return a;
    }

    private EvaluationResponses.Vo toVo(InternEvaluationEntity e) {
        EvaluationResponses.Vo v = new EvaluationResponses.Vo();
        BeanUtils.copyProperties(e, v);
        return v;
    }
}
