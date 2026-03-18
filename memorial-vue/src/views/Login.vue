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

          <!-- 右下角注册入口 -->
          <div class="register-link">
            <el-button type="primary" link @click="openRegisterDialog">
              注册
            </el-button>
          </div>
        </el-form-item>

      </el-form>

    </div>

    <!-- 注册弹窗 -->
    <el-dialog
      v-model="registerDialogVisible"
      title="用户注册"
      width="380px"
      destroy-on-close
    >
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        label-width="80px"
      >
        <el-form-item label="账号" prop="username">
          <el-input v-model="registerForm.username" maxlength="32" />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input v-model="registerForm.password" type="password" show-password />
        </el-form-item>

        <el-form-item label="确认密码" prop="passwordConfirm">
          <el-input v-model="registerForm.passwordConfirm" type="password" show-password />
        </el-form-item>

        <el-form-item label="昵称">
          <el-input v-model="registerForm.nickname" maxlength="32" />
        </el-form-item>

        <el-form-item label="手机号" prop="mobile">
          <el-input
            v-model="registerForm.mobile"
            maxlength="11"
            @input="registerForm.mobile = registerForm.mobile.replace(/\\D/g,'')"
          />
        </el-form-item>

        <el-form-item label="图形码" prop="pictureCode">
          <div class="row">
            <el-input v-model="registerForm.pictureCode" class="half" />
            <img
              :src="registerCaptchaImage"
              class="captcha-img"
              @click="getRegisterCaptcha"
            />
          </div>
        </el-form-item>

        <el-form-item label="短信码" prop="smsCode">
          <div class="row">
            <el-input v-model="registerForm.smsCode" maxlength="6" class="half" />
            <el-button
              class="half"
              :disabled="registerCountdown>0 || registerSending"
              @click="sendRegisterSms"
            >
              {{ registerCountdown > 0 ? registerCountdown + 's' : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="registerDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="registerLoading" @click="doRegister">
          注册
        </el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup lang="ts">

import {ref, reactive, onMounted, nextTick, watch} from 'vue'
import {passwordLoginAPI, smsLoginAPI, getCaptchaAPI, sendSmsCodeAPI, registerAPI} from '@/api/auth'
import {encryptPassword} from '@/utils/rsa'
import {useRouter, useRoute} from 'vue-router'
import {ElMessage} from 'element-plus'

const router = useRouter()
const route = useRoute()

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

/* 发送短信验证码（登录） */
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

    const redirect = (route.query.redirect as string) || '/'
    router.replace(redirect)

  } finally {
    loading.value = false
  }
}

/* 注册弹窗相关 */
const registerDialogVisible = ref(false)
const registerFormRef = ref()
const registerForm = reactive({
  username: '',
  password: '',
  passwordConfirm: '',
  nickname: '',
  mobile: '',
  pictureCode: '',
  smsCode: ''
})
const registerCaptchaImage = ref('')
const registerCaptchaKey = ref('')
const registerCountdown = ref(0)
const registerSending = ref(false)
const registerLoading = ref(false)

const validateRegisterPasswordConfirm = (_rule: any, value: string, callback: (e?: Error) => void) => {
  if (value !== registerForm.password) callback(new Error('两次输入的密码不一致'))
  else callback()
}

const registerRules: Record<string, any> = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  passwordConfirm: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateRegisterPasswordConfirm, trigger: 'blur' }
  ],
  mobile: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式错误', trigger: 'blur' }
  ],
  pictureCode: [{ required: true, message: '请输入图片验证码', trigger: 'blur' }],
  smsCode: [{ required: true, message: '请输入短信验证码', trigger: 'blur' }]
}

const openRegisterDialog = () => {
  registerDialogVisible.value = true
  getRegisterCaptcha()
}

const getRegisterCaptcha = async () => {
  try {
    const res = await getCaptchaAPI()
    registerCaptchaImage.value = res.image
    registerCaptchaKey.value = res.key
  } catch {
    ElMessage.error('获取验证码失败')
  }
}

const sendRegisterSms = async () => {
  if (registerSending.value || registerCountdown.value > 0) return
  await registerFormRef.value.validateField(['mobile', 'pictureCode'])
  try {
    registerSending.value = true
    await sendSmsCodeAPI({
      mobile: registerForm.mobile,
      key: registerCaptchaKey.value,
      pictureCode: registerForm.pictureCode
    })
    ElMessage.success('短信已发送')
    registerCountdown.value = 60
    const timer = setInterval(() => {
      registerCountdown.value--
      if (registerCountdown.value <= 0) clearInterval(timer)
    }, 1000)
  } finally {
    registerSending.value = false
  }
}

const doRegister = async () => {
  if (registerLoading.value) return
  try {
    await registerFormRef.value.validate()
    registerLoading.value = true
    const cipher = encryptPassword(registerForm.password)
    await registerAPI({
      username: registerForm.username.trim(),
      password: cipher,
      mobile: registerForm.mobile,
      smsCode: registerForm.smsCode,
      nickname: registerForm.nickname?.trim() || undefined
    })
    ElMessage.success('注册成功，请登录')
    registerDialogVisible.value = false
  } finally {
    registerLoading.value = false
  }
}

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

.register-link {
  margin-top: 8px;
  text-align: right;
  font-size: 18px;
}

</style>
