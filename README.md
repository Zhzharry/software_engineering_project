# 多源数据管理系统

灾情/危机数据管理系统，支持多源数据的存储、处理和查询。

## 快速运行指南

### 方式一：Docker 部署（推荐）

**一键启动所有服务，无需手动配置数据库：**

```bash
# 启动所有服务（MySQL + MongoDB + 后端 + 前端）
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

访问地址：
| 服务 | 地址 |
|------|------|
| 前端界面 | http://localhost |
| 后端 API | http://localhost:8080 |
| MySQL | localhost:3306 |
| MongoDB | localhost:27017 |

**常用 Docker 命令：**
```bash
# 停止所有服务
docker-compose down

# 重新构建并启动
docker-compose up -d --build

# 停止并删除数据卷（会清空数据库数据）
docker-compose down -v
```

> **注意**：首次启动前，请确保 Docker Desktop 已运行，并配置了镜像加速器（详见下方"Docker 镜像加速"章节）。

---

### 方式二：仅运行前端（Mock 模式）

**无需配置数据库**，前端内置 Mock 数据可独立运行：

```bash
cd frontend
npm install
npm run dev
```

访问 http://localhost:5173 即可看到完整界面。

---

### 方式三：本地完整运行（前端 + 后端 + 数据库）

#### 第一步：启动数据库

**MySQL：**
```bash
# 1. 启动 MySQL 服务
# 2. 创建数据库并初始化
mysql -u root -p < scripts/init_database.sql
```

**MongoDB：**
```bash
# 启动 MongoDB 服务（默认端口 27017）
mongod
```

#### 第二步：配置后端

编辑 `src/main/resources/application.yml`，修改数据库连接：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/zaiqing
    username: root
    password: 你的密码
  data:
    mongodb:
      uri: mongodb://localhost:27017/module_db
```

#### 第三步：启动后端

```bash
# 方式一：Maven 运行
mvn spring-boot:run

# 方式二：打包后运行
mvn clean package -DskipTests
java -jar target/module-1.0.0.jar
```

后端运行在 http://localhost:8080

#### 第四步：启动前端（连接真实后端）

```bash
cd frontend
npm install

# 创建环境配置，关闭 Mock
echo "VITE_USE_MOCK=false" > .env.local

npm run dev
```

前端运行在 http://localhost:5173

---

## 项目结构

```
├── src/                    # 后端代码 (Spring Boot)
│   └── main/
│       ├── java/           # Java 源码
│       └── resources/      # 配置文件
├── frontend/               # 前端代码 (Vue 3)
│   ├── src/
│   │   ├── api/           # API 接口
│   │   ├── mock/          # Mock 数据
│   │   ├── views/         # 页面组件
│   │   └── components/    # 公共组件
│   ├── Dockerfile         # 前端 Docker 构建文件
│   ├── nginx.conf         # Nginx 配置（生产环境）
│   └── package.json
├── scripts/
│   └── init_database.sql  # 数据库初始化脚本
├── Dockerfile             # 后端 Docker 构建文件
├── docker-compose.yml     # Docker Compose 编排文件
├── pom.xml                # Maven 配置
└── README.md
```

## 技术栈

| 层级 | 技术 |
|------|------|
| 前端 | Vue 3 + Element Plus + Vite |
| 后端 | Spring Boot 2.7 + Spring Data JPA/MongoDB |
| 数据库 | MySQL 5.7 / MongoDB 4.4 |
| 容器化 | Docker + Docker Compose |
| Web 服务器 | Nginx（生产环境） |

## 功能模块

- **用户管理** - 用户增删改查、状态管理
- **模块管理** - 系统模块配置
- **原始数据** - 多源数据采集与存储
- **处理数据** - 数据处理与置信度评估
- **日志管理** - 系统日志查看与统计

## 端口说明

| 服务 | 开发环境端口 | Docker 环境端口 |
|------|-------------|-----------------|
| 前端 | 5173 | 80 |
| 后端 | 8080 | 8080 |
| MySQL | 3306 | 3306 |
| MongoDB | 27017 | 27017 |

## 环境要求

### 本地开发
- Node.js 16+
- JDK 1.8+
- Maven 3.6+
- MySQL 5.7+
- MongoDB 4.0+

### Docker 部署
- Docker Desktop 4.0+
- Docker Compose V2

---

## Docker 镜像加速

由于网络原因，拉取 Docker 镜像可能较慢。建议配置镜像加速器：

1. 打开 **Docker Desktop** → **Settings** → **Docker Engine**
2. 在 JSON 配置中添加：

```json
{
  "registry-mirrors": [
    "https://docker.1ms.run",
    "https://docker.xuanyuan.me"
  ]
}
```

3. 点击 **Apply & Restart**

---

## 常见问题

**Q: Docker 启动时报 `connection timeout` 错误？**
A: Docker Hub 连接超时，请配置镜像加速器（见上方说明）

**Q: 前端启动后页面空白？**
A: 检查是否执行了 `npm install`

**Q: 前端显示"加载数据失败"？**
A:
- Docker 模式：检查后端容器是否正常运行 `docker-compose ps`
- 开发模式：如果配置了 `VITE_USE_MOCK=false`，需要确保后端已启动

**Q: 后端启动失败？**
A: 检查 MySQL 和 MongoDB 是否启动，以及 `application.yml` 中的连接配置是否正确

**Q: Docker 环境中文显示乱码？**
A: 已在 docker-compose.yml 中配置 MySQL 强制使用 UTF-8 字符集。如仍有问题，执行：
```bash
docker-compose down -v
docker-compose up -d
```

**Q: 如何查看后端日志？**
A:
```bash
# Docker 环境
docker logs -f zaiqing-backend

# 本地环境
# 日志直接输出到控制台
```

---

## API 文档

详见 [scripts/接口测试文档.md](./scripts/接口测试文档.md)

## 数据库配置

详见 [README_数据库配置说明.md](./README_数据库配置说明.md)

---

## 更新日志

### 2025-12-07
- 添加 Docker 部署支持（Dockerfile, docker-compose.yml）
- 修复 MySQL 中文乱码问题（配置强制 UTF-8 字符集）
- 修复原始数据 API 缺少 GET 全部数据接口的问题
- 修复前端模块管理字段映射问题（moduleName → name）
- 添加 Nginx 反向代理配置
