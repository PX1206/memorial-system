<template>
  <div>
    <el-card>
      <div class="toolbar">
        <span class="page-title">菜单管理</span>
        <el-button type="primary" @click="openDialog()" v-permission="'sys:menu:add'">
          <el-icon><Plus /></el-icon>新增菜单
        </el-button>
      </div>

      <el-table :data="menuTree" row-key="id" border default-expand-all v-loading="loading"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }">
        <el-table-column prop="title" label="菜单标题" min-width="180" />
        <el-table-column prop="type" label="类型" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.type === 'dir'" type="warning">目录</el-tag>
            <el-tag v-else-if="row.type === 'menu'" type="success">菜单</el-tag>
            <el-tag v-else type="info">按钮</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="icon" label="图标" width="80">
          <template #default="{ row }">
            <el-icon v-if="row.icon"><component :is="getIcon(row.icon)" /></el-icon>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路由路径" show-overflow-tooltip />
        <el-table-column prop="permission" label="权限标识" show-overflow-tooltip />
        <el-table-column prop="sort" label="排序" width="70" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="openDialog(row)" v-permission="'sys:menu:edit'">编辑</el-button>
            <el-button size="small" type="success" link @click="addChild(row)" v-permission="'sys:menu:add'">添加子级</el-button>
            <el-button size="small" type="danger" link @click="handleDelete(row)" v-permission="'sys:menu:delete'">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="580px" destroy-on-close>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="上级菜单">
          <el-tree-select
            v-model="form.pid"
            :data="menuTreeOptions"
            :props="{ value: 'id', label: 'title', children: 'children' }"
            check-strictly
            clearable
            placeholder="选择上级菜单（不选为顶级）"
            style="width: 100%"
          />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="菜单标题" prop="title">
              <el-input v-model="form.title" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="菜单类型" prop="type">
              <el-select v-model="form.type" style="width: 100%">
                <el-option label="目录" value="dir" />
                <el-option label="菜单" value="menu" />
                <el-option label="按钮" value="btn" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="图标">
              <el-input v-model="form.icon" placeholder="如: Setting, User" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序">
              <el-input-number v-model="form.sort" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="路由路径" v-if="form.type !== 'btn'">
          <el-input v-model="form.path" placeholder="/xxx/yyy" />
        </el-form-item>
        <el-form-item label="权限标识">
          <el-input v-model="form.permission" placeholder="如: sys:user:add" />
        </el-form-item>
        <el-form-item label="组件路径" v-if="form.type === 'menu'">
          <el-input v-model="form.component" placeholder="views/xxx/XxxList" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="是否隐藏">
              <el-switch v-model="form.hidden" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="缓存页面">
              <el-switch v-model="form.keepAlive" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveMenu">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { Plus, Document, Setting, User, DataAnalysis, Place, Connection, Grid, List, Monitor, Operation, Compass } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMenuTree, addMenu, updateMenu, deleteMenu } from '@/api/menu'

const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const menuTree = ref([])

const iconMap = {
  DataAnalysis, Place, Connection, User, Setting,
  Document, Grid, List, Monitor, Operation, Compass
}

function getIcon(name) {
  if (!name) return Document
  return iconMap[name] || Document
}

const menuTreeOptions = computed(() => {
  return [{ id: 0, title: '顶级菜单', children: menuTree.value }]
})

const dialogTitle = computed(() => {
  if (isEdit.value) return '编辑菜单'
  return form.pid ? '添加子级菜单' : '新增菜单'
})

const form = reactive({
  id: null, pid: 0, title: '', type: 'menu', icon: '', path: '',
  permission: '', component: '', sort: 0, hidden: false, keepAlive: false
})

const rules = {
  title: [{ required: true, message: '请输入菜单标题', trigger: 'blur' }],
  type: [{ required: true, message: '请选择菜单类型', trigger: 'change' }]
}

async function loadData() {
  loading.value = true
  try {
    const tree = await getMenuTree()
    menuTree.value = tree || []
  } catch { menuTree.value = [] }
  finally { loading.value = false }
}

onMounted(() => loadData())

function openDialog(row) {
  isEdit.value = !!row
  if (row) {
    Object.assign(form, {
      id: row.id, pid: row.pid || 0, title: row.title, type: row.type,
      icon: row.icon || '', path: row.path || '', permission: row.permission || '',
      component: row.component || '', sort: row.sort || 0,
      hidden: row.hidden || false, keepAlive: row.keepAlive || false
    })
  } else {
    Object.assign(form, {
      id: null, pid: 0, title: '', type: 'menu', icon: '', path: '',
      permission: '', component: '', sort: 0, hidden: false, keepAlive: false
    })
  }
  dialogVisible.value = true
}

function addChild(row) {
  Object.assign(form, {
    id: null, pid: row.id, title: '', type: 'menu', icon: '', path: '',
    permission: '', component: '', sort: 0, hidden: false, keepAlive: false
  })
  isEdit.value = false
  dialogVisible.value = true
}

function saveMenu() {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    saving.value = true
    try {
      if (isEdit.value) {
        await updateMenu(form)
        ElMessage.success('修改成功')
      } else {
        await addMenu(form)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      loadData()
    } catch { /* */ }
    finally { saving.value = false }
  })
}

function handleDelete(row) {
  ElMessageBox.confirm(`确定删除菜单 "${row.title}" 吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteMenu(row.id)
      ElMessage.success('删除成功')
      loadData()
    })
}
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-title { font-size: 16px; font-weight: 600; color: #303133; }
.text-muted { color: #c0c4cc; }
</style>
