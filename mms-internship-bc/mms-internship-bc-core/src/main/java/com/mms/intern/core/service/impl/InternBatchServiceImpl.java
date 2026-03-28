package com.mms.intern.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.intern.common.api.batch.BatchRequests;
import com.mms.intern.common.api.batch.BatchResponses;
import com.mms.intern.common.entity.InternBatchEntity;
import com.mms.intern.common.entity.InternPositionEntity;
import com.mms.intern.common.vo.PageResultVo;
import com.mms.intern.core.mapper.InternBatchMapper;
import com.mms.intern.core.mapper.InternPositionMapper;
import com.mms.intern.core.service.InternBatchService;
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
public class InternBatchServiceImpl implements InternBatchService {

    private final InternBatchMapper batchMapper;
    private final InternPositionMapper positionMapper;

    @Override
    public PageResultVo<BatchResponses.ListItem> page(BatchRequests.PageQuery q) {
        LambdaQueryWrapper<InternBatchEntity> w = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(q.getKeyword())) {
            w.like(InternBatchEntity::getBatchName, q.getKeyword().trim());
        }
        if (q.getActive() != null) {
            w.eq(InternBatchEntity::getActive, q.getActive());
        }
        w.orderByDesc(InternBatchEntity::getCreateTime);
        Page<InternBatchEntity> page = batchMapper.selectPage(new Page<>(q.getPageNum(), q.getPageSize()), w);
        Page<BatchResponses.ListItem> vo = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        vo.setRecords(page.getRecords().stream().map(this::toListItem).collect(Collectors.toList()));
        return InternPageSupport.wrap(vo);
    }

    @Override
    public List<BatchResponses.Option> options() {
        LambdaQueryWrapper<InternBatchEntity> w = new LambdaQueryWrapper<>();
        w.eq(InternBatchEntity::getActive, 1).orderByDesc(InternBatchEntity::getCreateTime);
        return batchMapper.selectList(w).stream().map(b -> {
            BatchResponses.Option o = new BatchResponses.Option();
            o.setId(b.getId());
            o.setBatchName(b.getBatchName());
            o.setSchoolYear(b.getSchoolYear());
            o.setTerm(b.getTerm());
            return o;
        }).collect(Collectors.toList());
    }

    @Override
    public BatchResponses.Detail get(Long id) {
        InternBatchEntity b = getEntity(id);
        BatchResponses.Detail d = new BatchResponses.Detail();
        BeanUtils.copyProperties(toListItem(b), d);
        d.setRemark(b.getRemark());
        d.setCreateBy(b.getCreateBy());
        d.setUpdateBy(b.getUpdateBy());
        d.setUpdateTime(b.getUpdateTime());
        return d;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(BatchRequests.Save dto) {
        InternBatchEntity b = new InternBatchEntity();
        b.setBatchName(dto.getBatchName());
        b.setSchoolYear(dto.getSchoolYear());
        b.setTerm(dto.getTerm());
        b.setSignUpStart(dto.getSignUpStart());
        b.setSignUpEnd(dto.getSignUpEnd());
        b.setActive(dto.getActive() == null ? 1 : dto.getActive());
        b.setRemark(dto.getRemark());
        batchMapper.insert(b);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, BatchRequests.Save dto) {
        InternBatchEntity b = getEntity(id);
        b.setBatchName(dto.getBatchName());
        b.setSchoolYear(dto.getSchoolYear());
        b.setTerm(dto.getTerm());
        b.setSignUpStart(dto.getSignUpStart());
        b.setSignUpEnd(dto.getSignUpEnd());
        if (dto.getActive() != null) {
            b.setActive(dto.getActive());
        }
        b.setRemark(dto.getRemark());
        batchMapper.updateById(b);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        getEntity(id);
        long cnt = positionMapper.selectCount(new LambdaQueryWrapper<InternPositionEntity>()
                .eq(InternPositionEntity::getBatchId, id));
        if (cnt > 0) {
            throw new BusinessException(ErrorCode.DATA_IN_USE, "批次下存在岗位，无法删除");
        }
        batchMapper.deleteById(id);
    }

    private InternBatchEntity getEntity(Long id) {
        InternBatchEntity b = batchMapper.selectById(id);
        if (b == null || Objects.equals(b.getDeleted(), 1)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "批次不存在");
        }
        return b;
    }

    private BatchResponses.ListItem toListItem(InternBatchEntity b) {
        BatchResponses.ListItem x = new BatchResponses.ListItem();
        x.setId(b.getId());
        x.setBatchName(b.getBatchName());
        x.setSchoolYear(b.getSchoolYear());
        x.setTerm(b.getTerm());
        x.setSignUpStart(b.getSignUpStart());
        x.setSignUpEnd(b.getSignUpEnd());
        x.setActive(b.getActive());
        x.setCreateTime(b.getCreateTime());
        return x;
    }
}
