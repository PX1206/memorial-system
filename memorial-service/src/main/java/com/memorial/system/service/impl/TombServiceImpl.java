package com.memorial.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.memorial.common.base.BaseServiceImpl;
import com.memorial.common.exception.BusinessException;
import com.memorial.common.pagination.PageInfo;
import com.memorial.common.pagination.Paging;
import com.memorial.common.tool.LoginUtil;
import com.memorial.system.entity.Family;
import com.memorial.system.entity.Tomb;
import com.memorial.system.service.TombStoryService;
import com.memorial.system.mapper.FamilyMapper;
import com.memorial.system.mapper.TombMapper;
import com.memorial.system.param.TombPageParam;
import com.memorial.system.param.TombParam;
import com.memorial.system.service.TombAccessChecker;
import com.memorial.system.service.TombService;
import com.memorial.system.vo.TombVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class TombServiceImpl extends BaseServiceImpl<TombMapper, Tomb> implements TombService {

    @Autowired
    private TombMapper tombMapper;

    @Autowired
    private FamilyMapper familyMapper;

    @Autowired
    private TombStoryService tombStoryService;

    @Autowired
    private TombAccessChecker tombAccessChecker;

    @Override
    public Paging<TombVO> getTombPageList(TombPageParam param) throws Exception {
        param.setCurrentUserId(LoginUtil.getUserId());
        param.setIsAdmin(LoginUtil.isAdmin());
        Page<TombVO> page = new PageInfo<>(param);
        IPage<TombVO> iPage = tombMapper.getTombList(page, param);
        Paging<TombVO> paging = new Paging<>(iPage);
        if (LoginUtil.isAdmin() && paging.getRecords() != null) {
            paging.getRecords().forEach(vo -> vo.setMyRole("admin"));
        }
        return paging;
    }

    @Override
    public TombVO getTomb(Long id) throws Exception {
        Tomb tomb = tombMapper.selectById(id);
        if (tomb == null) {
            throw new BusinessException(500, "墓碑信息不存在");
        }
        tombAccessChecker.checkAccess(tomb);
        ensureQrCodeKey(tomb);
        TombVO tombVO = tombMapper.getTombVO(id);
        tombVO.setStories(tombStoryService.listByTombId(id));
        return tombVO;
    }

    /** 校验墓碑参数字段长度：个人简介纯文字不超过1000字 */
    private void validateTombParam(TombParam param) {
        if (param.getBiography() != null && !param.getBiography().isEmpty()) {
            String plain = param.getBiography().replaceAll("<[^>]+>", "").trim();
            if (plain.length() > 1000) {
                throw new BusinessException(500, "个人简介纯文字不能超过1000字");
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addTomb(TombParam param) throws Exception {
        validateTombParam(param);
        Tomb tomb = new Tomb();
        tomb.setName(param.getName());
        tomb.setPhoto(param.getPhoto());
        tomb.setBiography(param.getBiography());
        tomb.setStory(param.getStory());
        tomb.setEpitaph(param.getEpitaph());
        tomb.setVisitorActionOpen(param.getVisitorActionOpen() == null ? Boolean.TRUE : param.getVisitorActionOpen());
        tomb.setFamilyId(param.getFamilyId());
        tomb.setAddress(param.getAddress());
        if (param.getFamilyId() != null) checkCanAddTombToFamily(param.getFamilyId());
        tomb.setVisitCount(0);
        tomb.setMessageCount(0);
        tomb.setStatus(1);
        tomb.setUserId(LoginUtil.getUserId());
        tomb.setCreateBy(LoginUtil.getUserId());
        tomb.setCreateTime(new Date());
        tomb.setQrCodeKey(generateUniqueQrCodeKey());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (param.getBirthday() != null && !param.getBirthday().isEmpty()) {
            tomb.setBirthday(sdf.parse(param.getBirthday()));
        }
        if (param.getDeathday() != null && !param.getDeathday().isEmpty()) {
            tomb.setDeathday(sdf.parse(param.getDeathday()));
        }

        tombMapper.insert(tomb);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTomb(TombParam param) throws Exception {
        validateTombParam(param);
        if (param.getId() == null) {
            throw new BusinessException(500, "墓碑ID不能为空");
        }
        Tomb         tomb = tombMapper.selectById(param.getId());
        if (tomb == null) {
            throw new BusinessException(500, "墓碑信息不存在");
        }
        tombAccessChecker.checkAccess(tomb);
        tomb.setName(param.getName());
        tomb.setPhoto(param.getPhoto());
        tomb.setBiography(param.getBiography());
        tomb.setStory(param.getStory());
        tomb.setEpitaph(param.getEpitaph());
        if (param.getVisitorActionOpen() != null) {
            tomb.setVisitorActionOpen(param.getVisitorActionOpen());
        }
        if (param.getFamilyId() != null) checkCanAddTombToFamily(param.getFamilyId());
        tomb.setFamilyId(param.getFamilyId());
        tomb.setAddress(param.getAddress());
        tomb.setUpdateBy(LoginUtil.getUserId());
        tomb.setUpdateTime(new Date());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (param.getBirthday() != null && !param.getBirthday().isEmpty()) {
            tomb.setBirthday(sdf.parse(param.getBirthday()));
        }
        if (param.getDeathday() != null && !param.getDeathday().isEmpty()) {
            tomb.setDeathday(sdf.parse(param.getDeathday()));
        }

        tombMapper.updateById(tomb);
        return true;
    }

    /** 生成唯一二维码标识（12位字母数字），重复则重试 */
    private String generateUniqueQrCodeKey() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
        Random r = ThreadLocalRandom.current();
        for (int tryCount = 0; tryCount < 10; tryCount++) {
            StringBuilder sb = new StringBuilder(12);
            for (int i = 0; i < 12; i++) {
                sb.append(chars.charAt(r.nextInt(chars.length())));
            }
            String key = sb.toString();
            if (tombMapper.selectCount(Wrappers.<Tomb>lambdaQuery().eq(Tomb::getQrCodeKey, key)) == 0) {
                return key;
            }
        }
        return "T" + System.currentTimeMillis() + r.nextInt(1000);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTomb(Long id) throws Exception {
        Tomb tomb = tombMapper.selectById(id);
        if (tomb != null) tombAccessChecker.checkAccess(tomb);
        return super.removeById(id);
    }

    @Override
    public TombVO getTombDetail(Long id) throws Exception {
        Tomb tomb = tombMapper.selectById(id);
        if (tomb == null) {
            throw new BusinessException(500, "墓碑信息不存在");
        }
        ensureQrCodeKey(tomb);
        TombVO tombVO = tombMapper.getTombVO(id);
        tombVO.setStories(tombStoryService.listByTombId(id));
        setFamilyMemberFlag(tombVO);
        tomb.setVisitCount(tomb.getVisitCount() + 1);
        tombMapper.updateById(tomb);
        return tombVO;
    }

    @Override
    public TombVO getTombDetailByCode(String code) throws Exception {
        if (code == null || code.trim().isEmpty()) {
            throw new BusinessException(500, "二维码标识不能为空");
        }
        TombVO tombVO = tombMapper.getTombVOByCode(code.trim());
        if (tombVO == null) {
            throw new BusinessException(500, "墓碑信息不存在或链接已失效");
        }
        tombVO.setStories(tombStoryService.listByTombId(tombVO.getId()));
        setFamilyMemberFlag(tombVO);
        Tomb tomb = tombMapper.selectById(tombVO.getId());
        if (tomb != null) {
            tomb.setVisitCount(tomb.getVisitCount() + 1);
            tombMapper.updateById(tomb);
        }
        return tombVO;
    }

    /** 纪念页：根据当前登录用户判断是否已是家族成员，用于前端控制「申请成为家族成员」提示显隐 */
    private void setFamilyMemberFlag(TombVO tombVO) {
        try {
            Long userId = LoginUtil.getUserId();
            if (userId != null && tombVO.getFamilyId() != null) {
                int count = familyMapper.isUserInSameRootFamilyTree(userId, tombVO.getFamilyId());
                tombVO.setIsFamilyMember(count > 0);
            } else {
                tombVO.setIsFamilyMember(false);
            }
        } catch (Exception e) {
            tombVO.setIsFamilyMember(false);
        }
    }

    /** 添加墓碑：仅能加到用户可操作范围内的家族节点（家族/家庭/支族均可） */
    private void checkCanAddTombToFamily(Long familyId) {
        if (LoginUtil.isAdmin()) return;
        Family family = familyMapper.selectById(familyId);
        if (family == null) {
            throw new BusinessException(500, "所属家族不存在");
        }
        if (familyMapper.canUserOperateFamily(LoginUtil.getUserId(), familyId) <= 0) {
            throw new BusinessException(403, "只能在自己有权限的家族节点下添加墓碑");
        }
    }

    @Override
    public void checkTombAccess(Long tombId) throws Exception {
        Tomb tomb = tombMapper.selectById(tombId);
        if (tomb == null) {
            throw new BusinessException(500, "墓碑信息不存在");
        }
        tombAccessChecker.checkAccess(tomb);
    }

    /** 老数据无 qr_code_key 时懒填充并写回 */
    private void ensureQrCodeKey(Tomb tomb) {
        if (tomb != null && (tomb.getQrCodeKey() == null || tomb.getQrCodeKey().isEmpty())) {
            tomb.setQrCodeKey(generateUniqueQrCodeKey());
            tombMapper.updateById(tomb);
        }
    }
}
