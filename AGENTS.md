# ShowTime - AI Agent 开发规范

> **版本**: 2.0.1 | **更新**: 2026-05-15 | **技术栈**: Java 17 + Vue 3 + TypeScript + Python

---

## 1. 项目结构

| 目录 | 说明 |
|------|------|
| `watcher-agent/` | Java 主应用 (Spring Boot 2.5.12)，端口 8888 |
| `watcher-sdk/` | 公共 SDK，被所有插件依赖 |
| `watcher-cas/` | CAS 虚拟化平台插件 |
| `watcher-uis/` | UIS 超融合平台插件 |
| `watcher-workspace/` | Workspace 云桌面插件 |
| `watcher-web/` | 前端 (Vue 3 + TypeScript + Vite)，端口 9090 |
| `watcher-ai/` | RAG 服务 (Python FastAPI + ChromaDB)，端口 8000 |
| `docs/` | 架构文档、设计文档 |
| `harness/` | Agent 基础设施配置 |
| `scripts/` | 工具脚本 |

**入口**:
- Java: `watcher-agent/src/main/java/com/virtual/cloud/om/agent/WatcherAgentApplication.java`
- Python: `watcher-ai/src/watcher_ai/main.py`

---

## 2. 技术栈

### 2.1 Java 后端 (JDK 25)
```
Spring Boot 2.5.12 | MyBatis-Plus 3.5.x | MySQL/MariaDB | Maven
```

**关键配置** (`watcher-agent/src/main/resources/`):
- `application.properties` - 主配置
- `application-dev.properties` - 开发环境
- `application-prod.properties` - 生产环境

### 2.2 TypeScript 前端
```
Vue 3 (Composition API) | TypeScript | Vite | Element Plus | Vuex 4 | Vue Router 4
```

### 2.3 Python RAG 服务
```
FastAPI 0.136+ | ChromaDB 1.5+ | Ollama Embedding | bge-m3 | MiniMax-M2
```

---

## 3. 关键 API

### 3.1 Java 控制器 (`watcher-agent/src/main/java/com/virtual/cloud/om/agent/controller/`)

| 控制器 | 路径 | 说明 |
|--------|------|------|
| `DeployController` | `/watcher/deploy/*` | 部署管理 |
| `ResourceController` | `/watcher/resource/*` | 资源 CRUD |
| `MetricController` | `/watcher/metric/*` | 指标查询 |
| `LoginController` | `/watcher/user/*` | 用户登录 |
| `LogController` | `/watcher/log/*` | 日志查询 |

### 3.2 RAG API (`watcher-ai/src/watcher_ai/api/knowledge.py`)

| 路径 | 方法 | 说明 |
|------|------|------|
| `/api/knowledge/kbs` | POST | 创建知识库 |
| `/api/knowledge/kbs` | GET | 列出知识库 |
| `/api/knowledge/kbs/{id}/build` | POST | 构建向量 |
| `/api/knowledge/chat` | POST | RAG 问答 |

### 3.3 健康检查
```
Java:   GET http://localhost:8888/watcher/health
RAG:    GET http://localhost:8000/health
前端:   http://localhost:9090
```

---

## 4. 开发规范

### 4.1 代码规范
```bash
# Java: 阿里巴巴开发规范
mvn checkstyle:check

# TypeScript: ESLint + Prettier
cd watcher-web && npm run lint

# Python: Black + Ruff
cd watcher-ai && ruff check src/
```

### 4.2 构建命令
```bash
# 全量构建
mvn clean package -DskipTests

# 单模块构建
mvn clean package -DskipTests -pl watcher-agent -am

# 前端构建
cd watcher-web && npm install && npm run build
```

### 4.3 Git 提交规范
```
story:     新功能
bugfix:    Bug 修复
docs:      文档更新
style:     代码格式
refactor:  重构
test:      测试
chore:     构建/工具
```

### 4.4 工作区规则
1. 不在循环中请求数据库或调用接口
2. 遍历集合优先使用 for 循环，避免 while/foreach/stream/iterator
3. 方法不返回 null，使用抛异常或返回 Optional

