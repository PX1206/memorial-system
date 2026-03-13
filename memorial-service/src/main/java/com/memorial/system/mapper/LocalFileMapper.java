package com.memorial.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.memorial.system.entity.LocalFile;
import com.memorial.system.param.FilePageParam;
import com.memorial.system.vo.FileVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LocalFileMapper extends BaseMapper<LocalFile> {

	IPage<FileVo> getFileList(@Param("page") Page page, @Param("localFilePath") String localFilePath,
							  @Param("param") FilePageParam param);

}
