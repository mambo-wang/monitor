# ShowTime 系统架构文档

> **版本**: 1.0.0 | **更新**: 2026-05-14 | **状态**: 进行中

---

## 1. 系统概览

ShowTime 是一个**远程运维终端管理系统**，支持多平台（CAS、UIS、Workspace、OneStor）的资源监控、数据采集和可视化分析。

### 1.1 核心功能

| 功能模块 | 描述 | 技术栈 |
|----------|------|--------|
| 资源管理 | 多平台主机/集群/存储管理 | Java Spring Boot |
| 数据采集 | 指标、日志、实时监控 | Java + WebSocket |
| 可视化 | 仪表盘、大屏展示 | Vue 3 + ECharts |
| 知识库 | RAG 智能问答 | Python FastAPI + ChromaDB |
| 部署管理 | 一键部署、批量操作 | Java + Shell |

---

## 2. 整体架构

```
┌─────────────────────────────────────────────────────────────────────┐
│                           客户端层                                  │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐                  │
│  │   Web UI   │  │  大屏展示   │  │   API 调用  │                  │
│  │  (Vue 3)   │  │  (ECharts) │  │   (Axios)   │                  │
│  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘                  │
└─────────┼────────────────┼────────────────┼───────────────────────────┘
          │                │                │
          ▼                ▼                ▼
┌─────────────────────────────────────────────────────────────────────┐
│                         前端服务层 (Node.js)                        │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │                     Vite Dev Server                         │    │
│  │                     静态资源服务                             │    │
│  │                     端口: 9090                             │    │
│  └─────────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────────┘
          │                                      │
          ▼                                      ▼
┌───────────────────────┐        ┌───────────────────────────────────┐
│   RAG API 服务        │        │         Java 后端服务              │
│   (Python FastAPI)    │        │         (Spring Boot)             │
│   端口: 8000          │        │         端口: 8888                │
├───────────────────────┤        ├───────────────────────────────────┤
│  ┌─────────────────┐  │        │  ┌───────────┐  ┌───────────────┐  │
│  │ 知识库管理 API  │  │        │  │ 控制器层  │  │  插件 Handler │  │
│  │ POST /chat     │  │        │  │ /deploy   │  │  CAS/UIS/Ws   │  │
│  │ GET  /kbs      │  │        │  │ /resource │  │  /Onestor    │  │
│  └────────┬────────┘  │        │  └─────┬─────┘  └───────┬───────┘  │
│           │           │        │        │                │          │
│  ┌────────▼────────┐  │        │  ┌─────▼─────────┐  ┌───▼───────┐  │
│  │   服务层        │  │        │  │   服务层       │  │  SDK 层   │  │
│  │ Chroma/LLM/Doc  │  │        │  │ Deploy/Metric │  │ Host/Lock │  │
│  └────────┬────────┘  │        │  └─────┬─────────┘  └───────────┘  │
│           │           │        │        │                          │
│  ┌────────▼────────┐  │        │  ┌─────▼─────────┐                  │
│  │   数据存储层    │  │        │  │   数据层       │                  │
│  │ ChromaDB/File   │  │        │  │ MyBatis/MySQL │                  │
│  └─────────────────┘  │        │  └───────────────┘                  │
└───────────────────────┘        └───────────────────────────────────┘
          │                                      │
          ▼                                      ▼
┌───────────────────────┐        ┌───────────────────────────────────┐
│   外部服务            │        │         外部服务                    │
│  ┌─────────────────┐ │        │  ┌───────────┐  ┌─────────────┐  │
│  │ Ollama Embedding │ │        │  │ CAS/UIS/  │  │   OneStor   │  │
│  │ (bge-m3)        │ │        │  │ Workspace │  │   Storage   │  │
│  └─────────────────┘ │        │  │ Platforms │  │   API       │  │
│  ┌─────────────────┐ │        │  └───────────┘  └─────────────┘  │
│  │ MiniMax LLM     │ │        │  ┌───────────┐                   │
│  │ (M2)            │ │        │  │  WebSocket│                   │
│  └─────────────────┘ │        │  │  Clients  │                   │
└───────────────────────┘        └───────────────────────────────────┘
```

---

## 3. 模块架构

### 3.1 Java 后端 (watcher-agent)

```
watcher-agent/
├── src/main/java/com/virtual/cloud/om/
│   ├── agent/                    # 主应用
│   │   ├── WatcherAgentApplication.java    # 入口
│   │   ├── controller/                      # REST 控制器
│   │   ├── config/                         # 配置类
│   │   ├── interceptor/                    # 拦截器
│   │   └── websocket/                      # WebSocket
│   ├── sdk/                       # 核心 SDK
│   │   ├── api/                               # API 接口定义
│   │   ├── common/                           # 公共类
│   │   ├── domain/                           # 领域模型
│   │   ├── mapper/                           # MyBatis Mapper
│   │   └── service/                          # SDK 服务
│   ├── cas/                       # CAS 虚拟化插件
│   ├── uis/                       # UIS 平台插件
│   ├── workspace/                 # Workspace 插件
│   └── onestor/                   # OneStor 存储插件
└── pom.xml
```