---

## 5. 数据库

| 数据库 | 路径/配置 | 用途 |
|--------|-----------|------|
| MySQL | `application.properties` 中配置 | 配置存储 |
| ChromaDB | `watcher-ai/src/chroma_db/` | 向量存储 |
| KB 元数据 | `watcher-ai/data/kb_store.json` | 知识库信息 |

**MySQL 连接**: `jdbc:mariadb://localhost:3306/watcher_db`

---

## 6. 环境变量

### 6.1 RAG 服务
```bash
OLLAMA_EMBED_URL=http://localhost:11434/api/embeddings
OLLAMA_EMBED_MODEL=bge-m3
MINIMAX_API_KEY=your-api-key
```

### 6.2 Java 后端
```bash
# application-*.properties 中配置
spring.datasource.url=jdbc:mariadb://localhost:3306/watcher_db
```

---

## 7. 故障排查

| 问题 | 解决方案 |
|------|----------|
| RAG 服务启动失败 | `lsof -i :8000` 检查端口，`tail -f watcher-ai/src/chroma_db/chroma.log` |
| 前端构建失败 | `cd watcher-web && rm -rf node_modules && npm install` |
| Java 服务启动失败 | `lsof -i :8888`，`tail -f watcher-agent/watcher-agent.log` |

---

## 8. 工具命令

```bash
# 代码检查
make lint              # 运行所有 linter
make lint-deps         # 检查依赖
make lint-quality      # 检查代码质量

# 构建
make build             # 构建所有服务
make build-java        # 仅构建 Java
make build-web         # 仅构建前端

# 测试
make test              # 运行所有测试

# 启动服务
make start-java        # 启动 Java (8888)
make start-web         # 启动前端 (9090)
make start-python      # 启动 Python RAG (8000)
```

---

## 9. 相关文档

| 文档 | 路径 | 描述 |
|------|------|------|
| 架构文档 | `docs/ARCHITECTURE.md` | 系统架构详解 |
| 开发指南 | `docs/DEVELOPMENT.md` | 开发环境配置 |
| 知识库设计 | `docs/knowledge/` | RAG 知识库设计 |
| AI Agent 工作流 | `docs/superpowers/` | Superpowers 规范 |

---

---

## 10. Graphify 代码图谱

本项目已通过 `graphify` 生成了代码知识图谱，详细记录了 **11743 个节点、15359 条边、878 个功能社区**。

### 10.1 图谱文件

| 文件 | 说明 |
|------|------|
| `graphify-out/GRAPH_REPORT.md` | 社区导航报告，列出所有社区及其包含的代码元素 |
| `graphify-out/.graphify_analysis.json` | 社区→节点映射，可用于精确定位文件/符号所属社区 |
| `graphify-out/graph.json` | 完整图数据 |

### 10.2 何时使用

**必须查询 graphify 的场景：**
- 修改某个类或方法时：先查它属于哪个社区，找出同社区内会受影响的相关文件
- 新增功能模块时：查是否有现成社区可以直接扩展
- 理解陌生代码时：按社区导航，先看社区边界再看代码
- 评估改动影响范围时：查社区的出入边（外部依赖方）

### 10.3 使用流程

```
1. 在 GRAPH_REPORT.md 或 .graphify_analysis.json 中搜索目标类/方法名
2. 定位所属社区编号
3. 查看该社区内所有节点 → 这些是强相关的协同修改单元
4. 再读取实际代码文件进行修改
```

示例：要修改 `HostCpuUsageMonitorCollector`，先查图谱找到它属于 *Community 56*（含 `HostCpuUsageCollector, HostNetIopsCollector, DateTimeTool`），修改时需一并评估这些关联类。

### 10.4 保持图谱更新

```bash
# 重要代码变更后更新图谱（无需 API 费用，基于静态分析）
graphify update .

# 完全重建（极少需要）
graphify run .
```

---

**最后更新**: 2026-05-18
