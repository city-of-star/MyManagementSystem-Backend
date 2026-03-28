package com.mms.intern.core.service.impl.support;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mms.intern.common.constant.InternConstants;
import com.mms.intern.common.entity.InternApplicationEntity;
import com.mms.intern.common.entity.InternPositionEntity;
import com.mms.intern.core.mapper.InternApplicationMapper;
import com.mms.intern.core.mapper.InternPositionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class InternPositionQuotaSupport {

    private final InternPositionMapper positionMapper;
    private final InternApplicationMapper applicationMapper;

    public boolean hasQuota(Long positionId) {
        InternPositionEntity p = positionMapper.selectById(positionId);
        if (p == null || Objects.equals(p.getDeleted(), 1)) {
            return false;
        }
        int quota = p.getQuota() == null ? 0 : p.getQuota();
        int used = countOccupied(positionId);
        return used < quota;
    }

    public int countOccupied(Long positionId) {
        LambdaQueryWrapper<InternApplicationEntity> w = new LambdaQueryWrapper<>();
        w.eq(InternApplicationEntity::getPositionId, positionId)
                .in(InternApplicationEntity::getStatus,
                        List.of(InternConstants.APP_PENDING, InternConstants.APP_APPROVED,
                                InternConstants.APP_IN_PROGRESS, InternConstants.APP_COMPLETED));
        return Math.toIntExact(applicationMapper.selectCount(w));
    }
}
