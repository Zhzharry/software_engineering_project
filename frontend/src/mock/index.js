// Mock 数据配置
// 开发环境默认使用 Mock，生产环境使用真实 API
// 也可以在 .env 文件中设置 VITE_USE_MOCK=true/false 来覆盖
export const USE_MOCK = import.meta.env.VITE_USE_MOCK === 'true' ||
  (import.meta.env.VITE_USE_MOCK === undefined && import.meta.env.DEV)

// 模拟延迟（毫秒）
const MOCK_DELAY = 300

// 生成随机 ID
const generateId = () => Math.random().toString(36).substr(2, 9)

// 模拟异步响应
const mockResponse = (data, delay = MOCK_DELAY) => {
  return new Promise(resolve => {
    setTimeout(() => {
      resolve({ data: { code: 200, data, message: 'success' } })
    }, delay)
  })
}

// ==================== 用户数据 ====================
let mockUsers = [
  { id: 1, username: 'admin', email: 'admin@example.com', phone: '13800138000', status: 1, createTime: '2024-01-01 10:00:00' },
  { id: 2, username: 'zhangsan', email: 'zhangsan@example.com', phone: '13800138001', status: 1, createTime: '2024-01-02 11:30:00' },
  { id: 3, username: 'lisi', email: 'lisi@example.com', phone: '13800138002', status: 0, createTime: '2024-01-03 14:20:00' },
  { id: 4, username: 'wangwu', email: 'wangwu@example.com', phone: '13800138003', status: 1, createTime: '2024-01-04 09:15:00' },
  { id: 5, username: 'zhaoliu', email: 'zhaoliu@example.com', phone: '13800138004', status: 1, createTime: '2024-01-05 16:45:00' },
]

export const mockUserAPI = {
  getAll: () => mockResponse(mockUsers),
  getById: (id) => mockResponse(mockUsers.find(u => u.id === parseInt(id))),
  getByUsername: (username) => mockResponse(mockUsers.find(u => u.username === username)),
  getPage: (page = 0, size = 10) => {
    const start = page * size
    const content = mockUsers.slice(start, start + size)
    return mockResponse({
      content,
      totalElements: mockUsers.length,
      totalPages: Math.ceil(mockUsers.length / size),
      number: page,
      size
    })
  },
  create: (user) => {
    const newUser = { ...user, id: Math.max(...mockUsers.map(u => u.id)) + 1, createTime: new Date().toLocaleString() }
    mockUsers.push(newUser)
    return mockResponse(newUser)
  },
  update: (id, user) => {
    const index = mockUsers.findIndex(u => u.id === parseInt(id))
    if (index !== -1) {
      mockUsers[index] = { ...mockUsers[index], ...user }
      return mockResponse(mockUsers[index])
    }
    return mockResponse(null)
  },
  delete: (id) => {
    mockUsers = mockUsers.filter(u => u.id !== parseInt(id))
    return mockResponse(true)
  },
  checkUsername: (username) => mockResponse(mockUsers.some(u => u.username === username))
}

// ==================== 模块数据 ====================
let mockModules = [
  { id: 1, moduleName: '数据采集模块', description: '负责从各数据源采集原始灾情数据', status: 1, createTime: '2024-01-01 10:00:00' },
  { id: 2, moduleName: '数据清洗模块', description: '对原始数据进行去重、格式化等清洗操作', status: 1, createTime: '2024-01-02 11:30:00' },
  { id: 3, moduleName: '数据分析模块', description: '对清洗后的数据进行统计分析和趋势预测', status: 1, createTime: '2024-01-03 14:20:00' },
  { id: 4, moduleName: '预警发布模块', description: '根据分析结果发布灾情预警信息', status: 0, createTime: '2024-01-04 09:15:00' },
  { id: 5, moduleName: '报告生成模块', description: '自动生成灾情分析报告', status: 1, createTime: '2024-01-05 16:45:00' },
]

export const mockModuleAPI = {
  getAll: (page = 0, size = 10) => {
    const start = page * size
    const content = mockModules.slice(start, start + size)
    return mockResponse({
      content,
      totalElements: mockModules.length,
      totalPages: Math.ceil(mockModules.length / size),
      number: page,
      size
    })
  },
  getById: (id) => mockResponse(mockModules.find(m => m.id === parseInt(id))),
  getByStatus: (status) => mockResponse(mockModules.filter(m => m.status === parseInt(status))),
  create: (module) => {
    const newModule = { ...module, id: Math.max(...mockModules.map(m => m.id)) + 1, createTime: new Date().toLocaleString() }
    mockModules.push(newModule)
    return mockResponse(newModule)
  },
  update: (id, module) => {
    const index = mockModules.findIndex(m => m.id === parseInt(id))
    if (index !== -1) {
      mockModules[index] = { ...mockModules[index], ...module }
      return mockResponse(mockModules[index])
    }
    return mockResponse(null)
  },
  delete: (id) => {
    mockModules = mockModules.filter(m => m.id !== parseInt(id))
    return mockResponse(true)
  }
}