**关键依赖**:
- Spring Boot Starter Web
- MyBatis-Plus
- WebSocket
- JJWT (JWT 认证)
- SM4 (加密)

### 3.2 前端 (watcher-web)

```
watcher-web/
├── src/
│   ├── api/                      # API 调用层
│   │   ├── dashboard/
│   │   ├── deploy/
│   │   ├── resource/
│   │   └── knowledge/           # RAG API 调用
│   ├── components/              # 公共组件
│   │   ├── charts/             # ECharts 图表
│   │   ├── table/              # 表格组件
│   │   └── uploader/           # 文件上传
│   ├── layout/                  # 布局组件
│   ├── router/                  # 路由配置
│   │   └── modules/             # 路由模块化
│   ├── store/                   # Vuex 状态
│   ├── utils/                   # 工具函数
│   └── views/                   # 页面组件
│       ├── dashboard/           # 仪表盘
│       ├── main/                # 主视图
│       └── large-display/       # 大屏展示
└── vite.config.ts
```

### 3.3 RAG 服务 (watcher-ai)

```
watcher-ai/
├── src/
│   ├── watcher_ai/
│   │   ├── main.py              # FastAPI 入口
│   │   ├── api/
│   │   │   └── knowledge.py     # 知识库 API
│   │   ├── services/
│   │   │   ├── kb_service.py        # KB 管理
│   │   │   ├── chroma_service.py     # 向量存储
│   │   │   ├── llm_service.py        # LLM 调用
│   │   │   ├── document_service.py   # 文档处理
│   │   │   ├── document_store.py     # 文档存储
│   │   │   └── build_service.py      # 构建编排
│   │   └── models/
│   │       └── schemas.py            # Pydantic 模型
│   ├── chroma_db/                # ChromaDB 数据
│   ├── uploads/                  # 上传文档
│   └── data/                     # KB 元数据
└── requirements.txt
```

---

## 4. API 设计

### 4.1 Java 后端 API

| 路径 | 方法 | 描述 |
|------|------|------|
| `/watcher/deploy/*` | * | 部署管理 |
| `/watcher/resource/*` | * | 资源 CRUD |
| `/watcher/metric/*` | * | 指标查询 |
| `/watcher/user/login` | POST | 用户登录 |
| `/watcher/log/*` | * | 日志查询 |
| `/watcher/largeDisplay/*` | * | 大屏数据 |

### 4.2 RAG API

| 路径 | 方法 | 描述 |
|------|------|------|
| `/api/knowledge/kbs` | POST | 创建知识库 |
| `/api/knowledge/kbs` | GET | 列出知识库 |
| `/api/knowledge/kbs/{id}` | GET | 获取知识库 |
| `/api/knowledge/kbs/{id}` | DELETE | 删除知识库 |
| `/api/knowledge/kbs/{id}/documents` | POST | 上传文档 |
| `/api/knowledge/kbs/{id}/build` | POST | 构建向量 |
| `/api/knowledge/chat` | POST | RAG 问答 |
| `/api/knowledge/kbs/{id}/search` | POST | 向量检索 |

---

## 5. 数据存储

### 5.1 关系型数据库 (MySQL)

| 表 | 描述 |
|----|------|
| agent_host | 主机信息 |
| agent_deploy | 部署记录 |
| agent_metric | 指标数据 |
| agent_log | 日志记录 |
| agent_user | 用户信息 |

### 5.2 向量数据库 (ChromaDB)

```
路径: watcher-ai/src/chroma_db/
集合: kb_{kb_id}
用途: 知识库向量存储 + 语义检索
```

### 5.3 文件存储

| 路径 | 用途 |
|------|------|
| `watcher-ai/uploads/` | 上传文档 |
| `watcher-ai/data/` | KB 元数据 |

---

## 6. RAG 流程

