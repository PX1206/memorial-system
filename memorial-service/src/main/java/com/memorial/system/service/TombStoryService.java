package com.memorial.system.service;

import com.memorial.common.base.BaseService;
import com.memorial.system.entity.TombStory;
import com.memorial.system.param.TombStoryParam;
import com.memorial.system.vo.TombStoryVO;

import java.util.List;

public interface TombStoryService extends BaseService<TombStory> {

    List<TombStoryVO> listByTombId(Long tombId) throws Exception;

    boolean addStory(TombStoryParam param) throws Exception;

    boolean updateStory(TombStoryParam param) throws Exception;

    boolean deleteStory(Long id) throws Exception;
}

