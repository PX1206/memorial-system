package com.memorial.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.memorial.system.entity.FamilyAdmin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 家族管理员（超级管理员/管理员）
 */
@Mapper
public interface FamilyAdminMapper extends BaseMapper<FamilyAdmin> {

    String getRoleInFamily(@Param("rootFamilyId") Long rootFamilyId, @Param("userId") Long userId);
}
