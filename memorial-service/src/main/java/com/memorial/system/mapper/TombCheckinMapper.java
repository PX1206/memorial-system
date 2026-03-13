package com.memorial.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.memorial.system.entity.TombCheckin;
import com.memorial.system.param.TombCheckinPageParam;
import com.memorial.system.vo.TombCheckinVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TombCheckinMapper extends BaseMapper<TombCheckin> {

    IPage<TombCheckinVO> getCheckinList(@Param("page") Page page, @Param("param") TombCheckinPageParam param);

    List<TombCheckinVO> getRecentCheckins(@Param("limit") int limit);
}
