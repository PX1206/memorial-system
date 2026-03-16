import request from '@/utils/request'

export function getMenuTree() {
  return request.get('/menu/getMenuTree')
}

export function getUserMenuTree() {
  return request.get('/menu/getUserMenuTree')
}

export function getUserPermissions() {
  return request.get('/menu/getUserPermissions')
}

export function addMenu(data) {
  return request.post('/menu/add', data)
}

export function updateMenu(data) {
  return request.post('/menu/update', data)
}

export function deleteMenu(id) {
  return request.post(`/menu/delete/${id}`)
}
