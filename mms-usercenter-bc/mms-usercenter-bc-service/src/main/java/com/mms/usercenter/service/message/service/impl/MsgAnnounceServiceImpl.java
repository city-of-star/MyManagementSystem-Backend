package com.mms.usercenter.service.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.usercenter.common.auth.entity.UserEntity;
import com.mms.usercenter.common.auth.entity.UserRoleEntity;
import com.mms.usercenter.common.message.constants.MsgConstants;
import com.mms.usercenter.common.message.dto.MsgAnnounceCreateDto;
import com.mms.usercenter.common.message.dto.MsgAnnouncePageQueryDto;
import com.mms.usercenter.common.message.dto.MsgAnnounceUpdateDto;
import com.mms.usercenter.common.message.dto.MsgAnnounceUserPageQueryDto;
import com.mms.usercenter.common.message.entity.MsgSysAnnounceEntity;
import com.mms.usercenter.common.message.entity.MsgSysInboxEntity;
import com.mms.usercenter.common.message.vo.MsgAnnounceUserVo;
import com.mms.usercenter.common.message.vo.MsgAnnounceVo;
import com.mms.usercenter.service.auth.mapper.UserMapper;
import com.mms.usercenter.service.auth.mapper.UserRoleMapper;
import com.mms.usercenter.service.message.mapper.MsgSysAnnounceMapper;
import com.mms.usercenter.service.message.mapper.MsgSysInboxMapper;
import com.mms.usercenter.service.message.service.MsgAnnounceService;
import com.mms.usercenter.service.message.support.MsgUnreadSupport;
import com.mms.usercenter.service.message.utils.MsgHtmlSanitizeUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 实现功能【系统公告服务实现】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-08-03 23:10:00
 */
@Slf4j
@Service
public class MsgAnnounceServiceImpl implements MsgAnnounceService {

    @Resource
    private MsgSysAnnounceMapper msgSysAnnounceMapper;

    @Resource
    private MsgSysInboxMapper msgSysInboxMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private MsgUnreadSupport msgUnreadSupport;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    @Qualifier("schedulerTaskExecutor")
    private ThreadPoolTaskExecutor schedulerTaskExecutor;

    @Override
    public Page<MsgAnnounceVo> getAnnouncePage(MsgAnnouncePageQueryDto dto) {
        int pageNum = dto.getPageNum() == null || dto.getPageNum() < 1 ? 1 : dto.getPageNum();
        int pageSize = dto.getPageSize() == null || dto.getPageSize() < 1 ? 10 : dto.getPageSize();
        Page<MsgSysAnnounceEntity> page = msgSysAnnounceMapper.selectPage(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<MsgSysAnnounceEntity>()
                        .like(StringUtils.hasText(dto.getKeyword()), MsgSysAnnounceEntity::getTitle, dto.getKeyword())
                        .eq(dto.getStatus() != null, MsgSysAnnounceEntity::getStatus, dto.getStatus())
                        .orderByDesc(MsgSysAnnounceEntity::getCreateTime));
        Page<MsgAnnounceVo> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<MsgAnnounceVo> records = page.getRecords().stream().map(this::toListVo).collect(Collectors.toList());
        fillCreatorNames(records);
        for (MsgAnnounceVo vo : records) {
            fillReadStats(vo);
        }
        result.setRecords(records);
        return result;
    }

