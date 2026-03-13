package com.memorial.system.controller;

import com.memorial.common.api.ApiResult;
import com.memorial.system.service.TombCheckinService;
import com.memorial.system.service.TombMessageService;
import com.memorial.system.service.TombService;
import com.memorial.system.vo.TombVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 游客端公开接口（不需要登录）
 */
@Slf4j
@RestController
@RequestMapping("/open/memorial")
@Api(value = "游客纪念页API", tags = {"游客纪念页"})
public class MemorialController {

    @Autowired
    private TombService tombService;

    @Autowired
    private TombMessageService tombMessageService;

    @Autowired
    private TombCheckinService tombCheckinService;

    @GetMapping("/detail/{id}")
    @ApiOperation(value = "获取墓碑详情（游客端）", response = TombVO.class)
    public ApiResult<TombVO> getDetail(@PathVariable("id") Long id) throws Exception {
        TombVO tombVO = tombService.getTombDetail(id);
        return ApiResult.ok(tombVO);
    }

    @PostMapping("/message")
    @ApiOperation(value = "提交留言（游客端）")
    public ApiResult<Boolean> addMessage(@RequestBody Map<String, Object> body) throws Exception {
        Long tombId = Long.valueOf(body.get("tombId").toString());
        String visitorName = body.getOrDefault("visitorName", "匿名").toString();
        String content = body.get("content").toString();
        boolean flag = tombMessageService.addMessage(tombId, visitorName, content);
        return ApiResult.result(flag);
    }

    @PostMapping("/checkin")
    @ApiOperation(value = "打卡（游客端）")
    public ApiResult<Boolean> checkin(@RequestBody Map<String, Object> body, HttpServletRequest request) throws Exception {
        Long tombId = Long.valueOf(body.get("tombId").toString());
        String visitorName = body.getOrDefault("visitorName", "匿名").toString();
        String type = body.getOrDefault("type", "扫码访问").toString();
        boolean flag = tombCheckinService.addCheckin(tombId, visitorName, type, request);
        return ApiResult.result(flag);
    }
}
