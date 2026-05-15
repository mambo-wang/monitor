# Tasks

## Atomic TDD Task List

---

### Feature: 数据库表创建

- [ ] RED: 编写数据库迁移测试——验证 chat_sessions 和 chat_messages 表结构
- [ ] GREEN: 最小实现——创建 scripts/migrate_chat_history.sql 脚本

---

### Feature: 对话历史 API - 获取用户历史列表

- [ ] RED: 编写测试——GET /api/knowledge/chat/history 返回用户对话列表
- [ ] GREEN: 最小实现——在 api/chat_history.py 中实现 list_chat_history 端点

---

### Feature: 对话历史 API - 获取会话详情

- [ ] RED: 编写测试——GET /api/knowledge/chat/sessions/{session_id} 返回会话详情和消息列表
- [ ] GREEN: 最小实现——实现 get_session_detail 端点，包含权限校验

---

### Feature: 对话历史 API - 删除会话

- [ ] RED: 编写测试——DELETE /api/knowledge/chat/sessions/{session_id} 删除指定会话
- [ ] GREEN: 最小实现——实现 delete_session 端点

---

### Feature: 对话历史 API - 带历史的问答

- [ ] RED: 编写测试——POST /api/knowledge/chat/with-history 自动保存问答记录
- [ ] GREEN: 最小实现——实现 with_history_chat 端点，集成 RAG 和历史保存

---

### Feature: 注册 API 路由

- [ ] RED: 编写测试——验证 chat_history 路由已正确注册到 FastAPI 应用
- [ ] GREEN: 最小实现——在 main.py 中引入并注册 chat_history 路由

---

## 执行顺序

1. 数据库表创建
2. 对话历史 API 端点开发（按顺序）
3. 路由注册
