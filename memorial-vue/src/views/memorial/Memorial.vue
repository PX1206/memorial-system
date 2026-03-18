<template>
  <div class="memorial">
    <el-card v-loading="loading" class="memorial-card">
      <div class="hero">
        <div class="hero-photo">
          <img :src="tomb.photo || defaultPhoto" class="photo" />
        </div>
        <div class="hero-info">
          <h2 class="name">{{ tomb.name || '加载中...' }}</h2>
          <div class="epitaph-under-name" v-if="tomb.epitaph">{{ tomb.epitaph }}</div>
          <div class="dates" v-if="tomb.birthday">{{ tomb.birthday }} — {{ tomb.deathday }}</div>
          <div class="address" v-if="tomb.address">所处位置：{{ tomb.address }}</div>
          <div class="meta">
            <el-tag size="small" type="info">访问量 {{ tomb.visitCount ?? 0 }}</el-tag>
            <el-tag size="small" type="success">留言数 {{ tomb.messageCount ?? 0 }}</el-tag>
            <el-tag size="small" :type="tomb.visitorActionOpen === false ? 'warning' : 'success'">
              {{ tomb.visitorActionOpen === false ? '仅家族成员可互动' : '开放互动' }}
            </el-tag>
          </div>

          <div class="actions">
            <el-button type="danger" @click="doCheckin('献花')">献花</el-button>
            <el-button type="warning" @click="doCheckin('点蜡烛')">点蜡烛</el-button>
          </div>

          <div class="name-setting" v-if="isLoggedIn">
            <el-checkbox v-model="actionAnonymous">匿名</el-checkbox>
            <span class="name-hint">不勾选则显示为当前账号昵称</span>
          </div>

          <div class="action-tip" v-if="!isLoggedIn">
            互动操作需要登录后进行。
            <el-button type="primary" link @click="openLoginDialog">登录</el-button>
          </div>

          <div class="apply-family-tip" v-if="tomb.visitorActionOpen === false && tomb.familyId">
            当前墓碑仅家族成员可互动，您可以申请成为家族成员。
            <el-button type="primary" link @click="openApplyDialog">申请成为家族成员</el-button>
          </div>
        </div>
      </div>

      <el-divider />

      <el-tabs v-model="tab" class="tabs" ref="tabsRef">
        <el-tab-pane label="简介" name="intro">
          <div class="section" v-if="tomb.biography">
            <div class="section-title">简介</div>
            <div class="text">{{ tomb.biography }}</div>
          </div>
          <div class="section empty" v-else>暂无简介</div>
        </el-tab-pane>

        <el-tab-pane label="生平事迹" name="stories">
          <div class="story-list" v-if="tomb.stories && tomb.stories.length">
            <div class="story-item clickable" v-for="s in tomb.stories" :key="s.id" @click="openStory(s)">
              <div class="story-title">{{ s.title }}</div>
              <div class="story-preview" v-html="toPreview(s.content)"></div>
              <div class="story-more">点击查看详情</div>
            </div>
          </div>
          <div class="section empty" v-else>暂无事迹</div>
        </el-tab-pane>

        <el-tab-pane label="留言" name="messages">
          <div class="compose">
            <div class="compose-row">
              <el-checkbox v-model="actionAnonymous">匿名</el-checkbox>
            </div>
            <el-input v-model="msgForm.content" placeholder="写下您的思念..." type="textarea" :rows="3" />
            <div class="compose-actions">
              <el-button type="primary" :loading="submitting" @click="submitMsg">提交留言</el-button>
              <span class="muted" v-if="isLoggedIn && tomb.visitorActionOpen === false">当前墓碑仅家族成员可留言。</span>
              <span class="muted">提示：留言需审核通过后才会对所有访客展示，未通过/待审核的留言仅本人可见。</span>
            </div>
          </div>

          <el-table :data="messageTable" border stripe v-loading="messageLoading" class="records-table">
            <el-table-column prop="visitorName" label="访客" width="120" />
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag v-if="row.status === 'approved'" type="success" size="small">已通过</el-tag>
                <el-tag v-else-if="row.status === 'pending'" type="warning" size="small">待审核</el-tag>
                <el-tag v-else-if="row.status === 'rejected'" type="danger" size="small">已拒绝</el-tag>
                <span v-else class="muted">-</span>
              </template>
            </el-table-column>
            <el-table-column prop="content" label="内容" show-overflow-tooltip />
            <el-table-column prop="createTime" label="时间" width="170" />
          </el-table>
          <div class="pagination-wrap">
            <el-pagination
              v-model:current-page="messageQuery.pageIndex"
              v-model:page-size="messageQuery.pageSize"
              :total="messageTotal"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              @current-change="loadMessages"
              @size-change="loadMessages"
            />
          </div>
        </el-tab-pane>

        <el-tab-pane label="献花/打卡" name="checkins">
          <el-table :data="checkinTable" border stripe v-loading="checkinLoading" class="records-table">
            <el-table-column prop="visitorName" label="访客" width="120" />
            <el-table-column prop="type" label="类型" width="120" />
            <el-table-column prop="createTime" label="时间" width="170" />
          </el-table>
          <div class="pagination-wrap">
            <el-pagination
              v-model:current-page="checkinQuery.pageIndex"
              v-model:page-size="checkinQuery.pageSize"
              :total="checkinTotal"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              @current-change="loadCheckins"
              @size-change="loadCheckins"
            />
          </div>
        </el-tab-pane>
      </el-tabs>

      <el-dialog v-model="storyDialogVisible" :title="currentStory.title" width="720px" destroy-on-close>
        <div class="story-dialog-content" v-html="currentStory.content"></div>
        <template #footer>
          <el-button type="primary" @click="storyDialogVisible = false">关闭</el-button>
        </template>
      </el-dialog>

      <!-- 登录弹窗：dialog 仅作定位/遮罩，视觉只有一层固定尺寸的登录卡片 -->
      <el-dialog
        v-model="loginDialogVisible"
        width="300px"
        :show-close="false"
        :close-on-click-modal="false"
        destroy-on-close
        class="memorial-login-dialog"
        align-center
        @closed="onLoginDialogClosed"
      >
        <div class="login-dialog-box">
          <el-icon class="login-dialog-box__close" @click="loginDialogVisible = false"><Close /></el-icon>
          <h2 class="login-dialog-box__title">系统登录</h2>
          <div class="login-dialog-box__tabs">
            <span :class="{ active: loginMode === 'password' }" @click="loginMode = 'password'">账号登录</span>
            <span :class="{ active: loginMode === 'sms' }" @click="loginMode = 'sms'">手机登录</span>
          </div>

          <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" class="login-dialog-box__form">
            <!-- 账号密码登录 -->
            <template v-if="loginMode === 'password'">
              <el-form-item prop="username">
                <el-input v-model="loginForm.username" placeholder="账号" />
              </el-form-item>
              <el-form-item prop="password">
                <el-input
                  v-model="loginForm.password"
                  type="password"
                  placeholder="密码"
                  @keyup.enter="handleDialogLogin"
                />
              </el-form-item>
            </template>

            <!-- 手机登录 -->
            <template v-else>
              <el-form-item prop="mobile">
                <el-input
                  v-model="loginForm.mobile"
                  placeholder="手机号"
                  maxlength="11"
                  @input="loginForm.mobile = loginForm.mobile.replace(/\D/g, '')"
                />
              </el-form-item>
              <el-form-item prop="pictureCode">
                <div class="login-dialog-box__row">
                  <el-input v-model="loginForm.pictureCode" placeholder="图片验证码" class="half" />
                  <img
                    :src="loginCaptchaImage"
                    class="login-dialog-box__captcha"
                    @click="getLoginCaptcha"
                  />
                </div>
              </el-form-item>
              <el-form-item prop="smsCode">
                <div class="login-dialog-box__row">
                  <el-input
                    v-model="loginForm.smsCode"
                    placeholder="短信验证码"
                    maxlength="6"
                    class="half"
                    @input="smsAutoSubmit"
                  />
                  <el-button
                    class="half"
                    :disabled="loginCountdown > 0 || loginSending"
                    @click="sendLoginSms"
                  >
                    {{ loginCountdown > 0 ? loginCountdown + 's' : '获取验证码' }}
                  </el-button>
                </div>
              </el-form-item>
            </template>

            <el-form-item>
              <el-button
                type="primary"
                style="width:100%"
                :loading="loginLoading"
                @click="handleDialogLogin"
              >
                登录
              </el-button>
              <div class="register-link" style="margin-top: 8px; text-align: right;">
                <el-button type="primary" link @click="openLoginRegisterDialog">注册</el-button>
              </div>
            </el-form-item>
          </el-form>
        </div>
      </el-dialog>

      <!-- 注册弹窗（纪念页） -->
      <el-dialog
        v-model="loginRegisterDialogVisible"
        title="用户注册"
        width="380px"
        destroy-on-close
      >
        <el-form
          ref="loginRegisterFormRef"
          :model="loginRegisterForm"
          :rules="loginRegisterRules"
          label-width="80px"
        >
          <el-form-item label="账号" prop="username">
            <el-input v-model="loginRegisterForm.username" maxlength="32" />
          </el-form-item>

          <el-form-item label="密码" prop="password">
            <el-input v-model="loginRegisterForm.password" type="password" show-password />
          </el-form-item>

          <el-form-item label="确认密码" prop="passwordConfirm">
            <el-input v-model="loginRegisterForm.passwordConfirm" type="password" show-password />
          </el-form-item>

          <el-form-item label="昵称">
            <el-input v-model="loginRegisterForm.nickname" maxlength="32" />
          </el-form-item>

          <el-form-item label="手机号" prop="mobile">
            <el-input
              v-model="loginRegisterForm.mobile"
              maxlength="11"
              @input="loginRegisterForm.mobile = loginRegisterForm.mobile.replace(/\D/g,'')"
            />
          </el-form-item>

          <el-form-item label="图形码" prop="pictureCode">
            <div class="login-dialog-box__row">
              <el-input v-model="loginRegisterForm.pictureCode" class="half" />
              <img
                :src="loginRegisterCaptchaImage"
                class="login-dialog-box__captcha"
                @click="getLoginRegisterCaptcha"
              />
            </div>
          </el-form-item>

          <el-form-item label="短信码" prop="smsCode">
            <div class="login-dialog-box__row">
              <el-input v-model="loginRegisterForm.smsCode" maxlength="6" class="half" />
              <el-button
                class="half"
                :disabled="loginRegisterCountdown > 0 || loginRegisterSending"
                @click="sendLoginRegisterSms"
              >
                {{ loginRegisterCountdown > 0 ? loginRegisterCountdown + 's' : '获取验证码' }}
              </el-button>
            </div>
          </el-form-item>
        </el-form>

        <template #footer>
          <el-button @click="loginRegisterDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="loginRegisterLoading" @click="handleDialogRegister">
            注册
          </el-button>
        </template>
      </el-dialog>

      <!-- 申请成为家族成员 -->
      <el-dialog v-model="applyDialogVisible" title="申请成为家族成员" width="420px" destroy-on-close>
        <el-form ref="applyFormRef" :model="applyForm" :rules="applyRules" label-width="90px">
          <el-form-item label="家族名称">{{ tomb.familyName || '—' }}</el-form-item>
          <el-form-item label="与族长关系" prop="relation">
            <el-select v-model="applyForm.relation" placeholder="请选择" style="width: 100%">
              <el-option v-for="r in relations" :key="r" :label="r" :value="r" />
            </el-select>
          </el-form-item>
          <el-form-item label="申请理由">
            <el-input v-model="applyForm.reason" type="textarea" :rows="2" placeholder="选填" maxlength="200" show-word-limit />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="applyDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="applySubmitting" @click="submitApply">提交申请</el-button>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Close } from '@element-plus/icons-vue'
