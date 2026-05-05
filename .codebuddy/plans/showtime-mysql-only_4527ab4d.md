---
name: showtime-mysql-only
overview: 将 ShowTime 项目从依赖 MongoDB、Kafka、Elasticsearch、ClickHouse 的架构改造为仅依赖 MySQL 的单机版本。保留用户认证功能，移除日志队列、搜索、定时任务等功能，先让项目能在本地环境运行起来。
todos:
  - id: cleanup-parent-pom
    content: 清理父 pom.xml，移除中间件版本管理（kafka/ES/quartz-mongodb）
    status: completed
  - id: cleanup-sdk-pom
    content: 改造 watcher-sdk pom.xml，移除 MongoDB/Kafka/ES/ClickHouse 依赖，添加 MySQL/Spring Data JPA 依赖
    status: completed
    dependencies:
      - cleanup-parent-pom
  - id: cleanup-agent-pom
    content: 改造 watcher-agent pom.xml，移除 quartz-mongodb，添加 mysql-connector
    status: completed
    dependencies:
      - cleanup-sdk-pom
  - id: cleanup-performer-pom
    content: 改造 performer-service pom.xml，移除 ClickHouse 依赖
    status: completed
    dependencies:
      - cleanup-agent-pom
  - id: create-mysql-schema
    content: 创建 MySQL 建表脚本 schema-mysql.sql，包含 sys_user、pwd_strategy、parameter 等表
    status: completed
    dependencies:
      - cleanup-sdk-pom
  - id: create-mysql-datasource
    content: 创建 MySQL 数据源配置类 DataSourceConfig.java
    status: completed
    dependencies:
      - create-mysql-schema
  - id: transform-entities
    content: 改造实体类，SysUser/PwdStrategy 等改用 JPA 注解替换 @Document
    status: completed
    dependencies:
      - create-mysql-datasource
  - id: create-repositories
    content: 创建 JPA Repository 接口替代 MongoTemplate 操作
    status: completed
    dependencies:
      - transform-entities
  - id: transform-login-service
    content: 改造 LoginService，使用 JPA Repository 替代 MongoTemplate
    status: completed
    dependencies:
      - create-repositories
  - id: transform-sysuser-service
    content: 改造 SysUserService，使用 JPA Repository
    status: completed
    dependencies:
      - transform-login-service
  - id: simplify-app-runner
    content: 简化 ApplicationRunnerImpl，移除中间件依赖的初始化逻辑
    status: completed
    dependencies:
      - transform-sysuser-service
  - id: disable-kafka-consumers
    content: 禁用/移除 KafkaConsumers 类，注释掉日志队列消费逻辑
    status: completed
    dependencies:
      - simplify-app-runner
  - id: disable-quartz-mongodb
    content: 改造 QuartzConfig，移除 MongoDB JobStore，改用内存或 JDBC
    status: completed
    dependencies:
      - disable-kafka-consumers
  - id: create-local-config
    content: 创建 application-local.properties 本地 MySQL 配置，禁用 ES/Kafka/插件
    status: completed
    dependencies:
      - disable-quartz-mongodb
  - id: verify-build
    content: 编译验证，执行 mvn clean compile 确保无依赖错误
    status: completed
    dependencies:
      - create-local-config
---

## 用户需求

将 ShowTime 项目改造为**仅使用 MySQL 的单机版本**，目标是本地能跑起来，保留用户认证功能。

## 核心要求

- 仅保留基础监控功能
- 保留用户认证和权限（迁移到 MySQL）
- 先跑起来，功能后续逐步恢复
- 接受去掉部分功能

## 现状分析

### 当前中间件依赖

| 中间件 | 用途 | 影响范围 |
| --- | --- | --- |
| **MongoDB** | 用户、配置、任务、日志存储 | 46个实体类、核心Service |
| **Kafka** | 日志消息队列 | KafkaConsumers |
| **Elasticsearch** | 日志全文检索 | ES客户端、索引操作 |
| **ClickHouse** | 时序指标存储 | performer-service |


### 需移除的功能

