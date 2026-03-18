package com.memorial.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.memorial.common.base.BaseServiceImpl;
import com.memorial.common.exception.BusinessException;
import com.memorial.common.tool.LoginUtil;
import com.memorial.common.pagination.Paging;
import com.memorial.system.entity.Family;
import com.memorial.system.entity.FamilyMember;
import com.memorial.system.entity.Tomb;
import com.memorial.system.entity.TombMessage;
import com.memorial.system.mapper.FamilyMapper;
import com.memorial.system.mapper.FamilyMemberMapper;
import com.memorial.system.mapper.TombMapper;
import com.memorial.system.mapper.TombMessageMapper;
import com.memorial.system.mapper.UserMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.memorial.system.entity.User;
import com.memorial.system.param.TombMessagePageParam;
import com.memorial.system.service.TombMessageService;
import com.memorial.system.vo.TombMessageVO;
import com.memorial.common.redis.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Objects;

@Slf4j
@Service
public class TombMessageServiceImpl extends BaseServiceImpl<TombMessageMapper, TombMessage> implements TombMessageService {

    @Autowired
    private TombMessageMapper tombMessageMapper;

    @Autowired
    private TombMapper tombMapper;

    @Autowired
    private FamilyMapper familyMapper;

    @Autowired
    private FamilyMemberMapper familyMemberMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public Paging<TombMessageVO> getMessagePageList(TombMessagePageParam param) throws Exception {
        try {
            param.setCurrentUserId(LoginUtil.getUserId());
            param.setIsAdmin(LoginUtil.isAdmin());
        } catch (Exception e) {
            param.setCurrentUserId(null);
            param.setIsAdmin(null); // 未登录时不按角色过滤（游客端按 tombId 查看）
        }
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
        checkTombAccessForMessage(message.getTombId());
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
        checkTombAccessForMessage(message.getTombId());
        message.setStatus("rejected");
        message.setRejectReason(reason);
        message.setUpdateTime(new Date());
        tombMessageMapper.updateById(message);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteMessage(Long id) throws Exception {
        TombMessage message = tombMessageMapper.selectById(id);
        if (message != null) checkTombAccessForMessage(message.getTombId());
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addMessageAuth(Long tombId, Long userId, String visitorName, String content) throws Exception {
        Tomb tomb = tombMapper.selectById(tombId);
        if (tomb == null) {
            throw new BusinessException(500, "墓碑不存在");
        }

        // 限流：每个用户每个墓碑 1小时内最多 3 次留言
        String limitKey = "limit:tomb:msg:" + tombId + ":" + userId;
        long num = redisUtil.incr(limitKey, 1);
        if (num == 1) {
            redisUtil.expire(limitKey, 3600);
        }
        if (num > 3) {
            throw new BusinessException(429, "操作过于频繁：1小时内最多留言3次");
        }

        // 权限：如果未开放访客互动，则必须为家族成员
        if (Boolean.FALSE.equals(tomb.getVisitorActionOpen()) && tomb.getFamilyId() != null) {
            Integer count = familyMemberMapper.selectCount(
                    com.baomidou.mybatisplus.core.toolkit.Wrappers.<FamilyMember>lambdaQuery()
                            .eq(FamilyMember::getFamilyId, tomb.getFamilyId())
                            .eq(FamilyMember::getUserId, userId)
                            .eq(FamilyMember::getDelFlag, false)
            );
            if (count == null || count <= 0) {
                throw new BusinessException(403, "当前墓碑未开放访客互动，仅家族成员可留言");
            }
        }

        User user = userMapper.selectById(userId);

        TombMessage message = new TombMessage();
        message.setTombId(tombId);
        message.setUserId(userId);
        message.setVisitorName(visitorName);
        if (user != null) {
            message.setAvatar(user.getHeadImg());
        }
        message.setContent(content);
        message.setStatus("pending");
        message.setCreateTime(new Date());
        message.setCreateBy(userId);
        tombMessageMapper.insert(message);
        return true;
    }

    /** 角色2校验：只能操作自己家族下墓碑的留言 */
    private void checkTombAccessForMessage(Long tombId) {
        if (LoginUtil.isAdmin()) return;
        Tomb tomb = tombMapper.selectById(tombId);
        if (tomb == null) return;
        Long userId = LoginUtil.getUserId();
        if (tomb.getFamilyId() == null) {
            if (Objects.equals(tomb.getUserId(), userId) || Objects.equals(tomb.getCreateBy(), userId)) return;
            throw new BusinessException(403, "无权限操作该留言");
        }
        Family family = familyMapper.selectById(tomb.getFamilyId());
        if (family == null) return;
        if (Objects.equals(family.getFounderId(), userId)) return;
        Integer count = familyMemberMapper.selectCount(Wrappers.<FamilyMember>lambdaQuery()
                .eq(FamilyMember::getFamilyId, tomb.getFamilyId())
                .eq(FamilyMember::getUserId, userId)
                .eq(FamilyMember::getDelFlag, false));
        if (count == null || count <= 0) {
            throw new BusinessException(403, "无权限操作该留言");
        }
    }
}
