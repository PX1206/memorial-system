package com.memorial.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.memorial.common.base.BaseServiceImpl;
import com.memorial.common.exception.BusinessException;
import com.memorial.common.pagination.PageInfo;
import com.memorial.common.pagination.Paging;
import com.memorial.common.tool.LoginUtil;
import com.memorial.system.entity.Family;
import com.memorial.system.entity.FamilyAdmin;
import com.memorial.system.entity.FamilyMember;
import com.memorial.system.mapper.FamilyAdminMapper;
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

    @Autowired
    private FamilyAdminMapper familyAdminMapper;

    @Override
    public Paging<FamilyVO> getFamilyPageList(FamilyPageParam param) throws Exception {
        param.setCurrentUserId(LoginUtil.getUserId());
        param.setIsAdmin(LoginUtil.isAdmin());
        Page<FamilyVO> page = new PageInfo<>(param);
        IPage<FamilyVO> iPage = familyMapper.getFamilyList(page, param);
        Paging<FamilyVO> paging = new Paging<>(iPage);
        if (LoginUtil.isAdmin() && paging.getRecords() != null) {
            paging.getRecords().forEach(vo -> vo.setMyRole("admin"));
        }
        return paging;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addFamily(FamilyParam param) throws Exception {
        String type = param.getType() != null ? param.getType().trim() : "家族";
        Long pid = param.getPid() != null ? param.getPid() : 0L;
        Long rootId = null;

        if ("家庭".equals(type) || "支族".equals(type)) {
            if (pid == null || pid <= 0) {
                throw new BusinessException(500, "创建家庭或支族时必须选择上级");
            }
            Family parent = familyMapper.selectById(pid);
            if (parent == null) throw new BusinessException(500, "上级不存在");
            checkCanCreateSubFamily(parent);
            rootId = parent.getRootId() != null ? parent.getRootId() : parent.getId();
        }

        Family family = new Family();
        family.setPid(pid);
        family.setType(type);
        family.setName(param.getName());
        family.setDescription(param.getDescription());
        family.setPhone(param.getPhone());
        family.setAddress(param.getAddress());
        family.setFounderId(LoginUtil.getUserId());
        family.setFounderName(LoginUtil.getUserName());
        family.setChiefId(param.getChiefId());
        family.setMemberCount(0);
        family.setTombCount(0);
        family.setStatus(1);

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

        if ("家族".equals(type)) {
            family.setRootId(family.getId());
            familyMapper.updateById(family);
            FamilyAdmin admin = new FamilyAdmin();
            admin.setFamilyId(family.getId());
            admin.setUserId(LoginUtil.getUserId());
            admin.setRole("super_admin");
            admin.setCreateBy(LoginUtil.getUserId());
            admin.setCreateTime(new Date());
            familyAdminMapper.insert(admin);
        } else {
            family.setRootId(rootId);
            familyMapper.updateById(family);
        }
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
        family.setType(param.getType());
        family.setChiefId(param.getChiefId());
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

    /** 新增下级家族/家庭：当前用户需为自己所在的家族或家庭节点创建，成员即可 */
    private void checkCanCreateSubFamily(Family parent) {
        if (LoginUtil.isAdmin()) return;
        Long userId = LoginUtil.getUserId();
        if (Objects.equals(parent.getFounderId(), userId)) return;
        Long rootId = parent.getRootId() != null ? parent.getRootId() : parent.getId();
        String adminRole = familyAdminMapper.getRoleInFamily(rootId, userId);
        if ("super_admin".equals(adminRole) || "admin".equals(adminRole)) return;
        if (Objects.equals(parent.getChiefId(), userId)) return;
        Integer count = familyMemberMapper.selectCount(Wrappers.<FamilyMember>lambdaQuery()
                .eq(FamilyMember::getFamilyId, parent.getId())
                .eq(FamilyMember::getUserId, userId)
                .eq(FamilyMember::getDelFlag, false));
        if (count == null || count <= 0) {
            throw new BusinessException(403, "只能在自己所在的家族或家庭下创建下级");
        }
    }

    /** 操作权限：超级管理员/管理员/族长可执行；普通成员也可执行（添加成员、创建下级等） */
    private void checkFamilyAdminAccess(Family family) {
        if (LoginUtil.isAdmin()) return;
        Long rootId = family.getRootId() != null ? family.getRootId() : family.getId();
        String adminRole = familyAdminMapper.getRoleInFamily(rootId, LoginUtil.getUserId());
        if ("super_admin".equals(adminRole) || "admin".equals(adminRole)) return;
        if (Objects.equals(family.getChiefId(), LoginUtil.getUserId())) return;
        checkFamilyAccess(family);
        // 通过 checkFamilyAccess 表示已是创始人或成员，允许操作
    }

    /**
     * 指定家族管理员。超级管理员可将某用户设为管理员，管理员拥有对当前根家族及所有下级分支的完整权限（递归包含所有家庭/支族）。
     * 传入任意家族节点 ID 均可，内部会解析到根家族后写入 family_admin。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean designateAdmin(Long familyId, Long userId) throws Exception {
        Family family = familyMapper.selectById(familyId);
        if (family == null) throw new BusinessException(500, "家族不存在");
        Long rootId = family.getRootId() != null ? family.getRootId() : family.getId();
        Family rootFamily = familyMapper.selectById(rootId);
        if (rootFamily == null || !"家族".equals(rootFamily.getType())) {
            throw new BusinessException(500, "根家族不存在");
        }
        String myRole = familyAdminMapper.getRoleInFamily(rootId, LoginUtil.getUserId());
        if (!"super_admin".equals(myRole)) {
            throw new BusinessException(403, "仅超级管理员可指定管理员");
        }
        FamilyAdmin existing = familyAdminMapper.selectOne(
                Wrappers.<FamilyAdmin>lambdaQuery()
                        .eq(FamilyAdmin::getFamilyId, rootId)
                        .eq(FamilyAdmin::getUserId, userId));
        if (existing != null) {
            if ("super_admin".equals(existing.getRole())) {
                throw new BusinessException(500, "不能修改超级管理员");
            }
            existing.setRole("admin");
            existing.setCreateBy(LoginUtil.getUserId());
            existing.setCreateTime(new Date());
            familyAdminMapper.updateById(existing);
        } else {
            FamilyAdmin admin = new FamilyAdmin();
            admin.setFamilyId(rootId);
            admin.setUserId(userId);
            admin.setRole("admin");
            admin.setCreateBy(LoginUtil.getUserId());
            admin.setCreateTime(new Date());
            familyAdminMapper.insert(admin);
        }
        return true;
    }

    private String getMemberRoleInFamily(Long familyId) {
        Long userId = LoginUtil.getUserId();
        Family f = familyMapper.selectById(familyId);
        if (f == null) return null;
        Long rootId = f.getRootId() != null ? f.getRootId() : f.getId();
        String adminRole = familyAdminMapper.getRoleInFamily(rootId, userId);
        if ("super_admin".equals(adminRole) || "admin".equals(adminRole)) return "admin";
        if (Objects.equals(f.getChiefId(), userId)) return "chief";
        return familyMemberMapper.getMemberRoleInFamily(userId, familyId);
    }
}
