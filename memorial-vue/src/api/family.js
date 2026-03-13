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
