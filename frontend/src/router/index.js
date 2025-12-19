import { createRouter, createWebHistory } from 'vue-router'
import { isAuthenticated } from '../utils/auth'
import Layout from '../components/Layout.vue'
import UserManagement from '../views/UserManagement.vue'
import ModuleManagement from '../views/ModuleManagement.vue'
import RawDataManagement from '../views/RawDataManagement.vue'
import LogDataManagement from '../views/LogDataManagement.vue'
import ProcessedDataManagement from '../views/ProcessedDataManagement.vue'
import Dashboard from '../views/Dashboard.vue'
import FileManagement from '../views/FileManagement.vue'
import DisasterDecode from '../views/DisasterDecode.vue'
import DisasterData from '../views/DisasterData.vue'
import StorageStrategy from '../views/StorageStrategy.vue'
import DataEviction from '../views/DataEviction.vue'
import MapVisualization from '../views/MapVisualization.vue'
import Login from '../views/Login.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: Dashboard,
        meta: { requiresAuth: true }
      },
      {
        path: '/users',
        name: 'Users',
        component: UserManagement,
        meta: { requiresAuth: true }
      },
      {
        path: '/modules',
        name: 'Modules',
        component: ModuleManagement,
        meta: { requiresAuth: true }
      },
      {
        path: '/raw-data',
        name: 'RawData',
        component: RawDataManagement,
        meta: { requiresAuth: true }
      },
      {
        path: '/log-data',
        name: 'LogData',
        component: LogDataManagement,
        meta: { requiresAuth: true }
      },
      {
        path: '/processed-data',
        name: 'ProcessedData',
        component: ProcessedDataManagement,
        meta: { requiresAuth: true }
      },
      {
        path: '/files',
        name: 'Files',
        component: FileManagement,
        meta: { requiresAuth: true }
      },
      {
        path: '/disaster-decode',
        name: 'DisasterDecode',
        component: DisasterDecode,
        meta: { requiresAuth: true }
      },
      {
        path: '/disaster-data',
        name: 'DisasterData',
        component: DisasterData,
        meta: { requiresAuth: true }
      },
      {
        path: '/storage-strategy',
        name: 'StorageStrategy',
        component: StorageStrategy,
        meta: { requiresAuth: true }
      },
      {
        path: '/data-eviction',
        name: 'DataEviction',
        component: DataEviction,
        meta: { requiresAuth: true }
      },
      {
        path: '/map',
        name: 'MapVisualization',
        component: MapVisualization,
        meta: { requiresAuth: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 如果路由需要认证
  if (to.meta.requiresAuth) {
    // 检查是否已登录
    if (isAuthenticated()) {
      next()
    } else {
      // 未登录，跳转到登录页
      next({
        path: '/login',
        query: { redirect: to.fullPath } // 保存原始路径，登录后可以跳转回来
      })
    }
  } else {
    // 不需要认证的路由（如登录页），如果已登录则跳转到首页
    if (to.path === '/login' && isAuthenticated()) {
      next('/dashboard')
    } else {
      next()
    }
  }
})

export default router
