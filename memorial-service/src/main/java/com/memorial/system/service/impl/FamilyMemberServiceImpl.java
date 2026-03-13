package com.memorial.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import com.memorial.system.service.FamilyMemberService;
import com.memorial.system.vo.FamilyMemberVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@Service
public class FamilyMemberServiceImpl extends BaseServiceImpl<FamilyMemberMapper, FamilyMember> implements FamilyMemberService {

    @Autowired
    private FamilyMemberMapper familyMemberMapper;

    @Autowired
    private FamilyMapper familyMapper;

    @Override
    public Paging<FamilyMemberVO> getMemberPageList(FamilyMemberPageParam param) throws Exception {
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

        FamilyMember member = new FamilyMember();
        member.setFamilyId(param.getFamilyId());
        member.setName(param.getName());
        member.setRelation(param.getRelation());
        member.setPhone(param.getPhone());
        member.setRole(param.getRole() != null ? param.getRole() : "成员");
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
        member.setName(param.getName());
        member.setRelation(param.getRelation());
        member.setPhone(param.getPhone());
        member.setRole(param.getRole());
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
        super.removeById(id);

        Family family = familyMapper.selectById(member.getFamilyId());
        if (family != null && family.getMemberCount() > 0) {
            family.setMemberCount(family.getMemberCount() - 1);
            familyMapper.updateById(family);
        }
        return true;
    }
}
