# Proposal

## Problem

当前 ShowTime 监控系统缺少知识库问答功能。用户需要：
1. 管理多个知识库（创建、删除、构建）
2. 上传和管理文档（PDF、Markdown、TXT）
3. 基于知识库进行 RAG 问答

参考项目使用 LangChain + ChromaDB + Ollama Embedding 实现 RAG 功能。

## Testable Behaviors

### 知识库管理
- WHEN `POST /api/knowledge/kbs` 被调用 THEN 创建新知识库并返回知识库信息
- WHEN `GET /api/knowledge/kbs` 被调用 THEN 返回所有知识库列表
- WHEN `GET /api/knowledge/kbs/{id}` 被调用 THEN 返回指定知识库详情
- WHEN `DELETE /api/knowledge/kbs/{id}` 被调用 THEN 删除指定知识库
- WHEN `POST /api/knowledge/kbs/{id}/build` 被调用 THEN 启动知识库构建任务

### 文档管理
- WHEN `POST /api/knowledge/kbs/{id}/documents` 上传文档 THEN 文档保存到知识库并返回文档信息
- WHEN `GET /api/knowledge/kbs/{id}/documents` 被调用 THEN 返回知识库内所有文档列表
- WHEN `GET /api/knowledge/kbs/{id}/documents/{doc_id}` 被调用 THEN 返回指定文档详情
- WHEN `DELETE /api/knowledge/kbs/{id}/documents/{doc_id}` 被调用 THEN 删除指定文档

### RAG 问答
- WHEN `POST /api/knowledge/chat` 发送问题 THEN 基于知识库检索相关片段并返回 AI 回答
- WHEN `POST /api/knowledge/chat` 发送问题且知识库为空 THEN 返回错误提示"请先构建知识库"

### 向量检索
- WHEN `POST /api/knowledge/kbs/{id}/search` 发送检索词 THEN 返回相关文档片段列表
- WHEN `GET /api/knowledge/kbs/{id}/stats` 被调用 THEN 返回知识库统计信息（文档数、块数）

## Acceptance Criteria

1. 用户可以创建、查看、删除知识库
2. 用户可以上传 PDF、Markdown、TXT 格式文档
3. 用户可以构建知识库（文档向量化存储到 ChromaDB）
4. 用户可以基于知识库进行自然语言问答
5. 前端页面支持知识库管理、文档管理、问答功能
6. 后端 API 完整且可独立测试
