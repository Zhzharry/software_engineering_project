<template>
  <div class="dashboard-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
              <el-icon :size="28"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalRawData || 0 }}</div>
              <div class="stat-label">原始数据</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
              <el-icon :size="28"><DataAnalysis /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalProcessedData || 0 }}</div>
              <div class="stat-label">处理数据</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
              <el-icon :size="28"><Folder /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalFiles || 0 }}</div>
              <div class="stat-label">文件数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);">
              <el-icon :size="28"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalUsers || 0 }}</div>
              <div class="stat-label">用户数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>数据类型分布</span>
              <el-tag type="info" size="small">饼图</el-tag>
            </div>
          </template>
          <div class="chart-container">
            <v-chart class="chart" :option="pieChartOption" autoresize />
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>数据趋势</span>
              <el-select v-model="trendDays" size="small" style="width: 100px" @change="loadTrendData">
                <el-option :value="7" label="近7天" />
                <el-option :value="14" label="近14天" />
                <el-option :value="30" label="近30天" />
              </el-select>
            </div>
          </template>
          <div class="chart-container">
            <v-chart class="chart" :option="lineChartOption" autoresize />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 第二行图表 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>数据来源分布</span>
              <el-tag type="info" size="small">柱状图</el-tag>
            </div>
          </template>
          <div class="chart-container">
            <v-chart class="chart" :option="barChartOption" autoresize />
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>处理状态</span>
              <el-tag type="info" size="small">环形图</el-tag>
            </div>
          </template>
          <div class="chart-container">
            <v-chart class="chart" :option="processStatusOption" autoresize />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近活动和快捷操作 -->
    <el-row :gutter="20" class="activity-row">
      <el-col :span="16">
        <el-card shadow="hover" class="activity-card">
          <template #header>
            <div class="card-header">
              <span>最近活动</span>
              <el-button type="primary" link @click="loadRecentActivities">
                <el-icon><Refresh /></el-icon> 刷新
              </el-button>
            </div>
          </template>
          <el-timeline>
            <el-timeline-item
              v-for="(activity, index) in recentActivities"
              :key="index"
              :timestamp="activity.time"
              :type="getActivityType(activity.type)"
              placement="top"
            >
              <div class="activity-content">
                <span class="activity-action">{{ activity.action }}</span>
                <span class="activity-detail">{{ activity.detail }}</span>
              </div>
            </el-timeline-item>
            <el-empty v-if="recentActivities.length === 0" description="暂无活动记录" />
          </el-timeline>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="quick-actions-card">
          <template #header>
            <span>快捷操作</span>
          </template>
          <div class="quick-actions">
            <el-button type="primary" @click="$router.push('/disaster-decode')">
              <el-icon><Search /></el-icon> 灾情解码
            </el-button>
            <el-button type="success" @click="$router.push('/files')">
              <el-icon><Upload /></el-icon> 上传文件
            </el-button>
            <el-button type="warning" @click="$router.push('/disaster-data')">
              <el-icon><DataLine /></el-icon> 数据查询
            </el-button>
            <el-button type="info" @click="$router.push('/raw-data')">
              <el-icon><Document /></el-icon> 原始数据
            </el-button>
          </div>
        </el-card>

        <!-- 文件统计 -->
        <el-card shadow="hover" class="file-stats-card" style="margin-top: 20px;">
          <template #header>
            <span>文件统计</span>
          </template>
          <div class="file-stats">
            <div class="file-stat-item">
              <el-icon :size="20" color="#409EFF"><Picture /></el-icon>
              <span class="file-stat-label">图片</span>
              <span class="file-stat-value">{{ fileStats.image || 0 }}</span>
            </div>
            <div class="file-stat-item">
              <el-icon :size="20" color="#67C23A"><Document /></el-icon>
              <span class="file-stat-label">文档</span>
              <span class="file-stat-value">{{ fileStats.document || 0 }}</span>
            </div>
            <div class="file-stat-item">
              <el-icon :size="20" color="#E6A23C"><VideoCamera /></el-icon>
              <span class="file-stat-label">视频</span>
              <span class="file-stat-value">{{ fileStats.video || 0 }}</span>
            </div>
            <div class="file-stat-item">
              <el-icon :size="20" color="#F56C6C"><Headset /></el-icon>
              <span class="file-stat-label">音频</span>
              <span class="file-stat-value">{{ fileStats.audio || 0 }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { PieChart, LineChart, BarChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from 'echarts/components'
import VChart from 'vue-echarts'
import { dashboardAPI, fileAPI } from '../api'
import { ElMessage } from 'element-plus'

// 注册 ECharts 组件
use([
  CanvasRenderer,
  PieChart,
  LineChart,
  BarChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
])

// 统计数据
const statistics = reactive({
  totalRawData: 0,
  totalProcessedData: 0,
  totalFiles: 0,
  totalUsers: 0
})

// 趋势天数
const trendDays = ref(7)

// 文件统计
const fileStats = reactive({
  image: 0,
  document: 0,
  video: 0,
  audio: 0
})

// 最近活动
const recentActivities = ref([])

// 图表数据
const chartData = reactive({
  dataTypeDistribution: { labels: [], values: [] },
  trendData: { dates: [], rawData: [], processedData: [] },
  sourceDistribution: {},
  processStatus: {}
})

// 饼图配置
const pieChartOption = computed(() => ({
  tooltip: {
    trigger: 'item',
    formatter: '{b}: {c} ({d}%)'
  },
  legend: {
    orient: 'vertical',
    left: 'left',
    top: 'center'
  },
  series: [
    {
      name: '数据类型',
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['60%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: false,
        position: 'center'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 16,
          fontWeight: 'bold'
        }
      },
      labelLine: {
        show: false
      },
      data: chartData.dataTypeDistribution.labels.map((label, index) => ({
        value: chartData.dataTypeDistribution.values[index],
        name: label
      }))
    }
  ]
}))

// 折线图配置
const lineChartOption = computed(() => ({
  tooltip: {
    trigger: 'axis'
  },
  legend: {
    data: ['原始数据', '处理数据'],
    bottom: 0
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '15%',
    top: '10%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: chartData.trendData.dates
  },
  yAxis: {
    type: 'value'
  },
  series: [
    {
      name: '原始数据',
      type: 'line',
      smooth: true,
      areaStyle: {
        opacity: 0.3
      },
      data: chartData.trendData.rawData
    },
    {
      name: '处理数据',
      type: 'line',
      smooth: true,
      areaStyle: {
        opacity: 0.3
      },
      data: chartData.trendData.processedData
    }
  ]
}))

// 柱状图配置
const barChartOption = computed(() => {
  const labels = Object.keys(chartData.sourceDistribution)
  const values = Object.values(chartData.sourceDistribution)
  return {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '10%',
      top: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: labels,
      axisLabel: {
        interval: 0,
        rotate: 30
      }
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        type: 'bar',
        data: values,
        itemStyle: {
          borderRadius: [4, 4, 0, 0],
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              { offset: 0, color: '#667eea' },
              { offset: 1, color: '#764ba2' }
            ]
          }
        }
      }
    ]
  }
})

