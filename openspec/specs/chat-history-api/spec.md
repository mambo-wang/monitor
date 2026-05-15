# Chat History API

## Purpose

TBD - 待补充

## Requirements

### Requirement: 获取用户对话历史列表
系统 SHALL 提供获取用户所有会话列表的接口

#### Scenario: 获取用户对话历史列表
- **GIVEN** 用户已登录，user_id 为 "user123"
- **WHEN** 调用 GET /api/knowledge/chat/history?page=1&page_size=20&user_id=user123
- **THEN** 返回该用户的所有会话列表，按 updated_at 倒序排列
- **THEN** 返回结果包含 sessions、total、page、page_size 字段

#### Scenario: 获取空用户对话历史
- **GIVEN** 用户 user456 没有任何对话记录
- **WHEN** 调用 GET /api/knowledge/chat/history?user_id=user456
- **THEN** 返回空列表，total 为 0

### Requirement: 获取会话详情
系统 SHALL 提供获取会话详细信息及消息列表的接口

#### Scenario: 获取会话详情
- **GIVEN** 存在会话 ID "sess123"，属于用户 "user123"
- **WHEN** 调用 GET /api/knowledge/chat/sessions/sess123
- **THEN** 返回该会话的详细信息（id、user_id、kb_id、title、message_count）
- **THEN** 返回该会话的所有消息列表（按时间升序）

#### Scenario: 获取他人会话被拒绝
- **GIVEN** 会话 ID "sess999" 属于用户 "user123"
- **WHEN** 用户 "user456" 调用 GET /api/knowledge/chat/sessions/sess999
- **THEN** 返回 HTTP 403 错误，detail 为 "Access denied"

### Requirement: 删除对话历史
系统 SHALL 提供删除会话及其所有消息的接口

#### Scenario: 删除对话历史
- **GIVEN** 存在会话 ID "sess789"，属于用户 "user123"
- **WHEN** 调用 DELETE /api/knowledge/chat/sessions/sess789
- **THEN** 该会话及其所有消息被永久删除
- **THEN** 返回 {"state": 0, "data": {"message": "deleted"}}

#### Scenario: 删除不存在的会话
- **GIVEN** 不存在会话 ID "not-exist-session"
- **WHEN** 调用 DELETE /api/knowledge/chat/sessions/not-exist-session
- **THEN** 返回 HTTP 404 错误，detail 为 "Session not found"

### Requirement: 问答接口保存历史
系统 SHALL 在问答交互时自动保存对话历史

#### Scenario: 问答接口保存历史
- **GIVEN** 用户已选择知识库 kb_id="kb001"，问题为 "如何重启服务？"
- **WHEN** 调用 POST /api/knowledge/chat/with-history，body 为 {"user_id": "user123", "kb_id": "kb001", "question": "如何重启服务？"}
- **THEN** 自动创建或复用会话，保存用户问题和 AI 回答
- **THEN** 返回答案及历史记录 session_id