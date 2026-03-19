package com.memorial.system.service;

import com.memorial.common.base.BaseService;
import com.memorial.common.pagination.Paging;
import com.memorial.system.entity.Tomb;
import com.memorial.system.param.TombPageParam;
import com.memorial.system.param.TombParam;
import com.memorial.system.vo.TombVO;

public interface TombService extends BaseService<Tomb> {

    Paging<TombVO> getTombPageList(TombPageParam param) throws Exception;

    TombVO getTomb(Long id) throws Exception;

    boolean addTomb(TombParam param) throws Exception;

    boolean updateTomb(TombParam param) throws Exception;

    boolean deleteTomb(Long id) throws Exception;

    TombVO getTombDetail(Long id) throws Exception;

    /**
     * 根据二维码标识获取墓碑详情（扫码访问，访问量+1）
     */
    TombVO getTombDetailByCode(String code) throws Exception;

    /** 校验当前用户是否有权操作指定墓碑（与家族管理逻辑一致），无权限则抛异常 */
    void checkTombAccess(Long tombId) throws Exception;
}
