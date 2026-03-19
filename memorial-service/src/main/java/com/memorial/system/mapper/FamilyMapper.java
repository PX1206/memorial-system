package com.memorial.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.memorial.system.entity.Family;
import com.memorial.system.param.FamilyPageParam;
import com.memorial.system.vo.FamilyVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FamilyMapper extends BaseMapper<Family> {

    IPage<FamilyVO> getFamilyList(@Param("page") Page page, @Param("param") FamilyPageParam param);

    List<FamilyVO> getAllFamilyList(@Param("currentUserId") Long currentUserId, @Param("isAdmin") Boolean isAdmin);

    List<FamilyVO> getFamilyTreeForMember(@Param("currentUserId") Long currentUserId, @Param("isAdmin") Boolean isAdmin);

    /**
     * 判断用户是否有权操作指定家庭（添加墓碑、编辑等）
     * 可操作范围：1.家族管理员对整个根树；2.普通用户仅限自己所在节点及其子孙节点
     * @return 1 表示有权限，0 表示无权限
     */
    int canUserOperateFamily(@Param("userId") Long userId, @Param("familyId") Long familyId);

    /**
     * 判断用户是否属于墓碑所属家族根下的任意家庭（同根家族成员可互动，如二伯家孩子给大伯家坟墓祭拜）
     * @return 1 表示是，0 表示否
     */
    int isUserInSameRootFamilyTree(@Param("userId") Long userId, @Param("tombFamilyId") Long tombFamilyId);

    /**
     * 按数据权限统计家族数量（管理员见全部，普通用户仅限有权限的家族）
     */
    long countByPermission(@Param("currentUserId") Long currentUserId, @Param("isAdmin") Boolean isAdmin);

    /**
     * 按数据权限统计可访问家族树内的去重用户数（用于非管理员时的「同族用户」展示）
     */
    long countDistinctUsersInAccessibleTree(@Param("currentUserId") Long currentUserId);
}
