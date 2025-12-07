# 数据库配置说明

## 概述

应用程序需要连接两个数据库：
1. **MySQL 5.7** - 用于存储结构化数据（用户、模块等）
2. **MongoDB 4.4** - 用于存储文档数据（原始数据、处理数据、日志等）

---

## 方式一：Docker 部署（推荐）

使用 Docker 部署时，**无需手动配置数据库**，docker-compose.yml 已包含完整配置：

```bash
# 启动所有服务
docker-compose up -d

# 数据库会自动初始化，包括：
# - 创建 zaiqing 数据库
# - 执行 scripts/init_database.sql 初始化表结构
# - 配置 UTF-8 字符集
```

### Docker 环境数据库配置

| 配置项 | 值 |
|--------|-----|
| MySQL 主机 | mysql（容器内） / localhost:3306（宿主机） |
| MySQL 用户名 | root |
| MySQL 密码 | 12345 |
| MySQL 数据库 | zaiqing |
| MongoDB 主机 | mongodb（容器内） / localhost:27017（宿主机） |
| MongoDB 数据库 | module_db |

### 字符集配置

docker-compose.yml 中已配置 MySQL 强制使用 UTF-8：

```yaml
command: >
  --character-set-server=utf8mb4
  --collation-server=utf8mb4_unicode_ci
  --init-connect='SET NAMES utf8mb4'
  --skip-character-set-client-handshake
```

这确保中文数据正确存储和显示。

---

## 方式二：本地安装数据库

### 配置 MySQL

#### 1. 确认 MySQL 服务运行

```powershell
# Windows 检查 MySQL 服务状态
Get-Service | Where-Object {$_.Name -like "*mysql*"}

# 启动 MySQL 服务
net start MySQL
```

#### 2. 创建数据库并初始化

```bash
mysql -u root -p < scripts/init_database.sql
```

#### 3. 修改配置文件

编辑 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/zaiqing?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8
    username: root
    password: 您的MySQL密码
```

### 配置 MongoDB

#### 1. 确认 MongoDB 服务运行

```powershell
# Windows 检查 MongoDB 服务状态
Get-Service | Where-Object {$_.Name -like "*mongo*"}

# 启动 MongoDB 服务
net start MongoDB
```

#### 2. 修改配置文件（如需要）

编辑 `src/main/resources/application.yml`：

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/module_db
      database: module_db
```

---

## 常见问题

### Q: Docker 环境中文显示乱码？

A: 执行以下命令重新初始化数据库：
```bash
docker-compose down -v
docker-compose up -d
```

### Q: 如何连接 Docker 中的数据库？

A: 使用以下连接信息：
- MySQL: `localhost:3306`，用户 `root`，密码 `12345`
- MongoDB: `localhost:27017`

```bash
# 连接 MySQL
docker exec -it zaiqing-mysql mysql -uroot -p12345

# 连接 MongoDB
docker exec -it zaiqing-mongodb mongo
```

### Q: 如何备份 Docker 中的数据库？

A:
```bash
# 备份 MySQL
docker exec zaiqing-mysql mysqldump -uroot -p12345 zaiqing > backup.sql

# 备份 MongoDB
docker exec zaiqing-mongodb mongodump --db module_db --out /dump
docker cp zaiqing-mongodb:/dump ./mongodb_backup
```

### Q: 如果暂时不需要 MySQL，能否禁用？

A: 不可以。当前架构中，MySQL 和 MongoDB 都是必需的。

### Q: 连接失败时如何快速测试？

A: 可以使用客户端直接测试连接：
```bash
# MySQL
mysql -u root -p -h localhost -P 3306

# MongoDB
mongo localhost:27017
```

---

## 重新编译和运行

配置修改后，需要重新编译：

```bash
# 在项目根目录执行
mvn clean package -DskipTests

# 运行应用
java -jar target/module-1.0.0.jar
```

---

## 更新日志

### 2025-12-07
- 添加 Docker 部署数据库配置说明
- 添加字符集配置说明（解决中文乱码）
- 添加数据库备份方法
