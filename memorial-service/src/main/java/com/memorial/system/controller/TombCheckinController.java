package com.memorial.system.controller;

import com.memorial.common.api.ApiResult;
import com.memorial.common.base.BaseController;
import com.memorial.common.pagination.Paging;
import com.memorial.system.param.TombCheckinPageParam;
import com.memorial.system.service.TombCheckinService;
import com.memorial.system.vo.TombCheckinVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/tomb/checkin")
@Api(value = "打卡记录API", tags = {"打卡记录"})
public class TombCheckinController extends BaseController {

    @Autowired
    private TombCheckinService tombCheckinService;

    @PostMapping("/getPageList")
    @ApiOperation(value = "打卡记录分页列表", response = TombCheckinVO.class)
    public ApiResult<Paging<TombCheckinVO>> getPageList(@Validated @RequestBody TombCheckinPageParam param) throws Exception {
        Paging<TombCheckinVO> paging = tombCheckinService.getCheckinPageList(param);
        return ApiResult.ok(paging);
    }
}
