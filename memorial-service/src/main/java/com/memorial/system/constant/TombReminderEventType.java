package com.memorial.system.constant;

import lombok.Getter;

/**
 * 墓碑提醒事件类型（与前端、JSON 存储一致）
 */
@Getter
public enum TombReminderEventType {
    DEATH_ANNIVERSARY("忌日"),
    QINGMING("清明节"),
    CHONGYANG("重阳节"),
    BIRTHDAY("生辰"),
    CUSTOM("自定义");

    private final String label;

    TombReminderEventType(String label) {
        this.label = label;
    }

    public static TombReminderEventType fromCode(String code) {
        if (code == null) {
            return null;
        }
        try {
            return TombReminderEventType.valueOf(code.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
