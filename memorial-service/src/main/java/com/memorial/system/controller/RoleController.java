package com.memorial.system.controller;

import com.memorial.common.api.ApiResult;
import com.memorial.common.base.BaseController;
import com.memorial.common.enums.OperationLogType;
import com.memorial.common.log.Module;
import com.memorial.common.log.OperationLog;
import com.memorial.common.pagination.Paging;
import com.memorial.system.param.RoleMenuParam;
import com.memorial.system.param.RolePageParam;
import com.memorial.system.param.RoleParam;
import com.memorial.system.param.UserRoleParam;
import com.memorial.system.service.RoleService;
import com.memorial.system.vo.RoleVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/role")
@Module("memorial")
@Api(value = "角色API", tags = {"角色管理"})
public class RoleController extends BaseController {

    @Autowired
    private RoleService roleService;

    @PostMapping("/getPageList")
    @ApiOperation(value = "角色分页列表", response = RoleVO.class)
    public ApiResult<Paging<RoleVO>> getRolePageList(@Validated @RequestBody RolePageParam rolePageParam) throws Exception {
        Paging<RoleVO> paging = roleService.getRolePageList(rolePageParam);
        return ApiResult.ok(paging);
    }

    @GetMapping("/getAllRoles")
    @ApiOperation(value = "获取所有启用的角色", response = RoleVO.class)
    public ApiResult<List<RoleVO>> getAllRoles() throws Exception {
        List<RoleVO> roles = roleService.getAllRoles();
        return ApiResult.ok(roles);
    }

    @PostMapping("/add")
    @OperationLog(name = "新增角色", type = OperationLogType.ADD)
    @ApiOperation(value = "新增角色", response = ApiResult.class)
    public ApiResult<Boolean> addRole(@Validated @RequestBody RoleParam roleParam) throws Exception {
        boolean flag = roleService.addRole(roleParam);
        return ApiResult.result(flag);
    }

    @PostMapping("/update")
    @OperationLog(name = "修改角色", type = OperationLogType.UPDATE)
    @ApiOperation(value = "修改角色", response = ApiResult.class)
    public ApiResult<Boolean> updateRole(@Validated @RequestBody RoleParam roleParam) throws Exception {
        boolean flag = roleService.updateRole(roleParam);
        return ApiResult.result(flag);
    }

    @PostMapping("/delete/{id}")
    @OperationLog(name = "删除角色", type = OperationLogType.DELETE)
    @ApiOperation(value = "删除角色", response = ApiResult.class)
    public ApiResult<Boolean> deleteRole(@PathVariable("id") Long id) throws Exception {
        boolean flag = roleService.deleteRole(id);
        return ApiResult.result(flag);
    }

    @PostMapping("/toggleStatus/{id}")
    @OperationLog(name = "切换角色状态", type = OperationLogType.UPDATE)
    @ApiOperation(value = "切换角色状态", response = ApiResult.class)
    public ApiResult<Boolean> toggleStatus(@PathVariable("id") Long id) throws Exception {
        boolean flag = roleService.toggleStatus(id);
        return ApiResult.result(flag);
    }

    @PostMapping("/saveRoleMenus")
    @OperationLog(name = "保存角色权限", type = OperationLogType.UPDATE)
    @ApiOperation(value = "保存角色权限", response = ApiResult.class)
    public ApiResult<Boolean> saveRoleMenus(@Validated @RequestBody RoleMenuParam roleMenuParam) throws Exception {
        boolean flag = roleService.saveRoleMenus(roleMenuParam);
        return ApiResult.result(flag);
    }

    @GetMapping("/getRoleMenuIds/{roleId}")
    @ApiOperation(value = "获取角色权限菜单ID列表")
    public ApiResult<List<Long>> getRoleMenuIds(@PathVariable("roleId") Long roleId) throws Exception {
        List<Long> menuIds = roleService.getRoleMenuIds(roleId);
        return ApiResult.ok(menuIds);
    }

    @PostMapping("/saveUserRoles")
    @OperationLog(name = "保存用户角色", type = OperationLogType.UPDATE)
    @ApiOperation(value = "保存用户角色", response = ApiResult.class)
    public ApiResult<Boolean> saveUserRoles(@Validated @RequestBody UserRoleParam userRoleParam) throws Exception {
        boolean flag = roleService.saveUserRoles(userRoleParam);
        return ApiResult.result(flag);
    }

    @GetMapping("/getUserRoleIds/{userId}")
    @ApiOperation(value = "获取用户角色ID列表")
    public ApiResult<List<Long>> getUserRoleIds(@PathVariable("userId") Long userId) throws Exception {
        List<Long> roleIds = roleService.getUserRoleIds(userId);
        return ApiResult.ok(roleIds);
    }
}
