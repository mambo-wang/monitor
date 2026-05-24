# Proposal

## Problem

1. **知识库无法编辑**：当前知识库创建后只能删除，无法修改名称和描述
2. **文档列表不持久**：文档信息存储在内存中，服务重启后文档列表为空，但文件实际存在于 uploads 目录

## Testable Behaviors

### 知识库编辑
- WHEN `PATCH /api/knowledge/kbs/{kb_id}` 被调用且请求体包含 `name` THEN 知识库的名称被更新为新值
- WHEN `PATCH /api/knowledge/kbs/{kb_id}` 被调用且请求体包含 `description` THEN 知识库的描述被更新为新值
- WHEN `PATCH /api/knowledge/kbs/{kb_id}` 被调用且 `kb_id` 不存在 THEN 返回 404 错误

### 文档列表持久化
- WHEN `GET /api/knowledge/kbs/{kb_id}/documents` 被调用 THEN 返回 uploads/{kb_id}/ 目录下所有文件的列表
- WHEN `GET /api/knowledge/kbs/{kb_id}/documents` 被调用 THEN 每个文档的 `chunk_count` 从 ChromaDB 查询该文件对应的块数量
- WHEN 服务重启后调用 `GET /api/knowledge/kbs/{kb_id}/documents` THEN 返回的文档列表与重启前一致

## Acceptance Criteria

1. 知识库的 name 和 description 可以通过 API 修改
2. 文档列表通过扫描 uploads 目录实现，服务重启后数据不丢失
3. 文档的 chunk_count 从 ChromaDB 动态查询，不存储在内存