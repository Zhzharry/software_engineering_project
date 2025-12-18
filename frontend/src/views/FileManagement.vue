<template>
  <div class="file-management">
    <!-- 搜索和操作栏 -->
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="文件类型">
          <el-select v-model="searchForm.fileType" placeholder="全部类型" clearable style="width: 150px">
            <el-option label="图片" value="image" />
            <el-option label="文档" value="document" />
            <el-option label="视频" value="video" />
            <el-option label="音频" value="audio" />
          </el-select>
        </el-form-item>
        <el-form-item label="文件名">
          <el-input v-model="searchForm.fileName" placeholder="搜索文件名" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon> 搜索
          </el-button>
          <el-button @click="resetSearch">
            <el-icon><Refresh /></el-icon> 重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 工具栏 -->
    <el-card shadow="never" class="toolbar-card">
      <div class="toolbar">
        <div class="toolbar-left">
          <el-upload
            ref="uploadRef"
            :action="uploadUrl"
            :data="uploadData"
            :before-upload="beforeUpload"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :show-file-list="false"
            multiple
          >
            <el-button type="primary">
              <el-icon><Upload /></el-icon> 上传文件
            </el-button>
          </el-upload>
          <el-button type="danger" :disabled="selectedFiles.length === 0" @click="handleBatchDelete">
            <el-icon><Delete /></el-icon> 批量删除 ({{ selectedFiles.length }})
          </el-button>
        </div>
        <div class="toolbar-right">
          <el-radio-group v-model="viewMode" size="small">
            <el-radio-button value="table">
              <el-icon><List /></el-icon>
            </el-radio-button>
            <el-radio-button value="grid">
              <el-icon><Grid /></el-icon>
            </el-radio-button>
          </el-radio-group>
        </div>
      </div>
    </el-card>

    <!-- 文件列表 - 表格视图 -->
    <el-card v-if="viewMode === 'table'" shadow="never" class="table-card" v-loading="loading">
      <el-table
        :data="filteredFiles"
        @selection-change="handleSelectionChange"
        style="width: 100%"
        row-key="id"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column label="预览" width="80">
          <template #default="{ row }">
            <el-image
              v-if="isImage(row.mimeType)"
              :src="getPreviewUrl(row.id)"
              :preview-src-list="[getPreviewUrl(row.id)]"
              fit="cover"
              style="width: 50px; height: 50px; border-radius: 4px;"
            />
            <el-icon v-else :size="40" :color="getFileIconColor(row.fileType)">
              <component :is="getFileIcon(row.fileType)" />
            </el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="originalName" label="文件名" min-width="200" show-overflow-tooltip />
        <el-table-column prop="fileType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getFileTypeTag(row.fileType)" size="small">
              {{ getFileTypeLabel(row.fileType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="fileSize" label="大小" width="100">
          <template #default="{ row }">
            {{ formatFileSize(row.fileSize) }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="上传时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handlePreview(row)">
              <el-icon><View /></el-icon> 预览
            </el-button>
            <el-button type="success" link @click="handleDownload(row)">
              <el-icon><Download /></el-icon> 下载
            </el-button>
            <el-popconfirm title="确定删除此文件？" @confirm="handleDelete(row)">
              <template #reference>
                <el-button type="danger" link>
                  <el-icon><Delete /></el-icon> 删除
                </el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[12, 24, 48, 96]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 文件列表 - 网格视图 -->
    <el-card v-else shadow="never" class="grid-card" v-loading="loading">
      <div class="file-grid">
        <div
          v-for="file in filteredFiles"
          :key="file.id"
          class="file-item"
          :class="{ selected: isSelected(file) }"
          @click="toggleSelect(file)"
        >
          <div class="file-preview">
            <el-image
              v-if="isImage(file.mimeType)"
              :src="getPreviewUrl(file.id)"
              fit="cover"
              class="preview-image"
            />
            <el-icon v-else :size="60" :color="getFileIconColor(file.fileType)">
              <component :is="getFileIcon(file.fileType)" />
            </el-icon>
          </div>
          <div class="file-info">
            <div class="file-name" :title="file.originalName">{{ file.originalName }}</div>
            <div class="file-meta">
              <span>{{ formatFileSize(file.fileSize) }}</span>
              <el-tag :type="getFileTypeTag(file.fileType)" size="small">
                {{ getFileTypeLabel(file.fileType) }}
              </el-tag>
            </div>
          </div>
          <div class="file-actions">
            <el-button type="primary" circle size="small" @click.stop="handlePreview(file)">
              <el-icon><View /></el-icon>
            </el-button>
            <el-button type="success" circle size="small" @click.stop="handleDownload(file)">
              <el-icon><Download /></el-icon>
            </el-button>
            <el-button type="danger" circle size="small" @click.stop="handleDelete(file)">
              <el-icon><Delete /></el-icon>
            </el-button>
          </div>
          <div v-if="isSelected(file)" class="selected-mark">
            <el-icon><Check /></el-icon>
          </div>
        </div>
      </div>

      <el-empty v-if="filteredFiles.length === 0" description="暂无文件" />

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[12, 24, 48, 96]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 预览对话框 -->
    <el-dialog v-model="previewVisible" :title="previewFile?.originalName" width="800px" destroy-on-close>
      <div class="preview-content">
        <el-image
          v-if="isImage(previewFile?.mimeType)"
          :src="getPreviewUrl(previewFile?.id)"
          fit="contain"
          style="max-height: 500px; width: 100%;"
        />
        <video
          v-else-if="isVideo(previewFile?.mimeType)"
          :src="getPreviewUrl(previewFile?.id)"
          controls
          style="max-height: 500px; width: 100%;"
        />
        <audio
          v-else-if="isAudio(previewFile?.mimeType)"
          :src="getPreviewUrl(previewFile?.id)"
          controls
          style="width: 100%;"
        />
        <div v-else class="no-preview">
          <el-icon :size="80" color="#909399"><Document /></el-icon>
          <p>该文件类型不支持预览</p>
          <el-button type="primary" @click="handleDownload(previewFile)">
            <el-icon><Download /></el-icon> 下载文件
          </el-button>
        </div>
      </div>
      <template #footer>
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="文件大小">{{ formatFileSize(previewFile?.fileSize) }}</el-descriptions-item>
          <el-descriptions-item label="文件类型">{{ previewFile?.mimeType }}</el-descriptions-item>
          <el-descriptions-item label="上传时间">{{ formatTime(previewFile?.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="文件描述">{{ previewFile?.description || '无' }}</el-descriptions-item>
        </el-descriptions>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { fileAPI } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Document, Picture, VideoCamera, Headset, Folder,
  View, Download, Delete, Upload, Search, Refresh, Check, List, Grid
} from '@element-plus/icons-vue'

// 上传配置
const uploadUrl = '/api/files/upload'
const uploadData = reactive({
  fileType: 'document'
})

// 视图模式
const viewMode = ref('table')

// 搜索表单
const searchForm = reactive({
  fileType: '',
  fileName: ''
})

// 文件列表
const files = ref([])
const loading = ref(false)
const selectedFiles = ref([])

// 分页
const pagination = reactive({
  page: 1,
  size: 12,
  total: 0
})

// 预览
const previewVisible = ref(false)
const previewFile = ref(null)

// 过滤后的文件
const filteredFiles = computed(() => {
  let result = files.value
  if (searchForm.fileType) {
    result = result.filter(f => f.fileType === searchForm.fileType)
  }
  if (searchForm.fileName) {
    const keyword = searchForm.fileName.toLowerCase()
    result = result.filter(f => f.originalName.toLowerCase().includes(keyword))
  }
  pagination.total = result.length
  const start = (pagination.page - 1) * pagination.size
  return result.slice(start, start + pagination.size)
})

// 加载文件列表
const loadFiles = async () => {
  loading.value = true
  try {
    const res = await fileAPI.getAll()
    if (res.data?.code === 200) {
      files.value = res.data.data || []
      pagination.total = files.value.length
    }
  } catch (error) {
    console.error('加载文件列表失败:', error)
    ElMessage.error('加载文件列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
}

// 重置搜索
const resetSearch = () => {
  searchForm.fileType = ''
  searchForm.fileName = ''
  pagination.page = 1
}

// 上传前检查
const beforeUpload = (file) => {
  // 自动判断文件类型
  if (file.type.startsWith('image/')) {
    uploadData.fileType = 'image'
  } else if (file.type.startsWith('video/')) {
    uploadData.fileType = 'video'
  } else if (file.type.startsWith('audio/')) {
    uploadData.fileType = 'audio'
  } else {
    uploadData.fileType = 'document'
  }
  return true
}

// 上传成功
const handleUploadSuccess = (response) => {
  if (response.code === 200) {
    ElMessage.success('文件上传成功')
    loadFiles()
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

// 上传失败
const handleUploadError = () => {
  ElMessage.error('文件上传失败')
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedFiles.value = selection
}

// 切换选择
const toggleSelect = (file) => {
  const index = selectedFiles.value.findIndex(f => f.id === file.id)
  if (index >= 0) {
    selectedFiles.value.splice(index, 1)
  } else {
    selectedFiles.value.push(file)
  }
}

// 是否选中
const isSelected = (file) => {
  return selectedFiles.value.some(f => f.id === file.id)
}

// 预览
const handlePreview = (file) => {
  previewFile.value = file
  previewVisible.value = true
}

// 下载
const handleDownload = async (file) => {
  try {
    const res = await fileAPI.download(file.id)
    const blob = new Blob([res.data])
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = file.originalName
    a.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('下载成功')
  } catch (error) {
    console.error('下载失败:', error)
    ElMessage.error('下载失败')
  }
}

// 删除
const handleDelete = async (file) => {
  try {
    const res = await fileAPI.delete(file.id)
    if (res.data?.code === 200) {
      ElMessage.success('删除成功')
      loadFiles()
    } else {
      ElMessage.error(res.data?.message || '删除失败')
    }
  } catch (error) {
    console.error('删除失败:', error)
    ElMessage.error('删除失败')
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定删除选中的 ${selectedFiles.value.length} 个文件吗？`, '批量删除', {
      type: 'warning'
    })
    for (const file of selectedFiles.value) {
      await fileAPI.delete(file.id)
    }
    ElMessage.success('批量删除成功')
    selectedFiles.value = []
    loadFiles()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
      ElMessage.error('批量删除失败')
    }
  }
}

// 分页
const handlePageChange = (page) => {
  pagination.page = page
}

const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
}

// 工具函数
const getPreviewUrl = (id) => fileAPI.preview(id)

const isImage = (mimeType) => mimeType?.startsWith('image/')
const isVideo = (mimeType) => mimeType?.startsWith('video/')
const isAudio = (mimeType) => mimeType?.startsWith('audio/')

const getFileIcon = (fileType) => {
  const icons = {
    image: Picture,
    video: VideoCamera,
    audio: Headset,
    document: Document
  }
  return icons[fileType] || Folder
}

const getFileIconColor = (fileType) => {
  const colors = {
    image: '#409EFF',
    video: '#E6A23C',
    audio: '#F56C6C',
    document: '#67C23A'
  }
  return colors[fileType] || '#909399'
}

const getFileTypeTag = (fileType) => {
  const tags = {
    image: 'primary',
    video: 'warning',
    audio: 'danger',
    document: 'success'
  }
  return tags[fileType] || 'info'
}

const getFileTypeLabel = (fileType) => {
  const labels = {
    image: '图片',
    video: '视频',
    audio: '音频',
    document: '文档'
  }
  return labels[fileType] || '其他'
}

const formatFileSize = (bytes) => {
  if (!bytes) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

onMounted(() => {
  loadFiles()
})
</script>

<style scoped>
.file-management {
  padding: 20px;
}

.search-card {
  margin-bottom: 16px;
}

.toolbar-card {
  margin-bottom: 16px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.toolbar-left {
  display: flex;
  gap: 10px;
}

.table-card, .grid-card {
  min-height: 400px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 网格视图 */
.file-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 16px;
}

.file-item {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 12px;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
}

.file-item:hover {
  border-color: #409EFF;
  box-shadow: 0 2px 12px rgba(64, 158, 255, 0.2);
}

.file-item.selected {
  border-color: #409EFF;
  background-color: #ecf5ff;
}

.file-preview {
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  border-radius: 4px;
  margin-bottom: 10px;
  overflow: hidden;
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.file-info {
  text-align: center;
}

.file-name {
  font-size: 14px;
  color: #303133;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 6px;
}

.file-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #909399;
}

.file-actions {
  position: absolute;
  top: 8px;
  right: 8px;
  display: none;
  gap: 4px;
}

.file-item:hover .file-actions {
  display: flex;
}

.selected-mark {
  position: absolute;
  top: 8px;
  left: 8px;
  width: 24px;
  height: 24px;
  background: #409EFF;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

/* 预览 */
.preview-content {
  min-height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.no-preview {
  text-align: center;
  padding: 40px;
}

.no-preview p {
  color: #909399;
  margin: 16px 0;
}
</style>
