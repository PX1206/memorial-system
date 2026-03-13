package com.memorial.system.controller;

import com.memorial.common.api.ApiResult;
import com.memorial.common.base.BaseController;
import com.memorial.common.enums.OperationLogType;
import com.memorial.common.log.Module;
import com.memorial.common.log.OperationLog;
import com.memorial.common.pagination.Paging;
import com.memorial.system.param.TombMessagePageParam;
import com.memorial.system.service.TombMessageService;
import com.memorial.system.vo.TombMessageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/tomb/message")
@Module("memorial")
@Api(value = "留言管理API", tags = {"留言管理"})
public class TombMessageController extends BaseController {

    @Autowired
    private TombMessageService tombMessageService;

    @PostMapping("/getPageList")
    @ApiOperation(value = "留言分页列表", response = TombMessageVO.class)
    public ApiResult<Paging<TombMessageVO>> getPageList(@Validated @RequestBody TombMessagePageParam param) throws Exception {
        Paging<TombMessageVO> paging = tombMessageService.getMessagePageList(param);
        return ApiResult.ok(paging);
    }

    @PostMapping("/approve/{id}")
    @OperationLog(name = "审核通过留言", type = OperationLogType.UPDATE)
    @ApiOperation(value = "审核通过", response = ApiResult.class)
    public ApiResult<Boolean> approve(@PathVariable("id") Long id) throws Exception {
        boolean flag = tombMessageService.approveMessage(id);
        return ApiResult.result(flag);
    }

    @PostMapping("/reject/{id}")
    @OperationLog(name = "拒绝留言", type = OperationLogType.UPDATE)
    @ApiOperation(value = "拒绝留言", response = ApiResult.class)
    public ApiResult<Boolean> reject(@PathVariable("id") Long id, @RequestBody(required = false) Map<String, String> body) throws Exception {
        String reason = body != null ? body.get("reason") : null;
        boolean flag = tombMessageService.rejectMessage(id, reason);
        return ApiResult.result(flag);
    }

    @PostMapping("/delete/{id}")
    @OperationLog(name = "删除留言", type = OperationLogType.DELETE)
    @ApiOperation(value = "删除留言", response = ApiResult.class)
    public ApiResult<Boolean> delete(@PathVariable("id") Long id) throws Exception {
        boolean flag = tombMessageService.deleteMessage(id);
        return ApiResult.result(flag);
    }
}
