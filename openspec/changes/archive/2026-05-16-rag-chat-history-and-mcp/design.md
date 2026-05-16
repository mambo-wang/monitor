# Design

## File Structure

### 后端 (watcher-ai)

```
watcher-ai/src/watcher_ai/
├── api/
│   ├── chat_history.py          # 已有：会话管理 API
│   └── mcp_rag.py               # 新增：MCP RAG 工具服务
├── services/
│   ├── chat_repository.py       # 已有：会话数据访问
│   ├── chroma_service.py         # 已有：向量检索
│   └── llm_service.py           # 已有：LLM 服务
└── tests/
    ├── test_chat_history_api.py  # 已有
    └── test_mcp_rag.py           # 新增：MCP API 单元测试
```

### 前端 (watcher-web)

```
watcher-web/src/views/main/knowledge/
├── ChatAssistant.vue             # 已有：问答界面
├── HistorySessions.vue          # 新增：历史会话列表弹窗组件
├── DocumentManage.vue            # 已有
└── KnowledgeIndex.vue            # 已有
```

### Skill

```
.codebuddy/skills/
└── rag-chat-mcp/
    └── SKILL.md                  # 新增：RAG 聊天 MCP Skill
```

---

## Test Strategy

### test_mcp_rag.py
- 单元测试：测试 `rag_chat`、`list_sessions`、`delete_session` 三个 MCP 工具函数
- Mock ChromaService 和 ChatRepository
- 验证参数校验和错误处理

### test_chat_history_api.py (扩展)
- 集成测试：测试知识库级别的会话列表 API
- 验证 `GET /api/knowledge/chat/history?kb_id=xxx` 返回正确数据

### ChatAssistant.vue (新增 UI)
- UI 组件测试：手动测试"新建会话"和"历史会话"按钮交互

---

## Implementation Plan

### Phase 1: 后端扩展
1. 修改 `chat_history.py` 的 `list_chat_history` API，支持按 `kb_id` 筛选
2. 新增 `GET /api/knowledge/chat/history?kb_id=xxx` 按知识库查会话
3. 已有 API `POST /with-history` 已支持 `kb_id` 创建会话，无需大改

### Phase 2: MCP 服务
1. 创建 `mcp_rag.py`，实现三个 MCP 工具：
   - `rag_chat(kb_id, question, session_id?)` - 问答
   - `list_sessions(kb_id)` - 列出知识库会话
   - `delete_session(session_id)` - 删除会话
2. MCP 服务运行在独立的 `/mcp` 路径

### Phase 3: 前端 UI
1. 创建 `HistorySessions.vue` 弹窗组件
2. 修改 `ChatAssistant.vue`，右上角添加两个按钮
3. 点击"历史会话"显示弹窗，可删除和继续对话

### Phase 4: Skill
1. 创建 `rag-chat-mcp/SKILL.md`
2. 定义触发词和 MCP 调用逻辑

---

## API 设计

### 新增 MCP 工具

```python
# /mcp/rag_chat
{
  "kb_id": "kb001",        # 知识库 ID（与 session_id 二选一）
  "question": "CAS虚拟机状态有哪些？",
  "session_id": "sess001"  # 可选：继续指定会话
}

# /mcp/list_sessions
{
  "kb_id": "kb001"
}

# /mcp/delete_session
{
  "session_id": "sess001"
}
```

### 修改的 REST API

```
GET /api/knowledge/chat/history?kb_id=kb001&page=1&page_size=20
# 返回该知识库下的所有会话列表
```