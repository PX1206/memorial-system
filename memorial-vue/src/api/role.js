import request from '@/utils/request'

export function getRolePageList(data) {
  return request.post('/role/getPageList', data)
}

export function getAllRoles() {
  return request.get('/role/getAllRoles')
}

export function addRole(data) {
  return request.post('/role/add', data)
}

export function updateRole(data) {
  return request.post('/role/update', data)
}

export function deleteRole(id) {
  return request.post(`/role/delete/${id}`)
}

export function toggleRoleStatus(id) {
  return request.post(`/role/toggleStatus/${id}`)
}

export function saveRoleMenus(data) {
  return request.post('/role/saveRoleMenus', data)
}

export function getRoleMenuIds(roleId) {
  return request.get(`/role/getRoleMenuIds/${roleId}`)
}

export function saveUserRoles(data) {
  return request.post('/role/saveUserRoles', data)
}

export function getUserRoleIds(userId) {
  return request.get(`/role/getUserRoleIds/${userId}`)
}
