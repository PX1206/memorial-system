<template>
  <div class="dashboard">
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card stat-card--primary">
          <div class="stat-icon"><el-icon :size="40"><Place /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.tombCount }}</div>
            <div class="stat-label">墓碑总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card stat-card--success">
          <div class="stat-icon"><el-icon :size="40"><User /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.userCount }}</div>
            <div class="stat-label">注册用户</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card stat-card--warning">
          <div class="stat-icon"><el-icon :size="40"><Connection /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.familyCount }}</div>
            <div class="stat-label">家族数量</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card stat-card--danger">
          <div class="stat-icon"><el-icon :size="40"><ChatDotRound /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.messageCount }}</div>
            <div class="stat-label">留言总数</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header><span class="card-title">最近打卡记录</span></template>
          <el-table :data="stats.recentCheckins" style="width: 100%">
            <el-table-column prop="visitorName" label="访客姓名" />
            <el-table-column prop="tombName" label="墓碑" />
            <el-table-column prop="createTime" label="打卡时间" />
            <el-table-column prop="type" label="类型">
              <template #default="{ row }">
                <el-tag :type="row.type === '献花' ? 'success' : 'warning'" size="small">{{ row.type }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!stats.recentCheckins?.length" description="暂无打卡记录" :image-size="80" />
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header><span class="card-title">最新留言</span></template>
          <div class="message-list">
            <div v-for="msg in stats.recentMessages" :key="msg.id" class="message-item">
              <div class="msg-header">
                <el-avatar :size="28" />
                <span class="msg-name">{{ msg.visitorName }}</span>
                <span class="msg-time">{{ msg.createTime }}</span>
              </div>
              <div class="msg-content">{{ msg.content }}</div>
            </div>
            <el-empty v-if="!stats.recentMessages?.length" description="暂无留言" :image-size="80" />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import { Place, User, Connection, ChatDotRound } from '@element-plus/icons-vue'
import { getDashboardStats } from '@/api/system'

const stats = reactive({
  tombCount: 0,
  userCount: 0,
  familyCount: 0,
  messageCount: 0,
  recentCheckins: [],
  recentMessages: []
})

onMounted(async () => {
  try {
    const res = await getDashboardStats()
    if (res) {
      Object.assign(stats, res)
    }
  } catch {
    // 接口失败使用默认值
  }
})
</script>

<style scoped>
.dashboard { min-height: 100%; }

.stat-card { display: flex; align-items: center; padding: 20px; }
.stat-card :deep(.el-card__body) { display: flex; align-items: center; gap: 16px; width: 100%; }

.stat-icon {
  width: 64px; height: 64px; border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  color: #fff; flex-shrink: 0;
}
.stat-card--primary .stat-icon { background: linear-gradient(135deg, #409eff, #66b1ff); }
.stat-card--success .stat-icon { background: linear-gradient(135deg, #67c23a, #85ce61); }
.stat-card--warning .stat-icon { background: linear-gradient(135deg, #e6a23c, #ebb563); }
.stat-card--danger .stat-icon { background: linear-gradient(135deg, #f56c6c, #f89898); }

.stat-value { font-size: 28px; font-weight: 700; color: #303133; }
.stat-label { font-size: 13px; color: #909399; margin-top: 4px; }
.card-title { font-weight: 600; font-size: 15px; }

.message-list { max-height: 300px; overflow-y: auto; }
.message-item { padding: 12px 0; border-bottom: 1px solid #f0f0f0; }
.message-item:last-child { border-bottom: none; }
.msg-header { display: flex; align-items: center; gap: 8px; margin-bottom: 6px; }
.msg-name { font-size: 13px; font-weight: 500; color: #303133; }
.msg-time { font-size: 12px; color: #c0c4cc; margin-left: auto; }
.msg-content { font-size: 13px; color: #606266; padding-left: 36px; line-height: 1.5; }
</style>