- Kafka 消费者（Filebeat日志采集）
- Elasticsearch 日志存储和检索
- ClickHouse 时序数据存储
- MongoDB 集群特性（Quartz集群）
- 插件模块（cas/ws/uis/onestor）

### 需保留的功能

- 用户登录认证（SysUser）
- 密码策略（PwdStrategy）
- 基础配置管理
- WebSocket 通信
- REST API 接口

## 技术方案

### 架构策略

采用**渐进式简化**策略：

1. 先让项目能启动
2. 逐步恢复功能

### 依赖替换方案

| 原依赖 | 替换方案 | 说明 |
| --- | --- | --- |
| MongoDB | MySQL + JPA/MyBatis | 存储用户和配置 |
| Kafka | 移除 | 禁用日志队列 |
| Elasticsearch | 移除 | 禁用日志搜索 |
| ClickHouse | MySQL | 存储指标数据 |
| Quartz MongoDB JobStore | JDBC JobStore | 使用MySQL存储任务 |


### 实现步骤

#### 第一阶段：依赖清理

1. 修改父 pom.xml - 移除中间件依赖声明
2. 修改 watcher-sdk/pom.xml - 移除 MongoDB/Kafka/ES/ClickHouse 依赖，添加 MySQL/JPA 依赖
3. 修改 watcher-agent/pom.xml - 移除 quartz-mongodb，添加 mysql-connector
4. 修改 performer-service/pom.xml - 移除 ClickHouse 依赖

#### 第二阶段：数据层改造

5. 创建 MySQL 建表脚本（init.sql）

- sys_user（用户表）
- pwd_strategy（密码策略）
- oad_watcher_status（状态）
- agent_unique_code（唯一码）
- operation_log（操作日志）
- task（任务表）
- deploy（部署表）
- parameter（参数配置）

6. 改造实体类（移除 @Document，改用 JPA 注解）

- SysUser.java
- PwdStrategy.java
- OadWatcherStatus.java
- AgentUniqueCode.java
- OperationLog.java

7. 创建 Repository 接口替代 MongoTemplate

#### 第三阶段：服务层改造

8. 改造 LoginService - 使用 JPA Repository 替代 MongoTemplate
9. 改造 SysUserService - 使用 JPA Repository
10. 改造 ApplicationRunnerImpl - 简化初始化逻辑

#### 第四阶段：配置改造

11. 创建 application-local.properties - MySQL 单机配置
12. 禁用 Elasticsearch 配置（@ConditionalOnProperty）
13. 禁用 Kafka 配置
14. 禁用插件模块（rest-client.*.enable=false）

#### 第五阶段：清理和测试

15. 移除/禁用 KafkaConsumers
16. 移除 Quartz MongoDB JobStore 配置
17. 创建本地启动脚本
18. 验证用户登录功能

## 目录结构

```
watcher-agent/src/main/resources/
├── application.properties      # 主配置（禁用中间件）
├── application-local.properties # 本地MySQL配置
├── application-dev.properties  # 开发配置
├── schema-mysql.sql           # [NEW] MySQL表结构
└── logback-local.xml          # [NEW] 本地日志配置

watcher-sdk/src/main/java/.../config/
├── datasource/                # [NEW] MySQL数据源配置
├── jpa/                       # [NEW] JPA配置
└── (保留) swagger/            # Swagger文档

watcher-agent/src/main/java/.../
├── entity/                    # 改造实体类（JPA注解）
├── repository/                # [NEW] JPA Repository
├── service/auth/              # 改造LoginService
└── service/SysUserService    # 改造用户服务
```

## 关键技术决策

### 数据访问层选择

- **方案A：Spring Data JPA** - 简单快捷，自动生成CRUD
- **方案B：MyBatis** - 灵活可控，performer已有generator插件
- **选择**：Spring Data JPA（最小改动原则）

### 定时任务处理

- 原方案：Quartz + MongoDB JobStore
- 新方案：Spring @Scheduled 简单定时（保留基础功能）

### 日志处理

- 原方案：Kafka → ES
- 新方案：直接写本地文件/数据库（简化）