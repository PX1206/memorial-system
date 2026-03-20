<template>
  <div>
    <el-card>
      <div class="toolbar" :class="{ 'toolbar-mobile': isMobile }">
        <template v-if="isMobile">
          <div class="toolbar-row toolbar-row-1">
            <el-input v-model="query.userName" placeholder="操作人" clearable class="search-input" @clear="loadData" />
            <el-input v-model="query.name" placeholder="日志名称" clearable class="search-input" @clear="loadData" />
          </div>
          <div class="toolbar-row toolbar-row-2">
            <el-button type="primary" @click="loadData" style="flex:1"><el-icon><Search /></el-icon>搜索</el-button>
          </div>
        </template>
        <template v-else>
          <div class="toolbar-left">
            <el-input v-model="query.userName" placeholder="搜索操作人" clearable style="width: 180px" @clear="loadData" />
            <el-input v-model="query.name" placeholder="搜索日志名称" clearable style="width: 180px" @clear="loadData" />
            <el-button type="primary" @click="loadData"><el-icon><Search /></el-icon>搜索</el-button>
          </div>
        </template>
      </div>

      <!-- 桌面端：表格 -->
      <div class="log-desktop">
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="userName" label="操作人" width="120" />
        <el-table-column prop="name" label="日志名称" width="180" />
        <el-table-column prop="module" label="模块" width="120" />
        <el-table-column prop="requestMethod" label="请求方式" width="90" />
        <el-table-column prop="path" label="请求路径" show-overflow-tooltip min-width="200" />
        <el-table-column prop="ip" label="IP地址" width="140" />
        <el-table-column prop="success" label="结果" width="80">
          <template #default="{ row }">
            <el-tag :type="row.success ? 'success' : 'danger'" size="small">{{ row.success ? '成功' : '失败' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="操作时间" width="170" />
      </el-table>
      </div>

      <!-- 手机端：卡片列表 -->
      <div class="log-mobile">
        <div v-loading="loading" class="log-card-list">
          <div v-for="row in tableData" :key="row.id" class="log-card">
            <div class="log-card__main">
              <div class="log-card__header">
                <span class="log-card__name">{{ row.name }}</span>
                <el-tag :type="row.success ? 'success' : 'danger'" size="small">{{ row.success ? '成功' : '失败' }}</el-tag>
              </div>
              <div class="log-card__meta">{{ row.userName }} · {{ row.module }} · {{ row.requestMethod }}</div>
              <div class="log-card__path" v-if="row.path">{{ row.path }}</div>
              <div class="log-card__time">{{ row.createTime }}</div>
            </div>
          </div>
          <el-empty v-if="!loading && !tableData?.length" description="暂无日志" :image-size="80" />
        </div>
      </div>

      <div class="pagination-wrap">
        <el-pagination
          :small="isMobile"
          v-model:current-page="query.pageIndex"
          v-model:page-size="query.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="loadData"
          @size-change="loadData"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { getLogPageList } from '@/api/system'

const loading = ref(false)
const total = ref(0)
const isMobile = ref(window.innerWidth < 768)
function checkMobile() { isMobile.value = window.innerWidth < 768 }
const query = reactive({ userName: '', name: '', pageIndex: 1, pageSize: 10 })
const tableData = ref([])

async function loadData() {
  loading.value = true
  try {
    const res = await getLogPageList(query)
    if (res) {
      tableData.value = res.records || []
      total.value = res.total || 0
    }
  } catch { /* 静默 */ }
  finally { loading.value = false }
}

onMounted(() => {
  checkMobile()
  window.addEventListener('resize', checkMobile)
  loadData()
})
onUnmounted(() => window.removeEventListener('resize', checkMobile))
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; flex-wrap: wrap; gap: 10px; }
.toolbar-mobile { flex-direction: column; align-items: stretch; }
.toolbar-mobile .toolbar-row { display: flex; gap: 10px; align-items: center; flex-wrap: wrap; }
.toolbar-mobile .toolbar-row-1 .search-input { flex: 1; min-width: 0; }
.toolbar-left { display: flex; gap: 10px; flex-wrap: wrap; }
.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 16px; }

.log-desktop { display: block; }
.log-mobile { display: none; }
.log-card-list { display: flex; flex-direction: column; gap: 12px; }
.log-card { padding: 14px; border-radius: 10px; border: 1px solid #ebeef5; background: #fafafa; }
.log-card__header { display: flex; align-items: center; gap: 8px; margin-bottom: 6px; }
.log-card__name { font-weight: 600; font-size: 15px; color: #303133; flex: 1; min-width: 0; overflow: hidden; text-overflow: ellipsis; }
.log-card__meta { font-size: 13px; color: #606266; margin-bottom: 4px; }
.log-card__path { font-size: 12px; color: #909399; word-break: break-all; margin-bottom: 4px; max-height: 36px; overflow: hidden; text-overflow: ellipsis; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; }
.log-card__time { font-size: 12px; color: #c0c4cc; }

@media (max-width: 768px) {
  .log-desktop { display: none; }
  .log-mobile { display: block; }
  .pagination-wrap { overflow-x: auto; }
  .pagination-wrap :deep(.el-pagination) { flex-wrap: wrap; }
}
</style>
