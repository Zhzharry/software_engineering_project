# 数据库配置说明

## 问题说明

应用程序需要连接两个数据库：
1. **MySQL** - 用于存储结构化数据
2. **MongoDB** - 用于存储文档数据

## 配置 MySQL 连接

### 1. 确认 MySQL 服务运行
```powershell
# Windows 检查 MySQL 服务状态
Get-Service | Where-Object {$_.Name -like "*mysql*"}

# 或者启动 MySQL 服务
net start MySQL
```

### 2. 修改配置文件
编辑 `src/main/resources/application.yml` 文件，修改以下配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/module_db?useSSL=false&serverTimezone=UTC
    username: root
    password: 您的MySQL密码  # 修改这里为您的实际密码
```

### 3. 创建数据库（如果不存在）
```sql
CREATE DATABASE IF NOT EXISTS module_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## 配置 MongoDB 连接

### 1. 确认 MongoDB 服务运行
```powershell
# Windows 检查 MongoDB 服务状态
Get-Service | Where-Object {$_.Name -like "*mongo*"}

# 或者启动 MongoDB 服务
net start MongoDB
```

### 2. 修改配置文件（如需要）
编辑 `src/main/resources/application.yml` 文件：

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/module_db
      database: module_db
```

## 常见问题

### Q: 如果暂时不需要 MySQL，能否禁用？
A: 可以，但需要修改代码配置。当前架构中，MySQL 和 MongoDB 都是必需的。

### Q: 如何查看当前 MySQL 密码？
A: 如果您忘记了密码，可以重置 MySQL root 密码或使用其他有权限的用户。

### Q: 连接失败时如何快速测试？
A: 可以使用 MySQL 客户端直接测试连接：
```bash
mysql -u root -p -h localhost -P 3306
```

## 重新编译和运行

配置修改后，需要重新编译：

```powershell
# 在项目根目录执行
mvn clean package

# 运行应用
java -jar target\demo.jar
```

