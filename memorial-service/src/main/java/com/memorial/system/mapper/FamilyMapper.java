package com.memorial.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.memorial.system.entity.Family;
import com.memorial.system.param.FamilyPageParam;
import com.memorial.system.vo.FamilyVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FamilyMapper extends BaseMapper<Family> {

    IPage<FamilyVO> getFamilyList(@Param("page") Page page, @Param("param") FamilyPageParam param);
}
