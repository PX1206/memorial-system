package com.memorial.system.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.memorial.common.exception.BusinessException;
import com.memorial.common.tool.LoginUtil;
import com.memorial.system.entity.Family;
import com.memorial.system.entity.FamilyMember;
import com.memorial.system.entity.FamilyMemberApply;
import com.memorial.system.entity.User;
import com.memorial.system.mapper.FamilyMapper;
import com.memorial.system.mapper.FamilyMemberApplyMapper;
import com.memorial.system.mapper.FamilyMemberMapper;
import com.memorial.system.mapper.UserMapper;
import com.memorial.system.param.ApplyJoinFamilyParam;
import com.memorial.system.service.FamilyMemberApplyService;
import com.memorial.system.vo.FamilyMemberApplyVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FamilyMemberApplyServiceImpl implements FamilyMemberApplyService {

    @Autowired
    private FamilyMemberApplyMapper applyMapper;
    @Autowired
    private FamilyMapper familyMapper;
    @Autowired
    private FamilyMemberMapper familyMemberMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean apply(ApplyJoinFamilyParam param) throws Exception {
        Family family = familyMapper.selectById(param.getFamilyId());
        if (family == null) {
            throw new BusinessException(500, "家族不存在");
        }
        Long userId = LoginUtil.getUserId();

        // 已是成员则不允许重复申请
        Integer exists = familyMemberMapper.selectCount(Wrappers.<FamilyMember>lambdaQuery()
                .eq(FamilyMember::getFamilyId, param.getFamilyId())
                .eq(FamilyMember::getUserId, userId)
                .eq(FamilyMember::getDelFlag, false));
        if (exists != null && exists > 0) {
            throw new BusinessException(500, "您已是该家族成员，无需申请");
        }

        // 已有待审核申请
        Integer pending = applyMapper.selectCount(Wrappers.<FamilyMemberApply>lambdaQuery()
                .eq(FamilyMemberApply::getFamilyId, param.getFamilyId())
                .eq(FamilyMemberApply::getUserId, userId)
                .eq(FamilyMemberApply::getStatus, "pending"));
        if (pending != null && pending > 0) {
            throw new BusinessException(500, "您已提交过申请，请等待审核");
        }

        FamilyMemberApply apply = new FamilyMemberApply();
        apply.setFamilyId(param.getFamilyId());
        apply.setUserId(userId);
        apply.setRelation(param.getRelation());
        apply.setReason(param.getReason());
        apply.setStatus("pending");
        apply.setCreateTime(new Date());
        applyMapper.insert(apply);
        return true;
    }

    @Override
    public List<FamilyMemberApplyVO> listByFamily(Long familyId, String status) throws Exception {
        List<FamilyMemberApply> list = applyMapper.selectList(Wrappers.<FamilyMemberApply>lambdaQuery()
                .eq(FamilyMemberApply::getFamilyId, familyId)
                .eq(status != null && !status.isEmpty(), FamilyMemberApply::getStatus, status)
                .orderByDesc(FamilyMemberApply::getCreateTime));
        Family family = familyMapper.selectById(familyId);
        String familyName = family != null ? family.getName() : null;
        return list.stream().map(a -> {
            FamilyMemberApplyVO vo = new FamilyMemberApplyVO();
            vo.setId(a.getId());
            vo.setFamilyId(a.getFamilyId());
            vo.setFamilyName(familyName);
            vo.setUserId(a.getUserId());
            vo.setRelation(a.getRelation());
            vo.setReason(a.getReason());
            vo.setStatus(a.getStatus());
            vo.setRejectReason(a.getRejectReason());
            vo.setCreateTime(a.getCreateTime());
            vo.setUpdateTime(a.getUpdateTime());
            User u = userMapper.selectById(a.getUserId());
            vo.setUserName(u != null ? (u.getNickname() != null && !u.getNickname().isEmpty() ? u.getNickname() : u.getUsername()) : String.valueOf(a.getUserId()));
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean approve(Long applyId) throws Exception {
        FamilyMemberApply apply = applyMapper.selectById(applyId);
        if (apply == null) {
            throw new BusinessException(500, "申请不存在");
        }
        if (!"pending".equals(apply.getStatus())) {
            throw new BusinessException(500, "该申请已处理");
        }
        Family family = familyMapper.selectById(apply.getFamilyId());
        if (family == null) {
            throw new BusinessException(500, "家族不存在");
        }
        checkFamilyAdminAccess(family);

        // 再次检查是否已是成员
        Integer exists = familyMemberMapper.selectCount(Wrappers.<FamilyMember>lambdaQuery()
                .eq(FamilyMember::getFamilyId, apply.getFamilyId())
                .eq(FamilyMember::getUserId, apply.getUserId())
                .eq(FamilyMember::getDelFlag, false));
        if (exists != null && exists > 0) {
            apply.setStatus("rejected");
            apply.setRejectReason("已是家族成员");
            apply.setUpdateTime(new Date());
            apply.setUpdateBy(LoginUtil.getUserId());
            applyMapper.updateById(apply);
            throw new BusinessException(500, "该用户已是家族成员");
        }

        FamilyMember member = new FamilyMember();
        member.setFamilyId(apply.getFamilyId());
        member.setUserId(apply.getUserId());
        member.setName(LoginUtil.getUserId().equals(apply.getUserId()) ? LoginUtil.getNickname() : getNicknameByUserId(apply.getUserId()));
        member.setRelation(apply.getRelation());
        member.setRole("成员");
        member.setCreateBy(LoginUtil.getUserId());
        member.setCreateTime(new Date());
        familyMemberMapper.insert(member);

        family.setMemberCount(family.getMemberCount() + 1);
        familyMapper.updateById(family);

        apply.setStatus("approved");
        apply.setUpdateTime(new Date());
        apply.setUpdateBy(LoginUtil.getUserId());
        applyMapper.updateById(apply);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean reject(Long applyId, String rejectReason) throws Exception {
        FamilyMemberApply apply = applyMapper.selectById(applyId);
        if (apply == null) {
            throw new BusinessException(500, "申请不存在");
        }
        if (!"pending".equals(apply.getStatus())) {
            throw new BusinessException(500, "该申请已处理");
        }
        Family family = familyMapper.selectById(apply.getFamilyId());
        if (family != null) checkFamilyAdminAccess(family);
        apply.setStatus("rejected");
        apply.setRejectReason(rejectReason);
        apply.setUpdateTime(new Date());
        apply.setUpdateBy(LoginUtil.getUserId());
        applyMapper.updateById(apply);
        return true;
    }

    private void checkFamilyAdminAccess(Family family) {
        Long userId = LoginUtil.getUserId();
        if (family.getFounderId() != null && family.getFounderId().equals(userId)) return;
        Integer count = familyMemberMapper.selectCount(Wrappers.<FamilyMember>lambdaQuery()
                .eq(FamilyMember::getFamilyId, family.getId())
                .eq(FamilyMember::getUserId, userId)
                .eq(FamilyMember::getDelFlag, false));
        if (count == null || count <= 0) {
            throw new BusinessException(403, "仅家族成员可审核申请");
        }
    }

    private String getNicknameByUserId(Long userId) {
        User u = userMapper.selectById(userId);
        if (u == null) return "用户" + userId;
        return (u.getNickname() != null && !u.getNickname().isEmpty()) ? u.getNickname() : u.getUsername();
    }
}
