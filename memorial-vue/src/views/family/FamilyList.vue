<template>
  <div>
    <div class="page-wrap">
      <el-card class="left-card">
        <div class="left-toolbar">
          <span class="left-title">家族树</span>
        </div>
        <el-tree
          ref="treeRef"
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
            <el-button v-if="isMobile" class="tree-toggle-btn" @click="treeDrawerVisible = true">
              <el-icon><List /></el-icon>选择家族
            </el-button>
            <el-input v-model="query.keyword" placeholder="搜索家族" clearable class="search-input" @clear="loadData" />
            <el-button type="primary" @click="loadData"><el-icon><Search /></el-icon>搜索</el-button>
          </div>
          <el-button type="primary" @click="openDialog()"><el-icon><Plus /></el-icon>新增家族</el-button>
        </div>

        <el-table :data="tableData" border stripe v-loading="loading">
          <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="type" label="类型" width="80">
          <template #default="{ row }">
            <el-tag :type="row.type === '家族' ? 'danger' : row.type === '支族' ? 'warning' : 'info'" size="small">
              {{ row.type || '家族' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="parentName" label="上级家族" width="140">
          <template #default="{ row }">
            <el-tag v-if="row.parentName" type="warning">{{ row.parentName }}</el-tag>
            <span v-else class="text-muted">顶级</span>
          </template>
        </el-table-column>
        <el-table-column prop="founderName" label="创建人" />
        <el-table-column prop="memberCount" label="成员数" width="100">
          <template #default="{ row }"><el-tag>{{ row.memberCount }} 人</el-tag></template>
        </el-table-column>
        <el-table-column prop="tombCount" label="墓碑数" width="100">
          <template #default="{ row }"><el-tag type="success">{{ row.tombCount }} 座</el-tag></template>
        </el-table-column>
        <el-table-column label="邀请码" width="180">
          <template #default="{ row }">
            <div class="invite-cell">
              <span class="invite-code">{{ row.inviteCode || '-' }}</span>
              <el-button
                v-if="canOperate(row) && row.inviteCode"
                size="small"
                type="primary"
                link
                @click="openInviteDialog(row)"
              >
                二维码
              </el-button>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="家族简介" show-overflow-tooltip />
        <el-table-column prop="address" label="所在地区" width="140" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button v-if="canOperate(row)" size="small" type="primary" link @click="openDialog(row)">编辑</el-button>
            <el-button size="small" type="success" link @click="viewMembers(row)">成员</el-button>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑家族' : '新增家族'" width="500px" destroy-on-close>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item label="类型">
          <el-select v-model="form.type" placeholder="选择类型" style="width: 100%" :disabled="isEdit">
            <el-option label="家族" value="家族" />
            <el-option label="家庭" value="家庭" />
            <el-option label="支族" value="支族" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="form.type && form.type !== '家族'" label="上级" prop="pid">
          <el-tree-select
            v-model="form.pid"
            :data="parentTreeOptions"
            :props="{ value: 'id', label: 'name', children: 'children' }"
            check-strictly
            placeholder="选择上级家族/家庭/支族"
            clearable
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="家族名称" prop="name"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="家族简介"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="联系电话"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="所在地区"><el-input v-model="form.address" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveFamily">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="inviteDialogVisible" title="家族邀请码" width="360px" destroy-on-close>
      <div class="invite-dialog-body" v-if="currentInvite.family">
        <div class="invite-info">
          <div class="invite-title">{{ currentInvite.family.name }}</div>
          <div class="invite-code-large">{{ currentInvite.family.inviteCode }}</div>
        </div>
        <div class="invite-qr-wrap">
          <img v-if="currentInvite.qr" :src="currentInvite.qr" class="invite-qr-img" />
          <span v-else class="text-muted">二维码生成中...</span>
        </div>
        <div class="invite-tip">扫码或转发链接，成员登录后即可加入该家族</div>
      </div>
      <template #footer>
        <el-button @click="inviteDialogVisible = false">关闭</el-button>
        <el-button type="primary" :disabled="!currentInvite.qr" @click="downloadInviteQR">下载二维码</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { Search, Plus, List } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import QRCode from 'qrcode'
import { getFamilyPageList, addFamily, updateFamily, deleteFamily, getFamilyTree } from '@/api/family'

const router = useRouter()
const loading = ref(false)
const saving = ref(false)
const treeLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const treeRef = ref()
const total = ref(0)
const familyTreeOptions = ref([])
const familyTreeData = ref([{ id: -1, name: '全部家族', children: [] }])

const selectedFamilyId = ref(null)
const query = reactive({ keyword: '', familyId: null, pageIndex: 1, pageSize: 10 })
const tableData = ref([])

const inviteDialogVisible = ref(false)
const treeDrawerVisible = ref(false)
const isMobile = ref(false)

function checkMobile() {
  isMobile.value = window.innerWidth < 768
}
const currentInvite = reactive({ family: null, qr: '' })

const form = reactive({ id: null, pid: 0, type: '家族', name: '', description: '', phone: '', address: '' })
const rules = { name: [{ required: true, message: '请输入家族名称', trigger: 'blur' }] }

async function loadData() {
  loading.value = true
  try {
    query.familyId = selectedFamilyId.value
    const res = await getFamilyPageList(query)
    if (res) {
      tableData.value = res.records || []
      total.value = res.total || 0
    }
  } catch { /* 静默 */ }
  finally { loading.value = false }
}

async function loadFamilyTree() {
  treeLoading.value = true
  try {
    const tree = await getFamilyTree()
    familyTreeOptions.value = tree || []
    familyTreeData.value = [{ id: -1, name: '全部家族', children: familyTreeOptions.value }]
  } catch {
    familyTreeOptions.value = []
    familyTreeData.value = [{ id: -1, name: '全部家族', children: [] }]
  } finally {
    treeLoading.value = false
  }
}

function handleTreeNodeClick(node) {
  if (Number(node.id) === -1) {
    selectedFamilyId.value = null
  } else {
    selectedFamilyId.value = Number(node.id)
  }
  query.pageIndex = 1
  loadData()
}
function filterToParentNodes(nodes) {
  if (!nodes || !nodes.length) return []
  return nodes.filter(n => !n.type || n.type === '家族' || n.type === '家庭' || n.type === '支族').map(n => ({
    ...n,
    children: n.children ? filterToParentNodes(n.children) : []
  }))
}
const parentTreeOptions = computed(() => {
  const raw = familyTreeOptions.value || []
  return [{ id: 0, name: '顶级（无上级）', children: filterToParentNodes(raw) }]
})

onMounted(async () => {
  checkMobile()
  window.addEventListener('resize', checkMobile)
  await loadFamilyTree()
  await nextTick()
  treeRef.value?.setCurrentKey?.(-1)
  loadData()
})
onUnmounted(() => window.removeEventListener('resize', checkMobile))

function openDialog(row) {
  isEdit.value = !!row
  if (row) Object.assign(form, { id: row.id, pid: row.pid || 0, type: row.type || '家族', name: row.name, description: row.description, phone: row.phone, address: row.address })
  else Object.assign(form, { id: null, pid: 0, type: '家族', name: '', description: '', phone: '', address: '' })
  loadFamilyTree()
  dialogVisible.value = true
}

function saveFamily() {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    saving.value = true
    try {
      if (isEdit.value) {
        await updateFamily(form)
        ElMessage.success('修改成功')
      } else {
        await addFamily(form)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      loadData()
      loadFamilyTree()
    } catch { /* */ }
    finally { saving.value = false }
  })
}

function viewMembers(row) {
  router.push({ path: '/family/member', query: { familyId: row.id, familyName: row.name } })
}

async function openInviteDialog(row) {
  currentInvite.family = row
  currentInvite.qr = ''
  inviteDialogVisible.value = true
  try {
    const baseUrl = window.location.origin
    const joinUrl = `${baseUrl}/family/join?code=${encodeURIComponent(row.inviteCode)}`
    currentInvite.qr = await QRCode.toDataURL(joinUrl, { width: 320, margin: 1 })
  } catch {
    ElMessage.error('生成二维码失败')
  }
}

function downloadInviteQR() {
  if (!currentInvite.qr || !currentInvite.family) return
  const link = document.createElement('a')
  link.download = `家族邀请码_${currentInvite.family.name}.png`
  link.href = currentInvite.qr
  link.click()
}

function canOperate(row) {
  return ['admin', 'chief', 'member'].includes(row.myRole)
}

function handleDelete(row) {
  ElMessageBox.confirm(`确定删除家族 "${row.name}" 吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteFamily(row.id)
      ElMessage.success('删除成功')
      loadData()
      loadFamilyTree()
    })
}
</script>

<style scoped>
.page-wrap { display: flex; gap: 14px; }
.left-card { width: 280px; flex: 0 0 280px; }
.right-card { flex: 1; min-width: 0; }

@media (max-width: 768px) {
  .page-wrap { flex-direction: column; }
  .left-card { display: none; }
  .right-card { flex: 1; min-width: 0; width: 100%; }
  .toolbar-left .search-input { flex: 1; min-width: 80px; max-width: 160px; }
  .toolbar { flex-wrap: wrap; gap: 8px; }
  .right-card { overflow-x: auto; }
  .right-card :deep(.el-table) { font-size: 12px; min-width: 600px; }
  .right-card :deep(.el-table .el-button) { padding: 2px 4px; }
}

.left-toolbar { display: flex; align-items: center; margin-bottom: 12px; }
.left-title { font-size: 14px; font-weight: 600; color: #303133; }

.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.toolbar-left { display: flex; align-items: center; gap: 8px; flex: 1; min-width: 0; }
.search-input { width: 220px; }
.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 16px; }
.text-muted { color: #c0c4cc; font-size: 13px; }
.invite-cell { display: flex; align-items: center; gap: 6px; }
.invite-code { font-family: monospace; font-size: 13px; }
.invite-dialog-body { text-align: center; }
.invite-info { margin-bottom: 8px; }
.invite-title { font-size: 16px; font-weight: 600; margin-bottom: 4px; }
.invite-code-large { font-family: monospace; font-size: 18px; letter-spacing: 2px; }
.invite-qr-wrap { margin: 10px 0; display: flex; justify-content: center; }
.invite-qr-img { width: 260px; height: 260px; border-radius: 8px; box-shadow: 0 0 8px rgba(0,0,0,0.08); }
.invite-tip { font-size: 13px; color: #909399; }
</style>
