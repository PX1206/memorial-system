package com.memorial.system.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.memorial.common.tool.AliyunSmsUtils;
import com.memorial.system.constant.TombReminderEventType;
import com.memorial.system.entity.Tomb;
import com.memorial.system.entity.TombReminder;
import com.memorial.system.entity.TombReminderSendLog;
import com.memorial.system.entity.User;
import com.memorial.system.mapper.TombMapper;
import com.memorial.system.mapper.TombReminderMapper;
import com.memorial.system.mapper.TombReminderSendLogMapper;
import com.memorial.system.mapper.UserMapper;
import com.memorial.system.service.TombReminderNotifyService;
import com.memorial.system.util.TombReminderLunarUtil;
import com.memorial.system.util.TombReminderSolarUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Service
public class TombReminderNotifyServiceImpl implements TombReminderNotifyService {

    private static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");

    @Autowired
    private TombReminderMapper tombReminderMapper;

    @Autowired
    private TombMapper tombMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TombReminderSendLogMapper sendLogMapper;

    @Autowired
    private AliyunSmsUtils aliyunSmsUtils;

    @Value("${aliyun.sms.reminder.sign-name:}")
    private String reminderSignName;

    @Value("${aliyun.sms.reminder.template-code:}")
    private String reminderTemplateCode;

    @Value("${aliyun.sms.reminder.template-param:content}")
    private String reminderTemplateParam;

    @Override
    public void runForDate(LocalDate today) {
        if (today == null) {
            today = LocalDate.now(ZONE);
        }
        List<TombReminder> list = tombReminderMapper.selectList(Wrappers.<TombReminder>lambdaQuery()
                .eq(TombReminder::getEnabled, true)
                .eq(TombReminder::getChannelSms, true));
        for (TombReminder r : list) {
            try {
                processOne(r, today);
            } catch (Exception e) {
                log.error("墓碑提醒处理失败 reminderId={}", r.getId(), e);
            }
        }
    }

    private void processOne(TombReminder r, LocalDate today) {
        User user = userMapper.selectById(r.getUserId());
        if (user == null || StringUtils.isBlank(user.getMobile())) {
            return;
        }
        Tomb tomb = tombMapper.selectById(r.getTombId());
        if (tomb == null || (tomb.getStatus() != null && tomb.getStatus() == 0)) {
            return;
        }
        List<String> typeCodes = JSON.parseArray(r.getEventTypes(), String.class);
        List<Integer> offsets = JSON.parseArray(r.getAdvanceOffsets(), Integer.class);
        List<String> customs = r.getCustomDates() == null ? null : JSON.parseArray(r.getCustomDates(), String.class);
        List<String> customRemarks = r.getCustomDateRemarks() == null ? null : JSON.parseArray(r.getCustomDateRemarks(), String.class);
        if (typeCodes == null || typeCodes.isEmpty() || offsets == null || offsets.isEmpty()) {
            return;
        }
        int year = today.getYear();
        for (String code : typeCodes) {
            TombReminderEventType t = TombReminderEventType.fromCode(code);
            if (t == null) {
                continue;
            }
            if (t == TombReminderEventType.CUSTOM) {
                if (customs == null) {
                    continue;
                }
                int idx = 0;
                for (String c : customs) {
                    LocalDate eventDate = resolveCustomAnnual(c, year, tomb);
                    if (eventDate != null) {
                        String remark = (customRemarks != null && idx < customRemarks.size())
                                ? customRemarks.get(idx) : null;
                        fireOffsets(r, tomb, user, today, t, eventDate, year, "C" + idx, remark);
                    }
                    idx++;
                }
                continue;
            }
            LocalDate eventDate = resolveBuiltin(t, year, tomb, r);
            if (eventDate == null) {
                continue;
            }
            fireOffsets(r, tomb, user, today, t, eventDate, year, "0", null);
        }
        if (Boolean.TRUE.equals(r.getChannelWechat())) {
            log.debug("微信渠道暂未开通，reminderId={}", r.getId());
        }
    }

