package com.memorial.system.service;

import com.memorial.common.base.BaseService;
import com.memorial.common.pagination.Paging;
import com.memorial.system.entity.TombMessage;
import com.memorial.system.param.TombMessagePageParam;
import com.memorial.system.vo.TombMessageVO;

public interface TombMessageService extends BaseService<TombMessage> {

    Paging<TombMessageVO> getMessagePageList(TombMessagePageParam param) throws Exception;

    boolean approveMessage(Long id) throws Exception;

    boolean rejectMessage(Long id, String reason) throws Exception;

    boolean deleteMessage(Long id) throws Exception;

    boolean addMessage(Long tombId, String visitorName, String content) throws Exception;

    /**
     * 提交留言（需要登录，支持匿名姓名展示）
     */
    boolean addMessageAuth(Long tombId, Long userId, String visitorName, String content) throws Exception;
}
