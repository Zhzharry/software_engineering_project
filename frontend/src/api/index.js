import axios from 'axios'
import { getToken, clearAuth } from '../utils/auth'

const api = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器：添加token
api.interceptors.request.use(
  config => {
    const token = getToken()
    if (token) {
      config.headers.Authorization = token
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器：处理401未授权
api.interceptors.response.use(
  response => response,
  error => {
    if (error.response && error.response.status === 401) {
      // 清除本地认证信息
      clearAuth()
      // 跳转到登录页
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

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

// ========== 新增API ==========

// 文件管理API
export const fileAPI = {
  upload: (formData) => api.post('/files/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  }),
  getAll: () => api.get('/files'),
  getById: (id) => api.get(`/files/${id}`),
  getByType: (fileType) => api.get(`/files/type/${fileType}`),
  getByRelated: (relatedDataId, relatedDataType) => api.get(`/files/related?relatedDataId=${relatedDataId}&relatedDataType=${relatedDataType}`),
  getByUser: (userId) => api.get(`/files/user/${userId}`),
  getUrl: (id) => api.get(`/files/${id}/url`),
  download: (id) => api.get(`/files/${id}/download`, { responseType: 'blob' }),
  preview: (id) => `/api/files/${id}/preview`,
  access: (id) => `/api/files/${id}/access`,
  update: (id, params) => api.put(`/files/${id}`, null, { params }),
  delete: (id) => api.delete(`/files/${id}`),
  getStatistics: () => api.get('/files/statistics')
}

// Dashboard数据看板API
export const dashboardAPI = {
  getData: () => api.get('/dashboard'),
  getStatistics: () => api.get('/dashboard/statistics'),
  getFileStatistics: () => api.get('/dashboard/files'),
  getRecentActivities: () => api.get('/dashboard/activities'),
  getChartData: () => api.get('/dashboard/charts'),
  getDataTypeDistribution: () => api.get('/dashboard/charts/data-type-distribution'),
  getTrendChart: (days = 7) => api.get(`/dashboard/charts/trend?days=${days}`)
}

// 存储策略API
export const storageStrategyAPI = {
  getAll: () => api.get('/storage-strategy'),
  getById: (id) => api.get(`/storage-strategy/${id}`),
  getByDataType: (dataType) => api.get(`/storage-strategy/data-type/${dataType}`),
  getActive: () => api.get('/storage-strategy/active'),
  create: (strategy) => api.post('/storage-strategy', strategy),
  update: (id, strategy) => api.put(`/storage-strategy/${id}`, strategy),
  delete: (id) => api.delete(`/storage-strategy/${id}`)
}

// 数据淘汰API
export const dataEvictionAPI = {
  cleanRawData: () => api.post('/data-eviction/raw-data'),
  cleanProcessedData: () => api.post('/data-eviction/processed-data'),
  cleanAll: () => api.post('/data-eviction/all'),
  cleanByStrategy: (dataType) => api.post(`/data-eviction/strategy/${dataType}`)
}

// 灾情解码API
export const disasterDecodeAPI = {
  decodeId: (id) => api.get(`/disaster-decode/decode/${id}`),
  batchDecodeIds: (ids) => api.post('/disaster-decode/decode/batch', { ids }),
  validateId: (id) => api.get(`/disaster-decode/validate/${id}`),
  getLocationByGeoCode: (geoCode) => api.get(`/disaster-decode/geo/${geoCode}`),
  locateByGeoCode: (geoCode) => api.get(`/disaster-decode/geo/locate/${geoCode}`),
  reverseGeocode: (longitude, latitude) => api.get(`/disaster-decode/geo/reverse?longitude=${longitude}&latitude=${latitude}`),
  decodeFileName: (fileName) => api.post('/disaster-decode/file/decode-filename', { fileName }),
  batchDecodeFileNames: (fileNames) => api.post('/disaster-decode/file/decode-filenames/batch', { fileNames }),
  decodeExcel: (formData, idColumnIndex, descriptionColumnIndex) => {
    const params = new URLSearchParams()
    if (idColumnIndex !== undefined) params.append('idColumnIndex', idColumnIndex)
    if (descriptionColumnIndex !== undefined) params.append('descriptionColumnIndex', descriptionColumnIndex)
    return api.post(`/disaster-decode/file/decode-excel?${params.toString()}`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },
  decodeExcelFromPath: (filePath, idColumnIndex, descriptionColumnIndex) => api.post('/disaster-decode/file/decode-excel/path', {
    filePath, idColumnIndex, descriptionColumnIndex
  }),
  // 新增：基于已上传文件的解码功能
  getDecodableFiles: () => api.get('/disaster-decode/file/decodable-files'),
  getExcelFiles: () => api.get('/disaster-decode/file/excel-files'),
  decodeFileById: (fileId) => api.get(`/disaster-decode/file/decode-by-id/${fileId}`),
  decodeFilesByIds: (fileIds) => api.post('/disaster-decode/file/decode-by-ids', { fileIds }),
  decodeExcelById: (fileId, idColumnIndex, descriptionColumnIndex, startRow, endRow) => {
    const params = new URLSearchParams()
    if (idColumnIndex !== undefined && idColumnIndex !== null) params.append('idColumnIndex', idColumnIndex)
    if (descriptionColumnIndex !== undefined && descriptionColumnIndex !== null) params.append('descriptionColumnIndex', descriptionColumnIndex)
    if (startRow !== undefined && startRow !== null) params.append('startRow', startRow)
    if (endRow !== undefined && endRow !== null) params.append('endRow', endRow)
    const queryString = params.toString()
    return api.get(`/disaster-decode/file/decode-excel-by-id/${fileId}${queryString ? '?' + queryString : ''}`)
  }
}

// 灾情数据查询API
export const disasterDataAPI = {
  queryByCategory: (category) => api.get(`/disaster-data/category/${encodeURIComponent(category)}`),
  queryBySubcategory: (subcategory) => api.get(`/disaster-data/subcategory/${encodeURIComponent(subcategory)}`),
  queryBySource: (source) => api.get(`/disaster-data/source/${encodeURIComponent(source)}`),
  queryByCarrierType: (carrierType) => api.get(`/disaster-data/carrier/${encodeURIComponent(carrierType)}`),
  queryByGeoCode: (geoCode) => api.get(`/disaster-data/geo/${geoCode}`),
  queryByTimeRange: (start, end) => api.get(`/disaster-data/time-range?start=${start}&end=${end}`),
  query: (params) => api.post('/disaster-data/query', params),
  getDetail: (id) => api.get(`/disaster-data/detail/${id}`)
}

// 灾情处理API
export const disasterProcessAPI = {
  process: (data) => api.post('/disaster-process/process', data),
  processBatch: (dataList) => api.post('/disaster-process/process/batch', dataList),
  processByIdString: (rawData, idString) => api.post('/disaster-process/process/by-id-string', { rawData, idString })
}

// 地图可视化API
export const mapAPI = {
  getDisasterPoints: (params = {}) => {
    const queryParams = new URLSearchParams()
    if (params.disasterCategory) queryParams.append('disasterCategory', params.disasterCategory)
    if (params.source) queryParams.append('source', params.source)
    if (params.carrierType) queryParams.append('carrierType', params.carrierType)
    const queryString = queryParams.toString()
    return api.get(`/map/disaster-points${queryString ? '?' + queryString : ''}`)
  },
  getRegionStatistics: (level = 'province') => api.get(`/map/region-statistics?level=${level}`),
  getClusterPoints: (zoomLevel = 5, bounds = '') => {
    const params = new URLSearchParams({ zoomLevel })
    if (bounds) params.append('bounds', bounds)
    return api.get(`/map/cluster?${params.toString()}`)
  },
  getTimelineData: (startTime, endTime) => {
    const params = new URLSearchParams()
    if (startTime) params.append('startTime', startTime)
    if (endTime) params.append('endTime', endTime)
    const queryString = params.toString()
    return api.get(`/map/timeline${queryString ? '?' + queryString : ''}`)
  },
  getMapOverview: () => api.get('/map/overview')
}

// 认证相关API
export const authAPI = {
  login: (username, phone) => api.post('/auth/login', { username, phone }),
  logout: () => api.post('/auth/logout'),
  getCurrentUser: () => api.get('/auth/current-user')
}

export default api
