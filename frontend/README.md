# 多源数据管理系统 - 前端

基于 Vue 3 + Element Plus + Vite 构建的前端项目。

## 目录结构

```
frontend/
├── src/
│   ├── api/            # API 接口封装
│   ├── components/     # 公共组件（Layout 布局）
│   ├── mock/           # Mock 模拟数据
│   ├── router/         # 路由配置
│   ├── views/          # 页面组件（5个管理页面）
│   ├── App.vue         # 根组件
│   ├── main.js         # 入口文件
│   └── style.css       # 全局样式
├── .env.example        # 环境变量示例
├── index.html          # HTML 模板
├── package.json        # 依赖配置
└── vite.config.js      # Vite 配置
```

## 快速开始

```bash
# 1. 安装依赖
npm install

# 2. 启动开发服务器
npm run dev

# 3. 访问 http://localhost:5173
```

## 运行模式

| 命令 | 模式 | 数据来源 | 用途 |
|------|------|----------|------|
| `npm run dev` | 开发模式 | Mock 模拟数据 | 前端独立开发，无需后端 |
| `npm run build` | 生产构建 | 真实后端 API | 部署上线 |

### 切换数据源

创建 `.env.local` 文件：

```bash
# 强制使用真实 API（联调时使用）
VITE_USE_MOCK=false

# 强制使用 Mock 数据
VITE_USE_MOCK=true
```

## 功能页面

| 页面 | 路径 | 功能 |
|------|------|------|
| 用户管理 | `/users` | 用户 CRUD、状态切换 |
| 模块管理 | `/modules` | 模块 CRUD、状态管理 |
| 原始数据 | `/raw-data` | 数据查看、标记处理、JSON 详情 |
| 处理数据 | `/processed-data` | 置信度管理、颜色进度条 |
| 日志管理 | `/log-data` | 日志统计、级别筛选 |

## 技术栈

- Vue 3.3 - 前端框架
- Element Plus 2.4 - UI 组件库
- Vue Router 4 - 路由管理
- Axios - HTTP 请求
- Vite 5 - 构建工具

## 与后端联调

1. 确保后端运行在 `http://localhost:8080`
2. 创建 `.env.local`，设置 `VITE_USE_MOCK=false`
3. 运行 `npm run dev`
4. Vite 会自动将 `/api` 请求代理到后端

## 生产构建

```bash
npm run build
```

构建产物在 `dist/` 目录，可部署到 Nginx 等 Web 服务器。
