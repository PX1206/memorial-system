<template>
  <div>
    <div class="page-wrap">
      <el-card class="left-card">
        <div class="left-toolbar">
          <span class="left-title">家族树</span>
        </div>
        <el-tree
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
            <el-input v-model="query.keyword" placeholder="搜索逝者姓名" clearable style="width: 220px" @clear="loadData" />
            <el-button type="primary" @click="loadData"><el-icon><Search /></el-icon>搜索</el-button>
          </div>
          <el-button v-if="canAddTomb" type="primary" @click="openDialog()"><el-icon><Plus /></el-icon>新增墓碑</el-button>
        </div>

        <div class="current-family">{{ currentFamilyTitle }}</div>

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
          <el-table-column prop="address" label="所处位置" width="140" show-overflow-tooltip />
        <el-table-column label="二维码" width="100" align="center">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="openQrDialog(row)">查看</el-button>
          </template>
        </el-table-column>
          <el-table-column prop="visitCount" label="访问量" width="90" />
          <el-table-column prop="messageCount" label="留言数" width="90" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button v-if="canOperate(row)" size="small" type="primary" link @click="openDialog(row)">编辑</el-button>
              <el-button v-if="canOperate(row)" size="small" type="warning" link @click="openStoryDrawer(row)">事迹</el-button>
              <el-button size="small" type="success" link @click="viewDetail(row)">详情</el-button>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑墓碑' : '新增墓碑'" width="600px" destroy-on-close>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="逝者姓名" prop="name">
              <el-input v-model="form.name" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属家庭">
              <el-tree-select
                v-model="form.familyId"
                :data="tombFamilyOptions"
                :props="{ value: 'id', label: 'name', children: 'children' }"
                check-strictly
                clearable
                :placeholder="isSuperAdmin ? '选择所属家族/家庭' : '只能选择家庭'"
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
        <el-form-item label="墓志铭">
          <el-input v-model="form.epitaph" maxlength="255" show-word-limit placeholder="如：音容宛在，永垂不朽" />
        </el-form-item>
        <el-form-item label="所处位置">
          <el-input v-model="form.address" placeholder="记录墓地所处位置（可选）" />
        </el-form-item>
        <el-form-item label="开放互动">
          <el-switch
            v-model="form.visitorActionOpen"
            inline-prompt
            active-text="开放"
            inactive-text="仅家族"
          />
          <span class="story-tip" style="margin-left: 10px">
            关闭后：留言/献花等仅家族成员可操作（仍需登录）；开启后：所有人登录后可操作（支持匿名姓名）。
          </span>
        </el-form-item>
        <el-form-item label="生平事迹">
          <div class="story-tip">
            生平事迹已支持“多条事迹 + 富文本编辑”。请先保存墓碑，然后在列表中点击“事迹”进行维护。
          </div>
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

    <!-- 事迹管理抽屉 -->
    <el-drawer v-model="storyVisible" :title="storyTomb.name + ' - 生平事迹'" size="55%">
      <div class="story-toolbar">
        <el-button type="primary" @click="openStoryEditor()"><el-icon><Plus /></el-icon>新增事迹</el-button>
      </div>

      <el-table :data="storyList" border stripe v-loading="storyLoading">
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="openStoryEditor(row)">编辑</el-button>
            <el-button size="small" type="danger" link @click="handleDeleteStory(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-dialog v-model="storyEditorVisible" :title="storyIsEdit ? '编辑事迹' : '新增事迹'" width="800px" destroy-on-close>
        <el-form :model="storyForm" ref="storyFormRef" label-width="80px">
          <el-form-item label="标题" prop="title" :rules="[{ required: true, message: '请输入标题', trigger: 'blur' }]">
            <el-input v-model="storyForm.title" maxlength="100" show-word-limit />
          </el-form-item>
          <el-form-item label="排序">
            <el-input-number v-model="storyForm.sort" :min="0" :max="9999" />
          </el-form-item>
          <el-form-item label="内容" prop="content" :rules="[{ required: true, message: '请输入内容', trigger: 'blur' }]">
            <div class="quill-wrap">
              <QuillEditor v-model:content="storyForm.content" contentType="html" theme="snow" />
            </div>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="storyEditorVisible = false">取消</el-button>
          <el-button type="primary" :loading="storySaving" @click="saveStory">保存</el-button>
        </template>
      </el-dialog>
    </el-drawer>

    <el-dialog v-model="qrDialogVisible" title="墓碑二维码" width="360px" destroy-on-close>
      <div class="invite-dialog-body" v-if="currentQr.tomb">
        <div class="invite-info">
          <div class="invite-title">{{ currentQr.tomb.name }}</div>
          <div class="invite-tip">扫码访问纪念页</div>
        </div>
        <div class="invite-qr-wrap">
          <img v-if="currentQr.qr" :src="currentQr.qr" class="invite-qr-img" />
          <span v-else class="text-muted">二维码生成中...</span>
        </div>
      </div>
      <template #footer>
        <el-button @click="qrDialogVisible = false">关闭</el-button>
        <el-button type="primary" :disabled="!currentQr.qr" @click="downloadCurrentQr">下载二维码</el-button>
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
          <el-descriptions-item label="所处位置" :span="2">{{ detailData.address || '暂无' }}</el-descriptions-item>
          <el-descriptions-item label="出生日期">{{ detailData.birthday }}</el-descriptions-item>
          <el-descriptions-item label="逝世日期">{{ detailData.deathday }}</el-descriptions-item>
          <el-descriptions-item label="访问量">{{ detailData.visitCount }}</el-descriptions-item>
          <el-descriptions-item label="留言数">{{ detailData.messageCount }}</el-descriptions-item>
          <el-descriptions-item label="个人简介" :span="2">{{ detailData.biography || '暂无' }}</el-descriptions-item>
          <el-descriptions-item label="墓志铭" :span="2">{{ detailData.epitaph || '暂无' }}</el-descriptions-item>
        </el-descriptions>

        <div class="detail-stories" v-if="detailData.stories && detailData.stories.length">
          <h3 class="detail-stories-title">生平事迹</h3>
          <div class="detail-story-item" v-for="s in detailData.stories" :key="s.id">
            <div class="detail-story-title">{{ s.title }}</div>
            <div class="detail-story-content" v-html="s.content"></div>
          </div>
        </div>

        <el-divider />

        <el-tabs v-model="detailTab" class="detail-tabs">
          <el-tab-pane label="留言记录" name="messages">
            <el-table :data="messageTable" border stripe v-loading="messageLoading">
              <el-table-column prop="visitorName" label="访客" width="120" />
              <el-table-column label="内容" min-width="240" show-overflow-tooltip>
                <template #default="{ row }">
                  <span class="msg-content">{{ row.content }}</span>
                  <el-button size="small" type="primary" link @click="openMessageDialog(row)">查看</el-button>
                </template>
              </el-table-column>
              <el-table-column label="状态" width="120">
                <template #default="{ row }">
                  <el-tag
                    :type="row.status === '通过' ? 'success' : row.status === '拒绝' ? 'danger' : 'warning'"
                  >
                    {{ row.status || '待审核' }}
                  </el-tag>
                  <el-tooltip v-if="row.status === '拒绝' && row.rejectReason" :content="row.rejectReason" placement="top">
                    <el-button size="small" type="danger" link>原因</el-button>
                  </el-tooltip>
                </template>
              </el-table-column>
              <el-table-column prop="createTime" label="时间" width="170" />
            </el-table>
            <div class="pagination-wrap">
              <el-pagination
                v-model:current-page="messageQuery.pageIndex"
                v-model:page-size="messageQuery.pageSize"
                :total="messageTotal"
                :page-sizes="[10, 20, 50]"
                layout="total, sizes, prev, pager, next, jumper"
                @current-change="loadMessages"
                @size-change="loadMessages"
              />
            </div>
          </el-tab-pane>

          <el-tab-pane label="献花/打卡记录" name="checkins">
            <el-table :data="checkinTable" border stripe v-loading="checkinLoading">
              <el-table-column prop="visitorName" label="访客" width="120" />
              <el-table-column prop="type" label="类型" width="120" />
              <el-table-column prop="ip" label="IP" width="120" />
              <el-table-column prop="location" label="位置" />
              <el-table-column prop="createTime" label="时间" width="170" />
            </el-table>
            <div class="pagination-wrap">
              <el-pagination
                v-model:current-page="checkinQuery.pageIndex"
                v-model:page-size="checkinQuery.pageSize"
                :total="checkinTotal"
                :page-sizes="[10, 20, 50]"
                layout="total, sizes, prev, pager, next, jumper"
                @current-change="loadCheckins"
                @size-change="loadCheckins"
              />
            </div>
          </el-tab-pane>
        </el-tabs>

        <el-dialog v-model="messageDialogVisible" title="留言详情" width="560px" destroy-on-close>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="访客">{{ currentMessage.visitorName || '匿名' }}</el-descriptions-item>
            <el-descriptions-item label="时间">{{ currentMessage.createTime }}</el-descriptions-item>
            <el-descriptions-item label="状态" :span="2">
              <el-tag :type="currentMessage.status === '通过' ? 'success' : currentMessage.status === '拒绝' ? 'danger' : 'warning'">
                {{ currentMessage.status || '待审核' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item v-if="currentMessage.status === '拒绝' && currentMessage.rejectReason" label="拒绝原因" :span="2">
              {{ currentMessage.rejectReason }}
            </el-descriptions-item>
            <el-descriptions-item label="内容" :span="2">
              <div class="msg-detail">{{ currentMessage.content }}</div>
            </el-descriptions-item>
          </el-descriptions>
          <template #footer>
            <el-button type="primary" @click="messageDialogVisible = false">关闭</el-button>
          </template>
        </el-dialog>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { Search, Plus, Picture, Close } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import QRCode from 'qrcode'
import { QuillEditor } from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import { getTombPageList, getTombDetail, addTomb, updateTomb, deleteTomb, getTombStoryList, addTombStory, updateTombStory, deleteTombStory, getMessagePageList, getCheckinPageList } from '@/api/tomb'
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
const treeLoading = ref(false)
const isSuperAdmin = ref(false)
const familyTreeData = ref([])
const selectedFamilyId = ref(null)
const selectedFamilyName = ref('')

const query = reactive({ keyword: '', familyId: null, pageIndex: 1, pageSize: 10 })
const tableData = ref([])

const form = reactive({
  id: null, name: '', birthday: '', deathday: '',
  familyId: null, photo: '', biography: '', story: '', epitaph: '', address: '', visitorActionOpen: true
})

const rules = {
  name: [{ required: true, message: '请输入逝者姓名', trigger: 'blur' }],
  birthday: [{ required: true, message: '请选择出生日期', trigger: 'change' }],
  deathday: [{ required: true, message: '请选择逝世日期', trigger: 'change' }]
}

async function loadData() {
  loading.value = true
  try {
    query.familyId = selectedFamilyId.value
    const res = await getTombPageList(query)
    if (res) {
      tableData.value = res.records || []
      total.value = res.total || 0
    }
  } catch { /* 静默 */ }
  finally { loading.value = false }
}

function collectFamilies(nodes, acc = []) {
  if (!nodes || !nodes.length) return acc
  for (const n of nodes) {
    if (n.type === '家庭') acc.push({ ...n, children: undefined })
    collectFamilies(n.children, acc)
  }
  return acc
}

const tombFamilyOptions = computed(() => {
  const raw = familyTreeOptions.value || []
  if (isSuperAdmin.value) return raw
  return collectFamilies(raw)
})

const canAddTomb = computed(() => isSuperAdmin.value || (tombFamilyOptions.value?.length || 0) > 0)

async function loadFamilyTree() {
  const u = JSON.parse(localStorage.getItem('currentUser') || '{}')
  isSuperAdmin.value = u.role === 'admin'
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

onMounted(() => { loadData(); loadFamilyTree() })

const currentFamilyTitle = computed(() => {
  if (selectedFamilyId.value === null) return '当前筛选：全部家族'
  return `当前筛选：${selectedFamilyName.value || '家族'}（含子家族）`
})

function handleTreeNodeClick(node) {
  if (Number(node.id) === -1) {
    selectedFamilyId.value = null
    selectedFamilyName.value = ''
  } else {
    selectedFamilyId.value = Number(node.id)
    selectedFamilyName.value = node.name || ''
  }
  query.pageIndex = 1
  loadData()
}

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
    const url = await uploadFile(file)
    if (url) {
      form.photo = url
      ElMessage.success('上传成功')
    }
  } catch { ElMessage.error('上传失败') }
  finally { e.target.value = '' }
}

function openDialog(row) {
  isEdit.value = !!row
  if (row) {
    Object.assign(form, { visitorActionOpen: true, ...row })
    if (form.visitorActionOpen === null || form.visitorActionOpen === undefined) form.visitorActionOpen = true
  } else {
    Object.assign(form, { id: null, name: '', birthday: '', deathday: '', familyId: null, photo: '', biography: '', story: '', epitaph: '', address: '', visitorActionOpen: true })
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
  detailVisible.value = true
  detailData.value = { ...row }
  getTombDetail(row.id)
    .then((res) => {
      if (res) detailData.value = res
      // 打开详情默认加载留言/打卡
      detailTab.value = 'messages'
      resetDetailLists(row.id)
    })
    .catch(() => {})
}

function resetDetailLists(tombId) {
  messageQuery.tombId = tombId
  messageQuery.pageIndex = 1
  checkinQuery.tombId = tombId
  checkinQuery.pageIndex = 1
  loadMessages()
  loadCheckins()
}

// =========================
// 详情：留言/打卡记录
// =========================

const detailTab = ref('messages')

const messageLoading = ref(false)
const messageTotal = ref(0)
const messageTable = ref([])
const messageQuery = reactive({ tombId: null, keyword: '', status: '', pageIndex: 1, pageSize: 10 })

const messageDialogVisible = ref(false)
const currentMessage = reactive({ id: null, visitorName: '', content: '', status: '', rejectReason: '', createTime: '' })

function openMessageDialog(row) {
  Object.assign(currentMessage, {
    id: row.id,
    visitorName: row.visitorName,
    content: row.content,
    status: row.status,
    rejectReason: row.rejectReason,
    createTime: row.createTime
  })
  messageDialogVisible.value = true
}

async function loadMessages() {
  if (!messageQuery.tombId) return
  messageLoading.value = true
  try {
    const res = await getMessagePageList(messageQuery)
    if (res) {
      messageTable.value = res.records || []
      messageTotal.value = res.total || 0
    }
  } catch { /* */ }
  finally { messageLoading.value = false }
}

const checkinLoading = ref(false)
const checkinTotal = ref(0)
const checkinTable = ref([])
const checkinQuery = reactive({ tombId: null, keyword: '', type: '', pageIndex: 1, pageSize: 10 })

async function loadCheckins() {
  if (!checkinQuery.tombId) return
  checkinLoading.value = true
  try {
    const res = await getCheckinPageList(checkinQuery)
    if (res) {
      checkinTable.value = res.records || []
      checkinTotal.value = res.total || 0
    }
  } catch { /* */ }
  finally { checkinLoading.value = false }
}

// =========================
// 列表：二维码弹窗（同家族邀请码风格）
// =========================

const qrDialogVisible = ref(false)
const currentQr = reactive({ tomb: null, qr: '' })

async function openQrDialog(row) {
  currentQr.tomb = row
  currentQr.qr = ''
  qrDialogVisible.value = true
  try {
    const code = row.qrCodeKey || row.id
    const url = `${window.location.origin}/memorial/${code}`
    currentQr.qr = await QRCode.toDataURL(url, { width: 320, margin: 1 })
  } catch {
    ElMessage.error('生成二维码失败')
  }
}

function downloadCurrentQr() {
  if (!currentQr.qr || !currentQr.tomb) return
  const link = document.createElement('a')
  link.download = `墓碑二维码_${currentQr.tomb.name}.png`
  link.href = currentQr.qr
  link.click()
}

function canOperate(row) {
  return row && ['admin', 'chief', 'member'].includes(row.myRole)
}

function handleDelete(row) {
  ElMessageBox.confirm(`确定删除墓碑 "${row.name}" 吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteTomb(row.id)
      ElMessage.success('删除成功')
      loadData()
    })
}

// =========================
// 事迹管理
// =========================

const storyVisible = ref(false)
const storyLoading = ref(false)
const storySaving = ref(false)
const storyList = ref([])
const storyTomb = reactive({ id: null, name: '' })
const storyEditorVisible = ref(false)
const storyIsEdit = ref(false)
const storyFormRef = ref()
const storyForm = reactive({ id: null, tombId: null, title: '', content: '', sort: 0 })

async function openStoryDrawer(row) {
  storyTomb.id = row.id
  storyTomb.name = row.name
  storyVisible.value = true
  await loadStoryList()
}

async function loadStoryList() {
  if (!storyTomb.id) return
  storyLoading.value = true
  try {
    const res = await getTombStoryList(storyTomb.id)
    storyList.value = res || []
  } catch { /* */ }
  finally { storyLoading.value = false }
}

function openStoryEditor(row) {
  storyIsEdit.value = !!row
  if (row) Object.assign(storyForm, { id: row.id, tombId: row.tombId, title: row.title, content: row.content, sort: row.sort ?? 0 })
  else Object.assign(storyForm, { id: null, tombId: storyTomb.id, title: '', content: '', sort: 0 })
  storyEditorVisible.value = true
}

function saveStory() {
  storyFormRef.value.validate(async (valid) => {
    if (!valid) return
    storySaving.value = true
    try {
      if (storyIsEdit.value) await updateTombStory(storyForm)
      else await addTombStory(storyForm)
      ElMessage.success('保存成功')
      storyEditorVisible.value = false
      loadStoryList()
    } catch { /* */ }
    finally { storySaving.value = false }
  })
}

function handleDeleteStory(row) {
  ElMessageBox.confirm(`确定删除事迹 "${row.title}" 吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteTombStory(row.id)
      ElMessage.success('删除成功')
      loadStoryList()
    })
}
</script>

<style scoped>
.page-wrap { display: flex; gap: 14px; }
.left-card { width: 280px; flex: 0 0 280px; }
.right-card { flex: 1; }
.left-toolbar { display: flex; align-items: center; justify-content: space-between; margin-bottom: 12px; }
.left-title { font-size: 14px; font-weight: 600; color: #303133; }

.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.toolbar-left { display: flex; gap: 10px; }
.current-family { margin-bottom: 10px; font-size: 13px; color: #909399; }
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
.story-tip { font-size: 13px; color: #909399; line-height: 1.6; }
.story-toolbar { margin-bottom: 12px; display: flex; justify-content: flex-end; }
.quill-wrap { width: 100%; }

.detail-tabs { margin-top: 10px; }
.detail-stories { margin-top: 16px; }
.detail-stories-title { margin: 0 0 10px; font-size: 16px; color: #303133; }
.detail-story-item { padding: 10px 12px; border: 1px solid #ebeef5; border-radius: 8px; margin-bottom: 12px; }
.detail-story-title { font-weight: 600; margin-bottom: 8px; color: #303133; }
.detail-story-content :deep(p) { margin: 6px 0; line-height: 1.8; color: #606266; }
.detail-story-content :deep(img) { max-width: 100%; border-radius: 6px; }
.msg-content { margin-right: 8px; }
.msg-detail { white-space: pre-wrap; line-height: 1.8; color: #606266; }
</style>
