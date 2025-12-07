<template>
  <div>
    <div class="card">
      <div class="card-header">
        <h2>模块管理</h2>
        <button class="btn btn-primary" @click="showCreateModal">新增模块</button>
      </div>

      <div v-if="message" :class="['alert', messageType === 'success' ? 'alert-success' : 'alert-error']">
        {{ message }}
      </div>

      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="modules.length === 0" class="empty">暂无模块数据</div>
      <table v-else class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>模块名称</th>
            <th>描述</th>
            <th>状态</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="module in modules" :key="module.id">
            <td>{{ module.id }}</td>
            <td>{{ module.name }}</td>
            <td>{{ module.description || '-' }}</td>
            <td>
              <span :class="module.status === 1 ? 'badge-success' : 'badge-danger'">
                {{ module.status === 1 ? '启用' : '禁用' }}
              </span>
            </td>
            <td>{{ formatDate(module.createTime) }}</td>
            <td>
              <button class="btn btn-secondary btn-sm" @click="editModule(module)">编辑</button>
              <button class="btn btn-danger btn-sm" @click="deleteModule(module.id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="模块名称">
          <el-input v-model="searchForm.name" placeholder="请输入模块名称" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 150px">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
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
          <span>模块列表</span>
          <div class="header-actions">
            <el-button type="primary" :icon="Plus" @click="handleAdd">新增模块</el-button>
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
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="name" label="模块名称" min-width="150" />
        <el-table-column prop="description" label="描述" min-width="250" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button
              type="primary"
              link
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-popconfirm title="确定要删除该模块吗？" @confirm="handleDelete(row.id)">
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
      :title="dialogType === 'add' ? '新增模块' : '编辑模块'"
      width="550px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="模块名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入模块名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入模块描述"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { moduleAPI } from '../api'

// 获取 API
const api = USE_MOCK ? mockModuleAPI : moduleAPI

// 搜索表单
const searchForm = reactive({
  name: '',
  status: ''
})

// 表格数据
const tableData = ref([])
const loading = ref(false)
const showModal = ref(false)
const editingModule = ref(null)
const message = ref('')
const messageType = ref('success')

// 分页
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 弹窗相关
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)
const submitLoading = ref(false)
const formData = reactive({
  name: '',
  description: '',
  status: 1
})

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入模块名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  description: [
    { max: 500, message: '描述不能超过 500 个字符', trigger: 'blur' }
  ]
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await moduleAPI.getAll()
    if (res.data.code === 200) {
      let data = res.data.data?.content || res.data.data || []
      // 前端筛选
      if (searchForm.name) {
        data = data.filter(item => item.name.includes(searchForm.name))
      }
      if (searchForm.status !== '') {
        data = data.filter(item => item.status === searchForm.status)
      }
      pagination.total = data.length
      // 前端分页
      const start = (pagination.page - 1) * pagination.size
      tableData.value = data.slice(start, start + pagination.size)
    }
  } catch (error) {
    showMessage('加载模块列表失败', 'error')
  } finally {
    loading.value = false
  }
}

const showCreateModal = () => {
  editingModule.value = null
  form.value = { name: '', description: '', status: 1 }
  showModal.value = true
}

// 重置
const handleReset = () => {
  searchForm.name = ''
  searchForm.status = ''
  pagination.page = 1
  loadData()
}

const closeModal = () => {
  showModal.value = false
  editingModule.value = null
}

// 新增
const handleAdd = () => {
  dialogType.value = 'add'
  Object.assign(formData, { name: '', description: '', status: 1 })
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  dialogType.value = 'edit'
  Object.assign(formData, { ...row })
  dialogVisible.value = true
}

// 切换状态
const handleToggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const statusText = newStatus === 1 ? '启用' : '禁用'
  try {
    if (editingModule.value) {
      await moduleAPI.update(editingModule.value.id, form.value)
      showMessage('模块更新成功', 'success')
    } else {
      await moduleAPI.create(form.value)
      showMessage('模块创建成功', 'success')
    }
    closeModal()
    loadModules()
  } catch (error) {
    showMessage(error.response?.data?.message || '操作失败', 'error')
  }
}

const deleteModule = async (id) => {
  if (!confirm('确定要删除这个模块吗？')) return
  try {
    await moduleAPI.delete(id)
    showMessage('模块删除成功', 'success')
    loadModules()
  } catch (error) {
    showMessage('删除失败', 'error')
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

const showMessage = (msg, type = 'success') => {
  message.value = msg
  messageType.value = type
  setTimeout(() => {
    message.value = ''
  }, 3000)
}

onMounted(() => {
  loadModules()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.card-header h2 {
  margin: 0;
  color: #2d3748;
}

.btn-sm {
  padding: 6px 12px;
  font-size: 12px;
}

.badge-success {
  padding: 4px 8px;
  background: #c6f6d5;
  color: #22543d;
  border-radius: 4px;
  font-size: 12px;
}

.badge-danger {
  padding: 4px 8px;
  background: #fed7d7;
  color: #742a2a;
  border-radius: 4px;
  font-size: 12px;
}
</style>

