package com.memorial.system.util;

import cn.hutool.core.date.ChineseDate;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Date;

/**
 * 农历周年：根据墓碑上保存的农历「月、日、是否闰月」推算目标公历年对应的公历日期。
 * <p>
 * 规则：按公历年从 1 月 1 日起顺序查找，取<strong>当年第一次</strong>满足该农历月日的公历日。
 * 若该农历月没有「某日」（如小月无三十），则取该月<strong>最后一天</strong>。
 */
public final class TombReminderLunarUtil {

    private TombReminderLunarUtil() {
    }

    /**
     * 公历「每年同一月日」：在指定公历年取该月该日，若不存在（如 2 月 29 日遇非闰年）则取该月最后一天。
     */
    public static LocalDate solarAnnualInGregorianYear(int gregorianYear, int month, int day) {
        if (month < 1 || month > 12 || day < 1) {
            return null;
        }
        YearMonth ym = YearMonth.of(gregorianYear, month);
        int max = ym.lengthOfMonth();
        int d = Math.min(day, max);
        return LocalDate.of(gregorianYear, month, d);
    }

    /**
     * 农历周年：用户填写的农历月、日、闰月标记，在目标公历年对应的公历日期。
     *
     * @param lunarMonth 农历月 1–12（不含闰月时为正；闰月由 leapMonth 表示）
     * @param lunarDay 农历日
     * @param leapMonth 是否为闰月（与 lunarMonth 组合为 Hutool 闰月语义）
     */
    public static LocalDate solarDateForLunarAnniversary(int lunarMonth, int lunarDay, boolean leapMonth, int targetGregorianYear, ZoneId zone) {
        if (zone == null || lunarDay <= 0 || lunarMonth < 1 || lunarMonth > 12) {
            return null;
        }
        int lunarMonthHutool = leapMonth ? -Math.abs(lunarMonth) : lunarMonth;

        LocalDate from = LocalDate.of(targetGregorianYear, 1, 1);
        LocalDate to = LocalDate.of(targetGregorianYear, 12, 31);

        int maxDayInMonth = 0;
        for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
            Date util = Date.from(d.atStartOfDay(zone).toInstant());
            ChineseDate c = new ChineseDate(util);
            if (c.getMonth() == lunarMonthHutool) {
                maxDayInMonth = Math.max(maxDayInMonth, c.getDay());
            }
        }
        if (maxDayInMonth <= 0) {
            return null;
        }
        int effectiveDay = Math.min(lunarDay, maxDayInMonth);

        for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
            Date util = Date.from(d.atStartOfDay(zone).toInstant());
            ChineseDate c = new ChineseDate(util);
            if (c.getMonth() == lunarMonthHutool && c.getDay() == effectiveDay) {
                return d;
            }
        }
        return null;
    }
}
