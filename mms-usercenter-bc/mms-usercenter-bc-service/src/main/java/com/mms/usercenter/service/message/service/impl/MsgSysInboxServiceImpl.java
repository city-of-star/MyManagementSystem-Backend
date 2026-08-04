package com.mms.usercenter.service.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.webmvc.utils.UserContextUtils;
import com.mms.usercenter.common.message.constants.MsgConstants;
import com.mms.usercenter.common.message.dto.MsgSysInboxPageQueryDto;
import com.mms.usercenter.common.message.dto.MsgSysInboxStarDto;
import com.mms.usercenter.common.message.entity.MsgSysInboxEntity;
import com.mms.usercenter.common.message.vo.MsgSysInboxVo;
import com.mms.usercenter.service.message.mapper.MsgSysInboxMapper;
import com.mms.usercenter.service.message.service.MsgSysInboxService;
import com.mms.usercenter.service.message.support.MsgUnreadSupport;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * 实现功能【系统收件箱服务实现】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-08-03 23:10:00
 */
@Service
public class MsgSysInboxServiceImpl implements MsgSysInboxService {

    @Resource
    private MsgSysInboxMapper msgSysInboxMapper;

    @Resource
    private MsgUnreadSupport msgUnreadSupport;

    @Override
    public Page<MsgSysInboxVo> getInboxPage(MsgSysInboxPageQueryDto dto) {
        Long userId = requireUserId();
        int pageNum = dto.getPageNum() == null || dto.getPageNum() < 1 ? 1 : dto.getPageNum();
        int pageSize = dto.getPageSize() == null || dto.getPageSize() < 1 ? 20 : dto.getPageSize();
        LambdaQueryWrapper<MsgSysInboxEntity> wrapper = new LambdaQueryWrapper<MsgSysInboxEntity>()
                .eq(MsgSysInboxEntity::getUserId, userId)
                .eq(dto.getStarred() != null && dto.getStarred() == 1, MsgSysInboxEntity::getStarred, 1)
                .and(StringUtils.hasText(dto.getKeyword()), w -> w
                        .like(MsgSysInboxEntity::getTitle, dto.getKeyword())
                        .or()
                        .like(MsgSysInboxEntity::getContentText, dto.getKeyword()));
        if (MsgConstants.SORT_TIME.equalsIgnoreCase(dto.getSortMode())) {
            wrapper.orderByDesc(MsgSysInboxEntity::getCreateTime);
        } else {
            wrapper.orderByAsc(MsgSysInboxEntity::getReadFlag)
                    .orderByDesc(MsgSysInboxEntity::getCreateTime);
        }
        Page<MsgSysInboxEntity> page = msgSysInboxMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        Page<MsgSysInboxVo> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(page.getRecords().stream().map(this::toVo).collect(Collectors.toList()));
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MsgSysInboxVo getInboxById(Long id) {
        MsgSysInboxEntity entity = requireOwnInbox(id);
        if (entity.getReadFlag() == null || entity.getReadFlag() == 0) {
            entity.setReadFlag(1);
            entity.setReadTime(LocalDateTime.now());
            msgSysInboxMapper.updateById(entity);
            msgUnreadSupport.pushUnread(entity.getUserId());
        }
        return toVo(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markRead(Long id) {
        MsgSysInboxEntity entity = requireOwnInbox(id);
        if (entity.getReadFlag() != null && entity.getReadFlag() == 1) {
            return;
        }
        entity.setReadFlag(1);
        entity.setReadTime(LocalDateTime.now());
        msgSysInboxMapper.updateById(entity);
        msgUnreadSupport.pushUnread(entity.getUserId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAllRead() {
        Long userId = requireUserId();
        msgSysInboxMapper.update(null, new LambdaUpdateWrapper<MsgSysInboxEntity>()
                .eq(MsgSysInboxEntity::getUserId, userId)
                .eq(MsgSysInboxEntity::getReadFlag, 0)
                .set(MsgSysInboxEntity::getReadFlag, 1)
                .set(MsgSysInboxEntity::getReadTime, LocalDateTime.now()));
        msgUnreadSupport.pushUnread(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void star(MsgSysInboxStarDto dto) {
        MsgSysInboxEntity entity = requireOwnInbox(dto.getId());
        int starred = dto.getStarred() != null && dto.getStarred() == 1 ? 1 : 0;
        entity.setStarred(starred);
        msgSysInboxMapper.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteInbox(Long id) {
        MsgSysInboxEntity entity = requireOwnInbox(id);
        msgSysInboxMapper.deleteById(entity.getId());
        msgUnreadSupport.pushUnread(entity.getUserId());
    }

    private MsgSysInboxEntity requireOwnInbox(Long id) {
        Long userId = requireUserId();
        MsgSysInboxEntity entity = msgSysInboxMapper.selectById(id);
        if (entity == null || !userId.equals(entity.getUserId())) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "消息不存在");
        }
        return entity;
    }

    private Long requireUserId() {
        Long userId = UserContextUtils.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.LOGIN_EXPIRED, "未登录");
        }
        return userId;
    }

    private MsgSysInboxVo toVo(MsgSysInboxEntity entity) {
        MsgSysInboxVo vo = new MsgSysInboxVo();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
