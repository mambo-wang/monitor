# 知识库对话历史功能 - 实现计划

**Change**: `add-knowledge-base-chat-history`
**创建日期**: 2026-05-15
**技术栈**: Python FastAPI + MySQL + ChromaDB
**模块**: watcher-ai

---

## 里程碑 (Milestones)

| 里程碑 | 描述 | 包含任务 |
|--------|------|----------|
| **M1: 数据层** | 数据库表结构创建 | 1.1, 1.2 |
| **M2: API 端点** | 对话历史 CRUD API | 2.1, 2.2, 3.1, 3.2, 4.1, 4.2, 5.1, 5.2 |
| **M3: 路由集成** | 路由注册与验证 | 6.1, 6.2 |

---

## 任务详情

### M1: 数据层

---

<!-- openspec-task: 1.1 -->
### 🔴 Task 1: RED - 编写数据库迁移测试

**文件**: `watcher-ai/tests/test_chat_migration.py`

**测试目标**: 验证 `chat_sessions` 和 `chat_messages` 表结构正确

**测试内容**:
```python
def test_chat_sessions_table_exists():
    """验证 chat_sessions 表存在且结构正确"""

def test_chat_messages_table_exists():
    """验证 chat_messages 表存在且结构正确"""

def test_foreign_key_cascade():
    """验证删除会话时消息级联删除"""
```

**验收标准**:
- [ ] 测试文件 `test_chat_migration.py` 存在
- [ ] 包含表结构验证测试
- [ ] 包含外键级联删除测试
- [ ] 测试在表不存在时失败

---

<!-- openspec-task: 1.2 -->
### 🟢 Task 2: GREEN - 创建数据库迁移脚本

**文件**: `scripts/migrate_chat_history.sql`

**实现内容**:

```sql
-- 创建 chat_sessions 表
CREATE TABLE IF NOT EXISTS chat_sessions (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    kb_id VARCHAR(36) NOT NULL,
    title VARCHAR(255) DEFAULT '',
    message_count INT DEFAULT 0,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_updated_at (updated_at)
);

-- 创建 chat_messages 表
CREATE TABLE IF NOT EXISTS chat_messages (
    id INT AUTO_INCREMENT PRIMARY KEY,
    session_id VARCHAR(36) NOT NULL,
    role VARCHAR(16) NOT NULL,
    content TEXT,
    sources JSON,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (session_id) REFERENCES chat_sessions(id) ON DELETE CASCADE,
    INDEX idx_session_id (session_id)
);
```

**验收标准**:
- [ ] SQL 文件存在于 `scripts/migrate_chat_history.sql`
- [ ] 包含 `chat_sessions` 表创建语句
- [ ] 包含 `chat_messages` 表创建语句
- [ ] 包含外键和索引定义
- [ ] 使用 `IF NOT EXISTS` 防止重复创建

---

### M2: API 端点

---

<!-- openspec-task: 2.1 -->
### 🔴 Task 3: RED - 编写获取历史列表 API 测试

**文件**: `watcher-ai/tests/test_chat_history_api.py`

**测试目标**: `GET /api/knowledge/chat/history` 返回用户对话列表

**测试内容**:
```python
def test_list_history_returns_user_sessions():
    """验证返回用户会话列表"""

def test_list_history_sorted_by_updated_at():
    """验证按更新时间倒序排列"""

def test_list_history_with_pagination():
    """验证分页功能"""

def test_list_history_empty_user():
    """验证空用户返回空列表"""
```

**验收标准**:
- [ ] 测试覆盖正常路径
- [ ] 测试覆盖分页场景
- [ ] 测试覆盖空结果场景
- [ ] 使用 FastAPI TestClient

---

<!-- openspec-task: 2.2 -->
### 🟢 Task 4: GREEN - 实现获取历史列表 API

**文件**: `watcher-ai/src/watcher_ai/api/chat_history.py` (新建)

**实现内容**:

```python
@router.get("/chat/history")
def list_chat_history(
    user_id: str,
    page: int = Query(1, ge=1),
    page_size: int = Query(20, ge=1, le=100)
):
    """获取用户对话历史列表"""
    result = ChatRepository.list_sessions_by_user(user_id, page, page_size)
    return ok_response(result)
```

