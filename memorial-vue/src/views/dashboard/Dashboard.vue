<template>
  <div class="dashboard">
    <el-row :gutter="[16, 16]" class="quick-row">
      <el-col :span="24">
        <el-card shadow="hover" class="quick-card">
          <template #header><span class="card-title">快捷入口</span></template>
          <div class="quick-links">
            <router-link to="/tomb" class="quick-link">
              <el-icon :size="24"><Place /></el-icon>
              <span>墓碑列表</span>
            </router-link>
            <router-link to="/tomb/message" class="quick-link">
              <el-icon :size="24"><ChatDotRound /></el-icon>
              <span>留言管理</span>
            </router-link>
            <router-link to="/tomb/checkin" class="quick-link">
              <el-icon :size="24"><Calendar /></el-icon>
              <span>打卡记录</span>
            </router-link>
            <router-link to="/family" class="quick-link">
              <el-icon :size="24"><Connection /></el-icon>
              <span>家族列表</span>
            </router-link>
            <router-link to="/family/member" class="quick-link">
              <el-icon :size="24"><User /></el-icon>
              <span>成员管理</span>
            </router-link>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="[16, 16]" class="stat-row">
      <el-col :xs="12" :sm="12" :md="6" :lg="6">
        <el-card shadow="hover" class="stat-card stat-card--primary">
          <div class="stat-icon"><el-icon :size="40"><Place /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.tombCount }}</div>
            <div class="stat-label">墓碑总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6" :lg="6">
        <el-card shadow="hover" class="stat-card stat-card--success">
          <div class="stat-icon"><el-icon :size="40"><User /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.userCount }}</div>
            <div class="stat-label">注册用户</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6" :lg="6">
        <el-card shadow="hover" class="stat-card stat-card--warning">
          <div class="stat-icon"><el-icon :size="40"><Connection /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.familyCount }}</div>
            <div class="stat-label">家族数量</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6" :lg="6">
        <el-card shadow="hover" class="stat-card stat-card--danger">
          <div class="stat-icon"><el-icon :size="40"><ChatDotRound /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.messageCount }}</div>
            <div class="stat-label">留言总数</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="[16, 16]" class="content-row">
      <el-col :xs="24" :sm="24" :md="16" :lg="16">
        <el-card shadow="hover" class="table-card">
          <template #header><span class="card-title">最近打卡记录</span></template>
          <!-- 桌面端：表格 -->
          <div class="checkin-desktop">
            <el-table :data="stats.recentCheckins" style="width: 100%">
              <el-table-column prop="visitorName" label="访客姓名" min-width="80" />
              <el-table-column prop="tombName" label="墓碑" min-width="80" />
              <el-table-column prop="createTime" label="打卡时间" min-width="140" />
              <el-table-column prop="type" label="类型" width="90">
                <template #default="{ row }">
                  <el-tag :type="row.type === '献花' ? 'success' : 'warning'" size="small">{{ row.type }}</el-tag>
                </template>
              </el-table-column>
            </el-table>
          </div>
          <!-- 手机端：卡片列表（与纪念页风格一致） -->
          <div class="checkin-mobile">
            <div class="checkin-card-list" v-if="stats.recentCheckins?.length">
              <div v-for="row in stats.recentCheckins" :key="row.id" class="checkin-card">
                <div class="checkin-card__name">{{ row.visitorName || '匿名' }}</div>
                <div class="checkin-card__tomb">墓碑：{{ row.tombName || '-' }}</div>
                <div class="checkin-card__meta">
                  <el-tag :type="row.type === '献花' ? 'success' : 'warning'" size="small">{{ row.type }}</el-tag>
                  <span class="checkin-card__time">{{ row.createTime }}</span>
                </div>
              </div>
            </div>
            <el-empty v-else description="暂无打卡记录" :image-size="80" />
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="8" :lg="8">
        <el-card shadow="hover" class="message-card">
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
import { Place, User, Connection, ChatDotRound, Calendar } from '@element-plus/icons-vue'
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
.stat-row { margin-bottom: 0; }
.content-row { margin-top: 16px; }
.quick-row { margin-top: 16px; }
.quick-links { display: flex; flex-wrap: wrap; gap: 16px; }
.quick-link {
  display: flex; align-items: center; gap: 10px; padding: 12px 18px;
  border-radius: 10px; border: 1px solid #ebeef5; background: #fafafa;
  color: #606266; text-decoration: none; transition: all 0.2s;
}
.quick-link:hover { border-color: #409eff; background: #ecf5ff; color: #409eff; }
.quick-link .el-icon { color: inherit; }

.stat-card { display: flex; align-items: center; padding: 16px; }
.stat-card :deep(.el-card__body) { display: flex; align-items: center; gap: 12px; width: 100%; }

.stat-icon {
  width: 52px; height: 52px; border-radius: 10px;
  display: flex; align-items: center; justify-content: center;
  color: #fff; flex-shrink: 0;
}
.stat-card--primary .stat-icon { background: linear-gradient(135deg, #409eff, #66b1ff); }
.stat-card--success .stat-icon { background: linear-gradient(135deg, #67c23a, #85ce61); }
.stat-card--warning .stat-icon { background: linear-gradient(135deg, #e6a23c, #ebb563); }
.stat-card--danger .stat-icon { background: linear-gradient(135deg, #f56c6c, #f89898); }

.stat-value { font-size: 24px; font-weight: 700; color: #303133; }
.stat-label { font-size: 12px; color: #909399; margin-top: 2px; }
.card-title { font-weight: 600; font-size: 15px; }

.table-card .el-empty { padding: 20px; }

/* 打卡记录：桌面表格 / 手机卡片切换 */
.checkin-desktop { display: block; }
.checkin-mobile { display: none; }
@media (max-width: 768px) {
  .checkin-desktop { display: none; }
  .checkin-mobile { display: block; }
}
.checkin-card-list { display: flex; flex-direction: column; gap: 12px; max-height: 320px; overflow-y: auto; }
.checkin-card {
  padding: 12px 14px;
  border-radius: 10px;
  border: 1px solid #ebeef5;
  background: #fafafa;
}
.checkin-card__name { font-weight: 600; font-size: 15px; color: #303133; margin-bottom: 4px; }
.checkin-card__tomb { font-size: 13px; color: #606266; margin-bottom: 8px; }
.checkin-card__meta { display: flex; align-items: center; gap: 8px; flex-wrap: wrap; font-size: 12px; color: #909399; }
.checkin-card__time { flex-shrink: 0; }

.message-list { max-height: 300px; overflow-y: auto; }
.message-item { padding: 12px 0; border-bottom: 1px solid #f0f0f0; }
.message-item:last-child { border-bottom: none; }
.msg-header { display: flex; align-items: center; gap: 8px; margin-bottom: 6px; flex-wrap: wrap; }
.msg-name { font-size: 13px; font-weight: 500; color: #303133; flex-shrink: 0; }
.msg-time { font-size: 12px; color: #c0c4cc; margin-left: auto; flex-shrink: 0; white-space: nowrap; }
.msg-content { font-size: 13px; color: #606266; padding-left: 36px; line-height: 1.5; word-break: break-word; }

/* 快捷入口 */
.quick-row { margin-top: 16px; }
.quick-links { display: flex; flex-wrap: wrap; gap: 12px; }
.quick-link {
  display: flex; align-items: center; gap: 10px;
  padding: 12px 18px; border-radius: 10px; border: 1px solid #ebeef5;
  background: #fafafa; color: #606266; text-decoration: none; font-size: 14px;
  transition: border-color 0.2s, background 0.2s, color 0.2s;
}
.quick-link:hover { border-color: #409eff; background: #ecf5ff; color: #409eff; }
.quick-link .el-icon { flex-shrink: 0; }

@media (max-width: 768px) {
  .stat-card { padding: 12px; }
  .stat-icon { width: 44px; height: 44px; }
  .stat-value { font-size: 20px; }
  .stat-label { font-size: 11px; }
  .message-card { margin-top: 16px; }
  .msg-header { gap: 6px; }
  .msg-time { font-size: 11px; }
  .quick-links { gap: 10px; }
  .quick-link { padding: 10px 14px; font-size: 13px; }
}
</style>
