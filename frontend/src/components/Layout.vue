<template>
  <el-container class="layout-container">
    <!-- 顶部导航栏 -->
    <el-header class="header">
      <div class="header-left">
        <div class="logo">
          <el-icon :size="24"><DataAnalysis /></el-icon>
          <span>多源数据管理系统</span>
        </div>
      </div>
      <div class="header-right">
        <el-dropdown>
          <span class="user-info">
            <el-avatar :size="32" :icon="UserFilled" />
            <span class="username">管理员</span>
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item :icon="User">个人中心</el-dropdown-item>
              <el-dropdown-item :icon="Setting">系统设置</el-dropdown-item>
              <el-dropdown-item divided :icon="SwitchButton">退出登录</el-dropdown-item>
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
          <el-menu-item index="/users">
            <el-icon><User /></el-icon>
            <template #title>用户管理</template>
          </el-menu-item>
          <el-menu-item index="/modules">
            <el-icon><Menu /></el-icon>
            <template #title>模块管理</template>
          </el-menu-item>
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
import { useRoute } from 'vue-router'
import {
  User, Menu, Document, DataAnalysis, Notebook,
  UserFilled, ArrowDown, Setting, SwitchButton,
  Fold, Expand
} from '@element-plus/icons-vue'

const route = useRoute()
const isCollapse = ref(false)

const activeMenu = computed(() => route.path)

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
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
}

.sidebar-menu .el-menu-item {
  height: 50px;
  line-height: 50px;
}

.sidebar-menu .el-menu-item.is-active {
  background: linear-gradient(90deg, #ecf5ff 0%, #fff 100%);
  border-right: 3px solid #409EFF;
}

.main {
  background: #f5f7fa;
  padding: 20px;
  overflow-y: auto;
}
</style>
