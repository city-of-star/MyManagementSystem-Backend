package com.mms.base.service.finance.job;

import com.mms.base.common.finance.entity.FinanceRecurringEntity;
import com.mms.base.service.finance.job.dto.FinanceRecurringRemindJobDto;
import com.mms.base.service.finance.mapper.FinanceRecurringMapper;
import com.mms.base.service.finance.support.FinanceRecurringScheduleSupport;
import com.mms.common.core.response.Response;
import com.mms.common.job.JobHandler;
import com.mms.common.job.annotation.JobDefinition;
import com.mms.common.job.enums.JobTypeEnum;
import com.mms.common.job.utils.JobParamUtils;
import com.mms.usercenter.common.message.constants.MsgConstants;
import com.mms.usercenter.common.message.dto.MsgBizNotifyDto;
import com.mms.usercenter.common.message.vo.MsgBizNotifyVo;
import com.mms.usercenter.feign.MsgBizNotifyFeign;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 实现功能【记账快捷模板到期提醒】
 * <p>
 * 扫描到期模板，按人聚合后投递系统通知（不自动记账）。
 * </p>
 *
 * @author li.hongyu
 * @date 2026-08-06
 */
@Slf4j
@Component
@JobDefinition(type = JobTypeEnum.FINANCE_RECURRING_REMIND, paramClass = FinanceRecurringRemindJobDto.class)
public class FinanceRecurringRemindJobHandler implements JobHandler {

    private static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");
    private static final DateTimeFormatter DAY_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final int NAME_PREVIEW_LIMIT = 5;

    @Resource
    private FinanceRecurringMapper financeRecurringMapper;

    @Resource
    private MsgBizNotifyFeign msgBizNotifyFeign;

    @Override
    public String execute(String paramsJson) {
        JobParamUtils.parseParams(paramsJson, FinanceRecurringRemindJobDto.class);
        LocalDateTime now = LocalDateTime.now(ZONE);
        String dayKey = now.toLocalDate().format(DAY_FMT);

        List<FinanceRecurringEntity> templates = financeRecurringMapper.listEnabledRemindTemplates();
        if (templates == null || templates.isEmpty()) {
            return "无启用中的周期提醒模板";
        }

        Map<Long, List<FinanceRecurringEntity>> dueByUser = new LinkedHashMap<>();
        for (FinanceRecurringEntity row : templates) {
            if (row.getUserId() == null) {
                continue;
            }
            if (!FinanceRecurringScheduleSupport.isDueForRemind(row, now)) {
                continue;
            }
            dueByUser.computeIfAbsent(row.getUserId(), k -> new ArrayList<>()).add(row);
        }

        if (dueByUser.isEmpty()) {
            return "当前时刻无到期模板，扫描=" + templates.size();
        }

        int created = 0;
        int skipped = 0;
        int failed = 0;
        for (Map.Entry<Long, List<FinanceRecurringEntity>> entry : dueByUser.entrySet()) {
            Long userId = entry.getKey();
            List<FinanceRecurringEntity> dueList = entry.getValue();
            try {
                MsgBizNotifyDto dto = buildNotify(userId, dayKey, dueList);
                Response<MsgBizNotifyVo> response = msgBizNotifyFeign.notify(dto);
                if (response == null || !Objects.equals(response.getCode(), Response.SUCCESS_CODE)) {
                    failed++;
                    log.warn("记账提醒投递失败，userId={}，message={}",
                            userId, response == null ? "null" : response.getMessage());
                    continue;
                }
                MsgBizNotifyVo data = response.getData();
                if (data != null && Boolean.TRUE.equals(data.getCreated())) {
                    created++;
                } else {
                    skipped++;
                }
            } catch (Exception ex) {
                failed++;
                log.error("记账提醒投递异常，userId={}，原因：{}", userId, ex.getMessage(), ex);
            }
        }

        String summary = String.format(
                "扫描=%d，到期用户=%d，新建通知=%d，幂等跳过=%d，失败=%d",
                templates.size(), dueByUser.size(), created, skipped, failed);
        log.info("记账快捷模板提醒任务完成，{}", summary);
        return summary;
    }

    private MsgBizNotifyDto buildNotify(Long userId, String dayKey, List<FinanceRecurringEntity> dueList) {
        List<String> names = dueList.stream()
                .map(FinanceRecurringEntity::getName)
                .filter(StringUtils::hasText)
                .collect(Collectors.toList());
        int total = names.size();
        String preview = names.stream().limit(NAME_PREVIEW_LIMIT).collect(Collectors.joining("、"));
        if (total > NAME_PREVIEW_LIMIT) {
            preview = preview + "…";
        }

        MsgBizNotifyDto dto = new MsgBizNotifyDto();
        dto.setUserId(userId);
        dto.setBizType(MsgConstants.BIZ_TYPE_FINANCE_RECURRING_DUE);
        dto.setBizId(userId + ":" + dayKey);
        dto.setTitle("记账提醒：快捷模板到期");
        dto.setContentText(String.format(
                "今日有 %d 项快捷模板到期：%s。请到「个人记账 → 快捷模板」点「记一笔」落账。",
                total, preview));
        dto.setLinkPath("/finance/recurrings");
        return dto;
    }
}
