<template>
  <div class="map-visualization">
    <!-- 顶部工具栏 -->
    <el-card shadow="never" class="toolbar-card">
      <div class="toolbar">
        <div class="filter-group">
          <el-select v-model="filters.disasterCategory" placeholder="灾情类别" clearable style="width: 140px">
            <el-option label="震情" value="震情" />
            <el-option label="人员伤亡及失踪" value="人员伤亡及失踪" />
            <el-option label="房屋破坏" value="房屋破坏" />
            <el-option label="生命线工程灾情" value="生命线工程灾情" />
            <el-option label="次生灾害" value="次生灾害" />
          </el-select>
          <el-select v-model="filters.source" placeholder="数据来源" clearable style="width: 140px">
            <el-option label="业务报送" value="业务报送" />
            <el-option label="传感器监测" value="传感器监测" />
            <el-option label="互联网感知" value="互联网感知" />
            <el-option label="卫星遥感" value="卫星遥感" />
            <el-option label="现场调查" value="现场调查" />
          </el-select>
          <el-button type="primary" @click="loadDisasterPoints" :loading="loading">
            <el-icon><Search /></el-icon> 筛选
          </el-button>
          <el-button @click="resetFilters">
            <el-icon><Refresh /></el-icon> 重置
          </el-button>
        </div>
        <div class="action-group">
          <el-button-group>
            <el-button :type="viewMode === 'point' ? 'primary' : ''" @click="setViewMode('point')">
              <el-icon><Location /></el-icon> 点位
            </el-button>
            <el-button :type="viewMode === 'cluster' ? 'primary' : ''" @click="setViewMode('cluster')">
              <el-icon><Grid /></el-icon> 聚合
            </el-button>
            <el-button :type="viewMode === 'heatmap' ? 'primary' : ''" @click="setViewMode('heatmap')">
              <el-icon><DataLine /></el-icon> 热力
            </el-button>
          </el-button-group>
        </div>
      </div>
    </el-card>

    <!-- 地图容器 -->
    <div class="map-container">
      <div id="amap-container" ref="mapContainer"></div>

      <!-- 地图信息面板 -->
      <div class="info-panel" v-if="overview">
        <h4>数据概览</h4>
        <div class="info-item">
          <span class="label">灾情点位</span>
          <span class="value">{{ overview.totalPoints || 0 }}</span>
        </div>
        <div class="info-item">
          <span class="label">涉及省份</span>
          <span class="value">{{ overview.provincesAffected || 0 }}</span>
        </div>
        <div class="info-item">
          <span class="label">涉及城市</span>
          <span class="value">{{ overview.citiesAffected || 0 }}</span>
        </div>
        <div class="info-divider"></div>
        <h4>类别分布</h4>
        <div class="category-list">
          <div v-for="(count, category) in overview.categoryDistribution" :key="category" class="category-item">
            <span class="category-name">{{ category }}</span>
            <span class="category-count">{{ count }}</span>
          </div>
        </div>
      </div>

      <!-- 图例 -->
      <div class="legend-panel">
        <h4>图例</h4>
        <div class="legend-item">
          <span class="legend-marker" style="background: #F56C6C"></span>
          <span>震情</span>
        </div>
        <div class="legend-item">
          <span class="legend-marker" style="background: #E6A23C"></span>
          <span>人员伤亡</span>
        </div>
        <div class="legend-item">
          <span class="legend-marker" style="background: #409EFF"></span>
          <span>房屋破坏</span>
        </div>
        <div class="legend-item">
          <span class="legend-marker" style="background: #67C23A"></span>
          <span>生命线工程</span>
        </div>
        <div class="legend-item">
          <span class="legend-marker" style="background: #909399"></span>
          <span>次生灾害</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, watch } from 'vue'
import { mapAPI } from '../api'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Location, Grid, DataLine } from '@element-plus/icons-vue'

// 高德地图实例
let map = null
let markers = []
let markerCluster = null
let heatmap = null

// 状态
const mapContainer = ref(null)
const loading = ref(false)
const viewMode = ref('point')
const overview = ref(null)
const disasterPoints = ref([])

// 筛选条件
const filters = reactive({
  disasterCategory: '',
  source: '',
  carrierType: ''
})

// 灾情类别对应的颜色
const categoryColors = {
  '震情': '#F56C6C',
  '人员伤亡及失踪': '#E6A23C',
  '房屋破坏': '#409EFF',
  '生命线工程灾情': '#67C23A',
  '次生灾害': '#909399'
}

