# Tasks

## Atomic TDD Task List

### Feature: 后端 - 按知识库查询会话列表

- [x] RED: 编写测试 - 验证 `ChatRepository.list_sessions_by_kb` 按 kb_id 筛选会话
  - 测试 kb_id 有会话时返回正确列表
  - 测试 kb_id 无会话时返回空列表
  - 测试分页参数正确

- [x] GREEN: 最小实现 - 实现 `ChatRepository.list_sessions_by_kb` 方法
  - 引用对应的 RED 测试

### Feature: 后端 - MCP RAG 工具服务

- [x] RED: 编写测试 - 验证 `rag_chat` MCP 工具函数
  - 测试 kb_id + question 返回问答结果
  - 测试 session_id 继续会话
  - 测试缺少 kb_id 且无 session_id 时报错

- [x] GREEN: 最小实现 - 实现 `rag_chat` 工具函数
  - 引用对应的 RED 测试

- [x] RED: 编写测试 - 验证 `list_sessions` MCP 工具函数
  - 测试传入 kb_id 返回该知识库的会话列表

- [x] GREEN: 最小实现 - 实现 `list_sessions` 工具函数
  - 引用对应的 RED 测试

- [x] RED: 编写测试 - 验证 `delete_session` MCP 工具函数
  - 测试传入 session_id 删除会话
  - 测试删除不存在的 session_id 报错

- [x] GREEN: 最小实现 - 实现 `delete_session` 工具函数
  - 引用对应的 RED 测试

### Feature: 前端 - 历史会话弹窗组件

- [x] RED: 编写测试 - 验证 `HistorySessions.vue` 组件渲染
  - 测试空列表显示"暂无历史会话"
  - 测试会话列表正确渲染
  - 测试删除按钮交互

- [x] GREEN: 最小实现 - 实现 `HistorySessions.vue` 弹窗组件
  - 引用对应的 RED 测试

### Feature: 前端 - ChatAssistant 按钮交互

- [x] RED: 编写测试 - 验证 `ChatAssistant.vue` 右上角按钮
  - 测试"新建会话"按钮点击创建新会话
  - 测试"历史会话"按钮点击弹出列表

- [x] GREEN: 最小实现 - 在 `ChatAssistant.vue` 添加按钮和交互逻辑
  - 引用对应的 RED 测试

### Feature: Skill - RAG Chat MCP Skill

- [x] RED: 编写测试 - 验证 Skill 触发和 MCP 调用
  - 测试 Skill 加载成功
  - 测试触发词匹配 rag_chat

- [x] GREEN: 最小实现 - 实现 `rag-chat-mcp` Skill
  - 引用对应的 RED 测试