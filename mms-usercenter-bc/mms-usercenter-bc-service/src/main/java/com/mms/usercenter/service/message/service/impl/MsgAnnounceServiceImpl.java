package com.mms.usercenter.service.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import com.mms.usercenter.common.message.vo.MsgAnnounceReadStatVo;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
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
    private PlatformTransactionManager transactionManager;

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
        fillReadStatsBatch(records);
        result.setRecords(records);
        return result;
    }

    @Override
    public MsgAnnounceVo getAnnounceById(Long id) {
        MsgSysAnnounceEntity entity = requireAnnounce(id);
        MsgAnnounceVo vo = toDetailVo(entity);
        fillCreatorNames(List.of(vo));
        fillReadStatsBatch(List.of(vo));
        return vo;
    }

    @Override
    public MsgAnnounceVo createAnnounce(MsgAnnounceCreateDto dto) {
        validateScope(dto);
        String html = MsgHtmlSanitizeUtils.sanitize(dto.getContentHtml());
        if (!hasMeaningfulContent(html)) {
            throw new BusinessException("公告内容不能为空");
        }
        List<Long> targetUserIds = resolveTargetUserIds(dto);
        if (targetUserIds.isEmpty()) {
            throw new BusinessException("发送范围内没有可用用户");
        }

        String title = dto.getTitle().trim();
        String text = MsgHtmlSanitizeUtils.toPlainText(html, 500);
        String scopePayload = writeScopePayload(dto);
        Integer scopeType = dto.getScopeType();
        int totalTarget = targetUserIds.size();

        Long announceId = new TransactionTemplate(transactionManager).execute(status -> {
            MsgSysAnnounceEntity entity = new MsgSysAnnounceEntity();
            entity.setTitle(title);
            entity.setContentHtml(html);
            entity.setContentText(text);
            entity.setScopeType(scopeType);
            entity.setScopePayload(scopePayload);
            entity.setStatus(MsgConstants.ANNOUNCE_PENDING);
            entity.setTotalTarget(totalTarget);
            entity.setSuccessCount(0);
            entity.setFailCount(0);
            entity.setDeleted(0);
            msgSysAnnounceMapper.insert(entity);
            return entity.getId();
        });
        if (announceId == null) {
            throw new BusinessException("公告创建失败");
        }

        doFanout(announceId, targetUserIds);

        MsgAnnounceVo vo = getAnnounceById(announceId);
        if (vo.getStatus() == null || vo.getStatus() != MsgConstants.ANNOUNCE_DONE) {
            throw new BusinessException(StringUtils.hasText(vo.getErrorMsg())
                    ? vo.getErrorMsg()
                    : "公告发送失败");
        }
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MsgAnnounceVo updateAnnounce(Long id, MsgAnnounceUpdateDto dto) {
        MsgSysAnnounceEntity entity = requireAnnounce(id);
        Integer status = entity.getStatus();
        if (status == null || (status != MsgConstants.ANNOUNCE_DONE && status != MsgConstants.ANNOUNCE_FAILED)) {
            throw new BusinessException("仅已发送或失败的公告可修改");
        }
        String html = MsgHtmlSanitizeUtils.sanitize(dto.getContentHtml());
        if (!hasMeaningfulContent(html)) {
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
            inbox.setReadFlag(0);
            inbox.setReadTime(null);
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
        requireAnnounce(id);
        softDeleteInboxByAnnounce(id);
        msgSysAnnounceMapper.deleteById(id);
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
        Integer status = entity.getStatus();
        if (status != null && status == MsgConstants.ANNOUNCE_RECALLED) {
            return;
        }
        if (status == null || status != MsgConstants.ANNOUNCE_PENDING) {
            return;
        }
        if (msgSysAnnounceMapper.casStatus(announceId,
                MsgConstants.ANNOUNCE_PENDING, MsgConstants.ANNOUNCE_RUNNING) <= 0) {
            return;
        }
        entity = msgSysAnnounceMapper.selectById(announceId);
        if (entity == null) {
            return;
        }

        int fail = 0;
        try {
            for (int i = 0; i < targetUserIds.size(); i += MsgConstants.FANOUT_BATCH_SIZE) {
                if (isFanoutAborted(announceId)) {
                    return;
                }
                int end = Math.min(i + MsgConstants.FANOUT_BATCH_SIZE, targetUserIds.size());
                List<Long> batch = targetUserIds.subList(i, end);
                List<Long> notified = new ArrayList<>();
                for (Long userId : batch) {
                    if (isFanoutAborted(announceId)) {
                        return;
                    }
                    try {
                        if (insertInboxIfAbsent(entity, userId)) {
                            notified.add(userId);
                        }
                    } catch (Exception ex) {
                        fail++;
                        log.warn("公告扇出失败 announceId={} userId={}", announceId, userId, ex);
                    }
                }
                int success = countActiveInbox(announceId);
                entity.setSuccessCount(success);
                entity.setFailCount(fail);
                entity.setCursorJson(writeCursor(end));
                entity.setStatus(MsgConstants.ANNOUNCE_RUNNING);
                msgSysAnnounceMapper.updateById(entity);
                msgUnreadSupport.pushUnreadBatch(notified);
            }
            if (isFanoutAborted(announceId)) {
                return;
            }
            MsgSysAnnounceEntity current = msgSysAnnounceMapper.selectById(announceId);
            if (current == null || (current.getStatus() != null
                    && current.getStatus() != MsgConstants.ANNOUNCE_RUNNING)) {
                return;
            }
            int success = countActiveInbox(announceId);
            entity.setSuccessCount(success);
            entity.setFailCount(fail);
            entity.setStatus(fail > 0 ? MsgConstants.ANNOUNCE_FAILED : MsgConstants.ANNOUNCE_DONE);
            if (fail > 0) {
                entity.setErrorMsg(success == 0
                        ? "全部目标用户发送失败"
                        : String.format("部分发送失败：成功 %d，失败 %d", success, fail));
            } else {
                entity.setErrorMsg(null);
            }
            msgSysAnnounceMapper.updateById(entity);
        } catch (Exception ex) {
            log.error("公告扇出异常 announceId={}", announceId, ex);
            if (isFanoutAborted(announceId)) {
                return;
            }
            MsgSysAnnounceEntity current = msgSysAnnounceMapper.selectById(announceId);
            if (current == null || (current.getStatus() != null
                    && current.getStatus() != MsgConstants.ANNOUNCE_RUNNING)) {
                return;
            }
            entity.setStatus(MsgConstants.ANNOUNCE_FAILED);
            entity.setErrorMsg(ex.getMessage() == null ? "发送失败" : truncate(ex.getMessage(), 500));
            entity.setSuccessCount(countActiveInbox(announceId));
            entity.setFailCount(fail);
            msgSysAnnounceMapper.updateById(entity);
        }
    }

    private boolean isFanoutAborted(Long announceId) {
        MsgSysAnnounceEntity current = msgSysAnnounceMapper.selectById(announceId);
        if (current == null) {
            return true;
        }
        Integer status = current.getStatus();
        return status != null && status == MsgConstants.ANNOUNCE_RECALLED;
    }

    private boolean insertInboxIfAbsent(MsgSysAnnounceEntity announce, Long userId) {
        if (isFanoutAborted(announce.getId())) {
            return false;
        }
        MsgSysInboxEntity existing = msgSysInboxMapper.selectByAnnounceUserIncludeDeleted(announce.getId(), userId);
        if (existing != null) {
            if (existing.getDeleted() != null && existing.getDeleted() == 1) {
                msgSysInboxMapper.restoreDeletedInbox(
                        existing.getId(),
                        announce.getTitle(),
                        announce.getContentHtml(),
                        announce.getContentText());
                return true;
            }
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
            MsgSysInboxEntity again = msgSysInboxMapper.selectByAnnounceUserIncludeDeleted(announce.getId(), userId);
            if (again != null) {
                if (again.getDeleted() != null && again.getDeleted() == 1) {
                    msgSysInboxMapper.restoreDeletedInbox(
                            again.getId(),
                            announce.getTitle(),
                            announce.getContentHtml(),
                            announce.getContentText());
                    return true;
                }
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

    private int countActiveInbox(Long announceId) {
        Long count = msgSysInboxMapper.selectCount(new LambdaQueryWrapper<MsgSysInboxEntity>()
                .eq(MsgSysInboxEntity::getAnnounceId, announceId));
        return count == null ? 0 : count.intValue();
    }

    private boolean hasMeaningfulContent(String html) {
        if (!StringUtils.hasText(html)) {
            return false;
        }
        return StringUtils.hasText(MsgHtmlSanitizeUtils.toPlainText(html, 0));
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

    private void fillReadStatsBatch(List<MsgAnnounceVo> records) {
        if (records == null || records.isEmpty()) {
            return;
        }
        List<Long> ids = records.stream().map(MsgAnnounceVo::getId).filter(id -> id != null).toList();
        if (ids.isEmpty()) {
            return;
        }
        Map<Long, MsgAnnounceReadStatVo> statMap = msgSysInboxMapper.countReadStatsByAnnounceIds(ids).stream()
                .collect(Collectors.toMap(MsgAnnounceReadStatVo::getAnnounceId, s -> s, (a, b) -> a));
        for (MsgAnnounceVo vo : records) {
            MsgAnnounceReadStatVo stat = statMap.get(vo.getId());
            if (stat == null) {
                vo.setReadCount(0);
                vo.setUnreadCount(0);
            } else {
                vo.setReadCount(stat.getReadCount() == null ? 0 : stat.getReadCount());
                vo.setUnreadCount(stat.getUnreadCount() == null ? 0 : stat.getUnreadCount());
            }
        }
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

    private String truncate(String text, int max) {
        if (text == null) {
            return null;
        }
        return text.length() <= max ? text : text.substring(0, max);
    }
}
