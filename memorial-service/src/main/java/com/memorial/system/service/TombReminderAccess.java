package com.memorial.system.service;

import com.memorial.system.entity.Tomb;
import org.springframework.stereotype.Component;

/**
 * 个人提醒：凡已登录用户均可为「可见的」墓碑配置自己的提醒；墓碑禁用（status=0）时不允许。
 */
@Component
public class TombReminderAccess {

    public boolean canManage(Long userId, Tomb tomb) {
        if (userId == null || tomb == null) {
            return false;
        }
        return tomb.getStatus() == null || tomb.getStatus() != 0;
    }
}
