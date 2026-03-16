<template>
  <div>
    <el-card>
      <div class="toolbar">
        <div class="toolbar-left">
          <el-input v-model="query.keyword" placeholder="搜索逝者姓名" clearable style="width: 220px" @clear="loadData" />
          <el-button type="primary" @click="loadData"><el-icon><Search /></el-icon>搜索</el-button>
        </div>
        <el-button type="primary" @click="openDialog()"><el-icon><Plus /></el-icon>新增墓碑</el-button>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column label="照片" width="80">
          <template #default="{ row }">
            <el-avatar :size="48" :src="row.photo" shape="square" v-if="row.photo" />
            <el-avatar :size="48" shape="square" v-else><el-icon><Picture /></el-icon></el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="逝者姓名" />
        <el-table-column prop="birthday" label="出生日期" width="120" />
        <el-table-column prop="deathday" label="逝世日期" width="120" />
        <el-table-column prop="familyName" label="所属家族" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.familyName" type="success">{{ row.familyName }}</el-tag>
            <span v-else class="text-muted">未关联</span>
          </template>
        </el-table-column>
        <el-table-column label="二维码" width="100" align="center">
          <template #default="{ row }">
            <el-popover placement="left" :width="200" trigger="hover" v-if="row.qrCode">
              <template #reference>
                <img :src="row.qrCode" width="40" style="cursor: pointer" />
              </template>
              <div style="text-align: center">
                <img :src="row.qrCode" width="160" />
                <div style="margin-top: 8px">
                  <el-button size="small" type="primary" link @click="downloadQR(row)">下载二维码</el-button>
                </div>
              </div>
            </el-popover>
            <el-button v-else size="small" type="warning" link @click="generateQR(row)">生成</el-button>
          </template>
        </el-table-column>
        <el-table-column prop="visitCount" label="访问量" width="90" />
        <el-table-column prop="messageCount" label="留言数" width="90" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="openDialog(row)">编辑</el-button>
            <el-button size="small" type="success" link @click="viewDetail(row)">详情</el-button>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑墓碑' : '新增墓碑'" width="600px" destroy-on-close>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="逝者姓名" prop="name">
              <el-input v-model="form.name" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属家族">
              <el-tree-select
                v-model="form.familyId"
                :data="familyTreeOptions"
                :props="{ value: 'id', label: 'name', children: 'children' }"
                check-strictly
                clearable
                placeholder="选择所属家族"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="出生日期" prop="birthday">
              <el-date-picker v-model="form.birthday" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="逝世日期" prop="deathday">
              <el-date-picker v-model="form.deathday" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="个人简介">
          <el-input v-model="form.biography" type="textarea" :rows="3" placeholder="墓主人个人简介" />
        </el-form-item>
        <el-form-item label="生平事迹">
          <el-input v-model="form.story" type="textarea" :rows="4" placeholder="墓主人生平事迹" />
        </el-form-item>
        <el-form-item label="照片">
          <div class="photo-upload">
            <div v-if="form.photo" class="photo-preview">
              <el-image :src="form.photo" fit="cover" class="photo-img" />
              <div class="photo-actions">
                <el-icon @click="form.photo = ''"><Close /></el-icon>
              </div>
            </div>
            <div v-else class="photo-placeholder" @click="triggerUpload">
              <el-icon :size="28" color="#c0c4cc"><Plus /></el-icon>
              <span>上传照片</span>
            </div>
            <input ref="fileInput" type="file" accept="image/*" style="display:none" @change="handleUpload" />
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveTomb">保存</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="detailVisible" :title="detailData.name + ' - 详细信息'" size="45%">
      <div class="detail-content">
        <div class="detail-photo" v-if="detailData.photo">
          <el-image :src="detailData.photo" fit="cover" style="width: 120px; height: 120px; border-radius: 8px" />
        </div>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="逝者姓名">{{ detailData.name }}</el-descriptions-item>
          <el-descriptions-item label="所属家族">{{ detailData.familyName || '未关联' }}</el-descriptions-item>
          <el-descriptions-item label="出生日期">{{ detailData.birthday }}</el-descriptions-item>
          <el-descriptions-item label="逝世日期">{{ detailData.deathday }}</el-descriptions-item>
          <el-descriptions-item label="访问量">{{ detailData.visitCount }}</el-descriptions-item>
          <el-descriptions-item label="留言数">{{ detailData.messageCount }}</el-descriptions-item>
          <el-descriptions-item label="个人简介" :span="2">{{ detailData.biography || '暂无' }}</el-descriptions-item>
          <el-descriptions-item label="生平事迹" :span="2">{{ detailData.story || '暂无' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Search, Plus, Picture, Close } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import QRCode from 'qrcode'
import { getTombPageList, addTomb, updateTomb, deleteTomb } from '@/api/tomb'
import { getFamilyTree } from '@/api/family'
import { uploadFile } from '@/api/user'

const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const detailVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const fileInput = ref()
const total = ref(0)
const detailData = ref({})
const familyTreeOptions = ref([])

const query = reactive({ keyword: '', pageIndex: 1, pageSize: 10 })
const tableData = ref([])

const form = reactive({
  id: null, name: '', birthday: '', deathday: '',
  familyId: null, photo: '', biography: '', story: ''
})

const rules = {
  name: [{ required: true, message: '请输入逝者姓名', trigger: 'blur' }],
  birthday: [{ required: true, message: '请选择出生日期', trigger: 'change' }],
  deathday: [{ required: true, message: '请选择逝世日期', trigger: 'change' }]
}

async function loadData() {
  loading.value = true
  try {
    const res = await getTombPageList(query)
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
    familyTreeOptions.value = tree || []
  } catch { familyTreeOptions.value = [] }
}

onMounted(() => { loadData(); loadFamilyTree() })

function triggerUpload() { fileInput.value?.click() }

async function handleUpload(e) {
  const file = e.target.files[0]
  if (!file) return
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.warning('图片不能超过5MB')
    return
  }
  const validTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp']
  if (!validTypes.includes(file.type)) {
    ElMessage.warning('仅支持 JPG/PNG/GIF/WebP 格式')
    return
  }
  try {
    const res = await uploadFile(file)
    if (res && res.url) {
      form.photo = res.url
      ElMessage.success('上传成功')
    }
  } catch { ElMessage.error('上传失败') }
  finally { e.target.value = '' }
}

