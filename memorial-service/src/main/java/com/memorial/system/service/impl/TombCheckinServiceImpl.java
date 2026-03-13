package com.memorial.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.memorial.common.base.BaseServiceImpl;
import com.memorial.common.exception.BusinessException;
import com.memorial.common.pagination.Paging;
import com.memorial.common.tool.IpUtil;
import com.memorial.system.entity.Tomb;
import com.memorial.system.entity.TombCheckin;
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

    @Override
    public Paging<TombCheckinVO> getCheckinPageList(TombCheckinPageParam param) throws Exception {
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
}
