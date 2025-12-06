import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json'
  }
})

// 用户相关API
export const userAPI = {
  getAll: () => api.get('/users'),
  getById: (id) => api.get(`/users/${id}`),
  getByUsername: (username) => api.get(`/users/username/${username}`),
  getPage: (page = 0, size = 10, sort = 'id') => api.get(`/users/page?page=${page}&size=${size}&sort=${sort}`),
  create: (user) => api.post('/users', user),
  update: (id, user) => api.put(`/users/${id}`, user),
  delete: (id) => api.delete(`/users/${id}`),
  checkUsername: (username) => api.get(`/users/check-username?username=${username}`)
}

// 模块相关API
export const moduleAPI = {
  getAll: (page = 0, size = 10, sort = 'id') => api.get(`/modules?page=${page}&size=${size}&sort=${sort}`),
  getById: (id) => api.get(`/modules/${id}`),
  getByStatus: (status) => api.get(`/modules/status/${status}`),
  create: (module) => api.post('/modules', module),
  update: (id, module) => api.put(`/modules/${id}`, module),
  delete: (id) => api.delete(`/modules/${id}`),
  getRawData: (moduleId) => api.get(`/modules/${moduleId}/raw-data`),
  getCombined: (moduleId) => api.get(`/modules/${moduleId}/combined`)
}

// 原始数据相关API
export const rawDataAPI = {
  getAll: () => api.get('/raw-data'),
  getById: (id) => api.get(`/raw-data/${id}`),
  getByType: (type) => api.get(`/raw-data/type/${type}`),
  getUnprocessed: () => api.get('/raw-data/unprocessed'),
  getTimeRange: (start, end) => api.get(`/raw-data/time-range?start=${start}&end=${end}`),
  getCountByType: (type) => api.get(`/raw-data/count/${type}`),
  create: (data) => api.post('/raw-data', data),
  markAsProcessed: (id) => api.put(`/raw-data/${id}/processed`)
}

// 日志数据相关API
export const logDataAPI = {
  getAll: () => api.get('/log-data'),
  getById: (id) => api.get(`/log-data/${id}`),
  getPage: (page = 0, size = 10) => api.get(`/log-data/page?page=${page}&size=${size}`),
  getByLevel: (level) => api.get(`/log-data/level/${level}`),
  getTimeRange: (start, end) => api.get(`/log-data/time-range?start=${start}&end=${end}`),
  getCountByLevel: (level) => api.get(`/log-data/count/level/${level}`),
  create: (data) => api.post('/log-data', data),
  delete: (id) => api.delete(`/log-data/${id}`)
}

// 处理数据相关API
export const processedDataAPI = {
  getAll: () => api.get('/processed-data'),
  getById: (id) => api.get(`/processed-data/${id}`),
  getByType: (type) => api.get(`/processed-data/type/${type}`),
  getByRawDataId: (rawDataId) => api.get(`/processed-data/raw-data/${rawDataId}`),
  getByMinConfidence: (minScore) => api.get(`/processed-data/confidence/min/${minScore}`),
  getByConfidenceRange: (minScore, maxScore) => api.get(`/processed-data/confidence/range?minScore=${minScore}&maxScore=${maxScore}`),
  searchByField: (field, value) => api.get(`/processed-data/search/field?field=${field}&value=${value}`),
  create: (data) => api.post('/processed-data', data),
  updateConfidence: (id, score) => api.put(`/processed-data/${id}/confidence?confidenceScore=${score}`),
  delete: (id) => api.delete(`/processed-data/${id}`)
}

export default api