import { getMemorialDetail, getMemorialDetailByCode, getMemorialMessagePageList, getMemorialCheckinPageList, submitMessage, submitCheckin } from '@/api/tomb'
import { passwordLoginAPI, smsLoginAPI, getCaptchaAPI, sendSmsCodeAPI, registerAPI } from '@/api/auth'
import { applyJoinFamily } from '@/api/family'
import { encryptPassword } from '@/utils/rsa'

const route = useRoute()
const router = useRouter()
const idOrCode = route.params.id
const loading = ref(true)
const submitting = ref(false)

const defaultPhoto = 'https://dummyimage.com/200x200/cccccc/000000&text=Photo'

const tomb = ref({})
const msgForm = reactive({ content: '' })
const actionAnonymous = ref(false)
const tab = ref('intro')
const tabsRef = ref()

const isLoggedIn = computed(() => !!localStorage.getItem('token'))

onMounted(async () => {
  try {
    const isNumeric = /^\d+$/.test(String(idOrCode))
    const res = isNumeric ? await getMemorialDetail(idOrCode) : await getMemorialDetailByCode(idOrCode)
    if (res) {
      tomb.value = res
      messageQuery.tombId = res.id
      checkinQuery.tombId = res.id
      loadMessages()
      loadCheckins()
    }
  } catch {
    ElMessage.error('获取墓碑信息失败')
  } finally {
    loading.value = false
  }
})

