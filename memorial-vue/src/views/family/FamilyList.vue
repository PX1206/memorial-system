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
        <el-table-column label="邀请码" width="180">
          <template #default="{ row }">
            <div class="invite-cell">
              <span class="invite-code">{{ row.inviteCode || '未生成' }}</span>
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
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import QRCode from 'qrcode'
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

const inviteDialogVisible = ref(false)
const currentInvite = reactive({ family: null, qr: '' })

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
  return row.myRole === '族长' || row.myRole === '管理员'
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
