package com.memorial.system.controller;

import com.memorial.common.api.ApiResult;
import com.memorial.common.base.BaseController;
import com.memorial.common.log.Module;
import com.memorial.system.param.TombReminderParam;
import com.memorial.system.param.TombReminderToggleParam;
import com.memorial.system.service.TombReminderService;
import com.memorial.system.vo.TombReminderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/tomb/reminder")
@Module("memorial")
@Api(value = "墓碑个人提醒", tags = {"墓碑个人提醒"})
public class TombReminderController extends BaseController {

    @Autowired
    private TombReminderService tombReminderService;

    @GetMapping("/get")
    @ApiOperation(value = "当前用户对某墓碑的提醒配置", response = TombReminderVO.class)
    public ApiResult<TombReminderVO> get(@RequestParam("tombId") Long tombId) throws Exception {
        return ApiResult.ok(tombReminderService.getByTombId(tombId));
    }

    @PostMapping("/save")
    @ApiOperation(value = "保存或更新提醒配置")
    public ApiResult<Boolean> save(@Validated @RequestBody TombReminderParam param) throws Exception {
        return ApiResult.result(tombReminderService.saveOrUpdate(param));
    }

    @PostMapping("/toggle")
    @ApiOperation(value = "开启/关闭提醒")
    public ApiResult<Boolean> toggle(@Validated @RequestBody TombReminderToggleParam param) throws Exception {
        return ApiResult.result(tombReminderService.toggle(param));
    }
}
