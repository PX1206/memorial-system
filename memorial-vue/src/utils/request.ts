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
      router.replace('/login')
      return Promise.reject(res)
    }
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
        router.replace('/login')
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
