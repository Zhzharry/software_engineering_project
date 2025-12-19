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
        <el-form-item label="灾情类别">
          <el-select v-model="searchForm.disasterCategory" placeholder="请选择类别" clearable style="width: 150px">
            <el-option v-for="category in disasterCategories" :key="category" :label="category" :value="category" />
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
        <!-- 按照数据库列顺序排列 -->
        <el-table-column prop="id" label="ID" width="120" fixed="left">
          <template #default="{ row }">
            <el-tooltip :content="String(row.id)" placement="top">
              <span>{{ String(row.id).substring(0, Math.min(8, String(row.id).length)) }}...</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column prop="dataType" label="数据类型" width="120">
          <template #default="{ row }">
            {{ row.dataType || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="disasterCategory" label="灾情类别" width="140">
          <template #default="{ row }">
            <el-tag :type="getCategoryTagType(row.disasterCategory)">
              {{ row.disasterCategory || '-' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="disasterSubcategory" label="灾情子类" width="120">
          <template #default="{ row }">
            {{ row.disasterSubcategory || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="disasterIndicator" label="灾情指标" width="120">
          <template #default="{ row }">
            {{ row.disasterIndicator || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="geoCode" label="地理码" width="130">
          <template #default="{ row }">
            {{ row.geoCode || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="disasterId" label="灾情ID" width="150" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.disasterId || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="disasterDateTime" label="灾情时间" width="170">
          <template #default="{ row }">
            {{ formatTime(row.disasterDateTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="sourceSystem" label="来源系统" min-width="150">
          <template #default="{ row }">
            {{ row.sourceSystem || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="sourceCategory" label="来源类别" width="120">
          <template #default="{ row }">
            {{ row.sourceCategory || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="carrierType" label="载体类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getCarrierTagType(row.carrierType)" size="small">
              {{ row.carrierType || '-' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="decodedDescription" label="解码描述" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.decodedDescription || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="processed" label="处理状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.processed ? 'success' : 'warning'">
              {{ row.processed ? '已处理' : '未处理' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.updateTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="View" @click="handleView(row)">详情</el-button>
            <el-button type="warning" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
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

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增原始数据' : '编辑原始数据'"
      width="900px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="120px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="数据类型" prop="dataType">
              <el-input v-model="formData.dataType" placeholder="请输入数据类型" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="灾情类别" prop="disasterCategory">
              <el-select v-model="formData.disasterCategory" placeholder="请选择灾情类别" style="width: 100%">
                <el-option v-for="category in disasterCategories" :key="category" :label="category" :value="category" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="灾情子类" prop="disasterSubcategory">
              <el-input v-model="formData.disasterSubcategory" placeholder="请输入灾情子类" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="灾情指标" prop="disasterIndicator">
              <el-input v-model="formData.disasterIndicator" placeholder="请输入灾情指标" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="地理码" prop="geoCode">
              <el-input v-model="formData.geoCode" placeholder="12位地理码" maxlength="12" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="灾情ID" prop="disasterId">
              <el-input v-model="formData.disasterId" placeholder="36位灾情ID" maxlength="36" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="灾情时间" prop="disasterDateTime">
              <el-date-picker
                v-model="formData.disasterDateTime"
                type="datetime"
                placeholder="选择灾情发生时间"
                value-format="YYYY-MM-DDTHH:mm:ss"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="来源系统" prop="sourceSystem">
              <el-input v-model="formData.sourceSystem" placeholder="请输入来源系统" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="来源类别" prop="sourceCategory">
              <el-input v-model="formData.sourceCategory" placeholder="请输入来源类别" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="载体类型" prop="carrierType">
              <el-select v-model="formData.carrierType" placeholder="请选择载体类型" style="width: 100%">
                <el-option label="文字" value="文字" />
                <el-option label="图像" value="图像" />
                <el-option label="音频" value="音频" />
                <el-option label="视频" value="视频" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="解码描述" prop="decodedDescription">
          <el-input
            v-model="formData.decodedDescription"
            type="textarea"
            :rows="3"
            placeholder="请输入解码后的完整描述"
          />
        </el-form-item>
        <el-form-item label="数据内容" prop="dataContent">
          <el-input
            v-model="formData.contentStr"
            type="textarea"
            :rows="6"
            placeholder="请输入 JSON 格式的数据内容"
          />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="formData.status" placeholder="请选择状态" style="width: 100%">
                <el-option label="已删除" :value="0" />
                <el-option label="正常" :value="1" />
                <el-option label="已处理" :value="2" />
                <el-option label="处理失败" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="处理状态" prop="processed">
              <el-switch v-model="formData.processed" />
            </el-form-item>
          </el-col>
        </el-row>
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
        <el-descriptions-item label="灾情类别">
          {{ currentDetail.disasterCategory || currentDetail.dataType || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="数据来源">{{ currentDetail.sourceSystem || currentDetail.source || '-' }}</el-descriptions-item>
        <el-descriptions-item label="处理状态">
          <el-tag :type="currentDetail.processed ? 'success' : 'warning'">
            {{ currentDetail.processed ? '已处理' : '未处理' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="采集时间">{{ formatTime(currentDetail.createTime || currentDetail.collectTime) }}</el-descriptions-item>
        <el-descriptions-item label="数据内容" :span="2">
          <pre class="json-content">{{ formatJson(currentDetail.content || currentDetail.dataContent) }}</pre>
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

// 使用真实 API
const api = rawDataAPI

// 灾情类别选项（与地图API保持一致）
const disasterCategories = ['震情', '人员伤亡及失踪', '房屋破坏', '生命线工程灾情', '次生灾害']

// 搜索表单
const searchForm = reactive({
  disasterCategory: '',
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

// 新增/编辑弹窗
const dialogVisible = ref(false)
const dialogType = ref('add') // 'add' 或 'edit'
const editingId = ref(null) // 编辑时的ID
const formRef = ref(null)
const submitLoading = ref(false)
const formData = reactive({
  dataType: '',
  disasterCategory: '',
  disasterSubcategory: '',
  disasterIndicator: '',
  geoCode: '',
  disasterId: '',
  disasterDateTime: null,
  sourceSystem: '',
  sourceCategory: '',
  carrierType: '',
  decodedDescription: '',
  dataContent: '',
  contentStr: '',
  status: 1,
  processed: false
})

// 详情弹窗
const detailVisible = ref(false)
const currentDetail = ref({})

// 表单验证规则
const formRules = {
  dataType: [{ required: false, message: '请输入数据类型', trigger: 'blur' }],
  disasterCategory: [{ required: false, message: '请选择灾情类别', trigger: 'change' }],
  sourceSystem: [{ required: false, message: '请输入来源系统', trigger: 'blur' }]
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    let res
    // 如果选择了灾情类别，使用专门的API接口
    if (searchForm.disasterCategory) {
      res = await api.getByDisasterCategory(searchForm.disasterCategory)
    } else {
      res = await api.getAll()
    }
    
    if (res.data.code === 200) {
      let data = res.data.data || []
      // 前端筛选（如果使用getAll接口，需要再次筛选）
      // 如果使用getByDisasterCategory接口，数据已经按类别筛选过了
      if (searchForm.processed !== '') {
        data = data.filter(item => item.processed === searchForm.processed)
      }
      if (searchForm.timeRange && searchForm.timeRange.length === 2) {
        const [start, end] = searchForm.timeRange
        data = data.filter(item => {
          if (!item.createTime) return false
          // 处理LocalDateTime格式（可能包含T）
          const timeStr = String(item.createTime).replace('T', ' ').split(' ')[0]
          return timeStr >= start && timeStr <= end
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
  searchForm.disasterCategory = ''
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
  dialogType.value = 'add'
  editingId.value = null
  Object.assign(formData, {
    dataType: '',
    disasterCategory: '',
    disasterSubcategory: '',
    disasterIndicator: '',
    geoCode: '',
    disasterId: '',
    disasterDateTime: null,
    sourceSystem: '',
    sourceCategory: '',
    carrierType: '',
    decodedDescription: '',
    dataContent: '',
    contentStr: '{\n  \n}',
    status: 1,
    processed: false
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  dialogType.value = 'edit'
  editingId.value = row.id
  Object.assign(formData, {
    dataType: row.dataType || '',
    disasterCategory: row.disasterCategory || '',
    disasterSubcategory: row.disasterSubcategory || '',
    disasterIndicator: row.disasterIndicator || '',
    geoCode: row.geoCode || '',
    disasterId: row.disasterId || '',
    disasterDateTime: row.disasterDateTime || null,
    sourceSystem: row.sourceSystem || '',
    sourceCategory: row.sourceCategory || '',
    carrierType: row.carrierType || '',
    decodedDescription: row.decodedDescription || '',
    dataContent: row.dataContent || '',
    contentStr: row.dataContent || (typeof row.dataContent === 'string' ? row.dataContent : JSON.stringify(row.dataContent || {}, null, 2)),
    status: row.status !== undefined ? row.status : 1,
    processed: row.processed !== undefined ? row.processed : false
  })
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
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    
    // 验证JSON格式（可选，如果不是JSON也可以保存为文本）
    let content
    try {
      content = JSON.parse(formData.contentStr)
    } catch (e) {
      // 如果不是JSON，直接使用字符串
      content = formData.contentStr
    }

    submitLoading.value = true
    
    const submitData = {
      dataType: formData.dataType,
      disasterCategory: formData.disasterCategory,
      disasterSubcategory: formData.disasterSubcategory,
      disasterIndicator: formData.disasterIndicator,
      geoCode: formData.geoCode,
      disasterId: formData.disasterId,
      disasterDateTime: formData.disasterDateTime,
      sourceSystem: formData.sourceSystem,
      sourceCategory: formData.sourceCategory,
      carrierType: formData.carrierType,
      decodedDescription: formData.decodedDescription,
      dataContent: formData.contentStr,
      status: formData.status,
      processed: formData.processed
    }

    if (dialogType.value === 'edit') {
      // 编辑模式
      await api.update(editingId.value, submitData)
      ElMessage.success('更新成功')
    } else {
      // 新增模式
      await api.create(submitData)
      ElMessage.success('新增成功')
    }
    
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error(error.response?.data?.message || '操作失败')
  } finally {
    submitLoading.value = false
  }
}

// 获取灾情类别标签类型（颜色）
const getCategoryTagType = (category) => {
  const categoryMap = {
    '震情': 'danger',
    '人员伤亡及失踪': 'warning',
    '房屋破坏': 'primary',
    '生命线工程灾情': 'success',
    '次生灾害': 'info'
  }
  return categoryMap[category] || ''
}

// 获取载体类型标签类型
const getCarrierTagType = (carrier) => {
  const types = {
    '文字': 'primary',
    '图像': 'success',
    '音频': 'warning',
    '视频': 'danger'
  }
  return types[carrier] || 'info'
}

// 获取状态标签类型
const getStatusTagType = (status) => {
  const types = {
    0: 'danger',   // 已删除
    1: 'success',  // 正常
    2: 'info',     // 已处理
    3: 'warning'   // 处理失败
  }
  return types[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    0: '已删除',
    1: '正常',
    2: '已处理',
    3: '处理失败'
  }
  return texts[status] || '未知'
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return '-'
  if (typeof time === 'string') {
    return time
  }
  // 如果是Date对象或其他格式，可以在这里转换
  return String(time)
}

// 格式化 JSON
const formatJson = (obj) => {
  if (!obj) return '-'
  // 如果是字符串，尝试解析为JSON
  if (typeof obj === 'string') {
    try {
      const parsed = JSON.parse(obj)
      return JSON.stringify(parsed, null, 2)
    } catch (e) {
      return obj
    }
  }
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
