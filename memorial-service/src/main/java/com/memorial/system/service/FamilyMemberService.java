package com.memorial.system.service;

import com.memorial.common.base.BaseService;
import com.memorial.common.pagination.Paging;
import com.memorial.system.entity.FamilyMember;
import com.memorial.system.param.FamilyMemberPageParam;
import com.memorial.system.param.FamilyMemberParam;
import com.memorial.system.vo.FamilyMemberVO;

public interface FamilyMemberService extends BaseService<FamilyMember> {

    Paging<FamilyMemberVO> getMemberPageList(FamilyMemberPageParam param) throws Exception;

    boolean addMember(FamilyMemberParam param) throws Exception;

    boolean updateMember(FamilyMemberParam param) throws Exception;

    boolean removeMember(Long id) throws Exception;
}
