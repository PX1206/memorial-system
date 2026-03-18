package com.memorial.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.memorial.common.base.BaseServiceImpl;
import com.memorial.common.exception.BusinessException;
import com.memorial.common.pagination.PageInfo;
import com.memorial.common.pagination.Paging;
import com.memorial.common.tool.LoginUtil;
import com.memorial.system.entity.Family;
import com.memorial.system.entity.FamilyMember;
import com.memorial.system.mapper.FamilyMapper;
import com.memorial.system.mapper.FamilyMemberMapper;
import com.memorial.system.param.FamilyPageParam;
import com.memorial.system.param.FamilyParam;
import com.memorial.system.service.FamilyService;
import com.memorial.system.vo.FamilyVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FamilyServiceImpl extends BaseServiceImpl<FamilyMapper, Family> implements FamilyService {

    @Autowired
    private FamilyMapper familyMapper;

    @Autowired
    private FamilyMemberMapper familyMemberMapper;

    @Override
    public Paging<FamilyVO> getFamilyPageList(FamilyPageParam param) throws Exception {
        param.setCurrentUserId(LoginUtil.getUserId());
        param.setIsAdmin(LoginUtil.isAdmin());
        Page<FamilyVO> page = new PageInfo<>(param);
        IPage<FamilyVO> iPage = familyMapper.getFamilyList(page, param);
        Paging<FamilyVO> paging = new Paging<>(iPage);
        if (LoginUtil.isAdmin() && paging.getRecords() != null) {
            paging.getRecords().forEach(vo -> vo.setMyRole("族长"));
        }
        return paging;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addFamily(FamilyParam param) throws Exception {
        Long pid = param.getPid() != null ? param.getPid() : 0L;
        if (pid != null && pid > 0) {
            Family parent = familyMapper.selectById(pid);
            if (parent != null) checkFamilyAdminAccess(parent);
        }
        Family family = new Family();
        family.setPid(pid);
        family.setName(param.getName());
        family.setDescription(param.getDescription());
        family.setPhone(param.getPhone());
        family.setAddress(param.getAddress());
        family.setFounderId(LoginUtil.getUserId());
        family.setFounderName(LoginUtil.getUserName());
        // 初始时尚未真正添加成员，这里从 0 开始，后续通过成员表维护
        family.setMemberCount(0);
        family.setTombCount(0);
        family.setStatus(1);
        // 生成简单的邀请码（长度 8，字母数字），尽量避免重复
        String inviteCode;
        do {
            inviteCode = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8).toUpperCase();
        } while (familyMapper.selectCount(
                Wrappers.<Family>lambdaQuery().eq(Family::getInviteCode, inviteCode)
        ) > 0);
        family.setInviteCode(inviteCode);

        family.setCreateBy(LoginUtil.getUserId());
        family.setCreateTime(new Date());
        familyMapper.insert(family);

        // 创建人自动成为该家族成员，角色为“族长”
        FamilyMember founder = new FamilyMember();
        founder.setFamilyId(family.getId());
        founder.setUserId(LoginUtil.getUserId());
        founder.setName(LoginUtil.getUserName());
        founder.setRelation("族长");
        founder.setRole("族长");
        founder.setCreateBy(LoginUtil.getUserId());
        founder.setCreateTime(new Date());
        familyMemberMapper.insert(founder);

        family.setMemberCount(1);
        familyMapper.updateById(family);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateFamily(FamilyParam param) throws Exception {
        if (param.getId() == null) {
            throw new BusinessException(500, "家族ID不能为空");
        }
        Family family = familyMapper.selectById(param.getId());
        if (family == null) {
            throw new BusinessException(500, "家族信息不存在");
        }
        checkFamilyAdminAccess(family);
        family.setPid(param.getPid() != null ? param.getPid() : 0L);
        family.setName(param.getName());
        family.setDescription(param.getDescription());
        family.setPhone(param.getPhone());
        family.setAddress(param.getAddress());
        family.setUpdateBy(LoginUtil.getUserId());
        family.setUpdateTime(new Date());
        familyMapper.updateById(family);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteFamily(Long id) throws Exception {
        Family family = familyMapper.selectById(id);
        if (family == null) {
            throw new BusinessException(500, "家族信息不存在");
        }
        checkFamilyAdminAccess(family);
        // 校验是否还有成员未移除
        Integer memberCount = familyMemberMapper.selectCount(
                Wrappers.<FamilyMember>lambdaQuery().eq(FamilyMember::getFamilyId, id)
        );
        if (memberCount != null && memberCount > 0) {
            throw new BusinessException(500, "请先移除家族下的所有成员，再删除家族");
        }
        return super.removeById(id);
    }

    @Override
    public List<FamilyVO> getFamilyTree() throws Exception {
        List<FamilyVO> all = familyMapper.getFamilyTreeForMember(LoginUtil.getUserId(), LoginUtil.isAdmin());
        return buildTree(all, 0L);
    }

    private List<FamilyVO> buildTree(List<FamilyVO> list, Long parentId) {
        return list.stream()
                .filter(f -> Objects.equals(f.getPid(), parentId))
                .peek(f -> f.setChildren(buildTree(list, f.getId())))
                .collect(Collectors.toList());
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

    /** 操作权限：仅族长或管理员可执行 */
    private void checkFamilyAdminAccess(Family family) {
        if (LoginUtil.isAdmin()) return;
        checkFamilyAccess(family);
        String role = getMemberRoleInFamily(family.getId());
        if (role == null || "成员".equals(role)) {
            throw new BusinessException(403, "仅族长或管理员可执行该操作");
        }
    }

    private String getMemberRoleInFamily(Long familyId) {
        Long userId = LoginUtil.getUserId();
        Family f = familyMapper.selectById(familyId);
        if (f != null && Objects.equals(f.getFounderId(), userId)) return "族长";
        return familyMemberMapper.getMemberRoleInFamily(userId, familyId);
    }
}
