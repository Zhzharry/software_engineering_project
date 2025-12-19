<template>
  <div class="disaster-data">
    <!-- 搜索条件 -->
    <el-card shadow="never" class="search-card">
      <el-form :model="searchForm" :inline="true" label-width="80px">
        <el-form-item label="灾害大类">
          <el-select v-model="searchForm.disasterCategory" placeholder="全部" clearable style="width: 140px">
            <el-option label="震情" value="震情" />
            <el-option label="人员伤亡及失踪" value="人员伤亡及失踪" />
            <el-option label="房屋破坏" value="房屋破坏" />
            <el-option label="生命线工程灾情" value="生命线工程灾情" />
            <el-option label="次生灾害" value="次生灾害" />
          </el-select>
        </el-form-item>
        <el-form-item label="灾害子类">
          <el-input v-model="searchForm.disasterSubcategory" placeholder="请输入" clearable style="width: 140px" />
        </el-form-item>
        <el-form-item label="数据来源">
          <el-select v-model="searchForm.source" placeholder="全部" clearable style="width: 140px">
            <el-option label="后方指挥部" value="后方指挥部" />
            <el-option label="前方指挥部" value="前方指挥部" />
            <el-option label="现场调查" value="现场调查" />
            <el-option label="互联网感知" value="互联网感知" />
            <el-option label="传感器监测" value="传感器监测" />
            <el-option label="卫星遥感" value="卫星遥感" />
          </el-select>
        </el-form-item>
        <el-form-item label="载体类型">
          <el-select v-model="searchForm.carrierType" placeholder="全部" clearable style="width: 120px">
            <el-option label="文字" value="文字" />
            <el-option label="图像" value="图像" />
            <el-option label="音频" value="音频" />
            <el-option label="视频" value="视频" />
          </el-select>
        </el-form-item>
        <el-form-item label="地理码">
          <el-input v-model="searchForm.geoCode" placeholder="12位地理码" clearable style="width: 140px" />
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="searchForm.timeRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DDTHH:mm:ss"
            style="width: 360px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon> 查询
          </el-button>
          <el-button @click="resetSearch">
            <el-icon><Refresh /></el-icon> 重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card shadow="never" class="table-card" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>灾情数据列表</span>
          <el-tag type="info">共 {{ pagination.total }} 条</el-tag>
        </div>
      </template>

      <el-table :data="tableData" style="width: 100%" row-key="id" @row-click="handleRowClick">
        <!-- 按照数据库列顺序排列 -->
        <el-table-column prop="id" label="ID" width="120" fixed="left">
          <template #default="{ row }">
            <el-text type="primary" truncated>{{ String(row.id) }}</el-text>
          </template>
        </el-table-column>
        <el-table-column prop="dataType" label="数据类型" width="120">
          <template #default="{ row }">
            {{ row.dataType || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="disasterCategory" label="灾害大类" width="130">
          <template #default="{ row }">
            <el-tag :type="getDisasterTagType(row.disasterCategory)" size="small">
              {{ row.disasterCategory || '-' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="disasterSubcategory" label="灾害子类" width="100">
          <template #default="{ row }">
            {{ row.disasterSubcategory || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="disasterIndicator" label="灾情指标" width="120" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.disasterIndicator || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="geoCode" label="地理码" width="130">
          <template #default="{ row }">
            {{ row.geoCode || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="disasterId" label="灾情ID" width="150" show-overflow-tooltip>
          <template #default="{ row }">
            <el-text type="primary" truncated>{{ row.disasterId || '-' }}</el-text>
          </template>
        </el-table-column>
        <el-table-column prop="disasterDateTime" label="灾情时间" width="170">
          <template #default="{ row }">
            {{ formatTime(row.disasterDateTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="sourceSystem" label="来源系统" width="120">
          <template #default="{ row }">
            {{ row.sourceSystem || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="sourceCategory" label="来源类别" width="100">
          <template #default="{ row }">
            {{ row.sourceCategory || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="carrierType" label="载体类型" width="80">
          <template #default="{ row }">
            <el-tag :type="getCarrierTagType(row.carrierType)" size="small">
              {{ row.carrierType || '-' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="decodedDescription" label="解码描述" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.decodedDescription || row.dataContent || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="170">
          <template #default="{ row }">
            {{ formatTime(row.updateTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click.stop="showDetail(row)">
              <el-icon><View /></el-icon> 详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="灾情数据详情" width="800px" destroy-on-close>
      <el-tabs v-model="detailTab">
        <el-tab-pane label="基本信息" name="basic">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="数据ID">{{ detailData?.rawData?.id }}</el-descriptions-item>
            <el-descriptions-item label="灾情ID">{{ detailData?.rawData?.disasterId }}</el-descriptions-item>
            <el-descriptions-item label="灾害大类">
              <el-tag :type="getDisasterTagType(detailData?.rawData?.disasterCategory)">
                {{ detailData?.rawData?.disasterCategory }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="灾害子类">{{ detailData?.rawData?.disasterSubcategory }}</el-descriptions-item>
            <el-descriptions-item label="灾情指标">{{ detailData?.rawData?.disasterIndicator }}</el-descriptions-item>
            <el-descriptions-item label="载体类型">{{ detailData?.rawData?.carrierType }}</el-descriptions-item>
            <el-descriptions-item label="数据来源">{{ detailData?.rawData?.sourceCategory }}</el-descriptions-item>
            <el-descriptions-item label="来源子类">{{ detailData?.rawData?.sourceSubcategory }}</el-descriptions-item>
            <el-descriptions-item label="地理码">{{ detailData?.rawData?.geoCode }}</el-descriptions-item>
            <el-descriptions-item label="发生时间">{{ formatTime(detailData?.rawData?.disasterDateTime) }}</el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ formatTime(detailData?.rawData?.createTime) }}</el-descriptions-item>
            <el-descriptions-item label="处理状态">
              <el-tag :type="detailData?.rawData?.processed ? 'success' : 'warning'" size="small">
                {{ detailData?.rawData?.processed ? '已处理' : '未处理' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="描述" :span="2">{{ detailData?.rawData?.decodedDescription }}</el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>

        <el-tab-pane label="解码信息" name="decoded" v-if="detailData?.decodedId">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="原始ID">{{ detailData?.decodedId?.originalId }}</el-descriptions-item>
            <el-descriptions-item label="时间码">{{ detailData?.decodedId?.timeCode }}</el-descriptions-item>
            <el-descriptions-item label="解析时间">{{ detailData?.decodedId?.dateTime }}</el-descriptions-item>
            <el-descriptions-item label="地理码">{{ detailData?.decodedId?.geoCode }}</el-descriptions-item>
            <el-descriptions-item label="来源大类码">{{ detailData?.decodedId?.sourceCategoryCode }}</el-descriptions-item>
            <el-descriptions-item label="来源大类">{{ detailData?.decodedId?.sourceCategoryName }}</el-descriptions-item>
            <el-descriptions-item label="来源子类码">{{ detailData?.decodedId?.sourceSubcategoryCode }}</el-descriptions-item>
            <el-descriptions-item label="来源子类">{{ detailData?.decodedId?.sourceSubcategoryName }}</el-descriptions-item>
            <el-descriptions-item label="载体码">{{ detailData?.decodedId?.carrierCode }}</el-descriptions-item>
            <el-descriptions-item label="载体名称">{{ detailData?.decodedId?.carrierName }}</el-descriptions-item>
            <el-descriptions-item label="灾情码">{{ detailData?.decodedId?.disasterCode }}</el-descriptions-item>
            <el-descriptions-item label="灾害大类码">{{ detailData?.decodedId?.disasterCategoryCode }}</el-descriptions-item>
            <el-descriptions-item label="灾害大类">{{ detailData?.decodedId?.disasterCategoryName }}</el-descriptions-item>
            <el-descriptions-item label="灾害子类码">{{ detailData?.decodedId?.disasterSubcategoryCode }}</el-descriptions-item>
            <el-descriptions-item label="灾害子类">{{ detailData?.decodedId?.disasterSubcategoryName }}</el-descriptions-item>
            <el-descriptions-item label="灾情指标码">{{ detailData?.decodedId?.disasterIndicatorCode }}</el-descriptions-item>
            <el-descriptions-item label="完整描述" :span="2">{{ detailData?.decodedId?.description }}</el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>

        <el-tab-pane label="原始数据" name="content" v-if="detailData?.rawData?.content">
          <el-input
            type="textarea"
            :rows="15"
            :value="JSON.stringify(detailData?.rawData?.content, null, 2)"
            readonly
          />
        </el-tab-pane>
      </el-tabs>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { disasterDataAPI } from '../api'
import { ElMessage } from 'element-plus'

// 搜索表单
const searchForm = reactive({
  disasterCategory: '',
  disasterSubcategory: '',
  source: '',
  carrierType: '',
  geoCode: '',
  timeRange: null
})

// 表格数据
const tableData = ref([])
const loading = ref(false)

// 分页
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 详情
const detailVisible = ref(false)
const detailTab = ref('basic')
const detailData = ref(null)

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const params = {
      disasterCategory: searchForm.disasterCategory || undefined,
      disasterSubcategory: searchForm.disasterSubcategory || undefined,
      source: searchForm.source || undefined,
      carrierType: searchForm.carrierType || undefined,
      geoCode: searchForm.geoCode || undefined,
      startTime: searchForm.timeRange?.[0] || undefined,
      endTime: searchForm.timeRange?.[1] || undefined,
      page: pagination.page,
      size: pagination.size
    }

    const res = await disasterDataAPI.query(params)
    if (res.data?.code === 200) {
      const responseData = res.data.data
      // 后端返回的是包含data和total的对象
      if (responseData && typeof responseData === 'object' && 'data' in responseData) {
        tableData.value = responseData.data || []
        pagination.total = responseData.total || 0
      } else {
        // 兼容旧格式：直接返回数组
        const data = Array.isArray(responseData) ? responseData : []
        tableData.value = data
        pagination.total = data.length
      }
      
      // 调试信息：打印返回的数据
      console.log('查询返回的数据:', tableData.value)
      console.log('数据条数:', tableData.value.length)
      if (tableData.value.length > 0) {
        console.log('第一条数据:', tableData.value[0])
      }
    } else {
      ElMessage.error(res.data?.message || '加载数据失败')
    }
  } catch (error) {
    console.error('加载数据失败:', error)
    console.error('错误详情:', error.response?.data || error.message)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadData()
}

// 重置
const resetSearch = () => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = key === 'timeRange' ? null : ''
  })
  pagination.page = 1
  loadData()
}

// 分页
const handlePageChange = (page) => {
  pagination.page = page
  loadData()
}

const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  loadData()
}

// 行点击
const handleRowClick = (row) => {
  showDetail(row)
}

// 显示详情
const showDetail = async (row) => {
  try {
    // MySQL的ID是Long类型，需要确保传递正确的ID
    const id = row.id || row.rawData?.id
    const res = await disasterDataAPI.getDetail(id)
    if (res.data?.code === 200) {
      detailData.value = res.data.data
      detailTab.value = 'basic'
      detailVisible.value = true
    } else {
      ElMessage.error(res.data?.message || '获取详情失败')
    }
  } catch (error) {
    console.error('获取详情失败:', error)
    ElMessage.error('获取详情失败')
  }
}

// 工具函数
const getDisasterTagType = (category) => {
  const types = {
    '震情': 'danger',
    '人员伤亡及失踪': 'danger',
    '房屋破坏': 'warning',
    '生命线工程灾情': 'warning',
    '次生灾害': 'info'
  }
  return types[category] || 'info'
}

const getCarrierTagType = (carrier) => {
  const types = {
    '文字': 'primary',
    '图像': 'success',
    '音频': 'warning',
    '视频': 'danger'
  }
  return types[carrier] || 'info'
}

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.disaster-data {
  padding: 20px;
}

.search-card {
  margin-bottom: 16px;
}

.table-card {
  min-height: 500px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
