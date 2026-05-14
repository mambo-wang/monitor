# ShowTime 开发指南

> **版本**: 1.0.0 | **更新**: 2026-05-14

---

## 1. 环境要求

### 1.1 必需软件

| 软件 | 版本 | 用途 |
|------|------|------|
| Java JDK | 17+ | Java 后端开发 |
| Node.js | 18+ | 前端开发 |
| Python | 3.11+ | RAG 服务开发 |
| Maven | 3.6+ | Java 构建 |
| Git | 2.0+ | 版本控制 |
| Conda | - | Python 环境管理 |

### 1.2 推荐 IDE

| 用途 | IDE | 插件 |
|------|-----|------|
| Java | IntelliJ IDEA | Lombok, MyBatisX |
| TypeScript | VS Code | Vue - Official, ESLint |
| Python | PyCharm | Python, Ruff |

---

## 2. 本地开发环境配置

### 2.1 克隆代码

```bash
git clone https://github.com/mambo-wang/ShowTime.git
cd ShowTime
```

### 2.2 Java 环境

```bash
# 检查 Java 版本
java -version
# openjdk 17.x

# Maven 项目构建
cd watcher-agent
mvn clean install -DskipTests
```

### 2.3 前端环境

```bash
cd watcher-web

# 安装依赖
npm install

# 开发模式启动 (端口 9090)
npm run dev

# 生产构建
npm run build
```

### 2.4 Python 环境

```bash
# 创建 conda 环境
conda create -n showtime python=3.11
conda activate showtime

# 安装依赖
cd watcher-ai
pip install -r requirements.txt

# 安装额外依赖
pip install fastapi uvicorn chromadb python-multipart pydantic httpx requests

# 启动 RAG 服务 (端口 8000)
cd src
python -m uvicorn watcher_ai.main:app --reload --port 8000
```

### 2.5 Ollama (Embedding 服务)

```bash
# 安装 Ollama
brew install ollama  # macOS
# 或: curl -fsSL https://ollama.com/install.sh | sh

# 启动 Ollama 服务
ollama serve

# 下载 bge-m3 模型 (用于 Embedding)
ollama pull bge-m3
```

---

## 3. 启动服务

### 3.1 方式一：单独启动

```bash
# 终端 1: Java 后端
cd watcher-agent
java -jar target/agent.jar --spring.profiles.active=dev

# 终端 2: 前端
cd watcher-web
npm run dev

# 终端 3: Python RAG
cd watcher-ai/src
python -m uvicorn watcher_ai.main:app --reload --port 8000

# 终端 4: Ollama (如果需要本地 Embedding)
ollama serve
```

### 3.2 方式二：使用 Makefile

```bash
# 查看可用命令
make help

# 启动所有服务
make start

# 单独启动
make start-java
make start-web
make start-python
```

---

## 4. 服务端口

| 服务 | 端口 | 访问地址 |
|------|------|----------|
| Java 后端 | 8888 | http://localhost:8888/watcher |
| 前端 | 9090 | http://localhost:9090 |
| RAG API | 8000 | http://localhost:8000 |
| Ollama | 11434 | http://localhost:11434 |

---

## 5. 数据库配置

### 5.1 MySQL

```properties
# watcher-agent/src/main/resources/application-dev.properties
spring.datasource.url=jdbc:mariadb://localhost:3306/watcher_db
spring.datasource.username=root
spring.datasource.password=your_password
```

### 5.2 ChromaDB

```bash
# 默认路径: watcher-ai/src/chroma_db
# 自动创建，无需手动配置
```

### 5.3 KB 元数据

```bash
# 路径: watcher-ai/data/kb_store.json
# 自动创建
```

---

## 6. API 测试

### 6.1 Java 后端

```bash
# 健康检查
curl http://localhost:8888/watcher/health

# 登录
curl -X POST http://localhost:8888/watcher/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"xxx"}'
```

### 6.2 RAG API

