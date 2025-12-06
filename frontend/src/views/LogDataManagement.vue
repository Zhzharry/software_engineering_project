<template>
  <div>
    <div class="card">
      <div class="card-header">
        <h2>日志数据管理</h2>
        <button class="btn btn-primary" @click="showCreateModal">新增日志</button>
      </div>

      <div style="display: flex; gap: 10px; margin-bottom: 20px;">
        <select v-model="filterLevel" @change="filterByLevel" class="btn btn-secondary" style="padding: 10px;">
          <option value="">全部级别</option>
          <option value="INFO">INFO</option>
          <option value="WARN">WARN</option>
          <option value="ERROR">ERROR</option>
          <option value="DEBUG">DEBUG</option>
        </select>
      </div>

      <div v-if="message" :class="['alert', messageType === 'success' ? 'alert-success' : 'alert-error']">
        {{ message }}
      </div>

      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="dataList.length === 0" class="empty">暂无日志数据</div>
      <table v-else class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>级别</th>
            <th>消息</th>
            <th>时间戳</th>
            <th>元数据</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in dataList" :key="item.id" :class="getLevelClass(item.level)">
            <td>{{ item.id.substring(0, 8) }}...</td>
            <td>
              <span :class="['badge', `badge-${item.level?.toLowerCase()}`]">
                {{ item.level }}
              </span>
            </td>
            <td>{{ item.message }}</td>
            <td>{{ formatDate(item.timestamp || item.createTime) }}</td>
            <td>{{ truncate(JSON.stringify(item.metadata || {}), 30) }}</td>
            <td>
              <button class="btn btn-danger btn-sm" @click="deleteLog(item.id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 创建模态框 -->
    <div v-if="showModal" class="modal" @click.self="closeModal">
      <div class="modal-content">
        <div class="modal-header">
          <h2>新增日志数据</h2>
          <button class="close-btn" @click="closeModal">&times;</button>
        </div>
        <form @submit.prevent="saveLog">
          <div class="form-group">
            <label>级别 *</label>
            <select v-model="form.level" required>
              <option value="INFO">INFO</option>
              <option value="WARN">WARN</option>
              <option value="ERROR">ERROR</option>
              <option value="DEBUG">DEBUG</option>
            </select>
          </div>
          <div class="form-group">
            <label>消息 *</label>
            <textarea v-model="form.message" rows="3" required></textarea>
          </div>
          <div class="form-group">
            <label>元数据 (JSON格式)</label>
            <textarea v-model="form.metadataStr" rows="3" placeholder='{"key": "value"}'></textarea>
          </div>
          <div style="display: flex; justify-content: flex-end; gap: 10px; margin-top: 20px;">
            <button type="button" class="btn btn-secondary" @click="closeModal">取消</button>
            <button type="submit" class="btn btn-primary">保存</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { logDataAPI } from '../api'

const dataList = ref([])
const loading = ref(false)
const showModal = ref(false)
const filterLevel = ref('')
const message = ref('')
const messageType = ref('success')

const form = ref({
  level: 'INFO',
  message: '',
  metadataStr: ''
})

const loadAll = async () => {
  loading.value = true
  try {
    const res = await logDataAPI.getAll()
    if (res.data.code === 200) {
      dataList.value = res.data.data || []
    }
  } catch (error) {
    showMessage('加载日志数据失败', 'error')
  } finally {
    loading.value = false
  }
}

const filterByLevel = async () => {
  if (!filterLevel.value) {
    loadAll()
    return
  }
  loading.value = true
  try {
    const res = await logDataAPI.getByLevel(filterLevel.value)
    if (res.data.code === 200) {
      dataList.value = res.data.data || []
    }
  } catch (error) {
    showMessage('筛选失败', 'error')
  } finally {
    loading.value = false
  }
}

const showCreateModal = () => {
  form.value = { level: 'INFO', message: '', metadataStr: '' }
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
}

const saveLog = async () => {
  try {
    const logData = {
      level: form.value.level,
      message: form.value.message,
      timestamp: new Date().toISOString()
    }
    if (form.value.metadataStr) {
      try {
        logData.metadata = JSON.parse(form.value.metadataStr)
      } catch (e) {
        showMessage('元数据JSON格式错误', 'error')
        return
      }
    }
    await logDataAPI.create(logData)
    showMessage('日志创建成功', 'success')
    closeModal()
    loadAll()
  } catch (error) {
    showMessage(error.response?.data?.message || '操作失败', 'error')
  }
}

const deleteLog = async (id) => {
  if (!confirm('确定要删除这条日志吗？')) return
  try {
    await logDataAPI.delete(id)
    showMessage('日志删除成功', 'success')
    loadAll()
  } catch (error) {
    showMessage('删除失败', 'error')
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

const truncate = (str, len) => {
  if (!str) return '-'
  return str.length > len ? str.substring(0, len) + '...' : str
}

const getLevelClass = (level) => {
  const levelMap = {
    ERROR: 'row-error',
    WARN: 'row-warn',
    INFO: 'row-info',
    DEBUG: 'row-debug'
  }
  return levelMap[level] || ''
}

const showMessage = (msg, type = 'success') => {
  message.value = msg
  messageType.value = type
  setTimeout(() => {
    message.value = ''
  }, 3000)
}

onMounted(() => {
  loadAll()
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

.badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
}

.badge-info {
  background: #bee3f8;
  color: #2c5282;
}

.badge-warn {
  background: #faf089;
  color: #744210;
}

.badge-error {
  background: #fed7d7;
  color: #742a2a;
}

.badge-debug {
  background: #e9d8fd;
  color: #553c9a;
}

.row-error {
  background: #fff5f5;
}

.row-warn {
  background: #fffaf0;
}

.row-info {
  background: #ebf8ff;
}

.row-debug {
  background: #faf5ff;
}
</style>

