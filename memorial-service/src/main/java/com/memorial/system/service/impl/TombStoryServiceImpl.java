package com.memorial.system.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.memorial.common.base.BaseServiceImpl;
import com.memorial.common.exception.BusinessException;
import com.memorial.common.tool.LoginUtil;
import com.memorial.system.entity.TombStory;
import com.memorial.system.mapper.TombStoryMapper;
import com.memorial.system.param.TombStoryParam;
import com.memorial.system.service.TombStoryService;
import com.memorial.system.vo.TombStoryVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TombStoryServiceImpl extends BaseServiceImpl<TombStoryMapper, TombStory> implements TombStoryService {

    @Autowired
    private TombStoryMapper tombStoryMapper;

    @Override
    public List<TombStoryVO> listByTombId(Long tombId) throws Exception {
        List<TombStory> list = tombStoryMapper.selectList(
                Wrappers.<TombStory>lambdaQuery()
                        .eq(TombStory::getTombId, tombId)
                        .eq(TombStory::getDelFlag, false)
                        .orderByAsc(TombStory::getSort)
                        .orderByAsc(TombStory::getId)
        );
        return list.stream().map(item -> {
            TombStoryVO vo = new TombStoryVO();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addStory(TombStoryParam param) throws Exception {
        TombStory story = new TombStory();
        story.setTombId(param.getTombId());
        story.setTitle(param.getTitle());
        story.setContent(param.getContent());
        story.setSort(param.getSort() != null ? param.getSort() : 0);
        story.setCreateBy(LoginUtil.getUserId());
        story.setCreateTime(new Date());
        tombStoryMapper.insert(story);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStory(TombStoryParam param) throws Exception {
        if (param.getId() == null) {
            throw new BusinessException(500, "事迹ID不能为空");
        }
        TombStory story = tombStoryMapper.selectById(param.getId());
        if (story == null || Boolean.TRUE.equals(story.getDelFlag())) {
            throw new BusinessException(500, "事迹信息不存在");
        }
        story.setTitle(param.getTitle());
        story.setContent(param.getContent());
        if (param.getSort() != null) {
            story.setSort(param.getSort());
        }
        story.setUpdateBy(LoginUtil.getUserId());
        story.setUpdateTime(new Date());
        tombStoryMapper.updateById(story);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteStory(Long id) throws Exception {
        TombStory story = tombStoryMapper.selectById(id);
        if (story == null || Boolean.TRUE.equals(story.getDelFlag())) {
            throw new BusinessException(500, "事迹信息不存在");
        }
        return super.removeById(id);
    }
}

