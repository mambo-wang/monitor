# Proposal

## Problem

当前 ShowTime 知识库问答功能缺少会话管理能力：
1. 用户无法查看某个知识库下的历史问答记录
2. 用户无法基于历史会话继续对话
3. 用户无法删除不需要的历史会话
4. AI Agent 无法通过 MCP 协议调用 RAG 问答能力

## Testable Behaviors

### 知识库会话管理

- WHEN 用户点击知识问答界面的"历史会话"按钮 THEN 弹出历史会话列表
- WHEN 用户点击"新建会话"按钮 THEN 创建新会话并切换到新会话上下文
- WHEN 历史会话列表展示时 THEN 显示该知识库下所有会话记录（按时间倒序）
- WHEN 用户点击某条历史会话 THEN 加载该会话的问答历史并继续对话
- WHEN 用户点击删除按钮 THEN 该会话及其关联的历史记录被永久删除
- WHEN 会话被删除后 THEN 列表实时更新，不再显示该会话

### MCP 服务

- WHEN MCP 客户端调用 `rag_chat` 工具并传入 kb_id 和 question THEN 返回问答结果
- WHEN MCP 客户端调用 `rag_chat` 工具并传入 session_id THEN 继续该会话的对话
- WHEN MCP 客户端调用 `list_sessions` 工具并传入 kb_id THEN 返回该知识库下的所有会话列表
- WHEN MCP 客户端调用 `delete_session` 工具并传入 session_id THEN 删除指定会话

### Skill for MCP

- WHEN 用户说 "使用知识库问答" 或类似指令 THEN 触发 Skill 调用 MCP rag_chat 工具
- WHEN Skill 调用成功 THEN 自动在当前 IDE 中展示问答结果
- WHEN Skill 调用失败 THEN 返回友好的错误提示

## Acceptance Criteria

1. 前端知识问答界面右上角显示"新建会话"和"历史会话"两个按钮
2. 点击"历史会话"按钮弹出模态框，显示当前知识库的历史会话列表
3. 每个会话显示：会话标题（含第一条问答摘要）、创建时间
4. 点击会话可加载历史问答并继续对话
5. 可删除不需要的会话
6. MCP 服务提供 rag_chat、list_sessions、delete_session 三个工具
7. 对应 Skill 可通过自然语言触发 MCP 调用并展示结果