<template>
  <div class="data-eviction">
    <!-- 概览卡片 -->
    <el-row :gutter="20" class="overview-cards">
      <el-col :span="8">
        <el-card shadow="hover" class="overview-card">
          <div class="overview-content">
            <el-icon :size="40" color="#409EFF"><Document /></el-icon>
            <div class="overview-info">
              <div class="overview-value">{{ statistics.rawDataCount || 0 }}</div>
              <div class="overview-label">原始数据总量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="overview-card">
          <div class="overview-content">
            <el-icon :size="40" color="#67C23A"><DataAnalysis /></el-icon>
            <div class="overview-info">
              <div class="overview-value">{{ statistics.processedDataCount || 0 }}</div>
              <div class="overview-label">处理数据总量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="overview-card">
          <div class="overview-content">
            <el-icon :size="40" color="#E6A23C"><Timer /></el-icon>
            <div class="overview-info">
              <div class="overview-value">{{ statistics.expiredCount || 0 }}</div>
              <div class="overview-label">待清理数据</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 操作卡片 -->
    <el-card shadow="never" class="action-card">
      <template #header>
        <span>数据清理操作</span>
      </template>

      <el-alert
        title="数据清理说明"
        type="warning"
        :closable="false"
        show-icon
        style="margin-bottom: 20px;"
      >
        数据清理操作将永久删除过期数据，此操作不可逆，请谨慎执行。系统已配置定时任务，每天凌晨2点自动执行清理。
      </el-alert>

      <el-row :gutter="20">
        <el-col :span="8">
          <el-card shadow="hover" class="action-item">
            <div class="action-icon">
              <el-icon :size="48" color="#409EFF"><Document /></el-icon>
            </div>
            <h3>清理原始数据</h3>
            <p>清理所有已过期的原始数据（根据expireTime或retentionDays判断）</p>
            <el-button type="primary" @click="cleanRawData" :loading="cleaning.rawData">
              <el-icon><Delete /></el-icon> 执行清理
            </el-button>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover" class="action-item">
            <div class="action-icon">
              <el-icon :size="48" color="#67C23A"><DataAnalysis /></el-icon>
            </div>
            <h3>清理处理数据</h3>
            <p>清理所有已过期的处理结果数据</p>
            <el-button type="success" @click="cleanProcessedData" :loading="cleaning.processedData">
              <el-icon><Delete /></el-icon> 执行清理
            </el-button>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover" class="action-item">
            <div class="action-icon">
              <el-icon :size="48" color="#F56C6C"><DeleteFilled /></el-icon>
            </div>
            <h3>清理所有过期数据</h3>
            <p>同时清理原始数据和处理数据中所有已过期的记录</p>
            <el-button type="danger" @click="cleanAll" :loading="cleaning.all">
              <el-icon><Delete /></el-icon> 全部清理
            </el-button>
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <!-- 按策略清理 -->
    <el-card shadow="never" class="strategy-card">
      <template #header>
        <span>按存储策略清理</span>
      </template>

      <el-form :inline="true">
        <el-form-item label="数据类型">
          <el-select v-model="strategyDataType" placeholder="选择数据类型" style="width: 200px">
            <el-option label="sensor" value="sensor" />
            <el-option label="weather" value="weather" />
            <el-option label="earthquake" value="earthquake" />
            <el-option label="flood" value="flood" />
            <el-option label="fire" value="fire" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="warning" @click="cleanByStrategy" :loading="cleaning.strategy" :disabled="!strategyDataType">
            <el-icon><Setting /></el-icon> 按策略清理
          </el-button>
        </el-form-item>
      </el-form>

      <el-table :data="strategies" style="margin-top: 20px;" v-loading="loadingStrategies">
        <el-table-column prop="strategyName" label="策略名称" />
        <el-table-column prop="dataType" label="数据类型" width="120" />
        <el-table-column prop="retentionDays" label="保留天数" width="100">
          <template #default="{ row }">
            {{ row.retentionDays ? row.retentionDays + '天' : '永久' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 清理历史记录 -->
    <el-card shadow="never" class="history-card">
      <template #header>
        <div class="card-header">
          <span>清理历史记录</span>
          <el-button type="primary" link @click="loadHistory">
            <el-icon><Refresh /></el-icon> 刷新
          </el-button>
        </div>
      </template>

      <el-timeline>
        <el-timeline-item
          v-for="(record, index) in cleanHistory"
          :key="index"
          :timestamp="record.time"
          :type="record.success ? 'success' : 'danger'"
          placement="top"
        >
          <div class="history-content">
            <span class="history-action">{{ record.action }}</span>
            <span class="history-result">{{ record.result }}</span>
          </div>
        </el-timeline-item>
        <el-empty v-if="cleanHistory.length === 0" description="暂无清理记录" />
      </el-timeline>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { dataEvictionAPI, storageStrategyAPI, rawDataAPI, processedDataAPI } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'

// 统计数据
const statistics = reactive({
  rawDataCount: 0,
  processedDataCount: 0,
  expiredCount: 0
})

// 清理状态
const cleaning = reactive({
  rawData: false,
  processedData: false,
  all: false,
  strategy: false
})

// 策略相关
const strategyDataType = ref('')
const strategies = ref([])
const loadingStrategies = ref(false)

// 清理历史
const cleanHistory = ref([])

// 加载统计
const loadStatistics = async () => {
  try {
    // 这里简化处理，实际应该调用专门的统计API
    const [rawRes, processedRes] = await Promise.all([
      rawDataAPI.getAll(),
      processedDataAPI.getAll()
    ])
    if (rawRes.data?.code === 200) {
      statistics.rawDataCount = rawRes.data.data?.length || 0
    }
    if (processedRes.data?.code === 200) {
      statistics.processedDataCount = processedRes.data.data?.length || 0
    }
  } catch (error) {
    console.error('加载统计失败:', error)
  }
}

// 加载策略
const loadStrategies = async () => {
  loadingStrategies.value = true
  try {
    const res = await storageStrategyAPI.getActive()
    if (res.data?.code === 200) {
      strategies.value = res.data.data || []
    }
  } catch (error) {
    console.error('加载策略失败:', error)
  } finally {
    loadingStrategies.value = false
  }
}

// 清理原始数据
const cleanRawData = async () => {
  try {
    await ElMessageBox.confirm('确定要清理所有过期的原始数据吗？此操作不可逆！', '确认清理', {
      type: 'warning'
    })
    cleaning.rawData = true
    const res = await dataEvictionAPI.cleanRawData()
    if (res.data?.code === 200) {
      const count = res.data.data || 0
      ElMessage.success(`清理完成，共删除 ${count} 条原始数据`)
      addHistory('清理原始数据', `删除 ${count} 条`, true)
      loadStatistics()
    } else {
      ElMessage.error(res.data?.message || '清理失败')
      addHistory('清理原始数据', '失败', false)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('清理失败:', error)
      ElMessage.error('清理失败')
    }
  } finally {
    cleaning.rawData = false
  }
}

// 清理处理数据
const cleanProcessedData = async () => {
  try {
    await ElMessageBox.confirm('确定要清理所有过期的处理数据吗？此操作不可逆！', '确认清理', {
      type: 'warning'
    })
    cleaning.processedData = true
    const res = await dataEvictionAPI.cleanProcessedData()
    if (res.data?.code === 200) {
      const count = res.data.data || 0
      ElMessage.success(`清理完成，共删除 ${count} 条处理数据`)
      addHistory('清理处理数据', `删除 ${count} 条`, true)
      loadStatistics()
    } else {
      ElMessage.error(res.data?.message || '清理失败')
      addHistory('清理处理数据', '失败', false)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('清理失败:', error)
      ElMessage.error('清理失败')
    }
  } finally {
    cleaning.processedData = false
  }
}

// 清理所有
const cleanAll = async () => {
  try {
    await ElMessageBox.confirm('确定要清理所有过期数据吗？此操作将同时删除原始数据和处理数据，不可逆！', '确认清理', {
      type: 'error'
    })
    cleaning.all = true
    const res = await dataEvictionAPI.cleanAll()
    if (res.data?.code === 200) {
      const count = res.data.data || 0
      ElMessage.success(`清理完成，共删除 ${count} 条数据`)
      addHistory('清理所有过期数据', `删除 ${count} 条`, true)
      loadStatistics()
    } else {
      ElMessage.error(res.data?.message || '清理失败')
      addHistory('清理所有过期数据', '失败', false)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('清理失败:', error)
      ElMessage.error('清理失败')
    }
  } finally {
    cleaning.all = false
  }
}

// 按策略清理
const cleanByStrategy = async () => {
  if (!strategyDataType.value) {
    ElMessage.warning('请选择数据类型')
    return
  }
  try {
    await ElMessageBox.confirm(`确定要按策略清理 "${strategyDataType.value}" 类型的数据吗？`, '确认清理', {
      type: 'warning'
    })
    cleaning.strategy = true
    const res = await dataEvictionAPI.cleanByStrategy(strategyDataType.value)
    if (res.data?.code === 200) {
      const count = res.data.data || 0
      ElMessage.success(`清理完成，共删除 ${count} 条数据`)
      addHistory(`按策略清理(${strategyDataType.value})`, `删除 ${count} 条`, true)
      loadStatistics()
    } else {
      ElMessage.error(res.data?.message || '清理失败')
      addHistory(`按策略清理(${strategyDataType.value})`, '失败', false)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('清理失败:', error)
      ElMessage.error('清理失败')
    }
  } finally {
    cleaning.strategy = false
  }
}

// 添加历史记录
const addHistory = (action, result, success) => {
  cleanHistory.value.unshift({
    action,
    result,
    success,
    time: new Date().toLocaleString('zh-CN')
  })
  // 保留最近20条
  if (cleanHistory.value.length > 20) {
    cleanHistory.value.pop()
  }
}

// 加载历史
const loadHistory = () => {
  // 本地存储的历史记录
}

onMounted(() => {
  loadStatistics()
  loadStrategies()
})
</script>

<style scoped>
.data-eviction {
  padding: 20px;
}

.overview-cards {
  margin-bottom: 20px;
}

.overview-card {
  height: 120px;
}

.overview-content {
  display: flex;
  align-items: center;
  height: 100%;
  padding: 10px;
}

.overview-info {
  margin-left: 20px;
}

.overview-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.overview-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.action-card {
  margin-bottom: 20px;
}

.action-item {
  text-align: center;
  padding: 20px;
}

.action-icon {
  margin-bottom: 15px;
}

.action-item h3 {
  margin: 0 0 10px 0;
  color: #303133;
}

.action-item p {
  color: #909399;
  font-size: 13px;
  margin-bottom: 15px;
  min-height: 40px;
}

.strategy-card {
  margin-bottom: 20px;
}

.history-card {
  min-height: 300px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.history-content {
  display: flex;
  flex-direction: column;
}

.history-action {
  font-weight: 500;
  color: #303133;
}

.history-result {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}
</style>
