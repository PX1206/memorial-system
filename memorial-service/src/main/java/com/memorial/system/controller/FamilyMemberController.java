package com.memorial.system.controller;

import com.memorial.common.api.ApiResult;
import com.memorial.common.base.BaseController;
import com.memorial.common.enums.OperationLogType;
import com.memorial.common.log.Module;
import com.memorial.common.log.OperationLog;
import com.memorial.common.pagination.Paging;
import com.memorial.system.param.ApplyJoinFamilyParam;
import com.memorial.system.param.BindMemberParam;
import com.memorial.system.param.FamilyMemberPageParam;
import com.memorial.system.param.FamilyMemberParam;
import com.memorial.system.param.JoinFamilyByCodeParam;
import com.memorial.system.service.FamilyMemberApplyService;
import com.memorial.system.service.FamilyMemberService;
import com.memorial.system.vo.FamilyMemberApplyVO;
import com.memorial.system.vo.FamilyMemberVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/family/member")
@Module("memorial")
@Api(value = "家族成员管理API", tags = {"家族成员管理"})
public class FamilyMemberController extends BaseController {

    @Autowired
    private FamilyMemberService familyMemberService;

    @Autowired
    private FamilyMemberApplyService familyMemberApplyService;

    @GetMapping("/myRole/{familyId}")
    @ApiOperation(value = "获取当前用户在指定家族的角色（族长/管理员/成员）")
    public ApiResult<String> getMyRole(@PathVariable("familyId") Long familyId) throws Exception {
        String role = familyMemberService.getMyRoleInFamily(familyId);
        return ApiResult.ok(role);
    }

    @PostMapping("/getPageList")
    @ApiOperation(value = "成员分页列表", response = FamilyMemberVO.class)
    public ApiResult<Paging<FamilyMemberVO>> getPageList(@Validated @RequestBody FamilyMemberPageParam param) throws Exception {
        Paging<FamilyMemberVO> paging = familyMemberService.getMemberPageList(param);
        return ApiResult.ok(paging);
    }

    @PostMapping("/add")
    @OperationLog(name = "添加家族成员", type = OperationLogType.ADD)
    @ApiOperation(value = "添加成员，返回含 bindCode 用于生成二维码", response = FamilyMemberVO.class)
    public ApiResult<FamilyMemberVO> add(@Validated @RequestBody FamilyMemberParam param) throws Exception {
        FamilyMemberVO vo = familyMemberService.addMember(param);
        return ApiResult.ok(vo);
    }

    @PostMapping("/bindByCode")
    @OperationLog(name = "扫码绑定成员", type = OperationLogType.UPDATE)
    @ApiOperation(value = "扫码绑定：当前用户绑定到指定成员（一用户可绑定多成员）", response = ApiResult.class)
    public ApiResult<Boolean> bindByCode(@Validated @RequestBody BindMemberParam param) throws Exception {
        boolean flag = familyMemberService.bindUserToMember(param.getBindCode());
        return ApiResult.result(flag);
    }

    @PostMapping("/unbind/{id}")
    @OperationLog(name = "解绑成员", type = OperationLogType.UPDATE)
    @ApiOperation(value = "解绑成员：清除成员的关联用户", response = ApiResult.class)
    public ApiResult<Boolean> unbind(@PathVariable("id") Long id) throws Exception {
        boolean flag = familyMemberService.unbindMember(id);
        return ApiResult.result(flag);
    }

    @PostMapping("/ensureBindCode/{id}")
    @OperationLog(name = "生成成员绑定码", type = OperationLogType.UPDATE)
    @ApiOperation(value = "为成员生成绑定码（无则生成）", response = FamilyMemberVO.class)
    public ApiResult<FamilyMemberVO> ensureBindCode(@PathVariable("id") Long id) throws Exception {
        FamilyMemberVO vo = familyMemberService.ensureBindCode(id);
        return ApiResult.ok(vo);
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

    @PostMapping("/joinByCode")
    @OperationLog(name = "通过邀请码加入家族", type = OperationLogType.ADD)
    @ApiOperation(value = "通过邀请码加入家族（当前用户）", response = ApiResult.class)
    public ApiResult<Boolean> joinByCode(@Validated @RequestBody JoinFamilyByCodeParam param) throws Exception {
        boolean flag = familyMemberService.joinByInviteCode(param);
        return ApiResult.result(flag);
    }

    @PostMapping("/apply")
    @OperationLog(name = "申请加入家族", type = OperationLogType.ADD)
    @ApiOperation(value = "申请成为家族成员（当前用户，用于纪念页「仅家族成员可互动」时申请）", response = ApiResult.class)
    public ApiResult<Boolean> apply(@Validated @RequestBody ApplyJoinFamilyParam param) throws Exception {
        boolean flag = familyMemberApplyService.apply(param);
        return ApiResult.result(flag);
    }

    @GetMapping("/apply/list")
    @ApiOperation(value = "查询某家族的成员申请列表（待审核/全部）", response = FamilyMemberApplyVO.class)
    public ApiResult<List<FamilyMemberApplyVO>> listApply(
            @RequestParam Long familyId,
            @RequestParam(required = false) String status) throws Exception {
        List<FamilyMemberApplyVO> list = familyMemberApplyService.listByFamily(familyId, status);
        return ApiResult.ok(list);
    }

    @PostMapping("/apply/approve/{id}")
    @OperationLog(name = "通过家族成员申请", type = OperationLogType.UPDATE)
    @ApiOperation(value = "通过申请", response = ApiResult.class)
    public ApiResult<Boolean> approveApply(@PathVariable("id") Long id) throws Exception {
        boolean flag = familyMemberApplyService.approve(id);
        return ApiResult.result(flag);
    }

    @PostMapping("/apply/reject/{id}")
    @OperationLog(name = "拒绝家族成员申请", type = OperationLogType.UPDATE)
    @ApiOperation(value = "拒绝申请", response = ApiResult.class)
    public ApiResult<Boolean> rejectApply(@PathVariable("id") Long id, @RequestBody(required = false) Map<String, String> body) throws Exception {
        String reason = body != null ? body.get("reason") : null;
        boolean flag = familyMemberApplyService.reject(id, reason);
        return ApiResult.result(flag);
    }
}