```bash
# 健康检查
curl http://localhost:8000/health

# 列出知识库
curl http://localhost:8000/api/knowledge/kbs

# 创建知识库
curl -X POST http://localhost:8000/api/knowledge/kbs \
  -H "Content-Type: application/json" \
  -d '{"name":"测试KB","description":"测试"}'

# 上传文档
curl -X POST http://localhost:8000/api/knowledge/kbs/{kb_id}/documents \
  -F "file=@/path/to/document.pdf"

# 构建知识库
curl -X POST http://localhost:8000/api/knowledge/kbs/{kb_id}/build

# RAG 问答
curl -X POST http://localhost:8000/api/knowledge/chat \
  -H "Content-Type: application/json" \
  -d '{"kb_id":"xxx","question":"什么是云桌面"}'
```

---

## 7. 测试

### 7.1 Java 测试

```bash
cd watcher-agent
mvn test

# 运行特定测试
mvn test -Dtest=DeployControllerTest
```

### 7.2 前端测试

```bash
cd watcher-web
npm run test
npm run test:unit
```

### 7.3 Python 测试

```bash
cd watcher-ai
python -m pytest tests/ -v

# 运行特定测试
python -m pytest tests/test_rag_api.py -v
```

---

## 8. 代码规范

### 8.1 Java

- 遵循 Google Java Style Guide
- 使用 Lombok 减少样板代码
- Controller → Service → Mapper 分层

### 8.2 TypeScript

- ESLint + Prettier 配置
- Vue 3 Composition API
- TypeScript 严格模式

### 8.3 Python

- Black 代码格式化
- Ruff Lint 检查
- 类型注解 (typing)

---

## 9. Git 工作流

### 9.1 分支命名

```
feature/rag-knowledge-base    # 新功能
fix/kb-persistence           # Bug 修复
hotfix/emergency-fix        # 热修复
```

### 9.2 提交规范

```bash
git commit -m "feat: 添加 RAG 知识库功能"
git commit -m "fix: 修复 KBService 持久化问题"
git commit -m "docs: 更新开发文档"
```

### 9.3 推送和拉取

```bash
# 推送
git push origin feature/rag-knowledge-base

# 拉取并变基
git pull --rebase origin develop
```

---

## 10. 常见问题

### 10.1 Python 服务启动失败

```bash
# 检查依赖
python -c "from watcher_ai.main import app"

# 检查端口占用
lsof -i :8000

# 查看详细错误
python -m uvicorn watcher_ai.main:app --reload --port 8000 --log-level debug
```

### 10.2 前端构建失败

```bash
cd watcher-web
rm -rf node_modules package-lock.json
npm install
npm run build
```

### 10.3 Java 服务启动失败

```bash
# 检查端口占用
lsof -i :8888

# 查看日志
tail -f watcher-agent/watcher-agent.log

# 清理并重新构建
mvn clean package
```

### 10.4 ChromaDB 数据丢失

```bash
# 检查 ChromaDB 路径
ls -la watcher-ai/src/chroma_db/

# 重建索引
# 1. 删除旧数据
rm -rf watcher-ai/src/chroma_db/

# 2. 重新构建知识库
# POST /api/knowledge/kbs/{id}/build
```

---

## 11. 环境变量参考

### 11.1 Python RAG 服务

```bash
export OLLAMA_EMBED_URL=http://localhost:11434/api/embeddings
export OLLAMA_EMBED_MODEL=bge-m3
export MINIMAX_API_KEY=your-api-key
```

### 11.2 Java 后端

```bash
# application.properties 中配置
export WATCHER_DB_HOST=localhost
export WATCHER_DB_PORT=3306
```

---

## 12. 相关文档

| 文档 | 路径 |
|------|------|
| 架构文档 | `docs/ARCHITECTURE.md` |
| AGENTS 规范 | `AGENTS.md` |
| 知识库设计 | `docs/knowledge/` |
| Superpowers | `docs/superpowers/` |

---

**最后更新**: 2026-05-14
