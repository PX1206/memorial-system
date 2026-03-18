package com.memorial.system.service;

import com.memorial.common.base.BaseService;
import com.memorial.common.pagination.Paging;
import com.memorial.system.entity.FamilyMember;
import com.memorial.system.param.FamilyMemberPageParam;
import com.memorial.system.param.FamilyMemberParam;
import com.memorial.system.param.JoinFamilyByCodeParam;
import com.memorial.system.vo.FamilyMemberVO;

public interface FamilyMemberService extends BaseService<FamilyMember> {

    Paging<FamilyMemberVO> getMemberPageList(FamilyMemberPageParam param) throws Exception;

    boolean addMember(FamilyMemberParam param) throws Exception;

    boolean updateMember(FamilyMemberParam param) throws Exception;

    boolean removeMember(Long id) throws Exception;

    /**
     * 通过邀请码加入家族（当前登录用户）
     */
    boolean joinByInviteCode(JoinFamilyByCodeParam param) throws Exception;

    /** 获取当前用户在指定家族的角色：族长/管理员/成员，非成员返回 null */
    String getMyRoleInFamily(Long familyId) throws Exception;
}
