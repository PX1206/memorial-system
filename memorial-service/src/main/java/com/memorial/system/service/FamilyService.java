package com.memorial.system.service;

import com.memorial.common.base.BaseService;
import com.memorial.common.pagination.Paging;
import com.memorial.system.entity.Family;
import com.memorial.system.param.FamilyPageParam;
import com.memorial.system.param.FamilyParam;
import com.memorial.system.vo.FamilyVO;

import java.util.List;

public interface FamilyService extends BaseService<Family> {

    Paging<FamilyVO> getFamilyPageList(FamilyPageParam param) throws Exception;

    boolean addFamily(FamilyParam param) throws Exception;

    boolean updateFamily(FamilyParam param) throws Exception;

    boolean deleteFamily(Long id) throws Exception;

    List<FamilyVO> getFamilyTree() throws Exception;

    /** 超级管理员指定某用户为管理员（仅 家族 层级） */
    boolean designateAdmin(Long familyId, Long userId) throws Exception;
}
