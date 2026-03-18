package com.memorial.system.service;

import com.memorial.system.param.ApplyJoinFamilyParam;
import com.memorial.system.vo.FamilyMemberApplyVO;

import java.util.List;

/**
 * 家族成员申请：用户申请加入家族，族长/管理员审核
 */
public interface FamilyMemberApplyService {

    /**
     * 当前用户申请加入某家族
     */
    boolean apply(ApplyJoinFamilyParam param) throws Exception;

    /**
     * 查询某家族的待审核/全部申请（族长或管理员可查）
     */
    List<FamilyMemberApplyVO> listByFamily(Long familyId, String status) throws Exception;

    /**
     * 通过申请：创建家族成员并更新申请状态
     */
    boolean approve(Long applyId) throws Exception;

    /**
     * 拒绝申请
     */
    boolean reject(Long applyId, String rejectReason) throws Exception;
}
