<template>
  <div>
    <el-card>
      <div class="toolbar">
        <div class="toolbar-left">
          <el-input v-model="query.keyword" placeholder="搜索角色名称" clearable style="width: 220px" />
          <el-button type="primary" @click="loadData">
            <el-icon><Search /></el-icon>搜索
          </el-button>
        </div>
        <el-button type="primary" @click="openDialog()">
          <el-icon><Plus /></el-icon>新增角色
        </el-button>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="name" label="角色名称" />
        <el-table-column prop="code" label="角色标识" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="userCount" label="用户数" width="80">
          <template #default="{ row }">
            <el-tag>{{ row.userCount }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="toggleStatus(row)" />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="openDialog(row)">编辑</el-button>
            <el-button size="small" type="success" link @click="setPermissions(row)">权限</el-button>
            <el-button size="small" type="danger" link @click="deleteRole(row)" :disabled="row.code === 'admin'">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑角色' : '新增角色'" width="500px" destroy-on-close>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="角色标识" prop="code">
          <el-input v-model="form.code" :disabled="isEdit" placeholder="如: admin, user" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveRole">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="permVisible" title="权限设置" width="500px">
      <div class="perm-title">角色：{{ currentRole.name }}</div>
      <el-tree
        ref="treeRef"
        :data="menuTree"
        show-checkbox
        node-key="id"
        :default-checked-keys="currentRole.permissions || []"
        :props="{ children: 'children', label: 'label' }"
      />
      <template #footer>
        <el-button @click="permVisible = false">取消</el-button>
        <el-button type="primary" @click="savePerm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Search, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const dialogVisible = ref(false)
const permVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const treeRef = ref()

const query = reactive({ keyword: '' })
const currentRole = ref({})

const tableData = ref([
  { id: 1, name: '超级管理员', code: 'admin', description: '系统最高权限，可管理所有功能', userCount: 2, status: 1, permissions: [1, 2, 3, 4, 5, 6, 7, 8], createTime: '2026-01-01 00:00:00' },
  { id: 2, name: '普通用户', code: 'user', description: '普通注册用户，只能管理自己的数据', userCount: 354, status: 1, permissions: [1, 3, 5], createTime: '2026-01-01 00:00:00' }
])

const menuTree = [
  { id: 1, label: '首页概览' },
  {
    id: 2, label: '墓碑管理', children: [
      { id: 3, label: '墓碑列表' },
      { id: 4, label: '留言管理' },
      { id: 5, label: '打卡记录' }
    ]
  },
  {
    id: 6, label: '家族管理', children: [
      { id: 7, label: '家族列表' },
      { id: 8, label: '成员管理' }
    ]
  },
  { id: 9, label: '用户管理' },
  {
    id: 10, label: '系统管理', children: [
      { id: 11, label: '角色管理' },
      { id: 12, label: '操作日志' }
    ]
  }
]

const form = reactive({ id: null, name: '', code: '', description: '' })

const rules = {
  name: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入角色标识', trigger: 'blur' }]
}

function loadData() {
  loading.value = true
  setTimeout(() => { loading.value = false }, 300)
}

onMounted(() => loadData())

function openDialog(row) {
  isEdit.value = !!row
  if (row) {
    Object.assign(form, row)
  } else {
    Object.assign(form, { id: null, name: '', code: '', description: '' })
  }
  dialogVisible.value = true
}

function saveRole() {
  formRef.value.validate((valid) => {
    if (!valid) return
    if (isEdit.value) {
      const idx = tableData.value.findIndex(r => r.id === form.id)
      if (idx !== -1) tableData.value[idx] = { ...tableData.value[idx], ...form }
      ElMessage.success('修改成功')
    } else {
      tableData.value.push({
        ...form,
        id: Date.now(),
        userCount: 0,
        status: 1,
        permissions: [],
        createTime: new Date().toLocaleString()
      })
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
  })
}

function toggleStatus(row) {
  ElMessage.success(`角色 ${row.name} 已${row.status === 1 ? '启用' : '禁用'}`)
}

function setPermissions(row) {
  currentRole.value = { ...row }
  permVisible.value = true
}

function savePerm() {
  const checked = treeRef.value.getCheckedKeys()
  const halfChecked = treeRef.value.getHalfCheckedKeys()
  const allKeys = [...checked, ...halfChecked]
  const idx = tableData.value.findIndex(r => r.id === currentRole.value.id)
  if (idx !== -1) tableData.value[idx].permissions = allKeys
  ElMessage.success('权限保存成功')
  permVisible.value = false
}

function deleteRole(row) {
  ElMessageBox.confirm(`确定删除角色 "${row.name}" 吗？`, '提示', { type: 'warning' })
    .then(() => {
      tableData.value = tableData.value.filter(r => r.id !== row.id)
      ElMessage.success('删除成功')
    })
}
</script>

<style scoped>
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.toolbar-left {
  display: flex;
  gap: 10px;
}

.perm-title {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 12px;
  color: #303133;
}
</style>
