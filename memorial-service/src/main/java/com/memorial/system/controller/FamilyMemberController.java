package com.memorial.system.controller;

import com.memorial.common.api.ApiResult;
import com.memorial.common.base.BaseController;
import com.memorial.common.enums.OperationLogType;
import com.memorial.common.log.Module;
import com.memorial.common.log.OperationLog;
import com.memorial.common.pagination.Paging;
import com.memorial.system.param.FamilyMemberPageParam;
import com.memorial.system.param.FamilyMemberParam;
import com.memorial.system.service.FamilyMemberService;
import com.memorial.system.vo.FamilyMemberVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/family/member")
@Module("memorial")
@Api(value = "家族成员管理API", tags = {"家族成员管理"})
public class FamilyMemberController extends BaseController {

    @Autowired
    private FamilyMemberService familyMemberService;

    @PostMapping("/getPageList")
    @ApiOperation(value = "成员分页列表", response = FamilyMemberVO.class)
    public ApiResult<Paging<FamilyMemberVO>> getPageList(@Validated @RequestBody FamilyMemberPageParam param) throws Exception {
        Paging<FamilyMemberVO> paging = familyMemberService.getMemberPageList(param);
        return ApiResult.ok(paging);
    }

    @PostMapping("/add")
    @OperationLog(name = "添加家族成员", type = OperationLogType.ADD)
    @ApiOperation(value = "添加成员", response = ApiResult.class)
    public ApiResult<Boolean> add(@Validated @RequestBody FamilyMemberParam param) throws Exception {
        boolean flag = familyMemberService.addMember(param);
        return ApiResult.result(flag);
    }

    @PostMapping("/update")
    @OperationLog(name = "修改家族成员", type = OperationLogType.UPDATE)
    @ApiOperation(value = "修改成员", response = ApiResult.class)
    public ApiResult<Boolean> update(@Validated @RequestBody FamilyMemberParam param) throws Exception {
        boolean flag = familyMemberService.updateMember(param);
        return ApiResult.result(flag);
    }

    @PostMapping("/delete/{id}")
    @OperationLog(name = "移除家族成员", type = OperationLogType.DELETE)
    @ApiOperation(value = "移除成员", response = ApiResult.class)
    public ApiResult<Boolean> delete(@PathVariable("id") Long id) throws Exception {
        boolean flag = familyMemberService.removeMember(id);
        return ApiResult.result(flag);
    }
}
