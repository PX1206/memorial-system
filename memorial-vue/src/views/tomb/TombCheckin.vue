<template>
  <div>
    <el-card>
      <div class="toolbar">
        <div class="toolbar-left">
          <el-input v-model="query.keyword" placeholder="搜索墓碑名/访客名" clearable style="width: 220px" @clear="loadData" />
          <el-select v-model="query.type" placeholder="打卡类型" clearable style="width: 130px" @change="loadData">
            <el-option label="献花" value="献花" />
            <el-option label="点蜡烛" value="点蜡烛" />
            <el-option label="扫码访问" value="扫码访问" />
          </el-select>
          <el-button type="primary" @click="loadData"><el-icon><Search /></el-icon>搜索</el-button>
        </div>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="tombName" label="墓碑" />
        <el-table-column prop="visitorName" label="访客姓名" />
        <el-table-column prop="type" label="打卡类型" width="120">
          <template #default="{ row }">
            <el-tag :type="row.type === '献花' ? 'success' : row.type === '点蜡烛' ? 'warning' : 'info'">{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ip" label="IP地址" width="140" />
        <el-table-column prop="location" label="位置" />
        <el-table-column prop="createTime" label="打卡时间" width="170" />
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="query.pageIndex"
          v-model:page-size="query.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
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
import { getCheckinPageList } from '@/api/tomb'

const loading = ref(false)
const total = ref(0)
const query = reactive({ keyword: '', type: '', pageIndex: 1, pageSize: 10 })
const tableData = ref([])

async function loadData() {
  loading.value = true
  try {
    const res = await getCheckinPageList(query)
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
