import request from '@/utils/request'

export function getUserPageList(data) {
  return request.post('/user/getPageList', data)
}

export function addUser(data) {
  return request.post('/user/add', data)
}

export function updateUser(data) {
  return request.post('/user/updateUser', data)
}

export function deleteUser(id) {
  return request.post(`/user/delete/${id}`)
}

export function getUserInfo(id) {
  return request.get(`/user/info/${id}`)
}

export function disableUser(id) {
  return request.post(`/user/disable/${id}`)
}

export function freezeUser(id) {
  return request.post(`/user/freeze/${id}`)
}

export function restoreUser(id) {
  return request.post(`/user/restore/${id}`)
}

export function resetPassword(data) {
  return request.post('/user/resetPassword', data)
}

export function updateCurrentUser(data) {
  return request.post('/user/update', data)
}

export function uploadFile(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/file/upload', formData)
}
