<template>
  <div class="memorial">
    <el-card v-loading="loading">
      <div class="header">
        <img :src="tomb.photo || defaultPhoto" class="photo" />
        <h2>{{ tomb.name || '加载中...' }}</h2>
        <p class="dates" v-if="tomb.birthday">{{ tomb.birthday }} — {{ tomb.deathday }}</p>
      </div>

      <div class="bio" v-if="tomb.biography">
        <h3>简介</h3>
        <p>{{ tomb.biography }}</p>
      </div>

      <div class="bio" v-if="tomb.story">
        <h3>生平事迹</h3>
        <p>{{ tomb.story }}</p>
      </div>

      <div class="actions">
        <el-button type="danger" @click="doCheckin('献花')">🌸 献花</el-button>
        <el-button type="warning" @click="doCheckin('点蜡烛')">🕯 点蜡烛</el-button>
      </div>

      <el-divider />

      <div class="message-section">
        <h3>留言板</h3>
        <div class="message-form">
          <el-input v-model="msgForm.visitorName" placeholder="您的姓名（可匿名）" style="width: 200px; margin-right: 10px" />
          <el-input v-model="msgForm.content" placeholder="写下您的思念..." type="textarea" :rows="2" style="margin-top: 10px" />
          <el-button type="primary" style="margin-top: 10px" :loading="submitting" @click="submitMsg">提交留言</el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getMemorialDetail, submitMessage, submitCheckin } from '@/api/tomb'

const route = useRoute()
const tombId = route.params.id
const loading = ref(true)
const submitting = ref(false)

const defaultPhoto = 'https://dummyimage.com/200x200/cccccc/000000&text=Photo'

const tomb = ref({})
const msgForm = reactive({ visitorName: '', content: '' })

onMounted(async () => {
  try {
    const res = await getMemorialDetail(tombId)
    if (res) tomb.value = res
  } catch {
    ElMessage.error('获取墓碑信息失败')
  } finally {
    loading.value = false
  }

  submitCheckin({ tombId: Number(tombId), type: '扫码访问' }).catch(() => {})
})

async function doCheckin(type) {
  try {
    await submitCheckin({ tombId: Number(tombId), type })
    ElMessage.success(`${type}成功，愿逝者安息`)
  } catch { /* */ }
}

async function submitMsg() {
  if (!msgForm.content.trim()) {
    ElMessage.warning('请输入留言内容')
    return
  }
  submitting.value = true
  try {
    await submitMessage({
      tombId: Number(tombId),
      visitorName: msgForm.visitorName || '匿名',
      content: msgForm.content
    })
    ElMessage.success('留言已提交，等待审核')
    msgForm.content = ''
  } catch { /* */ }
  finally { submitting.value = false }
}
</script>

<style scoped>
.memorial { max-width: 600px; margin: 40px auto; padding: 0 16px; }
.header { text-align: center; }
.photo { width: 200px; height: 200px; border-radius: 100px; object-fit: cover; }
.dates { color: #909399; font-size: 14px; }
.bio { margin-top: 20px; }
.bio h3 { margin-bottom: 8px; color: #303133; }
.bio p { color: #606266; line-height: 1.8; }
.actions { margin-top: 30px; text-align: center; }
.message-section { margin-top: 10px; }
.message-section h3 { margin-bottom: 12px; }
</style>
