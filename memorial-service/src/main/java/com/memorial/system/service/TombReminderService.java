package com.memorial.system.service;

import com.memorial.system.param.TombReminderParam;
import com.memorial.system.param.TombReminderToggleParam;
import com.memorial.system.vo.TombReminderVO;

public interface TombReminderService {

    TombReminderVO getByTombId(Long tombId) throws Exception;

    boolean saveOrUpdate(TombReminderParam param) throws Exception;

    boolean toggle(TombReminderToggleParam param) throws Exception;
}
