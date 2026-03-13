package com.memorial.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.memorial.system.entity.TombMessage;
import com.memorial.system.param.TombMessagePageParam;
import com.memorial.system.vo.TombMessageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TombMessageMapper extends BaseMapper<TombMessage> {

    IPage<TombMessageVO> getMessageList(@Param("page") Page page, @Param("param") TombMessagePageParam param);

    List<TombMessageVO> getRecentMessages(@Param("limit") int limit);
}
