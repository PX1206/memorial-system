package com.memorial.system.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.memorial.common.base.BaseServiceImpl;
import com.memorial.common.exception.BusinessException;
import com.memorial.common.tool.LoginUtil;
import com.memorial.system.constant.TombReminderEventType;
import com.memorial.system.entity.Tomb;
import com.memorial.system.entity.TombReminder;
import com.memorial.system.mapper.TombMapper;
import com.memorial.system.mapper.TombReminderMapper;
import com.memorial.system.param.TombReminderParam;
import com.memorial.system.param.TombReminderToggleParam;
import com.memorial.system.service.TombReminderAccess;
import com.memorial.system.service.TombReminderService;
import com.memorial.system.vo.TombReminderVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

@Service
public class TombReminderServiceImpl extends BaseServiceImpl<TombReminderMapper, TombReminder> implements TombReminderService {

    @Autowired
    private TombMapper tombMapper;

    @Autowired
    private TombReminderAccess tombReminderAccess;

    @Override
    public TombReminderVO getByTombId(Long tombId) throws Exception {
        if (tombId == null) {
            throw new BusinessException(500, "墓碑ID不能为空");
        }
        Tomb tomb = tombMapper.selectById(tombId);
        if (tomb == null) {
            throw new BusinessException(500, "墓碑不存在");
        }
        Long userId = LoginUtil.getUserId();
        if (!tombReminderAccess.canManage(userId, tomb)) {
            throw new BusinessException(403, "该墓碑已禁用，无法设置提醒");
        }
        TombReminder one = getBaseMapper().selectOne(Wrappers.<TombReminder>lambdaQuery()
                .eq(TombReminder::getUserId, userId)
                .eq(TombReminder::getTombId, tombId));
        if (one == null) {
            return null;
        }
        return toVo(one);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdate(TombReminderParam param) throws Exception {
        validate(param);
        Tomb tomb = tombMapper.selectById(param.getTombId());
        if (tomb == null) {
            throw new BusinessException(500, "墓碑不存在");
        }
        Long userId = LoginUtil.getUserId();
        if (!tombReminderAccess.canManage(userId, tomb)) {
            throw new BusinessException(403, "该墓碑已禁用，无法设置提醒");
        }

        List<Integer> merged = mergeOffsets(
                param.getAdvanceOffsets() == null ? new ArrayList<>() : param.getAdvanceOffsets(),
                param.getCustomAdvanceDays());
        if (merged.isEmpty()) {
            throw new BusinessException(500, "请至少选择一种提前提醒方式");
        }

        TombReminder entity = new TombReminder();
        entity.setUserId(userId);
        entity.setTombId(param.getTombId());
        entity.setRelationship(param.getRelationship().trim());
        entity.setEnabled(param.getEnabled() == null || param.getEnabled());
        entity.setChannelSms(param.getChannelSms() == null || param.getChannelSms());
        entity.setChannelWechat(Boolean.TRUE.equals(param.getChannelWechat()));
        entity.setEventTypes(JSON.toJSONString(param.getEventTypes()));
        entity.setCustomDates(param.getCustomDates() == null || param.getCustomDates().isEmpty()
                ? null : JSON.toJSONString(param.getCustomDates()));
        entity.setCustomDateRemarks(normalizeCustomRemarksJson(param.getCustomDates(), param.getCustomDateRemarks()));
        entity.setAdvanceOffsets(JSON.toJSONString(merged));

        TombReminder exist = getBaseMapper().selectOne(Wrappers.<TombReminder>lambdaQuery()
                .eq(TombReminder::getUserId, userId)
                .eq(TombReminder::getTombId, param.getTombId()));

        Date now = new Date();
        if (exist == null) {
            entity.setCreateTime(now);
            entity.setCreateBy(userId);
            entity.setUpdateTime(now);
            entity.setUpdateBy(userId);
            return save(entity);
        }
        if (param.getId() != null && !Objects.equals(param.getId(), exist.getId())) {
            throw new BusinessException(500, "数据不一致");
        }
        entity.setId(exist.getId());
        entity.setUpdateTime(now);
        entity.setUpdateBy(userId);
        return updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean toggle(TombReminderToggleParam param) throws Exception {
        TombReminder one = getById(param.getId());
        if (one == null) {
            throw new BusinessException(500, "提醒不存在");
        }
        Long userId = LoginUtil.getUserId();
        if (!Objects.equals(one.getUserId(), userId)) {
            throw new BusinessException(403, "无权操作");
        }
        Tomb tomb = tombMapper.selectById(one.getTombId());
        if (tomb != null && !tombReminderAccess.canManage(userId, tomb)) {
            throw new BusinessException(403, "该墓碑已禁用，无法设置提醒");
        }
        one.setEnabled(param.getEnabled());
        one.setUpdateTime(new Date());
        one.setUpdateBy(userId);
        return updateById(one);
    }

    private void validate(TombReminderParam param) {
        if (param.getEventTypes() == null || param.getEventTypes().isEmpty()) {
            throw new BusinessException(500, "请至少选择一种提醒日期类型");
        }
        for (String code : param.getEventTypes()) {
            if (TombReminderEventType.fromCode(code) == null) {
                throw new BusinessException(500, "无效的日期类型：" + code);
            }
        }
        if (param.getEventTypes().contains(TombReminderEventType.CUSTOM.name())) {
            if (param.getCustomDates() == null || param.getCustomDates().isEmpty()) {
                throw new BusinessException(500, "选择自定义日期时请填写日期");
            }
            if (param.getCustomDates().size() > 3) {
                throw new BusinessException(500, "自定义日期最多3个");
            }
            for (String s : param.getCustomDates()) {
                if (StringUtils.isBlank(s) || !parseCustomDate(s.trim())) {
                    throw new BusinessException(500, "自定义日期格式不正确，请使用 MM-dd 或 yyyy-MM-dd");
                }
            }
            validateCustomRemarks(param.getCustomDates().size(), param.getCustomDateRemarks());
        }
    }

    private void validateCustomRemarks(int dateCount, List<String> remarks) {
        if (remarks == null || remarks.isEmpty()) {
            return;
        }
        if (remarks.size() > 3) {
            throw new BusinessException(500, "自定义备注最多3条");
        }
        for (String r : remarks) {
            if (r != null && r.length() > 32) {
                throw new BusinessException(500, "单条自定义备注不超过32字");
            }
        }
        if (remarks.size() > dateCount) {
            throw new BusinessException(500, "备注条数不能超过已填自定义日期条数");
        }
    }

    /** 与 custom_dates 等长对齐，缺省补空串，仅在有备注时存 JSON */
    private String normalizeCustomRemarksJson(List<String> dates, List<String> remarks) {
        if (dates == null || dates.isEmpty()) {
            return null;
        }
        int n = dates.size();
        List<String> out = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            String r = remarks != null && i < remarks.size() && remarks.get(i) != null
                    ? remarks.get(i).trim() : "";
            out.add(r);
        }
        boolean any = out.stream().anyMatch(s -> s != null && !s.isEmpty());
        return any ? JSON.toJSONString(out) : null;
    }

    /** 解析是否可识别（不要求必须是未来日期） */
    private boolean parseCustomDate(String s) {
        if (s.matches("\\d{4}-\\d{2}-\\d{2}")) {
            try {
                new SimpleDateFormat("yyyy-MM-dd").parse(s);
                return true;
            } catch (ParseException e) {
                return false;
            }
        }
        if (s.matches("\\d{2}-\\d{2}")) {
            try {
                new SimpleDateFormat("MM-dd").parse(s);
                return true;
            } catch (ParseException e) {
                return false;
            }
        }
        return false;
    }

    private List<Integer> mergeOffsets(List<Integer> base, List<Integer> custom) {
        LinkedHashSet<Integer> set = new LinkedHashSet<>();
        if (base != null) {
            for (Integer i : base) {
                if (i != null && i >= 0 && i <= 366) {
                    set.add(i);
                }
            }
        }
        if (custom != null) {
            for (Integer i : custom) {
                if (i != null && i >= 0 && i <= 366) {
                    set.add(i);
                }
            }
        }
        return new ArrayList<>(set);
    }

    private TombReminderVO toVo(TombReminder e) {
        TombReminderVO vo = new TombReminderVO();
        vo.setId(e.getId());
        vo.setTombId(e.getTombId());
        vo.setRelationship(e.getRelationship());
        vo.setEnabled(e.getEnabled());
        vo.setChannelSms(e.getChannelSms());
        vo.setChannelWechat(e.getChannelWechat());
        if (StringUtils.isNotBlank(e.getEventTypes())) {
            vo.setEventTypes(JSON.parseArray(e.getEventTypes(), String.class));
        }
        if (StringUtils.isNotBlank(e.getCustomDates())) {
            vo.setCustomDates(JSON.parseArray(e.getCustomDates(), String.class));
        }
        if (StringUtils.isNotBlank(e.getCustomDateRemarks())) {
            vo.setCustomDateRemarks(JSON.parseArray(e.getCustomDateRemarks(), String.class));
        }
        if (StringUtils.isNotBlank(e.getAdvanceOffsets())) {
            vo.setAdvanceOffsets(JSON.parseArray(e.getAdvanceOffsets(), Integer.class));
        }
        return vo;
    }
}
