<template>
  <div>
    <el-card>
      <div class="toolbar" :class="{ 'toolbar-mobile': isMobile }">
        <template v-if="isMobile">
          <div class="toolbar-row toolbar-row-1">
            <el-button type="primary" @click="openDialog()" v-permission="'sys:user:add'"><el-icon><Plus /></el-icon>新增用户</el-button>
          </div>
          <div class="toolbar-row toolbar-row-2">
            <el-input v-model="query.keyword" placeholder="搜索用户名/手机号" clearable class="search-input" @clear="loadData" />
            <el-button type="primary" @click="loadData"><el-icon><Search /></el-icon>搜索</el-button>
          </div>
        </template>
        <template v-else>
          <div class="toolbar-left">
            <el-input v-model="query.keyword" placeholder="搜索用户名/手机号" clearable class="search-input" @clear="loadData" />
            <el-button type="primary" @click="loadData"><el-icon><Search /></el-icon>搜索</el-button>
          </div>
          <el-button type="primary" @click="openDialog()" v-permission="'sys:user:add'"><el-icon><Plus /></el-icon>新增用户</el-button>
        </template>
      </div>

      <!-- 桌面端：表格 -->
      <div v-show="!isMobile" class="user-desktop">
        <div class="table-scroll-wrap">
          <el-table :data="tableData" border stripe v-loading="loading" style="min-width: 800px">
            <el-table-column prop="id" label="ID" width="70" />
            <el-table-column label="头像" width="80">
              <template #default="{ row }">
                <el-avatar :size="36" :src="row.headImg || ''" />
              </template>
            </el-table-column>
            <el-table-column prop="username" label="用户名" />
            <el-table-column prop="nickname" label="昵称" />
            <el-table-column prop="mobile" label="手机号" />
            <el-table-column prop="sex" label="性别" width="80">
              <template #default="{ row }">{{ row.sex === 1 ? '男' : row.sex === 2 ? '女' : '未知' }}</template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="loginTime" label="最后登录" width="170" />
            <el-table-column prop="createTime" label="注册时间" width="170" />
            <el-table-column label="操作" width="300" fixed="right">
              <template #default="{ row }">
                <el-button size="small" type="primary" link @click="openDialog(row)" v-permission="'sys:user:edit'">编辑</el-button>
                <el-button size="small" type="warning" link @click="resetPwd(row)" v-permission="'sys:user:resetPwd'">重置密码</el-button>
                <el-button size="small" type="success" link @click="openRoleDialog(row)" v-permission="'sys:user:role'">角色</el-button>
                <el-button v-if="row.status === 1" size="small" type="info" link @click="handleDisable(row)" v-permission="'sys:user:disable'">禁用</el-button>
                <el-button v-if="row.status === 2 || row.status === 3 || row.status === 4" size="small" type="success" link @click="handleRestore(row)" v-permission="'sys:user:restore'">恢复</el-button>
                <el-button size="small" type="danger" link @click="handleDelete(row)" v-permission="'sys:user:delete'">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>

      <!-- 手机端：卡片列表 -->
      <div v-show="isMobile" class="user-mobile">
        <div v-loading="loading" class="user-card-list">
          <div v-for="row in tableData" :key="row.id" class="user-card">
            <div class="user-card__main">
              <el-avatar :size="48" :src="row.headImg || ''" class="user-card__photo" />
              <div class="user-card__info">
                <div class="user-card__name">{{ row.nickname || row.username }}</div>
                <div class="user-card__meta">{{ row.username }} · {{ row.mobile }}</div>
                <el-tag :type="statusType(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
              </div>
            </div>
            <div class="user-card__actions">
              <el-button size="small" type="primary" link @click="openDialog(row)" v-permission="'sys:user:edit'">编辑</el-button>
              <el-button size="small" type="warning" link @click="resetPwd(row)" v-permission="'sys:user:resetPwd'">重置</el-button>
              <el-button size="small" type="success" link @click="openRoleDialog(row)" v-permission="'sys:user:role'">角色</el-button>
              <el-button v-if="row.status === 1" size="small" type="info" link @click="handleDisable(row)" v-permission="'sys:user:disable'">禁用</el-button>
              <el-button v-if="row.status === 2 || row.status === 3 || row.status === 4" size="small" type="success" link @click="handleRestore(row)" v-permission="'sys:user:restore'">恢复</el-button>
              <el-button size="small" type="danger" link @click="handleDelete(row)" v-permission="'sys:user:delete'">删除</el-button>
            </div>
          </div>
          <el-empty v-if="!loading && !tableData?.length" description="暂无用户" :image-size="80" />
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑用户' : '新增用户'" :width="isMobile ? '95%' : '500px'" destroy-on-close>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname"><el-input v-model="form.nickname" /></el-form-item>
        <el-form-item label="手机号" prop="mobile"><el-input v-model="form.mobile" maxlength="11" /></el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.sex">
            <el-radio :value="1">男</el-radio>
            <el-radio :value="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="!isEdit" label="角色">
          <el-checkbox-group v-model="form.roleIds">
            <el-checkbox v-for="r in allRoles" :key="r.id" :label="r.id">{{ r.name }}</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item v-if="!isEdit" label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveUser">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="roleDialogVisible" title="分配角色" :width="isMobile ? '95%' : '400px'" destroy-on-close>
      <div class="role-title">用户：{{ roleUser.username }}</div>
      <el-checkbox-group v-model="selectedRoleIds">
        <el-checkbox v-for="role in allRoles" :key="role.id" :value="role.id" :label="role.name" />
      </el-checkbox-group>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="roleLoading" @click="saveUserRole">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { Search, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserPageList, addUser, updateUser, deleteUser, disableUser, restoreUser, resetPassword } from '@/api/user'
