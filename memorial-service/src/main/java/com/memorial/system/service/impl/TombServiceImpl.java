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
import com.memorial.system.entity.TombReminder;
import com.memorial.system.service.TombStoryService;
import com.memorial.system.mapper.FamilyMapper;
import com.memorial.system.mapper.TombMapper;
import com.memorial.system.mapper.TombReminderMapper;
import com.memorial.system.param.TombPageParam;
import com.memorial.system.param.TombParam;
import com.memorial.system.service.TombAccessChecker;
import com.memorial.system.service.TombReminderAccess;
import com.memorial.system.service.TombService;
import com.memorial.system.vo.TombVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class TombServiceImpl extends BaseServiceImpl<TombMapper, Tomb> implements TombService {

    private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_LOCAL_DATE;

    @Autowired
    private TombMapper tombMapper;

    @Autowired
    private FamilyMapper familyMapper;

    @Autowired
    private TombStoryService tombStoryService;

    @Autowired
    private TombAccessChecker tombAccessChecker;

    @Autowired
    private TombReminderAccess tombReminderAccess;

    @Autowired
    private TombReminderMapper tombReminderMapper;

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
        fillMyReminderOn(paging.getRecords());
        return paging;
    }

    /** 列表：当前用户对每条墓碑的个人提醒是否处于开启状态 */
    private void fillMyReminderOn(List<TombVO> records) {
        if (records == null || records.isEmpty()) {
            return;
        }
        Long userId;
        try {
            userId = LoginUtil.getUserId();
        } catch (Exception e) {
            return;
        }
        List<Long> tombIds = records.stream().map(TombVO::getId).filter(Objects::nonNull).collect(Collectors.toList());
        if (tombIds.isEmpty()) {
            return;
        }
        List<TombReminder> list = tombReminderMapper.selectList(Wrappers.<TombReminder>lambdaQuery()
                .eq(TombReminder::getUserId, userId)
                .in(TombReminder::getTombId, tombIds));
        Map<Long, TombReminder> map = new HashMap<>();
        for (TombReminder tr : list) {
            map.putIfAbsent(tr.getTombId(), tr);
        }
        for (TombVO vo : records) {
            TombReminder tr = map.get(vo.getId());
            vo.setMyReminderOn(tr != null && Boolean.TRUE.equals(tr.getEnabled()));
        }
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
        if (param.getBirthday() == null || param.getBirthday().trim().isEmpty()) {
            throw new BusinessException(500, "请填写出生日期");
        }
        if (param.getDeathday() == null || param.getDeathday().trim().isEmpty()) {
            throw new BusinessException(500, "请填写逝世日期");
        }
    }

    private Date parseSqlDate(String s) {
        LocalDate ld = LocalDate.parse(s.trim(), ISO_DATE);
        return java.sql.Date.valueOf(ld);
    }

    private void applyTombDateFields(Tomb tomb, TombParam param) {
        tomb.setBirthday(parseSqlDate(param.getBirthday()));
        tomb.setDeathday(parseSqlDate(param.getDeathday()));
        tomb.setLunarFlag(Boolean.TRUE.equals(param.getLunarFlag()));
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

        applyTombDateFields(tomb, param);

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

        applyTombDateFields(tomb, param);

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
        setReminderFlag(tombVO, tomb);
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
            setReminderFlag(tombVO, tomb);
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

    /** 是否可设置个人提醒（需登录；墓碑未禁用即可） */
    private void setReminderFlag(TombVO tombVO, Tomb tomb) {
        if (tomb == null) {
            tombVO.setCanSetReminder(false);
            return;
        }
        try {
            Long userId = LoginUtil.getUserId();
            tombVO.setCanSetReminder(tombReminderAccess.canManage(userId, tomb));
        } catch (Exception e) {
            tombVO.setCanSetReminder(false);
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
