package com.memorial.system.service;

import com.memorial.common.pagination.Paging;
import com.memorial.system.param.FilePageParam;
import com.memorial.system.vo.FileVo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * 文件表 服务类
 *
 * @author Sakura
 * @since 2022-08-22
 */
public interface FileService {

    /**
     * 删除
     *
     * @param code
     * @return
     * @throws Exception
     */
    boolean deleteFile(String code) throws Exception;


    /**
     * 获取分页对象
     *
     * @param filePageParam
     * @return
     * @throws Exception
     */
    Paging<FileVo> getFilePageList(FilePageParam filePageParam) throws Exception;

    /**
     * 上传
     * @param file
     * @return
     * @throws Exception
     */
    String upload(MultipartFile file) throws Exception;

    /**
     * 下载
     *
     * @param code
     * @return
     * @throws Exception
     */
    void download(HttpServletResponse response, String code) throws Exception;

}
