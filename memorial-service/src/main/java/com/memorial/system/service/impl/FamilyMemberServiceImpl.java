package com.memorial.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.memorial.common.base.BaseServiceImpl;
import com.memorial.common.exception.BusinessException;
import com.memorial.common.pagination.Paging;
import com.memorial.common.tool.LoginUtil;
import com.memorial.system.entity.Family;
import com.memorial.system.entity.FamilyMember;
import com.memorial.system.mapper.FamilyMapper;
import com.memorial.system.mapper.FamilyMemberMapper;
import com.memorial.system.param.FamilyMemberPageParam;
import com.memorial.system.param.FamilyMemberParam;
import com.memorial.system.param.JoinFamilyByCodeParam;
import com.memorial.system.service.FamilyMemberService;
import com.memorial.system.vo.FamilyMemberVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Objects;

@Slf4j
@Service
public class FamilyMemberServiceImpl extends BaseServiceImpl<FamilyMemberMapper, FamilyMember> implements FamilyMemberService {

    @Autowired
    private FamilyMemberMapper familyMemberMapper;

    @Autowired
    private FamilyMapper familyMapper;

    @Override
    public Paging<FamilyMemberVO> getMemberPageList(FamilyMemberPageParam param) throws Exception {
        param.setCurrentUserId(LoginUtil.getUserId());
        param.setIsAdmin(LoginUtil.isAdmin());
        Page<FamilyMemberVO> page = new Page<>(param.getPageIndex(), param.getPageSize());
        IPage<FamilyMemberVO> iPage = familyMemberMapper.getMemberList(page, param);
        return new Paging<>(iPage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addMember(FamilyMemberParam param) throws Exception {
        Family family = familyMapper.selectById(param.getFamilyId());
        if (family == null) {
            throw new BusinessException(500, "家族不存在");
        }
        checkFamilyAdminAccess(family);

        String roleToSet = param.getRole() != null && !param.getRole().trim().isEmpty() ? param.getRole() : "成员";
        if ("族长".equals(roleToSet)) {
            throw new BusinessException(403, "族长身份不可转让");
        }
        if ("管理员".equals(roleToSet)) {
            String myRole = getMyRoleInFamily(param.getFamilyId());
            if (!"族长".equals(myRole)) {
                throw new BusinessException(403, "仅族长可添加管理员");
            }
        }
        FamilyMember member = new FamilyMember();
        member.setFamilyId(param.getFamilyId());
        member.setName(param.getName());
        member.setRelation(param.getRelation());
        member.setPhone(param.getPhone());
        member.setRole(roleToSet);
        member.setCreateBy(LoginUtil.getUserId());
        member.setCreateTime(new Date());
        familyMemberMapper.insert(member);

        family.setMemberCount(family.getMemberCount() + 1);
        familyMapper.updateById(family);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMember(FamilyMemberParam param) throws Exception {
        if (param.getId() == null) {
            throw new BusinessException(500, "成员ID不能为空");
        }
        FamilyMember member = familyMemberMapper.selectById(param.getId());
        if (member == null) {
            throw new BusinessException(500, "成员信息不存在");
        }
        Family family = familyMapper.selectById(member.getFamilyId());
        if (family != null) checkFamilyAdminAccess(family);
        member.setName(param.getName());
        member.setRelation(param.getRelation());
        member.setPhone(param.getPhone());
        // 角色：仅族长可设为管理员；族长不可转让
        if (param.getRole() != null && !param.getRole().trim().isEmpty()) {
            if ("族长".equals(param.getRole())) {
                throw new BusinessException(403, "族长身份不可转让，仅家族创始人为族长");
            }
            if ("管理员".equals(param.getRole())) {
                String myRole = getMyRoleInFamily(member.getFamilyId());
                if (!"族长".equals(myRole)) {
                    throw new BusinessException(403, "仅族长可将成员设为管理员");
                }
            }
            member.setRole(param.getRole());
        }
        member.setUpdateBy(LoginUtil.getUserId());
        member.setUpdateTime(new Date());
        familyMemberMapper.updateById(member);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeMember(Long id) throws Exception {
        FamilyMember member = familyMemberMapper.selectById(id);
        if (member == null) {
            throw new BusinessException(500, "成员信息不存在");
        }
        Family family = familyMapper.selectById(member.getFamilyId());
        if (family != null) checkFamilyAdminAccess(family);
        super.removeById(id);

        if (family != null && family.getMemberCount() > 0) {
            family.setMemberCount(family.getMemberCount() - 1);
            familyMapper.updateById(family);
        }
        return true;
    }

    @Override
    public String getMyRoleInFamily(Long familyId) throws Exception {
        if (familyId == null) return null;
        Long userId = LoginUtil.getUserId();
        Family family = familyMapper.selectById(familyId);
        if (family != null && Objects.equals(family.getFounderId(), userId)) {
            return "族长";
        }
        return familyMemberMapper.getMemberRoleInFamily(userId, familyId);
    }

    /** 角色2校验：只能操作自己创建或加入的家族 */
    private void checkFamilyAccess(Family family) {
        if (LoginUtil.isAdmin()) return;
        Long userId = LoginUtil.getUserId();
        if (Objects.equals(family.getFounderId(), userId)) return;
        Integer count = familyMemberMapper.selectCount(Wrappers.<FamilyMember>lambdaQuery()
                .eq(FamilyMember::getFamilyId, family.getId())
                .eq(FamilyMember::getUserId, userId)
                .eq(FamilyMember::getDelFlag, false));
        if (count == null || count <= 0) {
            throw new BusinessException(403, "无权限操作该家族");
        }
    }

    /** 操作权限：仅族长或管理员可执行（编辑/删除家族、添加/编辑/移除成员、审核申请） */
    private void checkFamilyAdminAccess(Family family) throws Exception {
        if (LoginUtil.isAdmin()) return;
        String role = getMyRoleInFamily(family.getId());
        if (role == null || "成员".equals(role)) {
            throw new BusinessException(403, "仅族长或管理员可执行该操作");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean joinByInviteCode(JoinFamilyByCodeParam param) throws Exception {
        // 根据邀请码查询家族
        Family family = familyMapper.selectOne(Wrappers.<Family>lambdaQuery()
                .eq(Family::getInviteCode, param.getInviteCode())
                .eq(Family::getDelFlag, false));
        if (family == null) {
            throw new BusinessException(500, "邀请码无效或家族不存在");
        }

        Long userId = LoginUtil.getUserId();
        // 校验是否已是该家族成员
        Integer count = familyMemberMapper.selectCount(Wrappers.<FamilyMember>lambdaQuery()
                .eq(FamilyMember::getFamilyId, family.getId())
                .eq(FamilyMember::getUserId, userId)
                .eq(FamilyMember::getDelFlag, false));
        if (count != null && count > 0) {
            throw new BusinessException(500, "你已是该家族成员，不能重复加入");
        }

        // 创建成员记录
        FamilyMember member = new FamilyMember();
        member.setFamilyId(family.getId());
        member.setUserId(userId);
        member.setName(LoginUtil.getUserName());
        member.setRelation(param.getRelation());
        member.setRole(param.getRole() != null && !param.getRole().trim().isEmpty() ? param.getRole() : "成员");
        member.setCreateBy(userId);
        member.setCreateTime(new Date());
        familyMemberMapper.insert(member);

        // 成员数 +1
        family.setMemberCount(family.getMemberCount() + 1);
        familyMapper.updateById(family);

        return true;
    }
}
