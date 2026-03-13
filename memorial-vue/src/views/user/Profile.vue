<template>
  <div class="profile-page">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card>
          <div class="avatar-section">
            <el-avatar :src="user.headImg || ''" :size="80" />
            <h3>{{ user.nickname || user.username || '用户' }}</h3>
            <p class="user-no">编号：{{ user.userNo || '-' }}</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card>
          <template #header><span style="font-weight: 600">个人信息</span></template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="账号">{{ user.username }}</el-descriptions-item>
            <el-descriptions-item label="昵称">{{ user.nickname }}</el-descriptions-item>
            <el-descriptions-item label="手机号">{{ user.mobile }}</el-descriptions-item>
            <el-descriptions-item label="性别">{{ user.sex === 1 ? '男' : user.sex === 2 ? '女' : '未知' }}</el-descriptions-item>
            <el-descriptions-item label="生日">{{ user.birthday || '-' }}</el-descriptions-item>
            <el-descriptions-item label="地址">{{ user.address || '-' }}</el-descriptions-item>
            <el-descriptions-item label="最后登录">{{ user.loginTime || '-' }}</el-descriptions-item>
            <el-descriptions-item label="注册时间">{{ user.createTime || '-' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import { getUserInfoAPI } from '@/api/auth'

const user = reactive({
  username: '', nickname: '', mobile: '', sex: 0,
  birthday: '', address: '', headImg: '', userNo: '',
  loginTime: '', createTime: ''
})

onMounted(async () => {
  const currentUser = localStorage.getItem('currentUser')
  if (currentUser) {
    Object.assign(user, JSON.parse(currentUser))
  }
  try {
    const res = await getUserInfoAPI()
    if (res) Object.assign(user, res)
  } catch { /* 使用本地缓存 */ }
})
</script>

<style scoped>
.profile-page { padding: 10px 0; }
.avatar-section { text-align: center; padding: 20px 0; }
.avatar-section h3 { margin-top: 12px; margin-bottom: 4px; }
.user-no { color: #909399; font-size: 13px; }
</style>
