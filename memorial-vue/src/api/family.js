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

/** 获取当前用户在指定家族的角色（族长/管理员/成员） */
export function getMyRoleInFamily(familyId) {
  return request.get(`/family/member/myRole/${familyId}`)
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
