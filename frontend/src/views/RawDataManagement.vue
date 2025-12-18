<template>
  <div class="page-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>原始数据</el-breadcrumb-item>
      </el-breadcrumb>
      <h2 class="page-title">原始数据管理</h2>
    </div>

    <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="数据类型">
          <el-select v-model="searchForm.dataType" placeholder="请选择类型" clearable style="width: 150px">
            <el-option v-for="type in dataTypes" :key="type" :label="type" :value="type" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理状态">
          <el-select v-model="searchForm.processed" placeholder="请选择状态" clearable style="width: 150px">
            <el-option label="已处理" :value="true" />
            <el-option label="未处理" :value="false" />
          </el-select>
        </el-form-item>
        <el-form-item label="采集时间">
          <el-date-picker
            v-model="searchForm.timeRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 260px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作区域 -->
    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>原始数据列表</span>
          <div class="header-actions">
            <el-button type="primary" :icon="Plus" @click="handleAdd">新增数据</el-button>
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
        <el-table-column prop="dataType" label="数据类型" width="120">
          <template #default="{ row }">
            <el-tag>{{ row.dataType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="source" label="数据来源" min-width="150" />
        <el-table-column prop="processed" label="处理状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.processed ? 'success' : 'warning'">
              {{ row.processed ? '已处理' : '未处理' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="collectTime" label="采集时间" width="180" />
        <el-table-column label="操作" width="220" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="View" @click="handleView(row)">详情</el-button>
            <el-button
              v-if="!row.processed"
              type="success"
              link
              :icon="Check"
              @click="handleMarkProcessed(row)"
            >
              标记处理
            </el-button>
            <el-popconfirm title="确定要删除该数据吗？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button type="danger" link :icon="Delete">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <!-- 空状态 -->
      <el-empty v-if="!loading && !tableData.length" description="暂无数据" />

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

    <!-- 新增弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="新增原始数据"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="数据类型" prop="dataType">
          <el-select v-model="formData.dataType" placeholder="请选择数据类型" style="width: 100%">
            <el-option v-for="type in dataTypes" :key="type" :label="type" :value="type" />
          </el-select>
        </el-form-item>
        <el-form-item label="数据来源" prop="source">
          <el-input v-model="formData.source" placeholder="请输入数据来源" />
        </el-form-item>
        <el-form-item label="数据内容" prop="content">
          <el-input
            v-model="formData.contentStr"
            type="textarea"
            :rows="6"
            placeholder="请输入 JSON 格式的数据内容"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      title="数据详情"
      width="700px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="ID" :span="2">{{ currentDetail.id }}</el-descriptions-item>
        <el-descriptions-item label="数据类型">{{ currentDetail.dataType }}</el-descriptions-item>
        <el-descriptions-item label="数据来源">{{ currentDetail.source }}</el-descriptions-item>
        <el-descriptions-item label="处理状态">
          <el-tag :type="currentDetail.processed ? 'success' : 'warning'">
            {{ currentDetail.processed ? '已处理' : '未处理' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="采集时间">{{ currentDetail.collectTime }}</el-descriptions-item>
        <el-descriptions-item label="数据内容" :span="2">
          <pre class="json-content">{{ formatJson(currentDetail.content) }}</pre>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Delete, View, Check } from '@element-plus/icons-vue'
import { rawDataAPI } from '../api'
import { USE_MOCK, mockRawDataAPI } from '../mock'

// 获取 API
const api = USE_MOCK ? mockRawDataAPI : rawDataAPI

// 数据类型选项
const dataTypes = ['地震数据', '洪水数据', '气象数据', '火灾数据', '泥石流数据', '台风数据']

// 搜索表单
const searchForm = reactive({
  dataType: '',
  processed: '',
  timeRange: []
})

// 表格数据
const tableData = ref([])
const loading = ref(false)
const selectedIds = ref([])

// 分页
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 新增弹窗
const dialogVisible = ref(false)
const formRef = ref(null)
const submitLoading = ref(false)
const formData = reactive({
  dataType: '',
  source: '',
  contentStr: ''
})

// 详情弹窗
const detailVisible = ref(false)
const currentDetail = ref({})

// 表单验证规则
const formRules = {
  dataType: [{ required: true, message: '请选择数据类型', trigger: 'change' }],
  source: [{ required: true, message: '请输入数据来源', trigger: 'blur' }],
  contentStr: [{ required: true, message: '请输入数据内容', trigger: 'blur' }]
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await api.getAll()
    if (res.data.code === 200) {
      let data = res.data.data || []
      // 前端筛选
      if (searchForm.dataType) {
        data = data.filter(item => item.dataType === searchForm.dataType)
      }
      if (searchForm.processed !== '') {
        data = data.filter(item => item.processed === searchForm.processed)
      }
      if (searchForm.timeRange && searchForm.timeRange.length === 2) {
        const [start, end] = searchForm.timeRange
        data = data.filter(item => {
          const time = item.collectTime.split(' ')[0]
          return time >= start && time <= end
        })
      }
      pagination.total = data.length
      // 前端分页
      const start = (pagination.page - 1) * pagination.size
      tableData.value = data.slice(start, start + pagination.size)
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadData()
}

// 重置
const handleReset = () => {
  searchForm.dataType = ''
  searchForm.processed = ''
  searchForm.timeRange = []
  pagination.page = 1
  loadData()
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

// 新增
const handleAdd = () => {
  Object.assign(formData, { dataType: '', source: '', contentStr: '{\n  \n}' })
  dialogVisible.value = true
}

// 查看详情
const handleView = (row) => {
  currentDetail.value = { ...row }
  detailVisible.value = true
}

// 标记为已处理
const handleMarkProcessed = async (row) => {
  try {
    await ElMessageBox.confirm('确定要将该数据标记为已处理吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await api.markAsProcessed(row.id)
    ElMessage.success('标记成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
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
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 条数据吗？`, '提示', {
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

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    // 解析 JSON 内容
    let content
    try {
      content = JSON.parse(formData.contentStr)
    } catch (e) {
      ElMessage.error('数据内容必须是有效的 JSON 格式')
      return
    }
    submitLoading.value = true
    await api.create({
      dataType: formData.dataType,
      source: formData.source,
      content,
      processed: false
    })
    ElMessage.success('新增成功')
    dialogVisible.value = false
    loadData()
  } catch (error) {
    if (error !== false) {
      ElMessage.error('操作失败')
    }
  } finally {
    submitLoading.value = false
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
  loadData()
}

// 页码变化
const handlePageChange = () => {
  loadData()
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
  max-height: 300px;
  overflow-y: auto;
}
</style>
