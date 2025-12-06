# 多源数据管理系统

灾情/危机数据管理系统，支持多源数据的存储、处理和查询。

## 快速运行指南

### 方式一：仅运行前端（推荐先测试）

**无需配置数据库**，前端内置 Mock 数据可独立运行：

```bash
cd frontend
npm install
npm run dev
```

访问 http://localhost:5173 即可看到完整界面。

---

### 方式二：完整运行（前端 + 后端 + 数据库）

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
│   └── package.json
├── scripts/
│   └── init_database.sql  # 数据库初始化脚本
├── pom.xml                # Maven 配置
└── README.md
```

## 技术栈

| 层级 | 技术 |
|------|------|
| 前端 | Vue 3 + Element Plus + Vite |
| 后端 | Spring Boot 2.7 + Spring Data JPA/MongoDB |
| 数据库 | MySQL 5.7+ / MongoDB 4.0+ |

## 功能模块

- **用户管理** - 用户增删改查、状态管理
- **模块管理** - 系统模块配置
- **原始数据** - 多源数据采集与存储
- **处理数据** - 数据处理与置信度评估
- **日志管理** - 系统日志查看与统计

## 端口说明

| 服务 | 端口 | 说明 |
|------|------|------|
| 前端 | 5173 | Vite 开发服务器 |
| 后端 | 8080 | Spring Boot |
| MySQL | 3306 | 关系型数据库 |
| MongoDB | 27017 | 文档数据库 |

## 环境要求

- Node.js 16+
- JDK 1.8+
- Maven 3.6+
- MySQL 5.7+
- MongoDB 4.0+

## 生产部署

```bash
# 1. 构建前端
cd frontend
npm run build
# 将 dist/ 目录部署到 Nginx

# 2. 打包后端
cd ..
mvn clean package -DskipTests
# 将 jar 包部署到服务器
java -jar target/module-1.0.0.jar
```

## 常见问题

**Q: 前端启动后页面空白？**
A: 检查是否执行了 `npm install`

**Q: 前端显示"加载数据失败"？**
A: 开发模式会自动使用 Mock 数据，如果配置了 `VITE_USE_MOCK=false`，需要确保后端已启动

**Q: 后端启动失败？**
A: 检查 MySQL 和 MongoDB 是否启动，以及 `application.yml` 中的连接配置是否正确

## API 文档

详见 [scripts/接口测试文档.md](./scripts/接口测试文档.md)

## 数据库配置

详见 [README_数据库配置说明.md](./README_数据库配置说明.md)
