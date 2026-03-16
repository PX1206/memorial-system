<template>
  <div>
    <el-card>
      <div class="toolbar">
        <div class="toolbar-left">
          <el-input v-model="query.keyword" placeholder="搜索家族名称" clearable style="width: 220px" @clear="loadData" />
          <el-button type="primary" @click="loadData"><el-icon><Search /></el-icon>搜索</el-button>
        </div>
        <el-button type="primary" @click="openDialog()"><el-icon><Plus /></el-icon>新增家族</el-button>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="name" label="家族名称" />
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
        <el-table-column prop="description" label="家族简介" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="openDialog(row)">编辑</el-button>
            <el-button size="small" type="success" link @click="viewMembers(row)">成员</el-button>
            <el-button size="small" type="danger" link @click="handleDelete(row)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑家族' : '新增家族'" width="500px" destroy-on-close>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item label="上级家族">
          <el-tree-select
            v-model="form.pid"
            :data="familyTreeOptions"
            :props="{ value: 'id', label: 'name', children: 'children' }"
            check-strictly
            placeholder="选择上级家族（不选为顶级）"
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getFamilyPageList, addFamily, updateFamily, deleteFamily, getFamilyTree } from '@/api/family'

const router = useRouter()
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const total = ref(0)
const familyTreeOptions = ref([])

const query = reactive({ keyword: '', pageIndex: 1, pageSize: 10 })
const tableData = ref([])

const form = reactive({ id: null, pid: 0, name: '', description: '', phone: '', address: '' })
const rules = { name: [{ required: true, message: '请输入家族名称', trigger: 'blur' }] }

async function loadData() {
  loading.value = true
  try {
    const res = await getFamilyPageList(query)
    if (res) {
      tableData.value = res.records || []
      total.value = res.total || 0
    }
  } catch { /* 静默 */ }
  finally { loading.value = false }
}

async function loadFamilyTree() {
  try {
    const tree = await getFamilyTree()
    familyTreeOptions.value = [{ id: 0, name: '顶级（无上级）', children: tree || [] }]
  } catch { familyTreeOptions.value = [] }
}

onMounted(() => { loadData(); loadFamilyTree() })

function openDialog(row) {
  isEdit.value = !!row
  if (row) Object.assign(form, { id: row.id, pid: row.pid || 0, name: row.name, description: row.description, phone: row.phone, address: row.address })
  else Object.assign(form, { id: null, pid: 0, name: '', description: '', phone: '', address: '' })
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
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.toolbar-left { display: flex; gap: 10px; }
.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 16px; }
.text-muted { color: #c0c4cc; font-size: 13px; }
</style>
