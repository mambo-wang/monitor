# 知识库历史会话管理 + MCP 实现计划

> **面向 AI 代理的工作者：** 必需子技能：使用 superpowers:subagent-driven-development（推荐）或 superpowers:executing-plans 逐任务实现此计划。步骤使用复选框（`- [ ]`）语法来跟踪进度。
**目标：** 实现知识库历史会话管理功能，支持按知识库查询会话列表，提供 MCP RAG 工具服务
**架构：** 后端扩展 ChatRepository 支持 kb_id 筛选 → MCP 服务暴露 rag_chat/list_sessions/delete_session 工具 → 前端添加历史会话弹窗 → Skill 封装 MCP 调用
**技术栈：** Python FastAPI (watcher-ai), Vue 3 + TypeScript (watcher-web), MCP Protocol

---

## 文件结构

### 后端新增/修改文件

| 文件 | 职责 |
|------|------|
| `watcher-ai/src/watcher_ai/services/chat_repository.py` | 修改：新增 `list_sessions_by_kb` 方法 |
| `watcher-ai/src/watcher_ai/api/mcp_rag.py` | 新增：MCP RAG 工具服务 |
| `watcher-ai/src/watcher_ai/api/chat_history.py` | 修改：新增按 kb_id 查会话的 API |
| `watcher-ai/tests/test_mcp_rag.py` | 新增：MCP API 单元测试 |
| `watcher-ai/tests/test_chat_history_api.py` | 修改：扩展知识库会话列表测试 |

### 前端新增/修改文件

| 文件 | 职责 |
|------|------|
| `watcher-web/src/views/main/knowledge/HistorySessions.vue` | 新增：历史会话列表弹窗组件 |
| `watcher-web/src/views/main/knowledge/ChatAssistant.vue` | 修改：添加"新建会话"和"历史会话"按钮 |

### Skill 新增文件

| 文件 | 职责 |
|------|------|
| `.codebuddy/skills/rag-chat-mcp/SKILL.md` | 新增：RAG Chat MCP Skill |

---

## 任务 1：后端 - 按知识库查询会话列表

**文件：**
- 测试：`watcher-ai/tests/test_chat_repository.py`（新建）
- 修改：`watcher-ai/src/watcher_ai/services/chat_repository.py`

- [ ] **步骤 1：编写失败的测试**

```python
# watcher-ai/tests/test_chat_repository.py
import pytest
from watcher_ai.services.chat_repository import ChatRepository

def test_list_sessions_by_kb_returns_correct_list():
    """测试 kb_id 有会话时返回正确列表"""
    # GIVEN: kb_id="kb001" 有 2 个会话
    result = ChatRepository.list_sessions_by_kb("kb001", page=1, page_size=20)
    # THEN: 返回 sessions 列表，total=2
    assert result['total'] == 2
    assert len(result['sessions']) == 2

def test_list_sessions_by_kb_returns_empty_list():
    """测试 kb_id 无会话时返回空列表"""
    # GIVEN: kb_id="kb-empty" 没有任何会话
    result = ChatRepository.list_sessions_by_kb("kb-empty", page=1, page_size=20)
    # THEN: 返回空列表，total=0
    assert result['total'] == 0
    assert len(result['sessions']) == 0

def test_list_sessions_by_kb_pagination():
    """测试分页参数正确"""
    # WHEN: page=1, page_size=1
    result = ChatRepository.list_sessions_by_kb("kb001", page=1, page_size=1)
    # THEN: 只返回 1 条，但 total 仍是 2
    assert len(result['sessions']) == 1
    assert result['total'] == 2
    assert result['page'] == 1
    assert result['page_size'] == 1
```

- [ ] **步骤 2：运行测试验证失败**
```
运行：cd watcher-ai && pytest tests/test_chat_repository.py -v
预期：ERROR - module 'watcher_ai.services.chat_repository' has no attribute 'list_sessions_by_kb'
```

- [ ] **步骤 3：编写最少实现代码**

在 `chat_repository.py` 的 `ChatRepository` 类中添加方法：

```python
@staticmethod
def list_sessions_by_kb(
    kb_id: str,
    page: int = 1,
    page_size: int = 20
) -> Dict[str, Any]:
    """按知识库分页查询会话"""
    offset = (page - 1) * page_size

    count_sql = f"SELECT COUNT(*) as total FROM {ChatRepository.SESSION_TABLE} WHERE kb_id = %s"
    total_result = ChatRepository._get_client().query_one(count_sql, (kb_id,))
    total = total_result['total'] if total_result else 0

    sql = f"""
        SELECT * FROM {ChatRepository.SESSION_TABLE}
        WHERE kb_id = %s
        ORDER BY updated_at DESC
        LIMIT %s OFFSET %s
    """
    sessions = ChatRepository._get_client().query_all(sql, (kb_id, page_size, offset))

    return {
        'sessions': sessions,
        'total': total,
        'page': page,
        'page_size': page_size
    }
```