// 初始化地图
const initMap = () => {
  console.log('开始初始化地图...')
  
  // 检查是否已经加载过高德地图脚本
  if (window.AMap) {
    console.log('AMap对象已存在，直接创建地图')
    createMap()
    return
  }

  // 检查是否正在加载
  const existingScript = document.querySelector('script[src*="webapi.amap.com"]')
  if (existingScript) {
    console.log('地图脚本已存在，等待AMap对象初始化...')
    // 如果脚本已存在但AMap未加载，等待加载完成
    let checkCount = 0
    const maxChecks = 100 // 最多检查10秒
    const checkAMap = setInterval(() => {
      checkCount++
      if (window.AMap) {
        console.log('AMap对象初始化成功')
        clearInterval(checkAMap)
        createMap()
      } else if (checkCount >= maxChecks) {
        console.error('等待AMap对象超时')
        clearInterval(checkAMap)
        ElMessage.error('地图加载超时，请刷新页面重试')
      }
    }, 100)
    return
  }

  // 设置安全密钥（Web端API需要配置安全密钥）
  // 前端使用Web端（JS API）类型的密钥
  window._AMapSecurityConfig = {
    securityJsCode: 'fd075bd4dc91d1b2215acd9d4f625ff5' // 前端API的安全密钥
  }

  console.log('开始加载高德地图脚本...')
  
  // 加载高德地图脚本
  const script = document.createElement('script')
  script.type = 'text/javascript'
  script.async = true
  script.defer = true
  
  // 使用前端Web端（JS API）类型的密钥
  // 注意：这个密钥专门用于前端地图显示，不能用于后端服务
  const apiKey = '667083e32d238add0a83812184ecb210' // 前端Web端API密钥
  script.src = `https://webapi.amap.com/maps?v=2.0&key=${apiKey}&plugin=AMap.MarkerCluster,AMap.HeatMap,AMap.Geocoder`
  
  script.onload = () => {
    console.log('高德地图脚本加载成功，等待AMap对象初始化...')
    // 等待AMap对象完全初始化
    let checkCount = 0
    const maxChecks = 50 // 最多检查5秒
    const checkAMap = setInterval(() => {
      checkCount++
      if (window.AMap) {
        console.log('AMap对象初始化成功，开始创建地图实例')
        clearInterval(checkAMap)
        createMap()
      } else if (checkCount >= maxChecks) {
        console.error('AMap对象初始化超时')
        clearInterval(checkAMap)
        ElMessage.error('地图初始化失败：AMap对象未找到。请检查API密钥是否正确')
      }
    }, 100)
  }
  
  script.onerror = (error) => {
    console.error('地图脚本加载失败:', error)
    console.error('失败详情:', {
      src: script.src,
      readyState: script.readyState,
      error: error
    })
    ElMessage.error('地图脚本加载失败，可能的原因：1. 网络连接问题 2. API密钥无效 3. 跨域限制。请检查浏览器控制台获取详细信息')
  }
  
  // 添加脚本到页面
  document.head.appendChild(script)
  console.log('地图脚本已添加到页面，URL:', script.src)
}

// 创建地图实例
const createMap = () => {
  try {
    // 确保容器存在且有高度
    const container = document.getElementById('amap-container')
    if (!container) {
      console.error('地图容器不存在')
      ElMessage.error('地图容器不存在')
      return
    }

    // 如果容器没有高度，设置默认高度
    if (!container.offsetHeight || container.offsetHeight < 100) {
      const parentHeight = container.parentElement?.offsetHeight || 600
      container.style.height = `${Math.max(parentHeight - 20, 600)}px`
      console.warn('地图容器高度不足，已设置高度:', container.style.height)
    }

    console.log('开始创建地图实例')
    console.log('容器信息:', {
      id: container.id,
      width: container.offsetWidth,
      height: container.offsetHeight,
      parentWidth: container.parentElement?.offsetWidth,
      parentHeight: container.parentElement?.offsetHeight
    })

    // 检查AMap对象
    if (!window.AMap) {
      console.error('AMap对象不存在，无法创建地图')
      ElMessage.error('地图API未加载，请刷新页面重试')
      return
    }

    // 创建地图实例
    map = new window.AMap.Map('amap-container', {
      zoom: 5,
      center: [104.065735, 35.738353], // 中国中心点（经度，纬度）
      mapStyle: 'amap://styles/light',
      resizeEnable: true,
      viewMode: '2D', // 使用2D视图（3D可能在某些情况下不兼容）
      lang: 'zh_cn' // 中文
    })

    console.log('地图实例创建成功')

    // 地图加载完成后加载数据
    map.on('complete', () => {
      console.log('地图加载完成，开始加载数据')
      loadOverview()
      loadDisasterPoints()
    })

    // 监听地图错误
    map.on('error', (error) => {
      console.error('地图错误:', error)
      const errorMsg = error?.message || error?.toString() || '未知错误'
      console.error('错误详情:', error)
      
      // 根据错误类型给出提示
      if (errorMsg.includes('USERKEY_PLAT_NOMATCH')) {
        ElMessage.error('API密钥平台类型不匹配，请使用Web端（JS API）类型的密钥')
      } else if (errorMsg.includes('INVALID_USER_KEY')) {
        ElMessage.error('API密钥无效，请检查密钥是否正确')
      } else {
        ElMessage.error('地图加载出错: ' + errorMsg)
      }
    })

    // 监听地图加载进度
    map.on('load', () => {
      console.log('地图瓦片加载完成')
    })

  } catch (error) {
    console.error('创建地图实例失败:', error)
    console.error('错误堆栈:', error.stack)
    ElMessage.error('创建地图失败: ' + (error.message || error.toString()))
  }
}

