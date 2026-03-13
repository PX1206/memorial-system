<template>
  <div>
    <el-card>
      <div class="toolbar">
        <div class="toolbar-left">
          <el-input v-model="query.userName" placeholder="搜索操作人" clearable style="width: 180px" @clear="loadData" />
          <el-input v-model="query.name" placeholder="搜索日志名称" clearable style="width: 180px" @clear="loadData" />
          <el-button type="primary" @click="loadData"><el-icon><Search /></el-icon>搜索</el-button>
        </div>
      </div>

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

      <div class="pagination-wrap">
        <el-pagination
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
import { ref, reactive, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { getLogPageList } from '@/api/system'

const loading = ref(false)
const total = ref(0)
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

onMounted(() => loadData())
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.toolbar-left { display: flex; gap: 10px; flex-wrap: wrap; }
.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>
