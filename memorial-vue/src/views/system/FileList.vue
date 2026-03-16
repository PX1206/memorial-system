<template>
  <div>
    <el-card>
      <div class="toolbar">
        <div class="toolbar-left">
          <el-input v-model="query.keyword" placeholder="搜索文件名" clearable style="width: 220px" @clear="loadData" />
          <el-select v-model="query.type" placeholder="文件类型" clearable style="width: 140px" @change="loadData">
            <el-option label="图片" :value="1" />
            <el-option label="文档" :value="2" />
            <el-option label="视频" :value="3" />
            <el-option label="音频" :value="4" />
            <el-option label="其它" :value="5" />
          </el-select>
          <el-button type="primary" @click="loadData"><el-icon><Search /></el-icon>搜索</el-button>
        </div>
        <div class="toolbar-right">
          <el-radio-group v-model="viewMode" size="small">
            <el-radio-button label="table"><el-icon><List /></el-icon></el-radio-button>
            <el-radio-button label="grid"><el-icon><Grid /></el-icon></el-radio-button>
          </el-radio-group>
        </div>
      </div>

      <!-- 表格视图 -->
      <el-table v-if="viewMode === 'table'" :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="name" label="文件名">
          <template #default="{ row }">{{ row.name }}{{ row.suffix }}</template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="80">
          <template #default="{ row }">
            <el-tag :type="typeTagMap[row.type]?.tag || 'info'" size="small">{{ typeTagMap[row.type]?.label || '未知' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="size" label="大小" width="100">
          <template #default="{ row }">{{ formatSize(row.size) }}</template>
        </el-table-column>
        <el-table-column prop="createBy" label="上传者" width="100" />
        <el-table-column prop="createTime" label="上传时间" width="170" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="openPreview(row)">查看</el-button>
            <el-button size="small" type="success" link @click="handleDownload(row)">下载</el-button>
            <el-button size="small" type="danger" link @click="handleDelete(row)" v-permission="'sys:file:delete'">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 网格视图 -->
      <div v-else class="grid-view" v-loading="loading">
        <div v-for="item in tableData" :key="item.code" class="grid-item" @click="openPreview(item)">
          <div class="grid-thumb">
            <el-image v-if="item.type === 1" :src="item.url" fit="cover" class="grid-img" />
            <div v-else class="grid-icon">
              <el-icon :size="36" color="#c0c4cc"><Document /></el-icon>
            </div>
          </div>
          <div class="grid-info">
            <span class="grid-name" :title="item.name + item.suffix">{{ item.name }}{{ item.suffix }}</span>
            <div class="grid-actions">
              <el-button size="small" type="success" link @click.stop="handleDownload(item)">下载</el-button>
              <el-button size="small" type="danger" link @click.stop="handleDelete(item)" v-permission="'sys:file:delete'">删除</el-button>
            </div>
          </div>
        </div>
        <el-empty v-if="!loading && tableData.length === 0" description="暂无文件" />
      </div>

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="query.pageIndex"
          v-model:page-size="query.pageSize"
          :total="total"
          :page-sizes="[12, 24, 48]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="loadData"
          @size-change="loadData"
        />
      </div>
    </el-card>

    <!-- 预览对话框 -->
    <el-dialog
      v-model="previewVisible"
      :title="previewFile.name ? (previewFile.name + previewFile.suffix) : '文件预览'"
      width="800px"
      destroy-on-close
      class="preview-dialog"
    >
      <div class="preview-body">
        <el-image
          v-if="previewFile.type === 1"
          :src="previewFile.url"
          fit="contain"
          class="preview-image"
          :preview-src-list="allImageUrls"
          :initial-index="allImageUrls.indexOf(previewFile.url)"
          preview-teleported
        />
        <div v-else class="preview-file-placeholder">
          <el-icon :size="64" color="#c0c4cc"><Document /></el-icon>
          <p>该文件类型不支持预览</p>
          <el-button type="primary" @click="handleDownload(previewFile)">下载文件</el-button>
        </div>
      </div>
      <div class="preview-info">
        <el-descriptions :column="3" size="small" border>
          <el-descriptions-item label="类型">{{ typeTagMap[previewFile.type]?.label || '未知' }}</el-descriptions-item>
          <el-descriptions-item label="大小">{{ formatSize(previewFile.size) }}</el-descriptions-item>
          <el-descriptions-item label="上传者">{{ previewFile.createBy }}</el-descriptions-item>
          <el-descriptions-item label="上传时间" :span="2">{{ previewFile.createTime }}</el-descriptions-item>
          <el-descriptions-item label="访问地址">
            <el-link type="primary" :href="previewFile.url" target="_blank" :underline="false">打开链接</el-link>
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button type="success" @click="handleDownload(previewFile)">
          <el-icon><Download /></el-icon>下载
        </el-button>
        <el-button type="danger" @click="handleDeleteFromPreview" v-permission="'sys:file:delete'">
          <el-icon><Delete /></el-icon>删除
        </el-button>
        <el-button @click="previewVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { Search, List, Grid, Document, Download, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getFilePageList, deleteFile } from '@/api/system'

const loading = ref(false)
const total = ref(0)
const viewMode = ref('table')
const previewVisible = ref(false)
const previewFile = ref({})

const query = reactive({ keyword: '', type: null, pageIndex: 1, pageSize: 12 })
const tableData = ref([])

const typeTagMap = {
  1: { label: '图片', tag: '' },
  2: { label: '文档', tag: 'success' },
  3: { label: '视频', tag: 'warning' },
  4: { label: '音频', tag: 'danger' },
  5: { label: '其它', tag: 'info' }
}

const allImageUrls = computed(() => {
  return tableData.value.filter(f => f.type === 1).map(f => f.url)
})

function formatSize(kb) {
  if (!kb) return '-'
  if (kb < 1024) return kb + ' KB'
  return (kb / 1024).toFixed(1) + ' MB'
}

async function loadData() {
  loading.value = true
  try {
    const res = await getFilePageList(query)
    if (res) {
      tableData.value = res.records || []
      total.value = res.total || 0
    }
  } catch { /* 静默 */ }
  finally { loading.value = false }
}

onMounted(() => loadData())

function openPreview(item) {
  previewFile.value = { ...item }
  previewVisible.value = true
}

function handleDownload(item) {
  const link = document.createElement('a')
  link.href = item.url
  link.download = (item.name || 'file') + (item.suffix || '')
  link.target = '_blank'
  link.click()
}

function handleDelete(item) {
  ElMessageBox.confirm(`确定删除文件 "${item.name}${item.suffix}" 吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteFile(item.code)
      ElMessage.success('删除成功')
      loadData()
    })
}

async function handleDeleteFromPreview() {
  try {
    await ElMessageBox.confirm(`确定删除文件 "${previewFile.value.name}${previewFile.value.suffix}" 吗？`, '提示', { type: 'warning' })
    await deleteFile(previewFile.value.code)
    ElMessage.success('删除成功')
    previewVisible.value = false
    loadData()
  } catch { /* 取消 */ }
}
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.toolbar-left { display: flex; gap: 10px; }
.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 16px; }
.grid-view { display: grid; grid-template-columns: repeat(auto-fill, minmax(180px, 1fr)); gap: 16px; min-height: 200px; }
.grid-item { border: 1px solid #ebeef5; border-radius: 6px; overflow: hidden; cursor: pointer; transition: box-shadow .2s; }
.grid-item:hover { box-shadow: 0 2px 12px rgba(0,0,0,.1); }
.grid-thumb { height: 140px; display: flex; align-items: center; justify-content: center; background: #f5f7fa; }
.grid-img { width: 100%; height: 100%; }
.grid-icon { display: flex; align-items: center; justify-content: center; width: 100%; height: 100%; }
.grid-info { padding: 8px 10px; }
.grid-name { font-size: 13px; display: block; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.grid-actions { display: flex; gap: 4px; margin-top: 4px; }
.preview-body { text-align: center; margin-bottom: 16px; }
.preview-image { max-height: 450px; max-width: 100%; }
.preview-file-placeholder { padding: 60px 0; text-align: center; }
.preview-file-placeholder p { margin: 12px 0 16px; color: #909399; }
.preview-info { margin-top: 12px; }
</style>
