import { createRouter, createWebHistory } from 'vue-router'
import UserManagement from '../views/UserManagement.vue'
import ModuleManagement from '../views/ModuleManagement.vue'
import RawDataManagement from '../views/RawDataManagement.vue'
import LogDataManagement from '../views/LogDataManagement.vue'
import ProcessedDataManagement from '../views/ProcessedDataManagement.vue'

const routes = [
  {
    path: '/',
    redirect: '/users'
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
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router

