import axios from 'axios'
import router from '@/router'
import { msgError, msgWarning } from './message'

const request = axios.create({
  baseURL: import.meta.env.VITE_BASE_URL,
  timeout: 15000
})

request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = token
  }
  return config
})

request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code === 200) {
      return res.data
    }
    if (res.code === 401) {
      msgWarning(res.message || '登录已过期')
      localStorage.removeItem('token')
      localStorage.removeItem('currentUser')
      const from = router.currentRoute.value?.fullPath
      router.replace(from && from !== '/login' ? { path: '/login', query: { redirect: from } } : '/login')
      return Promise.reject(res)
    }
    // 429/403 等业务错误统一走后端 message
    msgError(res.message || '请求失败')
    return Promise.reject(res)
  },
  (error) => {
    if (error.response) {
      const res = error.response.data
      if (error.response.status === 401 || res?.code === 401) {
        msgWarning(res?.message || '登录已过期')
        localStorage.removeItem('token')
        localStorage.removeItem('currentUser')
        const from = router.currentRoute.value?.fullPath
        router.replace(from && from !== '/login' ? { path: '/login', query: { redirect: from } } : '/login')
      } else if (error.response.status === 429) {
        msgWarning(res?.message || '操作过于频繁，请稍后再试')
      } else if (error.response.status === 403) {
        msgWarning(res?.message || '暂无权限执行该操作')
      } else {
        msgError(res?.message || '服务器错误')
      }
    } else {
      msgError('网络错误，请稍后重试')
    }
    return Promise.reject(error)
  }
)

export default request
