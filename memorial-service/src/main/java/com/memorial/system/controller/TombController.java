package com.memorial.system.controller;

import com.memorial.common.api.ApiResult;
import com.memorial.common.base.BaseController;
import com.memorial.common.enums.OperationLogType;
import com.memorial.common.log.Module;
import com.memorial.common.log.OperationLog;
import com.memorial.common.pagination.Paging;
import com.memorial.system.param.TombPageParam;
import com.memorial.system.param.TombParam;
import com.memorial.system.service.TombService;
import com.memorial.system.vo.TombVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/tomb")
@Module("memorial")
@Api(value = "墓碑管理API", tags = {"墓碑管理"})
public class TombController extends BaseController {

    @Autowired
    private TombService tombService;

    @PostMapping("/getPageList")
    @ApiOperation(value = "墓碑分页列表", response = TombVO.class)
    public ApiResult<Paging<TombVO>> getPageList(@Validated @RequestBody TombPageParam param) throws Exception {
        Paging<TombVO> paging = tombService.getTombPageList(param);
        return ApiResult.ok(paging);
    }

    @GetMapping("/detail/{id}")
    @ApiOperation(value = "墓碑详情", response = TombVO.class)
    public ApiResult<TombVO> getDetail(@PathVariable("id") Long id) throws Exception {
        TombVO tombVO = tombService.getTomb(id);
        return ApiResult.ok(tombVO);
    }

    @PostMapping("/add")
    @OperationLog(name = "新增墓碑", type = OperationLogType.ADD)
    @ApiOperation(value = "新增墓碑", response = ApiResult.class)
    public ApiResult<Boolean> add(@Validated @RequestBody TombParam param) throws Exception {
        boolean flag = tombService.addTomb(param);
        return ApiResult.result(flag);
    }

    @PostMapping("/update")
    @OperationLog(name = "修改墓碑", type = OperationLogType.UPDATE)
    @ApiOperation(value = "修改墓碑", response = ApiResult.class)
    public ApiResult<Boolean> update(@Validated @RequestBody TombParam param) throws Exception {
        boolean flag = tombService.updateTomb(param);
        return ApiResult.result(flag);
    }

    @PostMapping("/delete/{id}")
    @OperationLog(name = "删除墓碑", type = OperationLogType.DELETE)
    @ApiOperation(value = "删除墓碑", response = ApiResult.class)
    public ApiResult<Boolean> delete(@PathVariable("id") Long id) throws Exception {
        boolean flag = tombService.deleteTomb(id);
        return ApiResult.result(flag);
    }
}