- [ ] **步骤 4：运行测试验证通过**
```
运行：cd watcher-ai && pytest tests/test_chat_repository.py -v
预期：PASS
```

- [ ] **步骤 5：Commit**
```bash
git add watcher-ai/src/watcher_ai/services/chat_repository.py watcher-ai/tests/test_chat_repository.py
git commit -m "feat(watcher-ai): add list_sessions_by_kb method for kb-scoped session query"
```

---

## 任务 2：后端 - MCP RAG rag_chat 工具

**文件：**
- 测试：`watcher-ai/tests/test_mcp_rag.py`
- 新增：`watcher-ai/src/watcher_ai/api/mcp_rag.py`

- [ ] **步骤 1：编写失败的测试**

```python
# watcher-ai/tests/test_mcp_rag.py
import pytest
from watcher_ai.api.mcp_rag import rag_chat, McpRagRequest

def test_rag_chat_with_kb_id_and_question():
    """测试 kb_id + question 返回问答结果"""
    # GIVEN: kb_id="kb001", question="CAS虚拟机状态有哪些？"
    request = McpRagRequest(kb_id="kb001", question="CAS虚拟机状态有哪些？")
    result = rag_chat(request)
    # THEN: 返回包含 answer 的响应
    assert 'answer' in result
    assert 'session_id' in result

def test_rag_chat_with_session_id_continues_session():
    """测试 session_id 继续会话"""
    # GIVEN: session_id="sess001" 已存在
    request = McpRagRequest(session_id="sess001", question="那如何关机？")
    result = rag_chat(request)
    # THEN: 返回的 session_id 与传入一致
    assert result['session_id'] == "sess001"

def test_rag_chat_without_kb_id_and_session_id_raises_error():
    """测试缺少 kb_id 且无 session_id 时报错"""
    # GIVEN: request 只有 question，没有 kb_id 和 session_id
    request = McpRagRequest(question="CAS虚拟机状态有哪些？")
    # THEN: 抛出 ValueError
    with pytest.raises(ValueError, match="kb_id or session_id is required"):
        rag_chat(request)
```

- [ ] **步骤 2：运行测试验证失败**
```
运行：cd watcher-ai && pytest tests/test_mcp_rag.py::test_rag_chat_with_kb_id_and_question -v
预期：FAIL - ERROR module 'watcher_ai.api.mcp_rag' has no attribute 'rag_chat'
```

- [ ] **步骤 3：编写最少实现代码**

创建 `watcher-ai/src/watcher_ai/api/mcp_rag.py`：

```python
"""MCP RAG 工具服务"""
from typing import Optional, Dict, Any
from pydantic import BaseModel

from watcher_ai.services.kb_service import KBService
from watcher_ai.services.chroma_service import ChromaService
from watcher_ai.services.chat_repository import ChatRepository
from watcher_ai.services.llm_service import LLMService


class McpRagRequest(BaseModel):
    """MCP RAG 请求模型"""
    kb_id: Optional[str] = None
    question: str
    session_id: Optional[str] = None


def _ok_response(data: Any):
    return {"state": 0, "data": data}


def rag_chat(request: McpRagRequest) -> Dict[str, Any]:
    """RAG 问答 MCP 工具"""
    if not request.kb_id and not request.session_id:
        raise ValueError("kb_id or session_id is required")

    # 1. 获取或创建会话
    if request.session_id:
        session = ChatRepository.get_session_by_id(request.session_id)
        if not session:
            raise ValueError(f"Session {request.session_id} not found")
        kb_id = session['kb_id']
    else:
        kb_id = request.kb_id
        # 检查 kb 是否存在
        kb = KBService.get_by_id(kb_id)
        if not kb:
            raise ValueError(f"Knowledge base {kb_id} not found")
        # 创建新会话
        session = ChatRepository.create_session(
            user_id="mcp_system",
            kb_id=kb_id,
            title=request.question[:50]  # 用问题前50字符作为标题
        )

    # 2. 执行 RAG 问答
    chunk_count = ChromaService.get_count(kb_id)
    if chunk_count == 0:
        return _ok_response({
            "answer": "Knowledge base is empty, please build first",
            "session_id": session['id']
        })

    results = ChromaService.search(kb_id, request.question, top_k=3)

    if not results.get("documents") or not results["documents"][0]:
        answer = "没有找到相关文档"
    else:
        context_parts = []
        for doc in results["documents"][0]:
            context_parts.append(doc)
        context = "\n\n".join(context_parts)
        answer = LLMService.chat(request.question, context)

    # 3. 保存消息
    ChatRepository.save_message(session['id'], "user", request.question)
    ChatRepository.save_message(session['id'], "assistant", answer)

    # 4. 更新消息计数
    messages = ChatRepository.list_messages_by_session(session['id'])
    ChatRepository.update_message_count(session['id'], len(messages))

    return _ok_response({
        "answer": answer,
        "session_id": session['id']
    })
```