// 加载概览数据
const loadOverview = async () => {
  try {
    const res = await mapAPI.getMapOverview()
    if (res.data?.code === 200) {
      overview.value = res.data.data
    }
  } catch (error) {
    console.error('加载概览失败:', error)
  }
}

// 加载灾情点位
const loadDisasterPoints = async () => {
  if (!map) return

  loading.value = true
  try {
    const params = {}
    if (filters.disasterCategory) params.disasterCategory = filters.disasterCategory
    if (filters.source) params.source = filters.source
    if (filters.carrierType) params.carrierType = filters.carrierType

    const res = await mapAPI.getDisasterPoints(params)
    if (res.data?.code === 200) {
      disasterPoints.value = res.data.data || []
      renderMarkers()
    }
  } catch (error) {
    console.error('加载灾情点位失败:', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 渲染标记点
const renderMarkers = () => {
  clearMarkers()

  if (viewMode.value === 'point') {
    renderPointMarkers()
  } else if (viewMode.value === 'cluster') {
    renderClusterMarkers()
  } else if (viewMode.value === 'heatmap') {
    renderHeatmap()
  }
}

// 渲染点位标记
const renderPointMarkers = () => {
  disasterPoints.value.forEach(point => {
    if (!point.longitude || !point.latitude) return

    const color = categoryColors[point.disasterCategory] || '#409EFF'

    const marker = new AMap.Marker({
      position: [point.longitude, point.latitude],
      content: `<div style="
        width: 12px;
        height: 12px;
        background: ${color};
        border: 2px solid #fff;
        border-radius: 50%;
        box-shadow: 0 2px 6px rgba(0,0,0,0.3);
      "></div>`,
      offset: new AMap.Pixel(-6, -6)
    })

    // 点击事件
    marker.on('click', () => {
      showInfoWindow(point)
    })

    marker.setMap(map)
    markers.push(marker)
  })
}

// 渲染聚合标记
const renderClusterMarkers = () => {
  const points = disasterPoints.value
    .filter(p => p.longitude && p.latitude)
    .map(p => ({
      lnglat: [p.longitude, p.latitude],
      ...p
    }))

  markerCluster = new AMap.MarkerCluster(map, points, {
    gridSize: 60,
    renderClusterMarker: (context) => {
      const count = context.count
      const div = document.createElement('div')
      div.style.cssText = `
        width: ${Math.min(40 + count * 2, 80)}px;
        height: ${Math.min(40 + count * 2, 80)}px;
        background: rgba(64, 158, 255, 0.8);
        border: 2px solid #fff;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #fff;
        font-weight: bold;
        font-size: 14px;
        box-shadow: 0 2px 8px rgba(0,0,0,0.3);
      `
      div.innerHTML = count
      context.marker.setContent(div)
    }
  })
}

// 渲染热力图
const renderHeatmap = () => {
  const heatmapData = disasterPoints.value
    .filter(p => p.longitude && p.latitude)
    .map(p => ({
      lng: p.longitude,
      lat: p.latitude,
      count: 1
    }))

  map.plugin(['AMap.HeatMap'], () => {
    heatmap = new AMap.HeatMap(map, {
      radius: 25,
      opacity: [0, 0.8],
      gradient: {
        0.4: 'blue',
        0.6: 'green',
        0.8: 'yellow',
        1.0: 'red'
      }
    })
    heatmap.setDataSet({
      data: heatmapData,
      max: 10
    })
  })
}

// 显示信息窗口
const showInfoWindow = (point) => {
  const content = `
    <div style="padding: 10px; min-width: 200px;">
      <h4 style="margin: 0 0 10px 0; color: #303133;">${point.disasterCategory || '未知类别'}</h4>
      <p style="margin: 5px 0; color: #606266; font-size: 13px;">
        <strong>子类别：</strong>${point.disasterSubcategory || '-'}
      </p>
      <p style="margin: 5px 0; color: #606266; font-size: 13px;">
        <strong>数据来源：</strong>${point.source || '-'}
      </p>
      <p style="margin: 5px 0; color: #606266; font-size: 13px;">
        <strong>载体类型：</strong>${point.carrierType || '-'}
      </p>
      <p style="margin: 5px 0; color: #606266; font-size: 13px;">
        <strong>位置：</strong>${point.province || ''}${point.city || ''}
      </p>
      <p style="margin: 5px 0; color: #909399; font-size: 12px;">
        经度: ${point.longitude?.toFixed(6)}, 纬度: ${point.latitude?.toFixed(6)}
      </p>
    </div>
  `

  const infoWindow = new AMap.InfoWindow({
    content: content,
    offset: new AMap.Pixel(0, -10)
  })

  infoWindow.open(map, [point.longitude, point.latitude])
}

// 清除标记
const clearMarkers = () => {
  markers.forEach(m => m.setMap(null))
  markers = []

  if (markerCluster) {
    markerCluster.setMap(null)
    markerCluster = null
  }

  if (heatmap) {
    heatmap.setMap(null)
    heatmap = null
  }
}

// 设置视图模式
const setViewMode = (mode) => {
  viewMode.value = mode
  renderMarkers()
}

// 重置筛选
const resetFilters = () => {
  filters.disasterCategory = ''
  filters.source = ''
  filters.carrierType = ''
  loadDisasterPoints()
}

// 监听视图模式变化
watch(viewMode, () => {
  renderMarkers()
})

onMounted(() => {
  console.log('MapVisualization组件已挂载')
  console.log('容器元素:', document.getElementById('amap-container'))
  
  // 确保DOM完全渲染后再初始化地图
  // 使用nextTick确保Vue完成DOM更新
  setTimeout(() => {
    const container = document.getElementById('amap-container')
    if (container) {
      console.log('容器已找到，开始初始化地图')
      initMap()
    } else {
      console.error('容器未找到，延迟重试...')
      // 如果容器还没准备好，再等一会儿
      setTimeout(() => {
        const retryContainer = document.getElementById('amap-container')
        if (retryContainer) {
          console.log('容器已找到（重试），开始初始化地图')
          initMap()
        } else {
          console.error('容器仍未找到，请检查DOM结构')
          ElMessage.error('地图容器未找到，请刷新页面')
        }
      }, 500)
    }
  }, 200)
})

onUnmounted(() => {
  clearMarkers()
  if (map) {
    map.destroy()
    map = null
  }
})
</script>

<style scoped>
.map-visualization {
  height: calc(100vh - 120px);
  min-height: 600px;
  display: flex;
  flex-direction: column;
  padding: 20px;
  box-sizing: border-box;
}

.toolbar-card {
  margin-bottom: 15px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filter-group {
  display: flex;
  gap: 10px;
  align-items: center;
}

.action-group {
  display: flex;
  gap: 10px;
}

.map-container {
  flex: 1;
  position: relative;
  border-radius: 4px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  min-height: 600px;
}

#amap-container {
  width: 100%;
  height: 100%;
  min-height: 600px;
}

.info-panel {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 200px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 8px;
  padding: 15px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
}

.info-panel h4 {
  margin: 0 0 10px 0;
  font-size: 14px;
  color: #303133;
}

.info-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.info-item .label {
  color: #909399;
  font-size: 13px;
}

.info-item .value {
  color: #303133;
  font-weight: 600;
  font-size: 14px;
}

.info-divider {
  height: 1px;
  background: #ebeef5;
  margin: 12px 0;
}

.category-list {
  max-height: 150px;
  overflow-y: auto;
}

.category-item {
  display: flex;
  justify-content: space-between;
  padding: 4px 0;
  font-size: 12px;
}

.category-name {
  color: #606266;
}

.category-count {
  color: #409EFF;
  font-weight: 500;
}

.legend-panel {
  position: absolute;
  bottom: 20px;
  left: 10px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 8px;
  padding: 12px 15px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
}

.legend-panel h4 {
  margin: 0 0 8px 0;
  font-size: 13px;
  color: #303133;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 5px;
  font-size: 12px;
  color: #606266;
}

.legend-marker {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  border: 1px solid #fff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.2);
}
</style>
