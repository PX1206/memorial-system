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
import com.memorial.system.entity.FamilyMember;
import com.memorial.system.entity.Tomb;
import com.memorial.system.service.TombStoryService;
import com.memorial.system.mapper.FamilyMapper;
import com.memorial.system.mapper.FamilyMemberMapper;
import com.memorial.system.mapper.TombMapper;
import com.memorial.system.param.TombPageParam;
import com.memorial.system.param.TombParam;
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
    private FamilyMemberMapper familyMemberMapper;

    @Autowired
    private TombStoryService tombStoryService;

    @Override
    public Paging<TombVO> getTombPageList(TombPageParam param) throws Exception {
        param.setCurrentUserId(LoginUtil.getUserId());
        param.setIsAdmin(LoginUtil.isAdmin());
        Page<TombVO> page = new PageInfo<>(param);
        IPage<TombVO> iPage = tombMapper.getTombList(page, param);
        return new Paging<>(iPage);
    }

    @Override
    public TombVO getTomb(Long id) throws Exception {
        Tomb tomb = tombMapper.selectById(id);
        if (tomb == null) {
            throw new BusinessException(500, "墓碑信息不存在");
        }
        checkTombAccess(tomb);
        ensureQrCodeKey(tomb);
        TombVO tombVO = tombMapper.getTombVO(id);
        tombVO.setStories(tombStoryService.listByTombId(id));
        return tombVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addTomb(TombParam param) throws Exception {
        Tomb tomb = new Tomb();
        tomb.setName(param.getName());
        tomb.setPhoto(param.getPhoto());
        tomb.setBiography(param.getBiography());
        tomb.setStory(param.getStory());
        tomb.setEpitaph(param.getEpitaph());
        tomb.setVisitorActionOpen(param.getVisitorActionOpen() == null ? Boolean.TRUE : param.getVisitorActionOpen());
        tomb.setFamilyId(param.getFamilyId());
        if (param.getFamilyId() != null) checkFamilyAccess(param.getFamilyId());
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
        if (param.getId() == null) {
            throw new BusinessException(500, "墓碑ID不能为空");
        }
        Tomb         tomb = tombMapper.selectById(param.getId());
        if (tomb == null) {
            throw new BusinessException(500, "墓碑信息不存在");
        }
        checkTombAccess(tomb);
        tomb.setName(param.getName());
        tomb.setPhoto(param.getPhoto());
        tomb.setBiography(param.getBiography());
        tomb.setStory(param.getStory());
        tomb.setEpitaph(param.getEpitaph());
        if (param.getVisitorActionOpen() != null) {
            tomb.setVisitorActionOpen(param.getVisitorActionOpen());
        }
        if (param.getFamilyId() != null) checkFamilyAccess(param.getFamilyId());
        tomb.setFamilyId(param.getFamilyId());
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
        if (tomb != null) checkTombAccess(tomb);
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
        Tomb tomb = tombMapper.selectById(tombVO.getId());
        if (tomb != null) {
            tomb.setVisitCount(tomb.getVisitCount() + 1);
            tombMapper.updateById(tomb);
        }
        return tombVO;
    }

    /** 角色2校验：只能操作指定家族（创建墓碑时选择家族） */
    private void checkFamilyAccess(Long familyId) {
        if (LoginUtil.isAdmin()) return;
        Family family = familyMapper.selectById(familyId);
        if (family == null) return;
        Long userId = LoginUtil.getUserId();
        if (Objects.equals(family.getFounderId(), userId)) return;
        Integer count = familyMemberMapper.selectCount(Wrappers.<FamilyMember>lambdaQuery()
                .eq(FamilyMember::getFamilyId, familyId)
                .eq(FamilyMember::getUserId, userId)
                .eq(FamilyMember::getDelFlag, false));
        if (count == null || count <= 0) {
            throw new BusinessException(403, "无权限在该家族下创建或关联墓碑");
        }
    }

    /** 角色2校验：只能操作自己家族下的墓碑或自己创建的墓碑 */
    private void checkTombAccess(Tomb tomb) {
        if (LoginUtil.isAdmin()) return;
        Long userId = LoginUtil.getUserId();
        if (tomb.getFamilyId() == null) {
            if (Objects.equals(tomb.getUserId(), userId) || Objects.equals(tomb.getCreateBy(), userId)) return;
            throw new BusinessException(403, "无权限操作该墓碑");
        }
        Family family = familyMapper.selectById(tomb.getFamilyId());
        if (family == null) return;
        if (Objects.equals(family.getFounderId(), userId)) return;
        Integer count = familyMemberMapper.selectCount(Wrappers.<FamilyMember>lambdaQuery()
                .eq(FamilyMember::getFamilyId, tomb.getFamilyId())
                .eq(FamilyMember::getUserId, userId)
                .eq(FamilyMember::getDelFlag, false));
        if (count == null || count <= 0) {
            throw new BusinessException(403, "无权限操作该墓碑");
        }
    }

    /** 老数据无 qr_code_key 时懒填充并写回 */
    private void ensureQrCodeKey(Tomb tomb) {
        if (tomb != null && (tomb.getQrCodeKey() == null || tomb.getQrCodeKey().isEmpty())) {
            tomb.setQrCodeKey(generateUniqueQrCodeKey());
            tombMapper.updateById(tomb);
        }
    }
}
