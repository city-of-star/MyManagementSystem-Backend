package com.mms.base.service.finance.support;

import com.mms.base.common.finance.entity.FinanceRecurringEntity;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 实现功能【快捷模板周期/时刻到期判断】
 * <p>
 * 与前端 {@code recurringCycle.ts} 对齐；时区由调用方保证（建议 Asia/Shanghai）。
 * </p>
 *
 * @author li.hongyu
 * @date 2026-08-06
 */
public final class FinanceRecurringScheduleSupport {

    private FinanceRecurringScheduleSupport() {
    }

    /**
     * 当前时刻是否应对该模板发出提醒（周期对上且已达提醒时刻）。
     */
    public static boolean isDueForRemind(FinanceRecurringEntity row, LocalDateTime now) {
        if (row == null || !Integer.valueOf(1).equals(row.getEnabled())) {
            return false;
        }
        if (!isDueOnDate(row, now.toLocalDate())) {
            return false;
        }
        int slot = row.getRemindMinuteOfDay() != null ? row.getRemindMinuteOfDay() : 480;
        if (slot < 0 || slot > 1410) {
            slot = 480;
        }
        LocalTime remindAt = LocalTime.of(slot / 60, slot % 60);
        return !now.toLocalTime().isBefore(remindAt);
    }

    /**
     * 仅判断日历日是否命中周期（不含时刻）。
     */
    public static boolean isDueOnDate(FinanceRecurringEntity row, LocalDate date) {
        if (row == null) {
            return false;
        }
        String cycle = normalizeCycle(row.getCycle());
        if ("none".equals(cycle)) {
            return false;
        }
        if ("daily".equals(cycle)) {
            return true;
        }
        if ("weekly".equals(cycle)) {
            int iso = date.getDayOfWeek().getValue();
            return row.getWeekday() != null && row.getWeekday() == iso;
        }
        if ("monthly".equals(cycle)) {
            Integer dom = row.getDayOfMonth();
            if (dom == null || dom < 1) {
                return false;
            }
            int lastDay = date.lengthOfMonth();
            int target = Math.min(dom, lastDay);
            return date.getDayOfMonth() == target;
        }
        return false;
    }

    public static String normalizeCycle(String cycle) {
        if (!StringUtils.hasText(cycle)) {
            return "none";
        }
        String c = cycle.trim();
        if ("daily".equals(c) || "weekly".equals(c) || "monthly".equals(c)) {
            return c;
        }
        return "none";
    }
}
