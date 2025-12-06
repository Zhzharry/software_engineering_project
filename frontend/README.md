# 多源数据管理系统 - 前端

基于 Vue 3 + Vite 的前端项目，运行在 5173 端口。

## 安装依赖

```bash
cd frontend
npm install
```

## 运行开发服务器

```bash
npm run dev
```

前端将在 `http://localhost:5173` 启动。

## 功能模块

1. **用户管理** - 用户的增删改查
2. **模块管理** - 模块的增删改查
3. **原始数据** - 原始数据的查看和管理
4. **日志数据** - 日志数据的查看和管理
5. **处理数据** - 处理数据的查看和管理

## 技术栈

- Vue 3
- Vue Router 4
- Axios
- Vite

## API 代理

前端通过 Vite 的代理功能将 `/api` 请求转发到后端 `http://localhost:8080`