    private void fireOffsets(TombReminder r, Tomb tomb, User user, LocalDate today,
                             TombReminderEventType t, LocalDate eventDate, int year, String subKey,
                             String customRemark) {
        List<Integer> offsets = JSON.parseArray(r.getAdvanceOffsets(), Integer.class);
        for (Integer offset : offsets) {
            if (offset == null) {
                continue;
            }
            LocalDate sendDate = eventDate.minusDays(offset);
            if (!sendDate.equals(today)) {
                continue;
            }
            String bizKey = t.name() + "_" + year + "_" + offset + "_" + subKey;
            if (existsLog(r.getId(), bizKey)) {
                continue;
            }
            String title = buildEventTitle(tomb, t, customRemark);
            String content = buildSmsText(r.getRelationship(), tomb.getName(), title, eventDate, offset);
            boolean sent = sendSms(user.getMobile(), content);
            if (sent) {
                insertLog(r.getId(), bizKey, today);
            }
        }
    }

    private boolean existsLog(Long reminderId, String bizKey) {
        Integer c = sendLogMapper.selectCount(Wrappers.<TombReminderSendLog>lambdaQuery()
                .eq(TombReminderSendLog::getReminderId, reminderId)
                .eq(TombReminderSendLog::getBizKey, bizKey));
        return c != null && c > 0;
    }

    private void insertLog(Long reminderId, String bizKey, LocalDate today) {
        TombReminderSendLog logEntity = new TombReminderSendLog();
        logEntity.setReminderId(reminderId);
        logEntity.setBizKey(bizKey);
        logEntity.setSendDate(Date.valueOf(today));
        logEntity.setChannel("sms");
        sendLogMapper.insert(logEntity);
    }

    private boolean sendSms(String mobile, String text) {
        if (StringUtils.isBlank(reminderTemplateCode) || StringUtils.isBlank(reminderSignName)) {
            log.warn("未配置 aliyun.sms.reminder.template-code / sign-name，跳过短信发送，内容：{}", text);
            return false;
        }
        JSONObject json = new JSONObject();
        json.put(reminderTemplateParam, text);
        return aliyunSmsUtils.sendSms(mobile, json.toJSONString(), reminderSignName, reminderTemplateCode);
    }

    private String buildSmsText(String relationship, String tombName, String eventLabel, LocalDate eventDate, int offsetDays) {
        String name = StringUtils.isBlank(tombName) ? "逝者" : tombName;
        String rel = StringUtils.isBlank(relationship) ? "亲人" : relationship;
        String dateStr = eventDate.getYear() + "年" + eventDate.getMonthValue() + "月" + eventDate.getDayOfMonth() + "日";
        if (offsetDays == 0) {
            return "【念园】提醒：您（" + rel + "）的 " + name + "，" + eventLabel + "为今日（" + dateStr + "），请记得追思祭扫。";
        }
        return "【念园】提醒：您（" + rel + "）的 " + name + "，" + eventLabel + "为 " + dateStr + "，本日为提前 " + offsetDays + " 天提醒，请妥善安排祭扫。";
    }

    /**
     * 短信标题是否标注「农历」：墓碑为农历语义时，忌日、生辰、自定义均按农历推算；
     * 清明、重阳仍按系统固定规则（公历/节气），不标农历。
     */
    private boolean useLunar(Tomb tomb, TombReminderEventType t) {
        if (tomb == null || !Boolean.TRUE.equals(tomb.getLunarFlag())) {
            return false;
        }
        return t == TombReminderEventType.DEATH_ANNIVERSARY
                || t == TombReminderEventType.BIRTHDAY
                || t == TombReminderEventType.CUSTOM;
    }

    private String buildEventTitle(Tomb tomb, TombReminderEventType t, String customRemark) {
        String title = t.getLabel();
        if (t == TombReminderEventType.CUSTOM && StringUtils.isNotBlank(customRemark)) {
            title = title + "（" + customRemark.trim() + "）";
        }
        if (useLunar(tomb, t)) {
            title = title + "（农历）";
        }
        return title;
    }

