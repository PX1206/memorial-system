package com.memorial.system.scheduled;

import com.memorial.system.service.TombReminderNotifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;

/**
 * 每日早晨扫描墓碑提醒并发送短信（微信渠道在业务层预留）
 */
@Component
@Slf4j
public class TombReminderScheduleJob {

    private static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");

    @Autowired
    private TombReminderNotifyService tombReminderNotifyService;

    //@Scheduled(cron = "0 0 8 * * ?")
    @Scheduled(initialDelay = 30000, fixedRate = Long.MAX_VALUE) // 测试用
    @Async("optimizeTaskExecutor")
    public void sendTombReminders() {
        log.info("墓碑提醒定时任务开始");
        try {
            tombReminderNotifyService.runForDate(LocalDate.now(ZONE));
        } catch (Exception e) {
            log.error("墓碑提醒定时任务异常", e);
        }
    }
}
