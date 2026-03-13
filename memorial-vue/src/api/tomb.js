import request from '@/utils/request'

export function getTombPageList(data) {
  return request.post('/tomb/getPageList', data)
}

export function addTomb(data) {
  return request.post('/tomb/add', data)
}

export function updateTomb(data) {
  return request.post('/tomb/update', data)
}

export function deleteTomb(id) {
  return request.post(`/tomb/delete/${id}`)
}

export function getTombDetail(id) {
  return request.get(`/tomb/detail/${id}`)
}

export function getMessagePageList(data) {
  return request.post('/tomb/message/getPageList', data)
}

export function approveMessage(id) {
  return request.post(`/tomb/message/approve/${id}`)
}

export function rejectMessage(id, reason) {
  return request.post(`/tomb/message/reject/${id}`, { reason })
}

export function deleteMessage(id) {
  return request.post(`/tomb/message/delete/${id}`)
}

export function getCheckinPageList(data) {
  return request.post('/tomb/checkin/getPageList', data)
}

// 游客端接口（公开）
export function getMemorialDetail(id) {
  return request.get(`/open/memorial/detail/${id}`)
}

export function submitMessage(data) {
  return request.post('/open/memorial/message', data)
}

export function submitCheckin(data) {
  return request.post('/open/memorial/checkin', data)
}
