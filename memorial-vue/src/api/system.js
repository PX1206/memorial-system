import request from '@/utils/request'

export function getLogPageList(data) {
  return request.post('/sysOperationLog/getPageList', data)
}

export function getLogDetail(id) {
  return request.get(`/sysOperationLog/info/${id}`)
}

export function getDashboardStats() {
  return request.get('/dashboard/stats')
}

export function getFilePageList(data) {
  return request.post('/file/getPageList', data)
}

export function deleteFile(code) {
  return request.post(`/file/delete/${code}`)
}
