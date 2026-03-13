package com.memorial.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.memorial.system.entity.FamilyMember;
import com.memorial.system.param.FamilyMemberPageParam;
import com.memorial.system.vo.FamilyMemberVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FamilyMemberMapper extends BaseMapper<FamilyMember> {

    IPage<FamilyMemberVO> getMemberList(@Param("page") Page page, @Param("param") FamilyMemberPageParam param);
}
