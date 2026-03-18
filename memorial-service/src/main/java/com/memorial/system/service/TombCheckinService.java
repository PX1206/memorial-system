package com.memorial.system.service;

import com.memorial.common.base.BaseService;
import com.memorial.common.pagination.Paging;
import com.memorial.system.entity.TombCheckin;
import com.memorial.system.param.TombCheckinPageParam;
import com.memorial.system.vo.TombCheckinVO;

import javax.servlet.http.HttpServletRequest;

public interface TombCheckinService extends BaseService<TombCheckin> {

    Paging<TombCheckinVO> getCheckinPageList(TombCheckinPageParam param) throws Exception;

    boolean addCheckin(Long tombId, String visitorName, String type, HttpServletRequest request) throws Exception;

    /**
     * 打卡（需要登录，支持匿名姓名展示）
     */
    boolean addCheckinAuth(Long tombId, Long userId, String visitorName, String type, HttpServletRequest request) throws Exception;
}
