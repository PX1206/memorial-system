package com.memorial.system.controller;

import com.memorial.common.api.ApiResult;
import com.memorial.common.base.BaseController;
import com.memorial.common.enums.OperationLogType;
import com.memorial.common.log.Module;
import com.memorial.common.log.OperationLog;
import com.memorial.system.param.TombStoryParam;
import com.memorial.system.service.TombStoryService;
import com.memorial.system.vo.TombStoryVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/tomb/story")
@Module("memorial")
@Api(value = "墓碑事迹管理API", tags = {"墓碑事迹管理"})
public class TombStoryController extends BaseController {

    @Autowired
    private TombStoryService tombStoryService;

    @GetMapping("/list/{tombId}")
    @ApiOperation(value = "获取某墓碑的事迹列表", response = TombStoryVO.class)
    public ApiResult<List<TombStoryVO>> list(@PathVariable("tombId") Long tombId) throws Exception {
        List<TombStoryVO> list = tombStoryService.listByTombId(tombId);
        return ApiResult.ok(list);
    }

    @PostMapping("/add")
    @OperationLog(name = "新增墓碑事迹", type = OperationLogType.ADD)
    @ApiOperation(value = "新增墓碑事迹", response = ApiResult.class)
    public ApiResult<Boolean> add(@Validated @RequestBody TombStoryParam param) throws Exception {
        boolean flag = tombStoryService.addStory(param);
        return ApiResult.result(flag);
    }

    @PostMapping("/update")
    @OperationLog(name = "修改墓碑事迹", type = OperationLogType.UPDATE)
    @ApiOperation(value = "修改墓碑事迹", response = ApiResult.class)
    public ApiResult<Boolean> update(@Validated @RequestBody TombStoryParam param) throws Exception {
        boolean flag = tombStoryService.updateStory(param);
        return ApiResult.result(flag);
    }

    @PostMapping("/delete/{id}")
    @OperationLog(name = "删除墓碑事迹", type = OperationLogType.DELETE)
    @ApiOperation(value = "删除墓碑事迹", response = ApiResult.class)
    public ApiResult<Boolean> delete(@PathVariable("id") Long id) throws Exception {
        boolean flag = tombStoryService.deleteStory(id);
        return ApiResult.result(flag);
    }
}

