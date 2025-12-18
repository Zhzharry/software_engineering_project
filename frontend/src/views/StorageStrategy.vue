<template>
  <div class="storage-strategy">
    <!-- 工具栏 -->
    <el-card shadow="never" class="toolbar-card">
      <div class="toolbar">
        <el-button type="primary" @click="showCreateDialog">
          <el-icon><Plus /></el-icon> 新建策略
        </el-button>
        <el-button @click="loadData">
          <el-icon><Refresh /></el-icon> 刷新
        </el-button>
      </div>
    </el-card>

    <!-- 策略列表 -->
    <el-card shadow="never" class="table-card" v-loading="loading">
      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="strategyName" label="策略名称" min-width="180" />
        <el-table-column prop="dataType" label="数据类型" width="120">
          <template #default="{ row }">
            <el-tag size="small">{{ row.dataType || '全部' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="storageType" label="存储类型" width="100" />
        <el-table-column prop="retentionDays" label="保留天数" width="100">
          <template #default="{ row }">
            {{ row.retentionDays ? row.retentionDays + '天' : '永久' }}
          </template>
        </el-table-column>
        <el-table-column prop="maxSize" label="最大容量" width="120">
          <template #default="{ row }">
            {{ formatSize(row.maxSize) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="showEditDialog(row)">
              <el-icon><Edit /></el-icon> 编辑
            </el-button>
            <el-popconfirm title="确定删除此策略？" @confirm="handleDelete(row)">
              <template #reference>
                <el-button type="danger" link>
                  <el-icon><Delete /></el-icon> 删除
                </el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新建/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑策略' : '新建策略'" width="600px">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="策略名称" prop="strategyName">
          <el-input v-model="formData.strategyName" placeholder="请输入策略名称" />
        </el-form-item>
        <el-form-item label="数据类型" prop="dataType">
          <el-select v-model="formData.dataType" placeholder="选择数据类型（可选）" clearable style="width: 100%">
            <el-option label="sensor" value="sensor" />
            <el-option label="weather" value="weather" />
            <el-option label="earthquake" value="earthquake" />
            <el-option label="flood" value="flood" />
            <el-option label="fire" value="fire" />
          </el-select>
        </el-form-item>
        <el-form-item label="存储类型" prop="storageType">
          <el-select v-model="formData.storageType" placeholder="选择存储类型" style="width: 100%">
            <el-option label="MongoDB" value="mongodb" />
            <el-option label="MySQL" value="mysql" />
          </el-select>
        </el-form-item>
        <el-form-item label="保留天数" prop="retentionDays">
          <el-input-number v-model="formData.retentionDays" :min="1" :max="3650" placeholder="留空表示永久保留" style="width: 100%" />
        </el-form-item>
        <el-form-item label="最大容量" prop="maxSize">
          <el-input v-model.number="formData.maxSize" placeholder="字节数，如 1073741824 表示 1GB">
            <template #append>字节</template>
          </el-input>
        </el-form-item>
        <el-form-item label="清理规则" prop="cleanupRule">
          <el-input
            v-model="formData.cleanupRule"
            type="textarea"
            :rows="3"
            placeholder='JSON格式，如: {"autoClean": true, "cleanupTime": "02:00"}'
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
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { storageStrategyAPI } from '../api'
import { ElMessage } from 'element-plus'

// 数据
const tableData = ref([])
const loading = ref(false)

// 对话框
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const submitLoading = ref(false)

const formData = reactive({
  id: null,
  strategyName: '',
  dataType: '',
  storageType: 'mongodb',
  retentionDays: null,
  maxSize: null,
  cleanupRule: '',
  status: 1
})

const formRules = {
  strategyName: [{ required: true, message: '请输入策略名称', trigger: 'blur' }]
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await storageStrategyAPI.getAll()
    if (res.data?.code === 200) {
      tableData.value = res.data.data || []
    }
  } catch (error) {
    console.error('加载策略失败:', error)
    ElMessage.error('加载策略失败')
  } finally {
    loading.value = false
  }
}

// 显示新建对话框
const showCreateDialog = () => {
  isEdit.value = false
  Object.assign(formData, {
    id: null,
    strategyName: '',
    dataType: '',
    storageType: 'mongodb',
    retentionDays: null,
    maxSize: null,
    cleanupRule: '',
    status: 1
  })
  dialogVisible.value = true
}

// 显示编辑对话框
const showEditDialog = (row) => {
  isEdit.value = true
  Object.assign(formData, row)
  dialogVisible.value = true
}

// 提交
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitLoading.value = true

    const data = { ...formData }
    let res
    if (isEdit.value) {
      res = await storageStrategyAPI.update(data.id, data)
    } else {
      res = await storageStrategyAPI.create(data)
    }

    if (res.data?.code === 200) {
      ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
      dialogVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.data?.message || '操作失败')
    }
  } catch (error) {
    console.error('提交失败:', error)
  } finally {
    submitLoading.value = false
  }
}

// 状态切换
const handleStatusChange = async (row) => {
  try {
    await storageStrategyAPI.update(row.id, { status: row.status })
    ElMessage.success('状态更新成功')
  } catch (error) {
    console.error('状态更新失败:', error)
    row.status = row.status === 1 ? 0 : 1
    ElMessage.error('状态更新失败')
  }
}

// 删除
const handleDelete = async (row) => {
  try {
    const res = await storageStrategyAPI.delete(row.id)
    if (res.data?.code === 200) {
      ElMessage.success('删除成功')
      loadData()
    } else {
      ElMessage.error(res.data?.message || '删除失败')
    }
  } catch (error) {
    console.error('删除失败:', error)
    ElMessage.error('删除失败')
  }
}

// 工具函数
const formatSize = (bytes) => {
  if (!bytes) return '不限'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.storage-strategy {
  padding: 20px;
}

.toolbar-card {
  margin-bottom: 16px;
}

.toolbar {
  display: flex;
  gap: 10px;
}

.table-card {
  min-height: 400px;
}
</style>