```
┌─────────────────────────────────────────────────────────────────────┐
│                         RAG 问答流程                                │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  1. 上传文档                                                        │
│     POST /api/knowledge/kbs/{id}/documents                         │
│     ↓                                                              │
│     文件保存到: uploads/{kb_id}/{filename}                          │
│                                                                     │
│  2. 构建知识库                                                      │
│     POST /api/knowledge/kbs/{id}/build                            │
│     ↓                                                              │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │ for each document:                                          │   │
│  │   1. DocumentProcessor.process_document()                  │   │
│  │      - 加载文件 (PDF/MD/TXT)                               │   │
│  │      - 文本分块 (chunk_size=500, overlap=50)              │   │
│  │   2. ChromaService.add_vectors()                           │   │
│  │      - 调用 Ollama Embedding API                           │   │
│  │      - 生成向量并存入 ChromaDB                              │   │
│  │   3. 更新 KB 元数据                                         │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                                                     │
│  3. 问答检索                                                        │
│     POST /api/knowledge/chat                                       │
│     ↓                                                              │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │ 1. ChromaService.search()                                  │   │
│  │    - 用户问题 → Ollama Embedding → 向量                    │   │
│  │    - ChromaDB 语义检索 top_k=3                             │   │
│  │                                                             │   │
│  │ 2. 构建上下文                                               │   │
│  │    - 拼接相关文档片段                                       │   │
│  │                                                             │   │
│  │ 3. LLMService.chat()                                       │   │
│  │    - 调用 MiniMax M2 API                                    │   │
│  │    - 基于上下文生成回答                                      │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 7. 插件系统

### 7.1 平台插件架构

```
                    ┌─────────────────┐
                    │   Agent Core    │
                    │  (watcher-sdk)  │
                    └────────┬────────┘
                             │
         ┌───────────────────┼───────────────────┐
         │                   │                   │
         ▼                   ▼                   ▼
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   CAS       │    │   UIS       │    │  Workspace  │
│  Platform   │    │  Platform   │    │  Platform   │
├─────────────┤    ├─────────────┤    ├─────────────┤
│CasHostHandler│    │UisHostHandler│    │WsHostHandler│
│CasLogPattern │    │             │    │             │
└─────────────┘    └─────────────┘    └─────────────┘
```

### 7.2 Handler 接口

```java
public interface HostApi {
    List<HostVO> getHosts(HostQuery query);
    HostVO getHostById(String id);
    // ...
}

public interface LockApi {
    boolean acquireLock(String resourceId);
    void releaseLock(String resourceId);
}
```

---

## 8. 安全架构

### 8.1 认证

```
┌─────────────────────────────────────────────────────────────────────┐
│                         认证流程                                    │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  1. 登录                                                            │
│     POST /watcher/user/login                                        │
│     ↓                                                              │
│     输入: { username, password }                                    │
│     验证: SM4 解密 + JWT 生成                                       │
│     输出: { token, userInfo }                                       │
│                                                                     │
│  2. 请求拦截                                                        │
│     携带: Authorization: Bearer {token}                            │
│     验证: JWT 签名 + 过期时间                                       │
│                                                                     │
│  3. 权限控制                                                        │
│     路由守卫: router/permission.ts                                 │
│     按钮权限: 角色 + 权限码                                         │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

### 8.2 加密

- **SM4**: 对称加密，用于密码传输
- **JWT**: 无状态令牌认证
- **HTTPS**: 生产环境传输加密

---

## 9. 部署架构

```
┌─────────────────────────────────────────────────────────────────────┐
│                        生产环境部署                                 │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│                        ┌──────────────┐                              │
│                        │   Nginx      │                              │
│                        │  (反向代理)   │                              │
│                        └──────┬───────┘                              │
│                               │                                      │
│              ┌────────────────┼────────────────┐                     │
│              │                │                │                     │
│              ▼                ▼                ▼                     │
│     ┌──────────────┐ ┌──────────────┐ ┌──────────────┐              │
│     │   前端静态   │ │  Java 后端   │ │  Python RAG  │              │
│     │   文件       │ │  (8888)      │ │  (8000)      │              │
│     │   (9090)     │ │              │ │              │              │
│     └──────────────┘ └──────────────┘ └──────────────┘              │
│                                                                     │
│                               │                                      │
│     ┌─────────────────────────┼─────────────────────────┐            │
│     │                         │                         │            │
│     ▼                         ▼                         ▼            │
│  ┌────────┐           ┌────────────┐            ┌────────────┐       │
│  │ MySQL  │           │  CAS/UIS   │            │  Ollama    │       │
│  │        │           │  Platforms │            │  (本地)    │       │
│  └────────┘           └────────────┘            └────────────┘       │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 10. 性能优化

### 10.1 前端

- 路由懒加载
- 组件按需引入 (Element Plus)
- ECharts 按需加载
- 图片/资源压缩

### 10.2 后端

- 连接池 (Druid)
- 缓存 (本地缓存)
- 异步处理 (WebSocket 推送)

### 10.3 RAG 服务

- ChromaDB 向量索引
- Ollama 本地 Embedding
- 批量向量写入

---

## 11. 监控与日志

| 组件 | 方式 |
|------|------|
| Java 日志 | Logback → 文件 |
| 前端日志 | Console + Sentry |
| RAG 日志 | Python print + 文件 |
| 监控 | JMX (Java) |

---

**文档版本**: 1.0.0
**最后更新**: 2026-05-14
