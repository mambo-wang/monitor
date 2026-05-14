# ShowTime - AI Agent 开发规范

> **版本**: 1.0.0 | **更新**: 2026-05-14 | **技术栈**: Java + TypeScript + Python

---

## 1. 项目结构

```
ShowTime/
├── watcher-web/          # 前端 (Vue 3 + TypeScript + Vite)
├── watcher-ai/           # RAG 服务 (Python + FastAPI)
├── watcher-agent/        # Java 主应用 (Spring Boot)
├── watcher-sdk/          # Java SDK
├── watcher-cas/          # CAS 平台插件
├── watcher-uis/          # UIS 平台插件
├── watcher-workspace/    # Workspace 平台插件
├── watcher-onestor/      # OneStor 存储插件
├── watcher-builder/      # 构建打包
├── docs/                 # 架构文档
├── harness/              # Agent 基础设施
└── scripts/              # 工具脚本
```

---

## 2. 技术栈速查

### 2.1 Java 后端
| 组件 | 版本 |
|------|------|
| Java | 17 |
| Spring Boot | 2.5.12 |
| MyBatis-Plus | 3.5.x |
| 数据库 | MySQL/MariaDB |
| 构建 | Maven |

**入口**: `watcher-agent/src/main/java/.../WatcherAgentApplication.java`

**启动**: `java -jar watcher-agent/target/agent.jar --spring.profiles.active=prod`

### 2.2 TypeScript 前端
| 组件 | 版本 |
|------|------|
| Vue | 3.x (Composition API) |
| TypeScript | 4.1+ |
| Vite | 2.3+ |
| Element Plus | 1.2+ |
| 状态管理 | Vuex 4 |
| 路由 | Vue Router 4 |

**启动**: `cd watcher-web && npm run dev`

### 2.3 Python RAG 服务
| 组件 | 版本 |
|------|------|
| FastAPI | 0.136+ |
| ChromaDB | 1.5+ |
| Ollama | - |
| Embedding | bge-m3 |
| LLM | MiniMax-M2 |

**启动**: `cd watcher-ai/src && python -m uvicorn watcher_ai.main:app --reload --port 8000`

---

## 3. 服务端口

| 服务 | 端口 | 健康检查 |
|------|------|----------|
| Java 后端 | 8888 | GET /watcher/health |
| 前端 | 9090 | http://localhost:9090 |
| RAG API | 8000 | GET http://localhost:8000/health |

---

## 4. 关键文件路径

### 4.1 API 路由 (Python)
```
watcher-ai/src/watcher_ai/api/knowledge.py
  POST /api/knowledge/kbs              # 创建知识库
  GET  /api/knowledge/kbs              # 列出知识库
  POST /api/knowledge/kbs/{id}/build   # 构建知识库
  POST /api/knowledge/chat             # RAG 问答
```

### 4.2 Java 控制器
```
watcher-agent/src/main/java/com/virtual/cloud/om/agent/controller/
  DeployController.java    # 部署管理 /deploy
  ResourceController.java # 资源管理 /resource
  MetricController.java   # 指标查询 /metric
  LoginController.java    # 登录认证 /user
```

### 4.3 前端路由
```
watcher-web/src/router/
  modules/dashboard.ts     # 仪表盘路由
  modules/resource.ts      # 资源管理路由
  modules/knowledge.ts     # 知识库路由 (新增)
```

---

## 5. 开发规范

### 5.1 代码规范
```bash
# Java: 使用 checkstyle 配置
mvn checkstyle:check

# TypeScript: ESLint + Prettier
cd watcher-web && npm run lint

# Python: Black + Ruff
cd watcher-ai && ruff check src/
```

### 5.2 Git 提交规范
```
feat:     新功能
fix:      Bug 修复
docs:     文档更新
style:    代码格式
refactor: 重构
test:     测试
chore:    构建/工具
```

### 5.3 分支策略
- `main`: 生产环境
- `develop`: 开发环境
- `feature/*`: 功能分支
- `hotfix/*`: 热修复分支

---

## 6. 依赖管理

### 6.1 Java (Maven)
```bash
cd watcher-agent
mvn clean package -DskipTests
```

### 6.2 前端 (npm)
```bash
cd watcher-web
npm install
npm run build
```

### 6.3 Python (pip/conda)
```bash
# 推荐使用 conda 环境
conda create -n showtime python=3.11
conda activate showtime
pip install -r watcher-ai/requirements.txt
```

---

## 7. 数据库迁移

**MySQL**: 使用 MyBatis-Plus 自动建表

**ChromaDB**: 本地向量数据库
```
路径: watcher-ai/src/chroma_db/
持久化: 自动
```

**KB 元数据**: JSON 文件
```
路径: watcher-ai/data/kb_store.json
```

---

## 8. 环境变量

### 8.1 Python RAG 服务
```bash
OLLAMA_EMBED_URL=http://localhost:11434/api/embeddings
OLLAMA_EMBED_MODEL=bge-m3
MINIMAX_API_KEY=your-api-key
```

### 8.2 Java 后端
```bash
# application-*.properties 中配置
spring.datasource.url=jdbc:mariadb://localhost:3306/watcher_db
```

---

## 9. 故障排查

### 9.1 RAG 服务启动失败
```bash
# 检查端口占用
lsof -i :8000

# 检查依赖
python -c "from watcher_ai.main import app"

# 查看日志
tail -f watcher-ai/src/chroma_db/chroma.log
```

### 9.2 前端构建失败
```bash
cd watcher-web
rm -rf node_modules
npm install
npm run build
```

### 9.3 Java 服务启动失败
```bash
# 检查端口
lsof -i :8888

# 查看日志
tail -f watcher-agent/watcher-agent.log
```

---

## 10. 相关文档

| 文档 | 路径 | 描述 |
|------|------|------|
| 架构文档 | `docs/ARCHITECTURE.md` | 系统架构详解 |
| 开发指南 | `docs/DEVELOPMENT.md` | 开发环境配置 |
| RAG 知识库 | `docs/knowledge/` | 知识库设计文档 |
| Superpowers | `docs/superpowers/` | AI Agent 工作流 |

---

## 11. 工具命令

```bash
# 代码检查
make lint              # 运行所有 linter
make lint-deps        # 检查依赖
make lint-quality     # 检查代码质量

# 构建
make build            # 构建所有服务
make build-java      # 仅构建 Java
make build-web       # 仅构建前端
make build-python    # 仅构建 Python

# 测试
make test             # 运行所有测试
make test-python     # Python 测试

# 启动服务
make start            # 启动所有服务 (需要终端)
make start-java       # 仅启动 Java
make start-web        # 仅启动前端
make start-python     # 仅启动 Python
```

---

**最后更新**: 2026-05-14
