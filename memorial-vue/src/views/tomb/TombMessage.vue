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
        <div class="toolbar" :class="{ 'toolbar-mobile': isMobile }">
          <template v-if="isMobile">
            <div class="toolbar-row toolbar-row-1">
              <el-button class="tree-toggle-btn" @click="treeDrawerVisible = true"><el-icon><List /></el-icon>选择家族</el-button>
            </div>
            <div class="toolbar-row toolbar-row-2">
              <el-input v-model="query.keyword" placeholder="搜索留言内容/墓碑名" clearable class="search-input" @clear="loadData" />
              <el-select v-model="query.status" placeholder="审核状态" clearable class="status-select" @change="loadData">
                <el-option label="待审核" value="pending" />
                <el-option label="已通过" value="approved" />
                <el-option label="已拒绝" value="rejected" />
              </el-select>
              <el-button type="primary" @click="loadData"><el-icon><Search /></el-icon>搜索</el-button>
            </div>
          </template>
          <template v-else>
            <div class="toolbar-left">
              <el-input v-model="query.keyword" placeholder="搜索留言内容/墓碑名" clearable style="width: 240px" @clear="loadData" />
              <el-select v-model="query.status" placeholder="审核状态" clearable style="width: 130px" @change="loadData">
                <el-option label="待审核" value="pending" />
                <el-option label="已通过" value="approved" />
                <el-option label="已拒绝" value="rejected" />
              </el-select>
              <el-button type="primary" @click="loadData"><el-icon><Search /></el-icon>搜索</el-button>
            </div>
          </template>
        </div>

        <div class="current-family">
          {{ currentFamilyTitle }}
          <el-button v-if="isMobile" type="primary" link size="small" @click="treeDrawerVisible = true" class="change-family-link">切换</el-button>
        </div>

        <!-- 桌面端：表格 -->
        <div class="message-desktop">
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
        </div>

        <!-- 手机端：卡片列表 -->
        <div class="message-mobile">
          <div v-loading="loading" class="message-card-list">
            <div v-for="row in tableData" :key="row.id" class="message-card">
              <div class="message-card__main">
                <div class="message-card__tomb">{{ row.tombName || '-' }}</div>
                <div class="message-card__visitor">{{ row.visitorName || '匿名' }}</div>
                <div class="message-card__content">{{ (row.content || '').slice(0, 60) }}{{ (row.content || '').length > 60 ? '…' : '' }}</div>
                <div class="message-card__meta">
                  <el-tag :type="statusType(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
                  <span class="message-card__time">{{ row.createTime }}</span>
                </div>
              </div>
              <div class="message-card__actions">
                <el-button v-if="canOperate(row) && row.status === 'pending'" size="small" type="success" link @click="approve(row)">通过</el-button>
                <el-button v-if="canOperate(row) && row.status === 'pending'" size="small" type="warning" link @click="reject(row)">拒绝</el-button>
                <el-button v-if="canOperate(row)" size="small" type="danger" link @click="handleDelete(row)">删除</el-button>
              </div>
            </div>
            <el-empty v-if="!loading && !tableData?.length" description="暂无留言" :image-size="80" />
          </div>
        </div>

        <div class="pagination-wrap">
          <el-pagination
            :small="isMobile"
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

    <el-drawer v-model="treeDrawerVisible" title="选择家族" direction="ltr" size="280px">
      <el-tree
        v-loading="treeLoading"
        :data="familyTreeData"
        :props="{ label: 'name', children: 'children' }"
        node-key="id"
        highlight-current
        default-expand-all
        @node-click="(node) => { handleTreeNodeClick(node); treeDrawerVisible = false }"
      />
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, computed } from 'vue'
import { Search, List } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMessagePageList, approveMessage, rejectMessage, deleteMessage } from '@/api/tomb'
import { getFamilyTree } from '@/api/family'

const loading = ref(false)
const treeLoading = ref(false)
const total = ref(0)
const familyTreeData = ref([])
const selectedFamilyId = ref(null)
const selectedFamilyName = ref('')
const treeDrawerVisible = ref(false)
const isMobile = ref(window.innerWidth < 768)
function checkMobile() { isMobile.value = window.innerWidth < 768 }
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
  checkMobile()
  window.addEventListener('resize', checkMobile)
  loadFamilyTree()
  loadData()
})
onUnmounted(() => window.removeEventListener('resize', checkMobile))

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
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; flex-wrap: wrap; gap: 10px; }
.toolbar-mobile { flex-direction: column; align-items: stretch; }
.toolbar-mobile .toolbar-row { display: flex; align-items: center; gap: 10px; flex-wrap: wrap; }
.toolbar-mobile .toolbar-row-1 { justify-content: space-between; }
.toolbar-mobile .toolbar-row-2 .search-input { flex: 1; min-width: 0; }
.toolbar-mobile .toolbar-row-2 .status-select { flex: 0 0 auto; }
.toolbar-left { display: flex; gap: 10px; flex-wrap: wrap; }
.tree-toggle-btn { flex-shrink: 0; }
.current-family { margin-bottom: 10px; font-size: 13px; color: #909399; display: flex; align-items: center; gap: 8px; flex-wrap: wrap; }
.change-family-link { flex-shrink: 0; }
.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 16px; }

/* 留言管理：桌面表格 / 手机卡片切换 */
.message-desktop { display: block; }
.message-mobile { display: none; }
.message-card-list { display: flex; flex-direction: column; gap: 12px; }
.message-card {
  padding: 14px;
  border-radius: 10px;
  border: 1px solid #ebeef5;
  background: #fafafa;
}
.message-card__main { margin-bottom: 12px; }
.message-card__tomb { font-weight: 600; font-size: 15px; color: #303133; margin-bottom: 4px; }
.message-card__visitor { font-size: 13px; color: #606266; margin-bottom: 6px; }
.message-card__content { font-size: 13px; color: #606266; line-height: 1.5; word-break: break-word; margin-bottom: 8px; }
.message-card__meta { display: flex; align-items: center; gap: 8px; flex-wrap: wrap; font-size: 12px; color: #909399; }
.message-card__time { flex-shrink: 0; }
.message-card__actions { display: flex; flex-wrap: wrap; gap: 4px; padding-top: 10px; border-top: 1px solid #ebeef5; }

@media (max-width: 768px) {
  .page-wrap { flex-direction: column; }
  .left-card { display: none; }
  .right-card { flex: 1; min-width: 0; width: 100%; overflow-x: visible; }
  .message-desktop { display: none; }
  .message-mobile { display: block; }
  .pagination-wrap { overflow-x: auto; }
  .pagination-wrap :deep(.el-pagination) { flex-wrap: wrap; }
}
</style>
