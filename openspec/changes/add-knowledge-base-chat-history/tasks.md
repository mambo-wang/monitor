# Tasks

## Atomic TDD Task List

---

### Feature: ChatRepository 数据库操作层

- [x] RED: 编写 ChatRepository 基础 CRUD 测试
  - 测试 create_session() 创建会话返回正确字段
  - 测试 get_session_by_id() 查询存在/不存在会话

- [x] GREEN: 实现 ChatRepository 基础 CRUD
  - 创建 chat_entity.py 定义 ChatSession 和 ChatMessage 实体
  - 创建 chat_repository.py 实现基础查询方法

- [x] RED: 编写 ChatRepository 消息操作测试
  - 测试 save_message() 保存用户和助手消息
  - 测试 list_messages_by_session() 查询消息列表

- [x] GREEN: 实现 ChatRepository 消息操作
  - 实现 save_message() 方法
  - 实现 list_messages_by_session() 方法

- [x] RED: 编写 ChatRepository 分页和删除测试
  - 测试 list_sessions_by_user() 分页查询
  - 测试 update_message_count() 更新消息数
  - 测试 delete_session() 幂等删除

- [x] GREEN: 实现 ChatRepository 分页和删除
  - 实现 list_sessions_by_user() 分页查询
  - 实现 update_message_count() 方法
  - 实现 delete_session() 方法

---

### Feature: ChatService 业务逻辑层

- [x] RED: 编写 ChatService 创建会话测试
  - 测试 create_or_append_session() 新建会话
  - 测试 create_or_append_session() 追加消息

- [x] GREEN: 实现 ChatService 创建会话
  - 创建 chat_service.py
  - 实现 create_or_append_session() 方法
  - 实现跨用户验证逻辑

- [x] RED: 编写 ChatService 查询和删除测试
  - 测试 get_user_sessions() 分页查询
  - 测试 get_session_detail() 获取详情
  - 测试 delete_session() 幂等删除

- [x] GREEN: 实现 ChatService 查询和删除
  - 实现 get_user_sessions() 方法
  - 实现 get_session_detail() 方法
  - 实现 delete_session() 方法

---

### Feature: ChatHistory API 端点

- [x] RED: 编写获取会话列表 API 测试
  - 测试 GET /api/knowledge/chat/sessions?user_id=xxx
  - 测试缺少 user_id 参数返回 422
  - 测试分页参数

- [x] GREEN: 实现获取会话列表 API
  - 创建 chat_history.py 路由文件
  - 实现 GET /api/knowledge/chat/sessions 端点

- [x] RED: 编写获取会话详情 API 测试
  - 测试 GET /api/knowledge/chat/sessions/{session_id}
  - 测试 session_id 不存在返回 404

- [x] GREEN: 实现获取会话详情 API
  - 实现 GET /api/knowledge/chat/sessions/{session_id} 端点

- [x] RED: 编写删除会话 API 测试
  - 测试 DELETE /api/knowledge/chat/sessions/{session_id}
  - 测试幂等性（重复删除返回成功）

- [x] GREEN: 实现删除会话 API
  - 实现 DELETE /api/knowledge/chat/sessions/{session_id} 端点

---

### Feature: 集成 RAG Chat 接口

- [x] RED: 编写 POST /api/knowledge/chat 集成测试
  - 测试新对话创建会话并保存消息
  - 测试续话追加消息到已有会话
  - 测试跨用户验证

- [x] GREEN: 实现 POST /api/knowledge/chat 集成
  - 修改 knowledge.py 中 /chat 接口
  - 保存用户消息到 chat_messages 表
  - 保存助手回复到 chat_messages 表
  - 返回 session_id 和 message_id

- [x] REFACTOR: 整合知识库路由
  - 在 main.py 中注册 chat_history 路由
  - 确保路由前缀统一

---

### Feature: 数据库迁移

- [ ] RED: 验证数据库表结构
  - 确认 chat_sessions 表创建成功
  - 确认 chat_messages 表创建成功
  - 确认级联删除配置正确

- [x] GREEN: 创建数据库迁移脚本
  - 创建 SQL 迁移文件
  - 在数据库中执行建表语句

---

### Feature: 端到端测试

- [ ] RED: 编写端到端测试
  - 测试完整对话流程：创建会话 → 发送消息 → 查询历史 → 删除会话

- [ ] GREEN: 执行端到端测试
  - 运行所有测试确保通过
  - 验证 API 响应格式统一

---

## 任务执行顺序

1. 数据库迁移 → 2. ChatRepository → 3. ChatService → 4. ChatHistory API → 5. 集成 RAG Chat → 6. 端到端测试