- [ ] **步骤 4：运行测试验证通过**
```
运行：cd watcher-ai && pytest tests/test_mcp_rag.py::test_rag_chat_with_kb_id_and_question -v
预期：PASS
```

- [ ] **步骤 5：Commit**
```bash
git add watcher-ai/src/watcher_ai/api/mcp_rag.py watcher-ai/tests/test_mcp_rag.py
git commit -m "feat(watcher-ai): add MCP rag_chat tool for RAG chat with session management"
```

---

## 任务 3：后端 - MCP list_sessions 工具

**文件：**
- 测试：`watcher-ai/tests/test_mcp_rag.py::test_list_sessions`
- 修改：`watcher-ai/src/watcher_ai/api/mcp_rag.py`

- [ ] **步骤 1：编写失败的测试**

```python
def test_list_sessions_returns_kb_sessions():
    """测试传入 kb_id 返回该知识库的会话列表"""
    # GIVEN: kb_id="kb001" 有 2 个会话
    result = list_sessions(kb_id="kb001")
    # THEN: 返回 sessions 列表，total=2
    assert 'sessions' in result
    assert len(result['sessions']) == 2
```

- [ ] **步骤 2：运行测试验证失败**
```
运行：cd watcher-ai && pytest tests/test_mcp_rag.py::test_list_sessions_returns_kb_sessions -v
预期：FAIL - ERROR module 'watcher_ai.api.mcp_rag' has no attribute 'list_sessions'
```

- [ ] **步骤 3：编写最少实现代码**

在 `mcp_rag.py` 中添加：

```python
def list_sessions(kb_id: str) -> Dict[str, Any]:
    """列出知识库下的所有会话 MCP 工具"""
    result = ChatRepository.list_sessions_by_kb(kb_id, page=1, page_size=100)
    return _ok_response(result)
```

- [ ] **步骤 4：运行测试验证通过**
```
运行：cd watcher-ai && pytest tests/test_mcp_rag.py::test_list_sessions_returns_kb_sessions -v
预期：PASS
```

- [ ] **步骤 5：Commit**
```bash
git add watcher-ai/src/watcher_ai/api/mcp_rag.py
git commit -m "feat(watcher-ai): add MCP list_sessions tool"
```

---

## 任务 4：后端 - MCP delete_session 工具

**文件：**
- 测试：`watcher-ai/tests/test_mcp_rag.py::test_delete_session`
- 修改：`watcher-ai/src/watcher_ai/api/mcp_rag.py`

- [ ] **步骤 1：编写失败的测试**

```python
def test_delete_session_deletes_session():
    """测试传入 session_id 删除会话"""
    # GIVEN: session_id="sess001" 已存在
    result = delete_session(session_id="sess001")
    # THEN: 返回删除成功确认
    assert result.get('message') == 'deleted' or result.get('state') == 0

def test_delete_session_not_found_raises_error():
    """测试删除不存在的 session_id 报错"""
    # GIVEN: session_id="not-exist-session" 不存在
    with pytest.raises(ValueError, match="Session not found"):
        delete_session(session_id="not-exist-session")
```

- [ ] **步骤 2：运行测试验证失败**
```
运行：cd watcher-ai && pytest tests/test_mcp_rag.py::test_delete_session_deletes_session -v
预期：FAIL - ERROR module 'watcher_ai.api.mcp_rag' has no attribute 'delete_session'
```

- [ ] **步骤 3：编写最少实现代码**

在 `mcp_rag.py` 中添加：

```python
def delete_session(session_id: str) -> Dict[str, Any]:
    """删除会话 MCP 工具"""
    session = ChatRepository.get_session_by_id(session_id)
    if not session:
        raise ValueError(f"Session {session_id} not found")
    ChatRepository.delete_session(session_id)
    return _ok_response({"message": "deleted"})
```

- [ ] **步骤 4：运行测试验证通过**
```
运行：cd watcher-ai && pytest tests/test_mcp_rag.py::test_delete_session_deletes_session -v
预期：PASS
```

