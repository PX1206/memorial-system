package com.memorial.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.memorial.system.entity.Menu;
import com.memorial.system.vo.MenuVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MenuMapper extends BaseMapper<Menu> {

    List<MenuVO> getAllMenus();

    List<String> getPermissionsByUserId(@Param("userId") Long userId);

    List<MenuVO> getMenusByUserId(@Param("userId") Long userId);
}
