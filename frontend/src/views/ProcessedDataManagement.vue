<template>
  <div class="page-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>处理数据</el-breadcrumb-item>
      </el-breadcrumb>
      <h2 class="page-title">处理数据管理</h2>
    </div>

    <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="处理类型">
          <el-select v-model="searchForm.processType" placeholder="请选择类型" clearable style="width: 150px">
            <el-option v-for="type in processTypes" :key="type" :label="type" :value="type" />
          </el-select>
        </el-form-item>
        <el-form-item label="置信度范围">
          <el-slider
            v-model="searchForm.confidenceRange"
            range
            :min="0"
            :max="1"
            :step="0.1"
            :format-tooltip="(val) => val.toFixed(1)"
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="搜索处理结果" clearable style="width: 180px" />
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
          <span>处理数据列表</span>
          <div class="header-actions">
            <el-button type="primary" :icon="Plus" @click="handleAdd">新增处理数据</el-button>
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
        <el-table-column prop="rawDataId" label="关联原始数据" width="130">
          <template #default="{ row }">
            <el-link type="primary" @click="viewRawData(row.rawDataId)">
              {{ row.rawDataId.substring(0, 6) }}...
            </el-link>
          </template>
        </el-table-column>
        <el-table-column prop="processType" label="处理类型" width="120">
          <template #default="{ row }">
            <el-tag type="info">{{ row.processType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="confidenceScore" label="置信度" width="180">
          <template #default="{ row }">
            <div class="confidence-cell">
              <el-progress
                :percentage="row.confidenceScore * 100"
                :color="getConfidenceColor(row.confidenceScore)"
                :stroke-width="10"
                :show-text="false"
                style="width: 100px"
              />
              <span :style="{ color: getConfidenceColor(row.confidenceScore) }">
                {{ (row.confidenceScore * 100).toFixed(0) }}%
              </span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="processTime" label="处理时间" width="180" />
        <el-table-column label="操作" width="220" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="View" @click="handleView(row)">详情</el-button>
            <el-button type="warning" link :icon="Edit" @click="handleEditConfidence(row)">
              修改置信度
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
      title="新增处理数据"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="120px"
      >
        <el-form-item label="关联原始数据" prop="rawDataId">
          <el-input v-model="formData.rawDataId" placeholder="请输入原始数据ID" />
        </el-form-item>
        <el-form-item label="处理类型" prop="processType">
          <el-select v-model="formData.processType" placeholder="请选择处理类型" style="width: 100%">
            <el-option v-for="type in processTypes" :key="type" :label="type" :value="type" />
          </el-select>
        </el-form-item>
        <el-form-item label="置信度" prop="confidenceScore">
          <el-slider
            v-model="formData.confidenceScore"
            :min="0"
            :max="1"
            :step="0.01"
            show-input
            :format-tooltip="(val) => (val * 100).toFixed(0) + '%'"
          />
        </el-form-item>
        <el-form-item label="处理结果" prop="resultStr">
          <el-input
            v-model="formData.resultStr"
            type="textarea"
            :rows="6"
            placeholder="请输入 JSON 格式的处理结果"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 修改置信度弹窗 -->
    <el-dialog
      v-model="confidenceDialogVisible"
      title="修改置信度"
      width="450px"
      :close-on-click-modal="false"
    >
      <el-form label-width="80px">
        <el-form-item label="置信度">
          <el-slider
            v-model="editConfidenceScore"
            :min="0"
            :max="1"
            :step="0.01"
            show-input
            :format-tooltip="(val) => (val * 100).toFixed(0) + '%'"
          />
        </el-form-item>
        <el-form-item>
          <div class="confidence-preview">
            <span>预览：</span>
            <el-progress
              :percentage="editConfidenceScore * 100"
              :color="getConfidenceColor(editConfidenceScore)"
              :stroke-width="14"
              style="width: 200px"
            />
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="confidenceDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleConfidenceSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      title="处理数据详情"
      width="700px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="ID" :span="2">{{ currentDetail.id }}</el-descriptions-item>
        <el-descriptions-item label="关联原始数据">{{ currentDetail.rawDataId }}</el-descriptions-item>
        <el-descriptions-item label="处理类型">
          <el-tag type="info">{{ currentDetail.processType }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="置信度">
          <div class="confidence-cell">
            <el-progress
              :percentage="(currentDetail.confidenceScore || 0) * 100"
              :color="getConfidenceColor(currentDetail.confidenceScore)"
              :stroke-width="12"
              style="width: 120px"
            />
            <span :style="{ color: getConfidenceColor(currentDetail.confidenceScore), marginLeft: '10px' }">
              {{ ((currentDetail.confidenceScore || 0) * 100).toFixed(0) }}%
            </span>
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="处理时间">{{ currentDetail.processTime }}</el-descriptions-item>
        <el-descriptions-item label="处理结果" :span="2">
          <pre class="json-content">{{ formatJson(currentDetail.result) }}</pre>
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
import { Search, Refresh, Plus, Delete, View, Edit } from '@element-plus/icons-vue'
import { processedDataAPI } from '../api'

// 使用真实 API
const api = processedDataAPI

// 处理类型选项
const processTypes = ['数据清洗', '趋势分析', '风险评估', '路径预测', '数据融合', '异常检测']

// 搜索表单
const searchForm = reactive({
  processType: '',
  confidenceRange: [0, 1],
  keyword: ''
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
  rawDataId: '',
  processType: '',
  confidenceScore: 0.8,
  resultStr: ''
})

// 修改置信度弹窗
const confidenceDialogVisible = ref(false)
const editConfidenceScore = ref(0.8)
const editingId = ref(null)

// 详情弹窗
const detailVisible = ref(false)
const currentDetail = ref({})

// 表单验证规则
const formRules = {
  rawDataId: [{ required: true, message: '请输入原始数据ID', trigger: 'blur' }],
  processType: [{ required: true, message: '请选择处理类型', trigger: 'change' }],
  resultStr: [{ required: true, message: '请输入处理结果', trigger: 'blur' }]
}

// 获取置信度颜色
const getConfidenceColor = (score) => {
  if (!score && score !== 0) return '#909399'
  if (score >= 0.8) return '#67C23A'
  if (score >= 0.5) return '#E6A23C'
  return '#F56C6C'
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await api.getAll()
    if (res.data.code === 200) {
      let data = res.data.data || []
      // 前端筛选
      if (searchForm.processType) {
        data = data.filter(item => item.processType === searchForm.processType)
      }
      // 置信度范围筛选
      const [minScore, maxScore] = searchForm.confidenceRange
      data = data.filter(item =>
        item.confidenceScore >= minScore && item.confidenceScore <= maxScore
      )
      // 关键词搜索
      if (searchForm.keyword) {
        data = data.filter(item => {
          const resultStr = JSON.stringify(item.result || {})
          return resultStr.includes(searchForm.keyword)
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
  searchForm.processType = ''
  searchForm.confidenceRange = [0, 1]
  searchForm.keyword = ''
  pagination.page = 1
  loadData()
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

// 新增
const handleAdd = () => {
  Object.assign(formData, {
    rawDataId: '',
    processType: '',
    confidenceScore: 0.8,
    resultStr: '{\n  \n}'
  })
  dialogVisible.value = true
}

// 查看详情
const handleView = (row) => {
  currentDetail.value = { ...row }
  detailVisible.value = true
}

// 查看原始数据
const viewRawData = (rawDataId) => {
  ElMessage.info(`查看原始数据: ${rawDataId}`)
}

// 修改置信度
const handleEditConfidence = (row) => {
  editingId.value = row.id
  editConfidenceScore.value = row.confidenceScore
  confidenceDialogVisible.value = true
}

// 提交置信度修改
const handleConfidenceSubmit = async () => {
  try {
    submitLoading.value = true
    await api.updateConfidence(editingId.value, editConfidenceScore.value)
    ElMessage.success('修改成功')
    confidenceDialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error('修改失败')
  } finally {
    submitLoading.value = false
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
    let result
    try {
      result = JSON.parse(formData.resultStr)
    } catch (e) {
      ElMessage.error('处理结果必须是有效的 JSON 格式')
      return
    }
    submitLoading.value = true
    await api.create({
      rawDataId: formData.rawDataId,
      processType: formData.processType,
      confidenceScore: formData.confidenceScore,
      result
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
  align-items: center;
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

.confidence-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.confidence-preview {
  display: flex;
  align-items: center;
  gap: 10px;
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
