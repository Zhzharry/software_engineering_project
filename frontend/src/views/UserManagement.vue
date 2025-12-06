<template>
  <div>
    <div class="card">
      <div class="card-header">
        <h2>用户管理</h2>
        <button class="btn btn-primary" @click="showCreateModal">新增用户</button>
      </div>

      <div v-if="message" :class="['alert', messageType === 'success' ? 'alert-success' : 'alert-error']">
        {{ message }}
      </div>

      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="users.length === 0" class="empty">暂无用户数据</div>
      <table v-else class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>用户名</th>
            <th>邮箱</th>
            <th>手机号</th>
            <th>状态</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in users" :key="user.id">
            <td>{{ user.id }}</td>
            <td>{{ user.username }}</td>
            <td>{{ user.email || '-' }}</td>
            <td>{{ user.phone || '-' }}</td>
            <td>
              <span :class="user.status === 1 ? 'badge-success' : 'badge-danger'">
                {{ user.status === 1 ? '启用' : '禁用' }}
              </span>
            </td>
            <td>{{ formatDate(user.createTime) }}</td>
            <td>
              <button class="btn btn-secondary btn-sm" @click="editUser(user)">编辑</button>
              <button class="btn btn-danger btn-sm" @click="deleteUser(user.id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 创建/编辑模态框 -->
    <div v-if="showModal" class="modal" @click.self="closeModal">
      <div class="modal-content">
        <div class="modal-header">
          <h2>{{ editingUser ? '编辑用户' : '新增用户' }}</h2>
          <button class="close-btn" @click="closeModal">&times;</button>
        </div>
        <form @submit.prevent="saveUser">
          <div class="form-group">
            <label>用户名 *</label>
            <input v-model="form.username" type="text" required />
          </div>
          <div class="form-group">
            <label>邮箱</label>
            <input v-model="form.email" type="email" />
          </div>
          <div class="form-group">
            <label>手机号</label>
            <input v-model="form.phone" type="tel" />
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
import { userAPI } from '../api'

const users = ref([])
const loading = ref(false)
const showModal = ref(false)
const editingUser = ref(null)
const message = ref('')
const messageType = ref('success')

const form = ref({
  username: '',
  email: '',
  phone: '',
  status: 1
})

const loadUsers = async () => {
  loading.value = true
  try {
    const res = await userAPI.getAll()
    if (res.data.code === 200) {
      users.value = res.data.data || []
    }
  } catch (error) {
    showMessage('加载用户列表失败', 'error')
  } finally {
    loading.value = false
  }
}

const showCreateModal = () => {
  editingUser.value = null
  form.value = { username: '', email: '', phone: '', status: 1 }
  showModal.value = true
}

const editUser = (user) => {
  editingUser.value = user
  form.value = { ...user }
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
  editingUser.value = null
}

const saveUser = async () => {
  try {
    if (editingUser.value) {
      await userAPI.update(editingUser.value.id, form.value)
      showMessage('用户更新成功', 'success')
    } else {
      await userAPI.create(form.value)
      showMessage('用户创建成功', 'success')
    }
    closeModal()
    loadUsers()
  } catch (error) {
    showMessage(error.response?.data?.message || '操作失败', 'error')
  }
}

const deleteUser = async (id) => {
  if (!confirm('确定要删除这个用户吗？')) return
  try {
    await userAPI.delete(id)
    showMessage('用户删除成功', 'success')
    loadUsers()
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
  loadUsers()
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

