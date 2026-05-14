# ShowTime Makefile
# 远程运维终端管理系统

.PHONY: help build test lint start stop clean

# 帮助信息
help:
	@echo "ShowTime 开发工具"
	@echo "======================================"
	@echo ""
	@echo "代码检查:"
	@echo "  make lint              - 运行所有代码检查"
	@echo "  make lint-deps        - 检查依赖完整性"
	@echo "  make lint-quality      - 检查代码质量"
	@echo ""
	@echo "构建:"
	@echo "  make build             - 构建所有服务"
	@echo "  make build-java        - 仅构建 Java 后端"
	@echo "  make build-web         - 仅构建前端"
	@echo "  make build-python      - 仅构建 Python 服务"
	@echo ""
	@echo "测试:"
	@echo "  make test              - 运行所有测试"
	@echo "  make test-java         - 运行 Java 测试"
	@echo "  make test-python       - 运行 Python 测试"
	@echo ""
	@echo "服务:"
	@echo "  make start             - 启动所有服务"
	@echo "  make start-java        - 启动 Java 后端"
	@echo "  make start-web         - 启动前端"
	@echo "  make start-python      - 启动 Python RAG"
	@echo "  make start-ollama      - 启动 Ollama"
	@echo ""
	@echo "清理:"
	@echo "  make clean             - 清理构建产物"

# ======================================
# 代码检查
# ======================================

lint: lint-deps lint-quality

lint-deps:
	@echo "检查依赖完整性..."
	@bash scripts/lint-deps

lint-quality:
	@echo "检查代码质量..."
	@bash scripts/lint-quality

# ======================================
# 构建
# ======================================

build: build-java build-web build-python

build-java:
	@echo "构建 Java 后端..."
	@cd watcher-agent && mvn clean package -DskipTests
	@echo "Java 构建完成: watcher-agent/target/agent.jar"

build-web:
	@echo "构建前端..."
	@cd watcher-web && npm install && npm run build
	@echo "前端构建完成"

build-python:
	@echo "检查 Python 依赖..."
	@pip install -r watcher-ai/requirements.txt
	@echo "Python 构建完成"

# ======================================
# 测试
# ======================================

test: test-java test-python

test-java:
	@echo "运行 Java 测试..."
	@cd watcher-agent && mvn test

test-python:
	@echo "运行 Python 测试..."
	@cd watcher-ai && python -m pytest tests/ -v

# ======================================
# 服务启动 (需要多个终端)
# ======================================

start:
	@echo "请在多个终端中分别运行:"
	@echo "  make start-java      - 终端 1: Java 后端 (8888)"
	@echo "  make start-web       - 终端 2: 前端 (9090)"
	@echo "  make start-python    - 终端 3: Python RAG (8000)"
	@echo "  make start-ollama    - 终端 4: Ollama (可选)"

start-java:
	@echo "启动 Java 后端 (端口 8888)..."
	@cd watcher-agent && java -jar target/agent.jar --spring.profiles.active=dev

start-web:
	@echo "启动前端 (端口 9090)..."
	@cd watcher-web && npm run dev

start-python:
	@echo "启动 Python RAG 服务 (端口 8000)..."
	@cd watcher-ai/src && python -m uvicorn watcher_ai.main:app --reload --port 8000

start-ollama:
	@echo "启动 Ollama (端口 11434)..."
	@ollama serve

# ======================================
# 清理
# ======================================

clean:
	@echo "清理构建产物..."
	@cd watcher-agent && mvn clean
	@cd watcher-web && rm -rf dist node_modules/.vite
	@rm -rf watcher-ai/src/watcher_ai/__pycache__
	@rm -rf watcher-ai/tests/__pycache__
	@find . -type d -name "__pycache__" -exec rm -rf {} + 2>/dev/null || true
	@find . -type f -name "*.pyc" -delete 2>/dev/null || true
	@echo "清理完成"

# ======================================
# 快捷命令
# ======================================

# 快速检查 (lint + build)
check: lint build

# 快速启动开发环境
dev: start-ollama start-java start-web start-python

# 查看服务状态
status:
	@echo "检查服务状态..."
	@echo ""
	@lsof -i :8888 2>/dev/null | grep LISTEN && echo "Java 后端: 运行中" || echo "Java 后端: 未运行"
	@lsof -i :9090 2>/dev/null | grep LISTEN && echo "前端: 运行中" || echo "前端: 未运行"
	@lsof -i :8000 2>/dev/null | grep LISTEN && echo "Python RAG: 运行中" || echo "Python RAG: 未运行"
	@lsof -i :11434 2>/dev/null | grep LISTEN && echo "Ollama: 运行中" || echo "Ollama: 未运行"