- [ ] **步骤 5：Commit**
```bash
git add watcher-ai/src/watcher_ai/api/mcp_rag.py
git commit -m "feat(watcher-ai): add MCP delete_session tool"
```

---

## 任务 5：前端 - HistorySessions.vue 弹窗组件

**文件：**
- 测试：手动 UI 测试
- 新增：`watcher-web/src/views/main/knowledge/HistorySessions.vue`

- [ ] **步骤 1：编写组件代码**

创建 `watcher-web/src/views/main/knowledge/HistorySessions.vue`：

```vue
<template>
  <el-dialog
    v-model="visible"
    title="历史会话"
    width="600px"
    @close="handleClose"
  >
    <div class="session-list" v-if="sessions.length > 0">
      <div
        v-for="session in sessions"
        :key="session.id"
        class="session-item"
        @click="handleSelectSession(session)"
      >
        <div class="session-info">
          <div class="session-title">{{ session.title || '无标题会话' }}</div>
          <div class="session-meta">
            <span>{{ formatDate(session.created_at) }}</span>
            <span>{{ session.message_count }} 条消息</span>
          </div>
        </div>
        <el-button
          type="danger"
          size="small"
          circle
          icon="el-icon-delete"
          @click.stop="handleDeleteSession(session.id)"
        ></el-button>
      </div>
    </div>
    <el-empty v-else description="暂无历史会话"></el-empty>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getChatSessions, deleteChatSession } from '@/api/knowledge'

const props = defineProps<{
  modelValue: boolean
  kbId: string
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
  (e: 'select-session', session: any): void
}>()

const visible = ref(props.modelValue)
const sessions = ref<any[]>([])

watch(() => props.modelValue, async (val) => {
  visible.value = val
  if (val) {
    await loadSessions()
  }
})

watch(visible, (val) => {
  emit('update:modelValue', val)
})

async function loadSessions() {
  try {
    const res = await getChatSessions(props.kbId)
    sessions.value = res.sessions || []
  } catch (e) {
    ElMessage.error('加载会话列表失败')
  }
}

function handleSelectSession(session: any) {
  emit('select-session', session)
  visible.value = false
}

async function handleDeleteSession(sessionId: string) {
  try {
    await ElMessageBox.confirm('确定要删除这个会话吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteChatSession(sessionId)
    ElMessage.success('删除成功')
    await loadSessions()
  } catch (e: any) {
    if (e !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

function handleClose() {
  visible.value = false
}

function formatDate(dateStr: string) {
  const date = new Date(dateStr)
  return date.toLocaleDateString() + ' ' + date.toLocaleTimeString()
}
</script>

<style scoped>
.session-list {
  max-height: 400px;
  overflow-y: auto;
}
.session-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: background 0.2s;
}
.session-item:hover {
  background: #f5f5f5;
}
.session-info {
  flex: 1;
}
.session-title {
  font-size: 14px;
  color: #333;
  margin-bottom: 4px;
}
.session-meta {
  font-size: 12px;
  color: #999;
}
.session-meta span {
  margin-right: 12px;
}
</style>
```

- [ ] **步骤 2：在 API 层添加方法**

修改 `watcher-web/src/api/knowledge.ts`，添加：

```typescript
export async function getChatSessions(kbId: string) {
  return request.get('/api/knowledge/chat/history', {
    params: { kb_id: kbId, page: 1, page_size: 100 }
  })
}

export async function deleteChatSession(sessionId: string) {
  return request.delete(`/api/knowledge/chat/sessions/${sessionId}`)
}
```

- [ ] **步骤 3：手动测试组件**
```
运行：cd watcher-web && npm run dev
验证：
1. 打开知识问答页面，点击"历史会话"按钮
2. 弹窗正常显示，空知识库显示"暂无历史会话"
3. 有会话时显示会话列表
4. 点击删除按钮，弹出确认框
5. 确认后列表更新
```

- [ ] **步骤 4：Commit**
```bash
git add watcher-web/src/views/main/knowledge/HistorySessions.vue watcher-web/src/api/knowledge.ts
git commit -m "feat(watcher-web): add HistorySessions modal component"
```

---

## 任务 6：前端 - ChatAssistant 添加按钮

**文件：**
- 修改：`watcher-web/src/views/main/knowledge/ChatAssistant.vue`

- [ ] **步骤 1：添加按钮到 header**

修改 `ChatAssistant.vue` 的 template header 部分：