**验收标准**:
- [ ] 端点 `GET /api/knowledge/chat/history` 可访问
- [ ] 支持 `user_id`, `page`, `page_size` 参数
- [ ] 返回结果包含 sessions, total, page, page_size
- [ ] 返回格式为 `{"state": 0, "data": {...}}`

---

<!-- openspec-task: 3.1 -->
### 🔴 Task 5: RED - 编写获取会话详情 API 测试

**文件**: `watcher-ai/tests/test_chat_history_api.py`

**测试目标**: `GET /api/knowledge/chat/sessions/{session_id}` 返回会话详情

**测试内容**:
```python
def test_get_session_detail_returns_info_and_messages():
    """验证返回会话信息和消息列表"""

def test_get_session_not_found():
    """验证不存在的会话返回 404"""

def test_get_session_access_denied():
    """验证跨用户访问返回 403"""
```

**验收标准**:
- [ ] 测试覆盖正常路径
- [ ] 测试覆盖 404 场景
- [ ] 测试覆盖权限校验（403）

---

<!-- openspec-task: 3.2 -->
### 🟢 Task 6: GREEN - 实现获取会话详情 API

**文件**: `watcher-ai/src/watcher_ai/api/chat_history.py`

**实现内容**:

```python
@router.get("/chat/sessions/{session_id}")
def get_session_detail(session_id: str, user_id: str = Query(...)):
    """获取会话详情及消息"""
    session = ChatRepository.get_session_by_id(session_id)
    if not session:
        raise HTTPException(status_code=404, detail="Session not found")
    if session['user_id'] != user_id:
        raise HTTPException(status_code=403, detail="Access denied")
    messages = ChatRepository.list_messages_by_session(session_id)
    return ok_response({
        'session': session,
        'messages': messages
    })
```

**验收标准**:
- [ ] 端点 `GET /api/knowledge/chat/sessions/{session_id}` 可访问
- [ ] 返回会话信息和消息列表
- [ ] 不存在的会话返回 404
- [ ] 跨用户访问返回 403

---

<!-- openspec-task: 4.1 -->
### 🔴 Task 7: RED - 编写删除会话 API 测试

**文件**: `watcher-ai/tests/test_chat_history_api.py`

**测试目标**: `DELETE /api/knowledge/chat/sessions/{session_id}` 删除会话

**测试内容**:
```python
def test_delete_session_success():
    """验证删除成功"""

def test_delete_session_not_found():
    """验证删除不存在的会话返回 404"""

def test_delete_session_access_denied():
    """验证跨用户删除返回 403"""
```

**验收标准**:
- [ ] 测试覆盖删除成功
- [ ] 测试覆盖 404 场景
- [ ] 测试覆盖权限校验

---

<!-- openspec-task: 4.2 -->
### 🟢 Task 8: GREEN - 实现删除会话 API

**文件**: `watcher-ai/src/watcher_ai/api/chat_history.py`

**实现内容**:

```python
@router.delete("/chat/sessions/{session_id}")
def delete_session(session_id: str, user_id: str = Query(...)):
    """删除对话会话"""
    session = ChatRepository.get_session_by_id(session_id)
    if not session:
        raise HTTPException(status_code=404, detail="Session not found")
    if session['user_id'] != user_id:
        raise HTTPException(status_code=403, detail="Access denied")
    ChatRepository.delete_session(session_id)
    return ok_response({"message": "deleted"})
```

**验收标准**:
- [ ] 端点 `DELETE /api/knowledge/chat/sessions/{session_id}` 可访问
- [ ] 删除成功返回 `{"state": 0, "data": {"message": "deleted"}}`
- [ ] 不存在的会话返回 404
- [ ] 跨用户删除返回 403

---

<!-- openspec-task: 5.1 -->
### 🔴 Task 9: RED - 编写带历史的问答 API 测试

**文件**: `watcher-ai/tests/test_chat_history_api.py`

**测试目标**: `POST /api/knowledge/chat/with-history` 自动保存问答

**测试内容**:
```python
def test_chat_with_history_creates_session():
    """验证新问答创建会话并保存"""

def test_chat_with_history_saves_user_and_assistant_messages():
    """验证同时保存用户消息和 AI 回复"""

def test_chat_with_history_returns_answer_and_session_id():
    """验证返回答案和 session_id"""
```