    @Override
    public MsgAnnounceVo getAnnounceById(Long id) {
        MsgSysAnnounceEntity entity = requireAnnounce(id);
        MsgAnnounceVo vo = toDetailVo(entity);
        fillCreatorNames(List.of(vo));
        fillReadStats(vo);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MsgAnnounceVo createAnnounce(MsgAnnounceCreateDto dto) {
        validateScope(dto);
        String html = MsgHtmlSanitizeUtils.sanitize(dto.getContentHtml());
        if (!StringUtils.hasText(html)) {
            throw new BusinessException("公告内容不能为空");
        }
        List<Long> targetUserIds = resolveTargetUserIds(dto);
        if (targetUserIds.isEmpty()) {
            throw new BusinessException("发送范围内没有可用用户");
        }

        MsgSysAnnounceEntity entity = new MsgSysAnnounceEntity();
        entity.setTitle(dto.getTitle().trim());
        entity.setContentHtml(html);
        entity.setContentText(MsgHtmlSanitizeUtils.toPlainText(html, 500));
        entity.setScopeType(dto.getScopeType());
        entity.setScopePayload(writeScopePayload(dto));
        entity.setStatus(MsgConstants.ANNOUNCE_PENDING);
        entity.setTotalTarget(targetUserIds.size());
        entity.setSuccessCount(0);
        entity.setFailCount(0);
        entity.setDeleted(0);
        msgSysAnnounceMapper.insert(entity);

        Long announceId = entity.getId();
        if (targetUserIds.size() <= MsgConstants.SYNC_FANOUT_MAX) {
            doFanout(announceId, targetUserIds);
        } else {
            runAfterCommit(() -> schedulerTaskExecutor.execute(() -> doFanout(announceId, targetUserIds)));
        }
        return getAnnounceById(announceId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MsgAnnounceVo updateAnnounce(Long id, MsgAnnounceUpdateDto dto) {
        MsgSysAnnounceEntity entity = requireAnnounce(id);
        Integer status = entity.getStatus();
        if (status != null && status == MsgConstants.ANNOUNCE_RUNNING) {
            throw new BusinessException("公告发送中，暂不可修改");
        }
        if (status != null && status == MsgConstants.ANNOUNCE_RECALLED) {
            throw new BusinessException("已撤回的公告不可修改");
        }
        String html = MsgHtmlSanitizeUtils.sanitize(dto.getContentHtml());
        if (!StringUtils.hasText(html)) {
            throw new BusinessException("公告内容不能为空");
        }
        String title = dto.getTitle().trim();
        String text = MsgHtmlSanitizeUtils.toPlainText(html, 500);
        entity.setTitle(title);
        entity.setContentHtml(html);
        entity.setContentText(text);
        msgSysAnnounceMapper.updateById(entity);

        List<MsgSysInboxEntity> inboxes = msgSysInboxMapper.selectList(new LambdaQueryWrapper<MsgSysInboxEntity>()
                .eq(MsgSysInboxEntity::getAnnounceId, id));
        List<Long> notifyUserIds = new ArrayList<>();
        for (MsgSysInboxEntity inbox : inboxes) {
            inbox.setTitle(title);
            inbox.setContentHtml(html);
            inbox.setContentText(text);
            msgSysInboxMapper.updateById(inbox);
            if (inbox.getUserId() != null) {
                notifyUserIds.add(inbox.getUserId());
            }
        }
        msgUnreadSupport.pushUnreadBatch(notifyUserIds);
        return getAnnounceById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recallAnnounce(Long id) {
        MsgSysAnnounceEntity entity = requireAnnounce(id);
        Integer status = entity.getStatus();
        if (status != null && status == MsgConstants.ANNOUNCE_RUNNING) {
            throw new BusinessException("公告发送中，暂不可撤回");
        }
        if (status != null && status == MsgConstants.ANNOUNCE_RECALLED) {
            throw new BusinessException("公告已撤回");
        }
        softDeleteInboxByAnnounce(id);
        entity.setStatus(MsgConstants.ANNOUNCE_RECALLED);
        entity.setErrorMsg(null);
        msgSysAnnounceMapper.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAnnounce(Long id) {
        MsgSysAnnounceEntity entity = requireAnnounce(id);
        Integer status = entity.getStatus();
        if (status != null && status == MsgConstants.ANNOUNCE_RUNNING) {
            throw new BusinessException("公告发送中，暂不可删除");
        }
        softDeleteInboxByAnnounce(id);
        msgSysAnnounceMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void retryAnnounce(Long id) {
        MsgSysAnnounceEntity entity = requireAnnounce(id);
        if (entity.getStatus() == null || entity.getStatus() != MsgConstants.ANNOUNCE_FAILED) {
            throw new BusinessException("仅失败状态的公告可重试发送");
        }
        List<Long> targetUserIds = resolveTargetUserIdsFromEntity(entity);
        if (targetUserIds.isEmpty()) {
            throw new BusinessException("发送范围内没有可用用户");
        }
        entity.setStatus(MsgConstants.ANNOUNCE_PENDING);
        entity.setErrorMsg(null);
        entity.setCursorJson(null);
        entity.setTotalTarget(targetUserIds.size());
        entity.setSuccessCount(0);
        entity.setFailCount(0);
        msgSysAnnounceMapper.updateById(entity);
        Long announceId = entity.getId();
        runAfterCommit(() -> schedulerTaskExecutor.execute(() -> doFanout(announceId, targetUserIds)));
    }

    @Override
    public Page<MsgAnnounceUserVo> pageReadUsers(Long announceId, MsgAnnounceUserPageQueryDto dto) {
        requireAnnounce(announceId);
        return pageUsers(announceId, 1, dto);
    }

    @Override
    public Page<MsgAnnounceUserVo> pageUnreadUsers(Long announceId, MsgAnnounceUserPageQueryDto dto) {
        requireAnnounce(announceId);
        return pageUsers(announceId, 0, dto);
    }

    private Page<MsgAnnounceUserVo> pageUsers(Long announceId, int readFlag, MsgAnnounceUserPageQueryDto dto) {
        int pageNum = dto.getPageNum() == null || dto.getPageNum() < 1 ? 1 : dto.getPageNum();
        int pageSize = dto.getPageSize() == null || dto.getPageSize() < 1 ? 10 : dto.getPageSize();
        return msgSysInboxMapper.pageAnnounceUsers(new Page<>(pageNum, pageSize), announceId, readFlag, dto);
    }

    private void doFanout(Long announceId, List<Long> targetUserIds) {
        MsgSysAnnounceEntity entity = msgSysAnnounceMapper.selectById(announceId);
        if (entity == null) {
            return;
        }
        entity.setStatus(MsgConstants.ANNOUNCE_RUNNING);
        msgSysAnnounceMapper.updateById(entity);

        int success = entity.getSuccessCount() == null ? 0 : entity.getSuccessCount();
        int fail = entity.getFailCount() == null ? 0 : entity.getFailCount();
        try {
            for (int i = 0; i < targetUserIds.size(); i += MsgConstants.FANOUT_BATCH_SIZE) {
                int end = Math.min(i + MsgConstants.FANOUT_BATCH_SIZE, targetUserIds.size());
                List<Long> batch = targetUserIds.subList(i, end);
                List<Long> notified = new ArrayList<>();
                for (Long userId : batch) {
                    try {
                        if (insertInboxIfAbsent(entity, userId)) {
                            success++;
                            notified.add(userId);
                        }
                    } catch (Exception ex) {
                        fail++;
                        log.warn("公告扇出失败 announceId={} userId={}", announceId, userId, ex);
                    }
                }
                entity.setSuccessCount(success);
                entity.setFailCount(fail);
                entity.setCursorJson(writeCursor(end));
                msgSysAnnounceMapper.updateById(entity);
                msgUnreadSupport.pushUnreadBatch(notified);
            }
            entity.setStatus(fail > 0 && success == 0 ? MsgConstants.ANNOUNCE_FAILED : MsgConstants.ANNOUNCE_DONE);
            if (fail > 0 && success == 0) {
                entity.setErrorMsg("全部目标用户发送失败");
            }
            msgSysAnnounceMapper.updateById(entity);
        } catch (Exception ex) {
            log.error("公告扇出异常 announceId={}", announceId, ex);
            entity.setStatus(MsgConstants.ANNOUNCE_FAILED);
            entity.setErrorMsg(ex.getMessage() == null ? "发送失败" : truncate(ex.getMessage(), 500));
            entity.setSuccessCount(success);
            entity.setFailCount(fail);
            msgSysAnnounceMapper.updateById(entity);
        }
    }

    private boolean insertInboxIfAbsent(MsgSysAnnounceEntity announce, Long userId) {
        Long exists = msgSysInboxMapper.selectCount(new LambdaQueryWrapper<MsgSysInboxEntity>()
                .eq(MsgSysInboxEntity::getAnnounceId, announce.getId())
                .eq(MsgSysInboxEntity::getUserId, userId));
        if (exists != null && exists > 0) {
            return false;
        }
        MsgSysInboxEntity inbox = new MsgSysInboxEntity();
        inbox.setUserId(userId);
        inbox.setAnnounceId(announce.getId());
        inbox.setBizType(MsgConstants.BIZ_TYPE_ANNOUNCE);
        inbox.setBizId(String.valueOf(announce.getId()));
        inbox.setTitle(announce.getTitle());
        inbox.setContentHtml(announce.getContentHtml());
        inbox.setContentText(announce.getContentText());
        inbox.setStarred(0);
        inbox.setReadFlag(0);
        inbox.setDeleted(0);
        try {
            msgSysInboxMapper.insert(inbox);
            return true;
        } catch (Exception ex) {
            Long again = msgSysInboxMapper.selectCount(new LambdaQueryWrapper<MsgSysInboxEntity>()
                    .eq(MsgSysInboxEntity::getAnnounceId, announce.getId())
                    .eq(MsgSysInboxEntity::getUserId, userId));
            if (again != null && again > 0) {
                return false;
            }
            throw ex;
        }
    }

    private void softDeleteInboxByAnnounce(Long announceId) {
        List<MsgSysInboxEntity> inboxes = msgSysInboxMapper.selectList(new LambdaQueryWrapper<MsgSysInboxEntity>()
                .eq(MsgSysInboxEntity::getAnnounceId, announceId));
        List<Long> notifyUserIds = new ArrayList<>();
        for (MsgSysInboxEntity inbox : inboxes) {
            if (inbox.getUserId() != null) {
                notifyUserIds.add(inbox.getUserId());
            }
            msgSysInboxMapper.deleteById(inbox.getId());
        }
        msgUnreadSupport.pushUnreadBatch(notifyUserIds);
    }

    private void validateScope(MsgAnnounceCreateDto dto) {
        Integer scopeType = dto.getScopeType();
        if (scopeType == null || (scopeType != MsgConstants.SCOPE_USER
                && scopeType != MsgConstants.SCOPE_ROLE
                && scopeType != MsgConstants.SCOPE_ALL)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "发送范围不合法");
        }
        if (scopeType == MsgConstants.SCOPE_USER && CollectionUtils.isEmpty(dto.getUserIds())) {
            throw new BusinessException("指定用户不能为空");
        }
        if (scopeType == MsgConstants.SCOPE_ROLE && CollectionUtils.isEmpty(dto.getRoleIds())) {
            throw new BusinessException("指定角色不能为空");
        }
    }

    private List<Long> resolveTargetUserIds(MsgAnnounceCreateDto dto) {
        if (dto.getScopeType() == MsgConstants.SCOPE_USER) {
            return filterEnabledUsers(new LinkedHashSet<>(dto.getUserIds()));
        }
        if (dto.getScopeType() == MsgConstants.SCOPE_ROLE) {
            return filterEnabledUsers(loadUserIdsByRoles(dto.getRoleIds()));
        }
        return filterEnabledUsers(loadAllEnabledUserIds());
    }

    private List<Long> resolveTargetUserIdsFromEntity(MsgSysAnnounceEntity entity) {
        try {
            Map<String, Object> payload = objectMapper.readValue(
                    entity.getScopePayload() == null ? "{}" : entity.getScopePayload(),
                    new TypeReference<>() {});
            Integer scopeType = entity.getScopeType();
            if (scopeType != null && scopeType == MsgConstants.SCOPE_USER) {
                List<Long> userIds = readLongList(payload.get("userIds"));
                return filterEnabledUsers(new LinkedHashSet<>(userIds));
            }
            if (scopeType != null && scopeType == MsgConstants.SCOPE_ROLE) {
                List<Long> roleIds = readLongList(payload.get("roleIds"));
                return filterEnabledUsers(loadUserIdsByRoles(roleIds));
            }
            return filterEnabledUsers(loadAllEnabledUserIds());
        } catch (Exception ex) {
            throw new BusinessException("公告范围快照解析失败");
        }
    }

    private Set<Long> loadUserIdsByRoles(List<Long> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return Set.of();
        }
        List<UserRoleEntity> relations = userRoleMapper.selectList(new LambdaQueryWrapper<UserRoleEntity>()
                .in(UserRoleEntity::getRoleId, roleIds));
        Set<Long> userIds = new LinkedHashSet<>();
        for (UserRoleEntity relation : relations) {
            if (relation.getUserId() != null) {
                userIds.add(relation.getUserId());
            }
        }
        return userIds;
    }

    private Set<Long> loadAllEnabledUserIds() {
        List<UserEntity> users = userMapper.selectList(new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getStatus, 1)
                .select(UserEntity::getId));
        Set<Long> ids = new LinkedHashSet<>();
        for (UserEntity user : users) {
            ids.add(user.getId());
        }
        return ids;
    }

    private List<Long> filterEnabledUsers(Set<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return List.of();
        }
        List<UserEntity> users = userMapper.selectList(new LambdaQueryWrapper<UserEntity>()
                .in(UserEntity::getId, userIds)
                .eq(UserEntity::getStatus, 1)
                .select(UserEntity::getId));
        Set<Long> enabled = users.stream().map(UserEntity::getId).collect(Collectors.toCollection(LinkedHashSet::new));
        List<Long> ordered = new ArrayList<>();
        for (Long userId : userIds) {
            if (enabled.contains(userId)) {
                ordered.add(userId);
            }
        }
        return ordered;
    }

    private String writeScopePayload(MsgAnnounceCreateDto dto) {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("scopeType", dto.getScopeType());
            if (!CollectionUtils.isEmpty(dto.getUserIds())) {
                map.put("userIds", dto.getUserIds());
            }
            if (!CollectionUtils.isEmpty(dto.getRoleIds())) {
                map.put("roleIds", dto.getRoleIds());
            }
            return objectMapper.writeValueAsString(map);
        } catch (Exception ex) {
            throw new BusinessException("范围快照序列化失败");
        }
    }

    private String writeCursor(int offset) {
        try {
            return objectMapper.writeValueAsString(Map.of("offset", offset));
        } catch (Exception ex) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private List<Long> readLongList(Object value) {
        if (!(value instanceof List<?> list) || list.isEmpty()) {
            return List.of();
        }
        List<Long> result = new ArrayList<>();
        for (Object item : list) {
            if (item instanceof Number number) {
                result.add(number.longValue());
            } else if (item != null) {
                result.add(Long.parseLong(String.valueOf(item)));
            }
        }
        return result;
    }

    private MsgSysAnnounceEntity requireAnnounce(Long id) {
        MsgSysAnnounceEntity entity = msgSysAnnounceMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "公告不存在");
        }
        return entity;
    }