watch(tab, (val) => {
  if (tomb.value?.id && val === 'messages') loadMessages()
  if (tomb.value?.id && val === 'checkins') loadCheckins()
})

function openLoginDialog() {
  pendingAction.value = null
  loginDialogVisible.value = true
  if (loginMode.value === 'sms' || loginMode.value === 'register') getLoginCaptcha()
}
function onLoginDialogClosed() {
  pendingAction.value = null
}

// =========================
// 记录列表（只读，游客端接口）
// =========================

const messageLoading = ref(false)
const messageTotal = ref(0)
const messageTable = ref([])
const messageQuery = reactive({ tombId: null, pageIndex: 1, pageSize: 10 })

async function loadMessages() {
  if (!messageQuery.tombId) return
  messageLoading.value = true
  try {
    const res = await getMemorialMessagePageList(messageQuery)
    if (res) {
      messageTable.value = res.records || []
      messageTotal.value = res.total || 0
    }
  } catch { /* */ }
  finally { messageLoading.value = false }
}

const checkinLoading = ref(false)
const checkinTotal = ref(0)
const checkinTable = ref([])
const checkinQuery = reactive({ tombId: null, pageIndex: 1, pageSize: 10 })

async function loadCheckins() {
  if (!checkinQuery.tombId) return
  checkinLoading.value = true
  try {
    const res = await getMemorialCheckinPageList(checkinQuery)
    if (res) {
      checkinTable.value = res.records || []
      checkinTotal.value = res.total || 0
    }
  } catch { /* */ }
  finally { checkinLoading.value = false }
}

