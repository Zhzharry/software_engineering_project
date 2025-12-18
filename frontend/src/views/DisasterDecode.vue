<template>
  <div class="disaster-decode">
    <!-- 标签页 -->
    <el-card shadow="never">
      <el-tabs v-model="activeTab" type="border-card">
        <!-- ID解码 -->
        <el-tab-pane label="ID解码" name="id">
          <div class="decode-section">
            <el-form :model="idForm" label-width="120px">
              <el-form-item label="36位ID">
                <el-input
                  v-model="idForm.id"
                  placeholder="请输入36位一体化编码ID"
                  maxlength="36"
                  show-word-limit
                  style="width: 400px;"
                />
                <el-button type="primary" @click="decodeId" :loading="idLoading" style="margin-left: 10px;">
                  <el-icon><Search /></el-icon> 解码
                </el-button>
                <el-button @click="validateId" :loading="idLoading">
                  <el-icon><Check /></el-icon> 验证格式
                </el-button>
              </el-form-item>
            </el-form>

            <!-- 解码结果 -->
            <el-card v-if="decodeResult" class="result-card" shadow="hover">
              <template #header>
                <div class="result-header">
                  <span>解码结果</span>
                  <el-tag type="success">解码成功</el-tag>
                </div>
              </template>
              <el-descriptions :column="2" border>
                <el-descriptions-item label="原始ID">{{ decodeResult.originalId }}</el-descriptions-item>
                <el-descriptions-item label="时间">{{ decodeResult.dateTime }}</el-descriptions-item>
                <el-descriptions-item label="地理码">{{ decodeResult.geoCode }}</el-descriptions-item>
                <el-descriptions-item label="灾害大类">
                  <el-tag type="danger">{{ decodeResult.disasterCategoryName }}</el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="灾害子类">{{ decodeResult.disasterSubcategoryName }}</el-descriptions-item>
                <el-descriptions-item label="灾情指标">{{ decodeResult.disasterIndicatorName }}</el-descriptions-item>
                <el-descriptions-item label="来源">{{ decodeResult.sourceCategoryName }} - {{ decodeResult.sourceSubcategoryName }}</el-descriptions-item>
                <el-descriptions-item label="载体">{{ decodeResult.carrierName }}</el-descriptions-item>
                <el-descriptions-item label="描述" :span="2">{{ decodeResult.description }}</el-descriptions-item>
              </el-descriptions>
            </el-card>
          </div>
        </el-tab-pane>

        <!-- 批量解码 -->
        <el-tab-pane label="批量解码" name="batch">
          <div class="decode-section">
            <el-form :model="batchForm" label-width="120px">
              <el-form-item label="ID列表">
                <el-input
                  v-model="batchForm.ids"
                  type="textarea"
                  :rows="6"
                  placeholder="每行输入一个36位ID"
                  style="width: 500px;"
                />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="batchDecodeIds" :loading="batchLoading">
                  <el-icon><DataLine /></el-icon> 批量解码
                </el-button>
                <span class="tip">共 {{ batchIdCount }} 个ID</span>
              </el-form-item>
            </el-form>

            <!-- 批量解码结果 -->
            <el-table v-if="batchResults.length > 0" :data="batchResults" style="margin-top: 20px;" max-height="400">
              <el-table-column prop="originalId" label="ID" width="150" show-overflow-tooltip />
              <el-table-column prop="disasterCategoryName" label="灾害大类" width="120">
                <template #default="{ row }">
                  <el-tag v-if="row.disasterCategoryName" type="danger" size="small">{{ row.disasterCategoryName }}</el-tag>
                  <span v-else>-</span>
                </template>
              </el-table-column>
              <el-table-column prop="disasterSubcategoryName" label="灾害子类" width="120" />
              <el-table-column prop="sourceCategoryName" label="来源" width="100" />
              <el-table-column prop="carrierName" label="载体" width="80" />
              <el-table-column prop="dateTime" label="时间" width="180" />
              <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
            </el-table>
          </div>
        </el-tab-pane>

        <!-- 文件名解码 -->
        <el-tab-pane label="文件名解码" name="filename">
          <div class="decode-section">
            <!-- 手动输入文件名 -->
            <el-form :model="fileNameForm" label-width="120px">
              <el-form-item label="文件名">
                <el-input
                  v-model="fileNameForm.fileName"
                  placeholder="输入包含36位ID的文件名，如: 510100000000202512181200001030101001.jpg"
                  style="width: 500px;"
                />
                <el-button type="primary" @click="decodeFileName" :loading="fileNameLoading" style="margin-left: 10px;">
                  <el-icon><Document /></el-icon> 解码
                </el-button>
              </el-form-item>
            </el-form>

            <!-- 从已上传文件选择 -->
            <el-divider content-position="left">或从已上传文件中选择</el-divider>

            <el-button type="primary" @click="loadDecodableFiles" :loading="decodableFilesLoading" style="margin-bottom: 15px;">
              <el-icon><Refresh /></el-icon> 加载可解码文件
            </el-button>

            <el-table
              v-if="decodableFiles.length > 0"
              :data="decodableFiles"
              style="width: 100%"
              max-height="300"
              @row-click="handleFileSelect"
              highlight-current-row
            >
              <el-table-column prop="originalName" label="文件名" min-width="300" show-overflow-tooltip />
              <el-table-column prop="fileType" label="类型" width="100" />
              <el-table-column prop="fileSize" label="大小" width="100">
                <template #default="{ row }">
                  {{ formatFileSize(row.fileSize) }}
                </template>
              </el-table-column>
              <el-table-column prop="createTime" label="上传时间" width="180">
                <template #default="{ row }">
                  {{ formatTime(row.createTime) }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="100">
                <template #default="{ row }">
                  <el-button type="primary" link @click.stop="decodeFileById(row.id)">
                    <el-icon><Search /></el-icon> 解码
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            <el-empty v-else-if="decodableFilesLoaded" description="没有找到文件名包含36位灾情码的文件" />

            <!-- 文件名解码结果 -->
            <el-card v-if="fileNameResult" class="result-card" shadow="hover">
              <template #header>
                <div class="result-header">
                  <span>解码结果</span>
                  <el-tag :type="fileNameResult.hasValidId ? 'success' : 'danger'">
                    {{ fileNameResult.hasValidId ? '包含有效ID' : '无有效ID' }}
                  </el-tag>
                </div>
              </template>
              <el-descriptions :column="2" border v-if="fileNameResult.hasValidId">
                <el-descriptions-item label="文件名">{{ fileNameResult.fileName }}</el-descriptions-item>
                <el-descriptions-item label="提取的ID">{{ fileNameResult.extractedId }}</el-descriptions-item>
                <el-descriptions-item label="灾害大类">
                  <el-tag type="danger">{{ fileNameResult.decodedId?.disasterCategoryName }}</el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="灾害子类">{{ fileNameResult.decodedId?.disasterSubcategoryName }}</el-descriptions-item>
                <el-descriptions-item label="来源">{{ fileNameResult.decodedId?.sourceCategoryName }}</el-descriptions-item>
                <el-descriptions-item label="载体">{{ fileNameResult.decodedId?.carrierName }}</el-descriptions-item>
                <el-descriptions-item label="时间">{{ fileNameResult.decodedId?.dateTime }}</el-descriptions-item>
                <el-descriptions-item label="描述" :span="2">{{ fileNameResult.decodedId?.description }}</el-descriptions-item>
              </el-descriptions>
              <el-empty v-else description="文件名中未找到有效的36位ID" />
            </el-card>
          </div>
        </el-tab-pane>

        <!-- Excel解码 -->
        <el-tab-pane label="Excel解码" name="excel">
          <div class="decode-section">
            <!-- 方式一：上传新文件 -->
            <el-form-item label="上传Excel文件" label-width="120px">
              <el-upload
                ref="excelUploadRef"
                :auto-upload="false"
                :limit="1"
                accept=".xlsx,.xls"
                :on-change="handleExcelChange"
                :on-remove="handleExcelRemove"
              >
                <template #trigger>
                  <el-button type="primary">
                    <el-icon><Upload /></el-icon> 选择Excel文件
                  </el-button>
                </template>
                <template #tip>
                  <div class="el-upload__tip">只能上传 .xlsx 或 .xls 文件</div>
                </template>
              </el-upload>
            </el-form-item>

            <!-- 方式二：从已上传文件选择 -->
            <el-divider content-position="left">或从已上传Excel文件中选择</el-divider>

            <el-button type="primary" @click="loadExcelFiles" :loading="excelFilesLoading" style="margin-bottom: 15px;">
              <el-icon><Refresh /></el-icon> 加载已上传Excel文件
            </el-button>

            <el-table
              v-if="excelFiles.length > 0"
              :data="excelFiles"
              style="width: 100%; margin-bottom: 20px;"
              max-height="200"
              @row-click="handleExcelFileSelect"
              highlight-current-row
            >
              <el-table-column type="selection" width="55" />
              <el-table-column prop="originalName" label="文件名" min-width="250" show-overflow-tooltip />
              <el-table-column prop="fileSize" label="大小" width="100">
                <template #default="{ row }">
                  {{ formatFileSize(row.fileSize) }}
                </template>
              </el-table-column>
              <el-table-column prop="createTime" label="上传时间" width="180">
                <template #default="{ row }">
                  {{ formatTime(row.createTime) }}
                </template>
              </el-table-column>
            </el-table>

            <el-alert
              v-if="selectedExcelFile"
              :title="`已选择文件: ${selectedExcelFile.originalName}`"
              type="success"
              :closable="false"
              style="margin-bottom: 15px;"
            />

            <!-- 解码参数 -->
            <el-form :model="excelForm" label-width="120px" style="margin-top: 20px;">
              <el-form-item label="ID列索引">
                <el-input-number v-model="excelForm.idColumnIndex" :min="0" :max="100" placeholder="自动识别" />
                <span class="tip">从0开始，留空则自动识别</span>
              </el-form-item>
              <el-form-item label="描述列索引">
                <el-input-number v-model="excelForm.descriptionColumnIndex" :min="0" :max="100" placeholder="可选" />
                <span class="tip">可选，用于显示描述信息</span>
              </el-form-item>
              <el-form-item label="行范围">
                <el-input-number v-model="excelForm.startRow" :min="1" placeholder="起始行" style="width: 120px;" />
                <span style="margin: 0 10px;">到</span>
                <el-input-number v-model="excelForm.endRow" :min="1" placeholder="结束行" style="width: 120px;" />
                <span class="tip">从1开始计数，默认解码全部数据行（跳过表头）</span>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="decodeExcel" :loading="excelLoading" :disabled="!excelFile && !selectedExcelFile">
                  <el-icon><DataAnalysis /></el-icon> 开始解码
                </el-button>
              </el-form-item>
            </el-form>

            <!-- Excel解码结果 -->
            <el-card v-if="excelResult" class="result-card" shadow="hover">
              <template #header>
                <div class="result-header">
                  <span>解码结果 - {{ excelResult.fileName }}</span>
                  <div>
                    <el-tag type="success">成功: {{ excelResult.successCount }}</el-tag>
                    <el-tag type="danger" style="margin-left: 8px;">失败: {{ excelResult.failCount }}</el-tag>
                  </div>
                </div>
              </template>
              <el-table :data="excelResult.rows" max-height="400">
                <el-table-column prop="rowIndex" label="行号" width="70" />
                <el-table-column prop="id" label="ID" width="150" show-overflow-tooltip />
                <el-table-column prop="description" label="描述" width="150" show-overflow-tooltip />
                <el-table-column label="状态" width="80">
                  <template #default="{ row }">
                    <el-tag :type="row.success ? 'success' : 'danger'" size="small">
                      {{ row.success ? '成功' : '失败' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="灾害类型" width="120">
                  <template #default="{ row }">
                    {{ row.decodedId?.disasterCategoryName || '-' }}
                  </template>
                </el-table-column>
                <el-table-column label="子类" width="100">
                  <template #default="{ row }">
                    {{ row.decodedId?.disasterSubcategoryName || '-' }}
                  </template>
                </el-table-column>
                <el-table-column label="来源" width="100">
                  <template #default="{ row }">
                    {{ row.decodedId?.sourceCategoryName || '-' }}
                  </template>
                </el-table-column>
                <el-table-column prop="errorMessage" label="错误信息" min-width="150" show-overflow-tooltip />
              </el-table>
            </el-card>
          </div>
        </el-tab-pane>

        <!-- 地理码定位 -->
        <el-tab-pane label="地理码定位" name="geo">
          <div class="decode-section">
            <el-form :model="geoForm" label-width="120px">
              <el-form-item label="12位地理码">
                <el-input
                  v-model="geoForm.geoCode"
                  placeholder="请输入12位地理码"
                  maxlength="12"
                  show-word-limit
                  style="width: 300px;"
                />
                <el-button type="primary" @click="locateByGeoCode" :loading="geoLoading" style="margin-left: 10px;">
                  <el-icon><Location /></el-icon> 定位
                </el-button>
              </el-form-item>
            </el-form>

            <!-- 地理码定位结果 -->
            <el-card v-if="geoResult" class="result-card" shadow="hover">
              <template #header>
                <div class="result-header">
                  <span>定位结果</span>
                  <el-tag type="success">定位成功</el-tag>
                </div>
              </template>
              <el-descriptions :column="2" border>
                <el-descriptions-item label="地理码">{{ geoResult.geoCode }}</el-descriptions-item>
                <el-descriptions-item label="经纬度">{{ geoResult.longitude }}, {{ geoResult.latitude }}</el-descriptions-item>
                <el-descriptions-item label="省份">{{ geoResult.province }}</el-descriptions-item>
                <el-descriptions-item label="城市">{{ geoResult.city }}</el-descriptions-item>
                <el-descriptions-item label="区县">{{ geoResult.district }}</el-descriptions-item>
                <el-descriptions-item label="乡镇">{{ geoResult.township || '-' }}</el-descriptions-item>
                <el-descriptions-item label="完整地址" :span="2">{{ geoResult.formattedAddress }}</el-descriptions-item>
              </el-descriptions>
            </el-card>

            <el-divider>逆地理编码</el-divider>

            <el-form :model="reverseGeoForm" label-width="120px">
              <el-form-item label="经度">
                <el-input-number v-model="reverseGeoForm.longitude" :precision="6" :step="0.1" style="width: 200px;" />
              </el-form-item>
              <el-form-item label="纬度">
                <el-input-number v-model="reverseGeoForm.latitude" :precision="6" :step="0.1" style="width: 200px;" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="reverseGeocode" :loading="reverseGeoLoading">
                  <el-icon><MapLocation /></el-icon> 逆地理编码
                </el-button>
              </el-form-item>
            </el-form>

            <!-- 逆地理编码结果 -->
            <el-card v-if="reverseGeoResult" class="result-card" shadow="hover">
              <template #header>
                <span>逆地理编码结果</span>
              </template>
              <el-descriptions :column="2" border>
                <el-descriptions-item label="经纬度">{{ reverseGeoResult.longitude }}, {{ reverseGeoResult.latitude }}</el-descriptions-item>
                <el-descriptions-item label="省份">{{ reverseGeoResult.province }}</el-descriptions-item>
                <el-descriptions-item label="城市">{{ reverseGeoResult.city }}</el-descriptions-item>
                <el-descriptions-item label="区县">{{ reverseGeoResult.district }}</el-descriptions-item>
                <el-descriptions-item label="地址" :span="2">{{ reverseGeoResult.formattedAddress }}</el-descriptions-item>
              </el-descriptions>
            </el-card>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- ID编码规则说明 -->
    <el-card shadow="never" class="help-card">
      <template #header>
        <span>36位ID编码规则说明</span>
      </template>
      <el-table :data="codeRules" border size="small">
        <el-table-column prop="position" label="位置" width="100" />
        <el-table-column prop="length" label="长度" width="80" />
        <el-table-column prop="name" label="名称" width="120" />
        <el-table-column prop="description" label="说明" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { disasterDecodeAPI } from '../api'
import { ElMessage } from 'element-plus'

// 当前标签页
const activeTab = ref('id')

// ID解码
const idForm = reactive({ id: '' })
const idLoading = ref(false)
const decodeResult = ref(null)

// 批量解码
const batchForm = reactive({ ids: '' })
const batchLoading = ref(false)
const batchResults = ref([])

const batchIdCount = computed(() => {
  return batchForm.ids.trim().split('\n').filter(id => id.trim()).length
})

// 文件名解码
const fileNameForm = reactive({ fileName: '' })
const fileNameLoading = ref(false)
const fileNameResult = ref(null)

// 可解码文件列表
const decodableFiles = ref([])
const decodableFilesLoading = ref(false)
const decodableFilesLoaded = ref(false)

// Excel解码
const excelFile = ref(null)
const excelForm = reactive({
  idColumnIndex: null,
  descriptionColumnIndex: null,
  startRow: null,
  endRow: null
})
const excelLoading = ref(false)
const excelResult = ref(null)

// Excel文件列表
const excelFiles = ref([])
const excelFilesLoading = ref(false)
const selectedExcelFile = ref(null)

// 地理码定位
const geoForm = reactive({ geoCode: '' })
const geoLoading = ref(false)
const geoResult = ref(null)

// 逆地理编码
const reverseGeoForm = reactive({ longitude: 116.405285, latitude: 39.904989 })
const reverseGeoLoading = ref(false)
const reverseGeoResult = ref(null)

// 编码规则
const codeRules = [
  { position: '1-12', length: '12位', name: '地理码', description: '省(2)+市(2)+县(2)+乡镇(3)+村(3)' },
  { position: '13-26', length: '14位', name: '时间码', description: 'YYYYMMDDHHMMSS 格式' },
  { position: '27', length: '1位', name: '来源大类', description: '0-系统内部, 1-外部接口' },
  { position: '28-29', length: '2位', name: '来源子类', description: '01-后方指挥部, 02-前方指挥部...' },
  { position: '30', length: '1位', name: '载体码', description: '0-文字, 1-图像, 2-音频, 3-视频' },
  { position: '31', length: '1位', name: '灾害大类', description: '1-震情, 2-人员伤亡, 3-房屋破坏...' },
  { position: '32-33', length: '2位', name: '灾害子类', description: '如房屋破坏: 01-土木, 02-砖木...' },
  { position: '34-36', length: '3位', name: '灾情指标', description: '具体灾情指标代码' }
]

// 解码单个ID
const decodeId = async () => {
  if (!idForm.id || idForm.id.length !== 36) {
    ElMessage.warning('请输入36位ID')
    return
  }
  idLoading.value = true
  try {
    const res = await disasterDecodeAPI.decodeId(idForm.id)
    if (res.data?.code === 200) {
      decodeResult.value = res.data.data
      ElMessage.success('解码成功')
    } else {
      ElMessage.error(res.data?.message || '解码失败')
    }
  } catch (error) {
    console.error('解码失败:', error)
    ElMessage.error('解码失败')
  } finally {
    idLoading.value = false
  }
}

// 验证ID格式
const validateId = async () => {
  if (!idForm.id) {
    ElMessage.warning('请输入ID')
    return
  }
  idLoading.value = true
  try {
    const res = await disasterDecodeAPI.validateId(idForm.id)
    if (res.data?.code === 200) {
      if (res.data.data) {
        ElMessage.success('ID格式正确')
      } else {
        ElMessage.warning('ID格式不正确')
      }
    }
  } catch (error) {
    console.error('验证失败:', error)
    ElMessage.error('验证失败')
  } finally {
    idLoading.value = false
  }
}

// 批量解码
const batchDecodeIds = async () => {
  const ids = batchForm.ids.trim().split('\n').filter(id => id.trim()).map(id => id.trim())
  if (ids.length === 0) {
    ElMessage.warning('请输入ID')
    return
  }
  batchLoading.value = true
  try {
    const res = await disasterDecodeAPI.batchDecodeIds(ids)
    if (res.data?.code === 200) {
      batchResults.value = res.data.data || []
      ElMessage.success(`成功解码 ${batchResults.value.length} 个ID`)
    } else {
      ElMessage.error(res.data?.message || '批量解码失败')
    }
  } catch (error) {
    console.error('批量解码失败:', error)
    ElMessage.error('批量解码失败')
  } finally {
    batchLoading.value = false
  }
}

// 解码文件名（手动输入）
const decodeFileName = async () => {
  if (!fileNameForm.fileName) {
    ElMessage.warning('请输入文件名')
    return
  }
  fileNameLoading.value = true
  try {
    const res = await disasterDecodeAPI.decodeFileName(fileNameForm.fileName)
    if (res.data?.code === 200) {
      fileNameResult.value = res.data.data
      ElMessage.success('解码完成')
    } else {
      ElMessage.error(res.data?.message || '解码失败')
    }
  } catch (error) {
    console.error('文件名解码失败:', error)
    ElMessage.error('文件名解码失败')
  } finally {
    fileNameLoading.value = false
  }
}

// 加载可解码文件列表
const loadDecodableFiles = async () => {
  decodableFilesLoading.value = true
  try {
    const res = await disasterDecodeAPI.getDecodableFiles()
    if (res.data?.code === 200) {
      decodableFiles.value = res.data.data || []
      decodableFilesLoaded.value = true
      if (decodableFiles.value.length === 0) {
        ElMessage.info('没有找到文件名包含36位灾情码的文件')
      } else {
        ElMessage.success(`找到 ${decodableFiles.value.length} 个可解码文件`)
      }
    } else {
      ElMessage.error(res.data?.message || '加载文件列表失败')
    }
  } catch (error) {
    console.error('加载可解码文件失败:', error)
    ElMessage.error('加载文件列表失败')
  } finally {
    decodableFilesLoading.value = false
  }
}

// 选择文件进行解码
const handleFileSelect = (row) => {
  decodeFileById(row.id)
}

// 根据文件ID解码
const decodeFileById = async (fileId) => {
  fileNameLoading.value = true
  try {
    const res = await disasterDecodeAPI.decodeFileById(fileId)
    if (res.data?.code === 200) {
      fileNameResult.value = res.data.data
      ElMessage.success('解码完成')
    } else {
      ElMessage.error(res.data?.message || '解码失败')
    }
  } catch (error) {
    console.error('文件解码失败:', error)
    ElMessage.error('文件解码失败')
  } finally {
    fileNameLoading.value = false
  }
}

// Excel文件选择（上传）
const handleExcelChange = (file) => {
  excelFile.value = file.raw
  selectedExcelFile.value = null  // 清除已选择的服务器文件
}

const handleExcelRemove = () => {
  excelFile.value = null
  excelResult.value = null
}

// 加载已上传的Excel文件列表
const loadExcelFiles = async () => {
  excelFilesLoading.value = true
  try {
    const res = await disasterDecodeAPI.getExcelFiles()
    if (res.data?.code === 200) {
      excelFiles.value = res.data.data || []
      if (excelFiles.value.length === 0) {
        ElMessage.info('没有找到已上传的Excel文件')
      } else {
        ElMessage.success(`找到 ${excelFiles.value.length} 个Excel文件`)
      }
    } else {
      ElMessage.error(res.data?.message || '加载Excel文件列表失败')
    }
  } catch (error) {
    console.error('加载Excel文件列表失败:', error)
    ElMessage.error('加载Excel文件列表失败')
  } finally {
    excelFilesLoading.value = false
  }
}

// 选择Excel文件
const handleExcelFileSelect = (row) => {
  selectedExcelFile.value = row
  excelFile.value = null  // 清除上传的文件
  ElMessage.success(`已选择文件: ${row.originalName}`)
}

// 解码Excel
const decodeExcel = async () => {
  if (!excelFile.value && !selectedExcelFile.value) {
    ElMessage.warning('请选择Excel文件')
    return
  }

  excelLoading.value = true
  try {
    let res
    if (selectedExcelFile.value) {
      // 使用已上传的文件
      res = await disasterDecodeAPI.decodeExcelById(
        selectedExcelFile.value.id,
        excelForm.idColumnIndex,
        excelForm.descriptionColumnIndex,
        excelForm.startRow,
        excelForm.endRow
      )
    } else {
      // 上传新文件
      const formData = new FormData()
      formData.append('file', excelFile.value)
      res = await disasterDecodeAPI.decodeExcel(
        formData,
        excelForm.idColumnIndex,
        excelForm.descriptionColumnIndex
      )
    }

    if (res.data?.code === 200) {
      excelResult.value = res.data.data
      ElMessage.success(`解码完成: 成功 ${excelResult.value.successCount}, 失败 ${excelResult.value.failCount}`)
    } else {
      ElMessage.error(res.data?.message || 'Excel解码失败')
    }
  } catch (error) {
    console.error('Excel解码失败:', error)
    ElMessage.error('Excel解码失败')
  } finally {
    excelLoading.value = false
  }
}

// 地理码定位
const locateByGeoCode = async () => {
  if (!geoForm.geoCode || geoForm.geoCode.length !== 12) {
    ElMessage.warning('请输入12位地理码')
    return
  }
  geoLoading.value = true
  try {
    const res = await disasterDecodeAPI.locateByGeoCode(geoForm.geoCode)
    if (res.data?.code === 200) {
      geoResult.value = res.data.data
      ElMessage.success('定位成功')
    } else {
      ElMessage.error(res.data?.message || '定位失败')
    }
  } catch (error) {
    console.error('定位失败:', error)
    ElMessage.error('定位失败')
  } finally {
    geoLoading.value = false
  }
}

// 逆地理编码
const reverseGeocode = async () => {
  if (!reverseGeoForm.longitude || !reverseGeoForm.latitude) {
    ElMessage.warning('请输入经纬度')
    return
  }
  reverseGeoLoading.value = true
  try {
    const res = await disasterDecodeAPI.reverseGeocode(reverseGeoForm.longitude, reverseGeoForm.latitude)
    if (res.data?.code === 200) {
      reverseGeoResult.value = res.data.data
      ElMessage.success('逆地理编码成功')
    } else {
      ElMessage.error(res.data?.message || '逆地理编码失败')
    }
  } catch (error) {
    console.error('逆地理编码失败:', error)
    ElMessage.error('逆地理编码失败')
  } finally {
    reverseGeoLoading.value = false
  }
}

// 工具函数
const formatFileSize = (bytes) => {
  if (!bytes) return '-'
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(2) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(2) + ' MB'
}

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}
</script>

<style scoped>
.disaster-decode {
  padding: 20px;
}

.decode-section {
  padding: 20px;
}

.result-card {
  margin-top: 20px;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tip {
  margin-left: 10px;
  color: #909399;
  font-size: 13px;
}

.help-card {
  margin-top: 20px;
}
</style>
