package com.memorial.system.service;

import java.time.LocalDate;

/**
 * 定时扫描并发送墓碑提醒（短信；微信预留）
 */
public interface TombReminderNotifyService {

    void runForDate(LocalDate today);
}
