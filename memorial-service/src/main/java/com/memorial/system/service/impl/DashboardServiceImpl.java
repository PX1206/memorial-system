package com.memorial.system.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.memorial.system.entity.Family;
import com.memorial.system.entity.Tomb;
import com.memorial.system.entity.TombMessage;
import com.memorial.system.entity.User;
import com.memorial.system.mapper.*;
import com.memorial.system.service.DashboardService;
import com.memorial.system.vo.DashboardVO;
import com.memorial.system.vo.TombCheckinVO;
import com.memorial.system.vo.TombMessageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private TombMapper tombMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private FamilyMapper familyMapper;
    @Autowired
    private TombMessageMapper tombMessageMapper;
    @Autowired
    private TombCheckinMapper tombCheckinMapper;

    @Override
    public DashboardVO getStats() throws Exception {
        DashboardVO vo = new DashboardVO();

        vo.setTombCount((long) tombMapper.selectCount(
                Wrappers.lambdaQuery(Tomb.class).eq(Tomb::getDelFlag, false)));
        vo.setUserCount((long) userMapper.selectCount(
                Wrappers.lambdaQuery(User.class).eq(User::getDelFlag, false)));
        vo.setFamilyCount((long) familyMapper.selectCount(
                Wrappers.lambdaQuery(Family.class).eq(Family::getDelFlag, false)));
        vo.setMessageCount((long) tombMessageMapper.selectCount(
                Wrappers.lambdaQuery(TombMessage.class).eq(TombMessage::getDelFlag, false)));

        List<TombCheckinVO> recentCheckins = tombCheckinMapper.getRecentCheckins(5);
        vo.setRecentCheckins(recentCheckins);

        List<TombMessageVO> recentMessages = tombMessageMapper.getRecentMessages(5);
        vo.setRecentMessages(recentMessages);

        return vo;
    }
}
