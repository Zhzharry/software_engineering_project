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

    <!-- 创建/编辑模态框 -->
    <div v-if="showModal" class="modal" @click.self="closeModal">
      <div class="modal-content">
        <div class="modal-header">
          <h2>{{ editingModule ? '编辑模块' : '新增模块' }}</h2>
          <button class="close-btn" @click="closeModal">&times;</button>
        </div>
        <form @submit.prevent="saveModule">
          <div class="form-group">
            <label>模块名称 *</label>
            <input v-model="form.name" type="text" required />
          </div>
          <div class="form-group">
            <label>描述</label>
            <textarea v-model="form.description" rows="3"></textarea>
          </div>
          <div class="form-group">
            <label>状态</label>
            <select v-model="form.status">
              <option :value="1">启用</option>
              <option :value="0">禁用</option>
            </select>
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
import { moduleAPI } from '../api'

const modules = ref([])
const loading = ref(false)
const showModal = ref(false)
const editingModule = ref(null)
const message = ref('')
const messageType = ref('success')

const form = ref({
  name: '',
  description: '',
  status: 1
})

const loadModules = async () => {
  loading.value = true
  try {
    const res = await moduleAPI.getAll()
    if (res.data.code === 200) {
      modules.value = res.data.data?.content || []
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

const editModule = (module) => {
  editingModule.value = module
  form.value = { ...module }
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
  editingModule.value = null
}

const saveModule = async () => {
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

