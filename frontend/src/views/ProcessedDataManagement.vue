<template>
  <div>
    <div class="card">
      <div class="card-header">
        <h2>处理数据管理</h2>
        <button class="btn btn-primary" @click="showCreateModal">新增处理数据</button>
      </div>

      <div v-if="message" :class="['alert', messageType === 'success' ? 'alert-success' : 'alert-error']">
        {{ message }}
      </div>

      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="dataList.length === 0" class="empty">暂无处理数据</div>
      <table v-else class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>原始数据ID</th>
            <th>处理类型</th>
            <th>处理内容</th>
            <th>置信度</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in dataList" :key="item.id">
            <td>{{ item.id.substring(0, 8) }}...</td>
            <td>{{ item.rawDataId || '-' }}</td>
            <td>{{ item.processType || '-' }}</td>
            <td>{{ truncate(JSON.stringify(item.processedContent || {}), 30) }}</td>
            <td>
              <span :class="getConfidenceClass(item.confidenceScore)">
                {{ item.confidenceScore || '-' }}
              </span>
            </td>
            <td>{{ formatDate(item.createTime) }}</td>
            <td>
              <button class="btn btn-secondary btn-sm" @click="updateConfidence(item)">更新置信度</button>
              <button class="btn btn-danger btn-sm" @click="deleteData(item.id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 创建模态框 -->
    <div v-if="showModal" class="modal" @click.self="closeModal">
      <div class="modal-content">
        <div class="modal-header">
          <h2>新增处理数据</h2>
          <button class="close-btn" @click="closeModal">&times;</button>
        </div>
        <form @submit.prevent="saveData">
          <div class="form-group">
            <label>原始数据ID</label>
            <input v-model.number="form.rawDataId" type="number" />
          </div>
          <div class="form-group">
            <label>处理类型</label>
            <input v-model="form.processType" type="text" />
          </div>
          <div class="form-group">
            <label>置信度 (0-1)</label>
            <input v-model.number="form.confidenceScore" type="number" step="0.01" min="0" max="1" />
          </div>
          <div class="form-group">
            <label>处理内容 (JSON格式)</label>
            <textarea v-model="form.processedContentStr" rows="4" placeholder='{"result": "处理完成"}'></textarea>
          </div>
          <div style="display: flex; justify-content: flex-end; gap: 10px; margin-top: 20px;">
            <button type="button" class="btn btn-secondary" @click="closeModal">取消</button>
            <button type="submit" class="btn btn-primary">保存</button>
          </div>
        </form>
      </div>
    </div>

    <!-- 更新置信度模态框 -->
    <div v-if="showConfidenceModal" class="modal" @click.self="closeConfidenceModal">
      <div class="modal-content">
        <div class="modal-header">
          <h2>更新置信度</h2>
          <button class="close-btn" @click="closeConfidenceModal">&times;</button>
        </div>
        <form @submit.prevent="saveConfidence">
          <div class="form-group">
            <label>置信度 (0-1) *</label>
            <input v-model.number="confidenceForm.score" type="number" step="0.01" min="0" max="1" required />
          </div>
          <div style="display: flex; justify-content: flex-end; gap: 10px; margin-top: 20px;">
            <button type="button" class="btn btn-secondary" @click="closeConfidenceModal">取消</button>
            <button type="submit" class="btn btn-primary">保存</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { processedDataAPI } from '../api'

const dataList = ref([])
const loading = ref(false)
const showModal = ref(false)
const showConfidenceModal = ref(false)
const editingId = ref(null)
const message = ref('')
const messageType = ref('success')

const form = ref({
  rawDataId: null,
  processType: '',
  confidenceScore: 0.8,
  processedContentStr: ''
})

const confidenceForm = ref({
  score: 0.8
})

const loadAll = async () => {
  loading.value = true
  try {
    const res = await processedDataAPI.getAll()
    if (res.data.code === 200) {
      dataList.value = res.data.data || []
    }
  } catch (error) {
    showMessage('加载处理数据失败', 'error')
  } finally {
    loading.value = false
  }
}

const showCreateModal = () => {
  form.value = { rawDataId: null, processType: '', confidenceScore: 0.8, processedContentStr: '' }
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
}

const saveData = async () => {
  try {
    const processedData = {
      rawDataId: form.value.rawDataId,
      processType: form.value.processType,
      confidenceScore: form.value.confidenceScore
    }
    if (form.value.processedContentStr) {
      try {
        processedData.processedContent = JSON.parse(form.value.processedContentStr)
      } catch (e) {
        showMessage('处理内容JSON格式错误', 'error')
        return
      }
    }
    await processedDataAPI.create(processedData)
    showMessage('处理数据创建成功', 'success')
    closeModal()
    loadAll()
  } catch (error) {
    showMessage(error.response?.data?.message || '操作失败', 'error')
  }
}

const updateConfidence = (item) => {
  editingId.value = item.id
  confidenceForm.value.score = item.confidenceScore || 0.8
  showConfidenceModal.value = true
}

const closeConfidenceModal = () => {
  showConfidenceModal.value = false
  editingId.value = null
}

const saveConfidence = async () => {
  try {
    await processedDataAPI.updateConfidence(editingId.value, confidenceForm.value.score)
    showMessage('置信度更新成功', 'success')
    closeConfidenceModal()
    loadAll()
  } catch (error) {
    showMessage('更新失败', 'error')
  }
}

const deleteData = async (id) => {
  if (!confirm('确定要删除这条处理数据吗？')) return
  try {
    await processedDataAPI.delete(id)
    showMessage('删除成功', 'success')
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

const getConfidenceClass = (score) => {
  if (!score) return 'badge-gray'
  if (score >= 0.8) return 'badge-success'
  if (score >= 0.6) return 'badge-warn'
  return 'badge-danger'
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

.badge-success {
  padding: 4px 8px;
  background: #c6f6d5;
  color: #22543d;
  border-radius: 4px;
  font-size: 12px;
}

.badge-warn {
  padding: 4px 8px;
  background: #faf089;
  color: #744210;
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

.badge-gray {
  padding: 4px 8px;
  background: #e2e8f0;
  color: #4a5568;
  border-radius: 4px;
  font-size: 12px;
}
</style>