function openDialog(row) {
  isEdit.value = !!row
  if (row) {
    Object.assign(form, { ...row })
  } else {
    Object.assign(form, { id: null, name: '', birthday: '', deathday: '', familyId: null, photo: '', biography: '', story: '' })
  }
  loadFamilyTree()
  dialogVisible.value = true
}

async function saveTomb() {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    saving.value = true
    try {
      if (isEdit.value) {
        await updateTomb(form)
        ElMessage.success('修改成功')
      } else {
        await addTomb(form)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      loadData()
    } catch { /* 错误由拦截器处理 */ }
    finally { saving.value = false }
  })
}

function viewDetail(row) {
  detailData.value = { ...row }
  detailVisible.value = true
}

async function generateQR(row) {
  const url = `${window.location.origin}/memorial/${row.id}`
  row.qrCode = await QRCode.toDataURL(url, { width: 300, margin: 2 })
  ElMessage.success('二维码已生成')
}

function downloadQR(row) {
  const link = document.createElement('a')
  link.download = `墓碑二维码_${row.name}.png`
  link.href = row.qrCode
  link.click()
}

function handleDelete(row) {
  ElMessageBox.confirm(`确定删除墓碑 "${row.name}" 吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteTomb(row.id)
      ElMessage.success('删除成功')
      loadData()
    })
}
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.toolbar-left { display: flex; gap: 10px; }
.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 16px; }
.text-muted { color: #c0c4cc; font-size: 13px; }
.detail-content { padding: 0 10px; }
.detail-photo { text-align: center; margin-bottom: 20px; }
.photo-upload { display: flex; align-items: flex-start; }
.photo-preview {
  position: relative;
  width: 120px;
  height: 120px;
  border-radius: 6px;
  overflow: hidden;
  border: 1px solid #e4e7ed;
}
.photo-img { width: 100%; height: 100%; }
.photo-actions {
  position: absolute;
  top: 4px;
  right: 4px;
  cursor: pointer;
  background: rgba(0,0,0,0.5);
  border-radius: 50%;
  width: 22px;
  height: 22px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}
.photo-placeholder {
  width: 120px;
  height: 120px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  gap: 6px;
  color: #c0c4cc;
  transition: border-color 0.3s;
}
.photo-placeholder:hover { border-color: #409eff; color: #409eff; }
</style>
