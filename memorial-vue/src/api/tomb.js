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

// 墓碑事迹（管理端）
export function getTombStoryList(tombId) {
  return request.get(`/tomb/story/list/${tombId}`)
}

export function addTombStory(data) {
  return request.post('/tomb/story/add', data)
}

export function updateTombStory(data) {
  return request.post('/tomb/story/update', data)
}

export function deleteTombStory(id) {
  return request.post(`/tomb/story/delete/${id}`)
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

// 游客端：按二维码标识获取墓碑详情（扫码访问）
export function getMemorialDetailByCode(code) {
  return request.get(`/open/memorial/detail/code/${encodeURIComponent(code)}`)
}

// 游客端：查看记录（只读）
export function getMemorialMessagePageList(data) {
  return request.post('/open/memorial/message/getPageList', data)
}

export function getMemorialCheckinPageList(data) {
  return request.post('/open/memorial/checkin/getPageList', data)
}

export function submitMessage(data) {
  return request.post('/open/memorial/message', data)
}

export function submitCheckin(data) {
  return request.post('/open/memorial/checkin', data)
}

/** 当前用户对某墓碑的个人提醒（需登录） */
export function getTombReminder(tombId) {
  return request.get('/tomb/reminder/get', { params: { tombId } })
}

export function saveTombReminder(data) {
  return request.post('/tomb/reminder/save', data)
}

export function toggleTombReminder(data) {
  return request.post('/tomb/reminder/toggle', data)
}
