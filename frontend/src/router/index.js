import { createRouter, createWebHistory } from 'vue-router'
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

const routes = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: Dashboard
  },
  {
    path: '/users',
    name: 'Users',
    component: UserManagement
  },
  {
    path: '/modules',
    name: 'Modules',
    component: ModuleManagement
  },
  {
    path: '/raw-data',
    name: 'RawData',
    component: RawDataManagement
  },
  {
    path: '/log-data',
    name: 'LogData',
    component: LogDataManagement
  },
  {
    path: '/processed-data',
    name: 'ProcessedData',
    component: ProcessedDataManagement
  },
  {
    path: '/files',
    name: 'Files',
    component: FileManagement
  },
  {
    path: '/disaster-decode',
    name: 'DisasterDecode',
    component: DisasterDecode
  },
  {
    path: '/disaster-data',
    name: 'DisasterData',
    component: DisasterData
  },
  {
    path: '/storage-strategy',
    name: 'StorageStrategy',
    component: StorageStrategy
  },
  {
    path: '/data-eviction',
    name: 'DataEviction',
    component: DataEviction
  },
  {
    path: '/map',
    name: 'MapVisualization',
    component: MapVisualization
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