// ==================== 原始数据 ====================
let mockRawData = [
  { id: '1', dataType: '地震数据', source: '中国地震台网', content: { magnitude: 5.2, location: '四川成都', depth: '10km' }, processed: false, collectTime: '2024-01-10 08:30:00' },
  { id: '2', dataType: '洪水数据', source: '水利部监测站', content: { waterLevel: 28.5, flowRate: 1200, location: '长江武汉段' }, processed: true, collectTime: '2024-01-10 09:15:00' },
  { id: '3', dataType: '气象数据', source: '气象局', content: { temperature: 35, humidity: 80, windSpeed: 15, weather: '暴雨' }, processed: false, collectTime: '2024-01-10 10:00:00' },
  { id: '4', dataType: '火灾数据', source: '消防救援中心', content: { location: '北京朝阳区', fireLevel: '二级', casualties: 0 }, processed: true, collectTime: '2024-01-10 11:30:00' },
  { id: '5', dataType: '地震数据', source: '中国地震台网', content: { magnitude: 3.8, location: '云南大理', depth: '8km' }, processed: false, collectTime: '2024-01-10 14:20:00' },
  { id: '6', dataType: '泥石流数据', source: '地质灾害监测站', content: { location: '甘肃陇南', riskLevel: '高', affectedArea: '5平方公里' }, processed: false, collectTime: '2024-01-10 15:45:00' },
  { id: '7', dataType: '台风数据', source: '气象局', content: { name: '海葵', level: 14, windSpeed: 45, path: '东南沿海' }, processed: true, collectTime: '2024-01-10 16:30:00' },
  { id: '8', dataType: '洪水数据', source: '水利部监测站', content: { waterLevel: 32.1, flowRate: 1800, location: '黄河郑州段' }, processed: false, collectTime: '2024-01-10 17:00:00' },
]

export const mockRawDataAPI = {
  getAll: () => mockResponse(mockRawData),
  getById: (id) => mockResponse(mockRawData.find(d => d.id === id)),
  getByType: (type) => mockResponse(mockRawData.filter(d => d.dataType === type)),
  getUnprocessed: () => mockResponse(mockRawData.filter(d => !d.processed)),
  getTimeRange: (start, end) => {
    return mockResponse(mockRawData.filter(d => {
      const time = new Date(d.collectTime).getTime()
      return time >= new Date(start).getTime() && time <= new Date(end).getTime()
    }))
  },
  getCountByType: (type) => mockResponse(mockRawData.filter(d => d.dataType === type).length),
  create: (data) => {
    const newData = { ...data, id: generateId(), collectTime: new Date().toLocaleString() }
    mockRawData.push(newData)
    return mockResponse(newData)
  },
  markAsProcessed: (id) => {
    const item = mockRawData.find(d => d.id === id)
    if (item) {
      item.processed = true
      return mockResponse(item)
    }
    return mockResponse(null)
  },
  delete: (id) => {
    mockRawData = mockRawData.filter(d => d.id !== id)
    return mockResponse(true)
  }
}

// ==================== 处理数据 ====================
let mockProcessedData = [
  { id: '1', rawDataId: '1', processType: '数据清洗', confidenceScore: 0.95, result: { cleanedData: '地震数据已清洗', anomalies: 0 }, processTime: '2024-01-10 09:00:00' },
  { id: '2', rawDataId: '2', processType: '趋势分析', confidenceScore: 0.82, result: { trend: '水位上涨', prediction: '预计持续上涨' }, processTime: '2024-01-10 10:00:00' },
  { id: '3', rawDataId: '4', processType: '风险评估', confidenceScore: 0.78, result: { riskLevel: '中等', recommendation: '加强监控' }, processTime: '2024-01-10 12:00:00' },
  { id: '4', rawDataId: '7', processType: '路径预测', confidenceScore: 0.45, result: { predictedPath: '东北方向', landfall: '预计24小时内登陆' }, processTime: '2024-01-10 17:00:00' },
  { id: '5', rawDataId: '3', processType: '数据融合', confidenceScore: 0.88, result: { fusedData: '气象与地质数据融合', correlation: 0.75 }, processTime: '2024-01-10 18:00:00' },
  { id: '6', rawDataId: '5', processType: '异常检测', confidenceScore: 0.35, result: { anomalyCount: 2, details: '数据存在异常值' }, processTime: '2024-01-10 19:00:00' },
]

