package com.mms.usercenter.service.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.core.exceptions.ServerException;
import com.mms.usercenter.common.auth.entity.PermissionEntity;
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
import com.mms.usercenter.common.message.vo.MsgLinkOptionVo;
import com.mms.usercenter.service.auth.mapper.PermissionMapper;
import com.mms.usercenter.service.auth.mapper.UserMapper;
import com.mms.usercenter.service.auth.mapper.UserRoleMapper;
import com.mms.usercenter.service.message.mapper.MsgSysAnnounceMapper;
import com.mms.usercenter.service.message.mapper.MsgSysInboxMapper;
import com.mms.usercenter.service.message.service.MsgAnnounceService;
import com.mms.usercenter.service.message.support.MsgUnreadSupport;
import com.mms.usercenter.service.message.utils.MsgHtmlSanitizeUtils;
import com.mms.usercenter.service.message.utils.MsgLinkPathUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 实现功能【系统公告服务实现类】
 * <p>
 * 提供系统公告管理的核心业务逻辑实现
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
    private PermissionMapper permissionMapper;

    @Resource
    private MsgUnreadSupport msgUnreadSupport;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private PlatformTransactionManager transactionManager;

    @Override
    public Page<MsgAnnounceVo> getAnnouncePage(MsgAnnouncePageQueryDto dto) {
        try {
            log.info("分页查询系统公告列表，参数：{}", dto);
            Page<MsgAnnounceVo> page = new Page<>(dto.getPageNum(), dto.getPageSize());
            return msgSysAnnounceMapper.getAnnouncePage(page, dto);
        } catch (Exception e) {
            log.error("分页查询系统公告列表失败：{}", e.getMessage(), e);
            throw new ServerException("查询系统公告列表失败", e);
        }
    }

    @Override
    public MsgAnnounceVo getAnnounceById(Long id) {
        try {
            log.info("根据ID查询系统公告，id：{}", id);
            if (id == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "公告ID不能为空");
            }
            return getAnnounceDetail(id);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("根据ID查询系统公告失败：{}", e.getMessage(), e);
            throw new ServerException("查询系统公告失败", e);
        }
    }

    @Override
    public List<MsgLinkOptionVo> listLinkOptions() {
        try {
            List<PermissionEntity> permissions = permissionMapper.selectList(new LambdaQueryWrapper<PermissionEntity>()
                    .in(PermissionEntity::getPermissionType, "catalog", "menu")
                    .eq(PermissionEntity::getStatus, 1)
                    .orderByAsc(PermissionEntity::getSortOrder)
                    .orderByAsc(PermissionEntity::getId));
            return buildLinkOptionTree(permissions);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询公告跳转页面选项失败：{}", e.getMessage(), e);
            throw new ServerException("查询跳转页面选项失败", e);
        }
    }

    @Override
    public MsgAnnounceVo createAnnounce(MsgAnnounceCreateDto dto) {
        try {
            log.info("创建系统公告，参数：{}", dto);
            // 校验参数
            validateScope(dto);
            // 净化 html
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
            String linkPath = MsgLinkPathUtils.normalizeOptional(dto.getLinkPath());
            // 将发送范围序列化为 JSON 快照
            String scopePayload = writeScopePayload(dto);
            Integer scopeType = dto.getScopeType();
            int totalTarget = targetUserIds.size();

            // 先提交发件记录，再同步扇出，避免脏读
            Long announceId = new TransactionTemplate(transactionManager).execute(status -> {
                MsgSysAnnounceEntity entity = new MsgSysAnnounceEntity();
                entity.setTitle(title);
                entity.setContentHtml(html);
                entity.setContentText(text);
                entity.setLinkPath(linkPath);
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

            MsgAnnounceVo vo = getAnnounceDetail(announceId);
            if (vo.getStatus() == null || vo.getStatus() != MsgConstants.ANNOUNCE_DONE) {
                throw new BusinessException(StringUtils.hasText(vo.getErrorMsg())
                        ? vo.getErrorMsg()
                        : "公告发送失败");
            }
            log.info("创建系统公告成功，announceId：{}", announceId);
            return vo;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建系统公告失败：{}", e.getMessage(), e);
            throw new ServerException("创建系统公告失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MsgAnnounceVo updateAnnounce(Long id, MsgAnnounceUpdateDto dto) {
        try {
            log.info("更新系统公告，id：{}，参数：{}", id, dto);
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
            String linkPath = MsgLinkPathUtils.normalizeOptional(dto.getLinkPath());
            entity.setTitle(title);
            entity.setContentHtml(html);
            entity.setContentText(text);
            entity.setLinkPath(linkPath);
            msgSysAnnounceMapper.updateById(entity);

            List<MsgSysInboxEntity> inboxes = msgSysInboxMapper.selectList(new LambdaQueryWrapper<MsgSysInboxEntity>()
                    .eq(MsgSysInboxEntity::getAnnounceId, id));
            List<Long> notifyUserIds = new ArrayList<>();
            for (MsgSysInboxEntity inbox : inboxes) {
                inbox.setTitle(title);
                inbox.setContentHtml(html);
                inbox.setContentText(text);
                inbox.setLinkPath(linkPath);
                // 修改正文后重新标未读，驱动铃铛提醒
                inbox.setReadFlag(0);
                inbox.setReadTime(null);
                msgSysInboxMapper.updateById(inbox);
                if (inbox.getUserId() != null) {
                    notifyUserIds.add(inbox.getUserId());
                }
            }
            msgUnreadSupport.pushUnreadBatch(notifyUserIds);
            log.info("更新系统公告成功，id：{}", id);
            return getAnnounceDetail(id);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新系统公告失败：{}", e.getMessage(), e);
            throw new ServerException("更新系统公告失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recallAnnounce(Long id) {
        try {
            log.info("撤回系统公告，id：{}", id);
            MsgSysAnnounceEntity entity = requireAnnounce(id);
            Integer status = entity.getStatus();
            if (status != null && status == MsgConstants.ANNOUNCE_RECALLED) {
                throw new BusinessException("公告已撤回");
            }
            softDeleteInboxByAnnounce(id);
            entity.setStatus(MsgConstants.ANNOUNCE_RECALLED);
            entity.setErrorMsg(null);
            msgSysAnnounceMapper.updateById(entity);
            log.info("撤回系统公告成功，id：{}", id);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("撤回系统公告失败：{}", e.getMessage(), e);
            throw new ServerException("撤回系统公告失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAnnounce(Long id) {
        try {
            log.info("删除系统公告，id：{}", id);
            requireAnnounce(id);
            softDeleteInboxByAnnounce(id);
            msgSysAnnounceMapper.deleteById(id);
            log.info("删除系统公告成功，id：{}", id);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除系统公告失败：{}", e.getMessage(), e);
            throw new ServerException("删除系统公告失败", e);
        }
    }

    @Override
    public Page<MsgAnnounceUserVo> pageReadUsers(Long announceId, MsgAnnounceUserPageQueryDto dto) {
        try {
            log.info("分页查询公告已读用户，announceId：{}，参数：{}", announceId, dto);
            requireAnnounce(announceId);
            return pageUsers(announceId, 1, dto);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("分页查询公告已读用户失败：{}", e.getMessage(), e);
            throw new ServerException("查询公告已读用户失败", e);
        }
    }

    @Override
    public Page<MsgAnnounceUserVo> pageUnreadUsers(Long announceId, MsgAnnounceUserPageQueryDto dto) {
        try {
            log.info("分页查询公告未读用户，announceId：{}，参数：{}", announceId, dto);
            requireAnnounce(announceId);
            return pageUsers(announceId, 0, dto);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("分页查询公告未读用户失败：{}", e.getMessage(), e);
            throw new ServerException("查询公告未读用户失败", e);
        }
    }

    // ==================== 私有工具方法 ====================

    /**
     * 按已读标记分页查询公告目标用户
     */
    private Page<MsgAnnounceUserVo> pageUsers(Long announceId, int readFlag, MsgAnnounceUserPageQueryDto dto) {
        Page<MsgAnnounceUserVo> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        return msgSysInboxMapper.pageAnnounceUsers(page, announceId, readFlag, dto);
    }

    /**
     * 将公告按批次扇出到目标用户收件箱，并回写成功/失败计数与终态
     */
    private void doFanout(Long announceId, List<Long> targetUserIds) {
        MsgSysAnnounceEntity entity = msgSysAnnounceMapper.selectById(announceId);
        if (entity == null) {
            return;
        }
        Integer status = entity.getStatus();
        if (status != null && status == MsgConstants.ANNOUNCE_RECALLED) {
            log.info("公告已撤回，跳过扇出，announceId：{}", announceId);
            return;
        }
        if (status == null || status != MsgConstants.ANNOUNCE_PENDING) {
            log.warn("公告状态非待发送，跳过扇出，announceId：{}，status：{}", announceId, status);
            return;
        }
        if (msgSysAnnounceMapper.casStatus(announceId,
                MsgConstants.ANNOUNCE_PENDING, MsgConstants.ANNOUNCE_RUNNING) <= 0) {
            log.info("公告扇出抢锁失败，跳过，announceId：{}", announceId);
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
                    log.info("公告扇出中止（已撤回或删除），announceId：{}", announceId);
                    return;
                }
                int end = Math.min(i + MsgConstants.FANOUT_BATCH_SIZE, targetUserIds.size());
                List<Long> batch = targetUserIds.subList(i, end);
                List<Long> notified = new ArrayList<>();
                for (Long userId : batch) {
                    if (isFanoutAborted(announceId)) {
                        log.info("公告扇出中止，announceId：{}", announceId);
                        return;
                    }
                    try {
                        if (insertInboxIfAbsent(entity, userId)) {
                            notified.add(userId);
                        }
                    } catch (Exception ex) {
                        fail++;
                        log.warn("公告扇出失败，announceId：{}，userId：{}，原因：{}", announceId, userId, ex.getMessage(), ex);
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
                log.info("公告扇出结束前已撤回/删除，不再回写完成态，announceId：{}", announceId);
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
            log.error("公告扇出异常，announceId：{}，原因：{}", announceId, ex.getMessage(), ex);
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

    /**
     * 判断扇出是否应中止（公告已删除或已撤回）
     */
    private boolean isFanoutAborted(Long announceId) {
        MsgSysAnnounceEntity current = msgSysAnnounceMapper.selectById(announceId);
        if (current == null) {
            return true;
        }
        Integer status = current.getStatus();
        return status != null && status == MsgConstants.ANNOUNCE_RECALLED;
    }

    /**
     * 为目标用户写入收件箱；已存在则跳过，已软删则恢复；返回 true 表示本次有效投递
     */
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
                        announce.getContentText(),
                        announce.getLinkPath());
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
        inbox.setLinkPath(announce.getLinkPath());
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
                            announce.getContentText(),
                            announce.getLinkPath());
                    return true;
                }
                return false;
            }
            throw ex;
        }
    }

    /**
     * 按公告软删全部收件箱记录，并推送未读数变更
     */
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

    /**
     * 统计公告下未删除的收件箱条数（成功投递数）
     */
    private int countActiveInbox(Long announceId) {
        Long count = msgSysInboxMapper.selectCount(new LambdaQueryWrapper<MsgSysInboxEntity>()
                .eq(MsgSysInboxEntity::getAnnounceId, announceId));
        return count == null ? 0 : count.intValue();
    }

    /**
     * 判断净化后的 HTML 是否含有效纯文本
     */
    private boolean hasMeaningfulContent(String html) {
        if (!StringUtils.hasText(html)) {
            return false;
        }
        return StringUtils.hasText(MsgHtmlSanitizeUtils.toPlainText(html, 0));
    }

    /**
     * 校验创建公告时的发送范围参数
     */
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

    /**
     * 根据创建 DTO 解析最终可投递的目标用户 ID 列表
     */
    private List<Long> resolveTargetUserIds(MsgAnnounceCreateDto dto) {
        if (dto.getScopeType() == MsgConstants.SCOPE_USER) {
            return filterEnabledUsers(new LinkedHashSet<>(dto.getUserIds()));
        }
        if (dto.getScopeType() == MsgConstants.SCOPE_ROLE) {
            return filterEnabledUsers(loadUserIdsByRoles(dto.getRoleIds()));
        }
        return filterEnabledUsers(loadAllEnabledUserIds());
    }

    /**
     * 按角色 ID 列表查出关联用户（去重，保序）
     */
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

    /**
     * 加载全部启用状态用户 ID
     */
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

    /**
     * 过滤出启用状态用户，并保持入参顺序
     */
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

    /**
     * 将发送范围序列化为 JSON 快照
     */
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
        } catch (Exception e) {
            throw new BusinessException("范围快照序列化失败");
        }
    }

    /**
     * 写入扇出游标（当前已处理偏移量）
     */
    private String writeCursor(int offset) {
        try {
            return objectMapper.writeValueAsString(Map.of("offset", offset));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 按 ID 加载公告实体，不存在则抛业务异常
     */
    private MsgSysAnnounceEntity requireAnnounce(Long id) {
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "公告ID不能为空");
        }
        MsgSysAnnounceEntity entity = msgSysAnnounceMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "公告不存在");
        }
        return entity;
    }

    /**
     * 查询公告详情 VO（含创建人、已读统计）
     */
    private MsgAnnounceVo getAnnounceDetail(Long id) {
        MsgAnnounceVo vo = msgSysAnnounceMapper.getAnnounceById(id);
        if (vo == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "公告不存在");
        }
        return vo;
    }

    /**
     * 将启用的目录/菜单编成树：仅有 path 的菜单可选；无可用菜单子树的目录剔除。
     */
    private List<MsgLinkOptionVo> buildLinkOptionTree(List<PermissionEntity> permissions) {
        if (CollectionUtils.isEmpty(permissions)) {
            return List.of();
        }
        Map<Long, PermissionEntity> byId = permissions.stream()
                .filter(p -> p.getId() != null)
                .collect(Collectors.toMap(PermissionEntity::getId, p -> p, (a, b) -> a));

        Set<Long> keepIds = new HashSet<>();
        for (PermissionEntity p : permissions) {
            if (!"menu".equals(p.getPermissionType()) || !StringUtils.hasText(p.getPath())) {
                continue;
            }
            Long cursor = p.getId();
            while (cursor != null && cursor > 0 && keepIds.add(cursor)) {
                PermissionEntity current = byId.get(cursor);
                if (current == null) {
                    break;
                }
                Long parentId = current.getParentId();
                cursor = (parentId == null || parentId <= 0) ? null : parentId;
            }
        }
        if (keepIds.isEmpty()) {
            return List.of();
        }

        Map<Long, MsgLinkOptionVo> nodeMap = new HashMap<>();
        for (PermissionEntity p : permissions) {
            if (!keepIds.contains(p.getId())) {
                continue;
            }
            MsgLinkOptionVo node = new MsgLinkOptionVo();
            node.setLabel(p.getPermissionName());
            node.setIcon(StringUtils.hasText(p.getIcon()) ? p.getIcon().trim() : null);
            boolean selectable = "menu".equals(p.getPermissionType()) && StringUtils.hasText(p.getPath());
            if (selectable) {
                node.setValue(p.getPath().trim());
                node.setDisabled(false);
            } else {
                node.setValue("c:" + p.getId());
                node.setDisabled(true);
            }
            nodeMap.put(p.getId(), node);
        }

        List<MsgLinkOptionVo> roots = new ArrayList<>();
        for (PermissionEntity p : permissions) {
            if (!keepIds.contains(p.getId())) {
                continue;
            }
            MsgLinkOptionVo node = nodeMap.get(p.getId());
            Long parentId = p.getParentId();
            if (parentId != null && parentId > 0 && nodeMap.containsKey(parentId)) {
                nodeMap.get(parentId).getChildren().add(node);
            } else {
                roots.add(node);
            }
        }
        return roots;
    }

    /**
     * 截断过长文本，避免错误信息超长落库
     */
    private String truncate(String text, int max) {
        if (text == null) {
            return null;
        }
        return text.length() <= max ? text : text.substring(0, max);
    }
}
