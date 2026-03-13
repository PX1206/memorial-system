package com.memorial.system.service;

import com.memorial.system.vo.AreaTreeVO;
import com.memorial.system.vo.AreaVO;

import java.util.List;

/**
 *  服务类
 *
 * @author Sakura
 * @since 2024-08-12
 */
public interface AreaService {

    List<AreaVO> getSubAreas(Integer parentId);

    List<AreaTreeVO> getAreas() throws Exception;

}
