package com.memorial.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.memorial.common.base.BaseServiceImpl;
import com.memorial.common.exception.BusinessException;
import com.memorial.common.pagination.Paging;
import com.memorial.system.entity.Tomb;
import com.memorial.system.entity.TombMessage;
import com.memorial.system.mapper.TombMapper;
import com.memorial.system.mapper.TombMessageMapper;
import com.memorial.system.param.TombMessagePageParam;
import com.memorial.system.service.TombMessageService;
import com.memorial.system.vo.TombMessageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@Service
public class TombMessageServiceImpl extends BaseServiceImpl<TombMessageMapper, TombMessage> implements TombMessageService {

    @Autowired
    private TombMessageMapper tombMessageMapper;

    @Autowired
    private TombMapper tombMapper;

    @Override
    public Paging<TombMessageVO> getMessagePageList(TombMessagePageParam param) throws Exception {
        Page<TombMessageVO> page = new Page<>(param.getPageIndex(), param.getPageSize());
        IPage<TombMessageVO> iPage = tombMessageMapper.getMessageList(page, param);
        return new Paging<>(iPage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean approveMessage(Long id) throws Exception {
        TombMessage message = tombMessageMapper.selectById(id);
        if (message == null) {
            throw new BusinessException(500, "留言不存在");
        }
        message.setStatus("approved");
        message.setUpdateTime(new Date());
        tombMessageMapper.updateById(message);

        Tomb tomb = tombMapper.selectById(message.getTombId());
        if (tomb != null) {
            tomb.setMessageCount(tomb.getMessageCount() + 1);
            tombMapper.updateById(tomb);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean rejectMessage(Long id, String reason) throws Exception {
        TombMessage message = tombMessageMapper.selectById(id);
        if (message == null) {
            throw new BusinessException(500, "留言不存在");
        }
        message.setStatus("rejected");
        message.setRejectReason(reason);
        message.setUpdateTime(new Date());
        tombMessageMapper.updateById(message);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteMessage(Long id) throws Exception {
        return super.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addMessage(Long tombId, String visitorName, String content) throws Exception {
        Tomb tomb = tombMapper.selectById(tombId);
        if (tomb == null) {
            throw new BusinessException(500, "墓碑不存在");
        }

        TombMessage message = new TombMessage();
        message.setTombId(tombId);
        message.setVisitorName(visitorName);
        message.setContent(content);
        message.setStatus("pending");
        message.setCreateTime(new Date());
        tombMessageMapper.insert(message);
        return true;
    }
}
