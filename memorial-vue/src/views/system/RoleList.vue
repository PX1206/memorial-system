<template>
  <div>
    <el-card>
      <div class="toolbar">
        <div class="toolbar-left">
          <el-input v-model="query.keyword" placeholder="搜索角色名称" clearable style="width: 220px" @keyup.enter="loadData" />
          <el-button type="primary" @click="loadData">
            <el-icon><Search /></el-icon>搜索
          </el-button>
        </div>
        <el-button type="primary" @click="openDialog()" v-permission="'sys:role:add'">
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
            <el-tag>{{ row.userCount || 0 }}</el-tag>
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
            <el-button size="small" type="primary" link @click="openDialog(row)" v-permission="'sys:role:edit'">编辑</el-button>
            <el-button size="small" type="success" link @click="setPermissions(row)" v-permission="'sys:role:perm'">权限</el-button>
            <el-button size="small" type="danger" link @click="handleDelete(row)" :disabled="row.code === 'admin'" v-permission="'sys:role:delete'">删除</el-button>
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
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑角色' : '新增角色'" width="500px" destroy-on-close>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="角色标识" prop="code">
          <el-input v-model="form.code" :disabled="isEdit" placeholder="如: admin, editor" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveRole" :loading="saveLoading">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="permVisible" title="权限设置" width="500px">
      <div class="perm-title">角色：{{ currentRole.name }}</div>
      <el-tree
        ref="treeRef"
        :data="menuTreeData"
        show-checkbox
        node-key="id"
        :default-checked-keys="checkedMenuIds"
        :props="{ children: 'children', label: 'title' }"
        default-expand-all
      />
      <template #footer>
        <el-button @click="permVisible = false">取消</el-button>
        <el-button type="primary" @click="savePerm" :loading="permLoading">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Search, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRolePageList, addRole, updateRole, deleteRole, toggleRoleStatus, saveRoleMenus, getRoleMenuIds } from '@/api/role'
import { getMenuTree } from '@/api/menu'

const loading = ref(false)
const saveLoading = ref(false)
const permLoading = ref(false)
const dialogVisible = ref(false)
const permVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const treeRef = ref()
const total = ref(0)

const query = reactive({ keyword: '', pageIndex: 1, pageSize: 10 })
const currentRole = ref({})
const checkedMenuIds = ref([])
const menuTreeData = ref([])

const tableData = ref([])

const form = reactive({ id: null, name: '', code: '', description: '' })

const rules = {
  name: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入角色标识', trigger: 'blur' }]
}

async function loadData() {
  loading.value = true
  try {
    const res = await getRolePageList(query)
    tableData.value = res.records || []
    total.value = res.total || 0
  } catch { /* handled by interceptor */ } finally {
    loading.value = false
  }
}

onMounted(() => loadData())

function openDialog(row) {
  isEdit.value = !!row
  if (row) {
    Object.assign(form, { id: row.id, name: row.name, code: row.code, description: row.description })
  } else {
    Object.assign(form, { id: null, name: '', code: '', description: '' })
  }
  dialogVisible.value = true
}

async function saveRole() {
  await formRef.value.validate()
  saveLoading.value = true
  try {
    if (isEdit.value) {
      await updateRole(form)
      ElMessage.success('修改成功')
    } else {
      await addRole(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch { /* handled by interceptor */ } finally {
    saveLoading.value = false
  }
}

async function toggleStatus(row) {
  try {
    await toggleRoleStatus(row.id)
    ElMessage.success(`角色 ${row.name} 已${row.status === 1 ? '启用' : '禁用'}`)
  } catch {
    row.status = row.status === 1 ? 0 : 1
  }
}

async function setPermissions(row) {
  currentRole.value = { ...row }
  try {
    const [tree, ids] = await Promise.all([
      getMenuTree(),
      getRoleMenuIds(row.id)
    ])
    menuTreeData.value = tree || []
    checkedMenuIds.value = filterLeafIds(tree || [], ids || [])
  } catch { /* handled by interceptor */ }
  permVisible.value = true
}

function filterLeafIds(tree, ids) {
  const leafIds = []
  function walk(nodes) {
    for (const node of nodes) {
      if (node.children && node.children.length > 0) {
        walk(node.children)
      } else {
        if (ids.includes(node.id)) leafIds.push(node.id)
      }
    }
  }
  walk(tree)
  return leafIds
}

async function savePerm() {
  const checked = treeRef.value.getCheckedKeys()
  const halfChecked = treeRef.value.getHalfCheckedKeys()
  const allKeys = [...checked, ...halfChecked]
  permLoading.value = true
  try {
    await saveRoleMenus({ roleId: currentRole.value.id, menuIds: allKeys })
    ElMessage.success('权限保存成功')
    permVisible.value = false
  } catch { /* handled by interceptor */ } finally {
    permLoading.value = false
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除角色 "${row.name}" 吗？`, '提示', { type: 'warning' })
  try {
    await deleteRole(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch { /* handled by interceptor */ }
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

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
