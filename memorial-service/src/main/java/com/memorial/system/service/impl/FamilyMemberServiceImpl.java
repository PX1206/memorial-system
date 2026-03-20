package com.memorial.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.memorial.common.base.BaseServiceImpl;
import com.memorial.common.exception.BusinessException;
import com.memorial.common.pagination.Paging;
import com.memorial.common.tool.LoginUtil;
import com.memorial.system.entity.Family;
import com.memorial.system.entity.FamilyAdmin;
import com.memorial.system.entity.FamilyMember;
import com.memorial.system.mapper.FamilyAdminMapper;
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

    @Autowired
    private FamilyAdminMapper familyAdminMapper;

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
    public FamilyMemberVO addMember(FamilyMemberParam param) throws Exception {
        Family family = familyMapper.selectById(param.getFamilyId());
        if (family == null) {
            throw new BusinessException(500, "家族不存在");
        }
        checkFamilyAdminAccess(family);

        String roleToSet = param.getRole() != null && !param.getRole().trim().isEmpty() ? param.getRole() : "成员";
        if ("族长".equals(roleToSet)) {
            throw new BusinessException(403, "族长需在家族设置中指定");
        }
        if ("管理员".equals(roleToSet)) {
            String myRole = getMyRoleInFamily(param.getFamilyId());
            if (!"admin".equals(myRole)) {
                throw new BusinessException(403, "仅超级管理员可指定管理员");
            }
        }

        String bindCode;
        do {
            bindCode = java.util.UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8).toUpperCase();
        } while (familyMemberMapper.selectCount(
                Wrappers.<FamilyMember>lambdaQuery().eq(FamilyMember::getBindCode, bindCode)
        ) > 0);

        FamilyMember member = new FamilyMember();
        member.setFamilyId(param.getFamilyId());
        member.setName(param.getName());
        member.setBindCode(bindCode);
        member.setRelation(param.getRelation());
        member.setPhone(param.getPhone());
        member.setRole(roleToSet);
        member.setCreateBy(LoginUtil.getUserId());
        member.setCreateTime(new Date());
        familyMemberMapper.insert(member);

        family.setMemberCount(family.getMemberCount() + 1);
        familyMapper.updateById(family);

        FamilyMemberVO vo = new FamilyMemberVO();
        vo.setId(member.getId());
        vo.setFamilyId(member.getFamilyId());
        vo.setFamilyName(family.getName());
        vo.setName(member.getName());
        vo.setBindCode(member.getBindCode());
        vo.setRelation(member.getRelation());
        vo.setPhone(member.getPhone());
        vo.setRole(member.getRole());
        vo.setCreateTime(member.getCreateTime());
        return vo;
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
            throw new BusinessException(403, "族长需在家族设置中指定");
        }
        if ("管理员".equals(param.getRole())) {
            String myRole = getMyRoleInFamily(member.getFamilyId());
            if (!"admin".equals(myRole)) {
                throw new BusinessException(403, "仅超级管理员可指定管理员");
            }
            member.setRole(param.getRole());
            ensureFamilyAdminForMember(member);
        } else {
            member.setRole(param.getRole());
            // 从管理员降为成员时，移除 family_admin 中的 admin 记录（不删 super_admin）
            removeFamilyAdminForMember(member);
        }
        }
        // 允许修改所在家庭
        if (param.getFamilyId() != null && !Objects.equals(param.getFamilyId(), member.getFamilyId())) {
            Family newFamily = familyMapper.selectById(param.getFamilyId());
            if (newFamily == null) {
                throw new BusinessException(500, "目标家族不存在");
            }
            checkFamilyAdminAccess(newFamily);
            if (family != null && family.getMemberCount() > 0) {
                family.setMemberCount(family.getMemberCount() - 1);
                familyMapper.updateById(family);
            }
            newFamily.setMemberCount((newFamily.getMemberCount() == null ? 0 : newFamily.getMemberCount()) + 1);
            familyMapper.updateById(newFamily);
            member.setFamilyId(param.getFamilyId());
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
        if ("管理员".equals(member.getRole())) {
            removeFamilyAdminForMember(member);
        }
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
        if (family == null) return null;
        Long rootId = family.getRootId() != null ? family.getRootId() : family.getId();
        String adminRole = familyAdminMapper.getRoleInFamily(rootId, userId);
        if ("super_admin".equals(adminRole) || "admin".equals(adminRole)) return "admin";
        if (Objects.equals(family.getChiefId(), userId)) return "chief";
        String memberRole = familyMemberMapper.getMemberRoleInFamily(userId, familyId);
        // 族长/管理员/成员 均返回 member，便于前端统一判断（成员也可添加成员、创建下级）
        return memberRole != null ? "member" : null;
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

    /** 成员设为管理员时同步写入 family_admin，使其拥有对根家族及所有分支的完整权限 */
    private void ensureFamilyAdminForMember(FamilyMember member) {
        if (member == null || member.getUserId() == null) return;
        Family family = familyMapper.selectById(member.getFamilyId());
        if (family == null) return;
        Long rootId = family.getRootId() != null ? family.getRootId() : family.getId();
        FamilyAdmin existing = familyAdminMapper.selectOne(
                Wrappers.<FamilyAdmin>lambdaQuery()
                        .eq(FamilyAdmin::getFamilyId, rootId)
                        .eq(FamilyAdmin::getUserId, member.getUserId()));
        if (existing != null) return;
        FamilyAdmin admin = new FamilyAdmin();
        admin.setFamilyId(rootId);
        admin.setUserId(member.getUserId());
        admin.setRole("admin");
        admin.setCreateBy(LoginUtil.getUserId());
        admin.setCreateTime(new Date());
        familyAdminMapper.insert(admin);
    }

    /** 成员从管理员降为成员时，移除 family_admin 中的 admin 记录（不删 super_admin） */
    private void removeFamilyAdminForMember(FamilyMember member) {
        if (member == null || member.getUserId() == null) return;
        Family family = familyMapper.selectById(member.getFamilyId());
        if (family == null) return;
        Long rootId = family.getRootId() != null ? family.getRootId() : family.getId();
        FamilyAdmin existing = familyAdminMapper.selectOne(
                Wrappers.<FamilyAdmin>lambdaQuery()
                        .eq(FamilyAdmin::getFamilyId, rootId)
                        .eq(FamilyAdmin::getUserId, member.getUserId()));
        if (existing != null && "admin".equals(existing.getRole())) {
            familyAdminMapper.deleteById(existing.getId());
        }
    }

    /** 操作权限：家族成员即可执行（添加/编辑/移除成员、审核申请等），非成员不可 */
    private void checkFamilyAdminAccess(Family family) throws Exception {
        if (LoginUtil.isAdmin()) return;
        String role = getMyRoleInFamily(family.getId());
        if (role == null) {
            throw new BusinessException(403, "仅家族成员可执行该操作");
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

        // 创建成员记录（邀请码加入固定为成员，不可选管理员）
        FamilyMember member = new FamilyMember();
        member.setFamilyId(family.getId());
        member.setUserId(userId);
        member.setName(LoginUtil.getNickname());
        member.setRelation(param.getRelation());
        member.setRole("成员");
        member.setCreateBy(userId);
        member.setCreateTime(new Date());
        familyMemberMapper.insert(member);

        // 成员数 +1
        family.setMemberCount(family.getMemberCount() + 1);
        familyMapper.updateById(family);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean bindUserToMember(String bindCode) throws Exception {
        if (bindCode == null || bindCode.trim().isEmpty()) {
            throw new BusinessException(500, "绑定码不能为空");
        }
        FamilyMember member = familyMemberMapper.selectOne(
                Wrappers.<FamilyMember>lambdaQuery()
                        .eq(FamilyMember::getBindCode, bindCode.trim())
                        .eq(FamilyMember::getDelFlag, false));
        if (member == null) {
            throw new BusinessException(500, "绑定码无效或成员不存在");
        }
        if (member.getUserId() != null) {
            throw new BusinessException(500, "当前二维码已绑定用户");
        }
        Family family = familyMapper.selectById(member.getFamilyId());
        if (family == null) {
            throw new BusinessException(500, "家族不存在");
        }
        Long userId = LoginUtil.getUserId();
        member.setUserId(userId);
        String mobile = LoginUtil.getMobile();
        if (mobile != null && !mobile.trim().isEmpty()) {
            member.setPhone(mobile.trim());
        }
        member.setUpdateBy(userId);
        member.setUpdateTime(new Date());
        familyMemberMapper.updateById(member);
        if ("管理员".equals(member.getRole())) {
            ensureFamilyAdminForMember(member);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unbindMember(Long memberId) throws Exception {
        FamilyMember member = familyMemberMapper.selectById(memberId);
        if (member == null) {
            throw new BusinessException(500, "成员不存在");
        }
        Family family = familyMapper.selectById(member.getFamilyId());
        if (family != null) {
            checkFamilyAdminAccess(family);
        }
        if ("管理员".equals(member.getRole())) {
            removeFamilyAdminForMember(member);
        }
        member.setUserId(null);
        member.setUpdateBy(LoginUtil.getUserId());
        member.setUpdateTime(new Date());
        familyMemberMapper.updateById(member);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FamilyMemberVO ensureBindCode(Long memberId) throws Exception {
        FamilyMember member = familyMemberMapper.selectById(memberId);
        if (member == null) {
            throw new BusinessException(500, "成员不存在");
        }
        if (member.getUserId() != null) {
            throw new BusinessException(500, "该成员已绑定用户，无需生成绑定码");
        }
        Family family = familyMapper.selectById(member.getFamilyId());
        if (family != null) {
            checkFamilyAdminAccess(family);
        }
        if (member.getBindCode() != null && !member.getBindCode().trim().isEmpty()) {
            FamilyMemberVO vo = new FamilyMemberVO();
            vo.setId(member.getId());
            vo.setFamilyId(member.getFamilyId());
            vo.setFamilyName(family != null ? family.getName() : null);
            vo.setName(member.getName());
            vo.setBindCode(member.getBindCode());
            vo.setRelation(member.getRelation());
            vo.setPhone(member.getPhone());
            vo.setRole(member.getRole());
            vo.setCreateTime(member.getCreateTime());
            return vo;
        }
        String bindCode;
        do {
            bindCode = java.util.UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8).toUpperCase();
        } while (familyMemberMapper.selectCount(
                Wrappers.<FamilyMember>lambdaQuery().eq(FamilyMember::getBindCode, bindCode)
        ) > 0);
        member.setBindCode(bindCode);
        member.setUpdateBy(LoginUtil.getUserId());
        member.setUpdateTime(new Date());
        familyMemberMapper.updateById(member);

        FamilyMemberVO vo = new FamilyMemberVO();
        vo.setId(member.getId());
        vo.setFamilyId(member.getFamilyId());
        vo.setFamilyName(family != null ? family.getName() : null);
        vo.setName(member.getName());
        vo.setBindCode(bindCode);
        vo.setRelation(member.getRelation());
        vo.setPhone(member.getPhone());
        vo.setRole(member.getRole());
        vo.setCreateTime(member.getCreateTime());
        return vo;
    }
}
