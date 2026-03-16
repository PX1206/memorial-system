package com.memorial.system.controller;

import com.memorial.common.api.ApiResult;
import com.memorial.common.base.BaseController;
import com.memorial.common.enums.OperationLogType;
import com.memorial.common.log.Module;
import com.memorial.common.log.OperationLog;
import com.memorial.common.pagination.Paging;
import com.memorial.system.param.FamilyPageParam;
import com.memorial.system.param.FamilyParam;
import com.memorial.system.service.FamilyService;
import com.memorial.system.vo.FamilyVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/family")
@Module("memorial")
@Api(value = "家族管理API", tags = {"家族管理"})
public class FamilyController extends BaseController {

    @Autowired
    private FamilyService familyService;

    @PostMapping("/getPageList")
    @ApiOperation(value = "家族分页列表", response = FamilyVO.class)
    public ApiResult<Paging<FamilyVO>> getPageList(@Validated @RequestBody FamilyPageParam param) throws Exception {
        Paging<FamilyVO> paging = familyService.getFamilyPageList(param);
        return ApiResult.ok(paging);
    }

    @PostMapping("/add")
    @OperationLog(name = "新增家族", type = OperationLogType.ADD)
    @ApiOperation(value = "新增家族", response = ApiResult.class)
    public ApiResult<Boolean> add(@Validated @RequestBody FamilyParam param) throws Exception {
        boolean flag = familyService.addFamily(param);
        return ApiResult.result(flag);
    }

    @PostMapping("/update")
    @OperationLog(name = "修改家族", type = OperationLogType.UPDATE)
    @ApiOperation(value = "修改家族", response = ApiResult.class)
    public ApiResult<Boolean> update(@Validated @RequestBody FamilyParam param) throws Exception {
        boolean flag = familyService.updateFamily(param);
        return ApiResult.result(flag);
    }

    @PostMapping("/delete/{id}")
    @OperationLog(name = "删除家族", type = OperationLogType.DELETE)
    @ApiOperation(value = "删除家族", response = ApiResult.class)
    public ApiResult<Boolean> delete(@PathVariable("id") Long id) throws Exception {
        boolean flag = familyService.deleteFamily(id);
        return ApiResult.result(flag);
    }

    @GetMapping("/getTree")
    @ApiOperation(value = "获取家族树", response = FamilyVO.class)
    public ApiResult<List<FamilyVO>> getTree() throws Exception {
        List<FamilyVO> tree = familyService.getFamilyTree();
        return ApiResult.ok(tree);
    }
}
