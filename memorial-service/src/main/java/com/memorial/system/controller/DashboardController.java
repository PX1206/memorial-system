package com.memorial.system.controller;

import com.memorial.common.api.ApiResult;
import com.memorial.common.base.BaseController;
import com.memorial.system.service.DashboardService;
import com.memorial.system.vo.DashboardVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/dashboard")
@Api(value = "仪表盘API", tags = {"仪表盘"})
public class DashboardController extends BaseController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/stats")
    @ApiOperation(value = "获取仪表盘统计数据", response = DashboardVO.class)
    public ApiResult<DashboardVO> getStats() throws Exception {
        DashboardVO stats = dashboardService.getStats();
        return ApiResult.ok(stats);
    }
}