**验收标准**:
- [ ] 测试覆盖会话创建
- [ ] 测试覆盖消息保存
- [ ] 测试覆盖返回格式

---

<!-- openspec-task: 5.2 -->
### 🟢 Task 10: GREEN - 实现带历史的问答 API

**文件**: `watcher-ai/src/watcher_ai/api/chat_history.py`

**实现内容**:

```python
class ChatWithHistoryRequest(BaseModel):
    user_id: str
    kb_id: str
    question: str
    session_id: Optional[str] = None

@router.post("/chat/with-history")
def chat_with_history(req: ChatWithHistoryRequest):
    """带历史的 RAG 问答，自动保存记录"""
    # 1. 获取或创建会话
    if req.session_id:
        session = ChatRepository.get_session_by_id(req.session_id)
        if not session or session['user_id'] != req.user_id:
            raise HTTPException(status_code=403, detail="Invalid session")
    else:
        session = ChatRepository.create_session(req.user_id, req.kb_id)

    # 2. 执行 RAG 问答
    kb = KBService.get_by_id(req.kb_id)
    if not kb:
        raise HTTPException(status_code=404, detail="Knowledge base not found")
    results = ChromaService.search(req.kb_id, req.question, top_k=3)
    context = "\n\n".join([doc for doc in results.get("documents", [[]])[0]] or [])
    answer = LLMService.chat(req.question, context)

    # 3. 保存消息
    ChatRepository.save_message(session['id'], "user", req.question)
    ChatRepository.save_message(session['id'], "assistant", answer)

    return ok_response({
        "answer": answer,
        "session_id": session['id']
    })
```

**验收标准**:
- [ ] 端点 `POST /api/knowledge/chat/with-history` 可访问
- [ ] 自动创建新会话（如未指定 session_id）
- [ ] 同时保存用户消息和 AI 回复
- [ ] 返回答案和 session_id

---

### M3: 路由集成

---

<!-- openspec-task: 6.1 -->
### 🔴 Task 11: RED - 编写路由注册验证测试

**文件**: `watcher-ai/tests/test_chat_history_api.py`

**测试目标**: 验证路由正确注册到 FastAPI 应用

**测试内容**:
```python
def test_router_registered_in_app():
    """验证 chat_history 路由已注册"""
    from watcher_ai.main import app
    # 检查路由是否存在
```

**验收标准**:
- [ ] 测试验证路由已注册
- [ ] 使用 `app.url_path_for()` 验证

---

<!-- openspec-task: 6.2 -->
### 🟢 Task 12: GREEN - 注册 API 路由

**文件**: `watcher-ai/src/watcher_ai/main.py`

**修改内容**:

```python
from watcher_ai.api.chat_history import router as chat_history_router

# 在 app 创建后添加
app.include_router(chat_history_router)
```

**验收标准**:
- [ ] `chat_history` 路由已注册到 FastAPI 应用
- [ ] 所有端点可正常访问
- [ ] CORS 配置正确

---

## 测试运行命令

```bash
# 运行数据库迁移
mysql -u root -p watcher_db < scripts/migrate_chat_history.sql

# 运行所有测试
cd watcher-ai && pytest tests/ -v

# 运行 API 测试
cd watcher-ai && pytest tests/test_chat_history_api.py -v

# 运行迁移测试
cd watcher-ai && pytest tests/test_chat_migration.py -v
```

---

## 验收总览

| 里程碑 | 状态 | 验收项 |
|--------|------|--------|
| M1 | ⬜ 未开始 | SQL 脚本执行成功，两个表创建完成 |
| M2 | ⬜ 未开始 | 所有 API 端点正常工作，测试全部通过 |
| M3 | ⬜ 未开始 | 路由注册成功，无 404 错误 |

### 最终验收检查

- [ ] `GET /api/knowledge/chat/history` 返回用户历史列表
- [ ] `GET /api/knowledge/chat/sessions/{id}` 返回会话详情
- [ ] `DELETE /api/knowledge/chat/sessions/{id}` 删除会话
- [ ] `POST /api/knowledge/chat/with-history` 保存问答历史
- [ ] 用户间数据隔离正常（403 响应）
- [ ] 所有 pytest 测试通过

---

**计划完成时间**: ~60 分钟（12 个 TDD 任务）
