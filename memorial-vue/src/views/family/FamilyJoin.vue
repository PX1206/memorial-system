<template>
  <div class="join-wrap">
    <el-card class="join-card">
      <h2 class="join-title">加入家族</h2>
      <p class="join-desc">请输入或从二维码中解析得到的邀请码，确认后即可加入对应家族。</p>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item label="邀请码" prop="inviteCode">
          <el-input v-model="form.inviteCode" placeholder="请输入家族邀请码" maxlength="16" />
        </el-form-item>
        <el-form-item label="与族长关系" prop="relation">
          <el-select v-model="form.relation" placeholder="请选择关系" style="width: 100%">
            <el-option v-for="r in relations" :key="r" :label="r" :value="r" />
          </el-select>
        </el-form-item>
      </el-form>

      <div class="join-actions">
        <el-button @click="goBack">返回</el-button>
        <el-button type="primary" :loading="submitting" @click="handleJoin">确认加入</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { joinFamilyByInviteCode } from '@/api/family'

const router = useRouter()
const route = useRoute()
const formRef = ref()
const submitting = ref(false)

const relations = ['父亲', '母亲', '儿子', '女儿', '兄弟', '姐妹', '爷爷', '奶奶', '外公', '外婆', '叔伯', '姑姨', '其他']

const form = reactive({
  inviteCode: '',
  relation: '',
  role: '成员'
})

const rules = {
  inviteCode: [{ required: true, message: '请输入邀请码', trigger: 'blur' }],
  relation: [{ required: true, message: '请选择关系', trigger: 'change' }]
}

onMounted(() => {
  let code = route.query.code
  if (typeof code === 'string' && code) {
    form.inviteCode = code
    try { localStorage.removeItem('pendingInviteCode') } catch (_) {}
    return
  }
  const pending = localStorage.getItem('pendingInviteCode')
  if (pending) {
    form.inviteCode = pending
    try { localStorage.removeItem('pendingInviteCode') } catch (_) {}
  }
})

function goBack() {
  router.push('/family')
}

function handleJoin() {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      await joinFamilyByInviteCode(form)
      ElMessage.success('加入家族成功')
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
.join-wrap {
  display: flex;
  justify-content: center;
  padding: 40px 16px;
}
.join-card {
  width: 480px;
}
.join-title {
  margin: 0 0 10px;
  font-size: 20px;
}
.join-desc {
  margin: 0 0 20px;
  font-size: 13px;
  color: #909399;
}
.join-actions {
  margin-top: 10px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>