const pendingAction = ref(null)

async function doCheckin(type) {
  if (!isLoggedIn.value) {
    pendingAction.value = { type: 'checkin', payload: type }
    loginDialogVisible.value = true
    if (loginMode.value === 'sms' || loginMode.value === 'register') getLoginCaptcha()
    return
  }
  if (!tomb.value?.id) return
  try {
    await submitCheckin({ tombId: tomb.value.id, visitorName: getActionName(), type })
    ElMessage.success(`${type}成功，愿逝者安息`)
    checkinQuery.pageIndex = 1
    loadCheckins()
  } catch { /* 拦截器已处理 */ }
}

async function submitMsg() {
  if (!msgForm.content.trim()) {
    ElMessage.warning('请输入留言内容')
    return
  }
  if (!isLoggedIn.value) {
    pendingAction.value = { type: 'message' }
    loginDialogVisible.value = true
    if (loginMode.value === 'sms' || loginMode.value === 'register') getLoginCaptcha()
    return
  }
  if (!tomb.value?.id) return
  submitting.value = true
  try {
    await submitMessage({
      tombId: tomb.value.id,
      visitorName: getActionName(),
      content: msgForm.content
    })
    ElMessage.success('留言已提交，等待审核')
    msgForm.content = ''
    messageQuery.pageIndex = 1
    loadMessages()
  } catch { /* 拦截器已处理 */ }
  finally { submitting.value = false }
}

function getActionName() {
  return actionAnonymous.value ? '匿名' : ''
}

// =========================
// 登录弹窗
// =========================

const loginDialogVisible = ref(false)
const loginMode = ref<'password' | 'sms'>('password')
const loginFormRef = ref()
const loginForm = reactive({
  username: '',
  password: '',
  mobile: '',
  pictureCode: '',
  smsCode: ''
})
const loginRules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  mobile: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^\d{11}$/, message: '手机号格式错误', trigger: 'blur' }
  ],
  pictureCode: [{ required: true, message: '请输入图片验证码', trigger: 'blur' }],
  smsCode: [{ required: true, message: '请输入短信验证码', trigger: 'blur' }]
}
const loginCaptchaImage = ref('')
const loginCaptchaKey = ref('')
const loginCountdown = ref(0)
const loginSending = ref(false)
const loginLoading = ref(false)

