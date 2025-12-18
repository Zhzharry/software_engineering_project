# ============================================
# 后端 Dockerfile - 灾情数据管理系统
# 使用 Java 8 + 多阶段构建
# ============================================

# 构建阶段
FROM eclipse-temurin:8-jdk AS builder

WORKDIR /app

# 安装 Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# 复制 pom.xml 并下载依赖（利用 Docker 缓存）
COPY pom.xml .

# 配置 Maven 使用阿里云镜像加速
RUN mkdir -p /root/.m2 && echo '<?xml version="1.0" encoding="UTF-8"?>\
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" \
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" \
xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">\
<mirrors><mirror><id>aliyun</id><mirrorOf>central</mirrorOf><name>Aliyun Maven</name>\
<url>https://maven.aliyun.com/repository/central</url></mirror></mirrors></settings>' > /root/.m2/settings.xml

# 下载依赖
RUN mvn dependency:go-offline -B

# 复制源代码并构建
COPY src ./src
RUN mvn clean package -DskipTests -B

# ============================================
# 运行阶段 - 使用更轻量的 JRE 镜像
# ============================================
FROM eclipse-temurin:8-jre

WORKDIR /app

# 安装 curl 用于健康检查，设置时区
RUN apt-get update && apt-get install -y curl tzdata \
    && ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo "Asia/Shanghai" > /etc/timezone \
    && rm -rf /var/lib/apt/lists/*

# 创建上传目录
RUN mkdir -p /app/uploads

# 复制构建好的 JAR 文件
COPY --from=builder /app/target/*.jar app.jar

# 暴露端口
EXPOSE 8080

# 设置 JVM 参数和启动应用
ENTRYPOINT ["java", "-Duser.timezone=Asia/Shanghai", "-Dfile.encoding=UTF-8", "-jar", "app.jar"]
