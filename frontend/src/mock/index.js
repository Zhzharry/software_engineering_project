// Mock 配置
// 设置为 false 使用真实后端 API，设置为 true 使用 mock 数据
export const USE_MOCK = false

// Mock API 占位符（当 USE_MOCK 为 false 时不会被使用）
const createMockResponse = (data) => Promise.resolve({ data: { code: 200, data, message: 'success' } })

export const mockUserAPI = {
  getAll: () => createMockResponse([]),
  getById: () => createMockResponse({}),
  create: () => createMockResponse({}),
  update: () => createMockResponse({}),
  delete: () => createMockResponse(null)
}

export const mockModuleAPI = {
  getAll: () => createMockResponse([]),
  getById: () => createMockResponse({}),
  create: () => createMockResponse({}),
  update: () => createMockResponse({}),
  delete: () => createMockResponse(null)
}

export const mockRawDataAPI = {
  getAll: () => createMockResponse([]),
  getById: () => createMockResponse({}),
  create: () => createMockResponse({}),
  delete: () => createMockResponse(null)
}

export const mockProcessedDataAPI = {
  getAll: () => createMockResponse([]),
  getById: () => createMockResponse({}),
  create: () => createMockResponse({}),
  updateConfidence: () => createMockResponse({}),
  delete: () => createMockResponse(null)
}

export const mockLogDataAPI = {
  getAll: () => createMockResponse([]),
  getById: () => createMockResponse({}),
  create: () => createMockResponse({}),
  delete: () => createMockResponse(null)
}