watch(loginMode, (val) => { if (val === 'sms') getLoginCaptcha() })

async function getLoginCaptcha() {
  try {
    const res = await getCaptchaAPI()
    loginCaptchaImage.value = res.image
    loginCaptchaKey.value = res.key
  } catch { ElMessage.error('获取验证码失败') }
}

async function sendLoginSms() {
  if (loginSending.value || loginCountdown.value > 0) return
  await loginFormRef.value.validateField(['mobile', 'pictureCode'])
  try {
    loginSending.value = true
    await sendSmsCodeAPI({ mobile: loginForm.mobile, key: loginCaptchaKey.value, pictureCode: loginForm.pictureCode })
    ElMessage.success('短信已发送')
    loginCountdown.value = 60
    const timer = setInterval(() => { loginCountdown.value--; if (loginCountdown.value <= 0) clearInterval(timer) }, 1000)
  } catch { loginForm.pictureCode = ''; getLoginCaptcha() }
  finally { loginSending.value = false }
}

function smsAutoSubmit() {
  if (/^\d{6}$/.test(loginForm.smsCode)) handleDialogLogin()
}

async function handleDialogLogin() {
  if (loginLoading.value) return
  try {
    loginLoading.value = true
    let res
    if (loginMode.value === 'password') {
      await loginFormRef.value.validateField(['username', 'password'])
      const plain = Date.now() + loginForm.password
      res = await passwordLoginAPI({ username: loginForm.username, password: encryptPassword(plain) })
    } else {
      await loginFormRef.value.validateField(['mobile', 'smsCode'])
      res = await smsLoginAPI({ mobile: loginForm.mobile, smsCode: loginForm.smsCode })
    }
    if (res?.token) {
      localStorage.setItem('token', res.token)
      localStorage.setItem('currentUser', JSON.stringify(res))
    }
    loginDialogVisible.value = false
    const action = pendingAction.value
    pendingAction.value = null
    if (action?.type === 'checkin') doCheckin(action.payload)
    else if (action?.type === 'message') submitMsg()
    else if (action?.type === 'applyFamily') openApplyDialog()
  } catch {
    // 验证失败或登录失败，保持弹窗打开
  } finally {
    loginLoading.value = false
  }
}

// 注册弹窗（纪念页）
const loginRegisterDialogVisible = ref(false)
const loginRegisterFormRef = ref()
const loginRegisterForm = reactive({
  username: '',
  password: '',
  passwordConfirm: '',
  nickname: '',
  mobile: '',
  pictureCode: '',
  smsCode: ''
})
const loginRegisterCaptchaImage = ref('')
const loginRegisterCaptchaKey = ref('')
const loginRegisterCountdown = ref(0)
const loginRegisterSending = ref(false)
const loginRegisterLoading = ref(false)

const validateLoginRegisterPasswordConfirm = (_rule, value, callback) => {
  if (value !== loginRegisterForm.password) callback(new Error('两次输入的密码不一致'))
  else callback()
}

const loginRegisterRules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  passwordConfirm: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateLoginRegisterPasswordConfirm, trigger: 'blur' }
  ],
  mobile: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^\d{11}$/, message: '手机号格式错误', trigger: 'blur' }
  ],
  pictureCode: [{ required: true, message: '请输入图片验证码', trigger: 'blur' }],
  smsCode: [{ required: true, message: '请输入短信验证码', trigger: 'blur' }]
}

function openLoginRegisterDialog() {
  loginRegisterDialogVisible.value = true
  getLoginRegisterCaptcha()
}

async function getLoginRegisterCaptcha() {
  try {
    const res = await getCaptchaAPI()
    loginRegisterCaptchaImage.value = res.image
    loginRegisterCaptchaKey.value = res.key
  } catch {
    ElMessage.error('获取验证码失败')
  }
}

