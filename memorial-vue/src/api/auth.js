import request from '@/utils/request'

export function passwordLoginAPI(data) {
  return request.post('/user/login/password', data)
}

export function smsLoginAPI(data) {
  return request.post('/user/login/sms', data)
}

export function getCaptchaAPI() {
  return request.get('/captcha/getPictureCode')
}

export function sendSmsCodeAPI(data) {
  return request.post('/sms/getCode', data)
}

export function logoutAPI() {
  return request.post('/user/logOut')
}

export function getUserInfoAPI() {
  return request.get('/user/userInfo')
}

/** 用户注册（需手机验证码，密码 RSA 加密） */
export function registerAPI(data) {
  return request.post('/user/register', data)
}
