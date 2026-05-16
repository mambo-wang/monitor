# Chat Session Management Spec

## Scenarios

### Scenario 1: 用户查看知识库内的历史会话列表
- **GIVEN** 用户已选择知识库 kb_id="kb001"，该知识库下有 3 个会话
- **WHEN** 调用 GET /api/knowledge/chat/history?kb_id=kb001
- **THEN** 返回该知识库下所有会话列表，按 updated_at 倒序排列
- **THEN** 每个会话显示 id、title（含第一条问答摘要）、created_at、updated_at、message_count

### Scenario 2: 用户创建新会话
- **GIVEN** 用户已选择知识库 kb_id="kb001"
- **WHEN** 点击"新建会话"按钮
- **THEN** 创建新会话并切换到该会话上下文
- **THEN** 界面清空当前问答并保存到历史会话，新建session准备接收新问题

### Scenario 3: 用户基于历史会话继续对话
- **GIVEN** 用户已选择知识库 kb_id="kb001"，该知识库下有会话 session_id="sess001"
- **WHEN** 用户点击会话 "sess001"
- **THEN** 加载该会话的所有问答历史并展示在界面上
- **THEN** 用户可以继续在该会话中提问

### Scenario 4: 用户删除历史会话
- **GIVEN** 用户已选择知识库 kb_id="kb001"，该知识库下有会话 session_id="sess001"
- **WHEN** 用户点击会话的删除按钮并确认
- **THEN** 该会话及其所有消息被永久删除
- **THEN** 会话列表实时更新，不再显示该会话

### Scenario 5: 用户删除知识库下的最后一个会话
- **GIVEN** 用户已选择知识库 kb_id="kb001"，该知识库下只有 1 个会话
- **WHEN** 用户删除该会话
- **THEN** 该知识库的会话列表显示为空
- **THEN** 界面显示"暂无历史会话"提示

### Scenario 6: 用户查看空知识库的会话列表
- **GIVEN** 用户已选择知识库 kb_id="kb-empty"，该知识库没有任何会话
- **WHEN** 用户点击"历史会话"按钮
- **THEN** 弹出空列表，显示"暂无历史会话"

### Scenario 7: 创建会话时自动生成标题
- **GIVEN** 用户在知识库 kb_id="kb001" 新建会话
- **WHEN** 用户发送第一个问题 "CAS虚拟机如何开机？"
- **THEN** 系统自动生成标题为 "CAS虚拟机如何开机？" 并保存
- **THEN** 后续在会话列表中显示该标题

### Scenario 8: AI Agent 通过 MCP 调用 RAG 问答
- **GIVEN** MCP 客户端已连接 watcher-ai 服务
- **WHEN** 调用 rag_chat 工具，传入 kb_id="kb001" 和 question="CAS虚拟机状态有哪些？"
- **THEN** 返回 RAG 问答结果
- **THEN** 答案自动保存到会话历史

### Scenario 9: AI Agent 通过 MCP 继续历史会话
- **GIVEN** MCP 客户端已连接 watcher-ai 服务，存在 session_id="sess001"
- **WHEN** 调用 rag_chat 工具，传入 session_id="sess001" 和 question="那如何关机？"
- **THEN** 在会话 sess001 中追加新问答
- **THEN** 返回 RAG 问答结果

### Scenario 10: AI Agent 通过 MCP 列出知识库会话
- **GIVEN** MCP 客户端已连接 watcher-ai 服务
- **WHEN** 调用 list_sessions 工具，传入 kb_id="kb001"
- **THEN** 返回该知识库下的所有会话列表

### Scenario 11: AI Agent 通过 MCP 删除会话
- **GIVEN** MCP 客户端已连接 watcher-ai 服务，存在 session_id="sess001"
- **WHEN** 调用 delete_session 工具，传入 session_id="sess001"
- **THEN** 删除该会话及其所有消息
- **THEN** 返回删除成功确认