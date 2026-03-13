<template>
  <div class="login-page">

    <div class="login-container">

      <h2 class="title">系统登录</h2>

      <div class="login-tabs">
        <span :class="{active: mode==='password'}" @click="mode='password'">
          账号登录
        </span>

        <span :class="{active: mode==='sms'}" @click="mode='sms'">
          手机登录
        </span>
      </div>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        class="form"
      >

        <!-- 账号密码登录 -->
        <template v-if="mode==='password'">

          <el-form-item prop="username">
            <el-input
              ref="usernameRef"
              v-model="form.username"
              placeholder="账号"
              @keyup.enter="login"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="密码"
              @keyup.enter="login"
            />
          </el-form-item>

        </template>

        <!-- 手机登录 -->
        <template v-else>

          <el-form-item prop="mobile">
            <el-input
              v-model="form.mobile"
              placeholder="手机号"
              maxlength="11"
              @input="form.mobile=form.mobile.replace(/\\D/g,'')"
            />
          </el-form-item>

          <!-- 图片验证码 -->
          <el-form-item prop="pictureCode">
            <div class="row">

              <el-input
                v-model="form.pictureCode"
                placeholder="图片验证码"
                class="half"
              />

              <img
                :src="captchaImage"
                class="captcha-img"
                @click="getCaptcha"
              />

            </div>
          </el-form-item>

          <!-- 短信验证码 -->
          <el-form-item prop="smsCode">
            <div class="row">

              <el-input
                v-model="form.smsCode"
                placeholder="短信验证码"
                maxlength="6"
                class="half"
                @input="smsAutoLogin"
              />

              <el-button
                class="half"
                :disabled="countdown>0 || sending"
                @click="sendSms"
              >
                {{ countdown > 0 ? countdown + 's' : '获取验证码' }}
              </el-button>

            </div>
          </el-form-item>

        </template>

        <el-form-item>
          <el-button
            type="primary"
            style="width:100%"
            :loading="loading"
            @click="login"
          >
            登录
          </el-button>
        </el-form-item>

      </el-form>

    </div>

  </div>
</template>

<script setup lang="ts">

import {ref, reactive, onMounted, nextTick} from 'vue'
import {passwordLoginAPI, smsLoginAPI, getCaptchaAPI, sendSmsCodeAPI} from '@/api/auth'
import {encryptPassword} from '@/utils/rsa'
import {useRouter} from 'vue-router'
import {ElMessage} from 'element-plus'

const router = useRouter()

const mode = ref<'password' | 'sms'>('password')

const formRef = ref()
const usernameRef = ref()

const captchaImage = ref('')
const captchaKey = ref('')

const countdown = ref(0)
const loading = ref(false)
const sending = ref(false)

const form = reactive({
  username: '',
  password: '',
  mobile: '',
  pictureCode: '',
  smsCode: ''
})

/* 表单校验 */
const rules = {

  username: [
    {required: true, message: '请输入账号', trigger: 'blur'}
  ],

  password: [
    {required: true, message: '请输入密码', trigger: 'blur'}
  ],

  mobile: [
    {required: true, message: '请输入手机号', trigger: 'blur'},
    {pattern: /^1[3-9]\d{9}$/, message: '手机号格式错误', trigger: 'blur'}
  ],

  pictureCode: [
    {required: true, message: '请输入图片验证码', trigger: 'blur'}
  ],

  smsCode: [
    {required: true, message: '请输入短信验证码', trigger: 'blur'}
  ]

}

/* 获取图片验证码 */
const getCaptcha = async () => {

  try {

    const res = await getCaptchaAPI()

    captchaImage.value = res.image
    captchaKey.value = res.key

  } catch {

    ElMessage.error('获取验证码失败')

  }

}

/* 发送短信验证码 */
const sendSms = async () => {

  if (sending.value || countdown.value > 0) return

  await formRef.value.validateField(['mobile', 'pictureCode'])

  try {

    sending.value = true

    await sendSmsCodeAPI({
      mobile: form.mobile,
      key: captchaKey.value,
      pictureCode: form.pictureCode
    })

    ElMessage.success('短信已发送')

    countdown.value = 60

    const timer = setInterval(() => {

      countdown.value--

      if (countdown.value <= 0) {
        clearInterval(timer)
      }

    }, 1000)

  } catch {

    // 发送失败刷新验证码
    form.pictureCode = ''
    getCaptcha()

  } finally {

    sending.value = false

  }

}

/* 验证码6位自动登录 */
const smsAutoLogin = () => {

  if (/^\d{6}$/.test(form.smsCode)) {
    login()
  }

}

// 登录成功后保存 token 和用户信息
const login = async () => {
  if (loading.value) return

  try {
    loading.value = true

    let res
    if (mode.value === 'password') {
      await formRef.value.validateField(['username', 'password'])
      const timestamp = Date.now()
      const plain = timestamp + form.password
      const cipher = encryptPassword(plain)

      res = await passwordLoginAPI({
        username: form.username,
        password: cipher
      })
    } else {
      await formRef.value.validateField(['mobile', 'smsCode'])
      res = await smsLoginAPI({
        mobile: form.mobile,
        smsCode: form.smsCode
      })
    }

    // 保存 token
    localStorage.setItem('token', res.token)

    // 保存完整用户信息
    localStorage.setItem('currentUser', JSON.stringify(res))

    router.push('/')

  } finally {
    loading.value = false
  }
}

import { watch } from 'vue'

watch(mode, (val) => {

  if (val === 'sms') {
    getCaptcha()
  }

})

onMounted(() => {

  nextTick(() => {
    usernameRef.value?.focus()
  })

})

</script>

<style scoped>

.login-page {
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

.login-container {
  position: absolute;
  right: 10%;
  top: 50%;
  transform: translateY(-50%);
  width: 300px;
  height: 340px;
  padding: 25px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 10px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
}

.title {
  text-align: center;
  margin-bottom: 18px;
}

.login-tabs {
  display: flex;
  justify-content: space-around;
  margin-bottom: 18px;
  cursor: pointer;
}

.login-tabs span {
  padding-bottom: 5px;
}

.login-tabs .active {
  border-bottom: 2px solid #409EFF;
  color: #409EFF;
}

.form .el-form-item {
  width: 75%;
  margin: 0 auto 14px;
}

.row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.half {
  width: 48%;
}

.captcha-img {
  width: 48%;
  height: 36px;
  cursor: pointer;
}

</style>
