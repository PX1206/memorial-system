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

    /**
     * 统计某用户某墓碑当日打卡次数（用于限流：每天每用户每墓碑最多3次）
     */
    int countTodayByTombAndUser(@Param("tombId") Long tombId, @Param("userId") Long userId);

    /**
     * 按数据权限获取最近打卡记录（仅含用户有权限的墓碑）
     */
    List<TombCheckinVO> getRecentCheckinsWithPermission(@Param("limit") int limit, @Param("currentUserId") Long currentUserId, @Param("isAdmin") Boolean isAdmin);
}
