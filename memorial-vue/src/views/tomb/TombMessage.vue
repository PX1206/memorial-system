<template>
  <div>
    <div class="page-wrap">
      <el-card class="left-card">
        <div class="left-toolbar">
          <span class="left-title">家族树</span>
        </div>
        <el-tree
          v-loading="treeLoading"
          :data="familyTreeData"
          :props="{ label: 'name', children: 'children' }"
          node-key="id"
          highlight-current
          default-expand-all
          @node-click="handleTreeNodeClick"
        />
      </el-card>

      <el-card class="right-card">
        <div class="toolbar">
          <div class="toolbar-left">
            <el-input v-model="query.keyword" placeholder="搜索留言内容/墓碑名" clearable style="width: 240px" @clear="loadData" />
            <el-select v-model="query.status" placeholder="审核状态" clearable style="width: 130px" @change="loadData">
              <el-option label="待审核" value="pending" />
              <el-option label="已通过" value="approved" />
              <el-option label="已拒绝" value="rejected" />
            </el-select>
            <el-button type="primary" @click="loadData"><el-icon><Search /></el-icon>搜索</el-button>
          </div>
        </div>

        <div class="current-family">{{ currentFamilyTitle }}</div>

        <el-table :data="tableData" border stripe v-loading="loading">
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="tombName" label="墓碑" width="120" />
          <el-table-column prop="visitorName" label="留言人" width="120" />
          <el-table-column prop="content" label="留言内容" show-overflow-tooltip min-width="200" />
          <el-table-column prop="createTime" label="留言时间" width="170" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button v-if="canOperate(row) && row.status === 'pending'" size="small" type="success" link @click="approve(row)">通过</el-button>
              <el-button v-if="canOperate(row) && row.status === 'pending'" size="small" type="warning" link @click="reject(row)">拒绝</el-button>
              <el-button v-if="canOperate(row)" size="small" type="danger" link @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMessagePageList, approveMessage, rejectMessage, deleteMessage } from '@/api/tomb'
import { getFamilyTree } from '@/api/family'

const loading = ref(false)
const treeLoading = ref(false)
const total = ref(0)
const familyTreeData = ref([])
const selectedFamilyId = ref(null)
const selectedFamilyName = ref('')
const query = reactive({ keyword: '', status: '', familyId: null, pageIndex: 1, pageSize: 10 })
const tableData = ref([])

const statusType = (s) => ({ approved: 'success', pending: 'warning', rejected: 'danger' })[s] || 'info'
const statusText = (s) => ({ approved: '已通过', pending: '待审核', rejected: '已拒绝' })[s] || s

function canOperate(row) {
  return row && ['admin', 'chief', 'member'].includes(row.myRole)
}

const currentFamilyTitle = computed(() => {
  if (selectedFamilyId.value === null) return '当前筛选：全部家族'
  return `当前筛选：${selectedFamilyName.value || '家族'}（含子家族）`
})

async function loadFamilyTree() {
  treeLoading.value = true
  try {
    const tree = await getFamilyTree()
    familyTreeData.value = [{ id: -1, name: '全部家族', children: tree || [] }]
  } catch {
    familyTreeData.value = [{ id: -1, name: '全部家族', children: [] }]
  } finally {
    treeLoading.value = false
  }
}

function handleTreeNodeClick(node) {
  if (Number(node.id) === -1) {
    selectedFamilyId.value = null
    selectedFamilyName.value = ''
    query.familyId = null
  } else {
    selectedFamilyId.value = Number(node.id)
    selectedFamilyName.value = node.name || ''
    query.familyId = Number(node.id)
  }
  query.pageIndex = 1
  loadData()
}

async function loadData() {
  loading.value = true
  query.familyId = selectedFamilyId.value === null ? null : selectedFamilyId.value
  if (query.familyId === -1) query.familyId = null
  try {
    const res = await getMessagePageList(query)
    if (res) {
      tableData.value = res.records || []
      total.value = res.total || 0
    }
  } catch { /* 静默 */ }
  finally { loading.value = false }
}

onMounted(() => {
  loadFamilyTree()
  loadData()
})

async function approve(row) {
  await approveMessage(row.id)
  ElMessage.success('已通过')
  loadData()
}

function reject(row) {
  ElMessageBox.prompt('请输入拒绝理由', '拒绝留言', { type: 'warning' })
    .then(async ({ value }) => {
      await rejectMessage(row.id, value)
      ElMessage.success('已拒绝')
      loadData()
    })
}

function handleDelete(row) {
  ElMessageBox.confirm('确定删除该留言吗？', '提示', { type: 'warning' })
    .then(async () => {
      await deleteMessage(row.id)
      ElMessage.success('删除成功')
      loadData()
    })
}
</script>

<style scoped>
.page-wrap { display: flex; gap: 14px; }
.left-card { width: 280px; flex: 0 0 280px; }
.right-card { flex: 1; }
.left-toolbar { display: flex; align-items: center; margin-bottom: 12px; }
.left-title { font-size: 14px; font-weight: 600; color: #303133; }
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.toolbar-left { display: flex; gap: 10px; }
.current-family { margin-bottom: 10px; font-size: 13px; color: #909399; }
.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>
