package com.memorial.system.service;

import com.memorial.common.exception.BusinessException;
import com.memorial.common.tool.LoginUtil;
import com.memorial.system.entity.Tomb;
import com.memorial.system.mapper.FamilyMapper;
import com.memorial.system.mapper.TombMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 墓碑访问权限校验（与家族管理逻辑一致），独立组件用于避免 TombService 与 TombStoryService 循环依赖
 */
@Component
public class TombAccessChecker {

    @Autowired
    private TombMapper tombMapper;
    @Autowired
    private FamilyMapper familyMapper;

    /** 校验当前用户是否有权操作指定墓碑，无权限则抛异常 */
    public void checkAccess(Long tombId) {
        Tomb tomb = tombMapper.selectById(tombId);
        if (tomb == null) {
            throw new BusinessException(500, "墓碑信息不存在");
        }
        checkAccess(tomb);
    }

    /** 校验当前用户是否有权操作指定墓碑，无权限则抛异常
     * 规则：系统管理员全部；家族管理员整棵根树；普通用户仅限所在节点及子孙下的墓碑 */
    public void checkAccess(Tomb tomb) {
        if (LoginUtil.isAdmin()) return;
        Long userId = LoginUtil.getUserId();
        if (tomb.getFamilyId() == null) {
            if (Objects.equals(tomb.getUserId(), userId) || Objects.equals(tomb.getCreateBy(), userId)) return;
            throw new BusinessException(403, "无权限操作该墓碑");
        }
        if (familyMapper.canUserOperateFamily(userId, tomb.getFamilyId()) <= 0) {
            throw new BusinessException(403, "无权限操作该墓碑");
        }
    }
}