async function sendLoginRegisterSms() {
  if (loginRegisterSending.value || loginRegisterCountdown.value > 0) return
  await loginRegisterFormRef.value.validateField(['mobile', 'pictureCode'])
  try {
    loginRegisterSending.value = true
    await sendSmsCodeAPI({
      mobile: loginRegisterForm.mobile,
      key: loginRegisterCaptchaKey.value,
      pictureCode: loginRegisterForm.pictureCode
    })
    ElMessage.success('短信已发送')
    loginRegisterCountdown.value = 60
    const timer = setInterval(() => {
      loginRegisterCountdown.value--
      if (loginRegisterCountdown.value <= 0) clearInterval(timer)
    }, 1000)
  } finally {
    loginRegisterSending.value = false
  }
}

async function handleDialogRegister() {
  if (loginRegisterLoading.value) return
  try {
    await loginRegisterFormRef.value.validate()
    loginRegisterLoading.value = true
    const cipher = encryptPassword(loginRegisterForm.password)
    await registerAPI({
      username: loginRegisterForm.username.trim(),
      password: cipher,
      mobile: loginRegisterForm.mobile,
      smsCode: loginRegisterForm.smsCode,
      nickname: loginRegisterForm.nickname?.trim() || undefined
    })
    ElMessage.success('注册成功，请登录')
    loginRegisterDialogVisible.value = false
  } finally {
    loginRegisterLoading.value = false
  }
}

// =========================
// 申请成为家族成员（仅当「仅家族成员可互动」时显示）
// =========================

const relations = ['父亲', '母亲', '儿子', '女儿', '兄弟', '姐妹', '爷爷', '奶奶', '外公', '外婆', '叔伯', '姑姨', '其他']
const applyDialogVisible = ref(false)
const applyFormRef = ref()
const applySubmitting = ref(false)
const applyForm = reactive({ relation: '', reason: '' })
const applyRules = { relation: [{ required: true, message: '请选择与族长关系', trigger: 'change' }] }

function openApplyDialog() {
  if (!isLoggedIn.value) {
    pendingAction.value = { type: 'applyFamily' }
    loginDialogVisible.value = true
    if (loginMode.value === 'sms' || loginMode.value === 'register') getLoginCaptcha()
    return
  }
  if (!tomb.value?.familyId) {
    ElMessage.warning('该墓碑未关联家族')
    return
  }
  applyForm.relation = ''
  applyForm.reason = ''
  applyDialogVisible.value = true
}

async function submitApply() {
  try {
    await applyFormRef.value.validate()
  } catch {
    return
  }
  applySubmitting.value = true
  try {
    await applyJoinFamily({
      familyId: tomb.value.familyId,
      relation: applyForm.relation,
      reason: applyForm.reason || undefined
    })
    ElMessage.success('申请已提交，请等待族长/管理员审核')
    applyDialogVisible.value = false
  } catch { /* 拦截器处理 */ }
  finally { applySubmitting.value = false }
}

// =========================
// 事迹：点进去看
// =========================

const storyDialogVisible = ref(false)
const currentStory = reactive({ title: '', content: '' })

function openStory(s) {
  currentStory.title = s.title
  currentStory.content = s.content
  storyDialogVisible.value = true
}

function toPreview(html) {
  const text = String(html || '')
    .replace(/<style[\s\S]*?<\/style>/gi, '')
    .replace(/<script[\s\S]*?<\/script>/gi, '')
    .replace(/<[^>]+>/g, ' ')
    .replace(/\s+/g, ' ')
    .trim()
  return text ? text.slice(0, 70) + (text.length > 70 ? '…' : '') : '（无内容）'
}
</script>

<style scoped>
.memorial { max-width: 980px; margin: 20px auto; padding: 0 12px; }
.memorial-card { border-radius: 14px; overflow: hidden; }