export const mockProcessedDataAPI = {
  getAll: () => mockResponse(mockProcessedData),
  getById: (id) => mockResponse(mockProcessedData.find(d => d.id === id)),
  getByType: (type) => mockResponse(mockProcessedData.filter(d => d.processType === type)),
  getByRawDataId: (rawDataId) => mockResponse(mockProcessedData.filter(d => d.rawDataId === rawDataId)),
  getByMinConfidence: (minScore) => mockResponse(mockProcessedData.filter(d => d.confidenceScore >= parseFloat(minScore))),
  getByConfidenceRange: (minScore, maxScore) => {
    return mockResponse(mockProcessedData.filter(d =>
      d.confidenceScore >= parseFloat(minScore) && d.confidenceScore <= parseFloat(maxScore)
    ))
  },
  searchByField: (field, value) => {
    return mockResponse(mockProcessedData.filter(d => {
      if (d.result && d.result[field]) {
        return String(d.result[field]).includes(value)
      }
      return false
    }))
  },
  create: (data) => {
    const newData = { ...data, id: generateId(), processTime: new Date().toLocaleString() }
    mockProcessedData.push(newData)
    return mockResponse(newData)
  },
  updateConfidence: (id, score) => {
    const item = mockProcessedData.find(d => d.id === id)
    if (item) {
      item.confidenceScore = parseFloat(score)
      return mockResponse(item)
    }
    return mockResponse(null)
  },
  delete: (id) => {
    mockProcessedData = mockProcessedData.filter(d => d.id !== id)
    return mockResponse(true)
  }
}

// ==================== 日志数据 ====================
let mockLogData = [
  { id: '1', level: 'INFO', message: '系统启动成功', metadata: { module: 'system' }, timestamp: '2024-01-10 08:00:00' },
  { id: '2', level: 'INFO', message: '数据采集任务开始执行', metadata: { module: 'collector', taskId: 'T001' }, timestamp: '2024-01-10 08:30:00' },
  { id: '3', level: 'WARN', message: '数据源响应延迟', metadata: { source: '气象局', delay: '2500ms' }, timestamp: '2024-01-10 09:15:00' },
  { id: '4', level: 'ERROR', message: '数据库连接失败', metadata: { database: 'MongoDB', error: 'Connection timeout' }, timestamp: '2024-01-10 10:00:00' },
  { id: '5', level: 'INFO', message: '数据清洗完成', metadata: { processedCount: 150, duration: '30s' }, timestamp: '2024-01-10 10:30:00' },
  { id: '6', level: 'WARN', message: '内存使用率超过80%', metadata: { usage: '82%', threshold: '80%' }, timestamp: '2024-01-10 11:00:00' },
  { id: '7', level: 'ERROR', message: 'API调用失败', metadata: { api: '/api/external/weather', statusCode: 503 }, timestamp: '2024-01-10 11:30:00' },
  { id: '8', level: 'INFO', message: '预警信息发布成功', metadata: { alertType: '暴雨预警', region: '华南地区' }, timestamp: '2024-01-10 12:00:00' },
  { id: '9', level: 'INFO', message: '用户登录成功', metadata: { username: 'admin', ip: '192.168.1.100' }, timestamp: '2024-01-10 12:30:00' },
  { id: '10', level: 'ERROR', message: '文件写入失败', metadata: { file: '/logs/report.pdf', error: 'Permission denied' }, timestamp: '2024-01-10 13:00:00' },
]

export const mockLogDataAPI = {
  getAll: () => mockResponse(mockLogData),
  getById: (id) => mockResponse(mockLogData.find(l => l.id === id)),
  getPage: (page = 0, size = 10) => {
    const start = page * size
    const content = mockLogData.slice(start, start + size)
    return mockResponse({
      content,
      totalElements: mockLogData.length,
      totalPages: Math.ceil(mockLogData.length / size),
      number: page,
      size
    })
  },
  getByLevel: (level) => mockResponse(mockLogData.filter(l => l.level === level)),
  getTimeRange: (start, end) => {
    return mockResponse(mockLogData.filter(l => {
      const time = new Date(l.timestamp).getTime()
      return time >= new Date(start).getTime() && time <= new Date(end).getTime()
    }))
  },
  getCountByLevel: (level) => mockResponse(mockLogData.filter(l => l.level === level).length),
  create: (data) => {
    const newData = { ...data, id: generateId(), timestamp: new Date().toLocaleString() }
    mockLogData.push(newData)
    return mockResponse(newData)
  },
  delete: (id) => {
    mockLogData = mockLogData.filter(l => l.id !== id)
    return mockResponse(true)
  }
}