// 处理状态环形图配置
const processStatusOption = computed(() => {
  const data = Object.entries(chartData.processStatus).map(([name, value]) => ({
    name,
    value
  }))
  return {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      top: 'center'
    },
    series: [
      {
        type: 'pie',
        radius: ['50%', '70%'],
        center: ['60%', '50%'],
        label: {
          show: true,
          formatter: '{b}\n{d}%'
        },
        data,
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }
})

// 获取活动类型颜色
const getActivityType = (type) => {
  const types = {
    create: 'success',
    update: 'warning',
    delete: 'danger',
    process: 'primary'
  }
  return types[type] || 'info'
}

// 加载统计数据
const loadStatistics = async () => {
  try {
    const res = await dashboardAPI.getStatistics()
    if (res.data?.code === 200) {
      Object.assign(statistics, res.data.data)
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

// 加载图表数据
const loadChartData = async () => {
  try {
    const res = await dashboardAPI.getChartData()
    if (res.data?.code === 200) {
      const data = res.data.data
      if (data.dataTypeDistribution) {
        chartData.dataTypeDistribution = data.dataTypeDistribution
      }
      if (data.trendChart) {
        chartData.trendData = data.trendChart
      }
      if (data.sourceDistribution) {
        chartData.sourceDistribution = data.sourceDistribution
      }
      if (data.processStatusDistribution) {
        chartData.processStatus = data.processStatusDistribution
      }
    }
  } catch (error) {
    console.error('加载图表数据失败:', error)
  }
}

// 加载趋势数据
const loadTrendData = async () => {
  try {
    const res = await dashboardAPI.getTrendChart(trendDays.value)
    if (res.data?.code === 200) {
      chartData.trendData = res.data.data
    }
  } catch (error) {
    console.error('加载趋势数据失败:', error)
  }
}

// 加载文件统计
const loadFileStatistics = async () => {
  try {
    const res = await fileAPI.getStatistics()
    if (res.data?.code === 200 && res.data.data?.typeCount) {
      Object.assign(fileStats, res.data.data.typeCount)
    }
  } catch (error) {
    console.error('加载文件统计失败:', error)
  }
}

// 加载最近活动
const loadRecentActivities = async () => {
  try {
    const res = await dashboardAPI.getRecentActivities()
    if (res.data?.code === 200 && res.data.data) {
      recentActivities.value = res.data.data
    }
  } catch (error) {
    console.error('加载最近活动失败:', error)
    ElMessage.error('加载最近活动失败')
    // 如果接口失败，保持空数组，不显示模拟数据
    recentActivities.value = []
  }
}

// 页面加载
onMounted(() => {
  loadStatistics()
  loadChartData()
  loadFileStatistics()
  loadRecentActivities()
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.stat-cards {
  margin-bottom: 20px;
}

.stat-card {
  height: 120px;
}

.stat-content {
  display: flex;
  align-items: center;
  height: 100%;
  padding: 10px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-right: 20px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.chart-row {
  margin-bottom: 20px;
}

.chart-card {
  height: 380px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-container {
  height: 300px;
}

.chart {
  width: 100%;
  height: 100%;
}

.activity-row {
  margin-bottom: 20px;
}

.activity-card {
  min-height: 300px;
}

.activity-content {
  display: flex;
  flex-direction: column;
}

.activity-action {
  font-weight: 500;
  color: #303133;
}

.activity-detail {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}

.quick-actions-card {
  min-height: 200px;
}

.quick-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.quick-actions .el-button {
  flex: 1 1 45%;
  min-width: 100px;
}

.file-stats-card {
  min-height: 180px;
}

.file-stats {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.file-stat-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.file-stat-label {
  flex: 1;
  color: #606266;
}

.file-stat-value {
  font-weight: bold;
  color: #303133;
}
</style>
