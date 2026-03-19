package com.memorial.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.memorial.common.base.BaseServiceImpl;
import com.memorial.common.exception.BusinessException;
import com.memorial.common.tool.LoginUtil;
import com.memorial.common.pagination.Paging;
import com.memorial.common.tool.IpUtil;
import com.memorial.system.entity.Tomb;
import com.memorial.system.entity.TombCheckin;
import com.memorial.system.entity.FamilyMember;
import com.memorial.system.mapper.FamilyMapper;
import com.memorial.system.mapper.FamilyMemberMapper;
import com.memorial.system.mapper.TombCheckinMapper;
import com.memorial.system.mapper.TombMapper;
import com.memorial.system.param.TombCheckinPageParam;
import com.memorial.system.service.TombCheckinService;
import com.memorial.system.vo.TombCheckinVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Slf4j
@Service
public class TombCheckinServiceImpl extends BaseServiceImpl<TombCheckinMapper, TombCheckin> implements TombCheckinService {

    @Autowired
    private TombCheckinMapper tombCheckinMapper;

    @Autowired
    private TombMapper tombMapper;

    @Autowired
    private FamilyMemberMapper familyMemberMapper;

    @Autowired
    private com.memorial.system.mapper.FamilyMapper familyMapper;

    @Override
    public Paging<TombCheckinVO> getCheckinPageList(TombCheckinPageParam param) throws Exception {
        try {
            param.setCurrentUserId(LoginUtil.getUserId());
            param.setIsAdmin(LoginUtil.isAdmin());
        } catch (Exception e) {
            param.setIsAdmin(null); // 未登录时不按角色过滤（游客端按 tombId 查看）
        }
        Page<TombCheckinVO> page = new Page<>(param.getPageIndex(), param.getPageSize());
        IPage<TombCheckinVO> iPage = tombCheckinMapper.getCheckinList(page, param);
        return new Paging<>(iPage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addCheckin(Long tombId, String visitorName, String type, HttpServletRequest request) throws Exception {
        Tomb tomb = tombMapper.selectById(tombId);
        if (tomb == null) {
            throw new BusinessException(500, "墓碑不存在");
        }

        TombCheckin checkin = new TombCheckin();
        checkin.setTombId(tombId);
        checkin.setVisitorName(visitorName);
        checkin.setType(type);
        checkin.setCreateTime(new Date());

        if (request != null) {
            checkin.setIp(IpUtil.getRequestIp(request));
        }

        tombCheckinMapper.insert(checkin);

        tomb.setVisitCount(tomb.getVisitCount() + 1);
        tombMapper.updateById(tomb);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addCheckinAuth(Long tombId, Long userId, String visitorName, String type, HttpServletRequest request) throws Exception {
        Tomb tomb = tombMapper.selectById(tombId);
        if (tomb == null) {
            throw new BusinessException(500, "墓碑不存在");
        }

        // 权限：如果未开放访客互动，则必须为同根家族成员（同一最顶级下的成员均可互动，如二伯家孩子给大伯家坟墓祭拜）
        if (Boolean.FALSE.equals(tomb.getVisitorActionOpen()) && tomb.getFamilyId() != null) {
            int count = familyMapper.isUserInSameRootFamilyTree(userId, tomb.getFamilyId());
            if (count <= 0) {
                throw new BusinessException(403, "当前墓碑未开放访客互动，仅同族成员可献花/打卡");
            }
        }

        // 限流：每天每用户每墓碑最多 3 次互动（基于 tomb_checkin 表统计，不受浏览器/缓存影响）
        int todayCount = tombCheckinMapper.countTodayByTombAndUser(tombId, userId);
        if (todayCount >= 3) {
            throw new BusinessException(429, "操作过于频繁：每天最多互动3次");
        }

        TombCheckin checkin = new TombCheckin();
        checkin.setTombId(tombId);
        checkin.setUserId(userId);
        checkin.setVisitorName(visitorName);
        checkin.setType(type);
        checkin.setCreateTime(new Date());
        if (request != null) {
            checkin.setIp(IpUtil.getRequestIp(request));
        }
        // 位置字段目前由外部逻辑填充，这里保持空
        tombCheckinMapper.insert(checkin);

        // 访问量+1（保留现有统计口径）
        tomb.setVisitCount(tomb.getVisitCount() + 1);
        tombMapper.updateById(tomb);
        return true;
    }
}
