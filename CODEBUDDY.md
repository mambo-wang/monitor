# CODEBUDDY.md This file provides guidance to CodeBuddy when working with code in this repository.

# ShowTime - AI Agent 远程运维监控系统

## 常用命令

### 构建
```bash
make build              # 全量构建 (Java + Web + Python)
make build-java         # Java 后端打包
make build-web          # 前端构建
```
### 代码检查
```bash
make lint               # 运行所有 linter
make lint-deps          # 检查依赖完整性
make lint-quality       # 检查代码质量
```
### 测试
```bash
make test               # 运行所有测试
make test-java          # Java: cd watcher-agent && mvn test
make test-python        # Python: cd watcher-ai && pytest tests/
```
### 开发服务
```bash
make start-java         # 启动 Java (8888): cd watcher-agent && java -jar target/agent.jar
make start-web          # 启动前端 (9090): cd watcher-web && npm run dev
make start-python       # 启动 RAG (8000): cd watcher-ai/src && uvicorn watcher_ai.main:app
```

---

## 架构概览

### 系统定位
ShowTime 是面向新华三云基产品线的监控系统，核心能力：日志采集检索、指标监控报表、RAG 知识库问答。

### 三层架构

**1. 采集层 (Java 插件体系)**
```
watcher-agent (主程序)
├── watcher-sdk (公共 DTO/配置/工具类)
├── watcher-cas (CAS 虚拟化平台)
├── watcher-uis (UIS 超融合)
├── watcher-workspace (Workspace 云桌面)
```
采集层采用插件化设计，各产品插件实现统一接口，共享 SDK 基础设施。

**2. 服务层**
- Java 后端 (8888): Spring Boot 2.5.12 + MyBatis-Plus，处理资源管理、部署、指标查询
- Python RAG (8000): FastAPI + ChromaDB，向量知识库 + LLM 问答

**3. 前端层**
- Vue 3 + TypeScript + Vite (9090)，Element Plus UI

### 数据流
```
插件采集 → MySQL 元数据存储
         → ChromaDB 向量存储 (RAG)
前端请求 → Java API → MySQL/ChromaDB
```

### 关键配置
- Java: `watcher-agent/src/main/resources/application-*.properties`
- RAG: `watcher-ai/src/watcher_ai/config.py`
- MySQL: `jdbc:mariadb://localhost:3306/watcher_db`

### 开发规范
1. 不在循环中请求数据库或调用接口
2. 遍历集合优先使用 for 循环
3. 方法不返回 null，返回 Optional 或抛异常
