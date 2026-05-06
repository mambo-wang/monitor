---
name: ShowTime Sprint1 开发计划
overview: 制定资源管理、指标查询、MCP服务三个功能的开发计划，覆盖前端页面、后端接口、数据库表设计
todos:
  - id: create-resource-table
    content: 在schema-mysql.sql中创建resource资源表和metric_data指标数据表
    status: completed
  - id: implement-resource-api
    content: 在ResourceService中实现资源CRUD方法，修改CollectController恢复资源同步接口
    status: completed
    dependencies:
      - create-resource-table
  - id: implement-metric-api
    content: 新增MetricController和MetricService，提供指标查询REST接口
    status: completed
    dependencies:
      - create-resource-table
  - id: frontend-add-resource
    content: 在前端新增资源页面（resource-add.vue），提供表单提交创建资源功能
    status: completed
  - id: frontend-metric-detail
    content: 新增指标详情页面，展示监控图表和时间范围切换功能
    status: completed
  - id: implement-mcp-server
    content: 使用[skill:MCP开发指南]创建MCP Server，提供资源查询和指标查询工具
    status: completed
    dependencies:
      - implement-resource-api
      - implement-metric-api
  - id: create-skill-definition
    content: 创建SKILL定义文件，使AI Agent能自然语言调用监控系统
    status: completed
    dependencies:
      - implement-mcp-server
---

## 产品概述

ShowTime是一个全方位监控系统（云基产品线），核心功能是指标采集和监控报表展示。

## 核心功能需求

### 1. 资源管理功能

- **后端REST接口**：接收ResourceDTO集合入库，提供资源CRUD操作
- **前端页面**：资源列表展示（已有）、新增资源功能（缺失）、资源详情
- **数据库**：创建资源表存储资源信息（平台类型、IP地址、端口、认证信息等），采集到的监控数据不入库
- **支持平台**：Workspace/UIS/CAS/ONEStor

### 2. 指标查询功能

- **后端REST接口**：接收资源IP和监控指标集合，返回对应环境的监控数据
- **指标类型**：支持100+种指标（CPU/内存/磁盘/网络/存储池/集群/主机/虚拟机/终端等）

### 3. MCP服务功能

- **MCP Server**：提供工具使AI Agent能调用监控接口
- **MCP工具**：查询资源列表、查询指标数据、查询告警信息
- **SKILL定义**：提供自然语言接口让AI Agent使用监控系统

## 视觉与交互效果

- 前端使用Vue 3 + Element Plus组件库
- 部分监控数据以图表形式展示（折线图/柱状图）
- 支持响应式布局

## 技术栈

- **后端**：Java 8 + Spring Boot 2.5.12 + MyBatis-Plus 3.5.3 + MySQL 8
- **前端**：Vue 3 + TypeScript + Element Plus
- **MCP**：Python/FastMCP
- **构建**：Maven多模块

## 技术架构

### 模块划分

```
ShowTime/
├── watcher-agent/      # 采集主程序 + REST API
├── watcher-sdk/        # 公共模块（DTO、常量、工具类）
├── watcher-cas/        # CAS产品指标采集
├── watcher-uis/        # UIS产品指标采集
├── watcher-workspace/  # Workspace产品指标采集
├── watcher-onestor/    # OneStor产品指标采集
└── watcher-web/        # 前端Vue 3
--- watcher-ai          # MCP Server
```

### 数据流设计

1. **资源同步**：前端 -> REST API -> ResourceService -> MySQL资源表
2. **指标查询/采集**：前端/MCP Client-> REST API -> DataReportService -> 调用接口实时查询
3. **MCP调用**：AI Agent -> MCP Server -> REST API -> DataReportService -> 调用接口实时查询

### 关键设计决策

1. **MCP Server实现**：使用Python FastMCP实现，独立部署
2. **接口认证**：复用现有JWT token机制
3. **数据兼容**：保留原有的ReportMetricEnum和DataReportTypeByMetricEnum枚举

## 数据库设计

### 资源表（已定义结构）

```sql
CREATE TABLE resource (
    id VARCHAR(64) PRIMARY KEY,
    platform VARCHAR(50),
    ip_address VARCHAR(100),
    port INT,
    protocol VARCHAR(20),
    auth_type VARCHAR(50),
    ac VARCHAR(100),
    ci VARCHAR(255),
    server_username VARCHAR(100),
    server_password VARCHAR(255),
    server_port INT,
    active INT DEFAULT 1,
    usable INT DEFAULT 1,
    remote INT DEFAULT 0,
    end_time DATETIME,
    create_time DATETIME,
    update_time DATETIME
);
```

## 实现说明

### 后端核心修改

1. **CollectController**：恢复 `/collect/resources` 接口实现
2. **新增ResourceController**：提供资源CRUD REST接口
3. **新增MetricController**：提供指标查询REST接口
4. **新增模块watcher-ai**：MCP Server配置和工具定义

### 前端核心修改

1. **新增资源页面**：表单提交创建资源
2. **新增指标详情页**：图表展示监控数据。包括集群/主机/虚拟机/终端等的列表以及主机监控/虚拟机监控报表图表数据
3. **时间范围选择器**：主机监控/虚拟机监控报表图表数支持切换不同时间范围

### 性能考虑

- 前端图表使用ECharts按需渲染

## Agent Extensions

### Skill

- **MCP开发指南**
- Purpose: 指导创建高质量的MCP Server，使AI Agent能调用监控接口
- Expected outcome: 提供Python FastMCP实现方案