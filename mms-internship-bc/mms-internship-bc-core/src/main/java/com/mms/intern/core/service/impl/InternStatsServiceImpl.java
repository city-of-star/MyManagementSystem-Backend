package com.mms.intern.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.intern.common.api.stats.StatsResponses;
import com.mms.intern.common.constant.InternConstants;
import com.mms.intern.common.entity.InternApplicationEntity;
import com.mms.intern.common.entity.InternBatchEntity;
import com.mms.intern.common.entity.InternPositionEntity;
import com.mms.intern.common.entity.InternWeeklyLogEntity;
import com.mms.intern.core.mapper.InternApplicationMapper;
import com.mms.intern.core.mapper.InternBatchMapper;
import com.mms.intern.core.mapper.InternPositionMapper;
import com.mms.intern.core.mapper.InternWeeklyLogMapper;
import com.mms.intern.core.service.InternStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class InternStatsServiceImpl implements InternStatsService {

    private final InternBatchMapper batchMapper;
    private final InternPositionMapper positionMapper;
    private final InternApplicationMapper applicationMapper;
    private final InternWeeklyLogMapper weeklyLogMapper;

    @Override
    public StatsResponses.BatchStatistics batchStats(Long batchId) {
        InternBatchEntity b = batchMapper.selectById(batchId);
        if (b == null || Objects.equals(b.getDeleted(), 1)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "批次不存在");
        }
        StatsResponses.BatchStatistics s = new StatsResponses.BatchStatistics();
        s.setBatchId(batchId);
        s.setBatchName(b.getBatchName());
        List<InternPositionEntity> positions = positionMapper.selectList(new LambdaQueryWrapper<InternPositionEntity>()
                .eq(InternPositionEntity::getBatchId, batchId));
        s.setPositionCount(positions.size());
        Set<Long> entIds = new HashSet<>();
        for (InternPositionEntity p : positions) {
            entIds.add(p.getEnterpriseId());
        }
        s.setEnterpriseCount(entIds.size());
        s.setApplicationTotal(countApp(batchId, null));
        s.setApplicationPending(countApp(batchId, InternConstants.APP_PENDING));
        s.setApplicationApproved(countApp(batchId, InternConstants.APP_APPROVED));
        s.setApplicationInProgress(countApp(batchId, InternConstants.APP_IN_PROGRESS));
        s.setApplicationCompleted(countApp(batchId, InternConstants.APP_COMPLETED));
        List<Long> appIds = applicationMapper.selectList(new LambdaQueryWrapper<InternApplicationEntity>()
                        .eq(InternApplicationEntity::getBatchId, batchId))
                .stream().map(InternApplicationEntity::getId).toList();
        if (appIds.isEmpty()) {
            s.setWeeklyLogPendingReview(0);
        } else {
            s.setWeeklyLogPendingReview(Math.toIntExact(weeklyLogMapper.selectCount(
                    new LambdaQueryWrapper<InternWeeklyLogEntity>()
                            .in(InternWeeklyLogEntity::getApplicationId, appIds)
                            .eq(InternWeeklyLogEntity::getStatus, InternConstants.LOG_SUBMITTED))));
        }
        return s;
    }

    private int countApp(Long batchId, String status) {
        LambdaQueryWrapper<InternApplicationEntity> w = new LambdaQueryWrapper<InternApplicationEntity>()
                .eq(InternApplicationEntity::getBatchId, batchId);
        if (status != null) {
            w.eq(InternApplicationEntity::getStatus, status);
        }
        return Math.toIntExact(applicationMapper.selectCount(w));
    }

    @Override
    public StatsResponses.Overview overview(Long batchId) {
        StatsResponses.Overview o = new StatsResponses.Overview();
        long totalBatches = batchMapper.selectCount(new LambdaQueryWrapper<InternBatchEntity>());
        long activeBatches = batchMapper.selectCount(new LambdaQueryWrapper<InternBatchEntity>().eq(InternBatchEntity::getActive, 1));
        o.setBatchCount(Math.toIntExact(totalBatches));
        o.setActiveBatchCount(Math.toIntExact(activeBatches));
        if (batchId != null) {
            o.setCurrentBatchStats(batchStats(batchId));
        }
        return o;
    }
}