    private MsgAnnounceVo toListVo(MsgSysAnnounceEntity entity) {
        MsgAnnounceVo vo = new MsgAnnounceVo();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    private MsgAnnounceVo toDetailVo(MsgSysAnnounceEntity entity) {
        return toListVo(entity);
    }

    private void fillReadStats(MsgAnnounceVo vo) {
        Long readCount = msgSysInboxMapper.selectCount(new LambdaQueryWrapper<MsgSysInboxEntity>()
                .eq(MsgSysInboxEntity::getAnnounceId, vo.getId())
                .eq(MsgSysInboxEntity::getReadFlag, 1));
        Long unreadCount = msgSysInboxMapper.selectCount(new LambdaQueryWrapper<MsgSysInboxEntity>()
                .eq(MsgSysInboxEntity::getAnnounceId, vo.getId())
                .eq(MsgSysInboxEntity::getReadFlag, 0));
        vo.setReadCount(readCount == null ? 0 : readCount.intValue());
        vo.setUnreadCount(unreadCount == null ? 0 : unreadCount.intValue());
    }

    private void fillCreatorNames(List<MsgAnnounceVo> records) {
        Set<Long> ids = records.stream().map(MsgAnnounceVo::getCreateBy).filter(id -> id != null).collect(Collectors.toSet());
        if (ids.isEmpty()) {
            return;
        }
        Map<Long, UserEntity> userMap = userMapper.selectBatchIds(ids).stream()
                .collect(Collectors.toMap(UserEntity::getId, u -> u, (a, b) -> a));
        for (MsgAnnounceVo vo : records) {
            UserEntity user = userMap.get(vo.getCreateBy());
            if (user != null) {
                vo.setCreateByName(StringUtils.hasText(user.getNickname()) ? user.getNickname() : user.getUsername());
            }
        }
    }

    private void runAfterCommit(Runnable task) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    task.run();
                }
            });
        } else {
            task.run();
        }
    }

    private String truncate(String text, int max) {
        if (text == null) {
            return null;
        }
        return text.length() <= max ? text : text.substring(0, max);
    }
}
