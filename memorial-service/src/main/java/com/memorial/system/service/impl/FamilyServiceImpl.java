package com.memorial.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.memorial.common.base.BaseServiceImpl;
import com.memorial.common.exception.BusinessException;
import com.memorial.common.pagination.PageInfo;
import com.memorial.common.pagination.Paging;
import com.memorial.common.tool.LoginUtil;
import com.memorial.system.entity.Family;
import com.memorial.system.mapper.FamilyMapper;
import com.memorial.system.param.FamilyPageParam;
import com.memorial.system.param.FamilyParam;
import com.memorial.system.service.FamilyService;
import com.memorial.system.vo.FamilyVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FamilyServiceImpl extends BaseServiceImpl<FamilyMapper, Family> implements FamilyService {

    @Autowired
    private FamilyMapper familyMapper;

    @Override
    public Paging<FamilyVO> getFamilyPageList(FamilyPageParam param) throws Exception {
        Page<FamilyVO> page = new PageInfo<>(param);
        IPage<FamilyVO> iPage = familyMapper.getFamilyList(page, param);
        return new Paging<>(iPage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addFamily(FamilyParam param) throws Exception {
        Family family = new Family();
        family.setPid(param.getPid() != null ? param.getPid() : 0L);
        family.setName(param.getName());
        family.setDescription(param.getDescription());
        family.setPhone(param.getPhone());
        family.setAddress(param.getAddress());
        family.setFounderId(LoginUtil.getUserId());
        family.setFounderName(LoginUtil.getUserName());
        family.setMemberCount(1);
        family.setTombCount(0);
        family.setStatus(1);
        family.setCreateBy(LoginUtil.getUserId());
        family.setCreateTime(new Date());
        familyMapper.insert(family);
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
        return super.removeById(id);
    }

    @Override
    public List<FamilyVO> getFamilyTree() throws Exception {
        List<FamilyVO> all = familyMapper.getAllFamilyList();
        return buildTree(all, 0L);
    }

    private List<FamilyVO> buildTree(List<FamilyVO> list, Long parentId) {
        return list.stream()
                .filter(f -> Objects.equals(f.getPid(), parentId))
                .peek(f -> f.setChildren(buildTree(list, f.getId())))
                .collect(Collectors.toList());
    }
}
