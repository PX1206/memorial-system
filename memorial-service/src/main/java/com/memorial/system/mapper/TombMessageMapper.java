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

    /**
     * 按数据权限统计留言数量（仅统计有权限的墓碑下的留言）
     */
    long countByPermission(@Param("currentUserId") Long currentUserId, @Param("isAdmin") Boolean isAdmin);

    /**
     * 按数据权限获取最新留言（仅含已通过审核且墓碑有权限的留言）
     */
    List<TombMessageVO> getRecentMessagesWithPermission(@Param("limit") int limit, @Param("currentUserId") Long currentUserId, @Param("isAdmin") Boolean isAdmin);
}
