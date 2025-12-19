<template>
  <el-container class="layout-container">
    <!-- 顶部导航栏 -->
    <el-header class="header">
      <div class="header-left">
        <div class="logo">
          <el-icon :size="24"><DataAnalysis /></el-icon>
          <span>灾情数据管理系统</span>
        </div>
      </div>
      <div class="header-right">
        <el-dropdown>
            <span class="user-info">
            <el-avatar :size="32" :icon="UserFilled" />
            <span class="username">{{ userInfo?.username || '管理员' }}</span>
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item :icon="User">个人中心</el-dropdown-item>
              <el-dropdown-item :icon="Setting">系统设置</el-dropdown-item>
              <el-dropdown-item divided :icon="SwitchButton" @click="handleLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>

    <el-container>
      <!-- 左侧菜单 -->
      <el-aside :width="isCollapse ? '64px' : '220px'" class="aside">
        <div class="collapse-btn" @click="toggleCollapse">
          <el-icon :size="20">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
        </div>
        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapse"
          :collapse-transition="false"
          router
          class="sidebar-menu"
        >
          <!-- 数据看板 -->
          <el-menu-item index="/dashboard">
            <el-icon><Odometer /></el-icon>
            <template #title>数据看板</template>
          </el-menu-item>

          <!-- 灾情中心 -->
          <el-sub-menu index="disaster">
            <template #title>
              <el-icon><Warning /></el-icon>
              <span>灾情中心</span>
            </template>
            <el-menu-item index="/disaster-decode">
              <el-icon><Search /></el-icon>
              <template #title>灾情解码</template>
            </el-menu-item>
            <el-menu-item index="/disaster-data">
              <el-icon><DataLine /></el-icon>
              <template #title>灾情查询</template>
            </el-menu-item>
            <el-menu-item index="/map">
              <el-icon><MapLocation /></el-icon>
              <template #title>地图可视化</template>
            </el-menu-item>
          </el-sub-menu>

          <!-- 数据管理 -->
          <el-sub-menu index="data">
            <template #title>
              <el-icon><Folder /></el-icon>
              <span>数据管理</span>
            </template>
            <el-menu-item index="/raw-data">
              <el-icon><Document /></el-icon>
              <template #title>原始数据</template>
            </el-menu-item>
            <el-menu-item index="/processed-data">
              <el-icon><DataAnalysis /></el-icon>
              <template #title>处理数据</template>
            </el-menu-item>
            <el-menu-item index="/log-data">
              <el-icon><Notebook /></el-icon>
              <template #title>日志管理</template>
            </el-menu-item>
          </el-sub-menu>

          <!-- 文件管理 -->
          <el-menu-item index="/files">
            <el-icon><Files /></el-icon>
            <template #title>文件管理</template>
          </el-menu-item>

          <!-- 系统设置 -->
          <el-sub-menu index="system">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>系统设置</span>
            </template>
            <el-menu-item index="/users">
              <el-icon><User /></el-icon>
              <template #title>用户管理</template>
            </el-menu-item>
            <el-menu-item index="/modules">
              <el-icon><Menu /></el-icon>
              <template #title>模块管理</template>
            </el-menu-item>
            <el-menu-item index="/storage-strategy">
              <el-icon><Box /></el-icon>
              <template #title>存储策略</template>
            </el-menu-item>
            <el-menu-item index="/data-eviction">
              <el-icon><Delete /></el-icon>
              <template #title>数据淘汰</template>
            </el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-aside>

      <!-- 主内容区 -->
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  User, Menu, Document, DataAnalysis, Notebook,
  UserFilled, ArrowDown, Setting, SwitchButton,
  Fold, Expand, Odometer, Warning, Search, DataLine,
  Folder, Files, Box, Delete, MapLocation
} from '@element-plus/icons-vue'
import { getUserInfo, clearAuth, isAuthenticated as checkAuth } from '../utils/auth'
import { authAPI } from '../api/index'

const route = useRoute()
const router = useRouter()
const isCollapse = ref(false)

const activeMenu = computed(() => route.path)
const userInfo = computed(() => getUserInfo())
const isAuthenticated = computed(() => checkAuth())

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    // 调用退出登录接口
    try {
      await authAPI.logout()
    } catch (error) {
      console.error('退出登录接口调用失败:', error)
    }

    // 清除本地认证信息
    clearAuth()

    ElMessage.success('已退出登录')
    
    // 跳转到登录页
    router.push('/login')
  } catch (error) {
    // 用户取消
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
  min-width: 1200px;
}

.header {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.header-left {
  display: flex;
  align-items: center;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #fff;
  font-size: 20px;
  font-weight: 600;
}

.logo .el-icon {
  color: #409EFF;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #fff;
  cursor: pointer;
}

.username {
  font-size: 14px;
}

.aside {
  background: #fff;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.05);
  transition: width 0.3s;
  display: flex;
  flex-direction: column;
}

.collapse-btn {
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border-bottom: 1px solid #f0f0f0;
  color: #606266;
  transition: all 0.3s;
}

.collapse-btn:hover {
  color: #409EFF;
  background: #ecf5ff;
}

.sidebar-menu {
  border-right: none;
  flex: 1;
  overflow-y: auto;
}

.sidebar-menu .el-menu-item {
  height: 50px;
  line-height: 50px;
}

.sidebar-menu .el-menu-item.is-active {
  background: linear-gradient(90deg, #ecf5ff 0%, #fff 100%);
  border-right: 3px solid #409EFF;
}

.sidebar-menu .el-sub-menu .el-menu-item {
  padding-left: 50px !important;
}

.main {
  background: #f5f7fa;
  padding: 20px;
  overflow-y: auto;
}
</style>
