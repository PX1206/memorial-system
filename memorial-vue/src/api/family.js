import request from '@/utils/request'

export function getFamilyPageList(data) {
  return request.post('/family/getPageList', data)
}

export function addFamily(data) {
  return request.post('/family/add', data)
}

export function updateFamily(data) {
  return request.post('/family/update', data)
}

export function deleteFamily(id) {
  return request.post(`/family/delete/${id}`)
}

export function getMemberPageList(data) {
  return request.post('/family/member/getPageList', data)
}

export function addFamilyMember(data) {
  return request.post('/family/member/add', data)
}

export function updateFamilyMember(data) {
  return request.post('/family/member/update', data)
}

export function removeFamilyMember(id) {
  return request.post(`/family/member/delete/${id}`)
}

export function getFamilyTree() {
  return request.get('/family/getTree')
}

/** 获取当前用户在指定家族的角色 */
export function getMyRoleInFamily(familyId) {
  return request.get(`/family/member/myRole/${familyId}`)
}

/** 超级管理员指定管理员 */
export function designateAdmin(data) {
  return request.post('/family/designateAdmin', data)
}

export function joinFamilyByInviteCode(data) {
  return request.post('/family/member/joinByCode', data)
}

// 申请成为家族成员（纪念页「仅家族成员可互动」时使用）
export function applyJoinFamily(data) {
  return request.post('/family/member/apply', data)
}

export function getFamilyApplyList(familyId, status) {
  return request.get('/family/member/apply/list', { params: { familyId, status } })
}

export function approveFamilyApply(id) {
  return request.post(`/family/member/apply/approve/${id}`)
}

export function rejectFamilyApply(id, reason) {
  return request.post(`/family/member/apply/reject/${id}`, { reason })
}

/** 扫码绑定：当前用户绑定到指定成员 */
export function bindMemberByCode(data) {
  return request.post('/family/member/bindByCode', data)
}

/** 解绑成员：清除成员的关联用户 */
export function unbindMember(id) {
  return request.post(`/family/member/unbind/${id}`)
}

/** 为成员生成/获取绑定码（用于展示二维码） */
export function ensureMemberBindCode(id) {
  return request.post(`/family/member/ensureBindCode/${id}`)
}
