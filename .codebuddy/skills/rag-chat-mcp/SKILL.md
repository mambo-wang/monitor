# RAG Chat MCP Skill

## 触发条件

当用户说以下内容时触发：
- "使用知识库问答"
- "在知识库里问一个问题"
- "RAG 问答"
- "查询知识库"
- "帮我问下知识库"
- "知识库会话"

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