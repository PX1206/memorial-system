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
import com.memorial.system.mapper.FamilyMemberMapper;
import com.memorial.system.mapper.TombCheckinMapper;
import com.memorial.system.mapper.TombMapper;
import com.memorial.system.param.TombCheckinPageParam;
import com.memorial.system.service.TombCheckinService;
import com.memorial.system.vo.TombCheckinVO;
import com.memorial.common.redis.RedisUtil;
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
    private RedisUtil redisUtil;

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

        // 限流：每个用户每个墓碑 1小时内最多 3 次互动（献花/点蜡烛等）
        String limitKey = "limit:tomb:checkin:" + tombId + ":" + userId;
        long num = redisUtil.incr(limitKey, 1);
        if (num == 1) {
            redisUtil.expire(limitKey, 3600);
        }
        if (num > 3) {
            throw new BusinessException(429, "操作过于频繁：1小时内最多互动3次");
        }

        // 权限：如果未开放访客互动，则必须为家族成员
        if (Boolean.FALSE.equals(tomb.getVisitorActionOpen()) && tomb.getFamilyId() != null) {
            Integer count = familyMemberMapper.selectCount(
                    com.baomidou.mybatisplus.core.toolkit.Wrappers.<FamilyMember>lambdaQuery()
                            .eq(FamilyMember::getFamilyId, tomb.getFamilyId())
                            .eq(FamilyMember::getUserId, userId)
                            .eq(FamilyMember::getDelFlag, false)
            );
            if (count == null || count <= 0) {
                throw new BusinessException(403, "当前墓碑未开放访客互动，仅家族成员可献花/打卡");
            }
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
