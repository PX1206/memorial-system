<template>
  <div>
    <div class="page-wrap">
      <el-card class="left-card">
        <div class="left-toolbar">
          <el-button @click="goBack"><el-icon><ArrowLeft /></el-icon>返回</el-button>
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
            <span class="family-title">{{ currentFamilyTitle }}</span>
            <el-input
              v-model="query.keyword"
              placeholder="搜索"
              clearable
              class="search-input"
              @clear="handleSearch"
            />
            <el-button type="primary" @click="handleSearch">搜索</el-button>
          </div>
          <el-button v-if="canOperate" type="primary" @click="openDialog()"><el-icon><Plus /></el-icon>添加成员</el-button>
        </div>

        <div v-if="familyId && canOperate" class="apply-section">
          <div class="apply-section-title">待审核申请</div>
          <el-table :data="applyList" border stripe v-loading="applyLoading" size="small">
            <el-table-column prop="userName" label="申请人" width="120" />
            <el-table-column prop="relation" label="关系" width="100" />
            <el-table-column prop="reason" label="申请理由" show-overflow-tooltip />
            <el-table-column prop="createTime" label="申请时间" width="170" />
            <el-table-column label="操作" width="160" fixed="right">
              <template #default="{ row }">
                <el-button size="small" type="success" link @click="handleApproveApply(row)">通过</el-button>
                <el-button size="small" type="danger" link @click="handleRejectApply(row)">拒绝</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div v-if="applyList.length === 0 && !applyLoading" class="apply-empty">暂无待审核申请</div>
        </div>

        <el-table :data="tableData" border stripe v-loading="loading">
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="name" label="姓名" width="100" />
          <el-table-column prop="userNickname" label="绑定用户" width="110">
            <template #default="{ row }">
              <span v-if="row.userNickname" class="user-nickname">{{ row.userNickname }}</span>
              <span v-else class="text-muted">-</span>
            </template>
          </el-table-column>
          <el-table-column prop="familyName" label="所属家族" width="140">
            <template #default="{ row }">
              <el-tag type="warning">{{ row.familyName || familyName }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="relation" label="关系" width="100">
            <template #default="{ row }"><el-tag>{{ row.relation }}</el-tag></template>
          </el-table-column>
          <el-table-column prop="phone" label="联系电话" />
          <el-table-column prop="role" label="家族角色" width="120">
            <template #default="{ row }">
              <el-tag :type="row.role === '族长' ? 'danger' : row.role === '管理员' ? 'warning' : 'info'">{{ row.role }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="加入时间" width="170" />
          <el-table-column v-if="canOperate" label="操作" width="260" fixed="right">
            <template #default="{ row }">
              <el-button size="small" type="primary" link @click="openDialog(row)">编辑</el-button>
              <el-button v-if="!row.userId && !row.userNickname" size="small" type="success" link @click="openBindQrDialog(row)">绑定二维码</el-button>
              <el-button v-else-if="row.userId || row.userNickname" size="small" type="warning" link @click="handleUnbind(row)">解绑</el-button>
              <el-button size="small" type="danger" link @click="handleRemove(row)">移除</el-button>
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
        :data="familyTreeData"
        :props="{ label: 'name', children: 'children' }"
        node-key="id"
        highlight-current
        default-expand-all
        @node-click="(node) => { handleTreeNodeClick(node); treeDrawerVisible = false }"
      />
    </el-drawer>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑成员' : '添加成员'" width="500px" destroy-on-close>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item label="所属家族" prop="familyId">
          <el-tree-select
            v-model="form.familyId"
            :data="familyTreeOptions"
            :props="{ value: 'id', label: 'name', children: 'children' }"
            check-strictly
            placeholder="选择所属家族"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="姓名" prop="name"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="关系" prop="relation">
          <el-select v-model="form.relation" style="width: 100%">
            <el-option v-for="r in relations" :key="r" :label="r" :value="r" />
          </el-select>
        </el-form-item>
        <el-form-item label="联系电话"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="家族角色">
          <el-select v-model="form.role" style="width: 100%" :disabled="!canSetRole">
            <el-option v-if="canSetAdmin" label="管理员" value="管理员" />
            <el-option label="成员" value="成员" />
          </el-select>
          <span v-if="!canSetRole" class="role-hint">仅超级管理员可指定管理员</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveMember">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="bindQrVisible" title="成员绑定二维码" width="360px" destroy-on-close>
      <div class="bind-qr-body" v-if="currentBind.member">
        <div class="bind-qr-info">
          <div class="bind-qr-name">{{ currentBind.member.name }}（{{ currentBind.member.familyName }}）</div>
          <div class="bind-qr-code">{{ currentBind.member.bindCode }}</div>
        </div>
        <div class="bind-qr-wrap">
          <img v-if="currentBind.qr" :src="currentBind.qr" class="bind-qr-img" />
          <span v-else class="text-muted">二维码生成中...</span>
        </div>
        <div class="bind-qr-tip">扫码或转发链接，用户登录后即可绑定到该成员</div>
      </div>
      <template #footer>
        <el-button @click="bindQrVisible = false">关闭</el-button>
        <el-button type="primary" :disabled="!currentBind.qr" @click="downloadBindQR">下载二维码</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, computed, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Plus, ArrowLeft, List } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMemberPageList, addFamilyMember, updateFamilyMember, removeFamilyMember, unbindMember, getFamilyTree, getFamilyApplyList, approveFamilyApply, rejectFamilyApply, getMyRoleInFamily, ensureMemberBindCode } from '@/api/family'
