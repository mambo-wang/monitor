# ShowTime - AI Agent 远程运维监控系统

ShowTime 是面向新华三云基产品线的监控系统，核心能力：**日志采集检索**、**指标监控报表**、**RAG 知识库问答**。

---

## 功能特性

### 1. 资源管理
- 主机、网络、存储等资源统一管理
- 实时状态监控与告警

### 2. 日志采集检索
- 多产品日志统一采集
- 全文检索，快速定位问题

### 3. 指标监控报表
- 采集层插件化设计，支持多产品
- 指标数据可视化展示

### 4. RAG 知识库问答
- 向量知识库构建与检索
- **对话历史管理**：支持查看、删除历史会话
- 基于上下文的智能问答

---

## 技术架构

```
┌─────────────────────────────────────────────────────────────┐
│                      前端层 (Vue 3 + TypeScript)            │
│                        Port: 9090                           │
└─────────────────────────────────────────────────────────────┘
                              │
              ┌───────────────┴───────────────┐
              ▼                               ▼
┌─────────────────────────┐     ┌─────────────────────────────┐
│   Java 后端 (8888)       │     │   Python RAG 服务 (8000)   │
│   Spring Boot 2.5.12     │     │   FastAPI + ChromaDB        │
│   MyBatis-Plus + MySQL  │     │   向量检索 + LLM 问答       │
└─────────────────────────┘     └─────────────────────────────┘
              │                               │
              ▼                               ▼
┌─────────────────────────┐     ┌─────────────────────────────┐
│        MySQL 8.0+       │     │        ChromaDB            │
│   元数据、配置存储        │     │   向量知识库存储            │
└─────────────────────────┘     └─────────────────────────────┘
```

---

## 模块说明

### 采集层 (Java 插件体系)

| 模块 | 说明 |
|------|------|
| `watcher-agent` | 采集程序主入口，Spring Boot，端口 8888 |
| `watcher-sdk` | 公共模块：DTO、配置、工具类 |
| `watcher-cas` | CAS 虚拟化平台插件 |
| `watcher-uis` | UIS 超融合平台插件 |
| `watcher-workspace` | Workspace 云桌面插件 |
| `watcher-onestor` | OneStor 分布式存储插件 |
| `watcher-builder` | 打包部署模块 |

### 服务层

| 模块 | 技术栈 | 端口 |
|------|--------|------|
| `watcher-ai` | Python FastAPI + ChromaDB + LLM | 8000 |

### 前端层

| 模块 | 技术栈 | 端口 |
|------|--------|------|
| `watcher-web` | Vue 3 + TypeScript + Vite | 9090 |

---

## 快速开始

### 环境要求
- JDK 17+
- Python 3.11+
- Node.js 18+
- MySQL 8.0+
- macOS/Linux

### 启动服务

```bash
# 1. 启动 Java 后端 (8888)
cd watcher-agent && java -jar target/agent.jar

# 2. 启动 Python RAG 服务 (8000)
cd watcher-ai/src && uvicorn watcher_ai.main:app --host 0.0.0.0 --port 8000

# 3. 启动前端 (9090)
cd watcher-web && npm run dev
```

### 运行测试

```bash
# 运行所有测试
make test

# Java 测试
cd watcher-agent && mvn test

# Python 测试
cd watcher-ai && pytest tests/
```

---

## 开发规范

1. **禁止循环调用**：不在循环中请求数据库或调用接口
2. **遍历优先 for**：遍历集合优先使用 for 循环
3. **不返回 null**：方法不返回 null，返回 Optional 或抛异常

---

## 项目结构

```
ShowTime/
├── watcher-agent/         # Java 采集程序
│   ├── watcher-sdk/       # 公共模块
│   ├── watcher-cas/       # CAS 插件
│   ├── watcher-uis/       # UIS 插件
│   ├── watcher-workspace/ # Workspace 插件
│   └── watcher-onestor/   # OneStor 插件
├── watcher-ai/            # Python RAG 服务
│   └── src/watcher_ai/
│       ├── api/           # API 路由
│       ├── services/       # 业务服务
│       ├── models/        # 数据模型
│       └── database/       # 数据库操作
├── watcher-web/           # Vue 3 前端
│   └── src/views/main/
│       ├── agent/         # 代理管理
│       ├── dashboard/     # 仪表盘
│       ├── knowledge/     # 知识库
│       ├── metric/        # 指标
│       ├── resource/      # 资源
│       └── user/          # 用户
├── watcher-builder/       # 打包部署
└── scripts/              # 数据库脚本
```

---

## API 文档

启动 RAG 服务后访问：`http://localhost:8000/docs`

### 知识库 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/knowledge/kbs` | 获取知识库列表 |
| POST | `/api/knowledge/kbs` | 创建知识库 |
| DELETE | `/api/knowledge/kbs/{kb_id}` | 删除知识库 |
| POST | `/api/knowledge/kbs/{kb_id}/documents` | 上传文档 |
| POST | `/api/knowledge/kbs/{kb_id}/build` | 构建知识库 |

### 对话历史 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/knowledge/chat/history` | 获取用户对话历史列表 |
| GET | `/api/knowledge/chat/sessions/{session_id}` | 获取会话详情 |
| DELETE | `/api/knowledge/chat/sessions/{session_id}` | 删除会话 |
| POST | `/api/knowledge/chat/with-history` | 带历史的 RAG 问答 |

---

## 许可证

MIT License
