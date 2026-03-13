import request from '@/utils/request'

export interface UserData {
  id: number
  userNo: string
  username: string
  nickname: string
  mobile: string
  headImg: string
  birthday: string | null
  sex: number
  status: number
  token: string
}

export const passwordLoginAPI = (data: { username: string; password: string }) =>
  request.post('/user/login/password', data)

export const smsLoginAPI = (data: { mobile: string; smsCode: string }) =>
  request.post('/user/login/sms', data)

export const getCaptchaAPI = () => request.get('/captcha/getPictureCode')

export const sendSmsCodeAPI = (data: { mobile: string; key: string; pictureCode: string }) =>
  request.post('/sms/getCode', data)

export const logoutAPI = () => request.post('/user/logOut')

export const getUserInfoAPI = () => request.get('/user/userInfo')
