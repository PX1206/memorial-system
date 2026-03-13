package com.memorial.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.memorial.system.entity.Tomb;
import com.memorial.system.param.TombPageParam;
import com.memorial.system.vo.TombVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TombMapper extends BaseMapper<Tomb> {

    IPage<TombVO> getTombList(@Param("page") Page page, @Param("param") TombPageParam param);

    TombVO getTombVO(@Param("id") Long id);
}