import QRCode from 'qrcode'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const bindQrVisible = ref(false)
const currentBind = reactive({ member: null, qr: '' })
const isEdit = ref(false)
const formRef = ref()
const total = ref(0)
const familyTreeOptions = ref([])
const treeLoading = ref(false)
const familyTreeData = ref([])
const treeRef = ref()
const treeDrawerVisible = ref(false)
const isMobile = ref(false)

function checkMobile() {
  isMobile.value = window.innerWidth < 768
}

const familyId = ref(route.query.familyId !== undefined ? Number(route.query.familyId) : null)
const familyName = ref(route.query.familyName || '')
const relations = ['父亲', '母亲', '儿子', '女儿', '兄弟', '姐妹', '爷爷', '奶奶', '外公', '外婆', '叔伯', '姑姨', '其他']

const query = reactive({ familyId: familyId.value, keyword: '', pageIndex: 1, pageSize: 10 })
const tableData = ref([])

const applyList = ref([])
const applyLoading = ref(false)
const myRoleInFamily = ref(null)
const canOperate = computed(() => ['admin', 'chief', 'member'].includes(myRoleInFamily.value))
const canSetAdmin = computed(() => myRoleInFamily.value === 'admin')
const canSetRole = computed(() => canOperate.value)

const form = reactive({ id: null, familyId: familyId.value, name: '', relation: '', phone: '', role: '成员' })
const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  relation: [{ required: true, message: '请选择关系', trigger: 'change' }],
  familyId: [{ required: true, message: '请选择所属家族', trigger: 'change' }]
}

async function loadData() {
  loading.value = true
  try {
    query.familyId = familyId.value
    const res = await getMemberPageList(query)
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

onMounted(async () => {
  checkMobile()
  window.addEventListener('resize', checkMobile)
  await loadFamilyTree()
  await nextTick()
  const key = familyId.value === null ? -1 : familyId.value
  treeRef.value?.setCurrentKey?.(key)
  await loadMyRole()
  await loadData()
  await loadApplyList()
})
onUnmounted(() => window.removeEventListener('resize', checkMobile))

function goBack() { router.push('/family') }

const currentFamilyTitle = computed(() => {
  if (familyId.value === null) return '全部家族 - 成员管理'
  if (familyName.value) return `${familyName.value} - 成员管理`
  return '成员管理'
})

async function handleTreeNodeClick(node) {
  if (Number(node.id) === -1) {
    familyId.value = null
    familyName.value = ''
    myRoleInFamily.value = null
  } else {
    familyId.value = Number(node.id)
    familyName.value = node.name || ''
    await loadMyRole()
  }
  query.pageIndex = 1
  loadData()
  loadApplyList()
}

async function loadMyRole() {
  if (!familyId.value) {
    myRoleInFamily.value = null
    return
  }
  try {
    const res = await getMyRoleInFamily(familyId.value)
    myRoleInFamily.value = res || null
  } catch {
    myRoleInFamily.value = null
  }
}

async function loadApplyList() {
  if (!familyId.value) {
    applyList.value = []
    return
  }
  applyLoading.value = true
  try {
    const res = await getFamilyApplyList(familyId.value, 'pending')
    applyList.value = res || []
  } catch { applyList.value = [] }
  finally { applyLoading.value = false }
}

function handleApproveApply(row) {
  ElMessageBox.confirm(`确定通过 "${row.userName}" 的加入申请吗？`, '提示', { type: 'info' })
    .then(async () => {
      await approveFamilyApply(row.id)
      ElMessage.success('已通过')
      loadApplyList()
      loadData()
    })
}

function handleRejectApply(row) {
  ElMessageBox.prompt('请输入拒绝理由（选填）', '拒绝申请', {
    confirmButtonText: '拒绝',
    cancelButtonText: '取消',
    inputPlaceholder: '选填'
  }).then(async ({ value }) => {
    await rejectFamilyApply(row.id, value || '')
    ElMessage.success('已拒绝')
    loadApplyList()
  })
}

function handleSearch() {
  query.pageIndex = 1
  loadData()
}

function openDialog(row) {
  isEdit.value = !!row
  if (row) Object.assign(form, { id: row.id, familyId: row.familyId, name: row.name, relation: row.relation, phone: row.phone, role: row.role })
  else Object.assign(form, { id: null, familyId: familyId.value, name: '', relation: '', phone: '', role: '成员' })
  dialogVisible.value = true
}

function saveMember() {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    saving.value = true
    try {
      if (isEdit.value) {
        await updateFamilyMember(form)
        ElMessage.success('修改成功')
        dialogVisible.value = false
        loadData()
      } else {
        const vo = await addFamilyMember(form)
        ElMessage.success('添加成功')
        dialogVisible.value = false
        loadData()
        if (vo && vo.bindCode) {
          currentBind.member = vo
          currentBind.qr = ''
          bindQrVisible.value = true
          try {
            const baseUrl = window.location.origin
            const bindUrl = `${baseUrl}/family/member/bind?code=${encodeURIComponent(vo.bindCode)}`
            currentBind.qr = await QRCode.toDataURL(bindUrl, { width: 320, margin: 1 })
          } catch {
            ElMessage.error('生成二维码失败')
          }
        }
      }
    } catch { /* */ }
    finally { saving.value = false }
  })
}

