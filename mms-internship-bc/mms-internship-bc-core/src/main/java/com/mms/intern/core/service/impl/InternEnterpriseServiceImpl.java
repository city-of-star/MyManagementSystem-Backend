package com.mms.intern.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.intern.common.api.enterprise.EnterpriseRequests;
import com.mms.intern.common.api.enterprise.EnterpriseResponses;
import com.mms.intern.common.constant.InternConstants;
import com.mms.intern.common.entity.InternEnterpriseEntity;
import com.mms.intern.common.vo.PageResultVo;
import com.mms.intern.core.mapper.InternEnterpriseMapper;
import com.mms.intern.core.mapper.InternPositionMapper;
import com.mms.intern.common.entity.InternPositionEntity;
import com.mms.intern.core.service.InternEnterpriseService;
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
public class InternEnterpriseServiceImpl implements InternEnterpriseService {

    private final InternEnterpriseMapper enterpriseMapper;
    private final InternPositionMapper positionMapper;

    @Override
    public PageResultVo<EnterpriseResponses.ListItem> page(EnterpriseRequests.PageQuery q) {
        LambdaQueryWrapper<InternEnterpriseEntity> w = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(q.getKeyword())) {
            String kw = q.getKeyword().trim();
            w.and(x -> x.like(InternEnterpriseEntity::getEnterpriseName, kw)
                    .or().like(InternEnterpriseEntity::getContactName, kw));
        }
        if (StringUtils.hasText(q.getAuditStatus())) {
            w.eq(InternEnterpriseEntity::getAuditStatus, q.getAuditStatus());
        }
        if (q.getStatus() != null) {
            w.eq(InternEnterpriseEntity::getStatus, q.getStatus());
        }
        w.orderByDesc(InternEnterpriseEntity::getCreateTime);
        Page<InternEnterpriseEntity> page = enterpriseMapper.selectPage(new Page<>(q.getPageNum(), q.getPageSize()), w);
        Page<EnterpriseResponses.ListItem> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        voPage.setRecords(page.getRecords().stream().map(this::toListItem).collect(Collectors.toList()));
        return InternPageSupport.wrap(voPage);
    }

    @Override
    public List<EnterpriseResponses.Option> options(String keyword) {
        LambdaQueryWrapper<InternEnterpriseEntity> w = new LambdaQueryWrapper<>();
        w.eq(InternEnterpriseEntity::getAuditStatus, InternConstants.ENTERPRISE_APPROVED)
                .eq(InternEnterpriseEntity::getStatus, 1)
                .orderByAsc(InternEnterpriseEntity::getEnterpriseName);
        if (StringUtils.hasText(keyword)) {
            w.like(InternEnterpriseEntity::getEnterpriseName, keyword.trim());
        }
        return enterpriseMapper.selectList(w).stream().map(e -> {
            EnterpriseResponses.Option o = new EnterpriseResponses.Option();
            o.setId(e.getId());
            o.setEnterpriseName(e.getEnterpriseName());
            return o;
        }).collect(Collectors.toList());
    }

    @Override
    public EnterpriseResponses.Detail get(Long id) {
        InternEnterpriseEntity e = getEntity(id);
        EnterpriseResponses.Detail d = new EnterpriseResponses.Detail();
        BeanUtils.copyProperties(toListItem(e), d);
        d.setAddress(e.getAddress());
        d.setIntro(e.getIntro());
        d.setAuditRemark(e.getAuditRemark());
        d.setAuditBy(e.getAuditBy());
        d.setAuditTime(e.getAuditTime());
        d.setRemark(e.getRemark());
        d.setCreateBy(e.getCreateBy());
        d.setUpdateTime(e.getUpdateTime());
        return d;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(EnterpriseRequests.Save dto) {
        InternEnterpriseEntity e = new InternEnterpriseEntity();
        e.setEnterpriseName(dto.getEnterpriseName());
        e.setCreditCode(dto.getCreditCode());
        e.setContactName(dto.getContactName());
        e.setContactPhone(dto.getContactPhone());
        e.setAddress(dto.getAddress());
        e.setIntro(dto.getIntro());
        e.setRemark(dto.getRemark());
        e.setAuditStatus(InternConstants.ENTERPRISE_PENDING);
        e.setStatus(1);
        enterpriseMapper.insert(e);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, EnterpriseRequests.Save dto) {
        InternEnterpriseEntity e = getEntity(id);
        e.setEnterpriseName(dto.getEnterpriseName());
        e.setCreditCode(dto.getCreditCode());
        e.setContactName(dto.getContactName());
        e.setContactPhone(dto.getContactPhone());
        e.setAddress(dto.getAddress());
        e.setIntro(dto.getIntro());
        e.setRemark(dto.getRemark());
        enterpriseMapper.updateById(e);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        getEntity(id);
        long cnt = positionMapper.selectCount(new LambdaQueryWrapper<InternPositionEntity>()
                .eq(InternPositionEntity::getEnterpriseId, id));
        if (cnt > 0) {
            throw new BusinessException(ErrorCode.DATA_IN_USE, "存在关联岗位，无法删除企业");
        }
        enterpriseMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void audit(Long id, EnterpriseRequests.Audit dto) {
        InternEnterpriseEntity e = getEntity(id);
        if (!InternConstants.ENTERPRISE_APPROVED.equals(dto.getAuditStatus())
                && !InternConstants.ENTERPRISE_REJECTED.equals(dto.getAuditStatus())) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "审核状态无效");
        }
        e.setAuditStatus(dto.getAuditStatus());
        e.setAuditRemark(dto.getAuditRemark());
        e.setAuditBy(InternUserHelper.requireUserId());
        e.setAuditTime(LocalDateTime.now());
        enterpriseMapper.updateById(e);
    }

    private InternEnterpriseEntity getEntity(Long id) {
        InternEnterpriseEntity e = enterpriseMapper.selectById(id);
        if (e == null || Objects.equals(e.getDeleted(), 1)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "企业不存在");
        }
        return e;
    }

    private EnterpriseResponses.ListItem toListItem(InternEnterpriseEntity e) {
        EnterpriseResponses.ListItem x = new EnterpriseResponses.ListItem();
        x.setId(e.getId());
        x.setEnterpriseName(e.getEnterpriseName());
        x.setCreditCode(e.getCreditCode());
        x.setContactName(e.getContactName());
        x.setContactPhone(e.getContactPhone());
        x.setAuditStatus(e.getAuditStatus());
        x.setStatus(e.getStatus());
        x.setCreateTime(e.getCreateTime());
        return x;
    }
}
