<template>
  <div class="profile-page">
    <el-row :gutter="20">
      <el-col :xs="24" :sm="24" :md="8" :lg="8">
        <el-card>
          <div class="avatar-section">
            <div class="avatar-wrapper" @click="triggerAvatarUpload">
              <el-avatar :src="user.headImg || defaultAvatar" :size="80" />
              <div class="avatar-overlay">
                <el-icon :size="20" color="#fff"><Camera /></el-icon>
              </div>
            </div>
            <input ref="avatarInput" type="file" accept="image/*" style="display:none" @change="handleAvatarUpload" />
            <h3>{{ user.nickname || user.username || '用户' }}</h3>
            <p class="user-no">编号：{{ user.userNo || '-' }}</p>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="16" :lg="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span class="card-title">个人信息</span>
              <el-button v-if="!editing" type="primary" link @click="startEdit">
                <el-icon><Edit /></el-icon>编辑
              </el-button>
            </div>
          </template>

          <!-- 展示模式 -->
          <el-descriptions v-if="!editing" :column="isMobile ? 1 : 2" border>
            <el-descriptions-item label="账号">{{ user.username }}</el-descriptions-item>
            <el-descriptions-item label="昵称">{{ user.nickname }}</el-descriptions-item>
            <el-descriptions-item label="手机号">{{ user.mobile }}</el-descriptions-item>
            <el-descriptions-item label="性别">{{ user.sex === 1 ? '男' : user.sex === 2 ? '女' : '未知' }}</el-descriptions-item>
            <el-descriptions-item label="生日">{{ user.birthday || '-' }}</el-descriptions-item>
            <el-descriptions-item label="地址">{{ user.address || '-' }}</el-descriptions-item>
            <el-descriptions-item label="最后登录">{{ user.loginTime || '-' }}</el-descriptions-item>
            <el-descriptions-item label="注册时间">{{ user.createTime || '-' }}</el-descriptions-item>
          </el-descriptions>

          <!-- 编辑模式 -->
          <el-form v-else :model="editForm" ref="formRef" label-width="80px">
            <el-row :gutter="20">
              <el-col :xs="24" :sm="24" :md="12" :lg="12">
                <el-form-item label="昵称">
                  <el-input v-model="editForm.nickname" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="12" :lg="12">
                <el-form-item label="性别">
                  <el-select v-model="editForm.sex" style="width: 100%">
                    <el-option label="男" :value="1" />
                    <el-option label="女" :value="2" />
                    <el-option label="未知" :value="0" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :xs="24" :sm="24" :md="12" :lg="12">
                <el-form-item label="生日">
                  <el-date-picker v-model="editForm.birthday" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="地址">
                  <el-input v-model="editForm.address" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item>
              <el-button type="primary" :loading="saving" @click="saveProfile">保存</el-button>
              <el-button @click="editing = false">取消</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { Camera, Edit } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getUserInfoAPI } from '@/api/auth'
import { updateCurrentUser, uploadFile } from '@/api/user'

const defaultAvatar = 'https://i.pravatar.cc/150?img=3'
const avatarInput = ref()
const isMobile = ref(window.innerWidth < 768)
function checkMobile() { isMobile.value = window.innerWidth < 768 }
const formRef = ref()
const editing = ref(false)
const saving = ref(false)

const user = reactive({
  id: null, username: '', nickname: '', mobile: '', sex: 0,
  birthday: '', address: '', headImg: '', userNo: '',
  loginTime: '', createTime: ''
})

const editForm = reactive({ nickname: '', sex: 0, birthday: '', address: '' })

onMounted(async () => {
  checkMobile()
  window.addEventListener('resize', checkMobile)
  const currentUser = localStorage.getItem('currentUser')
  if (currentUser) {
    Object.assign(user, JSON.parse(currentUser))
  }
  try {
    const res = await getUserInfoAPI()
    if (res) Object.assign(user, res)
  } catch { /* 使用本地缓存 */ }
})
onUnmounted(() => window.removeEventListener('resize', checkMobile))

function triggerAvatarUpload() { avatarInput.value?.click() }

async function handleAvatarUpload(e) {
  const file = e.target.files[0]
  if (!file) return
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.warning('图片不能超过5MB')
    return
  }
  const validTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp']
  if (!validTypes.includes(file.type)) {
    ElMessage.warning('仅支持 JPG/PNG/GIF/WebP 格式')
    return
  }
  try {
    const url = await uploadFile(file)
    if (url) {
      user.headImg = url
      await updateCurrentUser({ id: user.id, headImg: url })
      syncLocalStorage()
      ElMessage.success('头像更新成功')
    }
  } catch { ElMessage.error('上传失败') }
  finally { e.target.value = '' }
}

function startEdit() {
  Object.assign(editForm, {
    nickname: user.nickname || '',
    sex: user.sex || 0,
    birthday: user.birthday || '',
    address: user.address || ''
  })
  editing.value = true
}

async function saveProfile() {
  saving.value = true
  try {
    await updateCurrentUser({
      id: user.id,
      nickname: editForm.nickname,
      sex: editForm.sex,
      birthday: editForm.birthday,
      address: editForm.address
    })
    Object.assign(user, editForm)
    syncLocalStorage()
    editing.value = false
    ElMessage.success('修改成功')
  } catch { ElMessage.error('修改失败') }
  finally { saving.value = false }
}

function syncLocalStorage() {
  const currentUser = JSON.parse(localStorage.getItem('currentUser') || '{}')
  Object.assign(currentUser, {
    nickname: user.nickname,
    sex: user.sex,
    birthday: user.birthday,
    address: user.address,
    headImg: user.headImg
  })
  localStorage.setItem('currentUser', JSON.stringify(currentUser))
  window.dispatchEvent(new CustomEvent('user-info-updated'))
}
</script>

<style scoped>
.profile-page { padding: 10px 0; }
.avatar-section { text-align: center; padding: 20px 0; }
.avatar-section h3 { margin-top: 12px; margin-bottom: 4px; }
.user-no { color: #909399; font-size: 13px; }
.avatar-wrapper { position: relative; display: inline-block; cursor: pointer; }
.avatar-overlay {
  position: absolute; top: 0; left: 0; right: 0; bottom: 0;
  border-radius: 50%; background: rgba(0,0,0,.4);
  display: flex; align-items: center; justify-content: center;
  opacity: 0; transition: opacity .3s;
}
.avatar-wrapper:hover .avatar-overlay { opacity: 1; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.card-title { font-weight: 600; }
@media (max-width: 768px) {
  .profile-page { padding: 8px 0; }
  .avatar-section { padding: 16px 0; }
}
</style>