async function openBindQrDialog(row) {
  currentBind.member = null
  currentBind.qr = ''
  bindQrVisible.value = true
  try {
    const vo = await ensureMemberBindCode(row.id)
    currentBind.member = vo
    if (vo && vo.bindCode) {
      const baseUrl = window.location.origin
      const bindUrl = `${baseUrl}/family/member/bind?code=${encodeURIComponent(vo.bindCode)}`
      currentBind.qr = await QRCode.toDataURL(bindUrl, { width: 320, margin: 1 })
    }
  } catch {
    ElMessage.error('获取绑定码失败')
    bindQrVisible.value = false
  }
}

function downloadBindQR() {
  if (!currentBind.qr || !currentBind.member) return
  const link = document.createElement('a')
  link.download = `成员绑定_${currentBind.member.name}.png`
  link.href = currentBind.qr
  link.click()
}

function handleUnbind(row) {
  ElMessageBox.confirm(`确定解绑 "${row.name}" 与用户 "${row.userNickname || '已绑定用户'}" 的关联吗？解绑后可重新扫码绑定。`, '解绑确认', { type: 'warning' })
    .then(async () => {
      await unbindMember(row.id)
      ElMessage.success('解绑成功')
      loadData()
    })
}

function handleRemove(row) {
  ElMessageBox.confirm(`确定移除成员 "${row.name}" 吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await removeFamilyMember(row.id)
      ElMessage.success('已移除')
      loadData()
    })
}
</script>

<style scoped>
.page-wrap { display: flex; gap: 14px; }
.left-card { width: 280px; flex: 0 0 280px; }
.right-card { flex: 1; min-width: 0; }

.toolbar-left .search-input { width: 220px; }

@media (max-width: 768px) {
  .left-card { display: none; }
  .right-card { flex: 1; min-width: 0; width: 100%; }
  .toolbar-left .search-input { flex: 1; min-width: 80px; max-width: 160px; }
  .family-title { font-size: 14px; max-width: 120px; overflow: hidden; text-overflow: ellipsis; }
  .right-card { overflow-x: auto; }
  .right-card :deep(.el-table) { font-size: 12px; min-width: 600px; }
}

.left-toolbar { display: flex; align-items: center; justify-content: space-between; margin-bottom: 12px; }
.left-title { font-size: 14px; font-weight: 600; color: #303133; }

.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.toolbar-left { display: flex; align-items: center; gap: 12px; }
.family-title { font-size: 16px; font-weight: 600; color: #303133; }
.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 16px; }

.apply-section { margin-bottom: 16px; padding: 12px; background: #fafafa; border-radius: 8px; }
.apply-section-title { font-size: 14px; font-weight: 600; margin-bottom: 10px; color: #303133; }
.apply-empty { color: #909399; font-size: 13px; padding: 12px 0; }
.role-hint { font-size: 12px; color: #909399; margin-left: 8px; }

.bind-qr-body { text-align: center; }
.bind-qr-info { margin-bottom: 8px; }
.bind-qr-name { font-size: 16px; font-weight: 600; margin-bottom: 4px; }
.bind-qr-code { font-family: monospace; font-size: 18px; letter-spacing: 2px; }
.bind-qr-wrap { margin: 10px 0; display: flex; justify-content: center; }
.bind-qr-img { width: 260px; height: 260px; border-radius: 8px; box-shadow: 0 0 8px rgba(0,0,0,0.08); }
.bind-qr-tip { font-size: 13px; color: #909399; }
.text-muted { color: #c0c4cc; font-size: 13px; }
</style>