.hero { display: flex; gap: 16px; align-items: center; }
.hero-photo { flex: 0 0 auto; }
.photo { width: 140px; height: 140px; border-radius: 14px; object-fit: cover; border: 1px solid #ebeef5; background: #f5f7fa; }
.hero-info { flex: 1; min-width: 0; }
.name { margin: 0 0 6px; font-size: 22px; }
.epitaph-under-name { color: #909399; font-size: 13px; line-height: 1.6; margin-bottom: 6px; font-style: italic; }
.dates { color: #909399; font-size: 13px; margin-bottom: 10px; }
.address { color: #909399; font-size: 13px; margin-bottom: 10px; }
.meta { display: flex; flex-wrap: wrap; gap: 8px; margin-bottom: 12px; }
.actions { display: flex; flex-wrap: wrap; gap: 10px; }
.name-setting { margin-top: 10px; display: flex; gap: 10px; align-items: center; flex-wrap: wrap; }
.name-hint { font-size: 12px; color: #909399; }
.action-tip { margin-top: 10px; font-size: 13px; color: #909399; }
.apply-family-tip { margin-top: 8px; font-size: 13px; color: #909399; }

/* 登录弹窗内部卡片样式（固定尺寸与首页一致） */
.memorial-login-dialog .el-dialog__header { display: none; }
.memorial-login-dialog .el-dialog__body {
  padding: 0 !important;
  background: transparent !important;
}

.login-dialog-box {
  position: relative;
  width: 300px;
  height: 340px;
  padding: 25px;
  box-sizing: border-box;
  background: rgba(255, 255, 255, 0.98);
  border-radius: 10px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
}
.login-dialog-box__form {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
}

.login-dialog-box__close {
  position: absolute;
  right: 16px;
  top: 16px;
  font-size: 18px;
  color: #909399;
  cursor: pointer;
}
.login-dialog-box__close:hover { color: #303133; }

.login-dialog-box__title {
  text-align: center;
  margin: 0 0 18px;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.login-dialog-box__tabs {
  display: flex;
  justify-content: space-around;
  margin-bottom: 18px;
  cursor: pointer;
}
.login-dialog-box__tabs span { padding-bottom: 5px; color: #606266; }
.login-dialog-box__tabs .active { border-bottom: 2px solid #409EFF; color: #409EFF; }

.login-dialog-box__form :deep(.el-form-item) {
  width: 75%;
  margin: 0 auto 14px;
}
.login-dialog-box__form :deep(.el-form-item:last-child) { margin-bottom: 0; margin-top: auto; }

.login-dialog-box__row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}
.login-dialog-box__row .half { width: 48%; }

.login-dialog-box__captcha {
  width: 48%;
  height: 36px;
  cursor: pointer;
  vertical-align: middle;
}

.tabs { margin-top: 10px; }
.section { margin-bottom: 14px; }
.section-title { font-size: 15px; font-weight: 600; margin-bottom: 8px; color: #303133; }
.text { color: #606266; line-height: 1.8; white-space: pre-wrap; }
.empty { color: #c0c4cc; }
.compose { margin-bottom: 12px; display: flex; flex-direction: column; gap: 10px; }
.compose-row { display: flex; gap: 10px; flex-wrap: wrap; }
.compose-actions { display: flex; align-items: center; gap: 10px; flex-wrap: wrap; }
.muted { color: #909399; font-size: 12px; }

.story-list { display: flex; flex-direction: column; gap: 14px; }
.story-item { padding: 10px 12px; border: 1px solid #ebeef5; border-radius: 10px; }
.story-item.clickable { cursor: pointer; transition: box-shadow .2s, border-color .2s; }
.story-item.clickable:hover { border-color: #c6e2ff; box-shadow: 0 6px 16px rgba(64,158,255,0.12); }
.story-title { font-weight: 600; margin-bottom: 8px; color: #303133; }
.story-preview { color: #606266; line-height: 1.8; }
.story-more { margin-top: 6px; font-size: 12px; color: #409eff; }
.story-dialog-content :deep(p) { margin: 6px 0; line-height: 1.8; color: #606266; }
.story-dialog-content :deep(img) { max-width: 100%; border-radius: 8px; }

.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 12px; }
.records-table { width: 100%; }

@media (max-width: 720px) {
  .hero { flex-direction: column; align-items: flex-start; }
  .photo { width: 100%; height: 220px; }
  .actions { width: 100%; }
}
</style>

<!-- 弹窗被 teleport 到 body，必须用全局样式才能去掉外层 el-dialog 白框并居中 -->
<style>
.el-dialog.memorial-login-dialog {
  position: fixed !important;
  left: 50% !important;
  top: 50% !important;
  transform: translate(-50%, -50%) !important;
  margin: 0 !important;
  background: transparent !important;
  box-shadow: none !important;
  border: none !important;
  padding: 0 !important;
}
.el-dialog.memorial-login-dialog .el-dialog__header { display: none; }
.el-dialog.memorial-login-dialog .el-dialog__body {
  padding: 0 !important;
  background: transparent !important;
}
</style>
