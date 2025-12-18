# 灾情数据管理系统

多源灾情数据管理系统，支持灾情数据的存储、解码、查询、可视化和管理。

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

---

### 方式二：本地完整运行（前端 + 后端 + 数据库）

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

#### 第四步：启动前端

```bash
cd frontend
npm install
npm run dev
```

前端运行在 http://localhost:5173

---

## 功能模块

### 数据看板
- 系统数据概览统计
- 数据类型分布图表
- 数据趋势分析
- 最近活动记录

### 灾情中心
- **灾情解码** - 36位灾情ID解码、批量解码、Excel文件解码、地理位置查询
- **灾情查询** - 按类别/来源/载体/区域/时间多条件查询灾情数据

### 数据管理
- **原始数据** - 多源数据采集与存储
- **处理数据** - 数据处理与置信度评估
- **日志管理** - 系统日志查看与统计

### 文件管理
- 文件上传/下载/预览
- 多格式支持（图片、文档、视频、音频）
- 文件统计分析

### 系统设置
- **用户管理** - 用户增删改查、状态管理
- **模块管理** - 系统模块配置
- **存储策略** - 数据存储策略配置
- **数据淘汰** - 过期数据清理

### 地图可视化
- 灾情点位分布展示
- 区域统计热力图
- 时间轴动态展示
- 聚合点显示

---

## 项目结构

```
├── src/                    # 后端代码 (Spring Boot)
│   └── main/
│       ├── java/           # Java 源码
│       │   └── com/example/module/
│       │       ├── controller/    # 控制器
│       │       ├── service/       # 服务层
│       │       ├── entity/        # 实体类
│       │       ├── repository/    # 数据访问层
│       │       └── util/          # 工具类
│       └── resources/      # 配置文件
├── frontend/               # 前端代码 (Vue 3)
│   ├── src/
│   │   ├── api/           # API 接口
│   │   ├── views/         # 页面组件
│   │   │   ├── Dashboard.vue         # 数据看板
│   │   │   ├── DisasterDecode.vue    # 灾情解码
│   │   │   ├── DisasterData.vue      # 灾情查询
│   │   │   ├── FileManagement.vue    # 文件管理
│   │   │   ├── StorageStrategy.vue   # 存储策略
│   │   │   ├── DataEviction.vue      # 数据淘汰
│   │   │   └── ...
│   │   ├── components/    # 公共组件
│   │   └── router/        # 路由配置
│   └── package.json
├── scripts/
│   ├── init_database.sql          # 数据库初始化脚本
│   ├── 接口测试文档.md            # API 文档
│   └── 灾情解码API文档.md         # 灾情解码 API 文档
├── Dockerfile             # 后端 Docker 构建文件
├── docker-compose.yml     # Docker Compose 编排文件
├── pom.xml                # Maven 配置
└── README.md
```

## 技术栈

| 层级 | 技术 |
|------|------|
| 前端 | Vue 3 + Element Plus + Vite + ECharts |
| 后端 | Spring Boot 2.7 + Spring Data JPA/MongoDB |
| 数据库 | MySQL 5.7 + MongoDB 4.4 |
| 文件存储 | MongoDB GridFS |
| 容器化 | Docker + Docker Compose |
| Web 服务器 | Nginx（生产环境） |

## API 接口

### 核心接口
| 模块 | 接口前缀 | 说明 |
|------|---------|------|
| 用户管理 | `/api/users` | 用户CRUD |
| 模块管理 | `/api/modules` | 模块CRUD |
| 原始数据 | `/api/raw-data` | 原始数据管理 |
| 处理数据 | `/api/processed-data` | 处理数据管理 |
| 日志数据 | `/api/log-data` | 日志管理 |
| 文件管理 | `/api/files` | 文件上传下载 |
| 数据看板 | `/api/dashboard` | 统计数据 |
| 灾情解码 | `/api/disaster-decode` | ID解码、地理编码 |
| 灾情查询 | `/api/disaster-data` | 灾情数据查询 |
| 存储策略 | `/api/storage-strategy` | 策略配置 |
| 数据淘汰 | `/api/data-eviction` | 数据清理 |
| 地图可视化 | `/api/map` | 地图数据 |

详细接口文档见 [scripts/接口测试文档.md](./scripts/接口测试文档.md)

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

**Q: 前端显示"网络错误"或"加载数据失败"？**
A: 确保后端服务已启动，检查 `docker-compose ps` 或后端控制台

**Q: 后端启动失败？**
A: 检查 MySQL 和 MongoDB 是否启动，以及 `application.yml` 中的连接配置是否正确

**Q: 如何查看后端日志？**
A:
```bash
# Docker 环境
docker logs -f zaiqing-backend

# 本地环境
# 日志直接输出到控制台
```

---

## 相关文档

- [接口测试文档](./scripts/接口测试文档.md)
- [灾情解码API文档](./scripts/灾情解码API文档.md)
- [数据库配置说明](./README_数据库配置说明.md)
