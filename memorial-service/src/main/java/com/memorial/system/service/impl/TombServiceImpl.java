package com.memorial.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.memorial.common.base.BaseServiceImpl;
import com.memorial.common.exception.BusinessException;
import com.memorial.common.pagination.PageInfo;
import com.memorial.common.pagination.Paging;
import com.memorial.common.tool.LoginUtil;
import com.memorial.system.entity.Tomb;
import com.memorial.system.mapper.TombMapper;
import com.memorial.system.param.TombPageParam;
import com.memorial.system.param.TombParam;
import com.memorial.system.service.TombService;
import com.memorial.system.vo.TombVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Service
public class TombServiceImpl extends BaseServiceImpl<TombMapper, Tomb> implements TombService {

    @Autowired
    private TombMapper tombMapper;

    @Override
    public Paging<TombVO> getTombPageList(TombPageParam param) throws Exception {
        Page<TombVO> page = new PageInfo<>(param);
        IPage<TombVO> iPage = tombMapper.getTombList(page, param);
        return new Paging<>(iPage);
    }

    @Override
    public TombVO getTomb(Long id) throws Exception {
        TombVO tombVO = tombMapper.getTombVO(id);
        if (tombVO == null) {
            throw new BusinessException(500, "墓碑信息不存在");
        }
        return tombVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addTomb(TombParam param) throws Exception {
        Tomb tomb = new Tomb();
        tomb.setName(param.getName());
        tomb.setPhoto(param.getPhoto());
        tomb.setBiography(param.getBiography());
        tomb.setStory(param.getStory());
        tomb.setFamilyId(param.getFamilyId());
        tomb.setVisitCount(0);
        tomb.setMessageCount(0);
        tomb.setStatus(1);
        tomb.setUserId(LoginUtil.getUserId());
        tomb.setCreateBy(LoginUtil.getUserId());
        tomb.setCreateTime(new Date());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (param.getBirthday() != null && !param.getBirthday().isEmpty()) {
            tomb.setBirthday(sdf.parse(param.getBirthday()));
        }
        if (param.getDeathday() != null && !param.getDeathday().isEmpty()) {
            tomb.setDeathday(sdf.parse(param.getDeathday()));
        }

        tombMapper.insert(tomb);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTomb(TombParam param) throws Exception {
        if (param.getId() == null) {
            throw new BusinessException(500, "墓碑ID不能为空");
        }
        Tomb tomb = tombMapper.selectById(param.getId());
        if (tomb == null) {
            throw new BusinessException(500, "墓碑信息不存在");
        }

        tomb.setName(param.getName());
        tomb.setPhoto(param.getPhoto());
        tomb.setBiography(param.getBiography());
        tomb.setStory(param.getStory());
        tomb.setFamilyId(param.getFamilyId());
        tomb.setUpdateBy(LoginUtil.getUserId());
        tomb.setUpdateTime(new Date());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (param.getBirthday() != null && !param.getBirthday().isEmpty()) {
            tomb.setBirthday(sdf.parse(param.getBirthday()));
        }
        if (param.getDeathday() != null && !param.getDeathday().isEmpty()) {
            tomb.setDeathday(sdf.parse(param.getDeathday()));
        }

        tombMapper.updateById(tomb);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTomb(Long id) throws Exception {
        return super.removeById(id);
    }

    @Override
    public TombVO getTombDetail(Long id) throws Exception {
        TombVO tombVO = tombMapper.getTombVO(id);
        if (tombVO == null) {
            throw new BusinessException(500, "墓碑信息不存在");
        }
        // 访问量+1
        Tomb tomb = tombMapper.selectById(id);
        if (tomb != null) {
            tomb.setVisitCount(tomb.getVisitCount() + 1);
            tombMapper.updateById(tomb);
        }
        return tombVO;
    }
}
