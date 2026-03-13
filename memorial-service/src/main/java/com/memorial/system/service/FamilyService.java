package com.memorial.system.service;

import com.memorial.common.base.BaseService;
import com.memorial.common.pagination.Paging;
import com.memorial.system.entity.Family;
import com.memorial.system.param.FamilyPageParam;
import com.memorial.system.param.FamilyParam;
import com.memorial.system.vo.FamilyVO;

public interface FamilyService extends BaseService<Family> {

    Paging<FamilyVO> getFamilyPageList(FamilyPageParam param) throws Exception;

    boolean addFamily(FamilyParam param) throws Exception;

    boolean updateFamily(FamilyParam param) throws Exception;

    boolean deleteFamily(Long id) throws Exception;
}
