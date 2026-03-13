<template>
  <div>
    <el-card>
      <div class="toolbar">
        <div class="toolbar-left">
          <el-button @click="goBack"><el-icon><ArrowLeft /></el-icon>返回</el-button>
          <span class="family-title" v-if="familyName">{{ familyName }} - 成员管理</span>
        </div>
        <el-button type="primary" @click="openDialog()"><el-icon><Plus /></el-icon>添加成员</el-button>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="name" label="姓名" />
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
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="openDialog(row)">编辑</el-button>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑成员' : '添加成员'" width="500px" destroy-on-close>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item label="姓名" prop="name"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="关系" prop="relation">
          <el-select v-model="form.relation" style="width: 100%">
            <el-option v-for="r in relations" :key="r" :label="r" :value="r" />
          </el-select>
        </el-form-item>
        <el-form-item label="联系电话"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="家族角色">
          <el-select v-model="form.role" style="width: 100%">
            <el-option label="族长" value="族长" />
            <el-option label="管理员" value="管理员" />
            <el-option label="成员" value="成员" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveMember">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Plus, ArrowLeft } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMemberPageList, addFamilyMember, updateFamilyMember, removeFamilyMember } from '@/api/family'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const total = ref(0)

const familyId = ref(Number(route.query.familyId) || null)
const familyName = ref(route.query.familyName || '')
const relations = ['父亲', '母亲', '儿子', '女儿', '兄弟', '姐妹', '爷爷', '奶奶', '外公', '外婆', '叔伯', '姑姨', '其他']

const query = reactive({ familyId: familyId.value, keyword: '', pageIndex: 1, pageSize: 10 })
const tableData = ref([])

const form = reactive({ id: null, familyId: familyId.value, name: '', relation: '', phone: '', role: '成员' })
const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  relation: [{ required: true, message: '请选择关系', trigger: 'change' }]
}

async function loadData() {
  if (!familyId.value) return
  loading.value = true
  try {
    const res = await getMemberPageList(query)
    if (res) {
      tableData.value = res.records || []
      total.value = res.total || 0
    }
  } catch { /* 静默 */ }
  finally { loading.value = false }
}

onMounted(() => loadData())

function goBack() { router.push('/family') }

function openDialog(row) {
  isEdit.value = !!row
  if (row) Object.assign(form, row)
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
      } else {
        await addFamilyMember(form)
        ElMessage.success('添加成功')
      }
      dialogVisible.value = false
      loadData()
    } catch { /* */ }
    finally { saving.value = false }
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
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.toolbar-left { display: flex; align-items: center; gap: 12px; }
.family-title { font-size: 16px; font-weight: 600; color: #303133; }
.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>
