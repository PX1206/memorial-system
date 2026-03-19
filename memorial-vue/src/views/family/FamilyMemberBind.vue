<template>
  <div class="bind-wrap">
    <el-card class="bind-card">
      <h2 class="bind-title">绑定成员</h2>
      <p class="bind-desc">扫码后自动带入绑定码，确认后即可将当前账号绑定到该家族成员。</p>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item label="绑定码" prop="bindCode">
          <el-input v-model="form.bindCode" placeholder="请输入成员绑定码" maxlength="16" />
        </el-form-item>
      </el-form>

      <div class="bind-actions">
        <el-button @click="goBack">返回</el-button>
        <el-button type="primary" :loading="submitting" @click="handleBind">确认绑定</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { bindMemberByCode } from '@/api/family'

const router = useRouter()
const route = useRoute()
const formRef = ref()
const submitting = ref(false)

const form = reactive({ bindCode: '' })

const rules = {
  bindCode: [{ required: true, message: '请输入绑定码', trigger: 'blur' }]
}

onMounted(() => {
  let code = route.query.code
  if (typeof code === 'string' && code) {
    form.bindCode = code
    try { localStorage.removeItem('pendingBindCode') } catch (_) {}
    return
  }
  const pending = localStorage.getItem('pendingBindCode')
  if (pending) {
    form.bindCode = pending
    try { localStorage.removeItem('pendingBindCode') } catch (_) {}
  }
})

function goBack() {
  router.push('/family')
}

function handleBind() {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      await bindMemberByCode({ bindCode: form.bindCode })
      ElMessage.success('绑定成功')
      router.push('/family')
    } catch {
      // 错误由拦截器统一处理
    } finally {
      submitting.value = false
    }
  })
}
</script>

<style scoped>
.bind-wrap {
  display: flex;
  justify-content: center;
  padding: 40px 16px;
}
.bind-card {
  width: 480px;
}
.bind-title {
  margin: 0 0 10px;
  font-size: 20px;
}
.bind-desc {
  margin: 0 0 20px;
  font-size: 13px;
  color: #909399;
}
.bind-actions {
  margin-top: 10px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

@media (max-width: 768px) {
  .bind-wrap {
    padding: 16px;
  }
  .bind-card {
    width: 100%;
    max-width: 480px;
  }
  .bind-title {
    font-size: 18px;
  }
}
</style>
