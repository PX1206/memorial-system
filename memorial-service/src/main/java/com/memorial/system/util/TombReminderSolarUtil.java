package com.memorial.system.util;

import cn.hutool.core.date.ChineseDate;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * 清明节、重阳节等公历日期推算（清明按公历 4 月 5 日近似；重阳按农历九月初九对应公历）
 */
public final class TombReminderSolarUtil {

    private static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");

    private TombReminderSolarUtil() {
    }

    /** 清明：按当年公历 4 月 5 日（节气约在 4 月 4～6 日，此处统一为 5 日） */
    public static LocalDate qingming(int solarYear) {
        return LocalDate.of(solarYear, 4, 5);
    }

    /**
     * 重阳（农历九月初九）对应的公历日：在当年 9～11 月范围内逐日推算。
     */
    public static LocalDate chongyang(int solarYear) {
        LocalDate from = LocalDate.of(solarYear, 9, 1);
        LocalDate to = LocalDate.of(solarYear, 11, 30);
        for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
            if (isLunarNineNine(d)) {
                return d;
            }
        }
        return LocalDate.of(solarYear, 10, 1);
    }

    static boolean isLunarNineNine(LocalDate solar) {
        Date date = Date.from(solar.atStartOfDay(ZONE).toInstant());
        ChineseDate cd = new ChineseDate(date);
        return cd.getMonth() == 9 && cd.getDay() == 9;
    }
}
