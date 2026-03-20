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
              <el-input v-model="query.keyword" placeholder="搜索墓碑名/访客名" clearable class="search-input" @clear="loadData" />
              <el-select v-model="query.type" placeholder="打卡类型" clearable class="type-select" @change="loadData">
                <el-option label="献花" value="献花" />
                <el-option label="点蜡烛" value="点蜡烛" />
                <el-option label="扫码访问" value="扫码访问" />
              </el-select>
              <el-button type="primary" @click="loadData"><el-icon><Search /></el-icon>搜索</el-button>
            </div>
          </template>
          <template v-else>
            <div class="toolbar-left">
              <el-input v-model="query.keyword" placeholder="搜索墓碑名/访客名" clearable style="width: 220px" @clear="loadData" />
              <el-select v-model="query.type" placeholder="打卡类型" clearable style="width: 130px" @change="loadData">
                <el-option label="献花" value="献花" />
                <el-option label="点蜡烛" value="点蜡烛" />
                <el-option label="扫码访问" value="扫码访问" />
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
        <div class="checkin-desktop">
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
        </div>

        <!-- 手机端：打卡卡片列表 -->
        <div class="checkin-mobile">
          <div v-loading="loading" class="checkin-card-list">
            <div v-for="row in tableData" :key="row.id" class="checkin-card">
              <div class="checkin-card__name">{{ row.visitorName || '匿名' }}</div>
              <div class="checkin-card__tomb">墓碑：{{ row.tombName || '-' }}</div>
              <div class="checkin-card__meta">
                <el-tag :type="row.type === '献花' ? 'success' : row.type === '点蜡烛' ? 'warning' : 'info'" size="small">{{ row.type }}</el-tag>
                <span class="checkin-card__time">{{ row.createTime }}</span>
              </div>
              <div v-if="row.location" class="checkin-card__location">{{ row.location }}</div>
            </div>
            <el-empty v-if="!loading && !tableData?.length" description="暂无打卡记录" :image-size="80" />
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
import { getCheckinPageList } from '@/api/tomb'
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

const query = reactive({ keyword: '', type: '', familyId: null, pageIndex: 1, pageSize: 10 })
const tableData = ref([])

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
    const res = await getCheckinPageList(query)
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
.toolbar-mobile .toolbar-row-2 .type-select { flex: 0 0 auto; }
.toolbar-left { display: flex; gap: 10px; flex-wrap: wrap; }
.tree-toggle-btn { flex-shrink: 0; }
.current-family { margin-bottom: 10px; font-size: 13px; color: #909399; display: flex; align-items: center; gap: 8px; flex-wrap: wrap; }
.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 16px; }

@media (max-width: 768px) {
  .page-wrap { flex-direction: column; }
  .left-card { display: none; }
  .right-card { flex: 1; min-width: 0; width: 100%; overflow-x: auto; }
  .right-card :deep(.el-table) { font-size: 12px; min-width: 600px; }
  .pagination-wrap { overflow-x: auto; }
  .pagination-wrap :deep(.el-pagination) { flex-wrap: wrap; }
}
</style>
