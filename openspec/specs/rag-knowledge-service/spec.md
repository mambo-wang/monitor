# RAG Knowledge Service

## 服务概述

- **服务名称**: RAG 知识库问答服务
- **服务定义**: 提供知识库管理、聊天历史、RAG 问答的核心能力
- **依赖**:
  - ChromaDB（向量知识库）
  - MySQL（结构化数据：会话、消息、知识库元数据）
- **消费者**:
  - 前端 Vue 应用（Web UI）
  - AI IDE（通过 MCP 协议）

## 子规格

### 1. 聊天历史管理 (chat-history)

已实现功能。

#### Requirement: 获取用户对话历史列表

系统 SHALL 提供获取用户所有会话列表的接口

##### Scenario: 获取用户对话历史列表
- **GIVEN** 用户已登录，user_id 为 "user123"
- **WHEN** 调用 GET /api/knowledge/chat/history?page=1&page_size=20&user_id=user123
- **THEN** 返回该用户的所有会话列表，按 updated_at 倒序排列
- **THEN** 返回结果包含 sessions、total、page、page_size 字段

##### Scenario: 获取空用户对话历史
- **GIVEN** 用户 user456 没有任何对话记录
- **WHEN** 调用 GET /api/knowledge/chat/history?user_id=user456
- **THEN** 返回空列表，total 为 0

---

#### Requirement: 按知识库获取会话列表

系统 SHALL 提供按知识库获取会话列表的接口

##### Scenario: 按知识库获取会话列表
- **GIVEN** 用户已选择知识库 kb_id="kb001"，该知识库下有 3 个会话
- **WHEN** 调用 GET /api/knowledge/chat/history?kb_id=kb001
- **THEN** 返回该知识库下所有会话列表，按 updated_at 倒序排列
- **THEN** 每个会话显示 id、title、created_at、updated_at、message_count

##### Scenario: 按知识库获取空会话列表
- **GIVEN** 用户已选择知识库 kb_id="kb-empty"，该知识库没有任何会话
- **WHEN** 调用 GET /api/knowledge/chat/history?kb_id=kb-empty
- **THEN** 返回空列表，total 为 0

---

#### Requirement: 获取会话详情

系统 SHALL 提供获取会话详细信息及消息列表的接口

##### Scenario: 获取会话详情
- **GIVEN** 存在会话 ID "sess123"，属于用户 "user123"
- **WHEN** 调用 GET /api/knowledge/chat/sessions/sess123
- **THEN** 返回该会话的详细信息（id、user_id、kb_id、title、message_count）
- **THEN** 返回该会话的所有消息列表（按时间升序）

##### Scenario: 获取他人会话被拒绝
- **GIVEN** 会话 ID "sess999" 属于用户 "user123"
- **WHEN** 用户 "user456" 调用 GET /api/knowledge/chat/sessions/sess999
- **THEN** 返回 HTTP 403 错误，detail 为 "Access denied"

---

#### Requirement: 删除对话历史

系统 SHALL 提供删除会话及其所有消息的接口

##### Scenario: 删除对话历史
- **GIVEN** 存在会话 ID "sess789"，属于用户 "user123"
- **WHEN** 调用 DELETE /api/knowledge/chat/sessions/sess789
- **THEN** 该会话及其所有消息被永久删除
- **THEN** 返回 {"state": 0, "data": {"message": "deleted"}}

##### Scenario: 删除不存在的会话
- **GIVEN** 不存在会话 ID "not-exist-session"
- **WHEN** 调用 DELETE /api/knowledge/chat/sessions/not-exist-session
- **THEN** 返回 HTTP 404 错误，detail 为 "Session not found"

---

#### Requirement: 问答接口保存历史

系统 SHALL 在问答交互时自动保存对话历史

##### Scenario: 问答接口保存历史
- **GIVEN** 用户已选择知识库 kb_id="kb001"，问题为 "如何重启服务？"
- **WHEN** 调用 POST /api/knowledge/chat/with-history，body 为 {"user_id": "user123", "kb_id": "kb001", "question": "如何重启服务？"}
- **THEN** 自动创建或复用会话，保存用户问题和 AI 回答
- **THEN** 返回答案及历史记录 session_id

##### Scenario: 自动生成会话标题
- **GIVEN** 用户在知识库 kb_id="kb001" 新建会话
- **WHEN** 用户发送第一个问题 "CAS虚拟机如何开机？"
- **THEN** 系统自动生成标题为 "CAS虚拟机如何开机？" 并保存
- **THEN** 后续在会话列表中显示该标题

---

#### Requirement: MCP RAG 工具

系统 SHALL 通过 MCP 协议提供 RAG 问答工具

##### Scenario: MCP RAG 问答
- **GIVEN** MCP 客户端已连接 watcher-ai 服务
- **WHEN** 调用 rag_chat 工具，传入 kb_id="kb001" 和 question="CAS虚拟机状态有哪些？"
- **THEN** 返回 RAG 问答结果
- **THEN** 答案自动保存到会话历史

##### Scenario: MCP 继续历史会话
- **GIVEN** MCP 客户端已连接 watcher-ai 服务，存在 session_id="sess001"
- **WHEN** 调用 rag_chat 工具，传入 session_id="sess001" 和 question="那如何关机？"
- **THEN** 在会话 sess001 中追加新问答
- **THEN** 返回 RAG 问答结果

##### Scenario: MCP 列出知识库会话
- **GIVEN** MCP 客户端已连接 watcher-ai 服务
- **WHEN** 调用 list_sessions 工具，传入 kb_id="kb001"
- **THEN** 返回该知识库下的所有会话列表

##### Scenario: MCP 删除会话
- **GIVEN** MCP 客户端已连接 watcher-ai 服务，存在 session_id="sess001"
- **WHEN** 调用 delete_session 工具，传入 session_id="sess001"
- **THEN** 删除该会话及其所有消息
- **THEN** 返回删除成功确认

---

### 2. 知识库管理 (knowledge-base)

**待实现**

- 创建知识库
- 上传文档到知识库
- 向量化和嵌入处理
- 删除知识库

---

### 3. RAG 问答 (rag-answer)

**待实现**

- 基于知识库的 RAG 问答
- 检索增强生成
- 答案来源追踪

---

## 扩展点

- 多知识库支持
- AI Agent 集成
- 文档增量更新