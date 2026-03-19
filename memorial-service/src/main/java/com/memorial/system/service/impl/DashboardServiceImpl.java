package com.memorial.system.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.memorial.common.tool.LoginUtil;
import com.memorial.system.entity.User;
import com.memorial.system.mapper.FamilyMapper;
import com.memorial.system.mapper.TombCheckinMapper;
import com.memorial.system.mapper.TombMapper;
import com.memorial.system.mapper.TombMessageMapper;
import com.memorial.system.mapper.UserMapper;
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
        Long currentUserId = LoginUtil.getUserId();
        boolean isAdmin = LoginUtil.isAdmin();

        DashboardVO vo = new DashboardVO();

        vo.setTombCount(tombMapper.countByPermission(currentUserId, isAdmin));
        vo.setFamilyCount(familyMapper.countByPermission(currentUserId, isAdmin));
        vo.setMessageCount(tombMessageMapper.countByPermission(currentUserId, isAdmin));

        if (isAdmin) {
            vo.setUserCount((long) userMapper.selectCount(
                    Wrappers.lambdaQuery(User.class).eq(User::getDelFlag, false)));
        } else {
            vo.setUserCount(familyMapper.countDistinctUsersInAccessibleTree(currentUserId));
        }

        List<TombCheckinVO> recentCheckins = tombCheckinMapper.getRecentCheckinsWithPermission(5, currentUserId, isAdmin);
        vo.setRecentCheckins(recentCheckins);

        List<TombMessageVO> recentMessages = tombMessageMapper.getRecentMessagesWithPermission(5, currentUserId, isAdmin);
        vo.setRecentMessages(recentMessages);

        return vo;
    }
}
