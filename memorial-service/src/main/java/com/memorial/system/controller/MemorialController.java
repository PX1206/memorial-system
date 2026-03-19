package com.memorial.system.controller;

import com.memorial.common.api.ApiResult;
import com.memorial.common.exception.BusinessException;
import com.memorial.common.pagination.Paging;
import com.memorial.common.redis.RedisUtil;
import com.memorial.common.tool.LoginUtil;
import com.memorial.common.tool.StringUtil;
import com.memorial.common.tool.TokenUtil;
import com.memorial.system.service.TombCheckinService;
import com.memorial.system.service.TombMessageService;
import com.memorial.system.service.TombService;
import com.memorial.system.param.TombMessagePageParam;
import com.memorial.system.param.TombCheckinPageParam;
import com.memorial.system.vo.TombMessageVO;
import com.memorial.system.vo.TombCheckinVO;
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

    @Autowired
    private RedisUtil redisUtil;

    @GetMapping("/detail/{id}")
    @ApiOperation(value = "获取墓碑详情（游客端，按ID）", response = TombVO.class)
    public ApiResult<TombVO> getDetail(@PathVariable("id") Long id) throws Exception {
        TombVO tombVO = tombService.getTombDetail(id);
        return ApiResult.ok(tombVO);
    }

    @GetMapping("/detail/code/{code}")
    @ApiOperation(value = "获取墓碑详情（游客端，按二维码标识）", response = TombVO.class)
    public ApiResult<TombVO> getDetailByCode(@PathVariable("code") String code) throws Exception {
        TombVO tombVO = tombService.getTombDetailByCode(code);
        return ApiResult.ok(tombVO);
    }

    @PostMapping("/message")
    @ApiOperation(value = "提交留言（游客端）")
    public ApiResult<Boolean> addMessage(@RequestBody Map<String, Object> body) throws Exception {
        String token = TokenUtil.getToken();
        if (StringUtil.isBlank(token) || !redisUtil.hasKey(token)) {
            throw new BusinessException(401, "请重新登录！");
        }
        Long tombId = Long.valueOf(body.get("tombId").toString());
        String visitorName = resolveVisitorName(body.get("visitorName"));
        String content = body.get("content").toString();
        Long userId = LoginUtil.getUserId();
        LoginUtil.refreshToken();
        boolean flag = tombMessageService.addMessageAuth(tombId, userId, visitorName, content);
        return ApiResult.result(flag);
    }

    @PostMapping("/checkin")
    @ApiOperation(value = "打卡（游客端）")
    public ApiResult<Boolean> checkin(@RequestBody Map<String, Object> body, HttpServletRequest request) throws Exception {
        String token = TokenUtil.getToken();
        if (StringUtil.isBlank(token) || !redisUtil.hasKey(token)) {
            throw new BusinessException(401, "请重新登录！");
        }
        Long tombId = Long.valueOf(body.get("tombId").toString());
        String visitorName = resolveVisitorName(body.get("visitorName"));
        String type = body.getOrDefault("type", "扫码访问").toString();
        Long userId = LoginUtil.getUserId();
        LoginUtil.refreshToken();
        boolean flag = tombCheckinService.addCheckinAuth(tombId, userId, visitorName, type, request);
        return ApiResult.result(flag);
    }

    @PostMapping("/message/getPageList")
    @ApiOperation(value = "留言分页列表（游客端：他人仅看已审核，本人可见自己的全部留言）", response = TombMessageVO.class)
    public ApiResult<Paging<TombMessageVO>> getMessagePageList(@RequestBody TombMessagePageParam param) throws Exception {
        param.setStatus("approved");
        param.setForPublicMemorial(true); // 纪念页公开：按 tombId 展示，跳过家族权限过滤
        String token = TokenUtil.getToken();
        if (!StringUtil.isBlank(token) && redisUtil.hasKey(token)) {
            try {
                param.setCurrentUserId(LoginUtil.getUserId());
                LoginUtil.refreshToken();
            } catch (Exception e) {
                // 忽略用户信息异常，按未登录处理
            }
        }
        Paging<TombMessageVO> paging = tombMessageService.getMessagePageList(param);
        return ApiResult.ok(paging);
    }

    @GetMapping("/message/getPageList")
    @ApiOperation(value = "留言分页列表（游客端，仅已审核，GET，同上规则）", response = TombMessageVO.class)
    public ApiResult<Paging<TombMessageVO>> getMessagePageListGet(TombMessagePageParam param) throws Exception {
        param.setStatus("approved");
        param.setForPublicMemorial(true);
        String token = TokenUtil.getToken();
        if (!StringUtil.isBlank(token) && redisUtil.hasKey(token)) {
            try {
                param.setCurrentUserId(LoginUtil.getUserId());
                LoginUtil.refreshToken();
            } catch (Exception e) {
                // 忽略
            }
        }
        Paging<TombMessageVO> paging = tombMessageService.getMessagePageList(param);
        return ApiResult.ok(paging);
    }

    @PostMapping("/checkin/getPageList")
    @ApiOperation(value = "打卡记录分页列表（游客端）", response = TombCheckinVO.class)
    public ApiResult<Paging<TombCheckinVO>> getCheckinPageList(@RequestBody TombCheckinPageParam param) throws Exception {
        param.setForPublicMemorial(true); // 纪念页公开：按 tombId 展示，跳过家族权限过滤
        Paging<TombCheckinVO> paging = tombCheckinService.getCheckinPageList(param);
        return ApiResult.ok(paging);
    }

    @GetMapping("/checkin/getPageList")
    @ApiOperation(value = "打卡记录分页列表（游客端，GET）", response = TombCheckinVO.class)
    public ApiResult<Paging<TombCheckinVO>> getCheckinPageListGet(TombCheckinPageParam param) throws Exception {
        param.setForPublicMemorial(true);
        Paging<TombCheckinVO> paging = tombCheckinService.getCheckinPageList(param);
        return ApiResult.ok(paging);
    }

    /** 留言/献花显示名：明确传「匿名」用匿名，否则用当前用户昵称 */
    private String resolveVisitorName(Object visitorName) {
        if (visitorName != null) {
            String s = visitorName.toString().trim();
            if ("匿名".equals(s)) return "匿名";
        }
        return LoginUtil.getNickname();
    }
}
