<template>
  <div>
    <div class="card">
      <div class="card-header">
        <h2>原始数据管理</h2>
        <button class="btn btn-primary" @click="showCreateModal">新增数据</button>
      </div>

      <div style="display: flex; gap: 10px; margin-bottom: 20px;">
        <button class="btn btn-secondary" @click="loadUnprocessed">未处理数据</button>
        <button class="btn btn-secondary" @click="loadAll">全部数据</button>
      </div>

      <div v-if="message" :class="['alert', messageType === 'success' ? 'alert-success' : 'alert-error']">
        {{ message }}
      </div>

      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="dataList.length === 0" class="empty">暂无数据</div>
      <table v-else class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>模块ID</th>
            <th>数据类型</th>
            <th>数据内容</th>
            <th>来源</th>
            <th>状态</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in dataList" :key="item.id">
            <td>{{ item.id.substring(0, 8) }}...</td>
            <td>{{ item.moduleId || '-' }}</td>
            <td>{{ item.dataType || '-' }}</td>
            <td>{{ truncate(item.dataContent || JSON.stringify(item.content), 30) }}</td>
            <td>{{ item.source || '-' }}</td>
            <td>
              <span :class="item.processed ? 'badge-success' : 'badge-danger'">
                {{ item.processed ? '已处理' : '未处理' }}
              </span>
            </td>
            <td>{{ formatDate(item.createTime) }}</td>
            <td>
              <button v-if="!item.processed" class="btn btn-success btn-sm" @click="markAsProcessed(item.id)">标记已处理</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 创建模态框 -->
    <div v-if="showModal" class="modal" @click.self="closeModal">
      <div class="modal-content">
        <div class="modal-header">
          <h2>新增原始数据</h2>
          <button class="close-btn" @click="closeModal">&times;</button>
        </div>
        <form @submit.prevent="saveData">
          <div class="form-group">
            <label>模块ID</label>
            <input v-model.number="form.moduleId" type="number" />
          </div>
          <div class="form-group">
            <label>数据类型</label>
            <input v-model="form.dataType" type="text" />
          </div>
          <div class="form-group">
            <label>数据内容</label>
            <textarea v-model="form.dataContent" rows="3"></textarea>
          </div>
          <div class="form-group">
            <label>来源</label>
            <input v-model="form.source" type="text" />
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
import { rawDataAPI } from '../api'

const dataList = ref([])
const loading = ref(false)
const showModal = ref(false)
const message = ref('')
const messageType = ref('success')

const form = ref({
  moduleId: null,
  dataType: '',
  dataContent: '',
  source: '',
  processed: false
})

const loadAll = async () => {
  loading.value = true
  try {
    const res = await rawDataAPI.getAll()
    if (res.data.code === 200) {
      dataList.value = res.data.data || []
    }
  } catch (error) {
    showMessage('加载数据失败', 'error')
  } finally {
    loading.value = false
  }
}

const loadUnprocessed = async () => {
  loading.value = true
  try {
    const res = await rawDataAPI.getUnprocessed()
    if (res.data.code === 200) {
      dataList.value = res.data.data || []
    }
  } catch (error) {
    showMessage('加载未处理数据失败', 'error')
  } finally {
    loading.value = false
  }
}

const showCreateModal = () => {
  form.value = { moduleId: null, dataType: '', dataContent: '', source: '', processed: false }
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
}

const saveData = async () => {
  try {
    await rawDataAPI.create(form.value)
    showMessage('数据创建成功', 'success')
    closeModal()
    loadAll()
  } catch (error) {
    showMessage(error.response?.data?.message || '操作失败', 'error')
  }
}

const markAsProcessed = async (id) => {
  try {
    await rawDataAPI.markAsProcessed(id)
    showMessage('标记成功', 'success')
    loadAll()
  } catch (error) {
    showMessage('标记失败', 'error')
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

.badge-danger {
  padding: 4px 8px;
  background: #fed7d7;
  color: #742a2a;
  border-radius: 4px;
  font-size: 12px;
}
</style>