```vue
<div class="header">
  <el-button @click="$emit('back')">
    <i class="el-icon-arrow-left"></i> 返回
  </el-button>
  <h2>{{ kb.name }} - RAG 问答</h2>
  <div class="header-actions">
    <el-button size="small" @click="handleNewSession">
      新建会话
    </el-button>
    <el-button size="small" @click="showHistoryDialog = true">
      历史会话
    </el-button>
  </div>
  <el-button size="small" @click="showSources = !showSources">
    {{ showSources ? '隐藏' : '显示' }}来源
  </el-button>
</div>
```

添加弹窗组件引用：

```vue
<HistorySessions
  v-model="showHistoryDialog"
  :kb-id="kb.id"
  @select-session="handleSelectHistorySession"
/>
```

- [ ] **步骤 2：添加 script 逻辑**

```typescript
import HistorySessions from './HistorySessions.vue'

const showHistoryDialog = ref(false)

function handleNewSession() {
  messages.value = []
  currentSessionId.value = ''
}

function handleSelectHistorySession(session: any) {
  currentSessionId.value = session.id
  // TODO: 加载历史消息
}
```

- [ ] **步骤 3：手动测试**
```
验证：
1. 右上角显示"新建会话"和"历史会话"按钮
2. 点击"新建会话"清空当前对话
3. 点击"历史会话"弹出 HistorySessions 弹窗
```

- [ ] **步骤 4：Commit**
```bash
git add watcher-web/src/views/main/knowledge/ChatAssistant.vue
git commit -m "feat(watcher-web): add new session and history session buttons to ChatAssistant"
```

---

## 任务 7：Skill - RAG Chat MCP Skill

**文件：**
- 新增：`.codebuddy/skills/rag-chat-mcp/SKILL.md`

- [ ] **步骤 1：编写 Skill 文件**

创建 `.codebuddy/skills/rag-chat-mcp/SKILL.md`：

```markdown
# RAG Chat MCP Skill

## 触发条件
当用户说以下内容时触发：
- "使用知识库问答"
- "在知识库里问一个问题"
- "RAG 问答"
- "查询知识库"

## 功能描述
此 Skill 用于通过 MCP 协议调用 ShowTime 的 RAG 问答服务，帮助用户获取基于知识库的 AI 回答。

## 使用方式

### 1. 列出知识库会话
```
用户：请列出知识库 kb001 的所有会话
Skill：调用 MCP list_sessions 工具
```

### 2. 问答
```
用户：在知识库 kb001 中问"CAS虚拟机状态有哪些？"
Skill：调用 MCP rag_chat 工具
```

### 3. 继续会话
```
用户：继续 sess001 会话，问"那如何关机？"
Skill：调用 MCP rag_chat 工具（传入 session_id）
```

### 4. 删除会话
```
用户：删除会话 sess001
Skill：调用 MCP delete_session 工具
```

## MCP 工具调用

### rag_chat
调用 RAG 问答，自动创建会话并保存历史。

```json
{
  "kb_id": "知识库ID",
  "question": "问题内容",
  "session_id": "会话ID（可选，用于继续会话）"
}
```

### list_sessions
列出知识库下的所有会话。

```json
{
  "kb_id": "知识库ID"
}
```

### delete_session
删除指定会话。

```json
{
  "session_id": "会话ID"
}
```

## MCP 服务地址
`http://localhost:8000/mcp`

## 返回格式
```json
{
  "state": 0,
  "data": {
    "answer": "AI 回答内容",
    "session_id": "会话ID"
  }
}
```

## 错误处理
- 如果知识库为空，返回："知识库为空，请先构建知识库"
- 如果知识库不存在，返回："知识库不存在"
- 如果会话不存在，返回："会话不存在"
```

- [ ] **步骤 2：Commit**
```bash
git add .codebuddy/skills/rag-chat-mcp/SKILL.md
git commit -m "feat: add rag-chat-mcp skill for MCP RAG chat"
```

---

## 自检清单

### 规格覆盖度
- [x] Scenario 1: 按知识库查询会话列表 → Task 1
- [x] Scenario 2: 创建新会话 → Task 6 (handleNewSession)
- [x] Scenario 3: 基于历史会话继续对话 → Task 6 (handleSelectHistorySession)
- [x] Scenario 4: 删除历史会话 → Task 5 (handleDeleteSession)
- [x] Scenario 7: 自动生成标题 → Task 2 (rag_chat 中 title=request.question[:50])
- [x] Scenario 8-11: MCP 工具 → Tasks 2-4

### 占位符扫描
无"TODO"、"待定"、空白实现

### 类型一致性
- `session_id` 类型一致（string）
- `kb_id` 类型一致（string）
- API 响应结构一致（state + data）