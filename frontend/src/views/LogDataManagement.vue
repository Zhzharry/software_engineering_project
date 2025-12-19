<template>
  <div class="page-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>日志管理</el-breadcrumb-item>
      </el-breadcrumb>
      <h2 class="page-title">日志管理</h2>
    </div>

    <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="日志级别">
          <el-select v-model="searchForm.level" placeholder="请选择级别" clearable style="width: 150px">
            <el-option label="INFO" value="INFO">
              <el-tag type="primary" size="small">INFO</el-tag>
            </el-option>
            <el-option label="WARN" value="WARN">
              <el-tag type="warning" size="small">WARN</el-tag>
            </el-option>
            <el-option label="ERROR" value="ERROR">
              <el-tag type="danger" size="small">ERROR</el-tag>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="searchForm.timeRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 380px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stats-card" shadow="hover">
          <div class="stats-content">
            <div class="stats-icon info">
              <el-icon :size="28"><InfoFilled /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.info }}</div>
              <div class="stats-label">INFO 日志</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card" shadow="hover">
          <div class="stats-content">
            <div class="stats-icon warning">
              <el-icon :size="28"><WarningFilled /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.warn }}</div>
              <div class="stats-label">WARN 日志</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card" shadow="hover">
          <div class="stats-content">
            <div class="stats-icon danger">
              <el-icon :size="28"><CircleCloseFilled /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.error }}</div>
              <div class="stats-label">ERROR 日志</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card" shadow="hover">
          <div class="stats-content">
            <div class="stats-icon total">
              <el-icon :size="28"><Document /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.total }}</div>
              <div class="stats-label">总日志数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 操作区域 -->
    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>日志列表</span>
          <div class="header-actions">
            <el-button type="danger" :icon="Delete" :disabled="!selectedIds.length" @click="handleBatchDelete">
              批量删除
            </el-button>
          </div>
        </div>
      </template>

      <!-- 表格 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        border
        stripe
        :row-class-name="getRowClassName"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="id" label="ID" width="120">
          <template #default="{ row }">
            <el-tooltip :content="row.id" placement="top">
              <span>{{ row.id.substring(0, 8) }}...</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column prop="level" label="日志级别" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getLevelType(row.level)" effect="dark">
              {{ row.level }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="message" label="消息内容" min-width="300" show-overflow-tooltip />
        <el-table-column prop="timestamp" label="时间戳" width="180" />
        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="View" @click="handleView(row)">详情</el-button>
            <el-popconfirm title="确定要删除该日志吗？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button type="danger" link :icon="Delete">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <!-- 空状态 -->
      <el-empty v-if="!loading && !tableData.length" description="暂无日志数据" />

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      title="日志详情"
      width="700px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="ID" :span="2">{{ currentDetail.id }}</el-descriptions-item>
        <el-descriptions-item label="日志级别">
          <el-tag :type="getLevelType(currentDetail.level)" effect="dark">
            {{ currentDetail.level }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="时间戳">{{ currentDetail.timestamp }}</el-descriptions-item>
        <el-descriptions-item label="消息内容" :span="2">
          <div class="message-content">{{ currentDetail.message }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="元数据" :span="2">
          <pre class="json-content">{{ formatJson(currentDetail.metadata) }}</pre>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search, Refresh, Delete, View, Document,
  InfoFilled, WarningFilled, CircleCloseFilled
} from '@element-plus/icons-vue'
import { logDataAPI } from '../api'

// 使用真实 API
const api = logDataAPI

// 搜索表单
const searchForm = reactive({
  level: '',
  timeRange: []
})

// 表格数据
const tableData = ref([])
const allData = ref([])
const loading = ref(false)
const selectedIds = ref([])

// 分页
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 详情弹窗
const detailVisible = ref(false)
const currentDetail = ref({})

// 统计数据
const stats = computed(() => {
  const data = allData.value
  return {
    info: data.filter(item => item.level === 'INFO').length,
    warn: data.filter(item => item.level === 'WARN').length,
    error: data.filter(item => item.level === 'ERROR').length,
    total: data.length
  }
})

// 获取日志级别对应的标签类型
const getLevelType = (level) => {
  const typeMap = {
    INFO: 'primary',
    WARN: 'warning',
    ERROR: 'danger'
  }
  return typeMap[level] || 'info'
}

// 获取行样式类名
const getRowClassName = ({ row }) => {
  const classMap = {
    INFO: 'row-info',
    WARN: 'row-warn',
    ERROR: 'row-error'
  }
  return classMap[row.level] || ''
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await api.getAll()
    if (res.data.code === 200) {
      allData.value = res.data.data || []
      filterData()
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 筛选数据
const filterData = () => {
  let data = [...allData.value]
  // 级别筛选
  if (searchForm.level) {
    data = data.filter(item => item.level === searchForm.level)
  }
  // 时间范围筛选
  if (searchForm.timeRange && searchForm.timeRange.length === 2) {
    const [start, end] = searchForm.timeRange
    data = data.filter(item => {
      const time = item.timestamp
      return time >= start && time <= end
    })
  }
  pagination.total = data.length
  // 前端分页
  const startIndex = (pagination.page - 1) * pagination.size
  tableData.value = data.slice(startIndex, startIndex + pagination.size)
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  filterData()
}

// 重置
const handleReset = () => {
  searchForm.level = ''
  searchForm.timeRange = []
  pagination.page = 1
  filterData()
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

// 查看详情
const handleView = (row) => {
  currentDetail.value = { ...row }
  detailVisible.value = true
}

// 删除
const handleDelete = async (id) => {
  try {
    await api.delete(id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 条日志吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    for (const id of selectedIds.value) {
      await api.delete(id)
    }
    ElMessage.success('批量删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 格式化 JSON
const formatJson = (obj) => {
  if (!obj) return '-'
  try {
    return JSON.stringify(obj, null, 2)
  } catch (e) {
    return String(obj)
  }
}

// 分页大小变化
const handleSizeChange = () => {
  pagination.page = 1
  filterData()
}

// 页码变化
const handlePageChange = () => {
  filterData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.page-container {
  padding: 0;
}

.page-header {
  margin-bottom: 20px;
}

.page-title {
  margin: 10px 0 0 0;
  font-size: 22px;
  font-weight: 600;
  color: #303133;
}

.search-card {
  margin-bottom: 20px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.stats-row {
  margin-bottom: 20px;
}

.stats-card {
  cursor: pointer;
  transition: all 0.3s;
}

.stats-card:hover {
  transform: translateY(-2px);
}

.stats-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stats-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.stats-icon.info {
  background: linear-gradient(135deg, #409EFF 0%, #79bbff 100%);
}

.stats-icon.warning {
  background: linear-gradient(135deg, #E6A23C 0%, #f0c78a 100%);
}

.stats-icon.danger {
  background: linear-gradient(135deg, #F56C6C 0%, #f89898 100%);
}

.stats-icon.total {
  background: linear-gradient(135deg, #909399 0%, #b1b3b8 100%);
}

.stats-info {
  flex: 1;
}

.stats-value {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  line-height: 1.2;
}

.stats-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}

.table-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.message-content {
  padding: 12px;
  background: #f5f7fa;
  border-radius: 4px;
  line-height: 1.6;
  word-break: break-all;
}

.json-content {
  margin: 0;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 4px;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 13px;
  line-height: 1.5;
  white-space: pre-wrap;
  word-break: break-all;
  max-height: 200px;
  overflow-y: auto;
}

/* 表格行背景色 */
:deep(.row-info) {
  background-color: #ecf5ff !important;
}

:deep(.row-warn) {
  background-color: #fdf6ec !important;
}

:deep(.row-error) {
  background-color: #fef0f0 !important;
}
</style>
