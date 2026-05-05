# CODEBUDDY.md

This file provides guidance to CodeBuddy when working with code in this repository.

## 项目概述

ShowTime 是一个全方位监控系统，特别适合新华三云基产品线。核心功能：日志采集/全文检索、指标采集/监控报表、爬虫/知识库。

### 核心业务逻辑

**传入 IP 地址/监控指标 → 查询对应环境的对应数据**

系统提供 REST 接口，接收 IP 地址和监控指标作为参数，返回该 IP 在对应环境（CAS/UIS/OneStor/Workspace）下的监控数据。这是整个系统的核心价值点。

## 构建命令

```bash
# 全量构建
mvn clean package -DskipTests

# 单模块构建
mvn clean package -DskipTests -pl watcher-agent -am

# 跳过测试构建
mvn clean package -DskipTests

# 跳过检查构建
mvn clean package -Dmaven.test.skip=true
```

## 项目结构

```
ShowTime/
├── watcher-agent/      # 采集程序主入口（Spring Boot）
│   ├── config/        # Quartz/应用配置
│   ├── service/       # 采集服务（Kafka/MongoDB）
│   ├── task/          # Quartz定时任务
│   ├── controller/    # REST接口
│   └── entity/        # MongoDB实体
├── watcher-sdk/       # 公共模块（被所有模块依赖）
│   ├── api/           # 对外API接口定义
│   ├── config/        # ClickHouse/Kafka/ES/Rest配置
│   ├── constant/      # 枚举常量（指标类型/日志类型）
│   ├── dto/           # 数据传输对象
│   └── utils/         # 工具类
├── watcher-cas/       # CAS产品指标采集
├── watcher-uis/       # UIS产品指标采集
├── watcher-workspace/ # Workspace产品指标采集
├── watcher-onestor/   # OneStor产品指标采集
├── watcher-builder/   # 打包模块（assembly目录包含部署脚本）
├── watcher-web/       # 前端（Vue 3）
└── pom.xml            # 父POM，Java 8 + Spring Boot 2.5.12
```

## 技术栈

| 组件 | 用途 |
|------|------|
| Spring Boot 2.5.12 | Web框架 |
| Quartz | 定时任务调度 |
| MongoDB | 采集数据持久化 |
| Elasticsearch | 日志全文检索 |
| Kafka | 消息引擎 |
| ClickHouse | 指标分析（列存储） |
| MyBatis-Plus 3.5.3 | MySQL访问（单机版） |
| Vue 3 | 前端界面 |

## 关键设计

### SDK模块依赖关系
所有子模块都依赖 `watcher-sdk`，SDK提供统一的配置类（ClickHouseClientConfig、KafkaProducerConfig等）和常量定义。各产品模块（cas/uis/onestor）继承SDK配置后扩展自己的采集逻辑。

### 数据采集流程
1. `WatcherAgentApplication` 启动时加载 Quartz 任务配置
2. 定时任务 (`task/`) 触发指标/日志采集
3. 数据通过 Kafka 发送或直接写入 MongoDB/ClickHouse
4. 前端 `watcher-web` 通过 REST API 查询展示

### 常量定义
`watcher-sdk/constant/` 下按功能分类：
- `ReportMetricEnum.java` - 指标类型枚举
- `RealTimeLogTypeEnum.java` - 实时日志类型
- `OperationLogTypeEnum.java` - 操作日志类型
- `WarnTypeEnum.java` / `WarnMetricEnum.java` - 告警相关


## 数据库
- **MySQL**: 单机版配置存储（`emotional_chat`库）

连接信息配置在 `watcher-agent/src/main/resources/application.properties`。

## Git Hooks

项目已配置团队共用 Git Hooks（`.githooks/`）：
- 提交量检查（默认阈值1000行）
- 提交信息格式检查（必须以story/bugfix开头）

配置：`python3 .githooks/setup-hooks.py --show`


## 其他
代码风格要遵循阿里巴巴Java开发规范

当前正在做的：去掉中间件，只保留mysql也能启动项目。