    private LocalDate resolveBuiltin(TombReminderEventType t, int year, Tomb tomb, TombReminder r) {
        switch (t) {
            case DEATH_ANNIVERSARY:
                if (useLunar(tomb, t)) {
                    return lunarAnniversarySolarInYear(tomb.getDeathday(), year);
                }
                return annualSolarFromTombDate(tomb.getDeathday(), year);
            case BIRTHDAY:
                if (useLunar(tomb, t)) {
                    return lunarAnniversarySolarInYear(tomb.getBirthday(), year);
                }
                return annualSolarFromTombDate(tomb.getBirthday(), year);
            case QINGMING:
                return TombReminderSolarUtil.qingming(year);
            case CHONGYANG:
                return TombReminderSolarUtil.chongyang(year);
            default:
                return null;
        }
    }

    /** 墓碑 DATE 为公历语义：取其中月日在指定公历年对应的公历日期。 */
    private LocalDate annualSolarFromTombDate(java.util.Date utilDate, int year) {
        LocalDate ld = localDateFromUtilDate(utilDate);
        if (ld == null) {
            return null;
        }
        return TombReminderLunarUtil.solarAnnualInGregorianYear(year, ld.getMonthValue(), ld.getDayOfMonth());
    }

    /**
     * 墓碑 DATE 为农历语义：月、日视为农历月日，求该公历年「第一次」出现该农历月日的公历日期（与「当天农历月日是否等于该月日」一致）。
     */
    private LocalDate lunarAnniversarySolarInYear(java.util.Date utilDate, int year) {
        LocalDate ld = localDateFromUtilDate(utilDate);
        if (ld == null) {
            return null;
        }
        return TombReminderLunarUtil.solarDateForLunarAnniversary(
                ld.getMonthValue(), ld.getDayOfMonth(), false, year, ZONE);
    }

    private static LocalDate localDateFromUtilDate(java.util.Date utilDate) {
        if (utilDate == null) {
            return null;
        }
        return new java.sql.Date(utilDate.getTime()).toLocalDate();
    }

    /**
     * 自定义每年提醒日：墓碑为农历语义时，字符串中的月、日按<strong>农历</strong>理解，再换成本年公历日；
     * 否则按公历月日理解。
     */
    private LocalDate resolveCustomAnnual(String raw, int year, Tomb tomb) {
        if (StringUtils.isBlank(raw)) {
            return null;
        }
        if (tomb != null && Boolean.TRUE.equals(tomb.getLunarFlag())) {
            return resolveCustomAnnualAsLunar(raw, year);
        }
        String s = raw.trim();
        try {
            if (s.matches("\\d{4}-\\d{2}-\\d{2}")) {
                LocalDate full = LocalDate.parse(s);
                return LocalDate.of(year, full.getMonthValue(), full.getDayOfMonth());
            }
            if (s.matches("\\d{2}-\\d{2}")) {
                String[] p = s.split("-");
                int m = Integer.parseInt(p[0]);
                int day = Integer.parseInt(p[1]);
                return LocalDate.of(year, m, day);
            }
        } catch (Exception e) {
            log.warn("解析自定义日期失败: {}", raw);
        }
        return null;
    }

    /** 自定义日期为农历月日（与墓碑 lunar_flag 一致），求该公历年对应的公历日期 */
    private LocalDate resolveCustomAnnualAsLunar(String raw, int year) {
        String s = raw.trim();
        try {
            if (s.matches("\\d{4}-\\d{2}-\\d{2}")) {
                LocalDate full = LocalDate.parse(s);
                return TombReminderLunarUtil.solarDateForLunarAnniversary(
                        full.getMonthValue(), full.getDayOfMonth(), false, year, ZONE);
            }
            if (s.matches("\\d{2}-\\d{2}")) {
                String[] p = s.split("-");
                int m = Integer.parseInt(p[0]);
                int day = Integer.parseInt(p[1]);
                return TombReminderLunarUtil.solarDateForLunarAnniversary(m, day, false, year, ZONE);
            }
        } catch (Exception e) {
            log.warn("解析农历自定义日期失败: {}", raw);
        }
        return null;
    }
}