import { getAllRoles, getUserRoleIds, saveUserRoles } from '@/api/role'
import { encryptPassword } from '@/utils/rsa'

const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const roleDialogVisible = ref(false)
const roleLoading = ref(false)
const roleUser = ref({})
const selectedRoleIds = ref([])
const allRoles = ref([])
const isEdit = ref(false)
const formRef = ref()
const total = ref(0)
const isMobile = ref(window.innerWidth < 768)
function checkMobile() { isMobile.value = window.innerWidth < 768 }

const query = reactive({ keyword: '', pageIndex: 1, pageSize: 10 })
const tableData = ref([])

const form = reactive({ id: null, username: '', nickname: '', mobile: '', sex: 1, password: '', roleIds: [] })

const statusType = (s) => ({ 1: 'success', 2: 'danger', 3: 'warning', 4: 'warning' })[s] || 'info'
const statusText = (s) => ({ 1: '正常', 2: '禁用', 3: '冻结', 4: '临时冻结', 0: '注销' })[s] || '未知'

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  mobile: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式错误', trigger: 'blur' }
  ],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function loadData() {
  loading.value = true
  try {
    const res = await getUserPageList(query)
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

async function openDialog(row) {
  isEdit.value = !!row
  if (row) {
    Object.assign(form, { ...row, password: '', roleIds: [] })
  } else {
    Object.assign(form, { id: null, username: '', nickname: '', mobile: '', sex: 1, password: '', roleIds: [] })
    try {
      const roles = await getAllRoles()
      allRoles.value = roles || []
      const userRole = allRoles.value.find(r => r.code === 'user' || r.name === '普通用户')
      form.roleIds = userRole ? [userRole.id] : (allRoles.value[0] ? [allRoles.value[0].id] : [])
    } catch { allRoles.value = []; form.roleIds = [] }
  }
  dialogVisible.value = true
}

function saveUser() {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    saving.value = true
    try {
      if (isEdit.value) {
        await updateUser({ id: form.id, username: form.username, nickname: form.nickname, mobile: form.mobile, sex: form.sex })
        ElMessage.success('修改成功')
      } else {
        const encrypted = encryptPassword(form.password)
        await addUser({ username: form.username, nickname: form.nickname, mobile: form.mobile, sex: form.sex, password: encrypted, roleIds: form.roleIds })
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      loadData()
    } catch { /* */ }
    finally { saving.value = false }
  })
}

function handleDisable(row) {
  ElMessageBox.confirm(`确定禁用用户 "${row.username}" 吗？`, '提示', { type: 'warning' })
    .then(async () => { await disableUser(row.id); ElMessage.success('已禁用'); loadData() })
}

function handleRestore(row) {
  ElMessageBox.confirm(`确定恢复用户 "${row.username}" 吗？`, '提示')
    .then(async () => { await restoreUser(row.id); ElMessage.success('已恢复'); loadData() })
}

function resetPwd(row) {
  ElMessageBox.prompt('请输入新密码（8位以上，包含数字和字母）', '重置密码', { type: 'warning', inputType: 'password' })
    .then(async ({ value }) => {
      const encrypted = encryptPassword(value)
      await resetPassword({ userId: row.id, password: encrypted })
      ElMessage.success('密码已重置')
    })
}

function handleDelete(row) {
  ElMessageBox.confirm(`确定删除用户 "${row.username}" 吗？`, '提示', { type: 'warning' })
    .then(async () => { await deleteUser(row.id); ElMessage.success('删除成功'); loadData() })
}

async function openRoleDialog(row) {
  roleUser.value = row
  try {
    const [roles, ids] = await Promise.all([getAllRoles(), getUserRoleIds(row.id)])
    allRoles.value = roles || []
    selectedRoleIds.value = ids || []
  } catch { /* handled by interceptor */ }
  roleDialogVisible.value = true
}

async function saveUserRole() {
  roleLoading.value = true
  try {
    await saveUserRoles({ userId: roleUser.value.id, roleIds: selectedRoleIds.value })
    ElMessage.success('角色保存成功')
    roleDialogVisible.value = false
  } catch { /* handled by interceptor */ } finally {
    roleLoading.value = false
  }
}
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; flex-wrap: wrap; gap: 10px; }
.toolbar-left { display: flex; align-items: center; gap: 10px; flex-wrap: nowrap; flex-shrink: 0; }
.search-input { width: 220px; flex-shrink: 0; }
.table-scroll-wrap { overflow-x: auto; -webkit-overflow-scrolling: touch; }
.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 16px; overflow-x: auto; }
.role-title { font-size: 14px; font-weight: 600; margin-bottom: 12px; color: #303133; }

.toolbar-mobile { flex-direction: column; align-items: stretch; }
.toolbar-mobile .toolbar-row { display: flex; align-items: center; gap: 10px; flex-wrap: wrap; }
.toolbar-mobile .toolbar-row-1 { justify-content: space-between; }
.toolbar-mobile .toolbar-row-2 .search-input { flex: 1; min-width: 0; }

@media (max-width: 768px) {
  .toolbar { flex-direction: column; align-items: stretch; }
  .toolbar-left { width: 100%; }
  .search-input { flex: 1; max-width: none; }
  .pagination-wrap :deep(.el-pagination) { flex-wrap: wrap; }
}
</style>
